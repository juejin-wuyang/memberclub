/**
 * @(#)DemoMemberPurchaseSubmitExtension.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.purchase;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.purchase.extension.PurchaseExtension;
import com.memberclub.sdk.purchase.flow.*;
import com.memberclub.sdk.purchase.flow.aftersale.PurchaseInventoryOperateReverseFlow;
import com.memberclub.sdk.purchase.flow.aftersale.PurchaseMemberQuotaReverseFlow;
import com.memberclub.sdk.purchase.flow.aftersale.PurchaseNewMemberReverseFlow;
import com.memberclub.sdk.purchase.flow.cancel.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 购买提单扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.HOMEPAGE_SUBMIT_SCENE, SceneEnum.OUTER_SUBMIT_PURCHASE_SCENE, SceneEnum.OUTER_SUBMIT_REDEEM_SCENE})
})
public class DemoMemberPurchaseExtension implements PurchaseExtension {

    private static FlowChain<PurchaseSubmitContext> submitChain = null;
    private static FlowChain<AfterSaleApplyContext> purchaseReverseChain = null;
    private static FlowChain<PurchaseCancelContext> purchaseCancelFlowChain = null;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @PostConstruct
    public void init() {
        submitChain = FlowChain.newChain(PurchaseSubmitContext.class)
                .addNode(PurchaseLockSubmitFlow.class)
                .addNode(PurchaseSkuInfoInitalSubmitFlow.class)
                .addNode(PurchaseSubmitCmdValidateSubmitFlow.class)
                .addNode(PurchaseUserQuotaSubmitFlow.class)           //检查限额
                //.addNode(PurchaseRenewValidateFlow.class)         //检查续费
                .addNode(PurchaseInventoryValidateSubmitFlow.class)   //检查库存
                .addNode(MemberOrderSubmitSubmitFlow.class)           // 会员提单
                .addNode(PurchaseNewMemberSubmitFlow.class)         //新会员标记
                .addNode(PurchaseInventoryOperateSubmitFlow.class)    //扣减库存
                .addNode(CommonOrderSubmitFlow.class)               //订单系统提单
        ;

        purchaseReverseChain = FlowChain.newChain(AfterSaleApplyContext.class)
                .addNode(PurchaseNewMemberReverseFlow.class)
                .addNode(PurchaseInventoryOperateReverseFlow.class)
                .addNode(PurchaseMemberQuotaReverseFlow.class)
        //
        ;

        purchaseCancelFlowChain = FlowChain.newChain(PurchaseCancelContext.class)
                .addNode(PurchaseLockCancelFlow.class)
                .addNode(PurchaseOrderCancelFlow.class)
                .addNode(PurchaseNewMemberCancelFlow.class)
                .addNode(PurchaseUserQuotaCancelFlow.class)
                .addNode(PurchaseInventoryCancelFlow.class)

        //
        ;
    }

    @Override
    public void submit(PurchaseSubmitContext context) {
        submitChain.execute(context);
    }

    @Override
    public void reverse(AfterSaleApplyContext context) {
        purchaseReverseChain.execute(context);
    }

    @Override
    public void cancel(PurchaseCancelContext context) {
        MemberOrderDO memberOrder = memberOrderDomainService.
                getMemberOrderDO(context.getCmd().getUserId(), context.getCmd().getTradeId());
        context.setMemberOrder(memberOrder);

        purchaseCancelFlowChain.execute(context);
    }
}