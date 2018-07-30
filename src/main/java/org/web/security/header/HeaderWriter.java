package org.web.security.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chen Hui
 * @since 2016/3/23
 */
public interface HeaderWriter {

    /**
     * Create a {@code Header} instance.
     *
     * @param request the request
     * @param response the response
     */
    void writeHeaders(HttpServletRequest request, HttpServletResponse response);
}

