package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
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

    /**
     * 更新商品的上下架逻辑
     * @param spuId 商品id
     * @param saleable 上下架 true 上架  false:下架
     * @return 无
     */
    @PutMapping("/saleable")
    //接口文档有问题，required都应为true
    public ResponseEntity<Void> updateSaleable(@RequestParam("id")Long spuId,
                                               @RequestParam("saleable") Boolean saleable) {

        spuService.updateSaleable(spuId, saleable);

        //更新成功返回204
        //return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.noContent().build();


    }

    /**
     * 用于更新的回显,此处回显的内容包括了 spu,spuDetail,sku
     * 根据id查询商品spu,spuDetail,sku
     * @param spuId 商品id
     * @return 商品的所有信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpuDTO> queryGoodsById(@PathVariable("id") Long spuId) {

        SpuDTO spuDTO = spuService.queryGoodsById(spuId);

        return ResponseEntity.ok(spuDTO);
    }


    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO) {

        //新增
        spuService.updateGoods(spuDTO);

        //新增成功返回201
        return ResponseEntity.noContent().build();
    }


    /**
     * 根据id查询商品详情
     * @param spuId  商品id
     * @return 商品详情
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> queryDetailBySpuId(@RequestParam("id")Long spuId){

        SpuDetailDTO spuDetailDTO = detailService.queryDetailBySpuId(spuId);


        return ResponseEntity.ok(spuDetailDTO);
    }


    /**
     * 根据spu的id查sku集合
     * @param spuId 商品id
     * @return sku的集合
     */
    @GetMapping("/sku/of/spu")  //spuId只是sku中的普通字段，不是主键
    public ResponseEntity<List<SkuDTO> > querySkuListBySpuId(@RequestParam("id")Long spuId){

        List<SkuDTO> skuDTOS = skuService.querySkuBySpuId(spuId);


        return ResponseEntity.ok(skuDTOS);
    }

    /**
     * 根据id批量查询sku
     * @param ids sku的id集合
     * @return sku的集合
     */
    @GetMapping("/sku/list")  //spuId只是sku中的普通字段，不是主键
    public ResponseEntity<List<SkuDTO> > querySkuListByIds(@RequestParam("ids")List<Long> ids){

        List<Sku> skus = skuService.listByIds(ids);
        List<SkuDTO> skuDTOS = SkuDTO.convertEntityList(skus);


        return ResponseEntity.ok(skuDTOS);
    }

    @GetMapping("/spec/value")
    public ResponseEntity<List<SpecParamDTO>> queySpecValue(
            @RequestParam("id")Long spuId,@RequestParam(value = "searching",required = false)Boolean searching
    ){

        List<SpecParamDTO> params = spuService.queySpecValue(spuId,searching);
        return ResponseEntity.ok(params);


    }

    /**
     * 根据id查询spu
     * @param spuId spu的id
     * @return 商品spu的数据
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> querySpuById(@PathVariable("id")Long spuId) {
        Spu spu = spuService.getById(spuId);
        return ResponseEntity.ok(new SpuDTO(spu));

    }


    /**
     * 扣减库存
     * @param skuMap sku信息，key:skuId,value:扣减数量
     */
    //http://localhost/api/doc.html -->商品管理--> 批量减库存

    @PutMapping("/stock/minus")
    ResponseEntity<Void> deductStock(@RequestBody Map<Long,Integer> skuMap) {
        //业务比较复杂，放到service中去做
        skuService.deductStock(skuMap);

        return ResponseEntity.noContent().build();
    };




}