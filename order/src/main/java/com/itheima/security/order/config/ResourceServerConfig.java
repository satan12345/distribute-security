package com.itheima.security.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
 * @param
 * @author jipeng
 * @date 2020-02-21 13:58
 */
@Configuration
//标记是一个资源服务
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String RESOURCES_ID="res1";

    @Resource
    TokenStore tokenStore;


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                //资源id
                .resourceId(RESOURCES_ID)
                //验证令牌的服务
                //.tokenServices(resourceServerTokenServices())
                //资源服务自己校验
                .tokenStore(tokenStore)
                .stateless(true);
    }

    /**
     * token令牌的验证服务
     * @return
     */
    @Bean
    ResourceServerTokenServices resourceServerTokenServices(){
        //使用原厂服务请求授权服务器校验token 必须制定token的url,client_id,client_secret
        ResourceServerTokenServices tokenServices=new RemoteTokenServices();
        ((RemoteTokenServices) tokenServices).setCheckTokenEndpointUrl("http://localhost:53020/uaa/oauth/check_token");
        ((RemoteTokenServices) tokenServices).setClientId("c1");
        ((RemoteTokenServices) tokenServices).setClientSecret("secret");
        return tokenServices;

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
                //访问权限
               .antMatchers("/**").access("#oauth2.hasScope('all')")
                //关闭CSRF
               .and().csrf().disable()
                //Session管理
               .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}


