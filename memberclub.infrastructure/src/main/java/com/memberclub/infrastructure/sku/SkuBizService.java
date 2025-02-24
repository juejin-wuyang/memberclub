/**
 * @(#)SkuBizService.java, 一月 04, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.sku;

import com.memberclub.domain.dataobject.sku.SkuInfoDO;

/**
 * author: 掘金五阳
 */
public interface SkuBizService {

    public SkuInfoDO querySku(long skuId);
}