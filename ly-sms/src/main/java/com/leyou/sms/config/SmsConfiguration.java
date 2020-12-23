package com.leyou.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/22 15:38
 */

@Component
public class SmsConfiguration {

    @Bean
    public IAcsClient acsClient(SmsProperties prop) {

        DefaultProfile profile = DefaultProfile.getProfile(prop.getRegionID(), prop.getAccessKeyID(), prop.getAccessKeySecret());

        return new DefaultAcsClient(profile);
    }


    //消息转换器
    public class MqConfig {
        @Bean
        public Jackson2JsonMessageConverter messageConverter(){
            return new Jackson2JsonMessageConverter();
        }
    }

}
