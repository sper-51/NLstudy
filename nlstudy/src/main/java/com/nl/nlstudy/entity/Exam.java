package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam")
public class Exam extends BaseEntity {
    private String name;
    private Long examPaperId;
    private Long courseId;
    private Long teacherId;
    private Long semesterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private BigDecimal totalScore;
    private BigDecimal passScore;
    private Integer allowTimes;
    private Integer isRandomOrder; // 0-否，1-是
    private Integer isRandomOptions; // 0-否，1-是
    private String examType; // formal, practice
    private String status; // pending, published, ongoing, finished
    private Integer studentCount;
    private Integer submitCount;
}
