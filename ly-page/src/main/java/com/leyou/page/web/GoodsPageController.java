package com.leyou.page.web;

import com.leyou.page.service.GoodsPageService;
import com.leyou.page.vo.SpuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/20 10:00
 */

@RestController
@RequestMapping("page")
public class GoodsPageController {

    @Autowired
    private GoodsPageService goodsPageService;


    /**
     * 查询spu
     * @param spuId
     * @return spu数据   返回值
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<String> querySpuById(@PathVariable("id")Long spuId) {

        String json = goodsPageService.querySpuById(spuId);

        return ResponseEntity.ok(json);

    }


    /**
     * 查询商品sku数据
     * @param spuId 商品id
     * @return sku数据
     */
    @GetMapping("/sku/{id}")
    public ResponseEntity<String> querySkuPageData(@PathVariable("id") Long spuId){
        return ResponseEntity.ok(goodsPageService.loadSkuListData(spuId));
    }

    /**
     * 查询商品spuDetail数据
     * @param spuId 商品id
     * @return spu数据
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<String> queryDetailPageData(@PathVariable("id") Long spuId){
        return ResponseEntity.ok(goodsPageService.loadSpuDetailData(spuId));
    }

    /**
     * 查询商品分类数据
     * @param ids 商品分类id
     * @return 分类数据
     */
    @GetMapping("/categories")
    public ResponseEntity<String> queryCategoryPageData(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(goodsPageService.loadCategoriesData(ids));
    }

    /**
     * 查询品牌数据
     * @param id 品牌id
     * @return spu数据
     */
    @GetMapping("/brand/{id}")
    public ResponseEntity<String> queryBrandPageData(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsPageService.loadBrandData(id));
    }

    /**
     * 查询规格数据
     * @param categoryId 分类id
     * @return 规格参数
     */
    @GetMapping("/spec/{id}")
    public ResponseEntity<String> queryGoodsPageData(@PathVariable("id") Long categoryId){
        return ResponseEntity.ok(goodsPageService.loadSpecData(categoryId));
    }




}
