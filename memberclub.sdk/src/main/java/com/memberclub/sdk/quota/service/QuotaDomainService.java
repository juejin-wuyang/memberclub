/**
 * @(#)QuotaDomainService.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.quota.service;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.usertag.*;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.usertag.UserTagService;
import com.memberclub.sdk.quota.extension.QuotaExtension;
import com.memberclub.sdk.quota.extension.QuotaExtensionContext;
import com.memberclub.sdk.quota.extension.SkuAndRestrictInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class QuotaDomainService {

    @Autowired
    private UserTagService userTagService;

    @Autowired
    private ExtensionManager extensionManager;

    public void validate(PurchaseSubmitContext context) {
        long userId = context.getUserId();
        List<SkuInfoDO> skus = context.getSkuInfos();

        UserTagOpCmd cmd = new UserTagOpCmd();
        //cmd.setUniqueKey();
        cmd.setOpType(UserTagOpTypeEnum.GET);

        QuotaExtensionContext quotaExtensionContext = new QuotaExtensionContext();
        quotaExtensionContext.setUserId(userId);
        quotaExtensionContext.setBizType(context.getBizType());
        quotaExtensionContext.setOpType(UserTagOpTypeEnum.GET);
        quotaExtensionContext.setStartTime(context.getStartTime());

        List<SkuAndRestrictInfo> skuAndRestrictInfos = Lists.newArrayList();
        for (SkuInfoDO skuInfoDO : context.getSkuInfos()) {
            SkuAndRestrictInfo skuAndRestrictInfo = new SkuAndRestrictInfo();
            skuAndRestrictInfo.setSkuId(skuInfoDO.getSkuId());
            skuAndRestrictInfo.setBuyCount(skuInfoDO.getBuyCount());
            skuAndRestrictInfo.setRestrictInfo(skuInfoDO.getRestrictInfo());
            skuAndRestrictInfos.add(skuAndRestrictInfo);
        }
        quotaExtensionContext.setSkus(skuAndRestrictInfos);

        QuotaExtension quotaExtension = extensionManager.getExtension(BizScene.of(context.getBizType()),
                QuotaExtension.class);
        quotaExtension.buildUserTagOp(quotaExtensionContext);
        List<UserTagOpDO> usertagOps = quotaExtensionContext.getUserTagOpDOList();

        if (CollectionUtils.isEmpty(usertagOps)) {
            CommonLog.info("当前所购商品无限额:{}", CollectionUtilEx.mapToList(skus, SkuInfoDO::getSkuId));
            return;
        }
        cmd.setTags(usertagOps);

        Map<String, List<UserTagOpDO>> key2UserTagOpList = CollectionUtilEx.groupingBy(usertagOps, UserTagOpDO::getKey);

        Map<String, UserTagOpDO> key2UserTagOp = CollectionUtilEx.convertMapValue(key2UserTagOpList, (key, list) -> {
            UserTagOpDO userTagOpDO = list.get(0);
            int opCount = list.stream().collect(Collectors.summingInt(UserTagOpDO::getOpCount));
            userTagOpDO.setOpCount(opCount);
            return userTagOpDO;
        });

        UserTagOpResponse response = null;
        try {
            response = userTagService.operate(cmd);
        } catch (Exception e) {
            CommonLog.error("用户购买配额查询异常 cmd:{}", e);
            throw ResultCode.QUOTA_LACKING.newException("用户购买配额查询异常", e);
        }

        CommonLog.info("限额查询结果 cmd:{}, respone:{}", cmd, response);

        for (UserTagDO tag : response.getTags()) {
            UserTagOpDO userTagOpDO = key2UserTagOp.get(tag.getKey());
            if (tag.getCount() + userTagOpDO.getOpCount() > userTagOpDO.getTotalCount()) {
                CommonLog.warn("用户可购限额不足 skuId:{}, current:{}, opCount:{}, total:{} key:{}",
                        userTagOpDO.getSkuId(),
                        tag.getCount(),
                        userTagOpDO.getOpCount(),
                        userTagOpDO.getTotalCount(),
                        tag.getKey());
                throw ResultCode.QUOTA_LACKING.newException("用户可购限额不足");
            }
        }
        CommonLog.info("通过限额校验 tagKeys:{}", key2UserTagOp.keySet());
    }

    public void onSubmitSuccess(PurchaseSubmitContext context) {
        long userId = context.getUserId();
        List<SkuInfoDO> skus = context.getSkuInfos();

        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.buildUniqueKey(UserTagTypeEnum.quota, context.getBizType(), context.getMemberOrder().getTradeId());
        cmd.setOpType(UserTagOpTypeEnum.ADD);

        QuotaExtensionContext quotaExtensionContext = new QuotaExtensionContext();
        quotaExtensionContext.setUserId(userId);
        quotaExtensionContext.setBizType(context.getBizType());
        quotaExtensionContext.setOpType(UserTagOpTypeEnum.ADD);
        quotaExtensionContext.setStartTime(context.getStartTime());
        List<SkuAndRestrictInfo> skuAndRestrictInfos = Lists.newArrayList();
        for (MemberSubOrderDO subOrder : context.getMemberOrder().getSubOrders()) {
            SkuAndRestrictInfo skuAndRestrictInfo = new SkuAndRestrictInfo();
            skuAndRestrictInfo.setSkuId(subOrder.getSkuId());
            skuAndRestrictInfo.setBuyCount(subOrder.getBuyCount());
            skuAndRestrictInfo.setRestrictInfo(subOrder.getExtra().getSkuRestrictInfo());
            skuAndRestrictInfos.add(skuAndRestrictInfo);
        }
        quotaExtensionContext.setSkus(skuAndRestrictInfos);


        QuotaExtension quotaExtension = extensionManager.getExtension(
                BizScene.of(context.getBizType()), QuotaExtension.class);
        quotaExtension.buildUserTagOp(quotaExtensionContext);

        List<UserTagOpDO> usertagOps = quotaExtensionContext.getUserTagOpDOList();

        if (CollectionUtils.isEmpty(usertagOps)) {
            return;
        }
        cmd.setTags(usertagOps);
        long expireSeconds = usertagOps.stream()
                .max(Comparator.comparingLong(UserTagOpDO::getExpireSeconds)).get().getExpireSeconds();
        cmd.setExpireSeconds(expireSeconds);

        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("提单成功后写用户购买限额失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("提单成功后写用户购买限额成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("提单成功后写用户购买限额异常,内部有重试! cmd:{}", cmd);
        }

    }

    public void onCancel(MemberOrderDO memberOrderDO) {
        long userId = memberOrderDO.getUserId();

        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.setUniqueKey(memberOrderDO.getTradeId());
        cmd.setOpType(UserTagOpTypeEnum.DEL);

        QuotaExtensionContext quotaExtensionContext = buildQuotaExtensionContext4AfterSubmit(memberOrderDO, userId);

        QuotaExtension quotaExtension = extensionManager.getExtension(BizScene.of(memberOrderDO.getBizType()),
                QuotaExtension.class);
        quotaExtension.buildUserTagOp(quotaExtensionContext);

        List<UserTagOpDO> usertagOps = quotaExtensionContext.getUserTagOpDOList();

        if (CollectionUtils.isEmpty(usertagOps)) {
            CommonLog.info("无用户限额数据需要处理 cmd:{}", cmd);
            return;
        }
        cmd.setTags(usertagOps);
        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("订单取消后删除购买限额数据失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("订单取消后删除购买限额数据成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("订单取消后删除购买限额数据异常,内部有重试! cmd:{}", cmd, e);
        }
    }

    private QuotaExtensionContext buildQuotaExtensionContext4AfterSubmit(MemberOrderDO memberOrderDO, long userId) {
        QuotaExtensionContext quotaExtensionContext = new QuotaExtensionContext();
        quotaExtensionContext.setUserId(userId);
        quotaExtensionContext.setOpType(UserTagOpTypeEnum.DEL);
        quotaExtensionContext.setBizType(memberOrderDO.getBizType());
        quotaExtensionContext.setStartTime(memberOrderDO.getStime());

        List<SkuAndRestrictInfo> skuAndRestrictInfos = Lists.newArrayList();
        for (MemberSubOrderDO subOrder : memberOrderDO.getSubOrders()) {
            SkuAndRestrictInfo skuAndRestrictInfo = new SkuAndRestrictInfo();
            skuAndRestrictInfo.setSkuId(subOrder.getSkuId());
            skuAndRestrictInfo.setBuyCount(subOrder.getBuyCount());
            skuAndRestrictInfo.setRestrictInfo(subOrder.getExtra().getSkuRestrictInfo());
            skuAndRestrictInfos.add(skuAndRestrictInfo);
        }
        quotaExtensionContext.setSkus(skuAndRestrictInfos);
        return quotaExtensionContext;
    }


    /**
     * 暂时忽略,目前无取消订单流程
     *
     * @param context
     */
    public void onReverse(AfterSaleApplyContext context) {
        long userId = context.getCmd().getUserId();
        MemberOrderDO memberOrderDO = context.getPreviewContext().getMemberOrder();

        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.setUniqueKey(memberOrderDO.getTradeId());
        cmd.setOpType(UserTagOpTypeEnum.DEL);

        QuotaExtensionContext quotaExtensionContext = buildQuotaExtensionContext4AfterSubmit(memberOrderDO, userId);
        extensionManager.getExtension(BizScene.of(quotaExtensionContext.getBizType()),
                QuotaExtension.class).buildUserTagOp(quotaExtensionContext);
        List<UserTagOpDO> usertagOps = quotaExtensionContext.getUserTagOpDOList();

        if (CollectionUtils.isEmpty(usertagOps)) {
            CommonLog.info("无用户限额数据需要处理 cmd:{}", cmd);
            return;
        }
        cmd.setTags(usertagOps);
        try {
            UserTagOpResponse response = userTagService.operate(cmd);
            if (!response.isSuccess()) {
                CommonLog.error("售后成功后删除购买限额数据失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("售后成功后删除购买限额数据成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("售后成功后删除购买限额数据异常,内部有重试! cmd:{}", cmd, e);
        }
    }

}