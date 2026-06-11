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
@TableName("grade_review")
public class GradeReview implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gradeId;
    private Long examId;
    private Long studentId;
    private Long questionId;
    private String reason; // 复核原因
    private String studentComment; // 学生补充说明
    private BigDecimal originalScore; // 原始分数
    private BigDecimal newScore; // 新分数
    private String teacherComment; // 教师处理意见
    private String status; // pending, approved, rejected
    @TableField("review_teacher_id")
    private Long handlerId; // 处理人ID
    @TableField("review_time")
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
}
