package org.web.security.csrf;

import com.alibaba.fastjson.JSON;
import org.web.security.util.JedisClusterFactory;
import org.web.security.util.RedisConfigBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Chen Hui
 * @since 2016/3/22
 */

public final class RedisCsrfTokenRepository implements CsrfTokenRepository {
    private static final Log LOG = LogFactory.getLog(RedisCsrfTokenRepository.class);
    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private String CSRF_ID = "CSRF_ID";

    /** 缓存有效期10分钟*/
    private static final long CACHE_EXPIRATION = 1000 * 60 * 10;
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    private JedisCluster jedisCluster;//集群模式的client
    private RedisTemplate<String, String> redisTemplate;//单机模式的client
    private boolean cluster;//是否是集群模式;
    public RedisCsrfTokenRepository(RedisConfigBean bean) {
        initJedisPoolConfig();
        if (bean.isCluster()) {
            JedisClusterFactory clusterFactory = new JedisClusterFactory(bean.getHosts(), bean.getTimeout(), bean.getMaxRedirections());
            setRedisClient(clusterFactory, bean);
        } else {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            String hosts = bean.getHosts();
            String[] ipInfo = hosts.split(":");
            if (ipInfo.length == 2) {
                factory.setHostName(ipInfo[0]);
                factory.setPort(Integer.valueOf(ipInfo[1]));
                factory.setTimeout(bean.getTimeout());
                factory.setDatabase(0);
                factory.setPassword("");
                factory.setUsePool(true);
                setRedisClient(factory, bean);
            } else {
                throw new RuntimeException("ip & port 解析错误");
            }
        }
    }
    private void initJedisPoolConfig() {
        jedisPoolConfig.setMaxTotal(5000);
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setMaxWaitMillis(2000);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setTestWhileIdle(true);
    }

    private void setRedisClient(JedisClusterFactory jedisClusterFactory, RedisConfigBean bean) {
        jedisPoolConfig.setMaxTotal(bean.getMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(bean.getMaxWaitMillis());
        jedisClusterFactory.setPoolConfig(jedisPoolConfig);
        this.cluster = true;
        try {
            jedisClusterFactory.afterPropertiesSet();
            jedisCluster = jedisClusterFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRedisClient(JedisConnectionFactory jedisConnectionFactory, RedisConfigBean bean) {
        jedisPoolConfig.setMaxTotal(bean.getMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(bean.getMaxWaitMillis());
        this.cluster = false;
        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
        jedisConnectionFactory.afterPropertiesSet();
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        StringRedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        setRedisTemplate(redisTemplate);
    }
    private void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisTemplate.afterPropertiesSet();
    }
    private String getCsrfKey(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }
        for(Cookie cookie:cookies){
            if(CSRF_ID.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
    /*
         * (non-Javadoc)
         * @see org.springframework.security.web.csrf.CsrfTokenRepository#saveToken(org.springframework.security.web.csrf.CsrfToken, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request,
                          HttpServletResponse response) {
        String csrfKey = getCsrfKey(request);
        if(csrfKey == null){
            csrfKey = UUID.randomUUID().toString();
            try{
                Cookie csrfCookie = new Cookie(CSRF_ID, csrfKey);
                csrfCookie.setMaxAge(7200);
                csrfCookie.setDomain(".meizu.com");
                csrfCookie.setPath("/");
                response.addCookie(csrfCookie);
            }catch (Exception e){
                LOG.error(e.getMessage());
            }
        }
//        request.setAttribute(CSRF_ID, csrfKey);//for servlet get the csrf key
        if (token == null) {
            if (cluster) {
                jedisCluster.del(csrfKey);
            }else {
                redisTemplate.delete(csrfKey);
            }
        } else {
            if (cluster) {
                jedisCluster.set(csrfKey, JSON.toJSONString(token));
                jedisCluster.pexpire(csrfKey, CACHE_EXPIRATION);
            }else {
                redisTemplate.opsForValue().set(csrfKey, JSON.toJSONString(token));
                redisTemplate.expire(csrfKey,CACHE_EXPIRATION, TimeUnit.MILLISECONDS);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.csrf.CsrfTokenRepository#loadToken(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        String csrfKey = getCsrfKey(request);
        if(csrfKey == null){
            return null;
        }
//        request.setAttribute(CSRF_ID,csrfKey);//for servlet get the csrf key
        if (cluster) {
            String tokenStr = jedisCluster.get(csrfKey);
            if(tokenStr == null){
                return null;
            }
            CsrfToken csrfToken = JSON.parseObject(tokenStr,DefaultCsrfToken.class);
            return csrfToken;
        }else {
            String tokenStr = redisTemplate.opsForValue().get(csrfKey);
            if(tokenStr == null){
                return null;
            }
            CsrfToken csrfToken = JSON.parseObject(tokenStr, DefaultCsrfToken.class);
            return csrfToken;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.security.web.csrf.CsrfTokenRepository#generateToken(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(headerName, parameterName, createNewToken());
    }

    /**
     * Sets the {@link HttpServletRequest} parameter name that the {@link CsrfToken} is expected to appear on
     * @param parameterName the new parameter name to use
     */
    public void setParameterName(String parameterName) {
        Assert.hasLength(parameterName, "parameterName cannot be null or empty");
        this.parameterName = parameterName;
    }

    /**
     * Sets the header name that the {@link CsrfToken} is expected to appear on
     * and the header that the response will contain the {@link CsrfToken}.
     *
     * @param headerName
     *            the new header name to use
     */
    public void setHeaderName(String headerName) {
        Assert.hasLength(headerName, "headerName cannot be null or empty");
        this.headerName = headerName;
    }


    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}

