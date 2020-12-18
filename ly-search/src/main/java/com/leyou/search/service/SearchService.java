package com.leyou.search.service;

import com.leyou.search.entity.Goods;
import com.leyou.search.entity.SearchParamDTO;
import com.leyou.starter.elastic.dto.PageInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 10:52
 */
public interface SearchService {

    /**
     * 创建索引库并设置映射
     */
    void createIndexAndMapping();

    /**
     * 加载数据到索引库
     */
    void loadData();

    Mono<List<String>> getSuggestion(String keyPrefix);

    Mono<PageInfo<Goods>> searchGoodsList(SearchParamDTO paramDTO);
}
