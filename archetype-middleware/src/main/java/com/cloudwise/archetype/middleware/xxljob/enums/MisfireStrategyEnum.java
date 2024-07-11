package com.cloudwise.archetype.middleware.xxljob.enums;

import lombok.Getter;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
@Getter
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING("misfire_strategy_do_nothing"),

    /**
     * fire once now
     */
    FIRE_ONCE_NOW("misfire_strategy_fire_once_now");

    private final String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

}
