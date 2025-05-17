/**
 * @(#)LockService.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.service.domain;

import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.common.LockContext;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.sdk.config.extension.BizConfigTable;
import com.memberclub.sdk.config.service.ConfigService;
import com.memberclub.sdk.lock.LockService;
import com.memberclub.sdk.lock.extension.LockExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class MemberTradeLockService {

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private LockService lockService;

    @Autowired
    private ConfigService configService;

    public LockExtension getLockExtension(BizTypeEnum bizTypeEnum) {
        return extensionManager.getExtension(BizScene.of(bizTypeEnum), LockExtension.class);
    }

    /************************************ 购买提单 ********************************************/

    public void lockOnPrePurchase(PurchaseSubmitContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getBizType()).
                lockScene("purchase").
                lockMode(LockMode.LOCK_USER).
                userId(context.getUserId()).
                build();
        boolean lockable = getLockExtension(context.getBizType()).buildOnPrePurchase(lockContext, context);

        if (lockable) {
            Long lockValue = lockService.lock(lockContext);
            context.setLockValue(lockValue);
            return;
        }
        CommonLog.warn("提单前, 不加锁 lockContext:{}", lockContext);
    }


    public void unlockOnPurchaseSuccess(PurchaseSubmitContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getBizType())
                .lockScene("purchase")
                .lockMode(LockMode.LOCK_USER)
                .userId(context.getUserId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPurchaseSuccess(lockContext, context);

        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.warn("提单主流程成功, 不释放锁 lockContext:{}", lockContext);
    }


    public void lockOnPrePurchaseCancel(PurchaseCancelContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getCmd().getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getCmd().getBizType()).
                lockScene("purchase").
                lockMode(LockMode.LOCK_USER).
                userId(context.getCmd().getUserId()).
                build();
        boolean lockable = getLockExtension(context.getCmd().getBizType()).buildOnPrePurchaseCancel(lockContext, context);

        if (lockable) {
            Long lockValue = lockService.lock(lockContext);
            context.setLockValue(lockValue);
            return;
        }
        CommonLog.warn("取消前, 不加锁 lockContext:{}", lockContext);
    }


    public void unlockOnPurchaseCancelSuccess(PurchaseCancelContext context) {
        CommonLog.error("回滚阶段尝试解锁");
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getCmd().getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getCmd().getBizType())
                .lockScene("purchase")
                .lockMode(LockMode.LOCK_USER)
                .userId(context.getCmd().getUserId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getCmd().getBizType()).buildOnPurchaseCancel(lockContext, context);
        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.error("取消订单, 不释放锁 lockContext:{}", lockContext);
    }

    public void unlockOnPurchaseFail(PurchaseSubmitContext context) {
        CommonLog.error("回滚阶段尝试解锁");
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getBizType())
                .lockScene("purchase")
                .lockMode(LockMode.LOCK_USER)
                .userId(context.getUserId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPurchaseFail(lockContext, context);
        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.error("提单失败, 不释放锁 lockContext:{}", lockContext);
    }

    /************************************ 履约主流程 ********************************************/

    public void lockOnPrePerform(PerformContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getBizType()).
                lockScene("perform").
                lockMode(table.getLockMode()).
                userId(context.getUserId()).
                tradeId(context.getTradeId()).
                build();
        boolean lockable = getLockExtension(context.getBizType()).buildOnPrePerform(lockContext, context);

        if (lockable) {
            Long lockValue = lockService.lock(lockContext);

            context.setLockValue(lockValue);
            context.getCmd().setLockValue(context.getLockValue());
            return;
        }
        CommonLog.error("履约主流程前, 不加锁 lockContext:{}", lockContext);
    }


    public void unlockOnPerformSuccess(PerformContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getBizType())
                .lockScene("perform")
                .lockMode(table.getLockMode())
                .userId(context.getUserId())
                .tradeId(context.getTradeId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPerformSuccess(lockContext, context);

        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.error("履约主流程成功, 不释放锁 lockContext:{}", lockContext);
    }

    public void unlockOnPerformFail(PerformContext context) {
        CommonLog.error("回滚阶段尝试解锁");
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getBizType())
                .lockScene("perform")
                .lockMode(table.getLockMode())
                .userId(context.getUserId())
                .tradeId(context.getTradeId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPerformFail(lockContext, context);
        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.error("履约主流程失败, 不释放锁 lockContext:{}", lockContext);
    }

    /************************************ 周期履约 ********************************************/
    public void lockOnPrePeriodPerform(PeriodPerformContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getBizType()).
                lockScene("period_perform").
                lockMode(table.getLockMode()).
                userId(context.getUserId()).
                tradeId(context.getTradeId()).
                build();
        boolean lockable = getLockExtension(context.getBizType()).buildOnPrePeriodPerform(lockContext, context);

        if (!lockable) {
            CommonLog.error("周期履约流程前, 不加锁 lockContext:{}", lockContext);
            return;
        }

        Long lockValue = lockService.lock(lockContext);

        context.setLockValue(lockValue);
    }

    public void unlockOnPeriodPerformSuccess(PeriodPerformContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getBizType()).
                lockScene("period_perform").
                lockMode(table.getLockMode()).
                userId(context.getUserId()).
                lockValue(context.getLockValue()).
                tradeId(context.getTradeId()).
                build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPeriodPerformSuccess(lockContext, context);

        if (!unlockable) {
            CommonLog.error("周期履约流程成功, 不释放锁 lockContext:{}", lockContext);
            return;
        }
        lockService.unlock(lockContext);
    }

    public void unlockOnPeriodPerformFail(PeriodPerformContext context) {
        CommonLog.error("周期履约回滚阶段尝试解锁");
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getBizType())
                .lockScene("perform")
                .lockMode(table.getLockMode())
                .userId(context.getUserId())
                .tradeId(context.getTradeId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getBizType()).buildOnPeriodPerformFail(lockContext, context);
        if (unlockable) {
            lockService.unlock(lockContext);
            return;
        }
        CommonLog.error("周期履约流程失败, 不释放锁 lockContext:{}", lockContext);
    }
    /************************************ 周期履约 ********************************************/


    /************************************ 售后 ********************************************/
    public void lockOnPreAfterSale(AfterSaleApplyContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getApplyCmd().getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getApplyCmd().getBizType()).
                lockScene("after_sale").
                lockMode(table.getLockMode()).
                userId(context.getApplyCmd().getUserId()).
                tradeId(context.getApplyCmd().getTradeId()).
                build();
        boolean lockable = getLockExtension(context.getApplyCmd().getBizType()).buildOnAfterSale(lockContext, context);

        if (!lockable) {
            CommonLog.error("售后流程钱不加锁 lockContext:{}", lockContext);
            return;
        }
        Long lockValue = lockService.lock(lockContext);

        context.setLockValue(lockValue);
    }

    public void unlockOnAfterSaleSuccess(AfterSaleApplyContext context) {
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getApplyCmd().getBizType()));

        LockContext lockContext = LockContext.builder().
                bizType(context.getApplyCmd().getBizType()).
                lockScene("after_sale").
                lockMode(table.getLockMode()).
                userId(context.getApplyCmd().getUserId()).
                lockValue(context.getLockValue()).
                tradeId(context.getApplyCmd().getTradeId()).
                build();
        boolean unlockable = getLockExtension(context.getApplyCmd().getBizType()).buildOnAfterSaleSuccess(lockContext, context);

        if (!unlockable) {
            CommonLog.error("售后成功不释放锁 lockContext:{}", lockContext);
            return;
        }
        lockService.unlock(lockContext);
    }

    public void unlockOnAfterSaleFail(AfterSaleApplyContext context, Exception e) {
        CommonLog.error("售后回滚阶段尝试解锁");
        BizConfigTable table = configService.findConfigTable(BizScene.of(context.getApplyCmd().getBizType()));

        LockContext lockContext = LockContext.builder().bizType(context.getApplyCmd().getBizType())
                .lockScene("after_sale")
                .lockMode(table.getLockMode())
                .userId(context.getApplyCmd().getUserId())
                .tradeId(context.getApplyCmd().getTradeId())
                .lockValue(context.getLockValue())
                .build();
        boolean unlockable = getLockExtension(context.getApplyCmd().getBizType()).buildOnAfterSaleFail(lockContext, context, e);
        if (!unlockable) {
            CommonLog.error("售后失败不释放锁 lockContext:{}", lockContext);
            return;
        }
        lockService.unlock(lockContext);
    }

    /************************************ 售后 ********************************************/
}