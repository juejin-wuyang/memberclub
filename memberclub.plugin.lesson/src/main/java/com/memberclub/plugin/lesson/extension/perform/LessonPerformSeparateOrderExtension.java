/**
 * @(#)DefaultBuildPerformContextExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.lesson.extension.perform;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.flow.FlowChainService;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.perform.extension.build.PerformSeparateOrderExtension;
import com.memberclub.sdk.perform.flow.build.*;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "在线课程履约拆单扩展点实现类", bizScenes = {
        @Route(bizType = BizTypeEnum.LESSON, scenes = {SceneEnum.SCENE_MONTH_CARD}),
})
public class LessonPerformSeparateOrderExtension implements PerformSeparateOrderExtension {

    FlowChain<PerformContext> performSeparateOrderChain = null;


    @Autowired
    private FlowChainService flowChainService;

    @Autowired
    private PerformDomainService performDomainService;

    @PostConstruct
    public void run() throws Exception {
        performSeparateOrderChain = FlowChain.newChain(flowChainService, PerformContext.class)
                .addNode(InitialSkuPerformContextsFlow.class)
                .addNode(MutilBuyCountClonePerformItemFlow.class)
                //如果年卡周期是自然月,则可以在此处根据当前期数计算每期的天数
                .addNode(CalculateImmediatePerformItemPeriodFlow.class)//计算立即履约项 时间周期
                .addNode(CalculateOrderPeriodFlow.class)//计算订单整体有效期
                .addNode(PerformContextExtraInfoBuildFlow.class)// 构建扩展属性
        ;
    }

    @Override
    public void separateOrder(PerformContext context) {
        flowChainService.execute(performSeparateOrderChain, context);
    }

    /**
     * 取履约项最大时间和最小时间 作为整笔订单的有效期。
     *
     * @param context
     */
    @Override
    public void buildTimeRange(PerformContext context) {
        performDomainService.buildTimeRangeOnPerformBaseMaxMin(context);
    }
}