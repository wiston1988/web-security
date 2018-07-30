package org.web.security.data;


/**
 * @author Chen Hui
 * @since 2015/9/23
 */
public class ResponseResult {
    private int code;
    private String msg; //为了和老的返回格式的兼容
    private String errorMsg;
    private Object data;

    public static ResponseResult success(){
        return new ResponseResult(ResponseCode.SUCCESS_CODE);
    }

    public static ResponseResult success(Object data){
        ResponseResult result =  new ResponseResult(ResponseCode.SUCCESS_CODE);
        result.setData(data);
        return result;
    }

    public ResponseResult(ResponseCode ResponseCode){
        this(ResponseCode.getCode(), ResponseCode.getMessage());
    }
    public ResponseResult(int code, String msg){
        this.code = code;
        this.msg = msg;
        this.errorMsg = msg;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        this.errorMsg = msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.msg = errorMsg;
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
