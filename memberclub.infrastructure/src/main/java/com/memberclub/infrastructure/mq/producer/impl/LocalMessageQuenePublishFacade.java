/**
 * @(#)LocalMessageQueneFacade.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq.producer.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.mq.*;
import lombok.Data;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * author: 掘金五阳
 */

@ConditionalOnProperty(name = "memberclub.infrastructure.mq", havingValue = "local", matchIfMissing = false)
@Service
public class LocalMessageQuenePublishFacade implements MessageQuenePublishFacade, MessageQueueDebugFacade {

    public static final Logger LOG = LoggerFactory.getLogger(LocalMessageQuenePublishFacade.class);


    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Map<String, List<MessageQueueConsumerFacade>> consumerMap = Maps.newHashMap();

    private Map<String, List<String>> topic2Msgs = Maps.newHashMap();

    private DelayQueue<DelayMessage> delayQueue = new DelayQueue<DelayMessage>();

    @PostConstruct
    public void init() {
        Map<String, MessageQueueConsumerFacade> consumers = null;
        try {
            consumers = ApplicationContextUtils.getContext().getBeansOfType(MessageQueueConsumerFacade.class);
        } catch (Exception e) {
        }
        if (MapUtils.isNotEmpty(consumers)) {
            for (Map.Entry<String, MessageQueueConsumerFacade> entry : consumers.entrySet()) {
                MQQueueEnum queueEnums = entry.getValue().register();
                consumerMap.putIfAbsent(queueEnums.getTopicName(), Lists.newArrayList());
                consumerMap.get(queueEnums.getTopicName()).add(entry.getValue());
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        DelayMessage delayMessage = delayQueue.take();

                        executorService.submit(() -> consume(delayMessage.getTopic(), delayMessage.getMessage()));
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    @Override
    public List<String> getMessage(String topic) {
        return topic2Msgs.get(topic);
    }

    @Override
    public void resetMsgs(String topic) {
        topic2Msgs.remove(topic);
    }

    @Retryable(throwException = false)
    @Override
    public void publish(MQTopicEnum event, String message) {
        //executorService.execute(() -> {
        topic2Msgs.putIfAbsent(event.toString(), Lists.newArrayList());
        topic2Msgs.get(event.toString()).add(message);
        if (event == MQTopicEnum.TRADE_PAYMENT_TIMEOUT_EVENT) {
            DelayMessage delayMessage = new DelayMessage();
            delayMessage.setMessage(message);
            delayMessage.setExpectedTime(TimeUtil.now() + SwitchEnum.WAIT_PAY_TIME_SECONDS.getInt() * 1000);
            delayMessage.setTopic(event);
            delayQueue.add(delayMessage);
            CommonLog.warn("本地模式创建延迟消息 delayMessage: {}", delayMessage);
            return;
        }

        consume(event, message);
    }

    private void consume(MQTopicEnum event, String message) {
        if (consumerMap.containsKey(event.toString())) {
            for (MessageQueueConsumerFacade messageQueueConsumerFacade : consumerMap.get(event.toString())) {
                LOG.info("本地local 模式收到消息:{}", message);
                messageQueueConsumerFacade.consume(message);
            }
        }
    }


    @Data
    static class DelayMessage implements Delayed {

        private MQTopicEnum topic;
        private String message;
        private long expectedTime;

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expectedTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return ((DelayMessage) this).getExpectedTime() < ((DelayMessage) o).getExpectedTime() ?
                    -1 : 1;
        }
    }

}