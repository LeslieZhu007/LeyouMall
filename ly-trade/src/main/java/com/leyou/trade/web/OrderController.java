package com.leyou.trade.web;

import com.leyou.trade.DTO.OrderDTO;
import com.leyou.trade.DTO.OrderFormDTO;
import com.leyou.trade.entity.Order;
import com.leyou.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 9:51
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 创建
     * @param orderFormDTO
     * @return
     */
    @PostMapping

    public ResponseEntity<Long> createOrder(@RequestBody OrderFormDTO orderFormDTO) {
        Long orderId = orderService.createOrder(orderFormDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);

    }

    /**
     * 根据id查询订单
     * @param orderId 订单id
     * @return  orderDTO 订单信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> queryOrderById(@PathVariable("id")Long orderId) {
        Order order = orderService.getById(orderId);
        OrderDTO orderDTO = new OrderDTO(order);

        return ResponseEntity.ok(orderDTO);

    }
}
