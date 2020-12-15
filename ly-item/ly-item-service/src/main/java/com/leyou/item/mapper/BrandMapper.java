package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.aspectj.weaver.ast.Var;

import java.util.List;

/**
 * @author Leslie Arnoald
 */
public interface BrandMapper extends BaseMapper<Brand> {

    @Select("select b.* \n" +
            "from tb_category_brand cb \n" +
            "inner join tb_brand b on b.id = cb.brand_id\n" +
            "where cb.category_id = #{cid}")
    List<Brand> queryByCategoryId(@Param("cid") Long cid);


}