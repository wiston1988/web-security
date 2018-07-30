package org.web.security.xss;

import org.web.security.SecurityFilter;
import org.web.security.data.FilterResultWrapper;
import org.web.security.data.ResponseCode;
import org.web.security.exception.WebSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * @author Chen Hui
 * @since 2017/5/22
 */
public class XSSFilter implements SecurityFilter{
    private static final Log LOG = LogFactory.getLog(XSSFilter.class);

    private String xssSecurityConfig = "xss-security-config.xml";

    public XSSFilter(){
        //获取classpath下的文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("xss-security-config.xml");
        XSSSecurityManager.init(inputStream);
    }

    /**
     * 安全审核
     * 读取配置信息
     */
    @Override
    public FilterResultWrapper doFilterInvoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        if(XSSSecurityManager.REPLACE_INVALID){
            // http信息封装类
            XSSHttpRequestWrapper xssRequest = new XSSHttpRequestWrapper(request);
            return new FilterResultWrapper(xssRequest,response);
        }
        // 对request信息进行封装并进行校验工作
        if(XSSSecurityManager.CHECK_HEADER){
            if(!checkHeader(request)){
                LOG.error("request headers have invalid characters:"+request.getRequestURL());
                throw new WebSecurityException(ResponseCode.PARAMS_ERROR);
            }
        }
        if(XSSSecurityManager.CHECK_PARAMETER){
            if(!checkParameter(request)){
                LOG.error("request parameters have invalid characters:"+request.getRequestURL());
                throw new WebSecurityException(ResponseCode.PARAMS_ERROR);
            }
        }

        return new FilterResultWrapper(request,response);
    }

    /**
     * 没有违规的数据，就返回true;
     *
     * @return
     */
    public boolean checkHeader(HttpServletRequest request){
        Enumeration<String> headerParams = request.getHeaderNames();
        while(headerParams.hasMoreElements()){
            String headerName = headerParams.nextElement();
            String headerValue = request.getHeader(headerName);
            if(XSSSecurityManager.matches(headerValue)){
                return false;
            }
        }
        return true;
    }

    /**
     * 没有违规的数据，就返回true;
     *
     * @return
     */
    public boolean checkParameter(HttpServletRequest request){
        Map<String,String[]> submitParams = request.getParameterMap();
        Set<String> submitNames = submitParams.keySet();
        for(String submitName : submitNames){
            String[] submitValues = submitParams.get(submitName);
            for(String submitValue : submitValues){
                if(XSSSecurityManager.matches(submitValue)){
                    return false;
                }
            }
        }
        return true;
    }

    public String getXssSecurityConfig() {
        return xssSecurityConfig;
    }

    public void setXssSecurityConfig(String xssSecurityConfig) {
        this.xssSecurityConfig = xssSecurityConfig;
    }


}
