/**
 * @(#)LockExtension.java, 一月 23, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.lock.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.context.aftersale.apply.AfterSaleApplyContext;
import com.memberclub.domain.context.common.LockContext;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.period.PeriodPerformContext;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.cancel.PurchaseCancelContext;
import com.memberclub.domain.exception.AftersaleExecuteException;

/**
 * @author wuyang
 */
@ExtensionConfig(desc = "加减锁扩展点", type = ExtensionType.COMMON, must = false)
public interface LockExtension extends BaseExtension {

    public static final boolean LOCK_ON_USER_RELEASE_ON_FAIL = true;

    default boolean buildOnPrePurchase(LockContext context, PurchaseSubmitContext purchaseSubmitContext) {
        return true;
    }

    default boolean buildOnPurchaseSuccess(LockContext context, PurchaseSubmitContext purchaseSubmitContext) {
        if (context.getLockMode() == LockMode.LOCK_ORDER) {
            return true;
        } else if (context.getLockMode() == LockMode.LOCK_USER) {
            if (!LOCK_ON_USER_RELEASE_ON_FAIL) {
                CommonLog.warn("用户粒度锁, 提单成功不能释放,需在取消订单或履约成功后释放");
            }
            return LOCK_ON_USER_RELEASE_ON_FAIL;
        }
        return true;
    }

    default boolean buildOnPurchaseFail(LockContext context, PurchaseSubmitContext purchaseSubmitContext) {
        return true;
    }


    default boolean buildOnPrePurchaseCancel(LockContext context, PurchaseCancelContext purchaseCancelContext) {
        if (context.getLockMode() == LockMode.LOCK_ORDER) {
            return true;
        } else if (context.getLockMode() == LockMode.LOCK_USER) {
            if (!LOCK_ON_USER_RELEASE_ON_FAIL) {
                //用户粒度的锁主要应用于 会员场景,因会员身份不能重叠,所以需要加用户粒度的锁
                // 用户粒度的锁需要在 入口层即 购买域加锁,履约成功解锁,订单取消解锁
                purchaseCancelContext.setLockValue(purchaseCancelContext.getMemberOrder().getExtra().getLockValue());
            }
            return LOCK_ON_USER_RELEASE_ON_FAIL;
        }
        return true;
    }

    default boolean buildOnPurchaseCancel(LockContext context, PurchaseCancelContext cancelContext) {
        return true;
    }


    default boolean buildOnPrePerform(LockContext context, PerformContext performContext) {
        if (context.getLockMode() == LockMode.LOCK_ORDER) {
            return true;
        } else if (context.getLockMode() == LockMode.LOCK_USER) {
            if (!LOCK_ON_USER_RELEASE_ON_FAIL) {
                //用户粒度的锁主要应用于 会员场景,因会员身份不能重叠,所以需要加用户粒度的锁
                // 用户粒度的锁需要在 入口层即 购买域加锁,履约成功解锁,订单取消解锁
                performContext.getCmd().setLockValue(performContext.getMemberOrder().getExtra().getLockValue());
            }
            return LOCK_ON_USER_RELEASE_ON_FAIL;
        }
        return true;
    }

    default boolean buildOnPerformSuccess(LockContext context, PerformContext performContext) {
        if (context.getLockMode() == LockMode.LOCK_ORDER) {
            return true;
        } else if (context.getLockMode() == LockMode.LOCK_USER) {
            if (performContext.getLockValue() == null) {
                performContext.setLockValue(performContext.getMemberOrder().getExtra().getLockValue());
                context.setLockValue(performContext.getLockValue());
            }
            return true;
        }
        return true;
    }

    default boolean buildOnPerformFail(LockContext context, PerformContext performContext) {
        if (context.getLockMode() == LockMode.LOCK_ORDER) {
            return true;
        } else if (context.getLockMode() == LockMode.LOCK_USER) {
            return LOCK_ON_USER_RELEASE_ON_FAIL;
        }
        /**
         * if (performContext.getRetryTimes() >= SwitchEnum.PERFORM_RETRY_MAX_TIME.getInt(context.getBizType().getCode())) {
         *                 CommonLog.warn("用户维度锁,已达最大履约重试次数, 释放锁");
         *                 context.setUnlockOnPerformFail(true);
         *             } else {
         *                 CommonLog.warn("用户维度锁,在履约失败时,暂时不释放");
         *                 context.setUnlockOnPerformFail(false);
         *             }
         */
        return true;
    }

    default boolean buildOnPrePeriodPerform(LockContext context, PeriodPerformContext performContext) {
        return true;
    }

    default boolean buildOnPeriodPerformSuccess(LockContext context, PeriodPerformContext performContext) {
        return true;
    }

    default boolean buildOnPeriodPerformFail(LockContext context, PeriodPerformContext performContext) {
        return true;
    }


    default boolean buildOnAfterSale(LockContext context, AfterSaleApplyContext performContext) {
        return true;
    }


    default boolean buildOnAfterSaleSuccess(LockContext context, AfterSaleApplyContext performContext) {
        return true;
    }


    default boolean buildOnAfterSaleFail(LockContext context,
                                         AfterSaleApplyContext performContext, Exception e) {
        // TODO: 2025/1/1 如果异常类型是受理异常,则不能释放锁.
        if (e instanceof AftersaleExecuteException) {
            //如果是受理异常 ,需要进行重试或回滚.因此不能释放锁!
            CommonLog.error("售后受理异常,不能释放锁");
            return LOCK_ON_USER_RELEASE_ON_FAIL;
        } else {
            return true;
        }
    }
}