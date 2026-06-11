package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("teacher_info")
public class TeacherInfo implements Serializable {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String researchDirection;
    private String introduction;
}
