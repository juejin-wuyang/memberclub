/**
 * @(#)MemberOrderDomainService.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.memberorder.domain;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mapstruct.PurchaseConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberSubOrderDao;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import com.memberclub.sdk.memberorder.extension.MemberOrderRepositoryExtension;
import com.memberclub.sdk.util.TransactionHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@DS("tradeDataSource")
@Service
public class MemberOrderDomainService {


    @Autowired
    private MemberOrderDao memberOrderDao;

    @Autowired
    private MemberSubOrderDao memberSubOrderDao;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    @Autowired
    private MemberOrderDataObjectBuildFactory memberOrderDataObjectBuildFactory;

    @Autowired
    private TradeEventDomainService tradeEventDomainService;


    public void createMemberOrder(MemberOrderDO memberOrderDO) {
        MemberOrder order = memberOrderDataObjectBuildFactory.buildOrder4Create(memberOrderDO);

        List<MemberSubOrder> subOrders = memberOrderDO.getSubOrders().stream()
                .map(PurchaseConvertor.INSTANCE::toMemberSubOrder)
                .collect(Collectors.toList());

        int cnt = memberOrderDao.insertIgnoreBatch(Lists.newArrayList(order));
        if (cnt < 1) {
            OrderRemarkBuilder.builder(memberOrderDO).remark("创建订单失败").save();
            throw ResultCode.ORDER_CREATE_ERROR.newException("会员单生成失败");
        }
        OrderRemarkBuilder.builder(memberOrderDO).remark(memberOrderDO.getStatus(), "创建订单成功").save();

        int subOrderCnt = memberSubOrderDao.insertIgnoreBatch(subOrders);
        if (subOrderCnt < subOrders.size()) {
            OrderRemarkBuilder.builder(memberOrderDO).remark("创建订单子单失败").save();
            throw ResultCode.ORDER_CREATE_ERROR.newException("会员子单生成失败");
        }
        OrderRemarkBuilder.builder(memberOrderDO).remark("创建订单子单成功").save();
        CommonLog.info("生成订单数据成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(throwException = false)
    public void onSubmitSuccess(MemberOrderDO order) {
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getStatus, order.getStatus().getCode())
                .set(MemberOrder::getActPriceFen, order.getActPriceFen())
                .set(MemberOrder::getExtra, JsonUtils.toJson(order.getExtra()))
                .set(MemberOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(order.getBizType()),
                MemberOrderRepositoryExtension.class).onSubmitSuccess(order, wrapper);

        memberSubOrderDomainService.onSubmitSuccess(order);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order).remark(order.getStatus(), "提交订单成功").save();
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Retryable(throwException = false)
    public void onSubmitCancel(PurchaseCancelContext context, MemberOrderDO order) {
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getStatus, order.getStatus().getCode())
                .set(MemberOrder::getExtra, JsonUtils.toJson(order.getExtra()))
                .set(MemberOrder::getUtime, TimeUtil.now());

        extensionManager.getExtension(BizScene.of(order.getBizType()),
                MemberOrderRepositoryExtension.class).onSubmitCancel(order, wrapper);

        memberSubOrderDomainService.onSubmitCancel(context, order);
        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order).remark(order.getStatus(), "取消订单成功").save();
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer onStartPerform(PerformContext context) {
        context.getMemberOrder().onStartPerform(context);
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, context.getUserId())
                .eq(MemberOrder::getTradeId, context.getTradeId())
                .lt(MemberOrder::getPerformStatus, context.getMemberOrder().getPerformStatus().getCode())
                .set(MemberOrder::getPerformStatus, context.getMemberOrder().getPerformStatus().getCode())
                .set(MemberOrder::getUtime, TimeUtil.now());

        int cnt = extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderRepositoryExtension.class).onStartPerform(context, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(context.getMemberOrder())
                    .remark(context.getMemberOrder().getPerformStatus(), "开始履约").save();
        });
        return cnt;
    }

    @Transactional(rollbackFor = Exception.class)
    public void onPerformSuccess(PerformContext context, MemberOrderDO order) {
        order.onPerformSuccess(context);

        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getStatus, order.getStatus().getCode())
                .set(MemberOrder::getPerformStatus, order.getPerformStatus().getCode())
                .set(MemberOrder::getStime, order.getStime())
                .set(MemberOrder::getEtime, order.getEtime())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderRepositoryExtension.class).onPerformSuccess(context, order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(context.getMemberOrder())
                    .remark(context.getMemberOrder().getPerformStatus(), "履约成功").save();
        });
    }


    @Retryable(throwException = false)
    @Transactional(rollbackFor = Exception.class)
    public void submitFail(MemberOrderDO order) {
        // TODO: 2025/1/4

    }

    public void onPrePay(PurchaseSubmitContext context, MemberOrderDO order) {
        order.onPrePay(context);
        //更新数据库
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getPayTradeNo, order.getPaymentInfo().getPayTradeNo())
                .set(MemberOrder::getPayNodeType, order.getPaymentInfo().getPayNodeType().getName())
                .set(MemberOrder::getPayOnlineType, order.getPaymentInfo().getPayOnlineType().getName())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderRepositoryExtension.class).onPrePay(order, wrapper);
        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(context.getMemberOrder())
                    .remark(context.getMemberOrder().getPaymentInfo().getPayStatus(), "预支付").save();
        });
    }


    /**
     * 订单支付成功后，发现订单已取消那么原路退款
     */
    @Retryable(throwException = false)
    public void onRefund4OrderTimeout(PaymentNotifyContext context, MemberOrderDO order) {
        order.onRefund4OrderTimeout(context);

        //更新数据库
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        MemberOrderRepositoryExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderRepositoryExtension.class);
        extension.onRefund4OrderTimeout(order, wrapper);
        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order)
                    .remark(order.getPaymentInfo().getPayStatus(), "支付后发现订单已取消，对订单原路退款").save();
        });
    }

    @Retryable(throwException = false)
    public void onPaySuccess4OrderTimeout(PaymentNotifyContext context, MemberOrderDO order) {
        order.onPaySuccessOnPayment(context);

        //更新数据库
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getPayAccount, order.getPaymentInfo().getPayAccount())
                .set(MemberOrder::getPayAccountType, order.getPaymentInfo().getPayAccountType())
                .set(MemberOrder::getPayChannelType, order.getPaymentInfo().getPayChannelType())
                .set(MemberOrder::getPayTime, order.getPaymentInfo().getPayTime())
                .set(MemberOrder::getActPriceFen, order.getActPriceFen())
                .set(MemberOrder::getPayAmountFen, order.getPaymentInfo().getPayAmountFen())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        MemberOrderRepositoryExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderRepositoryExtension.class);
        extension.onPaySuccess4OrderTimeout(order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order)
                    .remark(order.getPaymentInfo().getPayStatus(), "支付后发现订单超时").save();
        });
    }

    @Retryable(throwException = false)
    public void onPaySuccess(PaymentNotifyContext context, MemberOrderDO order) {
        order.onPaySuccessOnPayment(context);
        order.onPaySuccessOnStatus(context);

        //更新数据库
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();

        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getStatus, order.getStatus().getCode())
                .set(MemberOrder::getPayAccount, order.getPaymentInfo().getPayAccount())
                .set(MemberOrder::getPayAccountType, order.getPaymentInfo().getPayAccountType())
                .set(MemberOrder::getPayChannelType, order.getPaymentInfo().getPayChannelType())
                .set(MemberOrder::getPayTime, order.getPaymentInfo().getPayTime())
                .set(MemberOrder::getActPriceFen, order.getActPriceFen()) //重写应付金额，保持和实付金额一致！
                .set(MemberOrder::getPayAmountFen, order.getPaymentInfo().getPayAmountFen())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        MemberOrderRepositoryExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderRepositoryExtension.class);
        extension.onPaySuccess(order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            //发布支付事件
            OrderRemarkBuilder.builder(order)
                    .remark(order.getStatus(), order.getPaymentInfo().getPayStatus(), "支付成功").save();
            tradeEventDomainService.publishEventOnPaySuccess(context);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void onReversePerformSuccess(ReversePerformContext context) {
        MemberOrderDO order = context.getMemberOrderDO();
        order.onReversePerformSuccess(context);

        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .lt(MemberOrder::getPerformStatus, order.getPerformStatus().getCode())
                .set(MemberOrder::getPerformStatus, order.getPerformStatus().getCode())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderRepositoryExtension.class).onReversePerformSuccess(context, order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order)
                    .remark(order.getPerformStatus(), "订单逆向履约成功").save();
        });
    }

    @Retryable
    public void onJustFreezeSuccess(AfterSaleApplyContext context, MemberOrderDO order) {
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            memberSubOrderDomainService.onJustFreezeSuccess(context, subOrder);
        }
    }

    @Retryable(throwException = false)
    @Transactional(rollbackFor = Exception.class)
    public void onPurchaseReverseSuccess(AfterSaleApplyContext context) {
        CommonLog.info("成功支付退款, 开始修改 MemberOrder/MemberSubOrder 主状态");

        MemberOrderDO memberOrder = context.getMemberOrder();
        memberOrder.onPurchaseReverseSuccess(context);
        memberOrderDao.updateStatus2RefundSuccess(memberOrder.getUserId(),
                memberOrder.getTradeId(),
                memberOrder.getStatus().getCode(),
                TimeUtil.now()
        );

        CommonLog.info("修改主单的主状态为{}", memberOrder.getStatus());
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            memberSubOrderDomainService.onPurchaseReverseSuccess(context, subOrder);
        }
        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(memberOrder).remark(memberOrder.getStatus(), "订单逆向购买完成").save();
        });
    }

    @Retryable
    @Transactional(rollbackFor = Exception.class)
    public void onPayRefundSuccess(AfterSaleApplyContext context) {
        if (!Boolean.TRUE.equals(context.getPayOrderRefundInvokeSuccess())) {
            CommonLog.info("没有调用支付退款,因此不修改支付状态");
            return;
        }
        MemberOrderDO order = context.getMemberOrder();
        order.onPayRefundSuccess(context);

        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .le(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getPayStatus, order.getPaymentInfo().getPayStatus().getCode())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(context.getApplyCmd().getBizType()),
                MemberOrderRepositoryExtension.class).onPayRefundSuccess(context, order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            OrderRemarkBuilder.builder(order).remark(order.getPaymentInfo().getPayStatus(), "支付退款成功").save();
        });
    }

    public MemberOrderDO getMemberOrderDO(long userId, String tradeId) {
        MemberOrder order = memberOrderDao.selectByTradeId(userId, tradeId);
        if (order == null) {
            return null;
        }

        List<MemberSubOrder> subOrders = memberSubOrderDao.selectByTradeId(userId, tradeId);

        MemberOrderDO memberOrderDO = memberOrderDataObjectBuildFactory.buildMemberOrderDO(order, subOrders);

        return memberOrderDO;
    }

    public List<MemberOrderDO> queryPayedOrders(long userId) {
        LambdaQueryWrapper<MemberOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberOrder::getUserId, userId);
        wrapper.gt(MemberOrder::getPerformStatus, MemberOrderPerformStatusEnum.INIT.getCode());
        wrapper.orderByDesc(MemberOrder::getCtime);

        List<MemberOrder> orders = memberOrderDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(orders)) {
            return Lists.newArrayList();
        }

        List<String> tradeIds = CollectionUtilEx.mapToList(orders, MemberOrder::getTradeId);

        LambdaQueryWrapper<MemberSubOrder> subOrderWrapper = new LambdaQueryWrapper<>();
        subOrderWrapper.eq(MemberSubOrder::getUserId, userId);
        subOrderWrapper.in(MemberSubOrder::getTradeId, tradeIds);

        List<MemberSubOrder> subOrders = memberSubOrderDao.selectList(subOrderWrapper);
        Map<String, List<MemberSubOrder>> tradeId2SubOrders = CollectionUtilEx.groupingBy(subOrders, MemberSubOrder::getTradeId);
        List<MemberOrderDO> memberOrderDOS = Lists.newArrayList();
        for (MemberOrder order : orders) {
            List<MemberSubOrder> subOrderList = tradeId2SubOrders.get(order.getTradeId());
            MemberOrderDO memberOrderDO = memberOrderDataObjectBuildFactory.buildMemberOrderDO(order, subOrderList);
            memberOrderDOS.add(memberOrderDO);
        }
        return memberOrderDOS;
    }

    public MemberOrderDO getMemberOrderDO(long userId, String tradeId, Long subTradeId) {
        MemberOrderDO memberOrderDO = getMemberOrderDO(userId, tradeId);
        if (memberOrderDO != null && CollectionUtils.isNotEmpty(memberOrderDO.getSubOrders())) {
            memberOrderDO.setSubOrders(
                    memberOrderDO.getSubOrders().stream()
                            .filter(sbo -> NumberUtils.compare(sbo.getSubTradeId(), subTradeId) == 0)
                            .collect(Collectors.toList())
            );
        }

        return memberOrderDO;
    }
}