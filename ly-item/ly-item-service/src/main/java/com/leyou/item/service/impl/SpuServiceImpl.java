package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.service.*;
import com.netflix.discovery.converters.Auto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.ITEM_DOWN_KEY;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.ITEM_UP_KEY;
import static com.leyou.common.constants.MQConstants.ExchangeConstants.ITEM_EXCHANGE_NAME;

/**
 * @author Leslie Arnoald
 */
@Service
public class SpuServiceImpl extends ServiceImpl<SpuMapper, Spu> implements SpuService {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    /*@Autowired  无需注入 this就可代表SpuService对象
    SpuService spuService;*/

    @Autowired
    SkuService skuService;

    @Autowired
    SpuDetailService spuDetailService;


    @Autowired
    SpecParamService specParamService;

    @Autowired
    AmqpTemplate amqpTemplate;


    @Override
    public PageDTO<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id) {
        /*
        * select *
          from tb_spu s
          where
          # s.id=2 and
          s.cid3=76
          and s.brand_id=18374
          and s.saleable=true
          limit 0,5
*/
        //1.分页查询
        Page<Spu> result = query().eq(id!=null,"id", id)
                .eq(categoryId!=null,"cid3", categoryId)
                .eq(brandId!=null,"brand_id", brandId)
                .eq(saleable!=null,"saleable", saleable)
                .page(new Page<>(page, rows));

        //2.解析
        List<Spu> records = result.getRecords();
        List<SpuDTO> spuDTOList = SpuDTO.convertEntityList(records);
        //categoryName; // 商品分类名称拼接
        //String brandName;// 品牌名称
        for (SpuDTO spuDTO : spuDTOList) {
            handleBrandAndCategoryName(spuDTO);
        }

        long total = result.getTotal();
        long totalPage = result.getPages();


        //3.封装DTO
        /*
        * public class PageDTO<T> {
          private Long total;// 总条数
          private Long totalPage;// 总页数
          private List<T> items;// 当前页数据*/

        return new PageDTO<>(total, totalPage,spuDTOList );
    }



    private void handleBrandAndCategoryName(SpuDTO spuDTO) {
        //1.添加分类名称

        /*@JsonIgnore
           public List<Long> getCategoryIds(){
           return Arrays.asList(cid1, cid2, cid3);
    }*/

        //1.1 获取商品关联的三级分类的id集合
        List<Long> categoryIds = spuDTO.getCategoryIds();
        //1.2 查询三级分类的集合
        List<Category> categories = categoryService.listByIds(categoryIds);
        //拼接三级名称

        if(!CollectionUtils.isEmpty(categories)) {
            String names = categories.stream().map(Category::getName).collect(Collectors.joining("/"));

            spuDTO.setCategoryName(names);
        }



        //如果不用lambda表达式则如此写
        /*StringBuilder sb = new StringBuilder();
        for (Category category : categories) {
            sb.append(category.getName()).append(">");
        }
        sb.deleteCharAt(sb.length() - 1);
        spuDTO.setCategoryName(sb.toString());*/




        //2.添加品牌名称
        //2.1 获取品牌id
        Long brandId = spuDTO.getBrandId();
        //2.2 根据品牌id查询品牌对象
        Brand brand = brandService.getById(brandId);
        //2.3 设置品牌名称
        if(brand!=null){
            spuDTO.setBrandName(brand.getName());
        }


    }



    @Override
    @Transactional
    public void saveGoods(SpuDTO spuDTO) {
        //1.新增SPU
        //1.1 DTO转PO
        Spu spu = spuDTO.toEntity(Spu.class);
        //1.2 默认下价
        spu.setSaleable(false);
        //1.3新增
        boolean success = save(spu);

        if (!success) {

            throw new LyException(500,"新增商品失败" );
        }

        //1.4获取回显的id
        Long spuId = spu.getId();

        //2. 新增SPUDetail

        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        // DTO 转PO
        SpuDetail spuDetail = spuDetailDTO.toEntity(SpuDetail.class);
        //填写id
        spuDetail.setSpuId(spuId);
        //写入数据库
        success = spuDetailService.save(spuDetail);

        if (!success) {

            throw new LyException(500,"新增商品失败" );
        }


        //3.新增SKU
        List<SkuDTO> skuDTOS = spuDTO.getSkus();
        //转PO
        List<Sku> skus = new ArrayList<>(skuDTOS.size());
        for (SkuDTO skuDTO : skuDTOS) {
            //dto转po
            Sku sku = skuDTO.toEntity(Sku.class);
            skus.add(sku);
            //填充字段
            sku.setSaleable(false);
            sku.setSpuId(spuId);
            sku.setSold(0L);
        }

        //写入数据库

        skuService.saveBatch(skus);


    }

    @Override
    @Transactional
    public void updateSaleable(Long spuId, Boolean saleable) {
        //更新spu上下架
        //update tb_spu set saleable = #{s} where id = #{spuId}
        Spu spu = new Spu();
        spu.setId(spuId);
        spu.setSaleable(saleable);
        boolean success = updateById(spu);
        if(!success) {
            throw new LyException(500,"更新上下架失败" );
        }
        //但是tb_sku中也有saleable字段，冗余保存
        //2.更新sku的上下架 update tb_sku set saleable = #{s} where spu_id = #{spuId}
        success = skuService.update().set("saleable", saleable)
                .eq("spu_id", spuId
                ).update();

        if(!success) {
            throw new LyException(500,"更新上下架失败" );
        }


        //发送消息
        String routingKey = saleable ? ITEM_UP_KEY : ITEM_DOWN_KEY;
        amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME,routingKey, spuId);
    }

    @Override
    public SpuDTO queryGoodsById(Long spuId) {
        //1.查询spu
        Spu spu = getById(spuId);
        SpuDTO spuDTO = new SpuDTO(spu);
        //2.查询spuDetail
        spuDTO.setSpuDetail(spuDetailService.queryDetailBySpuId(spuId));
        //3.查询sku
        spuDTO.setSkus(skuService.querySkuBySpuId(spuId));
        //4.查询分类和品牌名称
        handleBrandAndCategoryName(spuDTO);
        return spuDTO;
    }



    /**
     * 更新本质和添加差不多
     * @param spuDTO
     */
    @Override
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        //1.判断参数中是否包含spu的id
        Long spuId = spuDTO.getId();
        if(spuId!=null){
            //1.1 spu有变化 转po
            Spu spu = spuDTO.toEntity(Spu.class);
            //1.2确保不更新saleable
            spu.setSaleable(null);  //yml文件中有 update-strategy: NOT_EMPTY # 更新时，只更新非空字段 的策略
            //1.3 更新
            boolean success = updateById(spu);
            if(!success) {
                throw new LyException(500,"更新上下架失败" );
            }
        }

        //2.判断是否包含spuDetail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        if(spuDetailDTO!=null&& spuDetailDTO.getSpuId()!=null){
            SpuDetail spuDetail = spuDetailDTO.toEntity(SpuDetail.class);
            boolean success = spuDetailService.updateById(spuDetail);
            if (!success) {
                throw new LyException(500,"upgrading goods failed" );
            }
        }

        //3.判断是否包含sku
        List<SkuDTO> skuDTOList = spuDTO.getSkus();
        if(CollectionUtils.isEmpty(skuDTOList)) {
            //如果不包含sku,无需修改
            return;
        }
        /*for (SkuDTO skuDTO : skuDTOList) {
            Sku sku = skuDTO.toEntity(Sku.class);
        }*/
        //stream流代替for循环
        Map<Boolean, List<Sku>> skuMap = skuDTOList.stream()
                .map(skuDTO -> skuDTO.toEntity(Sku.class))
                //5.如果包含sku
                //判断是否包含saleable
                .collect(Collectors.groupingBy(sku -> sku.getSaleable() != null));
        //      -5.1 包含saleable： 就是需要删除sku
                List<Sku> deleteSkuList = skuMap.get(true);
                //6.1 判断是否为空
                if(!CollectionUtils.isEmpty(deleteSkuList)) {
                    //6.2 不为空，获取待删除的sku的id集合
                    List<Long> deleteIdList = deleteSkuList.stream().map(Sku::getId).collect(Collectors.toList());
                    //6.3 批量删除
                    skuService.removeByIds(deleteIdList);
                }





        //      7 不包含saleable： 就是需要新增或修改的sku
                List<Sku> saveOrUpdateSkuList = skuMap.get(false);

                if (!CollectionUtils.isEmpty(saveOrUpdateSkuList)) {
                    //7.2 不为空，需要增或改
                    skuService.saveOrUpdateBatch(saveOrUpdateSkuList);
                }

    }

    @Override
    public List<SpecParamDTO> queySpecValue(Long spuId, Boolean searching) {
        //1.查询spu
        Spu spu = getById(spuId);
        //2.获取分类信息
        Long categoryId = spu.getCid3();
        //3.根据分类信息查询规格参数key  tb_spec_param
        //此时specParamDTOS中还没有value
        List<SpecParamDTO> specParamDTOS = specParamService.querySpecParams(categoryId, null, searching);

        //4. 查询规格参数的值 tb_spu_detail
        SpuDetail spuDetail = spuDetailService.getById(spuId);
        //4.1 获取规格参数值
        String specification = spuDetail.getSpecification();
        //4.2把JSON字符串转map,key是specParamDTO的id  value是spec的值
        Map<Long, Object> valueMap = JsonUtils.toMap(specification, Long.class, Object.class);
        //5.给每个param找对应的value
        for (SpecParamDTO specParamDTO : specParamDTOS) {
            Long id = specParamDTO.getId();
            Object value = valueMap.get(id);
            specParamDTO.setValue(value);
        }



        return specParamDTOS;
    }

}