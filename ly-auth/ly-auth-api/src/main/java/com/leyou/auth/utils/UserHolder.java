package com.leyou.auth.utils;

import com.leyou.auth.dto.UserDetails;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 14:23
 */

/*
 * 进入ly-trade的一切请求都会先经过
 * LoginInterceptor这个拦截器，拦截器中
 * 已经解析出jwt,jwt就是浏览器Cookie中
 * 的内容，所以，在拿到Jwt之后将其保存起来
 * 保存到UserHolder上下文中
 * */
public class UserHolder {

    /*
      此处采用装饰模式：将想使用的对象放到类的内部
    *
    *
    * */

    /*
    * 存储用户
    * */
    private static final ThreadLocal<UserDetails> tl = new ThreadLocal<>();
    /**
     * 存储JWT
     */
    private static final ThreadLocal<String> tl2 = new ThreadLocal<>();
    //同样要写3个方法

    public static void setJwt(String jwt) {

        tl2.set(jwt);
    }

    public static String getJwt() {

        return tl2.get();

    }

    public static void removeJwt() {

        tl2.remove();
    }





    public static void setUser(UserDetails user) {
        tl.set(user);
    }

    public static UserDetails getUser() {
        return tl.get();
    }

    public static void removeUser() {
       tl.remove();
    }


}
