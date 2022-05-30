package com.manish.quartzscheduler.service;

import com.manish.quartzscheduler.scheduler.JobFailureHandler;
import lombok.NoArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class SchedulerService {

    @Autowired
    @Qualifier("schedulerFactoryBean")
    @Lazy
    SchedulerFactoryBean schedulerFactoryBean;

    public JobDetail buildJobDetail(Class jobClass, JobDataMap jobDataMap) {
        try {
            return JobBuilder.newJob(jobClass)
                    .usingJobData(jobDataMap)
                    .storeDurably(true)
                    .build();
        } catch (Exception e) {

        }
        return null;
    }

    public static SimpleTrigger createSimpleTrigger(JobDetail jobDetail,
                                                    String triggerName, JobDataMap map) {
        return createSimpleTriggerDelayed(jobDetail, triggerName, map, 0L);
    }

    public static SimpleTrigger createSimpleTriggerDelayed(JobDetail jobDetail, String triggerName, JobDataMap map, Long delayTime) {

        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setJobDataMap(map);
        factoryBean.setStartDelay(delayTime);
        factoryBean.setRepeatCount(0);

        factoryBean.setName(triggerName);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(
                SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /**
     * It starts the scheduler using the job detail and trigger.
     *
     * @param jobDetail
     * @param trigger
     */
    public void schedule(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(new JobFailureHandler(), GroupMatcher.anyGroup());
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
