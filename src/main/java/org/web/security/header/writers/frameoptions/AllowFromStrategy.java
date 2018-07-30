package org.web.security.header.writers.frameoptions;

import javax.servlet.http.HttpServletRequest;

public interface AllowFromStrategy {

    /**
     * Gets the value for ALLOW-FROM excluding the ALLOW-FROM. For example, the
     * result might be "https://example.com/".
     *
     * @param request the {@link HttpServletRequest}
     * @return the value for ALLOW-FROM or null if no header should be added for this request.
     */
    String getAllowFromValue(HttpServletRequest request);
}
