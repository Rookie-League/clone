package com.earphone.schedule.tag;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.quartz.JobKey;
import org.springframework.stereotype.Component;

/**
 * @description 调度任务注解，interval和cron必须有一个有值
 * @author yaojiamin
 * @createTime 2016-4-6 下午3:36:34
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface QuartzJob {
    /**
     * @description 调度任务名称
     * @createTime 2016-5-10 上午11:31:01
     * @fileName QuartzJob.java
     * @author yaojiamin
     */
    String name();

    /**
     * @description 调度任务分组
     * @createTime 2016-5-10 上午11:31:23
     * @fileName QuartzJob.java
     * @author yaojiamin
     */
    String group() default JobKey.DEFAULT_GROUP;

    /**
     * @description 调度表达式
     * @createTime 2016-5-10 上午11:29:59
     * @fileName QuartzJob.java
     * @author yaojiamin
     */
    String period() default "";

}
