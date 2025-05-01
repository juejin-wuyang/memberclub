package com.memberclub.plugin.douyinpkg.extension.perform;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.sdk.perform.extension.execute.PerformExecuteExtension;
import com.memberclub.sdk.perform.flow.complete.ExpireRefundTaskCreatedFlow;
import com.memberclub.sdk.perform.flow.complete.PerformMessagePublishFlow;
import com.memberclub.sdk.perform.flow.execute.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 执行履约扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.SCENE_MONTH_CARD})//抖音券包月卡,多份数, 多商品
})
public class DouyinPkgPerformExecuteExtension implements PerformExecuteExtension {
    private FlowChain<PerformContext> flowChain;

    private FlowChain<PerformContext> subFlowChain;
    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        subFlowChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(SingleSubOrderPerformFlow.class)
                .addNodeWithSubNodes(ImmediatePerformFlow.class, PerformItemContext.class,
                        // 构建 MemberPerformItem, 发放权益
                        ImmutableList.of(PerformItemCreateFlow.class, PerformItemGrantFlow.class));

        flowChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(PerformResourceLockFlow.class)
                .addNode(MemberOrderOnPerformSuccessFlow.class)
                .addNode(PerformMessagePublishFlow.class)
                .addNode(ExpireRefundTaskCreatedFlow.class)
                .addNodeWithSubNodes(MutilSubOrderPerformFlow.class, subFlowChain)
        ;
    }

    @Override
    public void execute(PerformContext context) {
        flowChainService.execute(flowChain, context);
    }
}
