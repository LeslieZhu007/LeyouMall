package com.leyou.page.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 虎哥
 */
@Data
public class SpuVO {
    private Long id;
    private String name;
    private List<Long> categoryIds;
    private Long brandId;
}