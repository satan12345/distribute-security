package com.itheima.uaa.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @param
 * @author jipeng
 * @date 2020-02-19 17:28
 */
@Configuration
//标识是一个授权服务
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Resource
    TokenStore tokenStore;
    /**
     * 客户端详情服务
     */
    @Resource
    ClientDetailsService clientDetailsService;
    /**
     * 授权码模式需要
     */
    @Resource
    AuthorizationCodeServices authorizationCodeServices;
    /**
     *
     * 认证管理器（选择密码模式的时候需要）
     */
    @Resource
    AuthenticationManager authenticationManager;
        /**
     * 选择密码模式的时候需要
      */
    @Resource
    UserDetailsService springDataUserDetailsService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    JwtAccessTokenConverter jwtAccessTokenConverter;



    /**
     * 用来配置令牌端点的安全策略
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security)  {
        /**
         * /oauth/authorize：授权端点。
         * /oauth/token：令牌端点。
         * /oauth/confirm_access：用户确认授权提交端点。
         * /oauth/error：授权服务错误信息端点。
         * /oauth/check_token：用于资源服务访问的令牌解析端点。
         * /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
         */
        security.
                //1）tokenkey这个endpoint当使用JwtToken且使用非对称加密时，资源服务用于获取公钥而开放的，
                // 这里指这个 endpoint完全公开。 /oauth/token_key
                tokenKeyAccess("permitAll()")
                //checkToken这个endpoint完全公开  /oauth/check_token
                .checkTokenAccess("permitAll()")
                //允许表单认证
                .allowFormAuthenticationForClients();



    }
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource){
        ClientDetailsService clientDetailsService=new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    /**
     * 用来配置客户端的详情服务
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //将客户端的信息保存到数据库中
        clients.withClientDetails(clientDetailsService);

//            clients
//                    //暂时使用内存的方式
//                    .inMemory()
//                    //客户端Id
//                    .withClient("c1")
//                    //秘钥
//                    .secret(passwordEncoder.encode("secret"))
//                    //客户端可以访问的资源列表
//                    .resourceIds("res1")
//                    // 该client允许的授权类型
//                    .authorizedGrantTypes("authorization_code",
//                            "password","client_credentials","implicit","refresh_token")
//                    // 允许的授权范围
//                    .scopes("all")
//                    //false 跳转到授权页面 为true的话 直接发放令牌
//                    .autoApprove(false)
//                    //加上验证回调地址
//                    .redirectUris("http://www.baidu.com");

    }

    /**
     * 用来配置令牌访问端点和令牌服务
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //密码模式
        endpoints.authenticationManager(authenticationManager)
        //授权码模式需要
        .authorizationCodeServices(authorizationCodeServices)
        //令牌管理服务
        .tokenServices(tokenService())
        //允许post提交来访问断点
        .allowedTokenEndpointRequestMethods(HttpMethod.POST);



    }

    /**
     * 设置授权码模式时授权码如何存取 暂时采用内存的方式
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource){
        JdbcAuthorizationCodeServices jdbcAuthorizationCodeServices=new JdbcAuthorizationCodeServices(dataSource);
        return jdbcAuthorizationCodeServices;
        //授权码存在内存
       // return new InMemoryAuthorizationCodeServices();
    }

    /**
     * 令牌服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenService(){
        DefaultTokenServices service=new DefaultTokenServices();
        //客户端信息服务 clientDetailsService 客户端详情服务
        service.setClientDetailsService(clientDetailsService);
        //是否支持刷新令牌
        service.setSupportRefreshToken(true);
        //临牌的存储策略
        service.setTokenStore(tokenStore);
        //设置令牌增强
        TokenEnhancerChain tokenEnhancerChain=new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Lists.newArrayList(jwtAccessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);

        //令牌默认有效期
        service.setAccessTokenValiditySeconds(7200);
        //刷新令牌默认有效期限
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }
}

