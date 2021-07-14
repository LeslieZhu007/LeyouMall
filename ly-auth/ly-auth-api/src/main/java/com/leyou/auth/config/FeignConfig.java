package com.leyou.auth.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 23:29
 */

//参见Feign的最佳实践及SpringBoot的自动装配原理
@Configuration
@EnableFeignClients(basePackages = "com.leyou.auth.client")
public class FeignConfig {
}
