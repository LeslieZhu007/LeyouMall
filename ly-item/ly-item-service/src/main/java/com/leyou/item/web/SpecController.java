package com.leyou.item.web;

import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.service.SpecGroupService;
import com.leyou.item.service.SpecParamService;
import com.leyou.item.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author Leslie Arnoald
 */
@RestController
@RequestMapping("spec")
public class SpecController {

    private SpecGroupService groupService;
    private SpecParamService paramService;


    public SpecController(SpecGroupService groupService, SpecParamService paramService) {
        this.groupService = groupService;
        this.paramService = paramService;
    }


    /**
     * Query specification groups by category_id
     * @param CategoryId
     * @return Collections of the specification groups
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> queryGroupByCategoryId(@RequestParam("id")Long CategoryId){
        List<SpecGroupDTO> list = groupService.queryGroupByCategoryId(CategoryId);

        return ResponseEntity.ok(list);
    }

    /**
     * Query the specification params
     * @param categoryId
     * @param groupId   specification group id
     * @param searching Whether participate in searching or not
     * @return a collection of the params
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParams(
            //required = false 不能少，否则报错，查不出数据
            @RequestParam(value = "categoryId",required = false) Long categoryId,
            @RequestParam(value = "groupId",required = false)Long groupId,
            @RequestParam(value = "searching",required = false)Boolean searching
    ) {

        List<SpecParamDTO> list = paramService.querySpecParams(categoryId, groupId, searching);

        return ResponseEntity.ok(list);
    }





    @GetMapping("list")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroups(@RequestParam("id") Long categoryId) {

        List<SpecGroupDTO> list = groupService.querySpecList(categoryId);

        return ResponseEntity.ok(list);

    }
}