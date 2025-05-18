/**
 * @(#)BizSecneBuildExtension.java, 十二月 16, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.common.extension;

import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformItemContext;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "构建BizScene 通用扩展点", type = ExtensionType.COMMON, must = false)
public interface BizSceneBuildExtension extends BaseExtension {

    default String buildPerformItemGrantExtensionScene(PerformItemContext context) {
        return String.valueOf(context.getItems().get(0).getRightType().getCode());
    }

    default String buildPreBuildPerformContextScene(PerformCmd cmd) {
        return SceneEnum.DEFAULT_SCENE.getValue();
    }

    default String buildSeparateOrderScene(PerformContext context) {
        return SceneEnum.SCENE_MONTH_CARD.getValue();
    }

    default String buildPerformContextExecuteScene(PerformContext performContext) {
        return SceneEnum.SCENE_MONTH_CARD.getValue();
    }

    default String buildAftersalePreviewScene(AfterSalePreviewContext context) {
        if (context.getCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT) {
            return SceneEnum.SCENE_SYSTEM_REFUND_4_ORDER_PAY_TIMEOUT.getValue();
        }
        if (context.getCmd().getSource() == AftersaleSourceEnum.SYSTEM_REFUND_4_PERFORM_FAIL) {
            return SceneEnum.SCENE_SYSTEM_REFUND_4_PERFORM_FAIL.getValue();
        }

        return SceneEnum.SCENE_AFTERSALE_MONTH_CARD.getValue();
    }

    default String buildAftersaleApplyScene(AfterSaleApplyContext context) {
        return SceneEnum.SCENE_AFTERSALE_MONTH_CARD.getValue();
    }
}