/**
 * @(#)AftersaleItemService.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.service.domain;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.contant.RefundTypeEnum;
import com.memberclub.domain.context.aftersale.contant.RefundWayEnum;
import com.memberclub.domain.context.aftersale.contant.UsageTypeEnum;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.sdk.aftersale.extension.preview.AftersaleAmountExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class AftersaleAmountService {

    @Autowired
    private ExtensionManager extensionManager;

    public Integer recommendRefundPrice(AftersalePreviewContext context) {
        int recommendRefundPrice = extensionManager.getExtension(
                        BizScene.of(context.getCmd().getBizType().getCode()), AftersaleAmountExtension.class)
                .calculteRecommendRefundPrice(context, context.getBatchCode2ItemUsage());
        return recommendRefundPrice;
    }

    public ItemUsage summingPrice(List<AssetDO> assets) {
        // TODO: 2024/12/22
        ItemUsage itemUsage = new ItemUsage();
        int usedPriceFen = assets.stream()
                .filter(AssetDO::isUsed)
                .collect(Collectors.summingInt(AssetDO::getPriceFen));
        int totalPriceFen = assets.stream()
                .filter(AssetDO::isNormal)
                .collect(Collectors.summingInt(AssetDO::getPriceFen));


        itemUsage.setUsedPrice(usedPriceFen);
        itemUsage.setTotalPrice(totalPriceFen);
        if (usedPriceFen <= 0) {
            itemUsage.setUsageType(UsageTypeEnum.UNUSE);
        } else if (usedPriceFen < totalPriceFen) {
            itemUsage.setUsageType(UsageTypeEnum.USED);
        } else {
            itemUsage.setUsageType(UsageTypeEnum.USEOUT);
        }
        return itemUsage;
    }

    public int unusePriceDividePayPriceToCacluateRefundPrice(int payPriceFen, Map<String, ItemUsage> itemUsageMap) {
        int usedPriceFen = 0;

        int totalPriceFen = 0;
        for (Map.Entry<String, ItemUsage> entry : itemUsageMap.entrySet()) {
            usedPriceFen += entry.getValue().getUsedPrice();
            totalPriceFen += entry.getValue().getTotalPrice();
        }
        int unusePriceFen = totalPriceFen - usedPriceFen;

        BigDecimal unuse = new BigDecimal(unusePriceFen);
        BigDecimal total = new BigDecimal(totalPriceFen);
        BigDecimal pay = new BigDecimal(payPriceFen);
        if (total.equals(new BigDecimal(0))) {
            throw AftersaleUnableCode.INTERNAL_ERROR.newException("获取售后金额异常");
        }

        int recommendRefundPrice = unuse.divide(total).multiply(pay).intValue();
        return recommendRefundPrice;
    }

    public int payPriceDividedUsed(int payPriceFen, Map<String, ItemUsage> itemUsageMap) {
        int usedPriceFen = 0;

        int totalPriceFen = payPriceFen;
        for (Map.Entry<String, ItemUsage> entry : itemUsageMap.entrySet()) {
            usedPriceFen += entry.getValue().getUsedPrice();
        }
        int unusePriceFen = totalPriceFen - usedPriceFen;

        if (unusePriceFen <= 0) {
            unusePriceFen = 0;
        }

        int recommendRefundPrice = unusePriceFen;
        return recommendRefundPrice;
    }


    public void calculateUsageTypeByAmount(AftersalePreviewContext context) {
        if (context.getRecommendRefundPrice() < 0) {
            throw AftersaleUnableCode.INTERNAL_ERROR.newException("退款金额内部计算错误", null);
        } else if (context.getRecommendRefundPrice() == 0) {
            context.setUsageType(UsageTypeEnum.USEOUT);
            throw AftersaleUnableCode.USE_OUT_ERROR.newException();
        } else if (context.getRecommendRefundPrice() > 0 && context.getRecommendRefundPrice() < context.getPayPriceFen()) {
            context.setUsageType(UsageTypeEnum.USED);
            context.setRefundType(RefundTypeEnum.PORTION_RFUND);
        } else if (context.getRecommendRefundPrice() == context.getPayPriceFen()) {
            context.setUsageType(UsageTypeEnum.UNUSE);
            context.setRefundType(RefundTypeEnum.ALL_REFUND);
        } else {
            throw AftersaleUnableCode.INTERNAL_ERROR.newException("退款金额内部计算错误", null);
        }
    }

    public RefundWayEnum calculateRefundWaySupportPortionRefund(AftersalePreviewContext context) {
        if (context.getUsageType() == UsageTypeEnum.USED || context.getUsageType() == UsageTypeEnum.UNUSE) {
            return RefundWayEnum.ORDER_BACKSTRACK;
        }
        throw AftersaleUnableCode.INTERNAL_ERROR.newException("不应该走到这里,已用尽应在上层拦截", null);
    }

    public RefundWayEnum calculateRefundWayUnSupportPortionRefund(AftersalePreviewContext context) {
        if (context.getUsageType() == UsageTypeEnum.UNUSE) {
            return RefundWayEnum.ORDER_BACKSTRACK;
        } else if (context.getUsageType() == UsageTypeEnum.USED) {
            return RefundWayEnum.CUSTOMER_REFUND;
        }
        throw AftersaleUnableCode.INTERNAL_ERROR.newException("不应该走到这里,已用尽应在上层拦截", null);
    }
}