package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Spu;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 虎哥
 */
public interface SpuService extends IService<Spu> {
    PageDTO<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id);

    void saveGoods(SpuDTO spuDTO);
}