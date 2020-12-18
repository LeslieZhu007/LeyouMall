package com.leyou.item.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 10:35
 */
@Configuration
@EnableFeignClients(basePackages = "com.leyou.item.client")
public class FeignConfig {

    //配置日志
    @Bean
    public Logger.Level logLevel() {

        return Logger.Level.BASIC;
    }
}
