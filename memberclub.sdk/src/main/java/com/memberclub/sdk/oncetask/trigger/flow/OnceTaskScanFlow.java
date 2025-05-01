/**
 * @(#)OnceTaskScanTableFlow.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.trigger.flow;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerJobContext;
import com.memberclub.sdk.oncetask.trigger.OnceTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class OnceTaskScanFlow extends FlowNode<OnceTaskTriggerJobContext> {

    @Autowired
    private OnceTaskDomainService onceTaskDomainService;

    @Override
    public void process(OnceTaskTriggerJobContext context) {
        onceTaskDomainService.scanTasks(context, (tasks) -> {
            onceTaskDomainService.execute(context, tasks);
        });
    }
}