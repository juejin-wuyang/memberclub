/**
 * @(#)DefaultBuildReverseInfoExtension.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse.impl;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionImpl;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.reverse.PerformHisReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.sdk.perform.extension.reverse.BuildReverseInfoExtension;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * author: 掘金五阳
 */
@ExtensionImpl(desc = "默认构建逆向履约信息", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultBuildReversePerformInfoExtension implements BuildReverseInfoExtension {

    @Autowired
    private PerformDomainService performDomainService;

    @Override
    public void buildAssets(ReversePerformContext context) {
        Map<Long, PerformHisReverseInfo> skuId2HisInfos = performDomainService.buildPerformHisReverseInfoMapBaseAssets(context);

        context.setReverseInfos(Lists.newArrayList(skuId2HisInfos.values()));
    }

    @Override
    public void buildTasks(ReversePerformContext context) {

    }
}