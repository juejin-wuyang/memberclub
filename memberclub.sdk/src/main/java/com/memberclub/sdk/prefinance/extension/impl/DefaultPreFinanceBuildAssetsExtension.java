/**
 * @(#)DefaultPreFinanceBuildAssetsExtension.java, 一月 25, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.extension.impl;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.RightTypeEnum;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.facade.AssetDO;
import com.memberclub.domain.facade.AssetStatusEnum;
import com.memberclub.sdk.assets.service.AssetsDomainService;
import com.memberclub.sdk.prefinance.extension.PreFinanceBuildAssetsExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认预结算资产构建扩展点", bizScenes = {@Route(bizType = BizTypeEnum.DEFAULT)})
public class DefaultPreFinanceBuildAssetsExtension implements PreFinanceBuildAssetsExtension {

    @Autowired
    private AssetsDomainService assetsDomainService;

    @Override
    public boolean buildAssets(PreFinanceContext context) {
        if (CollectionUtils.isEmpty(context.getPerformItems())) {
            CommonLog.warn("PerformItem 为空,无法获取资产 event:{}", context.getEvent());
            return false;
        }

        Map<RightTypeEnum, List<MemberPerformItemDO>> rightType2PerformItems =
                context.getPerformItems().stream().collect(Collectors.groupingBy(MemberPerformItemDO::getRightType));
        Map<String, List<AssetDO>> itemToken2Assets = Maps.newHashMap();
        for (Map.Entry<RightTypeEnum, List<MemberPerformItemDO>> entry : rightType2PerformItems.entrySet()) {
            Map<String, List<AssetDO>> tempItemToken2Assets =
                    assetsDomainService.queryAssets(context.getUserId(), entry.getKey().getCode(),
                            CollectionUtilEx.map(entry.getValue(), MemberPerformItemDO::getItemToken));
            itemToken2Assets.putAll(tempItemToken2Assets);
        }
        context.setItemToken2Assets(itemToken2Assets);
        return true;
    }

    @Override
    public void buildOnPerform(PreFinanceContext context) {
        for (Map.Entry<String, List<AssetDO>> entry : context.getItemToken2Assets().entrySet()) {
            List<AssetDO> assets = entry.getValue().stream().filter(asset -> asset.getStatus() == AssetStatusEnum.UNUSE.getCode())
                    .collect(Collectors.toList());
            entry.setValue(assets);
        }
    }

    @Override
    public void buildOnFreeze(PreFinanceContext context) {
        for (Map.Entry<String, List<AssetDO>> entry : context.getItemToken2Assets().entrySet()) {
            List<AssetDO> assets = entry.getValue().stream().filter(asset -> asset.getStatus() == AssetStatusEnum.FREEZE.getCode())
                    .collect(Collectors.toList());
            entry.setValue(assets);
        }
    }

    @Override
    public void buildOnExpire(PreFinanceContext context) {
        for (Map.Entry<String, List<AssetDO>> entry : context.getItemToken2Assets().entrySet()) {
            List<AssetDO> assets = CollectionUtilEx.filter(entry.getValue(), this::isExpire);
            entry.setValue(assets);
        }
    }

    private boolean isExpire(AssetDO asset) {
        return asset.getStatus() == AssetStatusEnum.EXPIRE.getCode() ||
                (asset.getStime() < TimeUtil.now() && TimeUtil.now() < asset.getEtime() && asset.getStatus() == AssetStatusEnum.UNUSE.getCode());
    }

    @Override
    public void buildOnRefund(PreFinanceContext context) {
        //context.setBatchCode2Assets(Maps.newHashMap());
    }
}