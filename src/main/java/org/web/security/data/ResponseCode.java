package org.web.security.data;

public enum ResponseCode {

    DEFAULT_ERROR(1, "对不起，系统繁忙"),
    PARAMS_ERROR(2, "参数错误"),
    VALID_FAILED(3, "验证失败"),
    SUCCESS_CODE(200,"请求成功"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    RPC_ERROR(501, "接口异常"),
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),

    STARTTIME_AFTER_ENDTIME(1000, "开始时间不能晚于结束时间"),
    SECURITY_INVALID_CSRF_TOKEN(1001, "请求验证失败"),
    SECURITY_MISSING_CSRF_TOKEN(1002, "请求伪造"),
    ;

    private final int code;
    private final String message;

    ResponseCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
