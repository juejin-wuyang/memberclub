/**
 * @(#)CancelPeriodPerformTaskFlow.java, 一月 11, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.period.reverse;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.oncetask.periodperform.service.PeriodPerformTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PeriodPerformTaskCancelFlow extends FlowNode<ReversePerformContext> {

    @Autowired
    private PeriodPerformTaskDomainService periodPerformTaskDomainService;

    @Override
    public void process(ReversePerformContext context) {
        periodPerformTaskDomainService.cancelActivatPeriodTasks(context,
                context.getSubTradeId2SubOrderReversePerformContext());
    }
}