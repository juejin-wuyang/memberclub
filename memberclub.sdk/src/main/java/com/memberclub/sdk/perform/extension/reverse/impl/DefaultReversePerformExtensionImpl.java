/**
 * @(#)DefaultReversePerformExtensionImpl.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse.impl;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.perform.extension.reverse.ReversePerformExtension;
import com.memberclub.sdk.perform.flow.period.reverse.PeriodPerformTaskCancelFlow;
import com.memberclub.sdk.perform.flow.reverse.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "逆向履约流程扩展", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultReversePerformExtensionImpl implements ReversePerformExtension {

    private FlowChain<ReversePerformContext> reversePerformChain;

    private FlowChain<ReversePerformContext> subReversePerformChain;

    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void init() {
        subReversePerformChain = FlowChain.newChain(flowChainService, ReversePerformContext.class)
                .addNode(SubOrderReversePerformFlow.class)//修改子单的履约状态
                .addNode(PeriodPerformTaskCancelFlow.class)// 取消周期履约任务
                .addNodeWithSubNodes(PerformItemReverse4MutilPeriodCardFlow.class, ReversePerformContext.class,
                        Lists.newArrayList(PerformItemReverseFlow.class,//逆向履约项
                                AssetsReverseFlow.class))//// 逆向资产
        ;

        reversePerformChain = FlowChain.newChain(flowChainService, ReversePerformContext.class)
                .addNode(ReversePerformContextInitializeFlow.class)//构建逆向履约信息
                .addNode(MemberOrderOnReverseSuccessFlow.class)//修改主单 的履约状态)
                .addNodeWithSubNodes(ReversePerform4MutilSubOrderFlow.class, subReversePerformChain)
        //addNote 清理 Tasks
        ;
    }

    @Override
    public void reverse(ReversePerformContext context) {
        flowChainService.execute(reversePerformChain, context);
    }
}