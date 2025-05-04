/**
 * @(#)OnceTaskTriggerDomainService.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.job;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerCmd;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerContext;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.oncetask.trigger.extension.OnceTaskTriggerExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.memberclub.infrastructure.dynamic_config.SwitchEnum.ONCE_TASK_SCAN_PERIOD_PERFORM_ELASPED_DAYS;

/**
 * author: 掘金五阳
 */
@Service
public class OnceTaskTriggerBizService {

    @Autowired
    private ExtensionManager extensionManager;


    public void triggerPeriodPerform(OnceTaskTriggerCmd cmd) {
        cmd.setTaskType(TaskTypeEnum.PERIOD_PERFORM);

        long minStime =
                TimeUtil.now() - TimeUnit.DAYS.toMillis(ONCE_TASK_SCAN_PERIOD_PERFORM_ELASPED_DAYS.getInt(cmd.getBizType().getCode()));

        long maxStime = TimeUtil.now() +
                TimeUnit.DAYS.toMillis(SwitchEnum.ONCE_TASK_SCAN_PERIOD_PERFORM_PRE_DAYS.getInt(cmd.getBizType().getCode()));

        cmd.setStatus(Lists.newArrayList(OnceTaskStatusEnum.FAIL, OnceTaskStatusEnum.INIT, OnceTaskStatusEnum.PROCESSING));
        cmd.setMinTriggerStime(minStime);
        cmd.setMaxTriggerStime(maxStime);

        trigger(cmd);
    }

    public void triggerFinanceExpire(OnceTaskTriggerCmd cmd) {
        cmd.setTaskType(TaskTypeEnum.FINANCE_EXPIRE);
        cmd.setStatus(Lists.newArrayList(OnceTaskStatusEnum.FAIL, OnceTaskStatusEnum.INIT, OnceTaskStatusEnum.PROCESSING));

        trigger(cmd);
    }

    public void trigger(OnceTaskTriggerCmd cmd) {
        OnceTaskTriggerContext context = new OnceTaskTriggerContext();
        context.setBizType(cmd.getBizType());
        context.setUserIds(cmd.getUserIds());
        context.setTaskGroupIds(cmd.getTaskGroupIds());
        context.setStatus(cmd.getStatus());
        context.setTaskType(cmd.getTaskType());
        context.setNow(TimeUtil.now());
        context.setMinTriggerStime(cmd.getMinTriggerStime());
        context.setMaxTriggerStime(cmd.getMaxTriggerStime());
        context.setSuccessCount(new AtomicLong(0));
        context.setFailCount(new AtomicLong(0));
        context.setTotalCount(new AtomicLong(0));

        extensionManager.getExtension(BizScene.of(cmd.getBizType(), cmd.getTaskType().getCode() + ""),
                OnceTaskTriggerExtension.class).trigger(context);
    }
}