/**
 * @(#)DefaultPerformContextBuildExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.extension.perform.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionImpl;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.dataobject.perform.PerformCmd;
import com.memberclub.domain.dataobject.perform.PerformContext;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.sdk.extension.perform.PreBuildPerformContextExtension;
import com.memberclub.sdk.flow.perform.build.CalculateRetrySourceFlow;
import com.memberclub.sdk.flow.perform.build.CheckMemberOrderPerformedFlow;
import com.memberclub.sdk.flow.perform.build.StartPerformUpdteMemberOrderFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * author: 掘金五阳
 */
@ExtensionImpl(desc = "默认履约上下文前置构建", bizScenes = {@Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = SceneEnum.DEFAULT_SCENE)})
public class DefaultPreBuildPrePerformContextExtension implements PreBuildPerformContextExtension, ApplicationRunner {

    FlowChain<PerformContext> preBuidPerformContextChain = null;


    @Autowired
    private FlowChainService flowChainService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        preBuidPerformContextChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(StartPerformUpdteMemberOrderFlow.class)
                .addNode(CalculateRetrySourceFlow.class)
                .addNode(CheckMemberOrderPerformedFlow.class);
    }

    @Override
    public PerformContext preBuild(PerformCmd cmd) {
        PerformContext context = PerformConvertor.INSTANCE.toPerformContext(cmd);

        flowChainService.execute(preBuidPerformContextChain, context);

        return context;
    }

}