/**
 * @(#)ReversePerformItemFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.reverse;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.flow.SkipException;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.perform.service.domain.MemberPerformItemDomainService;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PerformItemReverseFlow extends FlowNode<ReversePerformContext> {

    @Autowired
    private PerformDomainService performDomainService;

    @Autowired
    private MemberPerformItemDomainService memberPerformItemDomainService;

    @Override
    public void process(ReversePerformContext context) {
        List<PerformItemReverseInfo> items = context.getCurrentSubOrderReversePerformContext().getCurrentItems();
        if (items == null) {
            items = context.getCurrentSubOrderReversePerformContext().getItems();
            context.getCurrentSubOrderReversePerformContext().setCurrentItems(items);
        }

        if (memberPerformItemDomainService.isFinishReversePerform(context,
                context.getCurrentSubOrderReversePerformContext(), items)) {
            CommonLog.warn("已经完成逆向履约,无需再次重试");
            throw new SkipException("完成逆向履约,无需再次重试");
        }

        memberPerformItemDomainService.onStartReversePerform(context, context.getCurrentSubOrderReversePerformContext(), items);

    }

    @Override
    public void success(ReversePerformContext context) {
        memberPerformItemDomainService.onReversePerformSuccess(context,
                context.getCurrentSubOrderReversePerformContext(),
                context.getCurrentSubOrderReversePerformContext().getCurrentItems());
    }
}