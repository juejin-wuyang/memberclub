/**
 * @(#)MutilBuyCountCalculatePerformItemFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.google.common.collect.Lists;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PerformItemClone4BuyCountFlow extends FlowNode<PerformContext> {

    @Override
    public void process(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {
            int buyCount = subOrderPerformContext.getSubOrder().getBuyCount();
            if (buyCount <= 1) {
                continue;
            }
            List<MemberPerformItemDO> performItems = Lists.newArrayList();
            for (MemberPerformItemDO immediatePerformItem : subOrderPerformContext.getImmediatePerformItems()) {
                for (long i = buyCount; i > 0; i--) {
                    MemberPerformItemDO temp = PerformConvertor.INSTANCE.copyPerformItem(immediatePerformItem);
                    temp.setBuyIndex((int) i);
                    performItems.add(temp);
                }
            }
            subOrderPerformContext.setImmediatePerformItems(performItems);
        }
    }
}