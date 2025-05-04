/**
 * @(#)PerformCmd.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.RetryableContext;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class PerformCmd implements RetryableContext {

    private BizTypeEnum bizType;

    private long userId;

    /*private String orderId;*/

    /*private OrderSystemTypeEnum orderSystemType;*/

    private String tradeId;

    /*********重试相关***********/

    private int retryTimes;

    private long baseTime;

    private Long lockValue;

    /*********重试相关***********/

    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return;
    }
}