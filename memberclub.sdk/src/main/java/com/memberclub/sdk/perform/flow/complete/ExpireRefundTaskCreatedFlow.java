package com.memberclub.sdk.perform.flow.complete;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.oncetask.trigger.OnceTaskDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpireRefundTaskCreatedFlow extends FlowNode<PerformContext> {

    @Autowired
    private OnceTaskDomainService onceTaskDomainService;

    @Override
    public void process(PerformContext context) {

    }

    @Override
    public void success(PerformContext context) {
        onceTaskDomainService.onCreatedExpireRefundTask(context);
    }
}