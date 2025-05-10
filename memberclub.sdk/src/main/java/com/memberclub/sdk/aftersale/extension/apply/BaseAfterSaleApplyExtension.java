package com.memberclub.sdk.aftersale.extension.apply;

import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.sdk.aftersale.flow.apply.AfterSalePaymentRefundFlow;
import com.memberclub.sdk.aftersale.flow.apply.AfterSaleResourceLockFlow;
import com.memberclub.sdk.aftersale.flow.apply.AftersaleApplyPreviewFlow;
import com.memberclub.sdk.aftersale.flow.apply.AftersaleOrderGenerateFlow;
import com.memberclub.sdk.aftersale.flow.doapply.AftersaleOrderApplyFlow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class BaseAfterSaleApplyExtension implements AfterSaleApplyExtension {


    @Autowired
    protected FlowChainService flowChainService;
    FlowChain<AfterSaleApplyContext> apply4OnlyRefundMoneyFlowChain = null;

    @PostConstruct
    public void init() {
        apply4OnlyRefundMoneyFlowChain = FlowChain.newChain(flowChainService, AfterSaleApplyContext.class)
                .addNode(AfterSaleResourceLockFlow.class)       //加锁
                .addNode(AftersaleApplyPreviewFlow.class)       //售后预览
                .addNode(AftersaleOrderGenerateFlow.class)      //生成售后单
                .addNode(AftersaleOrderApplyFlow.class)         //调用订单退款
                .addNode(AfterSalePaymentRefundFlow.class)      //调用支付原路退款
        ;
    }


    @Override
    public void apply4OnlyRefundMoney(AfterSaleApplyContext context) {
        apply4OnlyRefundMoneyFlowChain.execute(context);
    }

    @Override
    public void customBuildAftersaleOrder(AfterSaleApplyContext context, AftersaleOrderDO aftersaleOrderDO) {

    }
}
