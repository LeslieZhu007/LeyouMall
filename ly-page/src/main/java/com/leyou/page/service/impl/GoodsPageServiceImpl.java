package com.leyou.page.service.impl;

import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.page.service.GoodsPageService;
import com.leyou.page.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/20 9:47
 */

@Service
public class GoodsPageServiceImpl implements GoodsPageService {

    @Autowired
    private ItemClient itemClient;


    @Autowired //StringRedisTemplate继承自 RedisTemplate
    private StringRedisTemplate redisTemplate;

    private static final String SPU_KEY_PREFIX = "page:spu:id:";
    private static final String SKU_KEY_PREFIX = "page:sku:id:";
    private static final String DETAIL_KEY_PREFIX = "page:detail:id:";
    private static final String CATEGORY_KEY_PREFIX = "page:category:id:";
    private static final String BRAND_KEY_PREFIX = "page:brand:id:";
    private static final String SPEC_KEY_PREFIX = "page:spec:id:";



    @Override
    public String querySpuById(Long spuId) {


        //1.远程查询数据 查询spu
        SpuDTO spuDTO = itemClient.querySpuById(spuId);

        //2.将查询数据转换为vo
        SpuVO spuVO = spuDTO.toEntity(SpuVO.class);
        spuVO.setCategoryIds(spuDTO.getCategoryIds());

        //vo转json
        String json = JsonUtils.toJson(spuVO);


        //3.写入redis
        //不指定过期时间，存储时间为永久
        //用cannal监听数据库变化更新redis
        //防止key重复，在key前加前缀 项目名称+业务名称
        //冒号分割，代表多级key  以后改为常量类保存
        //此处还可以指定过期时间，但此处不需要
        redisTemplate.opsForValue().set(SPU_KEY_PREFIX + spuId,json );






        return json;  //返回给nginx,无论nginx去redis还是tomcat查拿到的都是json
    }


    @Override
    public String loadSkuListData(Long spuId) {
        //1.远程查询数据
        List<SkuDTO> skuList = itemClient.querySkuBySpuId(spuId);

        //vo转json
        String json = JsonUtils.toJson(skuList);


        //3.写入redis
        //不指定过期时间，存储时间为永久
        //用cannal监听数据库变化更新redis
        //防止key重复，在key前加前缀 项目名称+业务名称
        //冒号分割，代表多级key  以后改为常量类保存
        redisTemplate.opsForValue().set(SKU_KEY_PREFIX + spuId,json );


        return json;
    }

    @Override
    public String loadSpuDetailData(Long spuId) {

        SpuDetailDTO detail = itemClient.querySpuDetailById(spuId);


        String json = JsonUtils.toJson(detail);

        redisTemplate.opsForValue().set(DETAIL_KEY_PREFIX + spuId, json);
        return json;
    }

    @Override
    public String loadCategoriesData(List<Long> ids) {

        if(ids.size()!=3) {
            throw new LyException(400,"商品分类需要三级id");
        }

        List<CategoryDTO> categoryDTOList = itemClient.queryCategoryByIds(ids);
        List<CategoryVO> list = categoryDTOList.stream().map(categoryDTO -> categoryDTO.toEntity(CategoryVO.class)).collect(Collectors.toList());

        String json = JsonUtils.toJson(list);

        redisTemplate.opsForValue().set(CATEGORY_KEY_PREFIX + ids.get(2),json );
        return json;
    }

    @Override
    public String loadBrandData(Long id) {
        //1.远程查询数据
        BrandDTO brandDTO = itemClient.queryBrandById(id);

        //2.将查询到的数据转化为VO
        BrandVO brandVO = brandDTO.toEntity(BrandVO.class);


        //3.把VO转化为json
        String json = JsonUtils.toJson(brandVO);


        //4.写入redis
        redisTemplate.opsForValue().set(BRAND_KEY_PREFIX + id,json );

        //5. 返回
        return json;

    }

    //接口文档   规格管理------->                                                                                                     根据分类id查询规格组及组内参数
    @Override
    public String loadSpecData(Long categoryId) {

        //1.远程查询数据
        List<SpecGroupDTO> groupDTOList = itemClient.querySpecList(categoryId);

        //两层转换     SpecGroupDTO  中包含  SpecParamDTO
        // groupDTO--->GroupVO     ParamDTO-->ParamVO
        //2.将查询到的数据转换为VO
        ArrayList<SpecGroupVO> groupVOList = new ArrayList<>(groupDTOList.size());
        for (SpecGroupDTO groupDTO : groupDTOList) {
            //2.1 groupDTO转为groupVO
            SpecGroupVO groupVO = new SpecGroupVO();
            groupVOList.add(groupVO);

            //2.2 groupVO设置name
            groupVO.setName(groupDTO.getName());

            //2.2.1 groupVO设置params
            List<SpecParamDTO> params = groupDTO.getParams();

            //2.3 paramDTO转VO
            List<SpecParamVO> paramVOList = params.stream().map(paramDTO -> paramDTO.toEntity(SpecParamVO.class)).collect(Collectors.toList());

            groupVO.setParams(paramVOList);

        }

        //3.vo转换为json
        String json = JsonUtils.toJson(groupVOList);

        //4.写入redis
        redisTemplate.opsForValue().set(SPEC_KEY_PREFIX + categoryId,json );
        return json;
    }

    @Override
    public void deleteSku(Long spuId) {
        redisTemplate.delete(SKU_KEY_PREFIX + spuId);
    }
}
