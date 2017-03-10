package com.earphone.clone.module.constant.service;

import com.earphone.clone.module.constant.mapper.ConstantMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.earphone.clone.module.constant.domain.Constant;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConstantService {
    @Resource
    private ConstantMapper constantMapper;

    public Constant findByKey(String key) {
        Constant constant = new Constant();
        constant.setKey(key);
        return constantMapper.selectOne(constant);
    }

    public List<Constant> findHierarchy(String hierarchy) {
        Constant constant = new Constant();
        constant.setHierarchy(hierarchy);
        return PageHelper.startPage(1, 2).doSelectPage(() -> constantMapper.select(constant));
    }
}
