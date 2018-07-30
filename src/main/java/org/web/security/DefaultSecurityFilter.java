package org.web.security;


import org.web.security.csrf.CsrfFilter;
import org.web.security.data.FilterResultWrapper;
import org.web.security.exception.WebSecurityException;
import org.web.security.header.HeaderWriterFilter;
import org.web.security.data.ResponseCode;
import org.web.security.data.ResponseResult;
import org.web.security.util.Utils;
import org.web.security.xss.XSSFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chen Hui
 */
public class DefaultSecurityFilter extends OncePerRequestFilter {
    private static final Log LOG = LogFactory.getLog(DefaultSecurityFilter.class);
    private boolean isFilter = true;

    private List<SecurityFilter> securityFilterList = new ArrayList<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        FilterResultWrapper filterResultWrapper = new FilterResultWrapper(request,response);
        if (isFilter && securityFilterList.size()>0) {
            try{
                for(int i = 0; i < securityFilterList.size(); i++){
                    filterResultWrapper = securityFilterList.get(i).doFilterInvoke(filterResultWrapper.getRequest(), filterResultWrapper.getResponse());
                    //在filter里已经返回response，则不需要再执行下一个filter了
                    if(filterResultWrapper.isInterrupt()){
                        return;
                    }
                }
            }catch (Exception e){
                LOG.error(e.getMessage(), e);
                if(e instanceof WebSecurityException){
                    Utils.responseForFailed(filterResultWrapper.getRequest(), filterResultWrapper.getResponse(), new ResponseResult(((WebSecurityException)e).getResponseCode()));
                }else {
                    Utils.responseForFailed(filterResultWrapper.getRequest(), filterResultWrapper.getResponse(), new ResponseResult(ResponseCode.INTERNAL_SERVER_ERROR));
                }
                return;
            }
        }
        filterChain.doFilter(filterResultWrapper.getRequest(), filterResultWrapper.getResponse());
    }

    @Override
    protected void initFilterBean() throws ServletException {
        if(securityFilterList.size()>0){
            return;
        }
        //防止并发初始化
        synchronized (securityFilterList) {
            securityFilterList.add(new HeaderWriterFilter());
            securityFilterList.add(new CsrfFilter());
            securityFilterList.add(new XSSFilter());
        }
    }

    public List<SecurityFilter> getSecurityFilterList() {
        return securityFilterList;
    }

    public void setSecurityFilterList(List<SecurityFilter> securityFilterList) {
        this.securityFilterList = securityFilterList;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }
}
