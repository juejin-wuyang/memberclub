/**
 * @(#)RabbitmqPublishFacadeImpl.java, 一月 14, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq.producer.impl;

import com.memberclub.common.retry.Retryable;
import com.memberclub.infrastructure.mq.MQTopicEnum;
import com.memberclub.infrastructure.mq.MessageQuenePublishFacade;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.mq", havingValue = "rabbitmq", matchIfMissing = true)
@Configuration
public class RabbitmqPublishFacadeImpl implements MessageQuenePublishFacade {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Retryable(throwException = false)
    public void publish(MQTopicEnum event, String message) {
        rabbitTemplate.convertAndSend(event.toString(), "*", message);
    }
}