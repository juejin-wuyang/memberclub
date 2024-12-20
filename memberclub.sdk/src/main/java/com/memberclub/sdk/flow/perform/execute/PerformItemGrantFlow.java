/**
 * @(#)PerformItemGrantFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.flow.perform.execute;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.dataobject.perform.PerformItemContext;
import com.memberclub.domain.dataobject.perform.PerformItemDO;
import com.memberclub.domain.dataobject.perform.execute.ItemGrantResult;
import com.memberclub.domain.dataobject.perform.execute.ItemGroupGrantResult;
import com.memberclub.sdk.extension.perform.execute.PerformItemGrantExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@Service
public class PerformItemGrantFlow extends FlowNode<PerformItemContext> {

    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(PerformItemContext context) {
        String scene = extensionManager.getSceneExtension(context.getPerformContext().toDefaultScene())
                .buildPerformItemGrantExtensionScene(context);

        PerformItemGrantExtension extension =
                extensionManager.getExtension(BizScene.of(context.getPerformContext().getBizType().toBizType(), scene),
                        PerformItemGrantExtension.class);
        ItemGroupGrantResult result = extension.grant(context, context.getItems());

        Map<String, PerformItemDO> token2Items =
                context.getItems().stream().collect(Collectors.toMap(PerformItemDO::getItemToken, Function.identity()));

        for (Map.Entry<String, ItemGrantResult> entry : result.getGrantMap().entrySet()) {
            token2Items.get(entry.getKey()).setBatchCode(entry.getValue().getBatchCode());
        }
        context.setResult(result);
    }
}