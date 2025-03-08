/**
 * @(#)PerformItemGrantFlow.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.execute;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.execute.ItemGrantResult;
import com.memberclub.domain.context.perform.execute.ItemGroupGrantResult;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.perform.extension.execute.MemberRightsGrantExtension;
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
        String scene = extensionManager.getSceneExtension(context.toDefaultScene())
                .buildPerformItemGrantExtensionScene(context);

        MemberRightsGrantExtension extension =
                extensionManager.getExtension(BizScene.of(context.getBizType().getCode(), scene),
                        MemberRightsGrantExtension.class);
        ItemGroupGrantResult result = extension.grant(context, context.getItems());

        Map<String, MemberPerformItemDO> token2Items =
                context.getItems().stream().collect(Collectors.toMap(MemberPerformItemDO::getItemToken, Function.identity()));

        for (Map.Entry<String, ItemGrantResult> entry : result.getGrantMap().entrySet()) {
            token2Items.get(entry.getKey()).setBatchCode(entry.getValue().getBatchCode());
        }
        context.setResult(result);
    }
}