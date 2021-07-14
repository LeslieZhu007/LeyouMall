package com.leyou.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 20:53
 */
public interface UserAuthService {
    void login(String username, String password, HttpServletResponse response);

    void logout(HttpServletResponse response, HttpServletRequest request);
}
