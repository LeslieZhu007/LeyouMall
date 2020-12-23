package com.leyou.sms.mq;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.leyou.common.utils.RegexUtils;
import com.leyou.sms.utils.SmsUtil;
import com.sun.prism.MaskTextureGraphics;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.QueueConstants.SMS_VERIFY_CODE_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/23 8:51
 */

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    //MQConstants
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SMS_VERIFY_CODE_QUEUE,durable = "true"),
            exchange = @Exchange(name = SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = VERIFY_CODE_KEY
    ))

    public void listenVerifyCode(Map<String,String> msg) {

        //获取消息
        if(CollectionUtils.isEmpty(msg)){
            //
            return;
        }

        String phone = msg.get("phone");
        //手机号做正则表达式校验
        if(!RegexUtils.isPhone(phone)){
            return;
        }


        String code = msg.get("code");

        //code校验
        if(!RegexUtils.isCodeValid(code)){
            return;
        }


        smsUtil.sendVerificationCode(phone, code);

    }



}
