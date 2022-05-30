package com.manish.quartzscheduler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchedulerEnum {

    MessageScheduleJob("", 2);

    private final String cronExpression;
    private final Integer retryCount;

    public static SchedulerEnum stringOf(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    SchedulerEnum(String cronExpression, int retryCount) {
        this.cronExpression = cronExpression;
        this.retryCount = retryCount;
    }
}
