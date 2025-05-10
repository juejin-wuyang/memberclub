/**
 * @(#)AftersaleReverseBuyFlow.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.flow.doapply;

import com.memberclub.common.flow.FlowNode;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import com.memberclub.sdk.purchase.service.biz.PurchaseBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleReversePurchaseFlow extends FlowNode<AfterSaleApplyContext> {

    @Autowired
    private AfterSaleDomainService aftersaleDomainService;

    @Autowired
    private PurchaseBizService purchaseBizService;

    @Override
    public void process(AfterSaleApplyContext context) {
        if (context.getAftersaleOrderDO().getStatus().isPurchaseReversed()) {
            CommonLog.info("当前售后状态已完成逆向购买,不再重复执行");
            return;
        }
        CommonLog.info("开始逆向购买流程");
        purchaseBizService.reverse(context);

        aftersaleDomainService.onPurchaseReversed(context);
    }
}