package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nl.nlstudy.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("semester")
public class Semester extends BaseEntity {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status; // 0-禁用，1-启用
}
