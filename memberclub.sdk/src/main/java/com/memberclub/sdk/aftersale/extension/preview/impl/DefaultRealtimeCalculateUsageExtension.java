/**
 * @(#)DefaultRealtimeCalculateUsageExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview.impl;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.preview.AftersalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.AssetFetchRequestDO;
import com.memberclub.domain.facade.AssetFetchResponseDO;
import com.memberclub.infrastructure.assets.facade.AssetsFacadeSPI;
import com.memberclub.sdk.aftersale.extension.preview.RealtimeCalculateUsageExtension;
import com.memberclub.sdk.aftersale.service.domain.AftersaleAmountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "实时查询计算使用情况", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {
                SceneEnum.RIGHT_TYPE_SCENE_COUPON,
                SceneEnum.RIGHT_TYPE_SCENE_FREE_FREIGHT_COUPON,
                SceneEnum.RIGHT_TYPE_SCENE_DISCOUNT_COUPON
        })
})
public class DefaultRealtimeCalculateUsageExtension implements RealtimeCalculateUsageExtension {

    @Autowired
    private AssetsFacadeSPI assetsFacadeSPI;

    @Autowired
    private AftersaleAmountService aftersaleAmountService;

    @Override
    public Map<String, ItemUsage> calculateItemUsage(AftersalePreviewContext context) {
        AssetFetchRequestDO request = new AssetFetchRequestDO();
        request.setUserId(context.getCmd().getUserId());
        List<String> assetBatchCodes = context.getCurrentPerformItemsGroupByRightType()
                .stream()
                .map(MemberPerformItemDO::getBatchCode)
                .collect(Collectors.toList());

        request.setRightType(context.getCurrentRightType());
        request.setAssetBatchs(assetBatchCodes);
        AssetFetchResponseDO responseDO = assetsFacadeSPI.fetch(request);

        CommonLog.info("调用下游查询资产状态, 结果:{}, 请求:{}", responseDO, request);
        if (!responseDO.isSuccess()) {
            throw ResultCode.DEPENDENCY_ERROR.newException("查询下游资产异常");
        }

        Map<String, ItemUsage> itemUsageMap = Maps.newHashMap();

        for (Map.Entry<String, List<AssetDO>> entry : responseDO.getAssetBatchCode2AssetsMap().entrySet()) {
            ItemUsage itemUsage = aftersaleAmountService.summingPrice(entry.getValue());
            itemUsageMap.put(entry.getKey(), itemUsage);
        }
        return itemUsageMap;
    }
}