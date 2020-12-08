package com.leyou.gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hystrix")
public class FallbackController {

    @RequestMapping("/fallback")
    //ResponseEntity包含
    public ResponseEntity<Map<String, Object>> handleFallback() {

        Map<String, Object> result = new HashMap<>();
        result.put("code",504 );
        result.put("msg","Service handling timeout,please try again later" );
        //返回响应状态码及响应体
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(result);
    }


}
