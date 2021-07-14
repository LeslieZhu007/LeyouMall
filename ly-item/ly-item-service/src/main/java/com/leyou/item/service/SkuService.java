package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Leslie Arnoald
 */
public interface SkuService extends IService<Sku> {
    List<SkuDTO> querySkuBySpuId(Long spuId);

    void deductStock(Map<Long, Integer> skuMap);
}