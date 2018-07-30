########版本3.0.0#########
接入完整步骤：
1. pom.xml依赖配置:
	<dependency>
		<groupId>com.meizu.web</groupId>
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
    <bean id="loginFilter" class="LoginFilter">
        <constructor-arg ref="redisConfigBean"/><!-- 存储登录信息的redis配置 -->
        <property name="loginProtectionPatterns" value="${security.loginProtectionPatterns}"/><!--需要校验用户登录的URL表达式，如：/vip/address/**表示/vip/address路径下的所有url都会拦截校验登录-->
        <property name="mobileLoginPatterns" value="${security.mobileLoginPatterns}"/><!--需要跳转到手机登录界面的URL表达式-->
        <property name="hostNameForPC" value="me.meizu.com"/><!--应用的PC端的域名-->
        <property name="hostNameForMobile" value="me.m.meizu.com"/><!--应用的手机端的域名-->
        <property name="supportHttps" value="${security.supportHttps}"/><!--跳转登录时回调页是否支持https-->
    </bean>
    <bean id="xssFilter" class="XSSFilter">
        <property name="xssSecurityConfig" value="xss-security-config.xml"/><!--可以不配置，默认使用自带的。如需要自定义，该值应为classpath下相对路径，如xss/security.xml。配置文件内容说明详见源码中的配置文件注解-->
    </bean>
    <bean id="securityFilter" class="DefaultSecurityFilter">
        <property name="securityFilterList"><!--配置顺序决定执行filter的先后-->
            <list>
                <ref bean="csrfFilter"/><!--csrf校验拦截器-->
                <ref bean="headerWriterFilter"/><!--安全头部写入拦截器-->
                <ref bean="loginFilter"/><!--登录校验拦截器-->
                <ref bean="xssFilter"/><!--xss校验拦截器-->
            </list>
        </property>
    </bean>

备注:
应用中获取用户信息：
controller中注入request，通过以下代码获取：
UserInfo userInfo = (UserInfo)request.getAttribute(LoginFilter.REQ_ATTR_USER_INFO);

#######版本2.1.7#########
新增uuid cookie写入

#######版本2.1.6#########
登录跳转https支持

#######版本2.1.5#########
将csrf token存储在redis中
相应配置如下：
<bean id="redisConfigBean" class="RedisConfigBean">
        <property name="hosts" value="${security.redis.host}"/>
        <property name="timeout" value="10000"/>
        <property name="maxRedirections" value="6"/>
        <property name="cluster" value="true"/>
</bean>
<bean id="csrfRedisConfigBean" class="RedisConfigBean">
	<property name="hosts" value="${promotion.redis.hosts}"/>
	<property name="timeout" value="10000"/>
	<property name="maxRedirections" value="6"/>
	<property name="cluster" value="true"/>
</bean>
<bean id="tokenRedisRepository" class="RedisCsrfTokenRepository">
	<constructor-arg ref="csrfRedisConfigBean"/>
</bean>
<bean id="securityFilter" class="com.meizu.web.security.SecurityFilter1">
	<constructor-arg ref="redisConfigBean"/><!-- 集群/单机 配置bean -->
	<property name="tokenRepository" ref="tokenRedisRepository"/>
</bean>


#######版本2.0.0#########
1. 新增动态改变session redis host调用接口：void sessionRedisChangeHandler(String hosts)
开发可通过实现disconf动态改变配置接口，调用这个接口更新redis hosts


########版本1.0-SNAPSHOT#########
接入步骤：
1. pom.xml依赖配置:
	<dependency>
		<groupId>com.meizu.web</groupId>
		<artifactId>web-security</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
2. web.xml（优先级配置最前）:
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
		<url-pattern>/*</url-pattern>
		<dispatcher>REQUEST</dispatcher>
		<dispatcher>FORWARD</dispatcher>
	</filter-mapping>
3. spring配置：
    <bean id="securityFilter" class="com.meizu.web.security.SecurityFilter1">
        <constructor-arg value="10.2.81.203:6379,10.2.81.203:6379,10.2.81.203:6379,10.2.81.203:6379"/><!--商城登录后用户session存储的redis-->
        <property name="useAlternative" value="true"/><!--是否启用这个安全方案-->
        <property name="ignoreCsrfUrlPatterns" value="/vip/admin/**,/vip/userinfo/saveOrUpdate,/php/**"/><!--需要忽略csrf校验的URL表达式，这里只针对非GET请求的url，不需要配置GET请求的url-->
    	<property name="ignoreLoginProtectionPatterns" value="/static/**"/><!--需要忽略校验用户登录的URL表达式-->
    	<property name="ignoreLoginButFetchUserInfoPatterns" value="/order/**"/><!--需要忽略校验用户登录但是获取用户信息的URL表达式-->
    	<property name="mobileLoginPatterns" value="/mobile/**"/><!--需要跳转到手机登录界面的URL表达式-->
    	<property name="hostNameForPC" value="my.meizu.com"/><!--应用的PC端的域名-->
    	<property name="hostNameForMobile" value="m.my.meizu.com"/><!--应用的手机端的域名-->
    	<property name="adminLoginProtectionPatterns" value="/admin/**"/><!--需要校验后台用户登录的URL表达式-->
    	<property name="adminSessionRedisHost" value="10.2.81.205:6380"/><!--商城后台登录后用户session存储的redis，如果adminLoginProtectionPatterns有值则这个也必须填写-->
    	<property name="getCsrfTokenUrlPatterns" value="/getcsrf/**"/><!--获取csrf的url，可以不配置，默认是/getcsrf/**-->
    	<property name="ignoreXFrameOptionsHeaderPatterns" value="/vip/admin/**"/><!--请求头不写入xframe，适配oms接入使用-->
    	<property name="mallLoginBindPagePatterns" value="/vip/m/order,/vip/m/address"/><!--针对需要跳转到登录和QQ绑定的页面-->
    </bean>

备注:
应用中获取用户信息：
controller中注入request，通过以下代码获取：
UserInfo userInfo = (UserInfo)request.getAttribute(SessionFilter.REQ_ATTR_USER_INFO);
