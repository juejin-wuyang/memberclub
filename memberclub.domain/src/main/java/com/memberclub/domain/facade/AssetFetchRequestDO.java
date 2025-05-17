/**
 * @(#)AssetFetchRequestDO.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.facade;

import lombok.Data;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Data
public class AssetFetchRequestDO {

    private long userId;

    private int rightType;

    private List<String> itemTokens;

}