package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 20:24
 */

//参见Feiqgn的最佳实践
//将以用户名和密码查询用户的方法通过Feign暴露出去
@FeignClient("user-service")
public interface UserClient {

    @GetMapping("info")
    UserDTO queryByUsernameAndPassword( @RequestParam("username")String username,
                                        @RequestParam("password") String password);


    /**
     * 根据id查询地址
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/address/{id}")
    AddressDTO queryAddressById(@PathVariable("id") Long id);


}