package com.manish.quartzscheduler.controller;

import com.manish.quartzscheduler.Job.MessageScheduleJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuartzController {

    @Autowired
    private MessageScheduleJob messageScheduleJob;

    @PostMapping("/scheduleMessage")
    public String scheduleMessage(@RequestParam(name = "message") String message) {
        try {
            messageScheduleJob.initialiseAsyncJob(message);
            return "Your message [" + message + "] scheduled successfully";
        } catch (SchedulerException e) {
            return "Message " + message + " doesn't scheduled due to error " + e;
        }
    }
}
