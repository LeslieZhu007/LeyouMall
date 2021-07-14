package com.leyou.auth.config;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/25 0:02
 */
//创建ClientProperties读取yml文件中 ly auth中的配置
    //通过ConfigurationProperties读取ly.auth的属性

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "ly.auth")
public class ClientProperties {



    private String clientId;


    private String secret;

    private List<String> includePathPatterns;
    private List<String> excludePathPatterns;

}
