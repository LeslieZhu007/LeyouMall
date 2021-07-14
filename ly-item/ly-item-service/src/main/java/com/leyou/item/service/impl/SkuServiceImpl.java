package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.service.SkuService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Leslie Arnoald
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    private static final String UPDATE_STOCK_STATEMENT =
            "com.leyou.item.mapper.SkuMapper.deductStock";


    @Override
    public List<SkuDTO> querySkuBySpuId(Long spuId) {

        // 不能用此Sku sku = getById(spuId);查，因为 spuId不是主键
        //select * from tb_sku where spu_id=#{}

        List<Sku> skuList = query().eq("spu_id", spuId).list();

        List<SkuDTO> skuDTOList = SkuDTO.convertEntityList(skuList);

        return skuDTOList;


    }


    /*
    * 解决线程的安全问题
    *
    * 1.加锁 synchronized  synchronized的对象是this,在堆空间 SkuServiceImpl@0x001
    *
    * 锁的原理就是一个监视器
    *
    * sql表中用unsigned这个无符号数解决了线程的安全问题
    * 但是sql语句的实现不能写在java中，只能写在sql语句中
    *
    *
    *
    *
    *
    *
    *
    *
    *
    * */





    @Override
    @Transactional
    public void deductStock(Map<Long, Integer> skuMap) {

        //mybatis plus自带的批处理方法
        //遍历skuMap
/*for (Map.Entry<Long, Integer> entry : skuMap.entrySet()) {
     Long skuId = entry.getKey();
     Integer num = entry.getValue();

    *//*
    //查询sku
     Sku sku = this.getById(skuId);

     //判断库存
     if(sku.getStock() < num) {

         //库存不足
         throw new LyException(400,"库存不足");
     }

     //库存充足，可以扣减
     //update tb_sku set stock=#{x},sold = #{y} where id = #{id}
     this.update()
             .set("stock",sku.getStock()-num )
             .set("sold",sku.getSold()+num)
             .eq("id",skuId).update();*//*

    //调用mapper中的方法
     Map<String,Object> param = new HashMap<>();
     param.put("id",skuId );
     param.put("num",num );
     int count = this.getBaseMapper().deductStock(param);
     if(count == 0) {
         throw new LyException(400,"更新库存失败，可能已经下架" );
     }
 }*/

        //批处理的方法 没有办法知道每条sql语句的执行的情况
        this.executeBatch(sqlSession -> {
            //遍历sku
            for (Map.Entry<Long, Integer> entry : skuMap.entrySet()) {
                Long skuId = entry.getKey();
                Integer num = entry.getValue();
                Map<String,Object> param = new HashMap<>();
                param.put("id",skuId );
                param.put("num",num );
                //编译sql,第一个参数：可以是sql,也可以
                //是statementId:mapper接口名，方法名
                //第二个参数是sql需要的参数
                int count = sqlSession.update(UPDATE_STOCK_STATEMENT, param);
                if(count == 0) {
                    throw new LyException(400,"更新库存失败，可能已经下架" );
                }
                //刷新和提交
                sqlSession.flushStatements();
        }
    });
}
}
