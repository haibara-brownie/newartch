package com.cloudwise.archetype.middleware.kafka;

import com.cloudwise.msg.annotations.CloudWiseMsg;
import com.cloudwise.msg.utils.MsgUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: pio.wang
 * @create: 2023-10-17
 * kafka相关操作
 **/
@Slf4j
@Service
public class KafkaOperate {
    /**
     * 注入的前提为启动类添加了@Msg注解
     */
    @Resource
    private MsgUtils msgUtils;


    /**
     * 简单发送消息
     */
    public void send() {
        String topic = "testTopic";
        String data = "test111";
        // 根据topic和数据发送
        msgUtils.producer().send(topic, data);

        String key = "test";
        // 指定key发送
        msgUtils.producer().send(topic, key, data);

        int partition = 1;
        long timestamp = System.currentTimeMillis();
        // 指定key、partion、时间戳发送
        msgUtils.producer().send(topic, partition, timestamp, key, data);

        // 包装数据体发送
        ProducerRecord<String, String> producerRecord = new ProducerRecord(topic, key, data);
        msgUtils.producer().send(producerRecord);
    }
    
    /**
     * 使用注解创建一个消费者
     *
     * @param data           消费的信息
     * @param acknowledgment
     * @param consumer
     */
    @CloudWiseMsg(topics = "cloudwiseTopic", groupId = "cloudwiseGroup", bootstrapServers = "${kafkaBrokerServers}")
    public void consumerCloudwiseMsg(List<ConsumerRecord> data, Acknowledgment acknowledgment, Consumer consumer) {
        log.error("数据量：{}", data.size());
        // 处理数据，然后提交
        acknowledgment.acknowledge();
    }
    
}
