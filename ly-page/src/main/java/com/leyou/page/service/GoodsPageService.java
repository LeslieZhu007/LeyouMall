package com.leyou.page.service;

import com.leyou.item.dto.SpuDTO;
import com.leyou.page.vo.SpuVO;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/20 9:40
 */
public interface GoodsPageService {

    //查询spu，存入redis
    /*1）与SPU有关的信息：
        - id：spu的id
        - name：商品名称
        - cid1\cid2\cid3：分类的三级id
        - brandId：品牌id
        */
    //spuDTO种内容进行精简----->VO
    //CategoryDTO,BrandDTO----------->VO
    //sku不需要重新定义
    //SpuDetail不需要重新定义
    //specParamDTO----------->VO

    String querySpuById(Long spuId);


    String loadSkuListData(Long spuId);

    String loadSpuDetailData(Long spuId);

    String loadCategoriesData(List<Long> ids);

    String loadBrandData(Long id);

    String loadSpecData(Long categoryId);

    void deleteSku(Long spuId);
}
