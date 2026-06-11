package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    private String name;
    private String code;
    private Long teacherId;
    private Long semesterId;
    private BigDecimal credits;
    private Integer hours;
    private String description;
    private Integer status; // 0-下架，1-上架
    private Integer isShared; // 0-否，1-是
}
