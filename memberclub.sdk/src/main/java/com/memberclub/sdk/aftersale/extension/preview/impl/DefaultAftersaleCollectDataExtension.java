/**
 * @(#)DefaultAftersaleCollectDataExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.aftersale.extension.preview.AftersaleCollectDataExtension;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认售后数据收集扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
})
public class DefaultAftersaleCollectDataExtension implements AftersaleCollectDataExtension {


    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Autowired
    private PerformDomainService performDomainService;

    @Override
    public AftersalePreviewContext collect(AfterSalePreviewCmd cmd) {
        AftersalePreviewContext context = new AftersalePreviewContext();

        MemberOrderDO memberOrder = memberOrderDomainService.getMemberOrderDO(cmd.getUserId(), cmd.getTradeId());

        context.setMemberOrder(memberOrder);
        context.setSubOrders(memberOrder.getSubOrders());

        List<MemberPerformItemDO> items = performDomainService.queryByTradeId(cmd.getUserId(), cmd.getTradeId());
        context.setPerformItems(items);
        context.setPayPriceFen(memberOrder.getPaymentInfo().getPayAmountFen());
        context.setDigestVersion(cmd.getDigestVersion());

        return context;
    }
}