package com.nl.nlstudy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nl.nlstudy.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
