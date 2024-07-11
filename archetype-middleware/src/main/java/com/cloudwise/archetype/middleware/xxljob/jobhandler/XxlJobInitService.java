package com.cloudwise.archetype.middleware.xxljob.jobhandler;

import com.cloudwise.archetype.middleware.xxljob.config.XxlJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Author pio.wang
 * @Since: 2021-09-01 16:18
 */
@Service
@Slf4j
public class XxlJobInitService {
    @Resource
    private XxlJobService xxlJobService;
    
    @Value("${identitySource.syncData.job.cron:0 0 2 * * ?}")
    private String identitySourceSyncDataJobCron;
    
    @PostConstruct
    public void init() {
        try {
            xxlJobService.registerJob("demoJobHandler",
                    identitySourceSyncDataJobCron,
                    SampleXxlJob.class.getSimpleName());

        } catch (Exception e) {
            log.error(" register job error!", e);
        }

    }

}