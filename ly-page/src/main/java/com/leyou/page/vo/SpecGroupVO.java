package com.leyou.page.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 虎哥
 */
@Data
public class SpecGroupVO {
    private String name;
    private List<SpecParamVO> params;
}