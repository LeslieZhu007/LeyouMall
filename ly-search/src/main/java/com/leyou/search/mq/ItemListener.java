package com.leyou.search.mq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.ITEM_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.QueueConstants.SEARCH_ITEM_UP;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.ITEM_UP_KEY;

/**SEARCH_ITEM_UPtrue
 * @Author: Leslie Arnold
 * @Date: 2020/12/18 11:38
 */
@Component
public class ItemListener {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SEARCH_ITEM_UP,durable = "true"),
            exchange =@Exchange(name = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = ITEM_UP_KEY
    ))
    public  void listenItemUp() {

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SEARCH_ITEM_UP,durable = "true"),
            exchange =@Exchange(name = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = ITEM_UP_KEY
    ))
    public  void listenItemDown() {

    }
}
