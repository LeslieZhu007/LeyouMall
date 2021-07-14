package com.leyou.auth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetails;

import com.leyou.common.exception.LyException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 虎哥
 */

//JwtUtils中的所有方法都不是静态的
public class JwtUtils {

    /**
     * JWT解析器
     */
    private final JwtParser jwtParser;
    /**
     * 秘钥
     */
    private final SecretKey secretKey;

    private final static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final static String KEY_PREFIX = "auth:uid:";

    public JwtUtils(String key) {
        // 生成秘钥
        secretKey = Keys.hmacShaKeyFor(key.getBytes(Charset.forName("UTF-8")));
        // JWT解析器
        this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    /**
     * 生成jwt，用默认的JTI
     *
     * @param userDetails 用户信息
     * @return JWT
     */
    public String createJwt(UserDetails userDetails) {
        return createJwt(userDetails, 1800);
    }

    /**
     * 生成jwt，自己指定的过期时间
     *
     * @param userDetails 用户信息
     * @return JWT
     */
    public String createJwt(UserDetails userDetails, int expireSeconds) {
        try {
            //1.生成jti
            String jti = createJti();
            // 生成token
            String jwt = Jwts.builder().signWith(secretKey)
                    .setId(jti)
                    .claim("user", mapper.writeValueAsString(userDetails))
                    //.setExpiration(DateTime.now().plusSeconds(expireSeconds).toDate())
                    .compact();
            //把jti存入redis
            redisTemplate.opsForValue().set(KEY_PREFIX + userDetails.getId(),jti ,expireSeconds , TimeUnit.SECONDS );
            return jwt;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证并解析jwt，返回包含用户信息的载荷
     *
     * @param jwt   token
     * @return 载荷，包含JTI和用户信息
     */
    public Payload parseJwt(String jwt) {
        try {
            //1.验证jwt
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(jwt);
            Claims claims = claimsJws.getBody();

            Payload payload = new Payload();
            payload.setJti(claims.getId());
            payload.setUserDetail(mapper.readValue(claims.get("user", String.class), UserDetails.class));
            //2.验证redis中的jti与当前jti是否一致
            //2.1 从redis当中取出jti
            String cacheJTI = redisTemplate.opsForValue().get(KEY_PREFIX + payload.getUserDetail().getId());

            //2.2 比较token中的jti
            if(!StringUtils.equals(payload.getJti(),cacheJTI )) {
                //token的JTI不一致
                throw new LyException(401,"您的账号已在别处登录或被禁止登录" );
            }
            //3.返回载荷
            return payload;
        }catch (IllegalArgumentException e) {
            throw new LyException(401, "用户未登录！");
        }catch (JwtException e) {
            throw new LyException(401, "登录无效或者已经超时！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createJti() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    /**
     * 刷新token
     * @param userId 用户id
     */
    public void refreshJwt(Long userId) {
        //用expire进行刷新
        redisTemplate.expire(KEY_PREFIX + userId,1800 ,TimeUnit.SECONDS );

    }

    public void refreshJwt(Long userId,Long expireSeconds) {
        //用expire进行刷新
        redisTemplate.expire(KEY_PREFIX + userId,expireSeconds ,TimeUnit.SECONDS );

    }


    public void deleteJwt(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}