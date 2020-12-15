package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.service.SkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Leslie Arnoald
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {
    @Override
    public List<SkuDTO> querySkuBySpuId(Long spuId) {

        // 不能用此Sku sku = getById(spuId);查，因为 spuId不是主键
        //select * from tb_sku where spu_id=#{}

        List<Sku> skuList = query().eq("spu_id", spuId).list();

        List<SkuDTO> skuDTOList = SkuDTO.convertEntityList(skuList);

        return skuDTOList;


    }
}
