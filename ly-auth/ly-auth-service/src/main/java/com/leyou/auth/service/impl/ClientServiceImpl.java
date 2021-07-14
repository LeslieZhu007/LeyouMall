package com.leyou.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.auth.entity.ClientInfo;
import com.leyou.auth.mapper.ClientMapper;
import com.leyou.auth.service.ClientService;
import com.leyou.common.exception.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author 虎哥
 */
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, ClientInfo> implements ClientService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    //读取yml文件中的jwt的key
    @Value("${ly.jwt.key}")
    private String key;


    @Override
    public String getSecretKey(String clientId, String secret) {
        //1.根据clientId查询数据   clientId不是主键，用query
        ClientInfo clientInfo = query().eq("client_id", clientId).one();
        //2.判断是否存在
        if(clientInfo == null) {
            //不存在,
            throw new LyException(400,"客户端信息有误");

        }
        //3.校验secrect是否正确
        boolean matches = passwordEncoder.matches(secret, clientInfo.getSecret());
        if(!matches) {
            throw new LyException(400,"客户端信息有误");
        }


        //4.返回密钥
        return key;
    }
}