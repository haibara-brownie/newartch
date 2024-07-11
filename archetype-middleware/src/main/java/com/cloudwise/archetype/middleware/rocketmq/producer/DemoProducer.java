package com.cloudwise.archetype.middleware.rocketmq.producer;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * 发送消息
 */
@Component
@Slf4j
public class DemoProducer {

    @Resource
    private MessageRocketMqProducer rocketMqProducer;

    public boolean sendMessage(String topic, String tags, String msgBody) {
        try {
            SendResult send;
            if (StringUtils.isEmpty(tags)) {
                send = rocketMqProducer.getMqProducer().send(new Message(topic, msgBody.getBytes()));
            } else {
                send = rocketMqProducer.getMqProducer().send(new Message(topic, tags, msgBody.getBytes()));
            }
            if (!Objects.equals(send.getSendStatus(), SendStatus.SEND_OK)) {
                return false;
            }
        } catch (Exception exception) {
            log.error("rocketmq send fail ", exception);
            return false;
        }
        return true;
    }

}

