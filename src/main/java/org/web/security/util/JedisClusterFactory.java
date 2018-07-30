package org.web.security.util;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Chen Hui
 * @since 2016/3/28
 */

public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {

    private String redisHosts ;

    private JedisCluster jedisCluster;
    private Integer timeout;
    private Integer maxRedirections;
    private JedisPoolConfig poolConfig;

    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    public JedisClusterFactory(String redisHosts, int timeout, int maxRedirections){
        this.redisHosts = redisHosts;
        this.timeout = timeout;
        this.maxRedirections = maxRedirections;
    }

    public JedisCluster getObject() throws Exception {
        return jedisCluster;
    }

    public Class<? extends JedisCluster> getObjectType() {
        return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
    }

    public boolean isSingleton() {
        return true;
    }

    private Set<HostAndPort> parseHostAndPort() throws Exception {
        try {
            String[] redisHostArr = redisHosts.split(",");
            Set<HostAndPort> haps = new HashSet<>();
            for (String redisHost : redisHostArr) {
                boolean isIpPort = p.matcher(redisHost).matches();

                if (!isIpPort) {
                    throw new IllegalArgumentException("ip 或 port 不合法");
                }
                String[] ipAndPort = redisHost.split(":");

                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                haps.add(hap);
            }

            return haps;
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception("解析Redis地址配置失败", ex);
        }
    }

    public void afterPropertiesSet() throws Exception {
        Set<HostAndPort> haps = this.parseHostAndPort();

        jedisCluster = new JedisCluster(haps, timeout, maxRedirections,poolConfig);

    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public void setRedisHosts(String redisHosts) {
        this.redisHosts = redisHosts;
    }
}