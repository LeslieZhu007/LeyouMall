package com.leyou.auth.config;

import com.leyou.auth.interceptor.LoginInterceptor;
import com.leyou.auth.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 10:55
 */
//要使拦截器生效，必须要有一个配置文件
@Configuration
//带有enable开关的mvc
@ConditionalOnProperty(prefix = "ly.auth",name = "enable",havingValue = "true")
public class MvcConfig implements WebMvcConfigurer {

    /*
    * /*循环依赖错误
     *
     * mvcResourceUrlProvider-->com.leyou.auth.config.MvcConfig
     * --->com.leyou.auth.config.AuthConfiguration--->
     * com.leyou.auth.client.AuthClient
     * 如此再循环依赖
     * 在jwutils上加上@lazy
     * 让其延迟加载，先把MvcConfig的
     * bean创建好
     * 因为jwUtils是在
     * addInterceptors中才会用到
     *
     *
     * */
    @Autowired
    @Lazy
    private JwtUtils jwtUtils;

    @Autowired
    private ClientProperties clientProperties;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册一个拦截器
        InterceptorRegistration registration = registry.addInterceptor(new LoginInterceptor(jwtUtils));

        //添加拦截路径，不添加默认拦截所有
        //做非空判断，而不是错误
        List<String> includePathPatterns = clientProperties.getIncludePathPatterns();
        if(!CollectionUtils.isEmpty(includePathPatterns)) {
            registration.addPathPatterns(includePathPatterns);
        }



        //添加不拦截的路径
        //做非空判断，而不是错误
        List<String> excludePathPatterns = clientProperties.getExcludePathPatterns();

        if(!CollectionUtils.isEmpty(excludePathPatterns)) {
            registration.excludePathPatterns(excludePathPatterns);
        }


    }
}
