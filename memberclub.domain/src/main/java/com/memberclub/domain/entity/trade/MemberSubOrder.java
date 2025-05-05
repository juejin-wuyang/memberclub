/**
 * @(#)MemberPerformHisPO.java, 十二月 14, 2024.
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
public class MemberSubOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private int bizType;

    private long userId;

    private Integer relatedOrderSystemType;

    private String relatedOrderId;

    private String tradeId;//会员单交易 ID

    private Long subTradeId;

    private Integer actPriceFen;

    private Integer originPriceFen;

    private Integer salePriceFen;

    private int buyCount;

    private long skuId;

    private int status;

    private int performStatus;

    private String extra;

    private long stime;

    private long etime;

    private long utime;

    private long ctime;
}