package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;

public interface UserService extends IService<User> {
    Boolean isDataExists(String data, Integer type);

    void sendVerifyCode(String phone);

    void register(User user, String code);

    UserDTO queryByUsernameAndPassword(String username, String password);
}