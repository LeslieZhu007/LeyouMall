package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.SkuService;
import com.leyou.item.service.SpuDetailService;
import com.leyou.item.service.SpuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("goods")
public class GoodsController {

    private final SpuService spuService;

    private final SpuDetailService detailService;

    private final SkuService skuService;

    public GoodsController(SpuService SpuService, SpuDetailService detailService, SkuService skuService) {
        this.spuService = SpuService;
        this.detailService = detailService;
        this.skuService = skuService;
    }


    /**
     *  分页查询spu
     * @param page 当前页码
     * @param rows 每页大小
     * @param saleable 上下架
     * @param categoryId 分类id
     * @param brandId 品牌id
     * @param id 商品spu的id
     * @return spu的分页结果
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageDTO<SpuDTO>> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "brandId", required = false) Long brandId,
            @RequestParam(value = "id", required = false) Long id
    ) {

        PageDTO<SpuDTO> result = spuService.querySpuByPage(page,rows,saleable,categoryId,brandId,id);

        return ResponseEntity.ok(result);
    }


    /**
     * 新增商品
     * @param spuDTO 商品详细信息  包括SPU,SpuDetail.SKU
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO) {

        //新增
        spuService.saveGoods(spuDTO);

        //新增成功返回201
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}