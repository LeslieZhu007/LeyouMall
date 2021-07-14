package com.leyou.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leyou.common.constants.RegexPatterns;
import com.leyou.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@TableName("tb_user")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @TableId
    private Long id;
    @NotNull(message = "用户名不能为空")
    //@Length(min = 4,max = 32,message = "用户名格式不正确")
    //@Pattern(regexp = "^[\\w]{4,32}&")
    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX,message = "用户名格式不正确")
    private String username;

    @NotNull(message = "密码不能为空")
    @Pattern(regexp = RegexPatterns.PASSWORD_REGEX,message = "密码格式不正确")
    private String password;

    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = RegexPatterns.PHONE_REGEX,message = "手机号格式不正确")
    private String phone;
}