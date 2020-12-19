package com.leyou.search.web;

import com.leyou.search.entity.Goods;
import com.leyou.search.entity.SearchParamDTO;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.dto.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 10:58
 */

@RestController
@RequestMapping("goods")
public class SearchController {

    @Autowired
    SearchService searchService;


    @GetMapping("init")
    public ResponseEntity<String> init() {
        //初始化索引库
        searchService.createIndexAndMapping();
        //加载数据
        searchService.loadData();

        return ResponseEntity.ok("初始化索引库成功");
    }



    //Request URL: http://api.leyou.com/search/goods/suggestion?key=x
    //请求路径  api.leyou.com/search/goods/suggestion  网关中消除了前缀
    // 所以路径就是 goods/suggestion
    //谷歌浏览器中查询到 /goods/suggestion  对应的ajax请求，得知
    //返结果与options绑定，options是一个String数组

    /**
     * 关键字自动补全
     * @param keyPrefix
     * @return
     */
    @GetMapping("/suggestion")
    public Mono<List<String>> getSuggestion(@RequestParam("key")String keyPrefix) {

        return searchService.getSuggestion(keyPrefix);

    }


    /*axios.post("/search/goods/list", this.params)
          .then(resp => {
            // 分页数据
            this.total = resp.data.total;
            // 计算总页数
            this.totalPage = Math.floor((this.total + 19) / 20);
            // 商品
            this.goodsList = resp.data.content;
          })
          .catch(e => console.log(e))*/


    //先前封装的工具类中就有
    /*public class PageInfo<T> {
      private long total;
      private List<T> content;

      这与前端数据相对应

      */

    //综合，销量，新品都是默认降序的，无法修改



    // request payload  请求参数
    /*{page: 1, key: " 红米5A", filters: {}, sortBy: "", desc: true}
        desc: true      升序，降序
        filters: {}    条件筛选
        key: " 红米5A"
        page: 1        分页
        sortBy: ""


        */

    /**
     * 搜索商品列表
     * @param paramDTO 请求参数的form表单
     * @return 分页的商品数据
     */
    @PostMapping("/list")

    public Mono<PageInfo<Goods>> searchGoodsList(@RequestBody SearchParamDTO paramDTO) {
        return searchService.searchGoodsList(paramDTO);
    }

}


