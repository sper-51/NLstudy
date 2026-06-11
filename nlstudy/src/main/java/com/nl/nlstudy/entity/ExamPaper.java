package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_paper")
public class ExamPaper extends BaseEntity {
    private String name;
    private Long courseId;
    private Long teacherId;
    private BigDecimal totalScore;
    private BigDecimal passScore;
    private Integer duration; // 考试时长（分钟）
    private Integer questionCount;
    private String type; // manual, random, template
    private String ruleConfig; // JSON
    private String description;
    private Integer status; // 0-草稿，1-已发布
}
