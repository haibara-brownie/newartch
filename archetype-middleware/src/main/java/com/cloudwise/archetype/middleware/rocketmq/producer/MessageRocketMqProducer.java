package com.cloudwise.archetype.middleware.rocketmq.producer;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.remoting.RPCHook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 前台RocketMq生产者
 *
 * @author maker.wang
 * @date 2021-10-26 16:21
 **/
@Component
@Slf4j
public class MessageRocketMqProducer {

    @Value("${message.rocketmq.producer.group:message-api-group}")
    private String producerGroup;

    @Value("${rocketMqNameServers:}")
    private String nameServerAddr;

    @Value("${rocketMqAccessKey:}")
    private String accessKey;

    @Value("${rocketMqSecretKey:}")
    private String secretKey;
    @Value("${message.rocketmq.producer.send-message-timeout:60000}")
    private int timeout;
    @Getter
    private DefaultMQProducer mqProducer;

    @PostConstruct
    public void initMQ() {
        log.debug("init message RocketMq start");
        log.debug("producerGroup:{},nameServerAddr:{}", producerGroup, nameServerAddr);
        mqProducer = new DefaultMQProducer(producerGroup, getAclRPCHook());
        mqProducer.setNamesrvAddr(nameServerAddr);
        mqProducer.setSendMsgTimeout(timeout);
        mqProducer.setInstanceName("MessageProducerToOtherServices@" + UtilAll.getPid() + "#" + System.currentTimeMillis());
        mqProducer.setVipChannelEnabled(false);
        try {
            mqProducer.start();
            log.info("init  message RocketMq start success");
        } catch (MQClientException e) {
            log.error("init  message RocketMq start fail，", e);
        }
    }

    @PreDestroy
    public void destroy() {
        mqProducer.shutdown();
        log.info("destroy inner message RocketMq start success");
    }

    private RPCHook getAclRPCHook() {
        log.info("加载RocketMq ACL配置:accessKey:{}-accessSecret:{}", this.accessKey, this.accessKey);
        if (StringUtils.isEmpty(accessKey) && StringUtils.isEmpty(secretKey)) {
            return null;
        }
        return new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
    }


}
