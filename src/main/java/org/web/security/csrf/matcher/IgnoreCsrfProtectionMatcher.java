package org.web.security.csrf.matcher;

import org.web.security.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @author Chen Hui
 * @since 2016/3/25
 */
public class IgnoreCsrfProtectionMatcher implements RequestMatcher {

    private final Pattern allowedMethods;

    private final String[] patterns;
    private final AntPathMatcher matcher;

    public IgnoreCsrfProtectionMatcher(String... patterns) {
        this.allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
        this.patterns = patterns;
        this.matcher = new AntPathMatcher();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if(allowedMethods.matcher(request.getMethod()).matches()) {
            return true;
        }
        if(patterns==null){
            return false;
        }
        String url = getRequestPath(request);
        for(String pattern : patterns) {
            if (matcher.match(pattern, url)) {
                return true;
            }
        }

        return false;
    }

    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        if(request.getPathInfo() != null) {
            url = url + request.getPathInfo();
        }
        return url;
    }

}
