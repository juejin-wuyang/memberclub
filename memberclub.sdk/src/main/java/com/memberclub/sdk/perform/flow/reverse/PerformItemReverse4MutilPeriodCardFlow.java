/**
 * @(#)MutilReversePerformItemFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.reverse;

import com.memberclub.common.flow.FlowChainService;
import com.memberclub.common.flow.SubFlowNode;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class PerformItemReverse4MutilPeriodCardFlow extends SubFlowNode<ReversePerformContext, ReversePerformContext> {

    @Autowired
    private FlowChainService flowChainService;

    @Override
    public void process(ReversePerformContext context) {
        Map<Integer, List<PerformItemReverseInfo>> rightType2Items = context.getCurrentSubOrderReversePerformContext().getItems().stream()
                .collect(Collectors.groupingBy(PerformItemReverseInfo::getRightType));
        for (Map.Entry<Integer, List<PerformItemReverseInfo>> entry : rightType2Items.entrySet()) {
            context.getCurrentSubOrderReversePerformContext().setCurrentRightType(entry.getKey());
            context.getCurrentSubOrderReversePerformContext().setCurrentItems(entry.getValue());
            flowChainService.execute(getSubChain(), context);
        }
    }
}