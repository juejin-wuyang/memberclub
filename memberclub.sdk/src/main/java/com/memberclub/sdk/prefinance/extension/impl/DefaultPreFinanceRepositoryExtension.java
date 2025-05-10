/**
 * @(#)DefaultPreFinanceDomainExtension.java, 一月 28, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.extension.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import com.memberclub.sdk.prefinance.extension.PreFinanceRepositoryExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认预结算 Domain扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultPreFinanceRepositoryExtension implements PreFinanceRepositoryExtension {

    @Autowired
    private OnceTaskDao onceTaskDao;

    @Override
    public void onCreateExpiredTask(PreFinanceContext context, List<OnceTaskDO> taskList) {
        List<OnceTask> tasks = taskList.stream()
                .map(PerformConvertor.INSTANCE::toOnceTask)
                .collect(Collectors.toList());
        int cnt = onceTaskDao.insertIgnoreBatch(tasks);
        CommonLog.info("创建结算过期处理任务 count:{}, {}", cnt, taskList);
    }

}