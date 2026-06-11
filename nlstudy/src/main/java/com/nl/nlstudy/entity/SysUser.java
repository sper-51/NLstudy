package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String realName;
    private String role; // admin, teacher, student
    private String email;
    private String phone;
    private String avatar;
    private Integer status; // 0-禁用，1-启用
    private java.time.LocalDateTime lastLoginTime;
    private String lastLoginIp;
}
