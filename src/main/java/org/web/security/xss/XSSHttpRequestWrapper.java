package org.web.security.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author Chen Hui
 * @since 2017/5/22
 */
public class XSSHttpRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 封装http请求
     * @param request
     */
    public XSSHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        //对特殊字符进行替换
        return XSSSecurityManager.securityReplace(value);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        //对特殊字符进行替换
        return XSSSecurityManager.securityReplace(value);
    }

}
