/**
 * @(#)RealtimeCalculateUsageFlow.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.preview;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.aftersale.contant.UsageTypeCalculateTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * author: 掘金五阳
 * 实时计算使用类型
 */
@Service
public class AfterSaleUsageAmountCompute4RealtimeFlow extends FlowNode<AfterSalePreviewContext> {

    @Autowired
    private AfterSaleAmountService afterSaleAmountService;

    @Override
    public void process(AfterSalePreviewContext context) {
        if (context.getCurrentSubOrderDO() == null) {
            context.setCurrentSubOrderDO(context.getSubOrders().get(0));
        }
        context.setUsageTypeCalculateType(UsageTypeCalculateTypeEnum.USE_AMOUNT);

        Map<String, ItemUsage> itemToken2ItemUsage = afterSaleAmountService.buildUsage(context);
        context.setCurrentBatchCode2ItemUsage(itemToken2ItemUsage);
        context.getItemToken2ItemUsage().putAll(itemToken2ItemUsage);

        int recommendRefundPrice = context.getRecommendRefundPrice();
        recommendRefundPrice += afterSaleAmountService.recommendRefundPrice(context);

        context.setRecommendRefundPrice(recommendRefundPrice);
    }
}