package com.leyou.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.trade.DTO.OrderFormDTO;
import com.leyou.trade.entity.Order;

/**
 * @author 虎哥
 */
public interface OrderService extends IService<Order> {
    Long createOrder(OrderFormDTO orderFormDTO);
}