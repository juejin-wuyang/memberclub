/**
 * @(#)PerformHisSaleInfo.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.perform.his;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class SubOrderSaleInfo {
    public Integer actPriceFen;

    public Integer originPriceFen;

    public Integer salePriceFen;
}