package com.memberclub.plugin.lesson.extension.purchase;


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
import com.memberclub.sdk.purchase.flow.aftersale.PurchaseReverseMemberQuotaFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseCancelLockFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseCancelOrderFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseCancelQuotaFlow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "Lesson 购买提单扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.LESSON, scenes = {SceneEnum.HOMEPAGE_SUBMIT_SCENE})
})
public class LessonPurchaseExtension implements PurchaseExtension {

    private static FlowChain<PurchaseSubmitContext> submitChain = null;
    private static FlowChain<AfterSaleApplyContext> purchaseReverseChain = null;
    private static FlowChain<PurchaseCancelContext> purchaseCancelFlowChain = null;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @PostConstruct
    public void init() {
        submitChain = FlowChain.newChain(PurchaseSubmitContext.class)
                .addNode(PurchaseSubmitLockFlow.class)
                .addNode(SkuInfoInitalSubmitFlow.class)
                .addNode(PurchaseSubmitCmdValidateFlow.class)
                .addNode(PurchaseUserQuotaFlow.class)                       //检查限额
                .addNode(AftersaleFrequnceValidateFlow.class)               //检查售后频率
                //.addNode(PurchaseValidateInventoryFlow.class)               //检查库存
                .addNode(MemberOrderSubmitFlow.class)                       // 会员提单
                //.addNode(PurchaseMarkNewMemberFlow.class)                   //新会员标记
                //.addNode(PurchaseOperateInventoryFlow.class)                //扣减库存
                .addNode(CommonOrderSubmitFlow.class)                       //订单系统提单
        ;

        purchaseReverseChain = FlowChain.newChain(AfterSaleApplyContext.class)
                //.addNode(PurchaseReverseNewMemberFlow.class)
                //.addNode(PurchaseReverseInventoryFlow.class)
                .addNode(PurchaseReverseMemberQuotaFlow.class)
        //
        ;

        purchaseCancelFlowChain = FlowChain.newChain(PurchaseCancelContext.class)
                .addNode(PurchaseCancelLockFlow.class)
                .addNode(PurchaseCancelOrderFlow.class)
                //.addNode(PurchaseCancelNewMemberFlow.class)
                .addNode(PurchaseCancelQuotaFlow.class)
        //.addNode(PurchaseCancelInventoryFlow.class)
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