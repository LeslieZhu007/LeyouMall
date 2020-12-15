package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.BrandService;
import com.leyou.item.service.CategoryBrandService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Leslie Arnoald
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {


    @Autowired
    CategoryBrandService categoryBrandService;

    @Override
    public List<BrandDTO> queryBrandByCategory(Long categoryId) {

        BrandMapper brandMapper = getBaseMapper();
        List<Brand> brands = brandMapper.queryByCategoryId(categoryId);

        List<BrandDTO> brandDTOS = BrandDTO.convertEntityList(brands);


        return brandDTOS;


    }

    @Override
    public PageDTO<BrandDTO> queryBrandByPage(String key, Integer page, Integer rows) {
        //0.健壮性判断
        page = Math.max(1,page );
        rows = Math.max(1,rows );
        //判断是否有搜索关键字
        boolean isNotBlank = StringUtils.isNotBlank(key);
        /*-- `name`加了转义符号
            -- 分页查询
            select * from tb_brand b
            where b.`name` like "%x%" or
            b.letter = "x"
            limit 0,5*/
        Page<Brand> result = query()
                .like("name", key)
                .or()
                .eq(isNotBlank, "letter", key)
                .page(new Page<>(page, rows));

        //2.解析
        long total = result.getTotal();
        long totalPage = result.getPages();
        List<Brand> list = result.getRecords();
        List<BrandDTO> brandDTOList = BrandDTO.convertEntityList(list);
        return new PageDTO<BrandDTO>(total,totalPage ,brandDTOList );
    }

    @Override
    @Transactional //要加事务
    public void saveBrand(BrandDTO brandDTO) {
        //1.把DTO 转 PO
        Brand brand = brandDTO.toEntity(Brand.class);
        boolean success = save(brand);

        if(!success) {

            throw new LyException(500,"更新品牌失败");
        }

        //回显id
        Long brandId = brand.getId();

        //3.中间表新增
        //3.1 获取与品牌有关的所有分类id
        List<Long> categoryIds = brandDTO.getCategoryIds();

        //3.2把分类id与品牌id组合，封装为CategoryBrand
        List<CategoryBrand> categoryBrands = new ArrayList<>(categoryIds.size());

        for (Long categoryId : categoryIds) {
            CategoryBrand categoryBrand = CategoryBrand.of(categoryId, brandId);
            categoryBrands.add(categoryBrand);

        }

        //改进为Stream流写法

        categoryBrandService.saveBatch(categoryBrands);




    }
}