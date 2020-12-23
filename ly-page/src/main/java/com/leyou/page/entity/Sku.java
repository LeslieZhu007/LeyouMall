package com.leyou.page.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/22 12:41
 */
@Data
@Table(name = "tb_sku")
public class Sku {

    @Id
    private Long id;

    @Column(name = "spu_id")
    private Long spuId;

    private String title;

    private String images;
}
