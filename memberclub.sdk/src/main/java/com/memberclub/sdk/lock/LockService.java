/**
 * @(#)UserLockService.java, 一月 23, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.lock;

import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.common.LockContext;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import com.memberclub.infrastructure.lock.DistributeLock;
import com.memberclub.sdk.common.Monitor;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class LockService {

    @Autowired
    private DistributeLock distributeLock;

    /**
     * @param context
     * @return
     */
    public Long lock(LockContext context) {
        String key = buildKey(context.getBizType(),
                context.getUserId(),
                context.getTradeId(),
                context.getLockMode());
        if (context.getLockValue() == null) {
            Long lockValue = TimeUtil.now() + RandomUtils.nextInt();
            context.setLockValue(lockValue);
        }
        String lockScene = StringUtils.defaultIfEmpty(context.getLockScene(), "common");

        boolean locked = distributeLock.lock(key, context.getLockValue(),
                SwitchEnum.LOCK_TIMEOUT_SECONDS.getInt(context.getBizType().getCode()));
        if (!locked) {
            Monitor.LOCK.counter(context.getBizType(), "lock", false, "scene", lockScene);
            throw ResultCode.LOCK_ERROR.newException();
        }

        Monitor.LOCK.counter(context.getBizType(), "lock", true, "scene", lockScene);
        return context.getLockValue();
    }


    private String buildKey(BizTypeEnum bizType, long userId, String tradeId, LockMode lockMode) {
        String format = "%s_%s_lock";
        String key = null;
        if (lockMode == LockMode.LOCK_ORDER) {
            key = String.format(format, bizType.getCode(), tradeId);
        } else if (lockMode == LockMode.LOCK_USER) {
            key = String.format(format, bizType.getCode(), userId);
        }
        return key;
    }

    //内部重试解锁,务必确保解锁成功
    @Retryable(initialDelaySeconds = 1, maxDelaySeconds = 5, maxTimes = 4, throwException = false)
    public void unlock(LockContext context) {
        String key = buildKey(context.getBizType(),
                context.getUserId(), context.getTradeId(), context.getLockMode());

        boolean succ = distributeLock.unlock(key, context.getLockValue());

        String lockScene = StringUtils.defaultIfEmpty(context.getLockScene(), "common");
        Monitor.PERFORM_EXECUTE.counter(context.getBizType(), "unlock", succ,
                "scene", lockScene);

        if (!succ) {
            throw ResultCode.LOCK_ERROR.newException("解锁失败");
        }
    }

}