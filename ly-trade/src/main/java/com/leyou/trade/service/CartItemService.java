package com.leyou.trade.service;

import com.leyou.trade.entity.CartItem;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 1:44
 */
public interface CartItemService {
    void saveCartItem(CartItem cartItem);

    List<CartItem> queryCartList();

    void updateCartNum(Long skuId, Integer num);
}
