package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_record")
public class ExamRecord extends BaseEntity {
    private Long examId;
    private Long studentId;
    private Long examStudentId;  // 考试参考学生ID
    private String status; // ongoing, submitted, auto_submitted
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private Integer answeredCount;
    private Integer totalQuestions;
    private Integer switchScreenCount;
    private Integer networkInterruptCount;
    private Double objectiveScore;
    private Double subjectiveScore;
    private Double totalScore;
    private Long durationSeconds; // 实际用时（秒）
}
