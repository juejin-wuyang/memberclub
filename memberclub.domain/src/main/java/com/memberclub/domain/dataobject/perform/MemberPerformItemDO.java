/**
 * @(#)PerformItemDO.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.perform;

import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.common.*;
import com.memberclub.domain.dataobject.perform.item.PerformItemExtraInfo;
import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class MemberPerformItemDO implements Comparable {

    PerformItemStatusEnum status;
    private long skuId;
    private long rightId;
    private RightUsedType rightUsedType;
    private RightTypeEnum rightType;
    private String itemToken;
    private String batchCode;
    private int totalCount;
    private String subTradeId;
    private int phase = 1;
    private int cycle;
    private int buyIndex = 1;
    /***
     * 0 发放
     * 1 激活
     */
    private GrantTypeEnum grantType;
    private int providerId;
    private PerformItemExtraInfo extra = new PerformItemExtraInfo();
    private int periodCount;
    private PeriodTypeEnum periodType;
    private long stime;
    private long etime;
    private long utime;

    @Override
    public int compareTo(Object o) {
        return phase < ((MemberPerformItemDO) o).getPhase() ? -1 : 1;
    }

    public void onFinishPerform(PerformItemContext context) {
        status = PerformItemStatusEnum.PERFORM_SUCCESS;
        utime = System.currentTimeMillis();
    }

    public boolean isFinanceable() {
        return !(Boolean.FALSE.equals(getExtra().getSettleInfo().getFinanceable()));
    }

}