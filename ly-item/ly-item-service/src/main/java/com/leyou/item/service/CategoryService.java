package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<CategoryDTO> queryCategoryByBrandId(Long brandId);

}