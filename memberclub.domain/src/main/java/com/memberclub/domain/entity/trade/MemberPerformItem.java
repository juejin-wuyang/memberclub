/**
 * @(#)MemberPerformItem.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.entity.trade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author 掘金五阳
 */
@Data
public class MemberPerformItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private int bizType;

    private long userId;

    private String tradeId;

    private long skuId;

    private long rightId;

    private int providerId;

    private String subTradeId;

    private int rightType;

    private String batchCode;

    private String itemToken;

    private int totalCount;

    private int phase;

    private int cycle;

    private int buyIndex;

    /***
     * 0 发放资产
     * 1 激活资产
     * 2 发放资格
     */
    private int grantType;

    /**
     * 发放状态
     */
    private int status;

    private String extra;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;
}