package com.leyou.auth;

import com.leyou.common.annotations.EnableExceptionAdvice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Leslie Arnoald
 */
@EnableExceptionAdvice
@SpringBootApplication
@MapperScan("com.leyou.auth.mapper")
public class LyAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyAuthApplication.class, args);
    }
}