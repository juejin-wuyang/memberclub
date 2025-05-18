/**
 * @(#)DemoMemberAfterSaleApplyExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.aftersale;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.sdk.aftersale.extension.apply.AfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.extension.apply.BaseAfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.flow.apply.*;
import com.memberclub.sdk.aftersale.flow.doapply.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "示例会员售后受理扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.SCENE_AFTERSALE_MONTH_CARD})
})
public class DemoMemberAfterSaleApplyExtension extends BaseAfterSaleApplyExtension implements AfterSaleApplyExtension {


    FlowChain<AfterSaleApplyContext> applyFlowChain = null;

    FlowChain<AfterSaleApplyContext> checkFlowChain = null;

    FlowChain<AfterSaleApplyContext> executeFlowChain = null;

    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        super.init();
        applyFlowChain = FlowChain.newChain(flowChainService, AfterSaleApplyContext.class)
                .addNode(AfterSaleResourceLockFlow.class)     //加锁
                .addNode(AftersaleApplyPreviewFlow.class)       //售后预览
                .addNode(AfterSalePreviewTokenCheckFlow.class)    //校验售后计划摘要
                .addNode(AftersaleOrderGenerateFlow.class)      //生成售后单
                .addNode(AftersaleOrderApplyFlow.class)
                .addNode(AfterSaleAsyncExecuteFlow.class)
        ;

        executeFlowChain = FlowChain.newChain(flowChainService, AfterSaleApplyContext.class)
                .addNode(AftersaleCompletedFlow.class)
                .addNode(AfterSaleReversePerformFlow.class)
                .addNode(AfterSalePayOrderRefundFlow.class)
                .addNode(AftersaleReversePurchaseFlow.class)
        //.addNode()
        ;
    }

    @Override
    public void apply(AfterSaleApplyContext context) {
        flowChainService.execute(applyFlowChain, context);
    }

    @Override
    public void execute(AfterSaleApplyContext context) {
        flowChainService.execute(executeFlowChain, context);
    }

    @Override
    public void customBuildAftersaleOrder(AfterSaleApplyContext context, AftersaleOrderDO aftersaleOrderDO) {

    }
}