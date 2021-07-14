package com.leyou.auth.jwt;

import com.leyou.auth.dto.Payload;
import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.utils.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/23 15:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void testJwt() throws InterruptedException {

        //UserDetail的静态方法  @AllArgsConstructor(staticName = "of")
        String jwt = jwtUtils.createJwt(UserDetails.of(110L, "Jack"), 2000);
        //String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJmOGNlNDdmZDA2MmQ0ZTcyOWU5NjY5ZGM2MDU4OTRkMSIsInVzZXIiOiJ7XCJpZFwiOjExMCxcInVzZXJuYW1lXCI6XCJKYWNrXCJ9IiwiZXhwIjoxNjA4ODExODE4fQ.EGhKlMQelorGl4_G3yYjdcMgskyaGXvxy4FPvrjM1Ws";
        //如果token是假的 会被校验出来 com.leyou.common.exception.LyException: 登录无效或者已经超时！
        //String jwt = "eyJhbGciOiJIUzI1NiJ9.esdyJqdGkiOiJmOGNlNDdmZDA2MmQ0ZTcyOWU5NjY5ZGM2MDU4OTRkMSIsInVzZXIiOiJ7XCJpZFwiOjExMCxcInVzZXJuYW1lXCI6XCJKYWNrXCJ9IiwiZXhwIjoxNjA4ODExODE4fQ.EGhKlMQelorGl4_G3yYjdcMgskyaGXvxy4FPvrjM1Ws";


        System.out.println("jwt = " + jwt);

        //Thread.sleep(2000);
        Payload payload = jwtUtils.parseJwt(jwt);
        System.out.println("payload = " + payload);


    }
}
