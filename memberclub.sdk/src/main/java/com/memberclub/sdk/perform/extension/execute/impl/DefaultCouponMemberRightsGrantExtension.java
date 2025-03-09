/**
 * @(#)CouponDefaultGrantExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.execute.ItemGrantResult;
import com.memberclub.domain.context.perform.execute.ItemGroupGrantResult;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.GrantItemDO;
import com.memberclub.domain.facade.GrantRequestDO;
import com.memberclub.domain.facade.GrantResponseDO;
import com.memberclub.infrastructure.assets.facade.AssetsFacadeSPI;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.perform.extension.execute.MemberRightsGrantExtension;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "券类型默认发放扩展点实现", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER,
                scenes = {
                        SceneEnum.RIGHT_TYPE_SCENE_COUPON,
                        SceneEnum.RIGHT_TYPE_SCENE_DISCOUNT_COUPON,
                        SceneEnum.RIGHT_TYPE_SCENE_FREE_FREIGHT_COUPON
                }),
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE,
                scenes = {
                        SceneEnum.RIGHT_TYPE_SCENE_COUPON,
                }),
        @Route(bizType = BizTypeEnum.LESSON,
                scenes = {
                        SceneEnum.RIGHT_TYPE_SCENE_COUPON,
                }),
})
public class DefaultCouponMemberRightsGrantExtension implements MemberRightsGrantExtension {

    //@Qualifier("couponGrantFacade")
    @Resource()
    private AssetsFacadeSPI assetsFacadeSPI;

    public static void monitor(PerformItemContext context, Object msg) {
        Monitor.PERFORM_EXECUTE.counter(context.getBizType(),
                "grant_result", msg,
                "period_perform", context.isPeriodPerform());
    }

    @Override
    public ItemGroupGrantResult grant(PerformItemContext context, List<MemberPerformItemDO> items) {

        GrantRequestDO request = new GrantRequestDO();
        request.setUserId(context.getUserId());

        List<GrantItemDO> grantItemDOS = Lists.newArrayList();
        for (MemberPerformItemDO item : items) {
            GrantItemDO grantItemDO = new GrantItemDO();
            grantItemDO.setItemToken(item.getItemToken());
            grantItemDO.setAssetCount(item.getTotalCount());
            grantItemDO.setChannelKey(String.valueOf(item.getRightId()));
            grantItemDO.setStime(item.getStime());
            grantItemDO.setEtime(item.getEtime());
            grantItemDO.setRightType(item.getRightType().getCode());
            grantItemDOS.add(grantItemDO);
        }

        request.setGrantItems(grantItemDOS);

        GrantResponseDO response = null;
        try {
            response = assetsFacadeSPI.grant(request);
            if (response == null) {
                Monitor.PERFORM_EXECUTE.counter(context.getBizType(),
                        "grant_code", "null",
                        "period_perform", context.isPeriodPerform());

                throw ResultCode.DEPENDENCY_ERROR.newException("调用下游为空");
            }
        } catch (Exception e) {
            monitor(context, "exception");
            CommonLog.error("调用下游异常 request:{} ", request, e);
            throw ResultCode.DEPENDENCY_ERROR.newException(String.format("调用下游异常:%s", e.getMessage()), e);
        }

        if (!response.isSuccess()) {
            monitor(context, response.getCode());
            throw ResultCode.DEPENDENCY_ERROR.newException();
        }
        monitor(context, response.getCode());

        CommonLog.warn("调用权益发放返回值:{}, 入参:{}", response, request);
        if (CollectionUtils.isEmpty(response.getItemToken2AssetsMap())) {
            monitor(context, "empty");
            throw ResultCode.DEPENDENCY_ERROR.newException("下游发券列表为空");
        }
        monitor(context, "normal");

        Map<String, ItemGrantResult> grantMap = Maps.newHashMap();
        for (Map.Entry<String, List<AssetDO>> entry : response.getItemToken2AssetsMap().entrySet()) {
            String itemToken = entry.getKey();
            String batchCode = entry.getValue().get(0).getBatchCode();

            ItemGrantResult result = new ItemGrantResult();
            result.setBatchCode(batchCode);
            grantMap.put(itemToken, result);
        }
        CommonLog.warn("调用权益发放结果:{}", grantMap);

        ItemGroupGrantResult result = new ItemGroupGrantResult();
        result.setGrantMap(grantMap);
        return result;
    }
}