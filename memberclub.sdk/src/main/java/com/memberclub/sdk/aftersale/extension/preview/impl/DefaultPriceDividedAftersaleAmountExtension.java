/**
 * @(#)PriceDividedAftersaleAmountExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.sdk.aftersale.extension.preview.AftersaleAmountExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "价格除法计算售后金额", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultPriceDividedAftersaleAmountExtension implements AftersaleAmountExtension {

    @Autowired
    private AfterSaleAmountService aftersaleAmountService;

    @Override
    public int computeRefundPrice(AfterSalePreviewContext context, Map<String, ItemUsage> batchCode2ItemUsageMap) {
        return aftersaleAmountService.unusePriceDividePayPriceToCacluateRefundPrice(
                context.getCurrentSubOrderDO().getActPriceFen(), batchCode2ItemUsageMap);
    }

    @Override
    public void computeUsageTypeByAmount(AfterSalePreviewContext context) {
        aftersaleAmountService.calculateUsageTypeByAmount(context);
    }

    @Override
    public RefundWayEnum computeRefundWay(AfterSalePreviewContext context) {
        return aftersaleAmountService.computeRefundWaySupportPortionRefund(context);
    }
}