/**
 * @(#)MutilPeriodMemberCopyPerformItemFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.google.common.collect.Lists;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class PerformItemClone4MutilPeriodCardFlow extends FlowNode<PerformContext> {


    @Override
    public void process(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {

            Map<RightTypeEnum, List<MemberPerformItemDO>> rightType2Items =
                    subOrderPerformContext.getImmediatePerformItems().stream().collect(Collectors.groupingBy(MemberPerformItemDO::getRightType));

            List<MemberPerformItemDO> performItems = Lists.newArrayList();
            List<MemberPerformItemDO> delayPerformItems = Lists.newArrayList();
            for (Map.Entry<RightTypeEnum, List<MemberPerformItemDO>> entry : rightType2Items.entrySet()) {
                for (MemberPerformItemDO immediatePerformItem : entry.getValue()) {
                    for (long i = immediatePerformItem.getCycle(); i > 0; i--) {
                        MemberPerformItemDO temp = PerformConvertor.INSTANCE.copyPerformItem(immediatePerformItem);
                        temp.setPhase((int) i);
                        if (i == 1) {
                            performItems.add(temp);
                        } else {
                            delayPerformItems.add(temp);
                        }
                    }
                }
            }
            subOrderPerformContext.setImmediatePerformItems(performItems);
            subOrderPerformContext.setDelayPerformItems(delayPerformItems);
        }
    }
}