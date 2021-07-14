package com.leyou.gateway.filters;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.constants.JwtConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/25 22:55
 */
@Slf4j
@Order //默认最低级别，因为过滤器不止这一个
@Component
public class TokenRefreshFilter implements GlobalFilter {

    @Autowired

    private JwtUtils jwtUtils;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            //1.获取Request
            ServerHttpRequest request = exchange.getRequest();

            //2.获取jwt

            //2.1获取所有的cookie
            MultiValueMap<String, HttpCookie> cookies = request.getCookies();

            //2.2 取出所需要的cookie
            HttpCookie cookie = cookies.getFirst(JwtConstants.COOKIE_NAME);
            if(cookie == null) {
                //说明没有cookie，未登录，无需刷新，直接放行
                return chain.filter(exchange);
            }
            //2.3获取cookie中的jwt
            String jwt = cookie.getValue();

            //3 解析jwt
            Payload payload = jwtUtils.parseJwt(jwt);
            UserDetails userDetail = payload.getUserDetail();

            //4.//刷新jwt
            //将刷新的动作放到jwtutils中完成
            jwtUtils.refreshJwt(userDetail.getId());

        } catch (Exception e) {
            log.error("刷新token失败",e );
        }


        //5.无论如何一定放行
        return chain.filter(exchange);
    }
}
