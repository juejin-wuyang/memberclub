/**
 * @(#)AssetReverseResponseDO.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.facade;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@Data
public class AssetReverseResponseDO {

    private int code;

    private Map<String, List<AssetDO>> itemToken2AssetsMap;

    public boolean isSuccess() {
        return code == 0;
    }
}