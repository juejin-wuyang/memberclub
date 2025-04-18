/**
 * @(#)PerformItemExtraInfo.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.perform.item;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class PerformItemExtraInfo {
    
    private PerformItemGrantInfo grantInfo = new PerformItemGrantInfo();

    private PerformItemViewInfo viewInfo = new PerformItemViewInfo();

    private PerformItemFinanceInfo settleInfo = new PerformItemFinanceInfo();

    private PerformItemSaleInfo saleInfo = new PerformItemSaleInfo();

}