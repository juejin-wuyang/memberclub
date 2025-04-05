/**
 * @(#)InventoryOpCmd.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.inventory;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.purchase.common.SubmitSourceEnum;
import com.memberclub.domain.entity.inventory.InventoryTargetTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class InventoryOpCmd {

    private long userId;

    private BizTypeEnum bizType;

    /**
     * @see SubmitSourceEnum
     */
    private int source;

    private String operateKey;

    private InventoryOpTypeEnum opType;

    private List<InventorySkuOpDO> skus;

    private InventoryTargetTypeEnum targetType;

    private long submitTime;
}