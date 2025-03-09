/**
 * @(#)InitialSkuPerformContextsFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.sku.SkuPerformItemConfigDO;
import com.memberclub.sdk.perform.extension.build.PerformItemCalculateExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class InitialSkuPerformContextsFlow extends FlowNode<PerformContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(PerformContext context) {
        List<SubOrderPerformContext> subOrderPerformContexts = Lists.newArrayList();
        for (MemberSubOrderDO subOrder : context.getMemberSubOrders()) {
            SubOrderPerformContext subOrderPerformContext = new SubOrderPerformContext();
            subOrderPerformContext.setPerformContext(context);
            subOrderPerformContext.setSubOrder(subOrder);
            subOrder.setUtime(TimeUtil.now());

            PerformItemCalculateExtension calculateExtension = extensionManager.getExtension(
                    context.toDefaultScene(), PerformItemCalculateExtension.class);

            List<MemberPerformItemDO> items = Lists.newArrayList();
            for (SkuPerformItemConfigDO config : subOrder.getPerformConfig().getConfigs()) {
                MemberPerformItemDO item = calculateExtension.toPerformItem(config);
                item.setSkuId(subOrder.getSkuId());
                item.setSubTradeId(String.valueOf(subOrder.getSubTradeId()));
                items.add(item);
            }
            subOrderPerformContext.setImmediatePerformItems(items);
            subOrderPerformContexts.add(subOrderPerformContext);
        }
        context.setSubOrderPerformContexts(subOrderPerformContexts);
        if (context.getBaseTime() == 0) {
            if (context.getMemberOrder().getExtra().getRenewStime() != null) {
                context.setBaseTime(context.getMemberOrder().getExtra().getRenewStime());
            } else {
                context.setBaseTime(TimeUtil.now());
            }
            context.getCmd().setBaseTime(context.getBaseTime());
        }
    }
}