/**
 * @(#)UserTagDO.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.context.usertag;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class UserTagOpDO {

    public long skuId;

    public String key;

    private Integer opCount;

    private Long totalCount;

    private long expireSeconds;
}