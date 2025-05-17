/**
 * @(#)AftersaleDomainService.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.service.domain;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderExtraDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderStatusEnum;
import com.memberclub.domain.entity.trade.AftersaleOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mapstruct.AftersaleConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.AftersaleOrderDao;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.sdk.aftersale.extension.domain.AfterSaleRepositoryExtension;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.memberorder.domain.MemberSubOrderDomainService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * author: 掘金五阳
 */
@DS("tradeDataSource")
@Service
public class AfterSaleDomainService {

    @Autowired
    private AftersaleOrderDao aftersaleOrderDao;

    @Autowired
    private AftersaleDataObjectFactory aftersaleDataObjectFactory;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MemberOrderDao memberOrderDao;

    @Autowired
    private MemberSubOrderDomainService memberSubOrderDomainService;

    public static void validatePeriod4ExpireRefundUnable(AfterSalePreviewContext context) {
        context.setStime(context.getMemberOrder().getStime());
        context.setEtime(context.getMemberOrder().getEtime());
        if (context.getEtime() < TimeUtil.now()) {
            CommonLog.info("当前订单已过期不能退 stime:{}, etime:{}", context.getStime(), context.getEtime());
            throw AftersaleUnableCode.EXPIRE_ERROR.newException();
        }
        CommonLog.info("当前订单有效期 stime:{}, etime:{}", context.getStime(), context.getEtime());
    }

    public static void validateStatus(AfterSalePreviewContext context) {
        MemberOrderStatusEnum status = context.getMemberOrder().getStatus();
        MemberOrderPerformStatusEnum performStatus = context.getMemberOrder().getPerformStatus();

        CommonLog.info("当前订单状态:{}", status.toString(), performStatus.toString());
        if (status == MemberOrderStatusEnum.COMPLETE_REFUNDED) {
            throw AftersaleUnableCode.REFUNDED.newException();
        }

        if (MemberOrderStatusEnum.nonPerformed(status.getCode())) {
            throw AftersaleUnableCode.NON_PERFORMED.newException();
        }

        if (MemberOrderStatusEnum.PORTION_REFUNDED == status) {
            CommonLog.info("当前会员订单已部分退可以再次申请售后");
        }
    }


    public static void generateDigest(AfterSalePreviewContext context) throws NoSuchAlgorithmException {
        List<Object> keys = Lists.newArrayList();
        keys.add(context.getCmd().getTradeId());
        keys.add(context.getRecommendRefundPrice());
        keys.add(context.getRefundType().getCode());
        keys.add(context.getRefundWay().getCode());

        String value = StringUtils.join(keys, ",");

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String digest = Base64.getUrlEncoder().encodeToString(
                messageDigest.digest(value.getBytes(Charsets.UTF_8)));
        context.setDigests(digest);
        context.setDigestVersion(1);
        CommonLog.info("生成售后计划摘要 版本:{},{}", context.getDigestVersion(), context.getDigests());
    }

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

    @Retryable
    @Transactional
    public void onPerformReversed(AfterSaleApplyContext context) {
        AftersaleOrderDO order = context.getAftersaleOrderDO();
        order.onPerformReversed(context);
        int cnt = aftersaleOrderDao.updateStatus(order.getUserId(),
                order.getId(),
                order.getStatus().getCode(),
                TimeUtil.now());
    }

    @Retryable
    @Transactional
    public void onPurchaseReverseSuccess(AfterSaleApplyContext context) {
        AftersaleOrderDO order = context.getAftersaleOrderDO();
        order.onPurchaseReversed(context);
        int cnt = aftersaleOrderDao.updateStatus(order.getUserId(),
                order.getId(),
                order.getStatus().getCode(),
                TimeUtil.now());
    }

    @Retryable
    @Transactional(rollbackFor = Exception.class)
    public void onPayRefundSuccess(AfterSaleApplyContext context) {
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
                AfterSaleRepositoryExtension.class).onRefundSuccess(context, order, wrapper);
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
                AfterSaleRepositoryExtension.class).onSuccess(context, order, wrapper);
    }

}