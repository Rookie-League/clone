package com.earphone.schedule.quartz;

import org.quartz.Job;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.Resource;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-6 下午3:39:18
 */
public class SpringQuartzJobFactory extends SpringBeanJobFactory {
    Logger logger = LoggerFactory.getLogger(SpringQuartzJobFactory.class);
    @Resource
    private ApplicationContext ctx;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        try {
            Job job = ctx.getBean(bundle.getJobDetail().getJobClass());
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
            if (isEligibleForPropertyPopulation(bw.getWrappedInstance())) {
                MutablePropertyValues pvs = new MutablePropertyValues();
                pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
                pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
                bw.setPropertyValues(pvs, true);
            }
            return job;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        }
    }
}
