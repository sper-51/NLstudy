package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("answer_record")
public class AnswerRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long questionId;
    private String studentAnswer;
    private Boolean isCorrect;
    private Double score;
    private Integer answerTimes; // 修改次数
    private LocalDateTime firstAnswerTime;
    @TableField("last_answer_time")
    private LocalDateTime lastModifyTime;
    @TableField(exist = false)
    private Integer sortOrder;
    private LocalDateTime createTime;
}
