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
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.dataobject.payment.context.PaymentNotifyContext;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mapstruct.PurchaseConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberSubOrderDao;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import com.memberclub.sdk.memberorder.extension.MemberOrderDomainExtension;
import com.memberclub.sdk.util.TransactionHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.memberclub.domain.common.MemberTradeEvent.MEMBER_ORDER_START_PERFORM;

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
            throw ResultCode.ORDER_CREATE_ERROR.newException("会员单生成失败");
        }

        int subOrderCnt = memberSubOrderDao.insertIgnoreBatch(subOrders);
        if (subOrderCnt < subOrders.size()) {
            throw ResultCode.ORDER_CREATE_ERROR.newException("会员子单生成失败");
        }
        CommonLog.info("生成会员单数据成功");
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
                MemberOrderDomainExtension.class).onSubmitSuccess(order, wrapper);

        memberSubOrderDomainService.onSubmitSuccess(order);
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
                MemberOrderDomainExtension.class).onSubmitCancel(order, wrapper);

        memberSubOrderDomainService.onSubmitCancel(context, order);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer onStartPerform(PerformContext context) {
        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, context.getUserId())
                .eq(MemberOrder::getTradeId, context.getTradeId())
                .lt(MemberOrder::getPerformStatus, MEMBER_ORDER_START_PERFORM.getToStatus())
                .set(MemberOrder::getPerformStatus, MEMBER_ORDER_START_PERFORM.getToStatus())
                .set(MemberOrder::getUtime, TimeUtil.now());

        int cnt = extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderDomainExtension.class).onStartPerform(context, wrapper);
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
                MemberOrderDomainExtension.class).onPerformSuccess(context, order, wrapper);
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
                MemberOrderDomainExtension.class).onPrePay(order, wrapper);
    }


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

        MemberOrderDomainExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderDomainExtension.class);
        extension.onRefund4OrderTimeout(order, wrapper);
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

        MemberOrderDomainExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderDomainExtension.class);
        extension.onPaySuccess4OrderTimeout(order, wrapper);
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

        MemberOrderDomainExtension extension = extensionManager.getExtension(BizScene.of(context.getBizType()), MemberOrderDomainExtension.class);
        extension.onPaySuccess(order, wrapper);

        TransactionHelper.afterCommitExecute(() -> {
            //发布支付事件
            tradeEventDomainService.publishEventOnPaySuccess(context);
        });
    }

    public void onPayTimeoutCheck(MemberOrderDO order) {

    }

    @Transactional(rollbackFor = Exception.class)
    public void onReversePerformSuccess(ReversePerformContext context) {
        MemberOrderDO order = context.getMemberOrderDO();
        order.onReversePerformSuccess(context);

        LambdaUpdateWrapper<MemberOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberOrder::getUserId, order.getUserId())
                .eq(MemberOrder::getTradeId, order.getTradeId())
                .set(MemberOrder::getPerformStatus, order.getPerformStatus().getCode())
                .set(MemberOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                MemberOrderDomainExtension.class).onReversePerformSuccess(context, order, wrapper);
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