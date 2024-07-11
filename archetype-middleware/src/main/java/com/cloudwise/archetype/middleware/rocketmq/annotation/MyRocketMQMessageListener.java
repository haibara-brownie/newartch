package com.cloudwise.archetype.middleware.rocketmq.annotation;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zafir.zhong
 * @description 对rocketmq注解进行封装
 * @date Created in 17:42 2023/6/12.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRocketMQMessageListener {

    String NAME_SERVER_PLACEHOLDER = "${rocketMqNameServers:}";
    String ACCESS_KEY_PLACEHOLDER = "${rocketMqAccessKey:}";
    String SECRET_KEY_PLACEHOLDER = "${rocketMqSecretKey:}";
    String TRACE_TOPIC_PLACEHOLDER = "${rocketmq.consumer.customized-trace-topic:}";
    String ACCESS_CHANNEL_PLACEHOLDER = "${rocketmq.access-channel:}";

    /**
     * Consumers of the same role is required to have exactly same subscriptions and consumerGroup to correctly achieve
     * load balance. It's required and needs to be globally unique.
     * <p>
     * <p>
     * See <a href="http://rocketmq.apache.org/docs/core-concept/">here</a> for further discussion.
     */
    String consumerGroup();

    /**
     * Topic name.
     */
    String topic();

    /**
     * Control how to selector message.
     *
     * @see SelectorType
     */
    SelectorType selectorType() default SelectorType.TAG;

    /**
     * Control which message can be select. Grammar please see {@link SelectorType#TAG} and {@link SelectorType#SQL92}
     */
    String selectorExpression() default "*";

    /**
     * Control consume mode, you can choice receive message concurrently or orderly.
     */
    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

    /**
     * Control message mode, if you want all subscribers receive message all message, broadcasting is a good choice.
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * Max consumer thread number.
     *
     * @see <a href="https://github.com/apache/rocketmq-spring/issues/429">issues#429</a>
     * @deprecated This property is not work well, because the consumer thread pool executor use
     * {@link LinkedBlockingQueue} with default capacity bound (Integer.MAX_VALUE), use
     * {@link RocketMQMessageListener#consumeThreadNumber} .
     */
    @Deprecated
    int consumeThreadMax() default 64;

    /**
     * consumer thread number.
     */
    int consumeThreadNumber() default 20;

    int pullBatchSize() default 16;

    /**
     * Max re-consume times.
     * <p>
     * In concurrently mode, -1 means 16;
     * In orderly mode, -1 means Integer.MAX_VALUE.
     */
    int maxReconsumeTimes() default -1;

    /**
     * Maximum amount of time in minutes a message may block the consuming thread.
     */
    long consumeTimeout() default 15L;

    /**
     * Timeout for sending reply messages.
     */
    int replyTimeout() default 3000;

    /**
     * The property of "access-key".
     */
    String accessKey() default ACCESS_KEY_PLACEHOLDER;

    /**
     * The property of "secret-key".
     */
    String secretKey() default SECRET_KEY_PLACEHOLDER;

    /**
     * Switch flag instance for message trace.
     */
    boolean enableMsgTrace() default false;

    /**
     * The name value of message trace topic.If you don't config,you can use the default trace topic name.
     */
    String customizedTraceTopic() default TRACE_TOPIC_PLACEHOLDER;

    /**
     * The property of "name-server".
     */
    String nameServer() default NAME_SERVER_PLACEHOLDER;

    /**
     * The property of "access-channel".
     */
    String accessChannel() default ACCESS_CHANNEL_PLACEHOLDER;

    /**
     * The property of "tlsEnable" default false.
     */
    String tlsEnable() default "false";

    /**
     * The namespace of consumer.
     */
    String namespace() default "";

    /**
     * Message consume retry strategy in concurrently mode.
     * <p>
     * -1,no retry,put into DLQ directly
     * 0,broker control retry frequency
     * >0,client control retry frequency
     */
    int delayLevelWhenNextConsume() default 0;

    /**
     * The interval of suspending the pull in orderly mode, in milliseconds.
     * <p>
     * The minimum value is 10 and the maximum is 30000.
     */
    int suspendCurrentQueueTimeMillis() default 1000;

    /**
     * Maximum time to await message consuming when shutdown consumer, in milliseconds.
     * The minimum value is 0
     */
    int awaitTerminationMillisWhenShutdown() default 1000;

    /**
     * The property of "instanceName".
     */
    String instanceName() default "DEFAULT";

    long pullInterval() default 0;

    int persistConsumerOffsetInterval() default 5000;

    int mqClientApiTimeout() default 10000;

    int pollNameServerInterval() default 30000;
}
