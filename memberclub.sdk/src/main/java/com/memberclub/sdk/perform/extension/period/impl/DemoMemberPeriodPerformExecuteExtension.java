/**
 * @(#)DefaultPeriodPerformExecuteExtension.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.period.impl;

import com.google.common.collect.ImmutableList;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.dataobject.task.perform.PerformTaskContentDO;
import com.memberclub.sdk.perform.extension.period.PeriodPerformExecuteExtension;
import com.memberclub.sdk.perform.flow.complete.PeriodPerformMessageFlow;
import com.memberclub.sdk.perform.flow.execute.PerformItemCreateFlow;
import com.memberclub.sdk.perform.flow.execute.PerformItemGrantFlow;
import com.memberclub.sdk.perform.flow.period.PeriodPerformImmediatePerformFlow;
import com.memberclub.sdk.perform.flow.period.PeriodPerformResourceLockFlow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "周期履约执行扩展点实现", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER)
})
public class DemoMemberPeriodPerformExecuteExtension implements PeriodPerformExecuteExtension {

    @Autowired
    private FlowChainService chainService;

    private FlowChain<PeriodPerformContext> flowChain = null;

    @PostConstruct
    public void init() {
        flowChain = FlowChain.newChain(chainService, PeriodPerformContext.class)
                .addNode(PeriodPerformResourceLockFlow.class)//周期履约加锁
                .addNode(PeriodPerformMessageFlow.class)//构建周期履约消息
                //编排周期履约流程,按权益类型分别履约
                .addNodeWithSubNodes(PeriodPerformImmediatePerformFlow.class, PerformItemContext.class,
                        ImmutableList.of(PerformItemCreateFlow.class, PerformItemGrantFlow.class));

    }

    @Override
    public void periodPerform(PeriodPerformContext context) {
        chainService.execute(flowChain, context);
    }


    @Override
    public void buildContext(OnceTaskDO task, PeriodPerformContext context) {
        context.setContent(((PerformTaskContentDO) task.getContent()));
        context.setSkuId(context.getContent().getSkuId());
        context.setTradeId(context.getContent().getTradeId());
        context.setSubTradeId(String.valueOf(context.getContent().getSubTradeId()));
    }
}