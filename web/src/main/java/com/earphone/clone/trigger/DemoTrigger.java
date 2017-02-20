package com.earphone.clone.trigger;

import com.earphone.schedule.trigger.BaseJobTrigger;
import org.quartz.JobExecutionContext;

import com.earphone.schedule.tag.QuartzJob;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-6 下午2:38:17
 */
@QuartzJob(name = "trigger", period = "1000")
public class DemoTrigger extends BaseJobTrigger {
    @Override
    protected void executeJob(JobExecutionContext jobContext) throws Exception {
        System.out.println(System.currentTimeMillis());
    }
}
