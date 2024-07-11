package com.cloudwise.archetype.middleware.rocketmq.consumer;

import com.cloudwise.archetype.middleware.rocketmq.annotation.MyRocketMQMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 监听消息
 *
 * @author pio.wang
 * @date 2023-10-17
 **/
@Component
@MyRocketMQMessageListener(consumerGroup = "grouptest",
        topic = "testtopic",
        consumeMode = ConsumeMode.CONCURRENTLY,
        replyTimeout = 15000,
        consumeThreadNumber = 1,
        pullBatchSize = 2)
@Slf4j
public class DemoConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.debug("rocketmq DemoConsumer get message: {}", message);
        try {
            // do something
        } catch (RuntimeException exception) {
            log.error("rocketmq DemoConsumer consume message fail", exception);
            throw exception;
        }
    }
}




