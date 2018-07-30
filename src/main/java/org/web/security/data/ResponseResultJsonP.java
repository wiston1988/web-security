package org.web.security.data;

/**
 * @author Chen Hui
 * @since 2016/5/18
 */
public class ResponseResultJsonP {
    private ResponseResult responseResult;
    private String callBackFunction;

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

    public String getCallBackFunction() {
        return callBackFunction;
    }

    public void setCallBackFunction(String callBackFunction) {
        this.callBackFunction = callBackFunction;
    }
}
