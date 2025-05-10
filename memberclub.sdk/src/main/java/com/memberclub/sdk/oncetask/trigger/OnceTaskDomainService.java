/**
 * @(#)OnceTaskTriggerDomainService.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.trigger;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.execute.OnceTaskExecuteContext;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerContext;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerJobContext;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mapstruct.CommonConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import com.memberclub.sdk.oncetask.aftersale.extension.OnceTaskRepositoryExtension;
import com.memberclub.sdk.oncetask.trigger.extension.OnceTaskTriggerExtension;
import com.memberclub.sdk.perform.service.domain.PerformDataObjectBuildFactory;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

/**
 * author: 掘金五阳
 */
@Service
public class OnceTaskDomainService {

    public static final Logger LOG = LoggerFactory.getLogger(OnceTaskDomainService.class);

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private OnceTaskDao onceTaskDao;

    @Autowired
    private PerformDataObjectBuildFactory performDataObjectBuildFactory;

    public void onCreatedExpireRefundTask(PerformContext context) {
        OnceTaskDO onceTaskDO = performDataObjectBuildFactory.buildExpireRefundOnceTaskOnPerformSuccess(context);

        extensionManager.getExtension(BizScene.of(context.getBizType()),
                OnceTaskRepositoryExtension.class).onCreatedExpireRefundTask(context, onceTaskDO);
    }


    public void scanTasks(OnceTaskTriggerJobContext context, Consumer<List<OnceTask>> consumer) {
        Long minId = 0L;
        int page = 10;
        List<OnceTask> tasks = null;
        String tableName = "once_task_hint";
        if (ApplicationContextUtils.isUnitTest()) {
            tableName = "once_task";
        }
        do {
            tasks = onceTaskDao.scanTasks(tableName, context.getContext().getBizType().getCode(),
                    context.getContext().getUserIds(), context.getContext().getTaskGroupIds(),
                    context.getContext().getMinTriggerStime(), context.getContext().getMaxTriggerStime(),
                    CollectionUtilEx.mapToSet(context.getContext().getStatus(), OnceTaskStatusEnum::getCode),
                    context.getContext().getTaskType().getCode(), minId, page);
            if (CollectionUtils.isNotEmpty(tasks)) {
                minId = tasks.get(tasks.size() - 1).getId();
            }

            CommonLog.info("扫描onceTask :{}", tasks);

            consumer.accept(tasks);
        } while (CollectionUtils.isNotEmpty(tasks));
        context.reduceMonitor();
    }

    public void execute(OnceTaskTriggerJobContext context, List<OnceTask> tasks) {
        for (OnceTask task : tasks) {
            OnceTaskDO taskDO = performDataObjectBuildFactory.toOnceTaskDO(task);
            OnceTaskExecuteContext executeContext = CommonConvertor.INSTANCE.toOnceTaskExecuteContext(context.getContext());
            executeContext.setOnceTask(taskDO);
            try {
                context.totalCount++;
                extensionManager.getExtension(BizScene.of(context.getContext().getBizType(), context.getContext().getTaskType().getCode() + ""),
                        OnceTaskTriggerExtension.class).execute(executeContext);
                LOG.info("执行任务成功 bizType:{}, taskType:{}, taskToken:{}, taskGroupId:{}",
                        taskDO.getBizType(), taskDO.getTaskType(), taskDO.getTaskToken(), task.getTaskGroupId());
                context.successCount++;
            } catch (Throwable e) {
                LOG.error("执行 OnceTask任务失败 task:{}", taskDO, e);
                context.failCount++;
            }
        }
    }

    public void monitor(OnceTaskTriggerContext context, Exception e) {
        String result = e != null ? "失败" : "成功";
        LOG.info("{} 任务执行完成,结果:{} 总数:{} 成功数量:{}, 失败数量:{}",
                context.getTaskType(), result,
                context.getTotalCount().get(),
                context.getSuccessCount().get(),
                context.getFailCount().get());
    }

    public void onStartExecute(OnceTaskExecuteContext context, OnceTaskDO task) {
        task.onStartExecute(context);
        LambdaUpdateWrapper<OnceTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OnceTask::getUserId, task.getUserId());
        wrapper.eq(OnceTask::getTaskToken, task.getTaskToken());
        wrapper.eq(OnceTask::getTaskType, context.getTaskType().getCode());
        wrapper.ne(OnceTask::getStatus, OnceTaskStatusEnum.SUCCESS.getCode());
        wrapper.set(OnceTask::getStatus, task.getStatus().getCode());

        int cnt = onceTaskDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("执行任务时,更新为执行中异常");
        }
    }

    public void onExecuteSuccess(OnceTaskExecuteContext context, OnceTaskDO task) {
        task.onExecuteSuccess(context);
        LambdaUpdateWrapper<OnceTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OnceTask::getUserId, task.getUserId());
        wrapper.eq(OnceTask::getTaskToken, task.getTaskToken());
        wrapper.eq(OnceTask::getTaskType, context.getTaskType().getCode());
        wrapper.set(OnceTask::getStatus, task.getStatus().getCode());

        int cnt = onceTaskDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("执行任务时,更新为执行中异常");
        }
    }

    public void onExecuteFail(OnceTaskExecuteContext context, OnceTaskDO task) {
        task.onExecuteFail(context);
        LambdaUpdateWrapper<OnceTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OnceTask::getUserId, task.getUserId());
        wrapper.eq(OnceTask::getTaskToken, task.getTaskToken());
        wrapper.eq(OnceTask::getTaskType, context.getTaskType().getCode());
        wrapper.ne(OnceTask::getStatus, OnceTaskStatusEnum.SUCCESS.getCode());
        wrapper.set(OnceTask::getStatus, task.getStatus().getCode());

        int cnt = onceTaskDao.update(null, wrapper);
        if (cnt < 1) {
            throw ResultCode.DATA_UPDATE_ERROR.newException("执行任务时,更新为执行中异常");
        }
    }
}