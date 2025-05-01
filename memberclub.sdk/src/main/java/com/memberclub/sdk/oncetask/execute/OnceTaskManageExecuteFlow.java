/**
 * @(#)OnceTaskRepositoryFlow.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.execute;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.oncetask.execute.OnceTaskExecuteContext;
import com.memberclub.sdk.oncetask.trigger.OnceTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class OnceTaskManageExecuteFlow extends FlowNode<OnceTaskExecuteContext> {

    @Autowired
    private OnceTaskDomainService onceTaskDomainService;

    @Override
    public void process(OnceTaskExecuteContext context) {
        onceTaskDomainService.onStartExecute(context, context.getOnceTask());
    }

    @Override
    public void success(OnceTaskExecuteContext context) {
        onceTaskDomainService.onExecuteSuccess(context, context.getOnceTask());
    }

    @Override
    public void rollback(OnceTaskExecuteContext context, Exception e) {
        onceTaskDomainService.onExecuteFail(context, context.getOnceTask());
    }
}