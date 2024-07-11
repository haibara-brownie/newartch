package com.cloudwise.archetype.middleware.xxljob.enums;

import lombok.Getter;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
@Getter
public enum ScheduleTypeEnum {

    NONE("schedule_type_none"),

    /**
     * schedule by cron
     */
    CRON("schedule_type_cron"),

    /**
     * schedule by fixed rate (in seconds)
     */
    FIX_RATE("schedule_type_fix_rate"),

    /**
     * schedule by fix delay (in seconds)， after the last time
     */
    FIX_DELAY("schedule_type_fix_delay");

    private final String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

}
