/**
 * @(#)MessageQueneFacade.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mq;

/**
 * author: 掘金五阳
 */
public interface MessageQuenePublishFacade {

    /**
     * 实现类应该添加 Retryable 注解
     *
     * @param event
     * @param message
     */
    public void publish(MQTopicEnum event, String message);
    
}
