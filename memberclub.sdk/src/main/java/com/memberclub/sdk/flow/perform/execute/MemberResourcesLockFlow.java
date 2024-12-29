/**
 * @(#)MemberResourcesLockFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.flow.perform.execute;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.service.perform.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberResourcesLockFlow extends FlowNode<PerformContext> {
    
    @Autowired
    private LockService lockService;

    @Override
    public void process(PerformContext context) {
        String lockValue = lockService.lock(context.getBizType(),
                context.getLockValue(),
                context.getUserId(),
                context.getTradeId());
        context.setLockValue(lockValue);
        context.getCmd().setLockValue(context.getLockValue());
    }


    @Override
    public void success(PerformContext context) {
        lockService.unlock(context.getBizType(),
                context.getUserId(),
                context.getTradeId(),
                context.getLockValue());
    }

    @Override
    public void rollback(PerformContext context) {
        lockService.rollbackLock(context.getBizType(),
                context.getUserId(),
                context.getTradeId(),
                context.getLockValue(),
                context.getRetryTimes());
    }
}