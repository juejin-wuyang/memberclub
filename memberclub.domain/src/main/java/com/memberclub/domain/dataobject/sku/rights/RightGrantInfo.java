/**
 * @(#)RightGrantInfo.java, 十二月 28, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku.rights;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class RightGrantInfo {

    private String rightConfigId;

    //开始时间
    private Long startTime;

    //截止时间
    private Long endTime;
}