package org.web.security.util.matcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chen Hui
 * @since 2016/3/22
 */
public interface RequestMatcher {

    /**
     * Decides whether the rule implemented by the strategy matches the supplied request.
     *
     * @param request the request to check for a match
     * @return true if the request matches, false otherwise
     */
    boolean matches(HttpServletRequest request);

}

