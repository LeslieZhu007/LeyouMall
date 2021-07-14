package com.leyou.trade.repository;

import com.leyou.trade.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 0:43
 */
public interface CartItemRepository extends MongoRepository<CartItem,String> {

    /**
     * 根据用户id查询购物车列表，默认按时间排序
     * @param userId 用户id
     * @return 购物车列表
     */
    List<CartItem> findByUserIdOrderByUpdateTimeDesc(Long userId);
}
