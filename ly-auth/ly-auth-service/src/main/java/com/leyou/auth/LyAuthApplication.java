package com.leyou.auth;

import com.leyou.common.annotations.EnableExceptionAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Leslie Arnoald
 */
@EnableExceptionAdvice
@SpringBootApplication
public class LyAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyAuthApplication.class, args);
    }
}