/**
 * @(#)DefaultGenerateAfterSalePlanDigestExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleExecuteCmd;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCoreResult;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.aftersale.extension.preview.AfterSalePreviewCheckExtension;
import lombok.SneakyThrows;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认的售后计划摘要生成", bizScenes = {
        //@Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
})
public class DefaultPreviewTokenExtension implements AfterSalePreviewCheckExtension {

    @SneakyThrows
    @Override
    public AfterSalePreviewCoreResult generatePreviewCoreResult(AfterSalePreviewContext context) {
        long expireTime = TimeUtil.now() + SwitchEnum.AFTER_SALE_PREVIEW_TOKEN_EXPIRE_TIME_SECONDS.getInt(context.getCmd().getBizType().getCode());
        return context.toCoreResult(expireTime);
    }

    @Override
    public void check(AfterSalePreviewCoreResult cachedResult, AfterSaleExecuteCmd afterSaleExecuteCmd) {
        if (cachedResult.getRefundWay() == afterSaleExecuteCmd.getRefundWay() && cachedResult.getRecommendRefundPrice() == afterSaleExecuteCmd.getRecommendRefundPrice()) {
            CommonLog.warn("售后受理校验 和预览结果一致 result:{}", cachedResult);
            return;
            //
        }

        CommonLog.error("售后受理校验不一致 上次校验结果：{}，本次结果:{}", afterSaleExecuteCmd, cachedResult);
        throw AftersaleUnableCode.CONDITION_OCCUR.newException();
    }

}