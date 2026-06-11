package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("student_question_bank_item")
public class StudentQuestionBankItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bankId;
    private Long questionId;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
