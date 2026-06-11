package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("question")
public class Question extends BaseEntity {
    private Long courseId;
    private Long teacherId;
    private String type; // single_choice, multi_choice, true_false, fill_blank, essay
    private String difficulty; // easy, medium, hard
    private String content;
    private String answer;
    private String analysis;
    private BigDecimal score;
    private String knowledgePoints;
    private String chapter;
    private Integer usageCount;
    private Integer status; // 0-禁用，1-启用
    private String source; // original, shared
    private Long sourceQuestionId;
}
