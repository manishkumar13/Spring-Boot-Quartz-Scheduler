package com.manish.quartzscheduler.Job;

import com.manish.quartzscheduler.enums.SchedulerEnum;
import com.manish.quartzscheduler.scheduler.AbstractScheduler;
import com.manish.quartzscheduler.service.MessageService;
import com.manish.quartzscheduler.service.SchedulerService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MessageScheduleJob extends AbstractScheduler {

    @Autowired
    private MessageService messageService;

    private static final String CLASS_NAME = "MessageScheduleJob";

    private static final Integer RETRY_COUNT = SchedulerEnum.valueOf(CLASS_NAME).getRetryCount();

    @Override
    public void executeTask(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String message = jobDataMap.getString("message");
        messageService.printMessage(message);
    }

    public void initialiseAsyncJob(String message) throws SchedulerException {
        String triggerName = buildJobTriggerName(Instant.now().getEpochSecond(), CLASS_NAME);
        JobDataMap jobDataMap = getJobDataMap(RETRY_COUNT);
        jobDataMap.put("message", message);

        JobDetail jobDetail = schedulerService.buildJobDetail(this.getClass(), jobDataMap);
        SimpleTrigger trigger = SchedulerService.createSimpleTrigger(jobDetail, triggerName, jobDataMap);

        schedulerService.schedule(jobDetail, trigger);
    }

    public void initialiseAsyncJobIn(Long milliseconds, String message) throws SchedulerException {
        String triggerName = buildJobTriggerName(Instant.now().getEpochSecond(), CLASS_NAME);
        JobDataMap jobDataMap = getJobDataMap(RETRY_COUNT);
        jobDataMap.put("message", message);

        JobDetail jobDetail = schedulerService.buildJobDetail(this.getClass(), jobDataMap);
        SimpleTrigger trigger = SchedulerService.createSimpleTriggerDelayed(jobDetail, triggerName, jobDataMap, milliseconds);

        schedulerService.schedule(jobDetail, trigger);
    }
}
