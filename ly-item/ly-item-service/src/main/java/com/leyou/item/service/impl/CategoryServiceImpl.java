package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.service.CategoryBrandService;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 虎哥
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryBrandService  categoryBrandService;



    @Override
    public List<CategoryDTO> queryCategoryByBrandId(Long brandId) {

        //查询中间表
        //select * from tb_category_brand where brand_id = 18374
        List<CategoryBrand> categoryBrandList = categoryBrandService.query().eq("brand_id", brandId).list();

        //从CategoryBrand获取分类id
        List<Long> idList = categoryBrandList.stream()
                .map(CategoryBrand::getCategoryId).collect(Collectors.toList());

        //此处省略了this,this.listByIds
        List<Category> list = listByIds(idList);
        //转换DTO
        List<CategoryDTO> categoryDTOList = CategoryDTO.convertEntityList(list);

        return categoryDTOList;
    }
}