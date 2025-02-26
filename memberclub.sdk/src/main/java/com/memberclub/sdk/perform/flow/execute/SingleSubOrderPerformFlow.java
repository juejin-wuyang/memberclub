/**
 * @(#)SingleMemberPerformHisFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.execute;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.sdk.memberorder.domain.MemberSubOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class SingleSubOrderPerformFlow extends FlowNode<PerformContext> {

    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    @Override
    public void process(PerformContext context) {
        if (context.getCurrentSubOrderPerformContext() == null) {
            SubOrderPerformContext subOrderPerformContext = context.getSubOrderPerformContexts().get(0);
            context.setCurrentSubOrderPerformContext(subOrderPerformContext);
        }

        memberSubOrderDomainService.onStartPerform(context, context.getCurrentSubOrderPerformContext());
    }


    @Override
    public void success(PerformContext context) {
        /***
         * 注意这里 应该把扩展点 全部放在 MemberSubOrderDomainExtension
         *
         */
        SubOrderPerformContext subOrderPerformContext = context.getCurrentSubOrderPerformContext();

        memberSubOrderDomainService.onPerformSuccess(context, subOrderPerformContext, subOrderPerformContext.getSubOrder());
    }
}