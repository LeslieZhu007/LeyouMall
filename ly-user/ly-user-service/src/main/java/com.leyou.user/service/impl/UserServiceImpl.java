package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.chrono.IslamicChronology;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.MQConstants.ExchangeConstants.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKeyConstants.VERIFY_CODE_KEY;

/**
 * @author Leslie Arnold
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //ctrl+shift+u 切换大小写
    private static final String KEY_PREFIX = "user:verify:phone:";








    @Override
    public Boolean isDataExists(String data, Integer type) {

        //1.健壮性判断
        if (type!=1 && type !=2) {
            //参数有误
            throw new LyException(400,"数据类型错误");
        }

        //查询是否存在就是去数据库中查询是否有此数据
        //对应sql语句
        //select count(*) from tb_user where username = #{data}
        Integer count = query().eq(type == 1, "username", data)
                .eq(type == 2, "phone", data).count();


        //判断是否存在
        return count == 1;
    }


    @Override
    public void sendVerifyCode(String phone) {
        //1.校验手机号格式
        if (!RegexUtils.isPhone(phone)){
            throw new LyException(400,"手机号格式有误!" );
        }



        //2.生成验证码
        String code = RandomStringUtils.randomNumeric(6);


        //3.调用AMQP,发送短信
        Map<String,String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);

        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,VERIFY_CODE_KEY,msg);


        //4.验证码存入redis
        redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);

    }

    @Override
    @Transactional
    public void register(User user, String code) {
        //1.校验用户名和密码
        //1.1取出redis验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());


        //1.2比较
        if (!StringUtils.equals(cacheCode,code )) {
            throw new LyException(400,"验证码错误" );

        }



        // TODO 2.校验用户数据

        //3.对密码加密，MD5,SHA

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //4.写入数据库

        this.save(user); //this可以省略

    }

    @Override
    public UserDTO queryByUsernameAndPassword(String username, String password) {
        //不同用户名，同一密码可以注册，因为相同密码加密结果不同

        //先根据用户名查询
        User user = query().eq("username", username).one();
        //2.判断结果
        if(user == null) {
            //用户名错误
            throw new LyException(400, "用户名或密码错误" );
        }

        //3.校验密码
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if(!matches) {
            //密码错误
            throw new LyException(400, "用户名或密码错误" );
        }

        return new UserDTO(user);
    }
}