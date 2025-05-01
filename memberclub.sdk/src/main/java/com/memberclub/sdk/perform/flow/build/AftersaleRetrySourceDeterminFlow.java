/**
 * @(#)CalculateUpstreamRetryFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.RetrySourceEunm;
import com.memberclub.domain.context.perform.PerformContext;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleRetrySourceDeterminFlow extends FlowNode<PerformContext> {

    @Override
    public void process(PerformContext context) {
        if (context.getCmd().getRetryTimes() == 0) {//上游调用
            if (context.getMemberOrderStartPerformUpdateCount() == 0) {//未更新记录,说明非首次请求,直接返回本次结果
                context.setRetrySource(RetrySourceEunm.UPSTREAM_RETRY);
            } else {
                context.setRetrySource(RetrySourceEunm.UPSTREAM_FIRST);
            }
        } else {
            context.setRetrySource(RetrySourceEunm.SELF_RETRY);
        }
    }
}