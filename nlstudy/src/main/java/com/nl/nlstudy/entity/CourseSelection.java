package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course_selection")
public class CourseSelection implements Serializable {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDateTime selectTime;
    private Integer status; // 0-已退课，1-在读
}
