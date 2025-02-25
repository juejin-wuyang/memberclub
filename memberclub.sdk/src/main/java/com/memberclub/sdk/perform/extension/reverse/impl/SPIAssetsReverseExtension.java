/**
 * @(#)SPIPerformItemReverseExtension.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.reverse.AssetsReverseResponse;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.domain.facade.AssetReverseRequestDO;
import com.memberclub.domain.facade.AssetReverseResponseDO;
import com.memberclub.infrastructure.assets.facade.AssetsFacadeSPI;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.perform.extension.reverse.AssetsReverseExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "SPI 接口逆向履约项资产", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {
                SceneEnum.RIGHT_TYPE_SCENE_COUPON,
                SceneEnum.RIGHT_TYPE_SCENE_DISCOUNT_COUPON,
                SceneEnum.RIGHT_TYPE_SCENE_FREE_FREIGHT_COUPON
        })
})
public class SPIAssetsReverseExtension implements AssetsReverseExtension {

    @Autowired
    private AssetsFacadeSPI assetsFacadeSPI;

    @Override
    public AssetsReverseResponse reverse(ReversePerformContext context,
                                         SubOrderReversePerformContext reverseInfo,
                                         List<PerformItemReverseInfo> items) {
        AssetsReverseResponse response = new AssetsReverseResponse();
        AssetReverseRequestDO requestDO = new AssetReverseRequestDO();
        requestDO.setUserId(context.getUserId());
        requestDO.setRightType(reverseInfo.getCurrentRightType());
        requestDO.setAssetBatchs(items.stream().map(PerformItemReverseInfo::getBatchCode).collect(Collectors.toList()));
        AssetReverseResponseDO responseDO = null;
        try {
            responseDO = assetsFacadeSPI.reverse(requestDO);
            if (responseDO == null) {
                Monitor.AFTER_SALE_DOAPPLY.counter(context.getBizType(),
                        "right_type", reverseInfo.getCurrentRightType(),
                        "code", "null");
                CommonLog.error("冻结下游资产返回值为空 req:{}", requestDO);
                throw ResultCode.DEPENDENCY_ERROR.newException("逆向冻结下游资产返回空");
            }
        } catch (Exception e) {
            Monitor.AFTER_SALE_DOAPPLY.counter(context.getBizType(),
                    "right_type", reverseInfo.getCurrentRightType(),
                    "code", "exception");
            CommonLog.error("冻结下游资产异常 req:{}", requestDO, e);
            throw ResultCode.DEPENDENCY_ERROR.newException("逆向冻结下游资产异常", e);
        }

        if (!responseDO.isSuccess()) {
            Monitor.AFTER_SALE_DOAPPLY.counter(context.getBizType(),
                    "right_type", reverseInfo.getCurrentRightType(),
                    "code", responseDO.getCode());
            CommonLog.error("冻结下游资产失败 req:{}, response:{}", requestDO, responseDO);
            throw ResultCode.DEPENDENCY_ERROR.newException("逆向冻结下游资产失败");
        } else {
            Monitor.AFTER_SALE_DOAPPLY.counter(context.getBizType(),
                    "right_type", reverseInfo.getCurrentRightType(),
                    "code", responseDO.getCode());
            CommonLog.info("冻结下游资产成功 req:{}, response:{}", requestDO, responseDO);
            response.setSuccess(true);
        }
        return response;
    }
}