/**
 * @(#)TradeEvent.java, 一月 12, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.event.trade;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class TradeEventDO {

    private TradeEventEnum eventType;

    private TradeEventDetailDO detail;
}