package com.leyou.page.handler;

import com.leyou.page.entity.Sku;
import com.leyou.page.service.GoodsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/22 12:49
 */
@CanalTable("tb_sku")
@Component
public class SkuHandler implements EntryHandler<Sku> {
    @Autowired
    private GoodsPageService goodsPageService;

    @Override
    public void insert(Sku sku) {
        goodsPageService.loadSkuListData(sku.getSpuId());
    }

    @Override
    public void update(Sku before, Sku after) {
        goodsPageService.loadSkuListData(after.getSpuId());

    }

    @Override
    public void delete(Sku sku) {
        goodsPageService.deleteSku(sku.getSpuId());

    }
}
