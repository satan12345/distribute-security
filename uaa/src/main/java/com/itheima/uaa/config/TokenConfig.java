package com.itheima.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @param
 * @author jipeng
 * @date 2020-02-19 18:09
 */
@Configuration
public class TokenConfig  {

    public static final String SIGNING_KEY="aaa123";

    /**
     * 令牌的存储策略
     * @return
     */
    @Bean
    TokenStore tokenStore(){
        //内存方式生成普通令牌
        //return new InMemoryTokenStore();
        //jwt令牌存储方案
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //对称秘钥，资源服务器使用该秘钥来验证
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }
}

