package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.service.SpuDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Leslie Arnoald
 */
@Service
public class SpuDetailServiceImpl extends ServiceImpl<SpuDetailMapper, SpuDetail> implements SpuDetailService {
    @Override
    public SpuDetailDTO queryDetailBySpuId(Long spuId) {
        //spuId是主键
        SpuDetail spuDetail = getById(spuId);
        SpuDetailDTO spuDetailDTO = new SpuDetailDTO(spuDetail);
        return spuDetailDTO;
    }
}