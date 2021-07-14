package com.leyou.trade.config;

import com.leyou.auth.utils.UserHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.catalina.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 14:49
 */

/*进入ly-trade的一切请求都会先经过
* LoginInterceptor这个拦截器，拦截器中
* 已经解析出jwt,jwt就是浏览器Cookie中
* 的内容，所以，在拿到Jwt之后将其保存起来
* 保存到UserHolder上下文中
* */


@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            //添加请求头
            template.header("Cookie","LY_TOKEN=" + UserHolder.getJwt());
        };
    }
}
