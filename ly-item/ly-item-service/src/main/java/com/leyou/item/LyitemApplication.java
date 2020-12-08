package com.leyou.item;

import com.leyou.common.annotations.EnableExceptionAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableExceptionAdvice
@SpringBootApplication
//用 @SpringCloudApplication 会报 ClassNotFoundException: com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect 错误
public class LyitemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyitemApplication.class, args);
    }
}
