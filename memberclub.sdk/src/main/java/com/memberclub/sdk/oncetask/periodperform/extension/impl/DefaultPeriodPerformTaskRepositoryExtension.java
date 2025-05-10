/**
 * @(#)DefaultOnceTaskDomainExtension.java, 一月 11, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.periodperform.extension.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.perform.delay.DelayItemContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.oncetask.periodperform.extension.PeriodPerformTaskRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认周期履约任务扩展点实现", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER)
})
public class DefaultPeriodPerformTaskRepositoryExtension implements PeriodPerformTaskRepositoryExtension {

    @Autowired
    private OnceTaskDao onceTaskDao;

    @Override
    public void onCreate(DelayItemContext context, List<OnceTask> tasks) {
        int count = onceTaskDao.insertIgnoreBatch(tasks);
        if (count >= tasks.size()) {
            CommonLog.warn("新增周期履约任务成功 count:{}, tasks:{}", count, tasks);
            Monitor.PERFORM_EXECUTE.counter(context.getPerformContext().getBizType(),
                    "task_create", true);
            return;
        }
        List<String> taskTokens = tasks.stream().map(OnceTask::getTaskToken).collect(Collectors.toList());
        List<OnceTask> taskFromDb = onceTaskDao.queryTasks(context.getPerformContext().getUserId(),
                taskTokens, TaskTypeEnum.PERIOD_PERFORM.getCode());
        if (taskFromDb.size() == count) {
            CommonLog.warn("幂等新增周期履约任务 count:{}, tasks:{}", count, tasks);
            Monitor.PERFORM_EXECUTE.counter(context.getPerformContext().getBizType(),
                    "task_create", "duplicated");
            return;
        }

        CommonLog.error("新增周期履约任务失败 dbCount:{}, expectCount:{}, dbtasks:{}", taskFromDb.size(), tasks.size(), taskFromDb);
        Monitor.PERFORM_EXECUTE.counter(context.getPerformContext().getBizType(),
                "task_create", false);
        throw ResultCode.PERIOD_PERFORM_TASK_CREATE_ERROR.newException();
    }

    @Override
    public void onCancel(ReversePerformContext reversePerformContext,
                         SubOrderReversePerformContext context,
                         LambdaUpdateWrapper<OnceTask> wrapper) {
        int cnt = onceTaskDao.update(null, wrapper);

        CommonLog.info("取消生效中的周期履约任务 taskTokens:{}, cnt:{}", context.getActiveTaskTokens(), cnt);
    }
}