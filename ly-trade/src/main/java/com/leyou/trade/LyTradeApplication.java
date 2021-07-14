package com.leyou.trade;

import com.leyou.common.annotations.EnableExceptionAdvice;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 虎哥
 */
@EnableExceptionAdvice
@SpringBootApplication
@MapperScan("com.leyou.trade.mapper")
public class LyTradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyTradeApplication.class, args);
    }
}