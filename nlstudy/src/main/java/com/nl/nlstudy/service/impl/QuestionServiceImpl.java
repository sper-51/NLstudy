package com.nl.nlstudy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nl.nlstudy.entity.Question;
import com.nl.nlstudy.mapper.QuestionMapper;
import com.nl.nlstudy.service.IQuestionService;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {
}
