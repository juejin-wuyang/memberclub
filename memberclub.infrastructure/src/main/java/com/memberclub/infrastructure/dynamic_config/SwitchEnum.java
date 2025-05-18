/**
 * @(#)SwitchEnum.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.dynamic_config;

import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.infrastructure.mq.MQContants;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @author 掘金五阳
 */
public enum SwitchEnum {

    /**
     * 加锁超时时间
     */
    LOCK_TIMEOUT_SECONDS("lock_time", 15 * 60),
    /**
     * 履约重试最大次数
     */
    PERFORM_RETRY_MAX_TIME("perform_retry_max_time", 5),
    /**
     * 售后降级开关
     */
    AFTERSALE_DEGRADE("aftersale_degrade_%s", false),

    /**
     * 售后发起次数缓存的过期时间
     */
    AFTERSALE_QUOTA_EXPIRE_DAYS("aftersale_quota_expire_days", 31),

    /**
     * 售后超过这个次数以后，不允许购买
     */
    AFTERSALE_QUOTA_MAX_VALUE("aftersale_quota_expire_days", 2),
    /**
     * 售后预览token降级
     */
    AFTERSALE_PREVIEW_TOKEN_CHECK_DEGRADE("aftersale_preview_token_check_degrade", false),
    /***
     * 预结算消费的重试次数
     */
    TRADE_EVENT_FOR_PRE_FINANCE_RETRY_TIMES("com.memberclub.trade.event.consumer.prefinance.retryTimes", 3),

    /***
     * 支付成功消费的重试次数
     */
    TRADE_EVENT_4_PAY_SUCCESS_RETRY_TIMES("com.memberclub.trade.event.consumer.paysuccess.retryTimes", 3),

    /**
     * 任务扫描 持续的最大天数, 如每个任务的 Stime+ 35天内均需要触发(如未执行成功)
     */
    ONCE_TASK_SCAN_PERIOD_PERFORM_ELASPED_DAYS("once_task_scan_period_perform_elasped_days", 35),


    /**
     * 任务扫描 持续的最大天数, 如每个任务的 Stime+ 35天内均需要触发(如未执行成功)
     */
    ONCE_TASK_SCAN_AFTERSALE_EXPIRE_REFUND_ELASPED_DAYS("once_task_scan_aftersale_expire_refund_elasped_days", 35),

    /**
     * 任务扫描,提前触发的天数, 如某个任务的 now < stime but (stime-1days) < now ,则可以被扫描到
     */
    ONCE_TASK_SCAN_PERIOD_PERFORM_PRE_DAYS("once_task_scan_period_perform_pre_days", 1),

    /**
     * 任务扫描,提前触发的天数, 如某个任务的 now < stime but (stime-1days) < now ,则可以被扫描到
     */
    ONCE_TASK_SCAN_AFTERSALE_EXPIRE_REFUND_PRE_DAYS("once_task_scan_aftersale_expire_refund_pre_days", 1),


    /**
     * 新会员用户标记的超时时间
     */
    NEW_MEMBER_USER_TAG_TIMEOUT("new_member_user_tag_timeout", TimeUnit.DAYS.toMillis(365 * 100)),

    /**
     * 新会员用户usertag相关的 唯一键 的超时时间
     */
    NEW_MEMBER_USER_TAG_UNIQUE_KEY_TIMEOUT("new_member_user_tag_timeout", TimeUnit.DAYS.toMillis(35)),


    ONCE_TASK_TABLE_NUM("once_task_table_num", 2),
    //

    RENEWABLE("renewable", 0),

    //系统自身商户ID，用于支付
    SELF_MERCHANT_ID("self_merchant_id", "888888"),

    WAIT_PAY_TIME_SECONDS("wait_pay_time_seconds", MQContants.WAIT_PAY_EXPIRE_SECONDS),

    PAY_AMOUNT_STRICT_VALIDATE("pay_amount_strict_validate", false),

    AMOUNT_COMPUTE_EXCEPTION_SKIP_ENABLE("amount_compute_exception_skip_enable", true),

    AFTER_SALE_PREVIEW_TOKEN_EXPIRE_TIME_SECONDS("after_sale_preview_token_expire_time_seconds", 5 * 60),//售后预览token过期
    ;


    private String key;

    private Object defaultValue;

    private int maxRate = 100;

    SwitchEnum(String key, Object defaultValue) {
        this(key, defaultValue, 100);
    }


    SwitchEnum(String key, Object defaultValue, Integer maxRate) {
        this.defaultValue = defaultValue;
        this.key = key;
        this.maxRate = maxRate;
    }

    private String getKey(int bizType) {
        return String.format("%s_%s", key, bizType);
    }

    private String getUserIdWhiteListKey() {
        return String.format("%s_%s", key, "white_list");
    }

    private String getRateKey() {
        return String.format("%s_%s", key, "rate");
    }

    public boolean getBoolean() {
        return getDynamicConfig().getBoolean(key, (Boolean) defaultValue);
    }

    public boolean getBoolean(int bizType, String... args) {
        String formatKey = String.format(key, args);
        return getBoolean(formatKey, bizType);
    }

    public boolean getBoolean(int bizType) {
        return getBoolean(key, bizType);
    }

    private boolean getBoolean(String key, int bizType) {
        key = String.format("%s_%s", key, bizType);

        return getDynamicConfig().getBoolean(key, (Boolean) getBoolean());
    }


    public boolean isEnable(int bizType, long userId) {
        String rateKey = getRateKey();

        int rateConfig = getDynamicConfig().getInt(rateKey, 0);

        int rate = (int) (userId % maxRate);

        if (rate < rateConfig) {
            return true;
        }

        String whiteList = getDynamicConfig().getString(getUserIdWhiteListKey(), "");

        String[] whiteUserIds = whiteList.split(",");
        for (String whiteUserId : whiteUserIds) {
            if (whiteUserId.equals(String.valueOf(userId))) {
                return true;
            }
        }
        return false;
    }

    public int getInt() {
        return getDynamicConfig().getInt(key, defaultValue);
    }

    public int getInt(int bizType) {
        String key = getKey(bizType);
        return getDynamicConfig().getInt(key, getInt());
    }

    public long getLong() {
        return getDynamicConfig()
                .getLong(key, defaultValue);
    }

    public long getLong(int bizType) {
        String key = getKey(bizType);
        return getDynamicConfig().getLong(key, getLong());
    }

    public String getString() {
        return getDynamicConfig().getString(key, (String) defaultValue);
    }

    public String getString(int bizType) {
        String key = getKey(bizType);
        return getDynamicConfig().getString(key, (String) getString());
    }

    public DynamicConfig getDynamicConfig() {
        DynamicConfig config = ApplicationContextUtils.getContext().getBean(DynamicConfig.class);
        Assert.notNull(config, "DynamicConfig依赖 Spring 启动");
        return config;
    }
}
