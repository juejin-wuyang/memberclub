/**
 * @(#)AftersalePreviewService.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.service;

import com.google.common.collect.Lists;
import com.memberclub.common.extension.BizSceneBuildExtension;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.log.LogDomainEnum;
import com.memberclub.common.log.UserLog;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyCmd;
import com.memberclub.domain.context.aftersale.apply.AftersaleApplyResponse;
import com.memberclub.domain.context.aftersale.contant.AftersaleUnableCode;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewResponse;
import com.memberclub.domain.context.oncetask.common.OnceTaskStatusEnum;
import com.memberclub.domain.context.oncetask.common.TaskTypeEnum;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerCmd;
import com.memberclub.domain.context.oncetask.trigger.OnceTaskTriggerContext;
import com.memberclub.domain.exception.AfterSaleUnableException;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.sdk.aftersale.extension.apply.AfterSaleApplyExtension;
import com.memberclub.sdk.aftersale.extension.preview.AftersaleCollectDataExtension;
import com.memberclub.sdk.aftersale.extension.preview.AftersalePreviewExtension;
import com.memberclub.sdk.oncetask.trigger.extension.OnceTaskTriggerExtension;
import com.memberclub.sdk.util.PriceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.memberclub.common.extension.ExtensionManager.extension;
import static com.memberclub.infrastructure.dynamic_config.SwitchEnum.ONCE_TASK_SCAN_AFTERSALE_EXPIRE_REFUND_ELASPED_DAYS;

/**
 * author: 掘金五阳
 */
@Service
public class AfterSaleBizService {

    @Autowired
    private ExtensionManager em;

    @UserLog(domain = LogDomainEnum.AFTER_SALE)
    public AfterSalePreviewResponse preview(AfterSalePreviewCmd cmd) {
        AfterSalePreviewResponse respose = new AfterSalePreviewResponse();
        try {
            AfterSalePreviewContext context = doPreview(cmd);
            respose.setSuccess(true);
            respose.setRecommendRefundPriceYuan(PriceUtils.change2Yuan(context.getRecommendRefundPrice()));
            respose.setRefundType(context.getRefundType());
            respose.setRefundWay(context.getRefundWay());
            respose.setPreviewToken(context.getPreviewToken());
        } catch (Exception e) {
            if (extractException(e) != null) {
                Throwable t = extractException(e);
                CommonLog.warn("售后预览流程返回不可退", e);

                respose.setUnableCode(((AfterSaleUnableException) t).getUnableCode());
                respose.setSuccess(false);
                respose.setUnableTip(e.getMessage());
            } else {
                CommonLog.error("售后预览流程异常", e);
                respose.setSuccess(false);
                respose.setUnableCode(AftersaleUnableCode.INTERNAL_ERROR.getCode());
                respose.setUnableTip(AftersaleUnableCode.INTERNAL_ERROR.toString());
            }
        }
        return respose;
    }

    private Throwable extractException(Exception e) {
        Throwable t = null;
        if (e instanceof AfterSaleUnableException) {
            t = e;
        } else if (e.getCause() instanceof AfterSaleUnableException) {
            t = e.getCause();
        }
        return t;
    }

    public AfterSalePreviewContext doPreview(AfterSalePreviewCmd cmd) {
        AftersaleCollectDataExtension aftersaleCollectDataExtension = extension(cmd.getBizType().toBizScene(), AftersaleCollectDataExtension.class);
        AfterSalePreviewContext context = aftersaleCollectDataExtension.collect(cmd);
        context.setCmd(cmd);
        context.setPreviewBeforeApply(cmd.isPreviewBeforeApply());

        BizSceneBuildExtension bizSceneBuildExtension = em.getSceneExtension(cmd.getBizType().toBizScene());
        String scene = bizSceneBuildExtension.buildAftersalePreviewScene(context);

        AftersalePreviewExtension previewExtension = extension(cmd.getBizType().toBizScene(scene), AftersalePreviewExtension.class);

        previewExtension.preview(context);
        return context;
    }

    @UserLog(domain = LogDomainEnum.AFTER_SALE)
    public AftersaleApplyResponse apply4RefundOnly(AftersaleApplyCmd cmd) {
        AfterSaleApplyContext context = new AfterSaleApplyContext();
        context.setApplyCmd(cmd);

        BizSceneBuildExtension bizSceneBuildExtension = em.getSceneExtension(BizScene.of(cmd.getBizType().getCode()));
        String applyExtensionScene = bizSceneBuildExtension.buildAftersaleApplyScene(context);
        context.setScene(applyExtensionScene);
        AftersaleApplyResponse response = new AftersaleApplyResponse();
        try {
            //调用受理方法
            AfterSaleApplyExtension extension = em.getExtension(BizScene.of(cmd.getBizType().getCode(),
                    applyExtensionScene), AfterSaleApplyExtension.class);
            extension.apply4OnlyRefundMoney(context);

            response.setSuccess(true);
            //response.setRefundWay();
            // TODO: 2025/1/1 处理返回值
        } catch (Exception e) {
            if (extractException(e) != null) {
                Throwable t = extractException(e);
                CommonLog.warn("售后受理前验证流程返回不可退", e);
                response.setUnableCode(((AfterSaleUnableException) t).getUnableCode());
                response.setSuccess(false);
                response.setUnableTip(e.getMessage());
            } else {
                CommonLog.error("售后受理流程异常", e);
                response.setSuccess(false);
                response.setUnableCode(AftersaleUnableCode.INTERNAL_ERROR.getCode());
                response.setUnableTip(AftersaleUnableCode.INTERNAL_ERROR.toString());
            }
        }
        return response;
    }

    @UserLog(domain = LogDomainEnum.AFTER_SALE)
    public AftersaleApplyResponse apply(AftersaleApplyCmd cmd) {
        cmd.isValid();
        AfterSaleApplyContext context = new AfterSaleApplyContext();
        context.setApplyCmd(cmd);

        BizSceneBuildExtension bizSceneBuildExtension = em.getSceneExtension(BizScene.of(cmd.getBizType().getCode()));
        String applyExtensionScene = bizSceneBuildExtension.buildAftersaleApplyScene(context);
        context.setScene(applyExtensionScene);
        AftersaleApplyResponse response = new AftersaleApplyResponse();
        try {
            //调用受理方法
            em.getExtension(BizScene.of(cmd.getBizType().getCode(), applyExtensionScene),
                    AfterSaleApplyExtension.class).apply(context);
            response.setSuccess(true);
            //response.setRefundWay();
            // TODO: 2025/1/1 处理返回值
        } catch (Exception e) {
            if (extractException(e) != null) {
                Throwable t = extractException(e);
                CommonLog.warn("售后受理前验证流程返回不可退", e);
                response.setUnableCode(((AfterSaleUnableException) t).getUnableCode());
                response.setSuccess(false);
                response.setUnableTip(e.getMessage());
            } else {
                CommonLog.error("售后受理流程异常", e);
                response.setSuccess(false);
                response.setUnableCode(AftersaleUnableCode.INTERNAL_ERROR.getCode());
                response.setUnableTip(AftersaleUnableCode.INTERNAL_ERROR.toString());
            }
        }
        return response;
    }


    public void triggerRefund(OnceTaskTriggerCmd cmd) {
        cmd.setTaskType(TaskTypeEnum.AFTERSALE_EXPIRE_REFUND);

        long minStime =
                TimeUtil.now() - TimeUnit.DAYS.toMillis(ONCE_TASK_SCAN_AFTERSALE_EXPIRE_REFUND_ELASPED_DAYS.getInt(cmd.getBizType().getCode()));

        long maxStime = TimeUtil.now() +
                TimeUnit.DAYS.toMillis(SwitchEnum.ONCE_TASK_SCAN_PERIOD_PERFORM_PRE_DAYS.getInt(cmd.getBizType().getCode()));

        cmd.setStatus(Lists.newArrayList(OnceTaskStatusEnum.FAIL, OnceTaskStatusEnum.INIT, OnceTaskStatusEnum.PROCESSING));
        cmd.setMinTriggerStime(minStime);
        cmd.setMaxTriggerStime(maxStime);

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

        em.getExtension(BizScene.of(cmd.getBizType(), cmd.getTaskType().getCode() + ""),
                OnceTaskTriggerExtension.class).trigger(context);
    }
}