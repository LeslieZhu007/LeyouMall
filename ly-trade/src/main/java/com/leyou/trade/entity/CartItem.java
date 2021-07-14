package com.leyou.trade.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leyou.auth.utils.UserHolder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
//Document(collection = "cart_item")
//SpEL表达式    @后跟bean的名称 类名首字母小写
@Document(collection = "#{@collectionNameBuilder.build()}")
public class CartItem{
    @Id
    @JsonIgnore
    private String id;
    @JsonIgnore
    private Long userId;
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String spec;// 商品规格参数
    private Date updateTime;// 更新时间


    public static String generateId(Long userId,Long skuId){

        //可以不用取 Long userId = UserHolder.getUser().getId();

        //定义拼接规则
        // a 和 b用什么符号无所谓
        //String.format("a 111 b 111");
        return String.format("a%db%d",userId,skuId);


    }
}