package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.service.*;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 虎哥
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
            String names = categories.stream().map(Category::getName).collect(Collectors.joining(">"));

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
}