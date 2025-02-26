package com.memberclub.sdk.oncetask.aftersale.extension;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.entity.trade.OnceTask;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import com.memberclub.infrastructure.mybatis.mappers.trade.OnceTaskDao;
import org.springframework.beans.factory.annotation.Autowired;

@ExtensionProvider(desc = "抖音券包 任务过期退任务 Domain 层扩展点", bizScenes =
        {@Route(bizType = BizTypeEnum.DEFAULT, scenes = {SceneEnum.DEFAULT_SCENE})})
public class DefaultOnceTaskDomainExtension implements OnceTaskDomainExtension {

    @Autowired
    private OnceTaskDao onceTaskDao;

    @Override
    public void onCreatedExpireRefundTask(PerformContext context, OnceTaskDO onceTaskDO) {
        OnceTask task = PerformConvertor.INSTANCE.toOnceTask(onceTaskDO);
        int cnt = onceTaskDao.insertIgnoreBatch(Lists.newArrayList(task));
        CommonLog.warn("过期退任务写入数量 cnt:{}", cnt);
    }
}
