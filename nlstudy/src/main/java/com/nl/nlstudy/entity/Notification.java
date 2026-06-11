package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type; // exam, grade, system, message
    private String title;
    private String content;
    private Long relatedId; // 关联的业务ID
    private Integer isRead; // 0-未读，1-已读
    private LocalDateTime readTime;
    private LocalDateTime createTime;
}
