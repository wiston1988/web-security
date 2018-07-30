package org.web.security.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chen Hui
 */
public class EnumUtil {

    public enum SOURCE_TYPE {
        /***
         * 用户来源
         ***/
        //多媒体
        H5_MEDIA("h5_media", 1),

        //mplus
        ANDROID_MALL_MPLUS("mplus", 1),

        //生活服务
        LIFESTYLE("lifestyle", 1),

        //无线商城
        MALL("mall", 1),

        //PC
        STORE("store", 1),

        //魅族商城app
        ANDROID_MALL("android", 1),

        //微信小程序
        WEIXIN("weixin", 2),;
        private String source;
        private Integer type;

        SOURCE_TYPE(String source, Integer type) {
            this.source = source;
            this.type = type;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        private static final Map<String, Integer> sourceTypeMap = new HashMap<>();

        static {
            for (SOURCE_TYPE sourceType : SOURCE_TYPE.values()) {
                sourceTypeMap.put(sourceType.getSource(), sourceType.getType());
            }
        }

        public static Integer getSourceType(Object source) {
            if(StringUtils.isEmpty(source)){
                return -1;
            }
            Integer type = sourceTypeMap.get(source.toString());
            return null == type ? -1 : type;
        }
    }

    public enum USER_ROLE {
        /***
         * 用户角色
         ***/
        BLACK(-1,"黑名单用户"),
        NORMAL(1,"普通用户"),
        MOPSALE(2,"MOPSALE用户"),;

        private int code;
        private String name;

        USER_ROLE(int code,String name) {
            this.code = code;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

    }
}
