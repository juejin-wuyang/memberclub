/**
 * @(#)PerformService.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.service;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.perform.PerformCmd;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformResp;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import com.memberclub.domain.exception.MemberException;
import com.memberclub.sdk.common.Monitor;
import com.memberclub.sdk.perform.extension.build.PerformAcceptOrderExtension;
import com.memberclub.sdk.perform.extension.build.PerformSeparateOrderExtension;
import com.memberclub.sdk.perform.extension.execute.PerformExecuteExtension;
import com.memberclub.sdk.perform.extension.period.PeriodPerformExecuteExtension;
import com.memberclub.sdk.perform.extension.reverse.ReversePerformExtension;
import com.memberclub.sdk.perform.service.domain.PerformDataObjectBuildFactory;
import com.memberclub.sdk.perform.service.domain.PerformDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class PerformBizService {

    @Autowired
    private ExtensionManager em;

    @Autowired
    private PerformDomainService performDomainService;

    @Autowired
    private PerformDataObjectBuildFactory performDataObjectBuildFactory;

    @UserLog(domain = LogDomainEnum.PERFORM)
    public PerformResp periodPerform(OnceTaskDO task) {
        PeriodPerformContext context = performDataObjectBuildFactory.buildPeriodPerformContext(task);
        PeriodPerformExecuteExtension extension =
                em.getExtension(BizScene.of(context.getBizType()), PeriodPerformExecuteExtension.class);
        extension.buildContext(task, context);

        PerformResp resp = new PerformResp();
        try {
            extension.periodPerform(context);
            resp.setSuccess(true);
            resp.setNeedRetry(false);
            return resp;
        } catch (MemberException e) {
            CommonLog.error("周期履约异常 task:{}", task, e);
            resp.setSuccess(false);
            resp.setNeedRetry(e.getCode().isNeedRetry());
            return resp;
        }
    }


    public void reversePerform(AfterSaleApplyContext context) {
        ReversePerformContext reversePerformContext = performDomainService.buildReversePerformContext(context);

        em.getExtension(context.toBizScene(), ReversePerformExtension.class).reverse(reversePerformContext);
    }


    @UserLog(domain = LogDomainEnum.PERFORM)
    @Retryable(initialDelaySeconds = 1, multiplier = 2.0, maxDelaySeconds = 10, throwException = true)
    public PerformResp perform(PerformCmd cmd) {
        PerformResp resp = new PerformResp();
        try {
            String preBuildScene = em.getSceneExtension(BizScene.of(cmd.getBizType().getCode()))
                    .buildPreBuildPerformContextScene(cmd);

            PerformContext context = em.getExtension(BizScene.of(cmd.getBizType().getCode(), preBuildScene),
                    PerformAcceptOrderExtension.class).acceptOrder(cmd);

            if (context.isSkipPerform()) {
                if (MemberOrderStatusEnum.isPerformed(context.getMemberOrder().getStatus().getCode())) {
                    resp.setSuccess(true);
                    resp.setNeedRetry(false);
                } else {
                    resp.setSuccess(true);
                    resp.setNeedRetry(true);
                }
                Monitor.PERFORM.counter(cmd.getBizType(),
                        "retryTimes", cmd.getRetryTimes(), "skip", true, "result", resp.isSuccess());

                return resp;
            }

            String separtateOrderScene = em.getSceneExtension(BizScene.of(cmd.getBizType().getCode()))
                    .buildSeparateOrderScene(context);
            em.getExtension(BizScene.of(cmd.getBizType().getCode(), separtateOrderScene),
                    PerformSeparateOrderExtension.class).separateOrder(context);

            //execute Context
            String executeScene = em.getSceneExtension(BizScene.of(cmd.getBizType().getCode()))
                    .buildPerformContextExecuteScene(context);
            em.getExtension(BizScene.of(cmd.getBizType().getCode(), executeScene),
                    PerformExecuteExtension.class).execute(context);

            resp.setSuccess(true);
            resp.setNeedRetry(false);

            Monitor.PERFORM.counter(cmd.getBizType(),
                    "retryTimes", cmd.getRetryTimes(), "skip", false, "result", resp.isSuccess());
            CommonLog.info("履约流程成功:{}", cmd);
        } catch (Throwable e) {
            CommonLog.error("内部履约流程异常,需要重试:{}", cmd, e);

            Monitor.PERFORM.counter(cmd.getBizType(),
                    "retryTimes", cmd.getRetryTimes(), "skip", false, "result", "exception");

            throw e;/*
            resp.setSuccess(false);
            resp.setNeedRetry(true);*/
        }

        //todo 处理 失败 重试,需要由外层注解处理!
        return resp;
    }
}