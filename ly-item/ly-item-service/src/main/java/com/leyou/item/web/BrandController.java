package com.leyou.item.web;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    private final BrandService brandService;
    //构造函数注入在类的对象初始化的一刻就注入。而autowire在spring bean的后置处理器中注入，
    //autowire可以解决循环依赖的问题
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * 根据id查询品牌
     * @param id 品牌id
     * @return 品牌对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> queryBrandById(@PathVariable("id")Long id){
        return ResponseEntity.ok(new BrandDTO(brandService.getById(id)));
    }

    /**
     * 根据品牌id集合查询品牌集合
     * @param ids id集合
     * @return 品牌集合
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        //1.查询
        List<Brand> brands = brandService.listByIds(ids);
        //2.转DTO
        List<BrandDTO> brandDTOS = BrandDTO.convertEntityList(brands);
        //3.返回
        return ResponseEntity.ok(brandDTOS);
    }


    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO> > queyBrandByCategory(@RequestParam("id")Long categoryId){
        List<BrandDTO> brandDTOS = brandService.queryBrandByCategory(categoryId);


        return ResponseEntity.ok(brandDTOS);
    }


    /**
     * 分页查询品牌
     * @param key 搜索关键字
     * @param page 当前页码
     * @param rows 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")

    public ResponseEntity<PageDTO<BrandDTO>> queryBrandByPage
            (@RequestParam(value = "key",required = false) String key,
             @RequestParam(value = "page",defaultValue = "1") Integer page,
             @RequestParam(value = "rows",defaultValue = "5") Integer rows)
    {
        return ResponseEntity.ok(brandService.queryBrandByPage(key,page,rows));
    }


    /**
     * 新增品牌
     * @param brandDTO
     * @return
     */
    @PostMapping
    //此处参数前面不能加任何参数
    public ResponseEntity<Void> saveBrand(BrandDTO brandDTO) {
        brandService.saveBrand(brandDTO);

        //新增成功，无返回值，返回201
        return ResponseEntity.status(HttpStatus.CREATED).build(); //body(null)也可也
    }

}