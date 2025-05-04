/**
 * @(#)AftersaleDomainService.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.service.domain;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.ImmutableList;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderExtraDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderStatusEnum;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.AftersaleOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mapstruct.AftersaleConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.AftersaleOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberSubOrderDao;
import com.memberclub.sdk.aftersale.extension.domain.AftersaleDomainExtension;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.event.trade.service.domain.TradeEventDomainService;
import com.memberclub.sdk.memberorder.domain.MemberSubOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author: 掘金五阳
 */
@DS("tradeDataSource")
@Service
public class AftersaleDomainService {

    @Autowired
    private AftersaleOrderDao aftersaleOrderDao;

    @Autowired
    private AftersaleDataObjectFactory aftersaleDataObjectFactory;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MemberOrderDao memberOrderDao;

    @Autowired
    private MemberSubOrderDao memberSubOrderDao;

    @Autowired
    private TradeEventDomainService tradeEventDomainService;
    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    public AftersaleOrderDO generateOrder(AfterSaleApplyContext context) {
        AftersaleOrderDO order = AftersaleConvertor.INSTANCE.toAftersaleOrderDO(context.getCmd());
        order.setActPayPriceFen(context.getPreviewContext().getPayPriceFen());
        order.setActRefundPriceFen(context.getPreviewContext().getActRefundPrice());
        order.setApplySkuInfos(context.getCmd().getApplySkus());
        order.setStatus(AftersaleOrderStatusEnum.INIT);
        order.setCtime(TimeUtil.now());
        order.setExtra(new AftersaleOrderExtraDO());
        order.getExtra().setReason(context.getCmd().getReason());
        order.getExtra().setApplySkus(order.getApplySkuInfos());
        order.setRefundType(context.getPreviewContext().getRefundType());
        order.setRecommendRefundPriceFen(context.getPreviewContext().getRecommendRefundPrice());
        order.setRefundWay(context.getPreviewContext().getRefundWay());
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createAfterSaleOrder(AftersaleOrderDO orderDO) {
        AftersaleOrder order = AftersaleConvertor.INSTANCE.toAftersaleOrder(orderDO);
        int cnt = aftersaleOrderDao.insertIgnoreBatch(ImmutableList.of(order));

        if (cnt < 1) {
            AftersaleOrder orderFromDb = aftersaleOrderDao.queryById(order.getUserId(), order.getId());
            if (orderFromDb != null) {
                CommonLog.warn("新增售后单幂等成功 orderFromDb:{}, orderNew:{}", orderFromDb, order);
                Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(), "insert", "duplicated");
                return;
            } else {
                CommonLog.error("新增售后单失败  orderNew:{}", order);
                Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(), "insert", "error");
                throw ResultCode.DATA_UPDATE_ERROR.newException("新增售后单失败");
            }
        } else {
            CommonLog.info("新增售后单成功:{}", order);
            Monitor.AFTER_SALE_DOAPPLY.counter(order.getBizType(), "insert", "succ");
        }
        return;
    }

    public AftersaleOrderDO queryAftersaleOrder(long userId, Long afterSaleId) {
        AftersaleOrder order = aftersaleOrderDao.queryById(userId, afterSaleId);
        if (order == null) {
            return null;
        }
        return aftersaleDataObjectFactory.buildAftersaleOrderDO(order);
    }

    @Transactional
    public void onPerformReversed(AfterSaleApplyContext context) {
        AftersaleOrderDO order = context.getAftersaleOrderDO();
        order.onPerformReversed(context);
        int cnt = aftersaleOrderDao.updateStatus(order.getUserId(),
                order.getId(),
                order.getStatus().getCode(),
                TimeUtil.now());
    }

    @Transactional
    public void onPurchaseReversed(AfterSaleApplyContext context) {
        AftersaleOrderDO order = context.getAftersaleOrderDO();
        order.onPurchaseReversed(context);
        int cnt = aftersaleOrderDao.updateStatus(order.getUserId(),
                order.getId(),
                order.getStatus().getCode(),
                TimeUtil.now());
    }

    @Transactional(rollbackFor = Exception.class)
    public void onOrderRefunded(AfterSaleApplyContext context) {
        AftersaleOrderDO order = context.getAftersaleOrderDO();
        order.onOrderRefunfSuccess(context);

        LambdaUpdateWrapper<AftersaleOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AftersaleOrder::getUserId, order.getUserId())
                .eq(AftersaleOrder::getId, order.getId())
                .set(AftersaleOrder::getActRefundPriceFen, order.getActRefundPriceFen())
                .set(AftersaleOrder::getExtra, JsonUtils.toJson(order.getExtra()))
                .set(AftersaleOrder::getStatus, order.getStatus().getCode())
                .set(AftersaleOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(order.getBizType()),
                AftersaleDomainExtension.class).onRefundSuccess(context, order, wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void onAftersaleSuccess(AfterSaleApplyContext context, AftersaleOrderDO order) {
        order.onAfterSaleSuccess(context);
        LambdaUpdateWrapper<AftersaleOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AftersaleOrder::getUserId, order.getUserId())
                .eq(AftersaleOrder::getId, order.getId())
                .set(AftersaleOrder::getStatus, order.getStatus().getCode())
                .set(AftersaleOrder::getUtime, order.getUtime())
        ;

        extensionManager.getExtension(BizScene.of(order.getBizType()),
                AftersaleDomainExtension.class).onSuccess(context, order, wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void onRefundSuccessForMemberOrder(AfterSaleApplyContext context) {
        if (!Boolean.TRUE.equals(context.getPayOrderRefundInvokeSuccess())) {
            CommonLog.info("没有调用订单退款,因此不修改主状态");
            for (MemberSubOrderDO subOrder : context.getPreviewContext().getSubOrders()) {
                memberSubOrderDomainService.onJustFreezeSuccess(context, subOrder);
            }
            return;
        }
        CommonLog.info("调用成功订单退款, 开始修改 MemberOrder/MemberSubOrder 主状态");

        MemberOrderDO memberOrder = context.getPreviewContext().getMemberOrder();
        memberOrder.onRefundSuccess(context);
        memberOrderDao.updateStatus2RefundSuccess(memberOrder.getUserId(),
                memberOrder.getTradeId(),
                memberOrder.getStatus().getCode(),
                TimeUtil.now()
        );

        CommonLog.info("修改主单的主状态为{}", memberOrder.getStatus());
        for (MemberSubOrderDO subOrder : context.getPreviewContext().getSubOrders()) {
            memberSubOrderDomainService.onRefundSuccess(context, subOrder);
        }
    }
}