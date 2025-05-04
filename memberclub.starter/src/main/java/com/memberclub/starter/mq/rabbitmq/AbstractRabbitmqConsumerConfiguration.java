/**
 * @(#)AbstractRabbitmqConsumerConfiguration.java, 一月 22, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.mq.rabbitmq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.mq.ConsumeStatauEnum;
import com.memberclub.infrastructure.mq.MQQueueEnum;
import com.memberclub.infrastructure.mq.MessageQueueConsumerFacade;
import com.memberclub.infrastructure.mq.RabbitRegisterConfiguration;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
public class AbstractRabbitmqConsumerConfiguration {

    public static final Logger LOG = LoggerFactory.getLogger(RabbitmqConsumerConfiguration.class);
    public static final String RETRY_COUNT = "retry-count";

    private Map<String, List<MessageQueueConsumerFacade>> consumerMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        Map<String, MessageQueueConsumerFacade> consumers = null;
        try {
            consumers =
                    ApplicationContextUtils.getContext().getBeansOfType(MessageQueueConsumerFacade.class);
        } catch (Exception e) {
        }
        if (MapUtils.isNotEmpty(consumers)) {
            for (Map.Entry<String, MessageQueueConsumerFacade> entry : consumers.entrySet()) {
                MQQueueEnum mqQueueEnum = entry.getValue().register();
                consumerMap.putIfAbsent(mqQueueEnum.getQueneName(), Lists.newArrayList());
                consumerMap.get(mqQueueEnum.getQueneName()).add(entry.getValue());
            }
        }
    }


    protected void consume(String value, Channel channel, Message message,
                           MQQueueEnum queue) throws IOException {
        LOG.info("收到rabbitmq消息 queue:{}, message:{}", queue.getDelayQueneName(), value);

        boolean fail = false;
        RuntimeException exception = null;
        for (MessageQueueConsumerFacade messageQueueConsumerFacade : consumerMap.get(queue.getQueneName())) {
            try {
                ConsumeStatauEnum status = messageQueueConsumerFacade.consume(value);
                if (status == ConsumeStatauEnum.retry) {
                    fail = true;
                }
            } catch (RuntimeException e) {
                exception = e;
                fail = true;
            }
        }
        if (!fail) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        throw exception;
    }

    protected void consumeAndFailRetry(String value, Channel channel, Message message,
                                       SwitchEnum retryConfig,
                                       MQQueueEnum queue) throws IOException {
        LOG.info("收到rabbitmq消息 queue:{}, message:{}", queue.getDelayQueneName(), value);

        boolean fail = false;
        for (MessageQueueConsumerFacade messageQueueConsumerFacade : consumerMap.get(queue.getQueneName())) {
            try {
                ConsumeStatauEnum status = messageQueueConsumerFacade.consume(value);
                if (status == ConsumeStatauEnum.retry) {
                    fail = true;
                }
            } catch (Exception e) {
                fail = true;
            }
        }
        if (!fail) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        Integer retryCount;
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        if (!headers.containsKey(RETRY_COUNT)) {
            retryCount = 0;
        } else {
            retryCount = (Integer) headers.get(RETRY_COUNT);
        }
        //判断是否满足最大重试次数
        int maxRetryTimes = retryConfig.getInt();
        if (retryCount++ < maxRetryTimes) {
            headers.put("retry-count", retryCount);
            //重新发送到MQ中
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties()
                    .builder().contentType("text/plain").headers(headers).build();
            channel.basicPublish(RabbitRegisterConfiguration.DEAD_LETTER_EXCHANGE,
                    queue.getDelayQueneName(), basicProperties,
                    message.getBody());
            LOG.warn("消费失败,将消息延迟 {} 毫秒后,重新投递到队列:{}, 当前:retryCount:{}, maxRetryCount:{}, message:{}",
                    queue.getDelayMillSeconds(),
                    queue.getQueneName(),
                    (retryCount - 1),
                    maxRetryTimes, value);
        } else {
            LOG.warn("消费失败,达到最大重试次数,无法重新投递到队列:{}, 当前:retryCount:{}, maxRetryCount:{}, message:{}",
                    queue.getQueneName(),
                    (retryCount - 1),
                    maxRetryTimes, value);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}