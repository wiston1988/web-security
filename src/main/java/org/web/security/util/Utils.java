package org.web.security.util;

import com.alibaba.fastjson.JSON;
import org.web.security.data.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author Chen Hui
 * @since 2016/5/18
 */
public class Utils {
    /**
     * Pattern for validating jsonp callback parameter values.
     */
    private static final Pattern CALLBACK_PARAM_PATTERN = Pattern.compile("[0-9A-Za-z_\\.]*");
    public static final String JSONP_CALL_BACK_KEY = "callback";

    public static void writeAjaxResponse(HttpServletRequest request,HttpServletResponse response,ResponseResult responseResult) throws IOException {
        if(isJsonPRequest(request)){
            String callbackFunction = request.getParameter(JSONP_CALL_BACK_KEY);
            StringBuilder sb = new StringBuilder();
            response.setContentType("application/javascript;charset=UTF-8");
            response.getWriter().write(sb.append(callbackFunction).append("(").append(JSON.toJSONString(responseResult)).append(")").toString());
            response.getWriter().close();
        }else {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(responseResult));
            response.getWriter().close();
        }
    }
    public static void responseForFailed(HttpServletRequest request, HttpServletResponse response,ResponseResult responseResult) throws IOException {
        String requestWith = request.getHeader("x-requested-with");
        if (requestWith != null && "XMLHttpRequest".equals(requestWith)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(responseResult));
            response.getWriter().close();
        } else if (Utils.isJsonPRequest(request)) {
            String callBackFunction = request.getParameter(Utils.JSONP_CALL_BACK_KEY);
            StringBuilder sb = new StringBuilder();
            response.setContentType("application/javascript;charset=UTF-8");
            response.getWriter().write(sb.append(callBackFunction).append("(").append(JSON.toJSONString(responseResult)).append(")").toString());
            response.getWriter().close();
        } else {
            if (HttpRequestDeviceUtils.isMobileDevice(request)) {
                response.sendRedirect("https://mall.meizu.com/not_found.html");
            } else {
                response.sendRedirect("https://store.meizu.com/not_found.html");
            }
        }
    }

    public static boolean isJsonPRequest(HttpServletRequest request){
        String value = request.getParameter(JSONP_CALL_BACK_KEY);
        boolean isJsonP = false;
        if (value != null) {
            if (isValidJsonpQueryParam(value)) {
                isJsonP = true;
            }
        }
        return isJsonP;
    }
    private static boolean isValidJsonpQueryParam(String value) {
        return CALLBACK_PARAM_PATTERN.matcher(value).matches();
    }

    /**
     * 判断是否为空串
     * @param value
     * @return
     */
    public static boolean isBlank(String value){
        return value == null || value.trim().equals("");
    }
}
