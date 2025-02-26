package com.memberclub.plugin.douyinpkg.extension.prefinance;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.flow.FlowChain;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.context.prefinance.common.PreFinanceEventEnum;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.event.trade.TradeEventEnum;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.sdk.prefinance.extension.PreFinanceHandleExtension;
import com.memberclub.sdk.prefinance.flow.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@ExtensionProvider(desc = "DemoMember 预结算处理扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE)
})
public class DouyinPkgPreFinanceHandleExtension implements PreFinanceHandleExtension {

    private static Table<TradeEventEnum, SubOrderPerformStatusEnum, PreFinanceEventEnum> financeEventEnumTable = HashBasedTable.create();
    private static Map<PreFinanceEventEnum, FlowChain<PreFinanceContext>> financeEventChainMap = Maps.newHashMap();

    static {
            /*financeEventEnumTable.put(TradeEventEnum.SUB_ORDER_PERFORM_SUCCESS, SubOrderPerformStatusEnum.PERFORM_SUCCESS, PreFinanceEventEnum.PERFORM);
            financeEventEnumTable.put(TradeEventEnum.SUB_ORDER_REFUND_SUCCESS, SubOrderPerformStatusEnum.COMPLETED_REVERSED, PreFinanceEventEnum.REFUND);
            financeEventEnumTable.put(TradeEventEnum.SUB_ORDER_REFUND_SUCCESS, SubOrderPerformStatusEnum.PORTION_REVERSED, PreFinanceEventEnum.REFUND);
            financeEventEnumTable.put(TradeEventEnum.SUB_ORDER_FREEZE_SUCCESS, SubOrderPerformStatusEnum.PORTION_REVERSED, PreFinanceEventEnum.FREEZE_NON_REFUND);
            financeEventEnumTable.put(TradeEventEnum.SUB_ORDER_PERIOD_PERFORM_SUCCESS, SubOrderPerformStatusEnum.PERFORM_SUCCESS, PreFinanceEventEnum.PERFORM);
            financeEventEnumTable.put(TradeEventEnum.MEMBER_PERFORM_ITEM_EXPIRE, SubOrderPerformStatusEnum.PERFORM_SUCCESS, PreFinanceEventEnum.EXPIRE);
        */
    }

    @PostConstruct
    public void init() {
        FlowChain<PreFinanceContext> performChain = FlowChain.newChain(PreFinanceContext.class)
                .addNode(PreFinanceBuildMemberOrderFlow.class)
                .addNode(PreFinanceBuildMemberAssetsFlow.class)
                .addNode(PreFinanceQueryAssetsFlow.class)
                .addNode(PreFinancePublishEventFlow.class)
                .addNode(PreFinanceCreateExpiredTaskFlow.class)
                //
                ;


        FlowChain<PreFinanceContext> freezeChain = FlowChain.newChain(PreFinanceContext.class)
                .addNode(PreFinanceBuildMemberOrderFlow.class)
                .addNode(PreFinanceBuildMemberAssetsFlow.class)
                .addNode(PreFinanceQueryAssetsFlow.class)
                .addNode(PreFinancePublishEventFlow.class)
                //.addNode()
                ;


        FlowChain<PreFinanceContext> refundChain = FlowChain.newChain(PreFinanceContext.class)
                .addNode(PreFinanceBuildMemberOrderFlow.class)
                .addNode(PreFinanceBuildMemberAssetsFlow.class)
                .addNode(PreFinanceQueryAssetsFlow.class)
                .addNode(PreFinancePublishEventFlow.class)
                //.addNode()
                ;

        FlowChain<PreFinanceContext> expireChain = FlowChain.newChain(PreFinanceContext.class)
                .addNode(PreFinanceBuildMemberOrderFlow.class)
                .addNode(PreFinanceBuildMemberAssetsFlow.class)
                .addNode(PreFinanceQueryAssetsFlow.class)
                .addNode(PreFinancePublishEventFlow.class)
                //.addNode()
                ;

        financeEventChainMap.put(PreFinanceEventEnum.PERFORM, performChain);
        financeEventChainMap.put(PreFinanceEventEnum.FREEZE_NON_REFUND, freezeChain);
        financeEventChainMap.put(PreFinanceEventEnum.REFUND, refundChain);
        financeEventChainMap.put(PreFinanceEventEnum.EXPIRE, expireChain);
    }


    @Override
    @UserLog(domain = LogDomainEnum.PRE_FINANCE, userId = "detail.userId", tradeId = "detail.tradeId", bizType = "detail.bizType")
    public void handle(TradeEventDO event) {
        PreFinanceEventEnum preFinanceEventEnum = financeEventEnumTable.get(event.getEventType(), event.getDetail().getPerformStatus());
        if (preFinanceEventEnum == null) {
            CommonLog.info("该事件无需预结算处理 eventType:{}, performStatus:{}", event.getEventType(), event.getDetail().getPerformStatus());
            return;
        }

        PreFinanceContext context = buildPreFinanceContext(event, preFinanceEventEnum);

        FlowChain<PreFinanceContext> preFinanceChain = financeEventChainMap.get(preFinanceEventEnum);
        if (preFinanceChain == null) {
            CommonLog.error("没有找到预结算流程 FinanceEventEnum:{}, event:{} ", preFinanceEventEnum, event);
            throw ResultCode.INTERNAL_ERROR.newException(String.format("没有找到预结算流程:%s", preFinanceEventEnum));
        }
        CommonLog.info("预结算开始受理 FinanceEventEnum:{}, event:{}", preFinanceEventEnum, event);
        preFinanceChain.execute(context);
    }

    private PreFinanceContext buildPreFinanceContext(TradeEventDO event, PreFinanceEventEnum preFinanceEventEnum) {
        PreFinanceContext context = new PreFinanceContext();
        context.setEvent(event);
        context.setPreFinanceEventEnum(preFinanceEventEnum);
        context.setBizType(event.getDetail().getBizType());
        context.setSkuId(event.getDetail().getSkuId());
        context.setSubTradeId(event.getDetail().getSubTradeId().toString());
        context.setTradeId(event.getDetail().getTradeId());
        context.setUserId(event.getDetail().getUserId());
        return context;
    }
}
