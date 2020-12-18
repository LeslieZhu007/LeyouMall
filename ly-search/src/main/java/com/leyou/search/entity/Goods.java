package com.leyou.search.entity;

import com.leyou.starter.elastic.annotaions.Id;
import com.leyou.starter.elastic.annotaions.Index;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Leslie Arnold
 */
@Data
@Index("goods")
public class Goods {

    /**
     * 商品的id
     */
    @Id
    private Long id;
    /**
     * 商品标题，用于搜索
     */
    private String title;
    /**
     * 商品预览图，从sku中取出一个即可
     */
    private String image;
    /**
     * 自动补全的候选字段，可以包含多个值，例如分类名称、品牌名称、商品名称
     */
    private Set<String> suggestion;
    /**
     * 商品分类，包含id
     */
    private Long categoryId;
    /**
     * 商品品牌，包含id
     */
    private Long brandId;
    /**
     * 规格参数的key和value对，用于过滤
     */
    private List<Map<String,Object>> specs;
    /**
     * 商品spu中的所有sku的价格集合（滤重）
     */
    private Set<Long> prices;
    /**
     * spu下的多个sku的销量之和
     */
    private Long sold;
    /**
     * 商品更新时间
     */
    private Date updateTime;
}