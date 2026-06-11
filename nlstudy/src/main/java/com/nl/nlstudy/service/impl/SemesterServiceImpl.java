package com.nl.nlstudy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nl.nlstudy.entity.Semester;
import com.nl.nlstudy.mapper.SemesterMapper;
import com.nl.nlstudy.service.ISemesterService;
import org.springframework.stereotype.Service;

@Service
public class SemesterServiceImpl extends ServiceImpl<SemesterMapper, Semester> implements ISemesterService {
}
