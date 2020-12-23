package com.leyou.ms;

import com.leyou.sms.LySmsApplication;
import com.leyou.sms.utils.SmsUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.QueueConstants.SMS_VERIFY_CODE_QUEUE;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/22 16:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySmsApplication.class)
public class SmsUtilTest {
    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSend() {

        String code = RandomStringUtils.randomNumeric(6);
        System.out.println("code = " + code);
        smsUtil.sendVerificationCode("13057085612",code );


    }

    @Test
    public void testMQ() {
        Map<String,String> msg = new HashMap<>();
        String code = RandomStringUtils.randomNumeric(6);
        System.out.println("code = " + code);
        smsUtil.sendVerificationCode("13057085612",code );
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, SMS_VERIFY_CODE_QUEUE,msg);
    }
}
