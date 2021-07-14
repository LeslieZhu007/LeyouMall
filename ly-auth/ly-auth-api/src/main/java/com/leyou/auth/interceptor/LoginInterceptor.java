package com.leyou.auth.interceptor;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.UserHolder;
import com.leyou.common.constants.JwtConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/24 10:49
 */
@Slf4j
//不注入Spring中

/*进入ly-trade的一切请求都会先经过
 * LoginInterceptor这个拦截器，拦截器中
 * 已经解析出jwt,jwt就是浏览器Cookie中
 * 的内容，所以，在拿到Jwt之后将其保存起来
 * 保存到UserHolder上下文中
 * */
public class LoginInterceptor implements HandlerInterceptor {

    private JwtUtils jwtUtils;

    //此处只能用构造函数注入
    //因为当前 Bean   LoginInterceptor 没有交给Spring管理，除非加@Component,但是这样做没有意义
    public LoginInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override //请求进入handler之前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取cookie中的jwt
        String jwt = CookieUtils.getCookieValue(request, JwtConstants.COOKIE_NAME );
        //2.判断是否存在
        if (jwt == null) {
            //没有cookie，未登录
            //HTTP401错误代表用户没有访问权限，需要进行身份认证
            throw new LyException(401,"用户未登录");
        }
        //3.校验jwt  在parseJwt内部已经有校验
        Payload payload = jwtUtils.parseJwt(jwt);
        //4. 存储用户信息
        UserHolder.setUser(payload.getUserDetail());
        UserHolder.setJwt(jwt);
        // 4.如果正确，则放行
        log.debug("用户{}正在访问路径:{}",payload.getUserDetail().getUsername(),request.getRequestURI());






        //4.如果正确则放行

        return true;
    }

    @Override //controller方法执行之后，视图渲染之前
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override //视图渲染之后，返回给用户之前执行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeJwt();

        UserHolder.removeUser();

    }
}
