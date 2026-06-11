package com.nl.nlstudy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nl.nlstudy.entity.SysClass;
import com.nl.nlstudy.mapper.SysClassMapper;
import com.nl.nlstudy.service.ISysClassService;
import org.springframework.stereotype.Service;

@Service
public class SysClassServiceImpl extends ServiceImpl<SysClassMapper, SysClass> implements ISysClassService {
}
