package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * @author Leslie Arnoald
 */
public interface SkuMapper extends BaseMapper<Sku> {

    @Update("update tb_sku set stock = stock - #{num}," +
            "sold = sold + #{num} where id = #{id} and saleable = 1")
    int deductStock(Map<String,Object> sku);


    //在mybatis源码中
    //@Param("num")Integer num,@Param("id")Long id --->
    // 会转换为 Map<String,Object> sku
    //key1=Integer,value1=num
    //key2=Long,value2=id
    //int deductStock(@Param("num")Integer num,@Param("id")Long id);
}