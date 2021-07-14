package com.leyou.trade.service.impl;

import com.leyou.auth.utils.UserHolder;
import com.leyou.trade.entity.CartItem;
import com.leyou.trade.repository.CartItemRepository;
import com.leyou.trade.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 1:44
 */
@Service
public class CartItemServiceImpl implements CartItemService {


    @Autowired
    private CartItemRepository repository;

    @Override
    public void saveCartItem(CartItem cartItem) {
        //尝试从mongoDB获取当前商品
        /*
        * {skuId: 27359021560, title: "小米 MI 6.1 手机 陶瓷黑尊享版 6GB 128GB",…}
            image: "http://image.leyou.com/images/10/7/1524297598540.jpg"
            num: 1
            price: 284900
            skuId: 27359021560
            spec: "{"4":"陶瓷黑尊享版","12":"6GB","13":"128GB"}"
            title: "小米 MI 6.1 手机   陶瓷黑尊享版 6GB 128GB"

            从前端传过来的数据没有userId,要用UserHolder
            */
        //1.获取用户id
        Long userId = UserHolder.getUser().getId();

        Long skuId = cartItem.getSkuId();


        String id = CartItem.generateId(userId, skuId);

        // 尝试从mongoDB获取当前商品
        Optional<CartItem> optional = repository.findById(id);

        //判断是否存在
        /*if(optional.isPresent()){
            //存在，需要获取已经存在的商品的购买数量
            CartItem item = optional.get();
            //与新购买的数量累加
            item.setNum(cartItem.getNum() + item.getNum());
            //写回mongoDB
            repository.save(item);
        } else {
            //不存在
            repository.save(cartItem);

        }*/
        //上述代码简化

        //id和skuId不能忘记更新 id和userId
        cartItem.setId(id);

        cartItem.setUserId(userId);

        if(optional.isPresent()) {

            Integer num = cartItem.getNum();

            cartItem = optional.get();


            cartItem.setNum(num + cartItem.getNum());

        }

        //时间：
        cartItem.setUpdateTime(new Date());
        repository.save(cartItem);


    }

    @Override
    public List<CartItem> queryCartList() {
        //1.获取当前用户
        Long userId = UserHolder.getUser().getId();


        //2.去查询 根据用户id查询  db.user2.find({"name":"jack"})
        return repository.findByUserIdOrderByUpdateTimeDesc(userId);
    }


    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void updateCartNum(Long skuId, Integer num) {
        //获取当前用户
        Long userId = UserHolder.getUser().getId();
        //获取id
        String id = CartItem.generateId(userId, skuId);
        //.update({"_id":""},{$set:{num:xx}})
        mongoTemplate.update(CartItem.class)
                .matching(Query.query(Criteria.where("_id").is(id)))
                .apply(Update.update("num",num))
        .first();
    }
}
