package org.web.security.exception;

import org.web.security.data.ResponseCode;

/**
 * @author wangong.lw
 * @since 1.0.0
 */

public class WebSecurityException extends RuntimeException {

    private static final long serialVersionUID = -4139789657525007087L;

    private ResponseCode responseCode;

    public WebSecurityException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }
}
