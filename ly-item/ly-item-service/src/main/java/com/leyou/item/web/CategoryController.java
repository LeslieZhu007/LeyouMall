package com.leyou.item.web;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.service.CategoryService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.util.resources.cldr.ti.CalendarData_ti_ET;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> queryCategoryById(@PathVariable("id") Long id){
        //根据id查询
        Category category = categoryService.getById(id);

        //转DTO
        CategoryDTO categoryDTO = new CategoryDTO(category);

        //返回结果 返回200
        //return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);
        //return ResponseEntity.ok().body(categoryDTO);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> queryCatagoryByIds(@RequestParam("ids")List<Long> ids) {
        //根据id批量查询
        List<Category> list = categoryService.listByIds(ids);


        List<CategoryDTO> dtoList = CategoryDTO.convertEntityList(list);


        //转换DTO
        // map的参数 是 Fucntion接口   Function<Category,CategoryDTO> f = category -> new CategoryDTO(category)
        /*List<CategoryDTO> dtoList = list //List<Category>
                .stream()//Stream<Category>
                //.map(category -> new CategoryDTO(category)) //Stream<CategoryDTO>
                .map(CategoryDTO::new)
                .collect(Collectors.toList());*/ //List<CategoryDTO>






       /* ArrayList<CategoryDTO> dtoList = new ArrayList<>(list.size());
        for (Category category : list) {
           dtoList.add(new CategoryDTO(category));
        }*/

       //jdk 1.8中的lambda表达式其实
        //java中函数无法作为参数传递，当我们需要传递一段行为时，只能十八函数
        //封装到一个接口的匿名内部类中去传递
        // JDK 1.8中的lambda表达式其实是对匿名内部类的一种简写形式
        /*
        new Thread(()->System.out.println()).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println();
            }
        }).start();
        */

        //lambda表达式是接口的匿名内部类的简写形式，但是如果每次使用
        //lambda都需要先定义接口，这样太麻烦。
        //JDK把这些常见的函数行为概况为4大类，并且提前定义好了这些接口
        //因此，以后在使用lambda表达式的时候，就无需先定义接口，这4类分别是：
        //1.有参有返回值： Function
        //2.有参无返回值  Consumer
        //3.无参有返回值  Supplier
        //4.无参无返回值  Runnable
        //5. 有参有返回值为布尔的  Predicate

        //java种的某些类种的函数，本身就符合函数式接口的定义，


        //方法引用的规则有4种情况
        //方法引用的基本语法是 【方法持有者】::【方法名称】
        // - 类的静态方法： 类名::方法名
        // - 类的非静态方法： 对象名::方法名
        // - 类的非静态方法： 类名::方法名  前提：对象方法的调用对象是流中的元素
        //- 类的构造函数 类名::new

        //  - 类的非静态方法： 类名::方法名举例：
        /*List<String> list2 = Arrays.asList("hello","list");
        list2.stream().map(str->str.toUpperCase());
        list2.stream().map(String::toUpperCase);
        list2.stream().map(str->str.parseInteger());*/





        return ResponseEntity.ok(dtoList);






    }


    /*
    * # 方式1：两次单表查询
# 根据brand id查询中间表
select * from tb_category_brand where brand_id = 18374

# 根据categoryId去查询分类
select * from tb_category where id in (76,84,86)

# 方式2 子查询
select * from tb_category where id in (select category_id from tb_category_brand where brand_id = 18374)

# 方式3 连接查询
select c.*
from tb_category_brand cb
inner join tb_category c
on c.id = cb.category_id
where brand_id = 18374
    *
    *
    *# 一般来说，能用连接查询就不用子查询，
     # 多次单表查询优于一次长查询
    *
    *
    * */

    /**
     *
     * @param parentId
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO> > queryCategoryByParentId(@RequestParam("pid") Long parentId){
        //select * from tb_category where parent_id = 0
        //非主键，不能用 getById 来查询
        List<Category> list = categoryService.query().eq("parent_id", parentId).list();
        //转DTO
        List<CategoryDTO> dtoList = CategoryDTO.convertEntityList(list);
        //返回200
        return ResponseEntity.ok(dtoList);
    }


    /**
     * 根据品牌id查询分类
     * @param brandId 品牌id
     * @return 分类集合
     */
    @GetMapping("/of/brand")
    public ResponseEntity<List<CategoryDTO> > queryCategoryByBrandId(@RequestParam("id")Long brandId){
        List<CategoryDTO> dtoList = categoryService.queryCategoryByBrandId(brandId);

        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("listStreamTest")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByIdsStream(@RequestParam("ids") List<Long> ids) {
        //根据id批量查询
        List<Category> categoryList = categoryService.listByIds(ids);
        //转换DTO
        //List<CategoryDTO> categoryDTOList = CategoryDTO.convertEntityList(categoryList);
        //1.Stream流改写
        List<CategoryDTO> categoryDTOList = categoryList //List<Category>
                .stream() //Stream<Category>
                //.map(category -> new CategoryDTO(category)) //Stream<CategoryDTO>
                //2.方法引用代替lambda表达式  Java中不支持函数作为参数传递
                //既然Java中没有办法传递函数，就将函数封装到对象中，传一个对象
                //过去，等于传递了函数。
                //CategoryDTO 的构造函数本身就是有参有返回值，本身也可也作为Function来使用
                .map(CategoryDTO::new)
                /*.map(new Function<Category, CategoryDTO>() {
                    @Override
                    public CategoryDTO apply(Category category) {
                        return new CategoryDTO(category);
                    }
                })*/
                .collect(Collectors.toList()); //List<CategoryDTO>

        //此处map的参数就是一个Function接口
        /*Function f = new Function<Category, CategoryDTO>() {
            @Override
            public CategoryDTO apply(Category category) {
                return new CategoryDTO(category);
            }
        };*/

        //即： lambda本质就是一个接口的实例，即接口的匿名内部类
        // map(f)
        //Function<Category, CategoryDTO> f_lambda = category -> new CategoryDTO(category);
        //或
        //Function f_lambda1 = (Function<Category, CategoryDTO>) category -> new CategoryDTO(category);


        /*
        JDK 1.8中的lambda表达式其实是对匿名内部类的一种简写形式,
        java中无法将函数作为参数传递，只能把参数封装到一个接口的匿名
        内部类中去传递
        */
        /*new Thread(()-> System.out.println()).run();
        new Thread(System.out::println).run();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println();
            }
        }).start();*/
        //由于每次使用lambda都要先定义接口，太麻烦，JDK把常见的函数行为
        //概况为4大类，并且提前定义好了这些接口，因此以后再使用lambda表达式的时候，就
        //无需再定义接口。这四类分别是:
        //1.有参有返回值  Function
        //2.有参无返回值  Consumer
        //3.无参有返回值  Supplier
        //4.有参无返回值  Runnable
        //5.有参返回值为布尔 Predicate


        //lambda表达式
        /*categoryList.forEach(new Consumer<Category>() {
            @Override
            public void accept(Category category) {
                System.out.println(category);
            }
        });

        categoryList.forEach(category -> System.out.println(category));
        //println函数本身也是有参无返回值，因此可以使用方法引用
        categoryList.forEach(System.out::println);
*/
        //Java中的某些函数本身就符合函数式接口的定义，因此我们希望直接把这些函数作为参数
        //传递，这个时候就可以用到方法引用，目的是把某个类的某个方法作为函数传递


        //方法引用的基本语法是：【方法的持有者】::[方法名称]。 其规则有4种情况
        // - 类的静态方法： 类名::方法名
        // - 类的非静态方法:  对象名::方法名
        // - 类的非静态方法: 类名::方法名,前提是方法的调用对象是流中的元素
        // - 类的构造函数: 类名::new

        List<String> list2 = Arrays.asList("hello","world");
        //toUpperCase是无参有返回值，不符合Function接口要求，但
        // s -> s.toUpperCase() 这种写法符合函数式接口要求
        // lambda中的参数是流中的元素，toUpperCase是非静态方法
        list2.stream().map(s -> s.toUpperCase());
        list2.stream().map(String::toUpperCase); //流中的元素作为方法的调用者

        list2.stream().map(str->Integer.parseInt(str));
        list2.stream().map(Integer::parseInt); //流中的元素作为方法的参数


        //返回200
        /*for (Category category : categoryList) {
            CategoryDTO categoryDTO = new CategoryDTO(category);
            categoryDTOList.add(categoryDTO);
        }*/

        return ResponseEntity.ok(categoryDTOList);
    }

}
