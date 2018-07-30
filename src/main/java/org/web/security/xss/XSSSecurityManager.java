package org.web.security.xss;

import org.web.security.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author Chen Hui
 * @since 2017/5/22
 */
public class XSSSecurityManager {
    private static final Log LOG = LogFactory.getLog(XSSFilter.class);

    /**
     * 配置文件标签 replace-invalid
     */
    public static String TAG_REPLACE_INVALID = "replace-invalid";

    /**
     * 配置文件标签 check-header
     */
    public static String TAG_CHECK_HEADER = "check-header";

    /**
     * 配置文件标签 check-parameter
     */
    public static String TAG_CHECK_PARAMETER = "check-parameter";

    public static boolean REPLACE_INVALID;
    public static boolean CHECK_HEADER;
    public static boolean CHECK_PARAMETER;

    /**
     * 配置文件标签 regex-list
     */
    public static String TAG_REGEX_LIST = "regex-list";

    /**
     * REGEX：校验正则表达式
     */
    private static String REGEX;

    /**
     * 特殊字符匹配
     */
    private static Pattern XSS_PATTERN;


    private XSSSecurityManager(){
        //不可被实例化
    }

    public static void init(InputStream inputStream){
        LOG.info("XSSSecurityManager init(FilterConfig config) begin");

        // 初始化安全过滤配置
        try {
            if(initConfig(inputStream)){
                // 生成匹配器
                XSS_PATTERN = Pattern.compile(REGEX);
            }
        } catch (DocumentException e) {
            LOG.error("安全过滤配置文件xss-security-config.xml加载异常",e);
        }
        LOG.info("XSSSecurityManager init(FilterConfig config) end");
    }

    /**
     * 读取安全审核配置文件xss-security-config.xml
     * 设置XSSSecurityConfig配置信息
     * @param inputStream 配置文件输入流
     * @return
     * @throws DocumentException
     */
    public static boolean initConfig(InputStream inputStream) throws DocumentException {
        LOG.info("XSSSecurityManager.initConfig(String path) begin");
        Element superElement = new SAXReader().read(inputStream).getRootElement();
        REPLACE_INVALID = new Boolean(getEleValue(superElement,TAG_REPLACE_INVALID));
        CHECK_HEADER = new Boolean(getEleValue(superElement,TAG_CHECK_HEADER));
        CHECK_PARAMETER = new Boolean(getEleValue(superElement,TAG_CHECK_PARAMETER));
        Element regexEle = superElement.element(TAG_REGEX_LIST);

        if(regexEle != null){
            Iterator<Element> regexIt = regexEle.elementIterator();
            StringBuffer tempStr = new StringBuffer("^");
            //xml的cdata标签传输数据时，会默认在\前加\，需要将\\替换为\
            while(regexIt.hasNext()){
                Element regex = (Element)regexIt.next();
                String tmp = regex.getText();
                tmp = tmp.replaceAll("\\\\\\\\", "\\\\");
                tempStr.append(tmp);
                tempStr.append("|");
            }
            if(tempStr.charAt(tempStr.length()-1)=='|'){
                REGEX= tempStr.substring(0, tempStr.length()-1)+"$";
                LOG.info("安全匹配规则"+REGEX);
            }else{
                LOG.error("安全过滤配置文件加载失败:正则表达式异常 "+tempStr.toString());
                return false;
            }
        }else{
            LOG.error("安全过滤配置文件中没有 "+TAG_REGEX_LIST+" 属性");
            return false;
        }
        LOG.info("XSSSecurityManager.initConfig(String path) end");
        return true;

    }

    /**
     * 从目标element中获取指定标签信息，若找不到该标签，记录错误日志
     * @param element 目标节点
     * @param tagName 制定标签
     * @return
     */
    private static String getEleValue(Element element, String tagName){
        if (Utils.isBlank(element.elementText(tagName))){
            LOG.error("安全过滤配置文件中没有 "+TAG_REGEX_LIST+" 属性");
        }
        return element.elementText(tagName);
    }

    /**
     * 对非法字符进行替换
     * @param text
     * @return
     */
    public static String securityReplace(String text){
        if(Utils.isBlank(text)){
            return text;
        }else{
            return text.replaceAll(REGEX, "");
        }
    }

    /**
     * 匹配字符是否含特殊字符
     * @param text
     * @return
     */
    public static boolean matches(String text){
        if(text==null){
            return false;
        }
        return XSS_PATTERN.matcher(text).matches();
    }

}
