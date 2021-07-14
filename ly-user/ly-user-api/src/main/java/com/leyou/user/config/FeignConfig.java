package com.leyou.user.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 20:38
 */

@EnableFeignClients(basePackages = "com.leyou.user.client")
@Configuration
public class FeignConfig {
}
