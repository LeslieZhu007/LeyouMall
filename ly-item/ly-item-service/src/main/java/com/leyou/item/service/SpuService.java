package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Spu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Leslie Arnoald
 */
public interface SpuService extends IService<Spu> {
    PageDTO<SpuDTO> querySpuByPage(Integer page, Integer rows, Boolean saleable, Long categoryId, Long brandId, Long id);

    void saveGoods(SpuDTO spuDTO);

    void updateSaleable(Long spuId, Boolean saleable);

    SpuDTO queryGoodsById(Long spuId);

    void updateGoods(SpuDTO spuDTO);

    List<SpecParamDTO> queySpecValue(Long spuId, Boolean searching);

}