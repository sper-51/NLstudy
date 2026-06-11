package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_question_bank")
public class StudentQuestionBank extends BaseEntity {
    private Long studentId;
    private String name;
    private String description;
    private String source; // original, imported, shared
    private Long sourceBankId;
    private Integer questionCount;
    private Integer isShared;
    private String shareCode;
    private java.time.LocalDateTime shareExpireTime;
    private Integer status;
}
