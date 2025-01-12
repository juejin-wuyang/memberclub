/**
 * @(#)MemberSubOrderDomainService.java, 一月 08, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder.domain;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.context.perform.common.SubOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.MemberSubOrder;
import com.memberclub.infrastructure.mybatis.mappers.MemberSubOrderDao;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
import com.memberclub.sdk.memberorder.extension.MemberSubOrderDomainExtension;
import com.memberclub.sdk.util.TransactionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author: 掘金五阳
 */
@Service
public class MemberSubOrderDomainService {

    @Autowired
    private MemberSubOrderDao memberSubOrderDao;

    @Autowired
    private ExtensionManager extensionManager;

    public void onSubmitSuccess(MemberOrderDO memberOrderDO) {
        for (MemberSubOrderDO subOrder : memberOrderDO.getSubOrders()) {
            LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
            subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                    .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                    .set(MemberSubOrder::getStatus, subOrder.getStatus().getCode())
                    .set(MemberSubOrder::getActPriceFen, subOrder.getActPriceFen())
                    .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                    .set(MemberSubOrder::getOrderId, subOrder.getOrderId())
                    .set(MemberSubOrder::getUtime, TimeUtil.now());

            extensionManager.getExtension(BizScene.of(subOrder.getBizType()),
                    MemberSubOrderDomainExtension.class).onSubmitSuccess(subOrder, subOrderWrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void onStartPerform(PerformContext performContext, SubOrderPerformContext subOrderPerformContext) {
        MemberSubOrderDO subOrder = subOrderPerformContext.getSubOrder();
        subOrder.onStartPerform(subOrderPerformContext);

        LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                .set(MemberSubOrder::getStatus, subOrder.getStatus().getCode())
                .set(MemberSubOrder::getPerformStatus, subOrder.getPerformStatus().getCode())
                .set(MemberSubOrder::getStime, subOrder.getStime())
                .set(MemberSubOrder::getEtime, subOrder.getEtime())
                .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                .set(MemberSubOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(subOrder.getBizType()), MemberSubOrderDomainExtension.class)
                .onStartPerform(performContext, subOrderPerformContext, subOrder, subOrderWrapper);
    }

    @Autowired
    private TradeEventDomainService tradeEventDomainService;

    @Transactional(rollbackFor = Exception.class)
    public void onPerformSuccess(PerformContext performContext,
                                 SubOrderPerformContext subOrderPerformContext,
                                 MemberSubOrderDO subOrder) {
        subOrder.onPerformSuccess(subOrderPerformContext);

        LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                .set(MemberSubOrder::getStatus, subOrder.getStatus().getCode())
                .set(MemberSubOrder::getPerformStatus, subOrder.getPerformStatus().getCode())
                .set(MemberSubOrder::getStime, subOrder.getStime())
                .set(MemberSubOrder::getEtime, subOrder.getEtime())
                .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                .set(MemberSubOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(subOrder.getBizType()), MemberSubOrderDomainExtension.class)
                .onPerformSuccess(performContext, subOrderPerformContext, subOrder, subOrderWrapper);

        TransactionHelper.afterCommitExecute(() -> {
            tradeEventDomainService.onPerformSuccessForSubOrder(performContext, subOrderPerformContext, subOrder);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void onStartReversePerform(ReversePerformContext context,
                                      SubOrderReversePerformContext subOrderReversePerformContext) {
        MemberSubOrderDO subOrder = subOrderReversePerformContext.getMemberSubOrder();
        subOrder.onStartReversePerform(context, subOrderReversePerformContext);
        LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                .set(MemberSubOrder::getPerformStatus, subOrder.getPerformStatus().getCode())
                .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                .set(MemberSubOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(subOrder.getBizType()), MemberSubOrderDomainExtension.class)
                .onStartReversePerform(context, subOrderReversePerformContext, subOrder, subOrderWrapper);
    }

    private SubOrderPerformStatusEnum generateMemberSubOrderFinishReverseStatus(ReversePerformContext context) {
        boolean allRefund = context.getCurrentSubOrderReversePerformContext().isAllRefund();

        return allRefund ? SubOrderPerformStatusEnum.COMPLETED_REVERSED :
                SubOrderPerformStatusEnum.PORTION_REVERSED;
    }

    public boolean isFinishReverseMemberSubOrder(ReversePerformContext context,
                                                 SubOrderReversePerformContext info) {
        SubOrderPerformStatusEnum status = generateMemberSubOrderFinishReverseStatus(context);

        MemberSubOrder his = memberSubOrderDao.selectBySkuId(context.getUserId(), context.getTradeId(), info.getSkuId());
        if (his.getPerformStatus() == status.getCode()) {
            CommonLog.info("已经完成更新履约单 [{}]", his);
            return true;
        }
        return false;
    }


    @Transactional(rollbackFor = Exception.class)
    public void onReversePerformSuccess(ReversePerformContext context,
                                        SubOrderReversePerformContext subOrderReversePerformContext) {
        MemberSubOrderDO subOrder = subOrderReversePerformContext.getMemberSubOrder();
        subOrder.onReversePerformSuccess(subOrderReversePerformContext.isAllRefund());

        LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                .set(MemberSubOrder::getPerformStatus, subOrder.getPerformStatus().getCode())
                .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                .set(MemberSubOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(subOrder.getBizType()), MemberSubOrderDomainExtension.class)
                .onReversePerformSuccess(context, subOrderReversePerformContext, subOrder, subOrderWrapper);

        TransactionHelper.afterCommitExecute(() -> {
            tradeEventDomainService.onReversePerformSuccessForSubOrder(context, subOrderReversePerformContext, subOrder);
        });
    }

    public void onRefundSuccess(AfterSaleApplyContext context,
                                MemberSubOrderDO subOrder) {
        LambdaUpdateWrapper<MemberSubOrder> subOrderWrapper = new LambdaUpdateWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, subOrder.getUserId())
                .eq(MemberSubOrder::getSubTradeId, subOrder.getSubTradeId())
                .set(MemberSubOrder::getStatus, subOrder.getStatus().getCode())
                .set(MemberSubOrder::getExtra, JsonUtils.toJson(subOrder.getExtra()))
                .set(MemberSubOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(subOrder.getBizType()), MemberSubOrderDomainExtension.class)
                .onRefundSuccess(context, subOrder, subOrderWrapper);
        
        TransactionHelper.afterCommitExecute(() -> {
            tradeEventDomainService.onRefundSuccessForSubOrder(context, subOrder);
        });
    }

}