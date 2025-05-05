/**
 * @(#)MemberOrder.java, 十二月 14, 2024.
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
public class MemberOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private int bizType;

    private long userId;

    private Integer relatedOrderSystemType;

    private String relatedOrderId;

    private String tradeId;

    private int renewType;

    private String extra;

    private Integer actPriceFen;

    private Integer originPriceFen;

    private Integer salePriceFen;

    private int source;

    private int status;

    private int performStatus;

    /***********支付信息**********/

    private int payStatus;

    private String payAccount;

    private long payTime;

    private String payAccountType;

    private String payChannelType;

    private String merchantId;

    private String payTradeNo;

    private Integer payAmountFen;

    private String payOnlineType;

    private String payNodeType;
    /***********支付信息**********/

    private long stime;

    private long etime;

    private long utime;

    private long ctime;
}