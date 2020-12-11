package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 虎哥
 */
public interface BrandService extends IService<Brand> {


    List<BrandDTO> queryBrandByCategory(Long categoryId);

    PageDTO<BrandDTO> queryBrandByPage(String key, Integer page, Integer rows);

    void saveBrand(BrandDTO brandDTO);
}