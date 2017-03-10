package com.earphone.clone.module.constant.service;

import com.earphone.clone.module.constant.mapper.ConstantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.earphone.clone.module.constant.domain.Constant;

import javax.annotation.Resource;

@Service
@Transactional(readOnly = true)
public class ConstantService {
    @Resource
    private ConstantMapper constantMapper;

    public Constant findByKey(String key) {
        return constantMapper.findByKey(key);
    }
}
