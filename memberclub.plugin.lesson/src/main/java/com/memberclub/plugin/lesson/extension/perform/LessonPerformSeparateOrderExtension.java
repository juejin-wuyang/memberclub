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
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.perform.extension.build.PerformSeparateOrderExtension;
import com.memberclub.sdk.perform.flow.build.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Comparator;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 履约上下文构建", bizScenes = {
        @Route(bizType = BizTypeEnum.LESSON, scenes = {SceneEnum.SCENE_MONTH_CARD}),
})
public class LessonPerformSeparateOrderExtension implements PerformSeparateOrderExtension {

    FlowChain<PerformContext> performSeparateOrderChain = null;


    @Autowired
    private FlowChainService flowChainService;

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

    @Override
    public void buildTimeRange(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {
            long stime = context.getBaseTime();
            long etime = context.getImmediatePerformEtime();
            if (context.getDelayPerformEtime() > 0) {
                etime = context.getDelayPerformEtime();
            }
            for (MemberPerformItemDO immediatePerformItem : subOrderPerformContext.getImmediatePerformItems()) {
                if (immediatePerformItem.getRightType() == RightTypeEnum.LESSON) {
                    stime = immediatePerformItem.getStime();
                    etime = immediatePerformItem.getEtime();
                }
            }

            subOrderPerformContext.getSubOrder().setStime(stime);
            subOrderPerformContext.getSubOrder().setEtime(etime);
        }
        long stime = context.getSubOrderPerformContexts().stream()
                .min(Comparator.comparingLong(o -> o.getSubOrder().getStime()))
                .get().getSubOrder().getStime();

        long etime = context.getSubOrderPerformContexts().stream()
                .max(Comparator.comparingLong(o -> o.getSubOrder().getEtime()))
                .get().getSubOrder().getEtime();

        context.setStime(stime);
        context.setEtime(etime);
    }
}