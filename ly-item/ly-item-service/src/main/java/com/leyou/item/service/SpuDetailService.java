package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.SpuDetail;

/**
 * @author Leslie Arnoald
 */
public interface SpuDetailService extends IService<SpuDetail> {
    SpuDetailDTO queryDetailBySpuId(Long spuId);
}
