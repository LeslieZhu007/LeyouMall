package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.service.SpecParamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 虎哥
 */
@Service
public class SpecParamServiceImpl extends ServiceImpl<SpecParamMapper, SpecParam> implements SpecParamService {
    @Override
    public List<SpecParamDTO> querySpecParams(Long categoryId, Long groupId, String searching) {
        // 1. Integrity assessment
        // Pagination can also be applied here to solve the illegal request param problem
        if(categoryId==null && groupId==null) {

            //Illegal request param
            throw new LyException(400,"The categoryId and the groupId can't both be null");

        }

        // name numeric options   are   reserved words must use ` `, the escaping symbols
        /*
        * SELECT  id,category_id,group_id,
            `name`,`numeric`,unit,generic,searching,
             segments,`options`,create_time,
             update_time  FROM tb_spec_param
            WHERE (category_id = 76 AND group_id = 1 AND searching = 0)   */

        //2. select * from tb_spec_param where category_id =  1 and group_id = 1 and searching = 1

        //Here,all the params passing in might be null,so we have to check they are null in the eq method
        List<SpecParam> specParams = query().eq(categoryId!=null,"category_id", categoryId)
                .eq(groupId!=null,"group_id", groupId)
                .eq(searching!=null,"searching", searching).list();

        //List<SpecParamDTO> specParamDTOS = specParams.stream().map(SpecParamDTO::new).collect(Collectors.toList());

        //Or a more elegant way of converting a normal list to a DTO list,because the lambda expression is built in the
        //convertEntityList method in the BaseDTO which every DTO has to inherit.
        List<SpecParamDTO> specParamDTOS = SpecParamDTO.convertEntityList(specParams);

        return specParamDTOS;
    }
}