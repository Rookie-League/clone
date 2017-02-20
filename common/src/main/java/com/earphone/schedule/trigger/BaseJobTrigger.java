package com.earphone.schedule.trigger;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.HashSet;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-6 下午1:54:39
 */
public abstract class BaseJobTrigger extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(BaseJobTrigger.class);
    private JobMarker jobMarker = JobMarker.getInstance();
    @Resource
    protected ApplicationContext appContext;

    @Override
    protected void executeInternal(JobExecutionContext jobContext) {
        JobKey jobKey = jobContext.getJobDetail().getKey();
        boolean marked = jobMarker.mark(jobKey);
        try {
            if (marked) executeJob(jobContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (marked) {
                jobMarker.remove(jobKey);
            }
        }
    }

    protected abstract void executeJob(JobExecutionContext jobContext) throws Exception;

    /**
     * @author yaojiamin
     * @description
     * @createTime 2016-4-7 下午3:10:31
     */
    private static class JobMarker {
        private static HashSet<JobKey> jobTable = new HashSet<>();
        private static final JobMarker jobMarker = new JobMarker();

        private JobMarker() {
        }

        /**
         * @description
         * @createTime 2016-4-7 下午3:10:53
         * @fileName JobMarker.java
         * @author yaojiamin
         */
        static JobMarker getInstance() {
            return jobMarker;
        }

        synchronized boolean mark(JobKey key) {
            return !jobTable.contains(key) && jobTable.add(key);
        }

        synchronized boolean remove(JobKey key) {
            return jobTable.remove(key);
        }
    }
}
