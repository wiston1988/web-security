package org.web.security.util.matcher;

import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chen Hui
 * @since 2016/3/25
 */
public class PathPatternsMatcher implements RequestMatcher {

    private final String[] patterns;
    private final AntPathMatcher matcher;

    public PathPatternsMatcher(String... patterns) {
        this.patterns = patterns;
        this.matcher = new AntPathMatcher();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
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
