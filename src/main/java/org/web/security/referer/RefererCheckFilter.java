
package org.web.security.referer;

import org.web.security.SecurityFilter;
import org.web.security.data.FilterResultWrapper;
import org.web.security.data.ResponseCode;
import org.web.security.exception.WebSecurityException;
import org.web.security.util.Utils;
import org.web.security.util.matcher.PathPatternsMatcher;
import org.web.security.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author Chen Hui
 * @since 2018/2/11
 */
public class RefererCheckFilter implements SecurityFilter {

    private RequestMatcher checkPathMatcher = new PathPatternsMatcher();
    private String[] allowDomains;

    @Override
    public FilterResultWrapper doFilterInvoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (checkPathMatcher.matches(request)) {
            String referer = request.getHeader("Referer");
            if(Utils.isBlank(referer)||allowDomains==null){
                throw new WebSecurityException(ResponseCode.FORBIDDEN);
            }
            boolean valid = false;
            for (String domain : allowDomains) {
                if (referer.startsWith("https://" + domain) || referer.startsWith("http://" + domain)) {
                    valid = true;
                    break;
                }
            }
            if(!valid){
                throw new WebSecurityException(ResponseCode.FORBIDDEN);
            }
        }
        return new FilterResultWrapper(request,response);
    }

    public void setCheckPathMatcher(String checkUriPatterns) {
        Assert.notNull(checkUriPatterns, "checkUriPatterns cannot be null");
        this.checkPathMatcher = new PathPatternsMatcher(StringUtils.commaDelimitedListToStringArray(checkUriPatterns));
    }

    public void setAllowDomains(String... allowDomains) {
        Assert.notNull(allowDomains, "allowDomains cannot be null");
        this.allowDomains = allowDomains;
    }
}
