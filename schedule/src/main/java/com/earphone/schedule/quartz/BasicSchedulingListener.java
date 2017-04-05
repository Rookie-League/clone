package com.earphone.schedule.quartz;

import java.util.Map.Entry;

import com.earphone.schedule.tag.QuartzJob;
import com.earphone.common.utils.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import com.earphone.common.validation.Assert;

import javax.annotation.Resource;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-5-10 上午11:28:03
 */
public abstract class BasicSchedulingListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(BasicSchedulingListener.class);
    @Resource
    private Scheduler scheduler;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            this.loadTriggers(event.getApplicationContext(), scheduler);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadTriggers(ApplicationContext context, Scheduler scheduler) throws Exception {
        scheduler.pauseAll();
        for (Entry<String, Object> entry : context.getBeansWithAnnotation(QuartzJob.class).entrySet()) {
            try {
                Class<? extends Job> clazz = parseClass(entry);
                QuartzJob annotation = AnnotationUtils.findAnnotation(clazz, QuartzJob.class);
                String name = annotation.name();
                String group = annotation.group();
                String period = annotation.period();
                Assert.wrapString(period).isNotBlank("Invalid quartz trigger config");
                JobKey jobKey = createJobKey(name, group);
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
                JobBuilder jobBuilder = createJobBuilder(clazz);
                JobDetail jobDetail = createJobDetail(jobBuilder, jobKey);
                Trigger trigger = createTrigger(name, period);
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        scheduler.resumeAll();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Job> parseClass(Entry<String, Object> entry) {
        Class<? extends Job> clazz = (Class<? extends Job>) entry.getValue().getClass();
        Assert.wrapBoolean(Job.class.isAssignableFrom(clazz)).isTrue(clazz + " doesn't implemented " + Job.class.getName());
        return clazz;
    }

    private JobBuilder createJobBuilder(Class<? extends Job> clazz) {
        return JobBuilder.newJob(clazz);
    }

    private JobKey createJobKey(String name, String group) {
        return new JobKey(name, group);
    }

    private Trigger createTrigger(String name, String periodKey) {
        String period = parsePeriodKey(periodKey);
        if (StringUtils.isNumeric(period)) {
            return createSimpleTrigger(getInterval(period), createTriggerBuilder(name));
        }
        return createCronTrigger(period, createTriggerBuilder(name));
    }

    protected abstract String parsePeriodKey(String periodKey);

    private SimpleTrigger createSimpleTrigger(Long interval, TriggerBuilder<Trigger> triggerBuilder) {
        return triggerBuilder.withSchedule(setIntervalInMilliseconds(interval)).build();
    }

    private SimpleScheduleBuilder setIntervalInMilliseconds(Long interval) {
        return SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(interval).repeatForever();
    }

    private Long getInterval(String interval) {
        return Long.valueOf(interval);
    }

    private CronTrigger createCronTrigger(String cron, TriggerBuilder<Trigger> triggerBuilder) {
        return triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
    }

    private TriggerBuilder<Trigger> createTriggerBuilder(String identity) {
        return TriggerBuilder.newTrigger().withIdentity(identity);
    }

    private JobDetail createJobDetail(JobBuilder jobBuilder, JobKey jobKey) {
        return jobBuilder.withIdentity(jobKey).build();
    }
}
