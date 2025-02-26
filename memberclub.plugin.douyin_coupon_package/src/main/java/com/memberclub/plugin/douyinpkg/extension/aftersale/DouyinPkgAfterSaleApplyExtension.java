package com.memberclub.plugin.douyinpkg.extension.aftersale;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.sdk.aftersale.extension.apply.AfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.flow.apply.*;
import com.memberclub.sdk.aftersale.flow.doapply.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "示例会员售后受理扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.SCENE_AFTERSALE_MONTH_CARD})
})
public class DouyinPkgAfterSaleApplyExtension implements AfterSaleApplyExtension {


    FlowChain<AfterSaleApplyContext> applyFlowChain = null;

    FlowChain<AfterSaleApplyContext> checkFlowChain = null;

    FlowChain<AfterSaleApplyContext> doApplyFlowChain = null;

    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        applyFlowChain = FlowChain.newChain(flowChainService, AfterSaleApplyContext.class)
                .addNode(AftersaleApplyLockFlow.class)     //加锁
                .addNode(AftersaleApplyPreviewFlow.class)       //售后预览
                .addNode(AfterSalePlanDigestCheckFlow.class)    //校验售后计划摘要
                .addNode(AftersaleGenerateOrderFlow.class)      //生成售后单
                .addNode(AftersaleDoApplyFlow.class)
        ;

        doApplyFlowChain = FlowChain.newChain(flowChainService, AfterSaleApplyContext.class)
                .addNode(AftersaleOrderDomainFlow.class)
                .addNode(MemberOrderRefundSuccessFlow.class) //售后成功后, 更新主单子单的状态为成功
                .addNode(AftersaleAsyncRollbackFlow.class)
                .addNode(AftersaleReversePerformFlow.class)
                .addNode(AftersaleReversePurchaseFlow.class)
                .addNode(AftersaleRefundOrderFlow.class)
        //.addNode()
        ;


    }

    @Override
    public void apply(AfterSaleApplyContext context) {
        flowChainService.execute(applyFlowChain, context);
    }

    @Override
    public void doApply(AfterSaleApplyContext context) {
        flowChainService.execute(doApplyFlowChain, context);
    }

    @Override
    public void customBuildAftersaleOrder(AfterSaleApplyContext context, AftersaleOrderDO aftersaleOrderDO) {

    }
}
