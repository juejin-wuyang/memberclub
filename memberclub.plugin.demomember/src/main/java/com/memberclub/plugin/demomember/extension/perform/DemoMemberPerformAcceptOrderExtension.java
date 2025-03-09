/**
 * @(#)DefaultPerformContextBuildExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.perform;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.sdk.perform.extension.build.PerformAcceptOrderExtension;
import com.memberclub.sdk.perform.flow.build.CalculateRetrySourceFlow;
import com.memberclub.sdk.perform.flow.build.CheckMemberOrderPerformedFlow;
import com.memberclub.sdk.perform.flow.build.StartPerformUpdteMemberOrderFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 履约上下文前置构建", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = SceneEnum.DEFAULT_SCENE),
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = SceneEnum.DEFAULT_SCENE),
        @Route(bizType = BizTypeEnum.LESSON, scenes = SceneEnum.DEFAULT_SCENE),
})
public class DemoMemberPerformAcceptOrderExtension implements PerformAcceptOrderExtension, ApplicationRunner {

    FlowChain<PerformContext> performAcceptOrderChain = null;

    @Autowired
    private FlowChainService flowChainService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        performAcceptOrderChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(StartPerformUpdteMemberOrderFlow.class)
                .addNode(CalculateRetrySourceFlow.class)
                .addNode(CheckMemberOrderPerformedFlow.class)
        ;
    }

    @Override
    public PerformContext acceptOrder(PerformCmd cmd) {
        PerformContext context = PerformConvertor.INSTANCE.toPerformContext(cmd);
        context.setCmd(cmd);

        flowChainService.execute(performAcceptOrderChain, context);

        return context;
    }

}