/**
 * @(#)AssetReverseRequestDO.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.facade;

import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AssetReverseRequestDO {
    private long userId;

    private int rightType;

    private List<String> assetBatchs;
}