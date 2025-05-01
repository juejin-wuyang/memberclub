/**
 * @(#)CreateDelayPerformOnceTaskFlow.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.execute.delay;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.delay.DelayItemContext;
import com.memberclub.sdk.oncetask.periodperform.service.PeriodPerformTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PerformDelayTaskCreateFlow extends FlowNode<DelayItemContext> {

    @Autowired
    private PeriodPerformTaskDomainService periodPerformTaskDomainService;

    @Override
    public void process(DelayItemContext context) {
        periodPerformTaskDomainService.onCreatePeriodPerformTask(context);
    }
}