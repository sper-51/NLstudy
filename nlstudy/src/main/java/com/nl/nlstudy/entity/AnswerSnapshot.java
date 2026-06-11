package com.nl.nlstudy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("answer_snapshot")
public class AnswerSnapshot implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examRecordId;
    private String snapshotData; // JSON格式存储答题状态
    private String saveType; // auto, manual
    private String networkStatus; // normal, interrupted
    private LocalDateTime saveTime;
}
