pentaho_autologin
=================

This is a SPRING Filter that can be included in the Pentaho Security Layer to make the platform to auto-login an specific user when an specific URL is accessed.

Intall Procedure
================

In the file "applicationContext-spring-security.xml" in the Pentaho System folder add

<!-- custom auto-login begin -->
  <bean id="AutoLoginFilter" class="com.analytics_bi.pentaho.loginfilter.CustomLoginFilter">
    <property name="authenticationManager">
      <ref local="authenticationManager" />
    </property>
    <property name="username" value="test"/>
    <property name="password" value="password"/>
    <property name="trusted_path" value="(.*)OpenSolution(.*)"/>
  </bean>
<!-- custom auto-login end -->


In the same file "filterChainProxy" bean, add the new bean defined to the list like in this
example…. Take a close look to the position

<bean id="filterChainProxy" class="org.springframework.security.util.FilterChainProxy">
  <property name="filterInvocationDefinitionSource">
  <!--
  You can safely remove the first pattern starting with /content/dashboards/print, if you're not using
  Enterprise Dashboards or not allowing printing of Dashboards,
  -->
  <value>
    <![CDATA[CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
    PATTERN_TYPE_APACHE_ANT
    /api/repos/dashboards/print=securityContextHolderAwareRequestFilter,httpSessionPentahoSessionContextIntegrationF
    ilter,httpSessionContextIntegrationFilter,preAuthenticatedSecurityFilter,httpSessionReuseDetectionFilter,logoutFilter,authenti
    cationProcessingFilter,basicProcessingFilter,requestParameterProcessingFilter,anonymousProcessingFilter,exceptionTransl
    ationFilter,filterInvocationInterceptor
    /webservices/**=securityContextHolderAwareRequestFilterForWS,httpSessionPentahoSessionContextIntegrationFilter,
    httpSessionContextIntegrationFilter,basicProcessingFilter,anonymousProcessingFilter,exceptionTranslationFilterForWS,filterI
    nvocationInterceptorForWS
    /api/**=securityContextHolderAwareRequestFilterForWS,httpSessionPentahoSessionContextIntegrationFilter,httpSessi
    onContextIntegrationFilter,AutoLoginFilter,basicProcessingFilter,anonymousProcessingFilter,exceptionTranslationFilterForW
    S,filterInvocationInterceptorForWS
    /plugin/**=securityContextHolderAwareRequestFilterForWS,httpSessionPentahoSessionContextIntegrationFilter,httpSe
    ssionContextIntegrationFilter,AutoLoginFilter,basicProcessingFilter,anonymousProcessingFilter,exceptionTranslationFilterFo
    rWS,filterInvocationInterceptorForWS
    /**=securityContextHolderAwareRequestFilter,httpSessionPentahoSessionContextIntegrationFilter,httpSessionContextI
    ntegrationFilter,httpSessionReuseDetectionFilter,logoutFilter,AutoLoginFilter,authenticationProcessingFilter,basicProcessing
    Filter,requestParameterProcessingFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor]]>
    </value>
  </property>
</bean>

In this example when a user uses in the URL the word OpenSolution then it will be automatically logged as test user
