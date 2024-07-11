package com.cloudwise.archetype.commons.concurrent;

import cn.hutool.core.util.RuntimeUtil;
import com.cloudwise.concurrent.MdcThreadPoolTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class ThreadPoolConfig implements SchedulingConfigurer {
    
    @Value("${server.threadPool.corePoolSize:64}")
    private int corePoolSize;
    @Value("${server.threadPool.maxPoolSize:1000}")
    private int maxPoolSize;
    @Value("${server.dataStore.queueCapacity:2000}")
    private int dataStoreQueueCapacity;
    @Value("${server.dataStore.threadKeepAliveSeconds:300}")
    private int dataStoreThreadKeepAliveSeconds;
    
    private static int getIoDefaultProcessors() {
        return 2 * RuntimeUtil.getProcessorCount() + 1;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        if (corePoolSize <= 0) {
            corePoolSize = (Math.max(getIoDefaultProcessors(), 64));
        }
        ThreadPoolTaskExecutor threadPoolTaskExecutor = MdcThreadPoolTaskExecutor.newMdcThreadPoolTaskExecutor();
        threadPoolTaskExecutor.initialize();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 设置队列容量
        threadPoolTaskExecutor.setQueueCapacity(dataStoreQueueCapacity);
        // 设置队列容量
        threadPoolTaskExecutor.setKeepAliveSeconds(dataStoreThreadKeepAliveSeconds);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置默认线程名称
        threadPoolTaskExecutor.setThreadNamePrefix("CloudWise-ThreadPoolConfig-");
        // 等待所有任务结束后再关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return threadPoolTaskExecutor;
    }

    /**
     * 定时任务线程池
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 设置线程数
        taskScheduler.setPoolSize(8);
        // 设置ErrorHandler
        taskScheduler.setErrorHandler(e -> log.error("cloudWiseTaskScheduler error:{}", e.getMessage(), e));
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置默认线程名称
        taskScheduler.setThreadNamePrefix("CloudWise-scheduler-pool-");
        // 等待所有任务结束后再关闭线程池
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        return taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
