package com.nl.nlstudy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.nlstudy.entity.SysClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysClassMapper extends BaseMapper<SysClass> {
    @Select("""
            SELECT c.id, c.name, c.grade, c.description
            FROM student_class sc
            JOIN sys_class c ON sc.class_id = c.id
            WHERE sc.student_id = #{studentId}
            ORDER BY sc.create_time DESC
            """)
    List<Map<String, Object>> selectClassesByStudentId(Long studentId);
}
