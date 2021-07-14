package com.leyou.auth.web;

import com.leyou.auth.service.UserAuthService;
import com.leyou.user.client.UserClient;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 20:49
 */

@RestController
@RequestMapping("user")
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;


    /**
     * 用户登录功能
     * @param username
     * @param password
     * @param response 响应 用来操作cookie
     * @return 无
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletResponse response
    ) {

        userAuthService.login(username, password,response);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        userAuthService.logout(response,request);

        return ResponseEntity.noContent().build();
    }
}
