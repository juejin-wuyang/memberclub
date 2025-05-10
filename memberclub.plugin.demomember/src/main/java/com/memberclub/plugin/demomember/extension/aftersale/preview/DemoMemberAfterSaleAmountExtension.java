/**
 * @(#)DefaultAfterSaleAmountExtension.java, 一月 26, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.aftersale.preview;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.sdk.aftersale.extension.preview.impl.DefaultPriceDividedAftersaleAmountExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 售后金额计算扩展点", bizScenes = {@Route(bizType = BizTypeEnum.DEMO_MEMBER)})
public class DemoMemberAfterSaleAmountExtension extends DefaultPriceDividedAftersaleAmountExtension {
    @Autowired
    private AfterSaleAmountService aftersaleAmountService;

    @Override
    public RefundWayEnum computeRefundWay(AfterSalePreviewContext context) {
        return aftersaleAmountService.calculateRefundWayUnSupportPortionRefund(context);
    }
}