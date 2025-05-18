/**
 * @(#)RetryMessage.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.retry;

import lombok.Data;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * author: 掘金五阳
 */
@Data
public class RetryMessage implements Delayed {

    private String beanName;

    private String beanClassName;

    private String methodName;

    private List<String> argsList;

    private List<String> argsClassList;

    private int retryTimes;

    private boolean fallbackEnabled;

    private int maxRetryTimes;

    private long expectedTime;

    private boolean throwException;

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expectedTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return ((RetryMessage) this).getExpectedTime() < ((RetryMessage) o).getExpectedTime() ?
                -1 : 1;
    }
}