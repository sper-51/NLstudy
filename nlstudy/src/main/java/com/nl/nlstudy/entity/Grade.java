package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("grade")
public class Grade implements Serializable {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private Long examId;
    private Long studentId;
    private BigDecimal totalScore;
    private BigDecimal objectiveScore;
    private BigDecimal subjectiveScore;
    @TableField("`rank`")
    private Integer rank;
    @TableField("percentile_rank")
    private BigDecimal percentile;
    private String status; // pending, published, reviewed
    private LocalDateTime publishTime;
}
