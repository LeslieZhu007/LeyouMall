package com.leyou.item.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Leslie Arnoald
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_category")
public class Category extends BaseEntity {

    @TableId
    private Long id;
    private String name;
    private Long parentId;
    private Boolean isParent;
    private Integer sort;
}