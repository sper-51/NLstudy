package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("student_question_bank_share")
public class StudentQuestionBankShare implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bankId;
    private String shareCode;
    private Long creatorId;
    private Long receiverId;
    private String receiverName;
    private LocalDateTime expireTime;
    private Integer status;
    private Integer usedCount;
    private LocalDateTime createTime;
}
