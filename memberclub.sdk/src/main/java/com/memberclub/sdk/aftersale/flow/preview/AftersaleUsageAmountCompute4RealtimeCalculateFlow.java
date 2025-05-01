/**
 * @(#)RealtimeCalculateUsageFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.contant.UsageTypeCalculateTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.sdk.aftersale.service.domain.AftersaleAmountService;
import com.memberclub.sdk.aftersale.service.domain.AftersaleUsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * author: 掘金五阳
 * 实时计算使用类型
 */
@Service
public class AftersaleUsageAmountCompute4RealtimeCalculateFlow extends FlowNode<AftersalePreviewContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private AftersaleUsageService aftersaleUsageService;

    @Autowired
    private AftersaleAmountService aftersaleAmountService;

    @Override
    public void process(AftersalePreviewContext context) {
        if (context.getCurrentSubOrderDO() == null) {
            context.setCurrentSubOrderDO(context.getSubOrders().get(0));
        }
        context.setUsageTypeCalculateType(UsageTypeCalculateTypeEnum.USE_AMOUNT);

        Map<String, ItemUsage> batchCode2ItemUsage = aftersaleUsageService.buildUsageMap(context);
        context.setCurrentBatchCode2ItemUsage(batchCode2ItemUsage);
        context.getBatchCode2ItemUsage().putAll(batchCode2ItemUsage);

        int recommendRefundPrice = context.getRecommendRefundPrice();
        recommendRefundPrice += aftersaleAmountService.recommendRefundPrice(context);

        context.setRecommendRefundPrice(recommendRefundPrice);
    }
}