package com.leyou.user.web;

import com.leyou.auth.utils.UserHolder;
import com.leyou.common.exception.LyException;
import com.leyou.user.dto.AddressDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("address")
public class AddressController {

    @GetMapping("hello")
    public ResponseEntity<String> hello(){
        //登录校验
        return ResponseEntity.ok("上海浦东新区航头镇航头路18号传智播客");
    }

    @GetMapping("me")
    public ResponseEntity<String> me(){
        //登录校验
        return ResponseEntity.ok("上海浦东新区航头镇航头路19号传智播客");
    }


    /**
     * 根据
     * @param id 地址id
     * @return 地址信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> queryAddressById(@PathVariable("id") Long id){
        //TODO 去数据库根据id查询地址
        AddressDTO address = new AddressDTO();

        //以下数据从数据库查，此处为假数据
        address.setId(1L);
        address.setUserId(32L);
        address.setStreet("航头镇航头路18号传智播客 3号楼");
        address.setCity("上海");
        address.setDistrict("浦东新区");
        address.setAddressee("虎哥");
        address.setPhone("15800000000");
        address.setProvince("上海");
        address.setPostcode("210000");
        address.setIsDefault(true);

        //校验查到的地址是否属于当前用户
        Long userId = UserHolder.getUser().getId();
        if(!Objects.equals(userId,address.getUserId())) {
            //不属于当前用户，非法访问
            throw new LyException(403,"没有权限访问");
        }


        return ResponseEntity.ok(address);
    }
}