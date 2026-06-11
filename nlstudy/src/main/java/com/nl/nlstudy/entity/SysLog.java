package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String operation; // 操作类型
    private String module; // 操作模块
    private String method; // 请求方法
    private String params; // 请求参数
    private String result; // 执行结果
    private String ipAddress; // IP地址
    private Integer executionTime; // 执行耗时(ms)
    private LocalDateTime createTime;
}
