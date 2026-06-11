package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("question_option")
public class QuestionOption implements Serializable {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long questionId;
    private String optionLabel; // A, B, C, D
    private String optionContent;
    private Integer isCorrect; // 0-否，1-是
    private Integer sortOrder;
    private LocalDateTime createTime;
}
