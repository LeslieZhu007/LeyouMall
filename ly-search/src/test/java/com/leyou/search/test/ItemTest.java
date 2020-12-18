package com.leyou.search.test;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.BrandDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/17 12:01
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemTest {

    @Autowired
    private ItemClient itemClient;



    @Test

    public  void testFind() {

        //feign.FeignException$NotFound:
        // status 404 reading ItemClient#queryBrandById(Long)
        //此方法没有写
        BrandDTO brandDTO = itemClient.queryBrandById(18374L);
        System.out.println("brandDTO = " + brandDTO);
    }





}
