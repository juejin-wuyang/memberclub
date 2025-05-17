/**
 * @(#)PerformContext.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform;

import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.RetrySourceEunm;
import com.memberclub.domain.dataobject.CommonUserInfo;
import com.memberclub.domain.dataobject.order.MemberOrderExtraInfo;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class PerformContext {

    private PerformCmd cmd;

    private RetrySourceEunm retrySource;

    private boolean skipPerform;

    private BizTypeEnum bizType;

    private long userId;

    /******************订单信息****************/
    /*private String orderId;*/

    private String tradeId;

    /*private OrderSystemTypeEnum orderSystemType;*/

    private int memberOrderStartPerformUpdateCount;

    private long stime;

    private long etime;

    /******************订单信息****************/

    /****************** Start 主单,子单和上下文 核心****************/

    private CommonUserInfo userInfo;

    private MemberOrderDO memberOrder;

    private MemberOrderExtraInfo memberOrderExtraInfo;

    private List<MemberSubOrderDO> memberSubOrders;

    private List<SubOrderPerformContext> subOrderPerformContexts;

    private SubOrderPerformContext currentSubOrderPerformContext;

    private long immediatePerformEtime;

    private long delayPerformEtime;

    /******************主单,子单和上下文 核心****************/


    /********************* Start 重试上下文***************/
    private int retryTimes;

    private Long lockValue;

    private long baseTime;

    /********************* 重试上下文***************/

    public BizScene toDefaultScene() {
        return BizScene.of(bizType.getCode());
    }

    public void initialize(MemberOrderDO memberOrder) {
        setMemberOrder(memberOrder);
        setMemberSubOrders(memberOrder.getSubOrders());
        MemberOrderExtraInfo extraInfo = getMemberOrder().getExtra();
        setMemberOrderExtraInfo(extraInfo);
        setUserInfo(extraInfo.getUserInfo());
    }
}