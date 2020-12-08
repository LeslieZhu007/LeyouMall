package com.leyou.item.web;

import com.leyou.item.entity.Item;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 虎哥
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<Item> saveItem(Item item){
        //try {
            Item result = itemService.saveItem(item);
            // 新增成功, 返回201
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        /*} catch (Exception e) {
            // 失败，返回500
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }*/
    }
}