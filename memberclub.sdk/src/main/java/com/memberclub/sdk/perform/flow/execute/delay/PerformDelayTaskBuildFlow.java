/**
 * @(#)BuildDelayPerformOnceTaskFlow.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.flow.execute.delay;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.flow.FlowNode;
import com.memberclub.domain.context.perform.delay.DelayItemContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.sdk.perform.extension.delay.DelayPerformTaskExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class PerformDelayTaskBuildFlow extends FlowNode<DelayItemContext> {


    @Autowired
    private ExtensionManager extensionManager;

    @Override
    public void process(DelayItemContext context) {
        List<OnceTaskDO> tasks = Lists.newArrayList();

        OnceTaskDO task = extensionManager.getExtension(context.getPerformContext().toDefaultScene(),
                DelayPerformTaskExtension.class).buildTask(context, context.getItems());
        tasks.add(task);

        context.setTasks(tasks);
    }
}