/**
 * @(#)ReversePerformPerformHisFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.reverse;

import com.google.common.collect.Lists;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.flow.SkipException;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.memberorder.domain.MemberSubOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class SubOrderReversePerformFlow extends FlowNode<ReversePerformContext> {

    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    @Override
    public void process(ReversePerformContext context) {
        if (context.getCurrentSubOrderReversePerformContext() == null) {
            context.setCurrentSubOrderReversePerformContext(
                    Lists.newArrayList(context.getSubTradeId2SubOrderReversePerformContext().values()).get(0));
        }
        if (memberSubOrderDomainService.isFinishReverseMemberSubOrder(context, context.getCurrentSubOrderReversePerformContext())) {
            throw new SkipException("已经完成更新履约单,不再重试");
        }
        memberSubOrderDomainService.onStartReversePerform(context, context.getCurrentSubOrderReversePerformContext());
    }

    @Override
    public void success(ReversePerformContext context) {

        memberSubOrderDomainService.onReversePerformSuccess(context, context.getCurrentSubOrderReversePerformContext());
    }
}