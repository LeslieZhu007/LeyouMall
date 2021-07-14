package com.leyou.auth.config;

import com.leyou.auth.client.AuthClient;
import com.leyou.auth.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 23:40
 */

@Slf4j
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
@ConditionalOnProperty(prefix = "ly.auth",name = {"clientId","secret"}) //yml文件不能写错
public class AuthConfiguration {

    @Autowired
    private AuthClient authClient;

    @Bean
    public JwtUtils jwtUtils(ClientProperties prop) throws InterruptedException {

        //申请密钥可能失败
        //尽量不要在Bean中写死循环，这样会造成Spring的阻塞

        while (true) {
            try {
                //申请密钥
                //此处涉及到远程调用，要用到ly-auth-api中的AuthClient，Feign的远程调用
                //首先注入AuthClient
                String key = authClient.getSecretKey(prop.getClientId(), prop.getSecret());
                log.info("获取密钥成功");

                //初始化jwtutils工具
                return new JwtUtils(key);
            } catch (Exception e) {
                log.error("申请jwt密钥失败，原因:{},会在10秒后重试",e.getMessage() );
                //Thread.sleep(prop.getRetryInterval());
                Thread.sleep(10000);
            }


        }


    }

}
