package com.leyou.trade.web;

import com.leyou.trade.entity.CartItem;
import com.leyou.trade.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 1:43
 */

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartItemService cartItemService;




    //saveCartItem接收的参数是未登录的参数，次数为了方便就不写
    //DTO ,直接那cartItem接收，由于cartItem是已经登录的对象，
    //需要对 @Id
    //    @JsonIgnore
    //    private String id;
    //    @JsonIgnore
    //    private Long userId;
    //这些加以处理

    /**
     * 新增商品到购物车
     * @param cartItem 商品信息
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> saveCartItem(@RequestBody CartItem cartItem) {

        cartItemService.saveCartItem(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 查询购物车列表
     * @return 当前用户的购物车列表
     */
    //不需要传递参数，只要登录就能查询
    @GetMapping("/list")
    public ResponseEntity<List<CartItem>> queryCartList() {
        return ResponseEntity.ok(cartItemService.queryCartList());

    }



    //前端传过来的数据是FormData 商品id  skuId和num

    /**
     * 更新购物车中的商品数量
     * @param skuId 商品的skuId
     * @param num 最终的数量
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(
            @RequestParam("id")Long skuId, //前端传来的是form表单信息所以
            @RequestParam("num")Integer num //用requestParam
    ) {
        cartItemService.updateCartNum(skuId,num);

        return ResponseEntity.noContent().build();
    }







}
