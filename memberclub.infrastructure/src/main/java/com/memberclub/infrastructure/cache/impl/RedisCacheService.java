/**
 * @(#)RedissonCacheService.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.cache.impl;

import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.RetryUtil;
import com.memberclub.common.util.FileUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.dataobject.inventory.InventoryCacheDO;
import com.memberclub.domain.dataobject.membership.MemberShipUnionDO;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import com.memberclub.infrastructure.cache.RedisLuaUtil;
import com.memberclub.infrastructure.cache.VersionCacheCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * author: 掘金五阳
 */
@Service
@ConditionalOnProperty(name = "memberclub.infrastructure.cache", havingValue = "redis", matchIfMissing = false)
public class RedisCacheService implements CacheService {

    public static final Logger LOG = LoggerFactory.getLogger(RedisCacheService.class);
    public static final String INVENTORY_CACHE_HKEY = "value";

   /* @Autowired
    private RedissonClient redissonClient;*/

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HashOperations<String, String, Object> hashOperations;


    private static String inventoryUpdateLua = null;

    static {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("lua/inventory_update.lua");
            Resource resource = resources[0];
            inventoryUpdateLua = FileUtils.readFile("lua/inventory_update.lua");

            LOG.info("提前加载 Lua 脚本 inventory_update.lua:{}", inventoryUpdateLua);
        } catch (Exception e) {
            LOG.error("加载 lua 脚本异常", e);
        }
    }

    @Override
    public <K, V> V del(CacheEnum cacheEnum, K k) {
        if (cacheEnum == CacheEnum.membership) {
            String key = buildMembershipKey(k);
            return (V) redisTemplate.opsForValue().getAndDelete(key);
        }
        return null;
    }

    @Override
    public <K, V> V put(CacheEnum cacheEnum, K k, V v) {
        if (cacheEnum == CacheEnum.inventory) {
            //return putInventoryCache(cacheEnum, (String) k, (InventoryCacheDO) v);
            return (V) putInventoryCacheBaseLua((String) k, (InventoryCacheDO) v);
        }
        if (cacheEnum == CacheEnum.membership) {
            String key = buildMembershipKey(k);
            long etime = ((MemberShipUnionDO) v).getEtime();
            long timeout = etime - TimeUtil.now();
            redisTemplate.opsForValue().set(key, v, timeout, TimeUnit.MILLISECONDS);
        }
        return null;
    }

    private <K> String buildMembershipKey(K k) {
        return "membership_" + k;
    }


    private InventoryCacheDO putInventoryCacheBaseLua(String k, InventoryCacheDO cache) {
        RedisScript<Long> script = new DefaultRedisScript<>(inventoryUpdateLua, Long.class);

        VersionCacheCmd cmd = new VersionCacheCmd();
        String key = buildInventoryKey(cache.getKey());
        cmd.setKey(key);
        cmd.setVersionKey("version");
        cmd.setVersion(cache.getVersion());
        cmd.setValueKey(INVENTORY_CACHE_HKEY);
        cmd.setValue(RetryUtil.toJson(cache));
        System.out.println(RedisLuaUtil.buildUpdateInventoryKeys(cmd));
        Long value = redisTemplate.execute(script, RedisLuaUtil.buildUpdateInventoryKeys(cmd));

        if (value != null && value > 1) {
            CommonLog.info("更新库存缓存成功 value:{}", value);
            return cache;
        } else {
            CommonLog.info("未更新库存缓存 value:{}", value);
            return null;
        }
    }

    private String buildInventoryKey(String key) {
        return "inventory_" + key;
    }
/*

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
    }*/


    private <K, V> V getInventoryCache(CacheEnum cacheEnum, String k) {
        return (V) hashOperations.get(buildInventoryKey(k), INVENTORY_CACHE_HKEY);
        //return (V) JsonUtils.fromJson(value.toString(), InventoryCacheDO.class);
    }

    @Override
    public <K, V> V get(CacheEnum cacheEnum, K k) {
        if (cacheEnum == CacheEnum.inventory) {
            return getInventoryCache(cacheEnum, (String) k);
        }
        if (cacheEnum == CacheEnum.membership) {
            String key = buildMembershipKey(k);
            return (V) redisTemplate.opsForValue().get(key);
        }
        return null;
    }
}