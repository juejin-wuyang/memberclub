/**
 * @(#)AssetFetchResponseDO.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
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
public class AssetFetchResponseDO {
    private int code;

    private Map<String, List<AssetDO>> itemToken2AssetsMap;

    public boolean isSuccess() {
        return code == 0;
    }


}