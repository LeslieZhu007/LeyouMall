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


/**
 * SmsListener的主要作用就是发短信
 */
@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    //MQConstants
    /*@RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SMS_VERIFY_CODE_QUEUE,durable = "true"),
            exchange = @Exchange(name = SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = VERIFY_CODE_KEY
    ))*/

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SMS_VERIFY_CODE_QUEUE),
            exchange = @Exchange(name = SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = VERIFY_CODE_KEY
    ))


    //phone,code是一个整体，所以用map接口
    public void listenVerifyCode(Map<String,String> msg) {

        //获取消息
        if(CollectionUtils.isEmpty(msg)){
            //不需要处理，直接retyrnb
            return;
        }

        String phone = msg.get("phone");
        //手机号做正则表达式校验 Common中的工具
        if(!RegexUtils.isPhone(phone)){
            return;
        }


        String code = msg.get("code");

        //code校验 6位以内的数字或字母   RegexPatterns
        if(!RegexUtils.isCodeValid(code)){
            return;
        }


        smsUtil.sendVerificationCode(phone, code);

    }



}
