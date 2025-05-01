/**
 * @(#)CalculateOrderPeriodFlow.java, 十二月 20, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.build;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.sdk.perform.extension.build.PerformSeparateOrderExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PeriodCompute4WholeOrderFlow extends FlowNode<PerformContext> {
    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(PerformContext context) {
        String separtateOrderScene = extensionManager.getSceneExtension(BizScene.of(context.getBizType().getCode()))
                .buildSeparateOrderScene(context);

        extensionManager.getExtension(BizScene.of(context.getBizType().getCode(), separtateOrderScene), PerformSeparateOrderExtension.class)
                .buildTimeRange(context);
    }
}