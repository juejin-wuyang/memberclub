package com.memberclub.plugin.douyinpkg.extension.purchase;


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
import com.memberclub.sdk.purchase.flow.aftersale.PurchaseMemberQuotaReverseFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseOrderCancelFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseResourceLockCancelFlow;
import com.memberclub.sdk.purchase.flow.cancel.PurchaseUserQuotaCancelFlow;
import com.memberclub.sdk.purchase.flow.discount.PurchaseAmountComputeSubmitFlow;
import com.memberclub.sdk.purchase.flow.payment.PrePaySubmitFlow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 购买提单扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.HOMEPAGE_SUBMIT_SCENE})
})
public class DouyinPkgPurchaseExtension implements PurchaseExtension {

    private static FlowChain<PurchaseSubmitContext> submitChain = null;
    private static FlowChain<AfterSaleApplyContext> purchaseReverseChain = null;
    private static FlowChain<PurchaseCancelContext> purchaseCancelFlowChain = null;
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @PostConstruct
    public void init() {
        submitChain = FlowChain.newChain(PurchaseSubmitContext.class)
                .addNode(PurchaseResourceLockSubmitFlow.class)
                .addNode(PurchaseContextInitalizeSubmitFlow.class)
                .addNode(PurchaseSubmitCmdValidateSubmitFlow.class)
                .addNode(PurchaseUserQuotaSubmitFlow.class)                       //检查限额
                //.addNode(PurchaseValidateInventoryFlow.class)                 //检查库存
                .addNode(PurchaseMemberOrderBuildSubmitFlow.class)              //订单构建
                .addNode(PurchaseAmountComputeSubmitFlow.class)                 //优惠计算
                .addNode(PurchaseMemberOrderSubmitFlow.class)                   //订单创建
                //.addNode(PurchaseMarkNewMemberFlow.class)                     //新会员标记
                //.addNode(PurchaseOperateInventoryFlow.class)                  //扣减库存
                .addNode(PrePaySubmitFlow.class)
        ;

        purchaseReverseChain = FlowChain.newChain(AfterSaleApplyContext.class)
                //.addNode(PurchaseReverseNewMemberFlow.class)
                //.addNode(PurchaseReverseInventoryFlow.class)
                .addNode(PurchaseMemberQuotaReverseFlow.class)
        //
        ;

        purchaseCancelFlowChain = FlowChain.newChain(PurchaseCancelContext.class)
                .addNode(PurchaseResourceLockCancelFlow.class)
                .addNode(PurchaseOrderCancelFlow.class)
                //.addNode(PurchaseCancelNewMemberFlow.class)
                .addNode(PurchaseUserQuotaCancelFlow.class)
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