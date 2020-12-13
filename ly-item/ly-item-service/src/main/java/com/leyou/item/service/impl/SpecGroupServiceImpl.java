package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Leslie Arnoald
 */
@Service
public class SpecGroupServiceImpl extends ServiceImpl<SpecGroupMapper, SpecGroup> implements SpecGroupService {

    @Autowired
    SpecParamService paramService;



    @Override
    public List<SpecGroupDTO> queryGroupByCategoryId(Long categoryId) {
        //this.query() can be simplified as query() since this can be elided here
        // Tips: Here,this represents an instance of the class SpecGroupServiceImpl
        //since Java has the polymorphism characteristic,we can assume here ,the instance is
        //in fact SpecGroupService's.Thus:
        // SpecGroupService specGroupService = new SpecGroupServiceImpl();

        //Query using category_id
        List<SpecGroup> list = query().eq("category_id", categoryId).list();

        //Converting into DTO
        List<SpecGroupDTO> specGroupDTOS = SpecGroupDTO.convertEntityList(list);

        return specGroupDTOS;
    }

    @Override
    public List<SpecGroupDTO> querySpecList(Long categoryId) {
        //1. Query the SpecGroup
        List<SpecGroup> specGroups = query().eq("category_id", categoryId).list();

        List<SpecGroupDTO> specGroupDTOS = SpecGroupDTO.convertEntityList(specGroups);
        // Determine where the specGroupDTOS is null
        if(CollectionUtils.isEmpty(specGroupDTOS)) {
            throw new LyException(404,"The goods spec group does not exist");
        }

        //Let's make a change to our methodology here
        //2.First,query by categoryId
        List<SpecParamDTO> specParamDTOList = paramService.querySpecParams(categoryId, null, null);
        //Filter the results by groupId,group by groupId and form a new map
        //The key of the map will be groupId,the value would be the corresponding collections of the param

        //3. Group the specParamDTOS by groupId,if their groupIds are identical,they should be in the same group,
        // this group will now be a map,the key will be groupId,value will be a collection of the param

        /*Map<Long,List<SpecParamDTO>> map = new HashMap<>();

        for (SpecParamDTO specParamDTO : specParamDTOList) {
            Long groupId = specParamDTO.getGroupId();
            //Determine if the key exists in the map
            if(!map.containsKey(groupId)) {
                //If the key doesn't exist,a new list should be created within a map
                map.put(groupId,new ArrayList<>());
                //Insert into the list params
                map.get(groupId).add(specParamDTO);
            } else {
                //the key already exists.store it into the list
                map.get(groupId).add(specParamDTO);
            }
        }*/

        //Incorporate the above codes into one Java Stream process

        Map<Long,List<SpecParamDTO>> map = specParamDTOList.stream().collect(Collectors.groupingBy(SpecParamDTO::getGroupId));






        //2.   set spec_params
        for (SpecGroupDTO specGroupDTO : specGroupDTOS) {
            Long groupId = specGroupDTO.getId();
            //It's better not to query sth within a forloop
            //List<SpecParamDTO> specParamDTOS = paramService.querySpecParams(null, groupId, null);
            List<SpecParamDTO> specParamDTOS = map.get(groupId);

            specGroupDTO.setParams(specParamDTOS);

        }
        return specGroupDTOS;
    }


}