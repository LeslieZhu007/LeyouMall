package com.leyou.user;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.annotations.EnableExceptionAdvice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableExceptionAdvice
@SpringBootApplication
@MapperScan("com.leyou.user.mapper")
public class LyUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUserApplication.class, args);
    }


    //消息转换器  可以直接挂到启动类中  因为启动类本质也是配置类
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}