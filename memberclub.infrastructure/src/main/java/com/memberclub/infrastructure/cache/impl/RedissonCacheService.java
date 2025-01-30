/**
 * @(#)RedissonCacheService.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.cache.impl;

import com.memberclub.domain.dataobject.inventory.InventoryCacheDO;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
@ConditionalOnProperty(name = "memberclub.infrastructure.cache", havingValue = "redisson", matchIfMissing = false)
public class RedissonCacheService implements CacheService {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public <K, V> V put(CacheEnum cacheEnum, K k, V v) {
        if (cacheEnum == CacheEnum.inventory) {
            return putInventoryCache(cacheEnum, (String) k, (InventoryCacheDO) v);
        }
        return null;
    }

    private <K, V> V putInventoryCache(CacheEnum cacheEnum, String k, InventoryCacheDO v) {
        InventoryCacheDO newValue = v;
        RMapCache<String, InventoryCacheDO> map = redissonClient.getMapCache(cacheEnum.getName());
        //常驻缓存

        InventoryCacheDO nv = map.compute(k, (key, old) -> {
            if (old == null) {
                return newValue;
            } else if (old.getVersion() > newValue.getVersion()) {
                return old;
            } else {
                return newValue;
            }
        });
        return (V) nv;
    }


    private <K, V> V getInventoryCache(CacheEnum cacheEnum, String k) {
        RMapCache<String, InventoryCacheDO> map = redissonClient.getMapCache(cacheEnum.getName());
        //常驻缓存
        return (V) map.get(k);
    }

    @Override
    public <K, V> V get(CacheEnum cacheEnum, K k) {
        if (cacheEnum == CacheEnum.inventory) {
            return getInventoryCache(cacheEnum, (String) k);
        }
        return null;
    }
}