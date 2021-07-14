package com.leyou.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.utils.UserHolder;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.trade.DTO.OrderFormDTO;
import com.leyou.trade.entity.Order;
import com.leyou.trade.entity.OrderDetail;
import com.leyou.trade.entity.OrderLogistics;
import com.leyou.trade.entity.enums.OrderStatus;
import com.leyou.trade.mapper.OrderMapper;
import com.leyou.trade.service.OrderDetailService;
import com.leyou.trade.service.OrderLogisticsService;
import com.leyou.trade.service.OrderService;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*循环依赖错误
*
* mvcResourceUrlProvider-->com.leyou.auth.config.MvcConfig
* --->com.leyou.auth.config.AuthConfiguration--->
* com.leyou.auth.client.AuthClient
* 如此再循环依赖
*
*
*
* */

/**
 * @author 虎哥
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private OrderDetailService detailService;

    @Autowired
    private OrderLogisticsService logisticsService;



    //此处是用feign进行的远程调用
    @Autowired
    private UserClient userClient;


    @Override
    @Transactional
    public Long createOrder(OrderFormDTO orderFormDTO) {
        //1.处理order数据
        Order order = new Order();
        //1.1 处理订单金额相关数据，查询sku,根据sku价格计算总金额,根据物流算运费，根据商品算优惠，计算实付金额

        //1.1.1 获取订单中的商品基本信息
        Map<Long, Integer> carts = orderFormDTO.getCarts();
        //1.1.2 获取skuId
        Set<Long> skuIds = carts.keySet();

        //1.1.3 查询sku
        List<SkuDTO> skuList = itemClient.querySkuByIds(skuIds);
        //1.1.4 计算总金额
        long total = 0;
        for (SkuDTO sku : skuList) {
            //判断商品状态 防止出现买前有货，付款时没有货的情况
            if(!sku.getSaleable()){
                //商品下架
                throw new LyException(400,"商品已经下架，无法下单" );
            }


            //获取商品的购买数量
            Integer num = carts.get(sku.getId());
            total += sku.getPrice() * num;
        }
        order.setTotalFee(total);
        order.setPaymentType(orderFormDTO.getPaymentType());
        //TODO 1.1.5. 根据物流算运费，目前全场包邮

        order.setPostFee(0L);
        //TODO 1.1.6 根据商品算优惠，目前没有优惠
        order.setActualFee(total+order.getPostFee());

        //1.2 用户信息
        Long userId = UserHolder.getUser().getId();
        order.setUserId(userId);

        //1.3 默认状态
        order.setStatus(OrderStatus.INIT.getValue());

        //1.4 写入数据库
        boolean success = this.save(order);
        if(!success) {
            throw new LyException(500,"订单新增失败");
        }

        //1.5 id回显
        Long orderId = order.getOrderId();

        //2.处理orderDetail
        //2.1查询所有的sku  上面已经查过
        List<OrderDetail> details = new ArrayList<>(skuIds.size());
        //2.2 将每一个sku,转换为一个OrderDetail
        for (SkuDTO sku : skuList) {
            OrderDetail detail = new OrderDetail();
            details.add(detail);
            detail.setTitle(sku.getTitle());
            detail.setSpec(sku.getSpecialSpec());
            detail.setSkuId(sku.getId());
            detail.setPrice(sku.getPrice());
            detail.setOrderId(orderId);
            detail.setNum(carts.get(sku.getId()));

            //substringBefore   Gets the substring before the first occurrence of a separator.
            detail.setImage(StringUtils.substringBefore(sku.getImages(),"," ));
        }

        //2.3 写入数据库
        detailService.saveBatch(details);

        //3.处理orderLogistics
        //仅仅通过addressId获取完整的物流信息
        Long addressId = orderFormDTO.getAddressId();

        //3.1 先根据id查询地址信息
        //此处要注意，浏览器发起的请求会携带cookie，
        // 但是java代码发起的内部请求根本不会携带cookie
        //所以user服务就会认为是未登录用户，会报错
        //因此需要在Java内部远程调用的时候携带cookie和header
        //UserClient是一个Feign客户端，因此这个请求是由Feign发起的
        //需要在Feign里面给Feign加一个请求头
        /*
        * 2020-12-29 20:08:39.203 ERROR 20340 --- [nio-8087-exec-9]
        * com.leyou.common.advice.CommonLogAdvice  :
        * Long com.leyou.trade.service.impl.OrderServiceImpl.createOrder(OrderFormDTO)
        * 方法执行失败，原因：status 401 reading UserClient#queryAddressById(Long)
        * feign.FeignException$Unauthorized: status 401 reading
        * UserClient#queryAddressById(Long)
        */
        AddressDTO addressDTO = userClient.queryAddressById(addressId);



        //3.2 查询到的地址信息封装到OrderLogistics
        //属性拷贝，前提是只有相同的字段才能被拷贝进去
        OrderLogistics orderLogistics = addressDTO.toEntity(OrderLogistics.class);
        //订单id
        orderLogistics.setOrderId(orderId);

        //3.3 写入数据库
        success = logisticsService.save(orderLogistics);

        if(!success) {
            throw new LyException(500,"订单新增失败" );
        }


        // 4.减库存  在item-api里面暴露一个对外减库存的接口

        //具体减库存的业务要在item-service的controller里面写
        try {
            itemClient.deductStock(carts);
        } catch (FeignException e) {
            throw new LyException(e.status(),e.contentUTF8());
        }

        return orderId;
    }
}