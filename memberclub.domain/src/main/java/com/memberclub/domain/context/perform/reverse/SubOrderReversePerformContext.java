/**
 * @(#)SkuReversePerformInfo.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.perform.reverse;

import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.task.OnceTaskDO;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class SubOrderReversePerformContext {

    List<String> activeTaskTokens;
    private long skuId;
    private Long subTradeId;
    private MemberSubOrderDO memberSubOrder;
    private List<PerformItemReverseInfo> items;
    private int totalItemsCount;
    private boolean allRefund;
    //如果有待取消的任务,也一并取消掉!
    private List<OnceTaskDO> activeTasks;
    /************************************/
    //临时数据
    private List<PerformItemReverseInfo> currentItems;

    private int currentRightType;

    /************************************/


    public void incrementTotalItems() {
        totalItemsCount++;
    }
}