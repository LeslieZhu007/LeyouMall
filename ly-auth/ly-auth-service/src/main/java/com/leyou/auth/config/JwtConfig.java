package com.leyou.auth.config;

import com.leyou.auth.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author: Leslie Arnold
        * @Date: 2020/12/23 15:28
        */
@Configuration
public class JwtConfig {

    //注入yml属性值  或者使用configurationProperties前缀
    @Value("${ly.jwt.key}")
    private String key;


    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(key);
    }


    //密码加密器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
