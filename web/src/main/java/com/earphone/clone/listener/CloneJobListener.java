package com.earphone.clone.listener;

import com.earphone.clone.module.constant.service.ConstantService;
import com.earphone.schedule.quartz.BasicSchedulingListener;

import javax.annotation.Resource;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2016/12/12
 * @createTime 17:47
 */
public class CloneJobListener extends BasicSchedulingListener {
    @Resource
    private ConstantService constantService;

    @Override
    protected String parsePeriodKey(String periodKey) {
        return constantService.findByKey(periodKey).getValue();
    }
}
