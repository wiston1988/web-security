package org.web.security.csrf;

import com.alibaba.fastjson.JSON;
import org.web.security.SecurityFilter;
import org.web.security.csrf.matcher.IgnoreCsrfProtectionMatcher;
import org.web.security.data.FilterResultWrapper;
import org.web.security.exception.WebSecurityException;
import org.web.security.data.ResponseCode;
import org.web.security.data.ResponseResult;
import org.web.security.util.UrlUtils;
import org.web.security.util.matcher.PathPatternsMatcher;
import org.web.security.util.matcher.RequestMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chen Hui
 * @since 2016/3/22
 */
public class CsrfFilter implements SecurityFilter {
    private static final Log LOG = LogFactory.getLog(CsrfFilter.class);
    private static final String RESPONSE_HEADER = "X-CSRF-HEADER";
    private static final String RESPONSE_PARAM = "X-CSRF-PARAM";
    private static final String RESPONSE_TOKEN = "X-CSRF-TOKEN";
    private CsrfTokenRepository tokenRepository = new HttpSessionCsrfTokenRepository();
    private RequestMatcher ignoreFilterPatterns = new IgnoreCsrfProtectionMatcher();
    private RequestMatcher getCsrfUrlPatternMatcher = new PathPatternsMatcher("/getcsrf/**");

    /* (non-Javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public FilterResultWrapper doFilterInvoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        CsrfToken csrfToken = tokenRepository.loadToken(request);
        final boolean missingToken = csrfToken == null;
        if(missingToken) {
            CsrfToken generatedToken = tokenRepository.generateToken(request);
            csrfToken = new SaveOnAccessCsrfToken(tokenRepository, request, response, generatedToken);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        request.setAttribute(csrfToken.getParameterName(), csrfToken);
        if (csrfToken != null) {
            response.setHeader(RESPONSE_HEADER, csrfToken.getHeaderName());
            response.setHeader(RESPONSE_PARAM, csrfToken.getParameterName());
            response.setHeader(RESPONSE_TOKEN, csrfToken.getToken());
        }
        if(ignoreFilterPatterns.matches(request)) {//get，head以及用户自定的url pattern 将被忽略校验csrf token
            if(getCsrfUrlPatternMatcher.matches(request)){ //获取csrf token的url pattern
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(ResponseResult.success()));
                response.getWriter().close();
                return new FilterResultWrapper(true,request,response);
            }
            return new FilterResultWrapper(request,response);
        }

        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if(actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }
        if(!csrfToken.getToken().equals(actualToken)) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Invalid CSRF token found for " + UrlUtils.buildFullRequestUrl(request));
            }
            if(missingToken) {
                LOG.error("missing csrf token: " + actualToken);
                throw new WebSecurityException(ResponseCode.SECURITY_MISSING_CSRF_TOKEN);
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter().write(JSON.toJSONString(new ResponseResult(ResponseCode.SECURITY_MISSING_CSRF_TOKEN)));
//                response.getWriter().close();
//                return new FilterResultWrapper(request,response);
            } else {
                LOG.error("Invalid CSRF Token '" + actualToken
                        + "' was found on the request parameter '"
                        + csrfToken.getParameterName() + "' or header '"
                        + csrfToken.getHeaderName() + "'.");
                throw new WebSecurityException(ResponseCode.SECURITY_INVALID_CSRF_TOKEN);
//                response.setContentType("application/json;charset=UTF-8");
//                response.getWriter().write(JSON.toJSONString(new ResponseResult(ResponseCode.SECURITY_INVALID_CSRF_TOKEN)));
//                response.getWriter().close();
//                return new FilterResultWrapper(request,response);
            }
        }
        tokenRepository.saveToken(null, request, response);
        return new FilterResultWrapper(request,response);
    }

    public CsrfTokenRepository getTokenRepository() {
        return tokenRepository;
    }

    public void setTokenRepository(CsrfTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void setGetCsrfTokenUrlPatterns(String getCsrfTokenUrlPatterns) {
        this.getCsrfUrlPatternMatcher = new PathPatternsMatcher(StringUtils.commaDelimitedListToStringArray(getCsrfTokenUrlPatterns));
    }
    public void setIgnoreFilterPatterns(String ignoreFilterPatterns) {
        Assert.notNull(ignoreFilterPatterns, "ignoreFilterPatterns cannot be null");
        this.ignoreFilterPatterns = new IgnoreCsrfProtectionMatcher(StringUtils.commaDelimitedListToStringArray(ignoreFilterPatterns));
    }
    @SuppressWarnings("serial")
    private static final class SaveOnAccessCsrfToken implements CsrfToken {
        private transient CsrfTokenRepository tokenRepository;
        private transient HttpServletRequest request;
        private transient HttpServletResponse response;

        private final CsrfToken delegate;

        public SaveOnAccessCsrfToken(CsrfTokenRepository tokenRepository,
                                     HttpServletRequest request, HttpServletResponse response,
                                     CsrfToken delegate) {
            super();
            this.tokenRepository = tokenRepository;
            this.request = request;
            this.response = response;
            this.delegate = delegate;
        }

        public String getHeaderName() {
            return delegate.getHeaderName();
        }

        public String getParameterName() {
            return delegate.getParameterName();
        }

        public String getToken() {
            saveTokenIfNecessary();
            return delegate.getToken();
        }

        @Override
        public String toString() {
            return "SaveOnAccessCsrfToken [delegate=" + delegate + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((delegate == null) ? 0 : delegate.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SaveOnAccessCsrfToken other = (SaveOnAccessCsrfToken) obj;
            if (delegate == null) {
                if (other.delegate != null)
                    return false;
            } else if (!delegate.equals(other.delegate))
                return false;
            return true;
        }

        private void saveTokenIfNecessary() {
            if(this.tokenRepository == null) {
                return;
            }

            synchronized(this) {
                if(tokenRepository != null) {
                    this.tokenRepository.saveToken(delegate, request, response);
                    this.tokenRepository = null;
                    this.request = null;
                    this.response = null;
                }
            }
        }

    }


}

