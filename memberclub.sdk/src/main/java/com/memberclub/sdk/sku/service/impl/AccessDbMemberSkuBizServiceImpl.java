/**
 * @(#)MemberSkuBizServiceImpl.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.sku.service.impl;

import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.infrastructure.sku.SkuBizService;
import com.memberclub.sdk.sku.service.SkuDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.sku", havingValue = "db", matchIfMissing = true)
@Service
public class AccessDbMemberSkuBizServiceImpl implements SkuBizService {

    @Autowired
    private SkuDomainService skuDomainService;

    @Override
    public SkuInfoDO querySku(long skuId) {
        return skuDomainService.queryById(skuId);
    }
}