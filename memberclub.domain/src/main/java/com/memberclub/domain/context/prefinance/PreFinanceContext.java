/**
 * @(#)PreFinanceContext.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.prefinance;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.prefinance.common.PreFinanceEventEnum;
import com.memberclub.domain.dataobject.event.trade.TradeEventDO;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.perform.MemberSubOrderDO;
import com.memberclub.domain.dataobject.prefinance.PreFinanceItemDO;
import com.memberclub.domain.dataobject.prefinance.PreFinanceRecordDO;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.facade.AssetDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@Data
public class PreFinanceContext {

    Map<String, List<AssetDO>> itemToken2Assets;
    private Long userId;
    private BizTypeEnum bizType;
    private String tradeId;
    private String subTradeId;
    private Long skuId;
    private TradeEventDO event;
    private PreFinanceEventEnum preFinanceEventEnum;
    private MemberOrderDO memberOrder;
    private MemberSubOrderDO subOrder;
    private List<MemberPerformItemDO> performItems;
    private PreFinanceEvent preFinanceEvent;

    private PreFinanceRecordDO record;

    private List<PreFinanceItemDO> financeItems;
}