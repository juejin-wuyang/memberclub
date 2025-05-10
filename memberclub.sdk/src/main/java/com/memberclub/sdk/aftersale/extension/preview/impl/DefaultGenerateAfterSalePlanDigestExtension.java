/**
 * @(#)DefaultGenerateAfterSalePlanDigestExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.sdk.aftersale.extension.preview.GenerateAfterSalePlanDigestExtension;
import com.memberclub.sdk.aftersale.service.domain.AfterSaleDomainService;
import lombok.SneakyThrows;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认的售后计划摘要生成", bizScenes = {
        //@Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})
})
public class DefaultGenerateAfterSalePlanDigestExtension implements GenerateAfterSalePlanDigestExtension {

    @SneakyThrows
    @Override
    public void generateDigest(AfterSalePreviewContext context) {
        AfterSaleDomainService.generateDigest(context);
    }
}