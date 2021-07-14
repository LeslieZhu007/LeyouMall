package com.leyou.trade.util;

import com.leyou.auth.dto.UserDetails;
import com.leyou.auth.utils.UserHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Leslie Arnold
 * @Date: 2020/12/27 0:51
 */

@Component
public class CollectionNameBuilder {

    /**
     * 集合名称的前缀
     */
    @Value("${ly.mongo.collectionNamePrefix}")
    private String namePrefix;


    public String build() {


        //获取用户id  UserHolder在auth-api中，存放的是threadlocal
        UserDetails user = UserHolder.getUser();
        //健壮性判断
        if( user == null) {
            //用户未登录，无需生成集合名称
            return "";
        }

        //用户存在

        //用户id计算hash值，并计算集合名称
        int index = user.getId().hashCode() % 100;

        return namePrefix + index;

    }
}
