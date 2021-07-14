package com.leyou.auth.web;

import com.leyou.auth.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leslie Arnold
 */
@RestController
@RequestMapping("client")
public class ClientAuthController {

    @Autowired
    private ClientService clientService;

    /**
     * 申请jwt密钥
     * @param clientId 客户端id
     * @param secret 客户端密码
     * @return jwt密钥
     */
    @GetMapping("/key")
    public ResponseEntity<String> getSecretKey(@RequestParam("clientId") String clientId, @RequestParam("secret") String secret){
        String key = clientService.getSecretKey(clientId, secret);
        return ResponseEntity.ok(key);
    }
}