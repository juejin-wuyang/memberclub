/**
 * @(#)DemoMemberPerformExecuteExtension.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.perform;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.delay.DelayItemContext;
import com.memberclub.sdk.perform.extension.execute.PerformExecuteExtension;
import com.memberclub.sdk.perform.flow.complete.PerformMessagePublishFlow;
import com.memberclub.sdk.perform.flow.execute.*;
import com.memberclub.sdk.perform.flow.execute.delay.DelayPerformFlow;
import com.memberclub.sdk.perform.flow.execute.delay.PerformDelayTaskBuildFlow;
import com.memberclub.sdk.perform.flow.execute.delay.PerformDelayTaskCreateFlow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 执行履约扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.SCENE_MONTH_CARD}),//Demo会员月卡,多份数,
})
public class DemoMemberPerformExecuteExtension implements PerformExecuteExtension {
    private FlowChain<PerformContext> flowChain;

    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        flowChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(PerformResourceLockFlow.class)
                .addNode(MemberOrderOnPerformSuccessFlow.class)
                .addNode(PerformMessagePublishFlow.class)
                .addNode(SingleSubOrderPerformFlow.class)
                .addNodeWithSubNodes(ImmediatePerformFlow.class, PerformItemContext.class,
                        // 构建 MemberPerformItem, 发放权益
                        ImmutableList.of(PerformItemCreateFlow.class, PerformItemGrantFlow.class))
                .addNodeWithSubNodes(DelayPerformFlow.class, DelayItemContext.class,
                        // 构建 任务, 存储任务
                        ImmutableList.of(PerformDelayTaskBuildFlow.class, PerformDelayTaskCreateFlow.class))

        ;
    }

    @Override
    public void execute(PerformContext context) {
        flowChainService.execute(flowChain, context);
    }
}