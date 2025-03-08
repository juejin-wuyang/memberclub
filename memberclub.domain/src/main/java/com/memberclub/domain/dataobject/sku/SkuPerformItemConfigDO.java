/**
 * @(#)SkuPerformItemConfig.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku;

import com.memberclub.domain.dataobject.sku.rights.RightFinanceInfo;
import com.memberclub.domain.dataobject.sku.rights.RightGrantInfo;
import com.memberclub.domain.dataobject.sku.rights.RightSaleInfo;
import com.memberclub.domain.dataobject.sku.rights.RightViewInfo;
import lombok.Data;

/**
 * @author 掘金五阳
 */
@Data
public class SkuPerformItemConfigDO {

    private int bizType;

    private int rightType;

    private int rightId;

    private int totalCount;

    private int periodCount;

    private int periodType;

    private int cycle;

    private String providerId;

    private RightGrantInfo grantInfo = new RightGrantInfo();

    private RightFinanceInfo settleInfo = new RightFinanceInfo();

    private RightViewInfo viewInfo = new RightViewInfo();

    private RightSaleInfo saleInfo = new RightSaleInfo();
}