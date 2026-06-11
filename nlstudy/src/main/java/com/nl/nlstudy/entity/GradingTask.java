package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("grading_task")
public class GradingTask implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long questionId;
    private Long studentId;
    @TableField("teacher_id")
    private Long graderId; // 批改人ID
    private String status; // pending, grading, completed
    private BigDecimal score;
    private String comment;
    @TableField("grading_time")
    private LocalDateTime gradeTime;
    private LocalDateTime createTime;
}
