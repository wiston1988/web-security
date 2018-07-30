package org.web.security.csrf;

import org.springframework.util.Assert;
/**
 * @author Chen Hui
 * @since 2016/3/22
 */
@SuppressWarnings("serial")
public final class DefaultCsrfToken implements CsrfToken {

    private String token;

    private String parameterName;

    private String headerName;

    public DefaultCsrfToken() {
        super();
    }
    /**
     * Creates a new instance
     * @param headerName the HTTP header name to use
     * @param parameterName the HTTP parameter name to use
     * @param token the value of the token (i.e. expected value of the HTTP parameter of parametername).
     */
    public DefaultCsrfToken(String headerName, String parameterName, String token) {
        Assert.hasLength(headerName, "headerName cannot be null or empty");
        Assert.hasLength(parameterName, "parameterName cannot be null or empty");
        Assert.hasLength(token, "token cannot be null or empty");
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.csrf.CsrfToken#getHeaderName()
     */
    public String getHeaderName() {
        return headerName;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.csrf.CsrfToken#getParameterName()
     */
    public String getParameterName() {
        return parameterName;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.csrf.CsrfToken#getToken()
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}

