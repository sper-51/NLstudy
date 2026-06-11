package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("exam_paper_question")
public class PaperQuestion {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("exam_paper_id")
    private Long paperId;
    
    private Long questionId;
    
    private BigDecimal score;
    
    private Integer sortOrder;
    
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}