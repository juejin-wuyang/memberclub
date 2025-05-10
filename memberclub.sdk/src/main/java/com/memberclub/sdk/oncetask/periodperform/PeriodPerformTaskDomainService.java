/**
 * @(#)OnceTaskDomainService.java, 一月 05, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.oncetask.periodperform.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.perform.delay.DelayItemContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import com.memberclub.sdk.oncetask.periodperform.extension.PeriodPerformTaskRepositoryExtension;
import com.memberclub.sdk.perform.service.domain.PerformDataObjectBuildFactory;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum.*;

/**
 * author: 掘金五阳
 */
@DS("tradeDataSource")
@Service
public class PeriodPerformTaskDomainService {
    @Autowired
    private OnceTaskDao onceTaskDao;

    @Autowired
    private PerformDataObjectBuildFactory performDataObjectBuildFactory;

    @Autowired
    private ExtensionManager extensionManager;

    @Transactional(rollbackFor = Exception.class)
    public void onCreatePeriodPerformTask(DelayItemContext context) {
        List<OnceTask> tasks = context.getTasks().stream()
                .map(onceTaskDO -> PerformConvertor.INSTANCE.toOnceTask(onceTaskDO))
                .collect(Collectors.toList());
        extensionManager.getExtension(BizScene.of(context.getPerformContext().getBizType()),
                PeriodPerformTaskRepositoryExtension.class).onCreate(context, tasks);
    }

    public void buildActivePeriodTasks(ReversePerformContext context,
                                       Map<String, SubOrderReversePerformContext> subTradeId2SubOrderReversePerformContext) {
        for (Map.Entry<String, SubOrderReversePerformContext> entry : subTradeId2SubOrderReversePerformContext.entrySet()) {
            SubOrderReversePerformContext subOrderReversePerformContext = entry.getValue();

            LambdaQueryWrapper<OnceTask> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OnceTask::getUserId, context.getUserId())
                    .eq(OnceTask::getTaskGroupId, String.valueOf(subOrderReversePerformContext.getSubTradeId()))
                    .eq(OnceTask::getTaskType, TaskTypeEnum.PERIOD_PERFORM.getCode())
                    .in(OnceTask::getStatus, Lists.newArrayList(FAIL.getCode(), PROCESSING.getCode(), INIT.getCode()));

            List<OnceTask> tasks = onceTaskDao.selectList(queryWrapper);
            List<OnceTaskDO> taskDOList = tasks.stream().map(performDataObjectBuildFactory::toOnceTaskDO)
                    .collect(Collectors.toList());

            List<String> activeTaskTokens =
                    taskDOList.stream().map(OnceTaskDO::getTaskToken).collect(Collectors.toList());

            subOrderReversePerformContext.setActiveTasks(taskDOList);
            subOrderReversePerformContext.setActiveTaskTokens(activeTaskTokens);
        }
    }

    public void cancelActivatPeriodTasks(ReversePerformContext context,
                                         Map<String, SubOrderReversePerformContext> subTradeId2SubOrderReversePerformContext) {
        for (Map.Entry<String, SubOrderReversePerformContext> entry : subTradeId2SubOrderReversePerformContext.entrySet()) {
            if (CollectionUtils.isNotEmpty(entry.getValue().getActiveTasks())) {
                for (OnceTaskDO activeTask : entry.getValue().getActiveTasks()) {
                    activeTask.onCancel(context, entry.getValue());
                }

                LambdaUpdateWrapper<OnceTask> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(OnceTask::getUserId, context.getUserId())
                        .in(OnceTask::getTaskToken, entry.getValue().getActiveTaskTokens())
                        .set(OnceTask::getStatus, OnceTaskStatusEnum.CANCEL.getCode())
                        .set(OnceTask::getUtime, TimeUtil.now())
                ;

                extensionManager.getExtension(BizScene.of(context.getBizType()),
                        PeriodPerformTaskRepositoryExtension.class).onCancel(context, entry.getValue(), wrapper);

            }
        }
    }


}