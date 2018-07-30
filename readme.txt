########版本3.0.0#########
接入完整步骤：
1. pom.xml依赖配置:
	<dependency>
		<groupId>org.web</groupId>
		<artifactId>web-security</artifactId>
		<version>3.0.0</version>
	</dependency>
2. 配置web.xml：
	<filter>
		<filter-name>securityFilter</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<init-param>
			<param-name>targetFilterLifecycle</param-name>
			<param-value>true</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>securityFilter</filter-name>
		<url-pattern>/*</url-pattern> <!--可以是精确到某路径下，排除admin路径-->
		<dispatcher>REQUEST</dispatcher>
		<dispatcher>FORWARD</dispatcher>
	</filter-mapping>
3. 配置spring配置文件（注意：所有拦截器的property都是可选的，只有需要时才配置）：
	<!-- 统一登录相关
     单机和集群都用这个配置bean:
     hosts:redis服务器的地址,单机{ip:port},集群{ip:port,ip:port}
     timeout:超时时间
     maxRedirections:重试次数,只针对集群有效,可以根据集群的机器数量设置
     cluster:是否为集群模式,maxRedirections只有在cluster为true的时候才生效，默认为true（考虑到现在的机器为单机模式，所以扩展了这个字段，后期再去掉）
    -->
    <bean id="redisConfigBean" class="RedisConfigBean">
        <property name="hosts" value="${security.redis.host}"/>
        <property name="timeout" value="10000"/>
        <property name="maxRedirections" value="6"/>
        <property name="cluster" value="true"/>
    </bean>
    <bean id="tokenRedisRepository" class="RedisCsrfTokenRepository"><!--CSRF存储读写实例-->
        <constructor-arg ref="redisConfigBean"/>
    </bean>
    <bean id="csrfFilter" class="CsrfFilter">
        <property name="ignoreFilterPatterns" value="${security.ignoreCsrfUrlPatterns}"/><!--需要忽略CSRF校验的URL表达式-->
        <property name="getCsrfTokenUrlPatterns" value="${security.getCsrfTokenUrlPatterns}"/><!--获取CSRF的url，可以不配置，默认是/getcsrf/**-->
        <property name="tokenRepository" ref="tokenRedisRepository"/><!--CSRF token存储的位置，默认是存储在session中；如果线上采用的tomcat集群，则需要配置存储在redis集群或者将session共享存储-->
    </bean>
    <bean id="headerWriterFilter" class="HeaderWriterFilter">
        <property name="ignoreFilterPatterns" value="${security.ignoreHeaderWriterFilterPatterns}"/><!--需要忽略安全头写入的URL表达式-->
        <property name="ignoreXFrameOptionsHeaderPatterns" value="${security.ignoreXFrameOptionsHeaderPatterns}"/><!--请求头不写入xframe，一般不需要配置，主要针对oms和前台用户公用的api-->
    </bean>

    <bean id="xssFilter" class="XSSFilter">
        <property name="xssSecurityConfig" value="xss-security-config.xml"/><!--可以不配置，默认使用自带的。如需要自定义，该值应为classpath下相对路径，如xss/security.xml。配置文件内容说明详见源码中的配置文件注解-->
    </bean>
    <bean id="securityFilter" class="DefaultSecurityFilter">
        <property name="securityFilterList"><!--配置顺序决定执行filter的先后-->
            <list>
                <ref bean="csrfFilter"/><!--csrf校验拦截器-->
                <ref bean="headerWriterFilter"/><!--安全头部写入拦截器-->
                <ref bean="xssFilter"/><!--xss校验拦截器-->
            </list>
        </property>
    </bean>
