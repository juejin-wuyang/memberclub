/**
 * @(#)MockCouponGrantFacade.java, 十二月 18, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.assets.facade;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.facade.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.asset", havingValue = "local")
@Service
public class MockAssetsFacadeSPI implements AssetsFacadeSPI {

    public Map<String, List<AssetDO>> itemToken2Assets = Maps.newHashMap();
    private AtomicLong couponIdGenerator = new AtomicLong(System.currentTimeMillis());

    @Override
    public GrantResponseDO grant(GrantRequestDO requestDO) {
        GrantResponseDO responseDO = new GrantResponseDO();
        responseDO.setCode(0);
        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (GrantItemDO grantItem : requestDO.getGrantItems()) {
            List<AssetDO> coupons = Lists.newArrayList();
            String batchCode = RandomStringUtils.randomAlphabetic(10);
            for (int assetCount = grantItem.getAssetCount(); assetCount > 0; assetCount--) {
                AssetDO coupon = new AssetDO();
                coupon.setStime(grantItem.getStime());
                coupon.setEtime(grantItem.getEtime());
                coupon.setRightType(grantItem.getRightType());
                coupon.setCtime(TimeUtil.now());
                coupon.setBatchCode(batchCode);
                coupon.setPriceFen(500);
                coupon.setAssetId(couponIdGenerator.incrementAndGet());
                coupon.setAssetType(1);
                coupon.setUserId(requestDO.getUserId());
                coupons.add(coupon);
            }
            itemToken2Assets.put(grantItem.getItemToken(), coupons);
            map.put(grantItem.getItemToken(), coupons);
        }
        responseDO.setItemToken2AssetsMap(map);
        return responseDO;
    }

    @Override
    public AssetFetchResponseDO fetch(AssetFetchRequestDO request) {
        AssetFetchResponseDO resp = new AssetFetchResponseDO();

        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (String itemTokens : request.getItemTokens()) {
            List<AssetDO> assets = itemToken2Assets.get(itemTokens);
            if (CollectionUtils.isNotEmpty(assets)) {
                map.put(itemTokens, assets);
            }
        }
        resp.setItemToken2AssetsMap(map);
        return resp;
    }

    @Override
    public AssetReverseResponseDO reverse(AssetReverseRequestDO request) {
        AssetReverseResponseDO resp = new AssetReverseResponseDO();

        Map<String, List<AssetDO>> map = Maps.newHashMap();
        for (String itemToken : request.getItemTokens()) {
            List<AssetDO> assets = itemToken2Assets.get(itemToken);
            for (AssetDO asset : assets) {
                if (asset.getStatus() == AssetStatusEnum.UNUSE.getCode()) {
                    asset.setStatus(AssetStatusEnum.FREEZE.getCode());
                }
            }
            map.put(itemToken, assets);
        }
        resp.setItemToken2AssetsMap(map);
        return resp;
    }
}