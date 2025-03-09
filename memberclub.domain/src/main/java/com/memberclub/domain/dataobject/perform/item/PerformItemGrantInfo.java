/**
 * @(#)PerformItemGrantInfo.java, 十二月 28, 2024.
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
public class PerformItemGrantInfo {

    private Integer periodCount;

    private long startTime;

    private long endTime;

    private Integer periodType;
}