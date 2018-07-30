package org.web.security.util;

/**
 * Created by Chen Hui.
 */
public class RedisConfigBean{
    private String hosts;
    private int maxRedirections = 12;
    private int timeout = 2000;
    //是否为集群模式,默认为true
    private boolean cluster = true;

    //最大连接数
    private int maxTotal = 5000;

    //最长等待时间(多长时间连接不到就报错)
    private int maxWaitMillis = 3000;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }
}
