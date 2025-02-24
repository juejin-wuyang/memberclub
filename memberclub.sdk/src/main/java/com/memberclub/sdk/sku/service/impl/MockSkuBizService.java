/**
 * @(#)MockSkuBizService.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.sku.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.entity.inventory.Inventory;
import com.memberclub.infrastructure.mybatis.mappers.sku.InventoryDao;
import com.memberclub.infrastructure.sku.SkuBizService;
import com.memberclub.sdk.sku.service.MemberSkuDataObjectFactory;
import com.memberclub.sdk.sku.service.SkuDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.sku", havingValue = "local", matchIfMissing = false)
@Configuration
public class MockSkuBizService implements SkuBizService {

    private Map<Long, SkuInfoDO> map = Maps.newHashMap();

    public static final Logger LOG = LoggerFactory.getLogger(MockSkuBizService.class);


    @Autowired
    private SkuDomainService skuDomainService;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private MemberSkuDataObjectFactory memberSkuDataObjectFactory;

    @Scheduled(fixedDelay = 10000, initialDelay = 1000)
    public void init() {
        List<SkuInfoDO> skus = skuDomainService.queryAllSkus();
        for (SkuInfoDO skuInfoDO : skus) {
            LOG.info("加载商品数据到缓存:{}", skuInfoDO.getSkuId());
            addSku(skuInfoDO.getSkuId(), skuInfoDO);
        }
    }

    public void addSku(long skuId, SkuInfoDO skuInfo) {
        map.put(skuId, skuInfo);
        skuDomainService.createMemberSku(skuInfo);
    }

    public void addSkuAndCreateInventory(long skuId, SkuInfoDO skuInfo) {
        addSku(skuId, skuInfo);
        if (skuInfo.getInventoryInfo().isEnable()) {
            Inventory inventory = memberSkuDataObjectFactory.buildInventoryFromSku(skuInfo);
            int cnt = inventoryDao.insertIgnoreBatch(Lists.newArrayList(inventory));
            if (cnt >= 1) {
                LOG.warn("成功创建商品库存 {}", inventory);
            } else {
                LOG.warn("商品库存已存在,无需再次创建 {}", skuInfo.getSkuId());
            }
        }
    }

    @Override
    public SkuInfoDO querySku( long skuId) {
        return map.get(skuId);
    }
}