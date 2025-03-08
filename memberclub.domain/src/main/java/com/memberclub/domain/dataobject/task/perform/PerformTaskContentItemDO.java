/**
 * @(#)PerformTaskContentItemDO.java, 十二月 29, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.task.perform;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class PerformTaskContentItemDO {
    private int bizType;

    private long userId;

    private String tradeId;

    private String subTradeId;

    private long skuId;

    private long rightId;

    private int providerId;

    private int rightType;

    private String batchCode;

    private String itemToken;

    private int totalCount;

    private int phase;

    private int cycle;

    private int buyIndex;

    /***
     * 0 发放
     * 1 激活
     */
    private int grantType;

    private int status;

    private String extra;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;
}