package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_code")
public class CourseCode implements Serializable {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long courseId;
    private String code;
    private LocalDateTime expireTime;
    private Integer maxUses;
    private Integer usedCount;
    private Integer status; // 0-禁用，1-启用
}
