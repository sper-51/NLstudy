package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("online_user")
public class OnlineUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String role;
    private String token;
    private LocalDateTime loginTime;
    private LocalDateTime lastActiveTime;
    private String ipAddress;
    private Integer status;
    private LocalDateTime createTime;
}
