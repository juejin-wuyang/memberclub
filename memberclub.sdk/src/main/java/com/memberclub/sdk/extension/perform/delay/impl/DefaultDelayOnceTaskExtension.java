/**
 * @(#)DefaultDelayOnceTaskExtension.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.extension.perform.delay.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionImpl;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.common.TaskTypeEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.infrastructure.mapstruct.ConvertorMethod;
import com.memberclub.sdk.extension.perform.delay.DelayOnceTaskExtension;

/**
 * author: 掘金五阳
 */
@ExtensionImpl(desc = "DemoMember 延迟任务扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DefaultDelayOnceTaskExtension implements DelayOnceTaskExtension {

    @Override
    public OnceTaskDO buildTask(PerformContext context, MemberPerformItemDO item) {
        OnceTaskDO task = ConvertorMethod.buildTaskForPeriodPerform(context, item);
        task.setTaskType(TaskTypeEnum.PERIOD_PERFORM);
        task.setTaskContentClassName();
        task.setContent();
        return task;
    }
}