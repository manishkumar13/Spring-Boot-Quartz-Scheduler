package com.manish.quartzscheduler.scheduler;

import com.manish.quartzscheduler.helper.constant.SchedulerConstants;
import com.manish.quartzscheduler.service.SchedulerService;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;

public class JobFailureHandler implements JobListener {

    @Override
    public String getName() {
        return "JobFailureHandlerListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        Integer presentRetriesCount = (Integer) context.getMergedJobDataMap().get(SchedulerConstants.CURRENT_RETRY_COUNT);
        if (presentRetriesCount == null) {
            presentRetriesCount = 0;
        }
        context.put(SchedulerConstants.RETRIES, ++presentRetriesCount);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
        if (e == null) {
            return;
        }

        String method = context.getJobDetail().getJobClass().getSimpleName();
        Integer retryCount = (Integer) context.get(SchedulerConstants.RETRIES);
        Integer maxAllowedRetries = getMaxRetryCount(context);

        if (retryCount >= maxAllowedRetries) {
            return;
        }

        rescheduleJob(context, retryCount, method);
    }

    private void rescheduleJob(JobExecutionContext context, Integer retryCount, String method) {
        String triggerName = context.getTrigger().getKey().getName();
        JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
        jobDataMap.replace(SchedulerConstants.CURRENT_RETRY_COUNT, retryCount);
        Long delay = 0L;
        if (jobDataMap.containsKey(SchedulerConstants.RETRY_DELAY))
            delay = (long) jobDataMap.get(SchedulerConstants.RETRY_DELAY);
        Trigger trigger;
        if (delay > 0L)
            trigger = SchedulerService.createSimpleTriggerDelayed(context.getJobDetail(), triggerName, jobDataMap, delay);
        else
            trigger = SchedulerService.createSimpleTrigger(context.getJobDetail(), triggerName, jobDataMap);
        try {
            if (context.getTrigger() instanceof CronTriggerImpl) {
                context.getScheduler().scheduleJob(trigger);
            } else {
                context.getScheduler().rescheduleJob(context.getTrigger().getKey(), trigger);
            }
        } catch (SchedulerException e1) {

        }
    }

    private Integer getMaxRetryCount(JobExecutionContext context) {
        Object maxAllowedRetries = context.getTrigger().getJobDataMap().get(SchedulerConstants.MAX_ALLOWED_RETRY_COUNT);
        if (maxAllowedRetries == null) {
            maxAllowedRetries = 0;
        }
        return (int) maxAllowedRetries;
    }
}
