package com.manish.quartzscheduler.scheduler;

import com.manish.quartzscheduler.helper.constant.SchedulerConstants;
import com.manish.quartzscheduler.service.SchedulerService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractScheduler implements Job {

    @Autowired
    protected SchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            executeTask(context);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    public abstract void executeTask(JobExecutionContext context) throws Exception;

    public JobDataMap getJobDataMap(Integer retryCount) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SchedulerConstants.CURRENT_RETRY_COUNT, 0);
        jobDataMap.put(SchedulerConstants.MAX_ALLOWED_RETRY_COUNT, retryCount);
        return jobDataMap;
    }

    public JobDataMap getJobDataMap(Integer retryCount, Long delay) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(SchedulerConstants.CURRENT_RETRY_COUNT, 0);
        jobDataMap.put(SchedulerConstants.MAX_ALLOWED_RETRY_COUNT, retryCount);
        jobDataMap.put(SchedulerConstants.RETRY_DELAY, delay);
        return jobDataMap;
    }

    public String buildJobTriggerName(Long uniqueId, String className) {
        return uniqueId.toString() + className;
    }
}
