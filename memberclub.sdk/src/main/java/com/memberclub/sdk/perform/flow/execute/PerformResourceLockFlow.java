/**
 * @(#)MemberResourcesLockFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.execute;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.perform.service.domain.MemberTradeLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PerformResourceLockFlow extends FlowNode<PerformContext> {

    @Autowired
    private MemberTradeLockService memberTradeLockService;


    @Override
    public void process(PerformContext context) {
        memberTradeLockService.lockOnPrePerform(context);
    }


    @Override
    public void success(PerformContext context) {
        memberTradeLockService.unlockOnPerformSuccess(context);
    }

    @Override
    public void rollback(PerformContext context, Exception e) {
        memberTradeLockService.unlockOnPerformFail(context);
    }
}