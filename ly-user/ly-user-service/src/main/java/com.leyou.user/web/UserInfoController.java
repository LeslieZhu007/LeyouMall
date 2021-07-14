package com.leyou.user.web;

import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.utils.UserHolder;
import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.ResultSet;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/23 19:35
 */
@RestController
@RequestMapping("info")
public class UserInfoController {
    @Autowired
    private UserService userService;

    /**
     * 校验数据是否存在
     *
     * @param data  需要校验的数据
     * @param type  校验数据的类型,1.验证码 2.手机号
     * @return   true,false
     */

    @GetMapping("/exists/{data}/{type}")
    public ResponseEntity<Boolean> isDataExists(@PathVariable("data") String data,
                                                @PathVariable("type") Integer type) {

        return ResponseEntity.ok(userService.isDataExists(data,type));

    }

    /**
     * 发送短信啊验证码
     * @param phone 手机号码
     * @return 无
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {

        userService.sendVerifyCode(phone);

        return ResponseEntity.noContent().build();

    }


    /**
     * 用户注册
     * @param user 用户信息
     * @param code 验证码
     * @return 无
     */
    @PostMapping
    public ResponseEntity<Void> register(@Valid User user, BindingResult result, @RequestParam("code")String code) {
        //判断校验结果
        if(result.hasErrors()) {
            //参数有误
            throw new LyException(400,"请求参数有误!");
        }


        userService.register(user,code);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    @GetMapping
    public ResponseEntity<UserDTO> queryByUsernameAndPassword(
            @RequestParam("username")String username,
            @RequestParam("password") String password) {
                return ResponseEntity.ok(userService.queryByUsernameAndPassword(username,password));
            }




            /*//获取jwt

        //验证并解析jwt

        //获取payload

        // 获取用户

        //返回

            以上这些工作在interceptor中已经做过
       */

            /*
            * 将interceptor中的信息取出，存放在一个地方，这样
            * UserInfoController中就可以直接取出*/
    @GetMapping("/me")
    public ResponseEntity<UserDetails> whoAmI(){

        return ResponseEntity.ok(UserHolder.getUser());

            }

}
