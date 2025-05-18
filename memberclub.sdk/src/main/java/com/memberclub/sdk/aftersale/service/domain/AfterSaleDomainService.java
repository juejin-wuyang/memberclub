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
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.apply.AfterSaleExecuteCmd;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.contant.UsageTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCoreResult;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.context.perform.common.MemberOrderPerformStatusEnum;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderExtraDO;
import com.memberclub.domain.dataobject.aftersale.AftersaleOrderStatusEnum;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.AftersaleOrder;
import com.memberclub.domain.exception.AftersaleExecuteException;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import com.memberclub.infrastructure.id.IdTypeEnum;
import com.memberclub.infrastructure.mapstruct.AftersaleConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.AftersaleOrderDao;
import com.memberclub.sdk.aftersale.extension.apply.AfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.extension.domain.AfterSaleRepositoryExtension;
import com.memberclub.sdk.aftersale.extension.preview.AfterSalePreviewCheckExtension;
import com.memberclub.sdk.common.IdGeneratorDomainService;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.memberclub.domain.exception.ResultCode.AFTERSALE_EXECUTE_ERROR;

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
    private MemberOrderDomainService memberOrderDomainService;
    @Autowired
    private PerformDomainService performDomainService;
    @Autowired
    private IdGeneratorDomainService idGeneratorDomainService;
    @Autowired
    private CacheService cacheService;

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

    public String generatePreviewToken(AftersaleSourceEnum source, String tradeId) {
        if (source == AftersaleSourceEnum.System_Expire || source == AftersaleSourceEnum.SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT
                || source == AftersaleSourceEnum.SYSTEM_REFUND_4_PERFORM_FAIL) {
            return tradeId + "_SYSTEM_REFUND";
        }
        return idGeneratorDomainService.generateId(IdTypeEnum.PREVIEW_TOKEN) + "";
    }

    @Retryable
    public void savePreviewToken(AfterSalePreviewContext context, String previewToken, AfterSalePreviewCoreResult result) {
        cacheService.put(CacheEnum.after_sale_preview_token, previewToken, result);
    }

    public void checkPreviewResult(AfterSaleApplyContext context) {
        AfterSalePreviewCoreResult coreResult =
                cacheService.get(CacheEnum.after_sale_preview_token, context.getExecuteCmd().getApplyCmd().getPreviewToken());
        if (coreResult == null) {
            throw AftersaleUnableCode.PREVIEW_TOKEN_TIMEOUT_ERROR.newException();
        }
        extensionManager.getExtension(context.toBizScene(), AfterSalePreviewCheckExtension.class).check(coreResult, context.getExecuteCmd());
    }

    @Retryable(maxTimes = 5, initialDelaySeconds = 1, maxDelaySeconds = 5, throwException = true)
    public void execute(AfterSaleExecuteCmd cmd) {
        AfterSaleApplyContext context = new AfterSaleApplyContext();
        initializeApplyContext(cmd, context);
        try {
            extensionManager.getExtension(context.toBizScene(),
                    AfterSaleApplyExtension.class).execute(context);
        } catch (Exception e) {
            CommonLog.error("售后受理执行流程异常 context:{}", context, e);
            throw new AftersaleExecuteException(AFTERSALE_EXECUTE_ERROR, e);
        }
    }

    public void initializeApplyContext(AfterSaleExecuteCmd cmd, AfterSaleApplyContext context) {
        MemberOrderDO order = memberOrderDomainService.getMemberOrderDO(cmd.getApplyCmd().getUserId(), cmd.getApplyCmd().getTradeId());

        List<MemberPerformItemDO> performItems = performDomainService.queryItemsByTradeId(cmd.getApplyCmd().getUserId(), cmd.getApplyCmd().getTradeId());

        List<MemberPerformItemDO> reversablePerformItems = performItems.stream().filter((item) -> {
            ItemUsage itemUsage = cmd.getItemToken2ItemUsage().get(item.getItemToken());
            if (itemUsage.getUsageType() == UsageTypeEnum.UNUSE || itemUsage.getUsageType() == UsageTypeEnum.USED) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        AftersaleOrderDO aftersaleOrderDO = getAfterSaleOrderDO(cmd.getApplyCmd().getUserId(), cmd.getApplyCmd().getPreviewToken());
        context.setApplyCmd(cmd.getApplyCmd());
        context.setExecuteCmd(cmd);
        context.setMemberOrder(order);
        context.setAftersaleOrderDO(aftersaleOrderDO);
        context.setScene(cmd.getScene());

        context.setTotalPerformItems(performItems);
        context.setReversablePerformItems(reversablePerformItems);
    }

    public AftersaleOrderDO generateOrder(AfterSaleApplyContext context) {
        AftersaleOrderDO order = AftersaleConvertor.INSTANCE.toAftersaleOrderDO(context.getApplyCmd());
        order.setActPayPriceFen(context.getMemberOrder().getPaymentInfo().getPayAmountFen());
        order.setApplySkuInfos(context.getApplyCmd().getApplySkus());
        order.setStatus(AftersaleOrderStatusEnum.INIT);
        order.setPreviewToken(context.getExecuteCmd().getApplyCmd().getPreviewToken());
        order.setCtime(TimeUtil.now());
        order.setExtra(new AftersaleOrderExtraDO());
        order.getExtra().setReason(context.getApplyCmd().getReason());
        order.getExtra().setApplySkus(order.getApplySkuInfos());
        order.setRefundType(context.getExecuteCmd().getRefundType());
        order.setRecommendRefundPriceFen(context.getExecuteCmd().getRecommendRefundPrice());
        order.setRefundWay(context.getExecuteCmd().getRefundWay());
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

    public AftersaleOrderDO getAfterSaleOrderDO(long userId, String previewToken) {
        AftersaleOrder order = aftersaleOrderDao.queryByPreviewToken(userId, previewToken);
        if (order == null) {
            return null;
        }
        return aftersaleDataObjectFactory.buildAftersaleOrderDO(order);
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