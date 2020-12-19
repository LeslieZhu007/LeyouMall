package com.leyou.search.service.impl;

import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.entity.SearchParamDTO;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.leyou.starter.elastic.dto.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.leyou.search.constants.SearchConstants.*;
import static java.lang.Double.parseDouble;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 10:53
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsRepository repository;

    @Autowired
    private ItemClient itemClient;


    @Override
    public void createIndexAndMapping() {
        //尝试删除索引库

        try {
            log.info("尝试删除存在的索引库");
            repository.deleteIndex();

        } catch (Exception e) {
            log.warn("索引库不存在，无需删除！");

        }

        //创建索引库和映射
        log.info("尝试创建索引库");
        repository.createIndex(SOURCE_TEMPLATE);


    }

    @Override
    public void loadData() {
        int page = 1,rows = 100;
        while (true) {
            log.info("开始导入第{}页数据",page);
            //1.查询商品spu
            PageDTO<SpuDTO> info = itemClient.querySpuByPage(page, rows, true, null, null, null);
            List<SpuDTO> spuDTOList = info.getItems();

            //2.把spu数据封装到goods
            /*List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : spuDTOList) {

                Goods goods = buildGoods(spuDTO);
                goodsList.add(goods);
            }
*/
            //以上以stream流改写
            //List<Goods> goodsList = spuDTOList.stream().map(spuDTO -> buildGoods(spuDTO)).collect(Collectors.toList());
            //改为方法引用
            //buildGoods是本类持有的方法
            List<Goods> goodsList = spuDTOList.stream().map(this::buildGoods).collect(Collectors.toList());


            //3.写入elasticsearch
            repository.saveAll(goodsList);
            log.info("导入第{}页数据成功",page);

            if (page>=info.getTotalPage()) {
                //已经是最后一页
                break;
            }

            //4.翻页
            page++;
        }
        log.info("全部数据导入成功，共{}页",page);

    }

    private Goods buildGoods(SpuDTO spuDTO) {
        //spuDTO.toEntity(Goods.class)  toEntity 不能全部拷贝只能拷一部分

        //1.商品标题，原有标题基础上，拼接分类名称，品牌名称
        String title = spuDTO.getTitle() + spuDTO.getBrandName() + spuDTO.getCategoryName();

        //2.suggestion：自动补全字段，包含：商品名称，分类名称，品牌名称
        Set<String> suggestion = new HashSet<>();
        suggestion.add(spuDTO.getName());
        suggestion.add(spuDTO.getBrandName());
        //分类名称是用3个/拼接起来的
        Collections.addAll(suggestion,spuDTO.getCategoryName().split("/"));



        //3.规格参数specs,List,其中list<Map<String,Object>> key是name和value,
        //name的值是规格参数的名称，value是规格参数的值
        /*
        * 例如：
        * "specs":[
        *      {name:"屏幕尺寸",value:"5.9"},
        *      {name:"运行内存",value:"5.9"},
        *      {name:"电池容量",value:"5.9"},
        * ]*/


        /*
        * 格式化:
        * [
         *      {
         *      name:"屏幕尺寸",
         *      value:"5.9"
         *      },
         *      {
         *      name:"运行内存",
         *      value:"5.9"
         *      },
         *      {
         *      name:"电池容量",
         *      value:"5.9"
         *      },
         * ]
        *
        *
        *
        * */
        //3.1先查询出规格参数的name和value
        /*
         参见设计文档中：
         GET根据spuId查询spu的所有规格参数值
         */
        List<SpecParamDTO> params = itemClient.querySpecsValues(spuDTO.getId(), true);
        //3.2 准备specs集合
        List<Map<String,Object>> specs = new ArrayList<>(params.size());
        //3.3 把SpecParamDTO变为一个Map<String,Object>
        for (SpecParamDTO param : params) {
            Map<String,Object> map = new HashMap<>(2);
            map.put("name",param.getName() );
            //判断param是否是数字，是否有范围
            //如果有，取出segements
            //用,切割segements   0-3.5,3.6-4.3,4.4-4.7,4.8-5.5,5.6-5.9,6.0-
            //用-切割某个取键，得到min和max
            //判断value是否在min和max之间
            //如果在，存储这个min-max的范围
            map.put("value",chooseSegment(param));
            specs.add(map);
        }


        //4.sku相关数据，包括： 图片，价格，销量
        List<SkuDTO> skuList = spuDTO.getSkus();
        if(CollectionUtils.isEmpty(skuList)) {
            //查询sku
            skuList = itemClient.querySkuBySpuId(spuDTO.getId());


        }



        //4. sku 相关数据  销量，价格和图片都是sku中的内容
        //4.1 销量
        Long sold = 0L;
        //4.2 价格
        TreeSet<Long> prices = new TreeSet<>();
        for (SkuDTO skuDTO : skuList) {
            sold += skuDTO.getSold();
            prices.add(skuDTO.getPrice());
        }

        //4.3 图片，取一张即可
        //String images = skuList.get(0).getImages();
        //int index = images.indexOf(",");
//        String image = images.substring(0, index);
        String images = StringUtils.substringBefore(skuList.get(0).getImages(), ",");

        //5.封装goods
        Goods goods = new Goods();
        goods.setUpdateTime(new Date());
        goods.setTitle(title);
        goods.setSuggestion(suggestion);
        goods.setSpecs(specs);
        goods.setSold(sold);
        goods.setPrices(prices);
        goods.setImage(images);
        goods.setId(spuDTO.getId());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());
        return goods;
    }


    private Object chooseSegment(SpecParamDTO p) {
        Object value = p.getValue();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        if(!p.getNumeric() || StringUtils.isBlank(p.getSegments()) || value instanceof Collection){
            return value;
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }


    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public Mono<List<String>> getSuggestion(String keyPrefix) {

        /*GET /goods/_search
            {
              "_source": "",
              "suggest": {
                "hehe": {
                  "prefix": "x",
                  "completion": {
                    "field": "suggestion",
                    "skip_duplicates":true,
                    "size":20
                  }
                }
              }
            }*/

        /*SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(new SuggestBuilder()
                    .addSuggestion("hehe", SuggestBuilders.completionSuggestion("suggestion").prefix(keyPrefix)));*/

        //对上述查询的封装
        return repository.suggestBySingleField(SUGGEST_FIELD, keyPrefix);
    }


    @Override
    public Mono<PageInfo<Goods>> searchGoodsList(SearchParamDTO paramDTO) {
        /*GET /goods/_search
        {
          "_source": "{
            "includes": ["id","title","prices","sold","images"]
          },
          "query": {

            "bool": {
              "must": [
                {
                  "match": {
                    "title": "手机"
                  }
                }
              ]
            }
          },
          "from": 0,
          "size": 20,
          "sort": [
            {
              "prices": {
                "order": "desc"
              }
            }
          ],
          "highlight": {
            "fields": {"title": {}}
          }
        }
        */
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.source过滤
        sourceBuilder.fetchSource(INCLUDE_SOURCE, EMPTY_SOURCE);
        //2.query查询
        String key = paramDTO.getKey();
        if(StringUtils.isBlank(key)){
            throw new LyException(404,"没有找打与“”相关产品");
        }
        sourceBuilder.query(QueryBuilders.boolQuery().must(
                QueryBuilders.matchQuery("title",key )
        ));
        //3.sort排序
        //分2种情况 sortBy为空，不为空，为空则按照lucene语言的score原理进行排序
        String sortBy = paramDTO.getSortBy();
        if(StringUtils.isNotBlank(sortBy)) {
            //需要排序
            sourceBuilder.sort(sortBy,paramDTO.getDesc() ? SortOrder.DESC : SortOrder.ASC );

        }
        //4 分页
        sourceBuilder.from(paramDTO.getFrom()).size(paramDTO.getSize());

        //5 高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title")
        .preTags(PRE_TAG).postTags(POST_TAG));
        ;


        return  repository.queryBySourceBuilderForPageHighlight(sourceBuilder);
    }

    @Override
    public void saveItemBySpuId(Long spuId) {

        //查询spu
        SpuDTO spuDTO = itemClient.queryGoodsById(spuId);

        //构建goods
        Goods goods = buildGoods(spuDTO);

        //新增goods
        repository.save(goods);

    }

    @Override
    public void deleteItemBySpuId(Long spuId) {
        repository.deleteById(spuId);
    }
}
