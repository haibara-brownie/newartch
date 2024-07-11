package com.cloudwise.archetype.middleware.xxljob.enums;

import lombok.Getter;

/**
 * Created by xuxueli on 17/3/10.
 */
@Getter
public enum ExecutorRouteStrategyEnum {

    FIRST("jobconf_route_first"),
    LAST("jobconf_route_last"),
    ROUND("jobconf_route_round"),
    RANDOM("jobconf_route_random"),
    LEAST_FREQUENTLY_USED("jobconf_route_lfu"),
    LEAST_RECENTLY_USED("jobconf_route_lru"),
    FAILOVER("jobconf_route_failover"),
    BUSYOVER("jobconf_route_busyover"),
    SHARDING_BROADCAST("jobconf_route_shard");

    ExecutorRouteStrategyEnum(String title) {
        this.title = title;
    }

    private final String title;

}
