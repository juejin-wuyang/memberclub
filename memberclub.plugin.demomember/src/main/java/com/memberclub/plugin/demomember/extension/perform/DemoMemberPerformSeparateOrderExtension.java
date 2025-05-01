/**
 * @(#)DefaultBuildPerformContextExtension.java, 十二月 15, 2024.
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
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.perform.extension.build.PerformSeparateOrderExtension;
import com.memberclub.sdk.perform.flow.build.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 履约上下文构建", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.SCENE_MONTH_CARD}),
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.SCENE_MONTH_CARD}),
})
public class DemoMemberPerformSeparateOrderExtension implements PerformSeparateOrderExtension {

    FlowChain<PerformContext> performSeparateOrderChain = null;


    @Autowired
    private FlowChainService flowChainService;

    @PostConstruct
    public void run() throws Exception {
        performSeparateOrderChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(PerformContextsInitializeFlow.class)       // 履约上下文初始化
                .addNode(PerformItemClone4BuyCountFlow.class)       // 履约项克隆，用于多份数购买场景
                //如果年卡周期是自然月,则可以在此处根据当前期数计算每期的天数
                .addNode(PeriodCompute4ImmediatePerformItemFlow.class)//计算立即履约项 时间周期
                .addNode(PeriodCompute4WholeOrderFlow.class)        //计算订单整体有效期
                .addNode(PerformContextExtraInfoBuildFlow.class)    // 构建扩展属性
        ;
    }

    @Override
    public void separateOrder(PerformContext context) {
        flowChainService.execute(performSeparateOrderChain, context);
    }

}