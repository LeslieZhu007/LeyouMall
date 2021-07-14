package com.leyou.auth.service.impl;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.service.UserAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.constants.JwtConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.leyou.common.constants.JwtConstants.COOKIE_NAME;
import static com.leyou.common.constants.JwtConstants.COOKIE_PATH;
import static com.leyou.common.constants.JwtConstants.DOMAIN;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 20:53
 */

@Service
public class UserAuthServiceImpl implements UserAuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void login(String username, String password, HttpServletResponse response) {
        //1.根据用户名和密码查询用户
        //queryByUsernameAndPassword 该方法体本身有可能抛出异常
        UserDTO userDTO = null;
        try {
            userDTO = userClient.queryByUsernameAndPassword(username, password);
        } catch (FeignException e) {
            if (e.status() == 400){
                //用户名或者密码错误
                throw new LyException(e.status(),e.contentUTF8() );

            }
            throw new LyException(500,"验证用户失败！",e );
        }

        //2.对查询结果做判断
        if(userDTO == null) { //401 未登录
            throw new LyException(401,"用户名或者密码错误");        }

        //3.生成jwt    过期时间不指定默认半个小时
        String jwt = jwtUtils.createJwt(UserDetails.of(userDTO.getId(), userDTO.getUsername()));



        //4.写入cookie
        writeJwt2Cookie(response, jwt);

    }

    private void writeJwt2Cookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(COOKIE_NAME, jwt);
        cookie.setPath(COOKIE_PATH); //cookie生效的路径
        //cookie.setMaxAge(); 默认-1
        cookie.setHttpOnly(true); //限制cookie无法被js操作
        cookie.setDomain(DOMAIN);
        response.addCookie(cookie);
    }

    @Override
    public void logout(HttpServletResponse response, HttpServletRequest request) {
        //1.获取cookie中的jwt
        String jwt = CookieUtils.getCookieValue(request, JwtConstants.COOKIE_NAME);
        if(jwt == null) {
            return;
        }
        try {
            //2.解析jwt
            Payload payload = jwtUtils.parseJwt(jwt);
            //3.删除redis
            Long userId = payload.getUserDetail().getId();
            jwtUtils.deleteJwt(userId);


        } catch (Exception e) {
            //解析jwt失败，有可能是未登录，放弃退出

        }

        //4.删除cookie
        //其实是重写cookie
        CookieUtils.deleteCookie(COOKIE_NAME,DOMAIN ,response );
    }
}
