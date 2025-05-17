package com.memberclub.sdk.quota.service.aftersale;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.usertag.*;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.usertag.UserTagService;
import com.memberclub.sdk.quota.extension.aftersale.AftersaleQuotaExtension;
import com.memberclub.sdk.quota.extension.aftersale.AftersaleQuotaExtensionContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AftersaleQuotaDomainService {

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

        AftersaleQuotaExtensionContext quotaExtensionContext = new AftersaleQuotaExtensionContext();
        quotaExtensionContext.setUserId(userId);
        quotaExtensionContext.setBizType(context.getBizType());
        quotaExtensionContext.setOpType(UserTagOpTypeEnum.GET);

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                AftersaleQuotaExtension.class).buildUserTagOp(quotaExtensionContext);
        List<UserTagOpDO> usertagOps = quotaExtensionContext.getUserTagOpDOList();

        if (CollectionUtils.isEmpty(usertagOps)) {
            CommonLog.info("当前订单无需校验售后频率:{}", CollectionUtilEx.mapToList(skus, SkuInfoDO::getSkuId));
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
            CommonLog.error("用户售后频率查询异常 cmd:{}", e);
            throw ResultCode.AFTERSALE_FREQUNCE_EXCEED.newException("售后频率过高，禁止购买", e);
        }

        CommonLog.info("售后频率查询结果 cmd:{}, respone:{}", cmd, response);

        for (UserTagDO tag : response.getTags()) {
            UserTagOpDO userTagOpDO = key2UserTagOp.get(tag.getKey());
            if (tag.getCount() >= userTagOpDO.getTotalCount()) {
                CommonLog.warn("用户售后频率过高 skuId:{}, current:{}, opCount:{}, total:{} key:{}",
                        userTagOpDO.getSkuId(),
                        tag.getCount(),
                        userTagOpDO.getOpCount(),
                        userTagOpDO.getTotalCount(),
                        tag.getKey());
                throw ResultCode.AFTERSALE_FREQUNCE_EXCEED.newException();
            }
        }
        CommonLog.info("通过售后频率校验 tagKeys:{}", key2UserTagOp.keySet());
    }

    public void onApply(AfterSaleApplyContext context) {
        long userId = context.getApplyCmd().getUserId();

        UserTagOpCmd cmd = new UserTagOpCmd();
        cmd.buildUniqueKey(UserTagTypeEnum.aftersalequota, context.getApplyCmd().getBizType(), context.getApplyCmd().getTradeId());
        cmd.setOpType(UserTagOpTypeEnum.ADD);

        AftersaleQuotaExtensionContext quotaExtensionContext = new AftersaleQuotaExtensionContext();
        quotaExtensionContext.setUserId(userId);
        quotaExtensionContext.setBizType(context.getApplyCmd().getBizType());
        quotaExtensionContext.setOpType(UserTagOpTypeEnum.ADD);
        quotaExtensionContext.setOpCount(context.getMemberOrder().getSubOrders().stream().mapToInt(MemberSubOrderDO::getBuyCount).sum());

        extensionManager.getExtension(BizScene.of(context.getApplyCmd().getBizType()),
                AftersaleQuotaExtension.class).buildUserTagOp(quotaExtensionContext);
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
                CommonLog.error("记录用户售后次数失败,内部有重试! cmd:{}", cmd);
                return;
            }
            CommonLog.info("记录用户售后次数成功 cmd:{}", cmd);
        } catch (Exception e) {
            CommonLog.error("记录用户售后次数异常,内部有重试! cmd:{}", cmd);
        }

    }
}
