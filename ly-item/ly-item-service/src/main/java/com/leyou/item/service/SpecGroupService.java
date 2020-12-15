package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;

import java.util.List;

/**
 * @author Leslie Arnoald
 */
public interface SpecGroupService extends IService<SpecGroup> {
    List<SpecGroupDTO> queryGroupByCategoryId(Long categoryId);


    List<SpecGroupDTO> querySpecList(Long categoryId);
}