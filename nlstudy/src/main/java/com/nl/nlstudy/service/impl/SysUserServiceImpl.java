package com.nl.nlstudy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nl.nlstudy.entity.SysUser;
import com.nl.nlstudy.mapper.SysUserMapper;
import com.nl.nlstudy.service.ISysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
