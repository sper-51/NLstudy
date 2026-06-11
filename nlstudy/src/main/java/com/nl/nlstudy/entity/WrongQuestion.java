package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wrong_question")
public class WrongQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;
    private String sourceType; // exam, practice
    private Long examId;
    private Long examRecordId;
    private Long practiceRecordId;
    private Long questionId;
    private String studentAnswer;
    private String correctAnswer;
    private Integer wrongTimes;
    private LocalDateTime lastWrongTime;
    private Integer isFavorited;
    private Integer practiceCount;
    private Integer correctCount;
    private String mySolution;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
