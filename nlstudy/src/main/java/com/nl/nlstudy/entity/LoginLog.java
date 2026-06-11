package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("login_log")
public class LoginLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private Integer status; // 1-成功，0-失败
    private String ipAddress;
    private String loginMethod; // password, token
    private String userAgent;
    private String errorMessage;
    private LocalDateTime createTime;
}
