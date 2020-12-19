package com.leyou.item.test;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/18 22:06
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class MqTest {

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Test
    public void test() {
        amqpTemplate.convertAndSend("simple_queue",123L);
    }
}
