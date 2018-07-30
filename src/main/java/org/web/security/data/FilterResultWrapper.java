package org.web.security.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chen Hui
 * @since 2017/5/24
 */
public class FilterResultWrapper {
    private boolean interrupt = false;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public FilterResultWrapper(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public FilterResultWrapper(boolean interrupt, HttpServletRequest request, HttpServletResponse response) {
        this.interrupt = interrupt;
        this.request = request;
        this.response = response;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
