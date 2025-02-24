/**
 * @(#)InventoryDomainService.java, 一月 29, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.inventory.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.ApplicationContextUtils;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.context.inventory.InventoryOpCmd;
import com.memberclub.domain.context.inventory.InventoryOpContext;
import com.memberclub.domain.context.inventory.InventoryOpTypeEnum;
import com.memberclub.domain.context.inventory.InventorySkuOpDO;
import com.memberclub.domain.dataobject.inventory.InventoryCacheCmd;
import com.memberclub.domain.dataobject.inventory.InventoryCacheDO;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.dataobject.sku.SkuInventoryInfo;
import com.memberclub.domain.entity.inventory.Inventory;
import com.memberclub.domain.entity.inventory.InventoryRecord;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.sku.InventoryDao;
import com.memberclub.infrastructure.mybatis.mappers.sku.InventoryRecordDao;
import com.memberclub.infrastructure.sku.SkuBizService;
import com.memberclub.sdk.sku.service.MemberSkuDataObjectFactory;
import com.memberclub.sdk.util.TransactionHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * author: 掘金五阳
 */
@DS("skuDataSource")
@Service
public class InventoryDomainService {

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private InventoryRecordDao inventoryRecordDao;

    @Autowired
    private SkuBizService skuBizService;

    @Autowired
    private InventoryCacheDomainService inventoryCacheDomainService;

    @Autowired
    private MemberSkuDataObjectFactory memberSkuDataObjectFactory;

    public void querySkuInventoryInfos(InventoryOpContext context) {
        Map<Long, SkuInventoryInfo> skuId2Inventorys = Maps.newHashMap();
        for (InventorySkuOpDO sku : context.getCmd().getSkus()) {
            SkuInfoDO skuInfoDO = skuBizService.querySku(sku.getSkuId());
            skuId2Inventorys.put(sku.getSkuId(), skuInfoDO.getInventoryInfo());
        }
        context.setSkuId2InventoryInfo(skuId2Inventorys);
    }

    public boolean filterBySkuAndGetOperatable(InventoryOpContext context) {
        Iterator<InventorySkuOpDO> iterator = context.getCmd().getSkus().iterator();
        List<Long> removedSkuIds = Lists.newArrayList();
        while (iterator.hasNext()) {
            InventorySkuOpDO skuOpDO = iterator.next();

            SkuInventoryInfo inventoryInfo = context.getSkuId2InventoryInfo().get(skuOpDO.getSkuId());
            if (inventoryInfo == null) {
                throw ResultCode.CONFIG_DATA_ERROR.newException("未找到商品库存数据");
            }
            boolean removable = !inventoryInfo.isEnable();

            if (removable) {
                iterator.remove();
                removedSkuIds.add(skuOpDO.getSkuId());
            }
        }
        if (CollectionUtils.isNotEmpty(removedSkuIds)) {
            CommonLog.warn("部分商品无需管理库存:{}", removedSkuIds);
        }
        return context.isOperatable();
    }

    public boolean filterByRecordsAndGetOperatable(InventoryOpContext context) {
        List<InventorySkuOpDO> newSkuOps = Lists.newArrayList();
        for (Map.Entry<Long, List<InventoryRecord>> entry : context.getSkuId2InventoryRecords().entrySet()) {
            for (InventoryRecord record : entry.getValue()) {
                InventorySkuOpDO skuOpDO = new InventorySkuOpDO();
                skuOpDO.setCount(record.getOpCount());
                skuOpDO.setSkuId(entry.getKey());
                skuOpDO.setSubKey(record.getSubKey());
                newSkuOps.add(skuOpDO);
            }
        }

        Set<Long> skuIds = CollectionUtilEx.mapToSet(newSkuOps, InventorySkuOpDO::getSkuId);
        Set<Long> originSkuIds = CollectionUtilEx.mapToSet(context.getCmd().getSkus(), InventorySkuOpDO::getSkuId);
        Collection<Long> removedSkuIds = CollectionUtils.removeAll(originSkuIds, skuIds);

        if (CollectionUtils.isNotEmpty(removedSkuIds)) {
            CommonLog.warn("部分商品无需回补库存:{}", removedSkuIds);
        }
        context.getCmd().setSkus(newSkuOps);
        return context.isOperatable();
    }


    public List<Inventory> queryInventorys(Long skuId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getTargetId, skuId);
        return inventoryDao.selectList(wrapper);
    }

    public List<InventoryRecord> queryInventoryUserRecords(long userId, String operateKey, int opType) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryRecord::getUserId, userId);
        wrapper.eq(InventoryRecord::getOperateKey, operateKey);
        wrapper.eq(InventoryRecord::getOpType, opType);
        return inventoryRecordDao.selectList(wrapper);
    }

    public Inventory queryInventory(Long targetId,
                                    int targetType,
                                    String subKey) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getTargetId, targetId);
        wrapper.eq(Inventory::getTargetType, targetType);
        wrapper.eq(Inventory::getSubKey, subKey);
        return inventoryDao.selectOne(wrapper);
    }

    //@Async("threadPoolExecutor")
    public void sync(List<InventoryRecord> records) {
        List<InventoryCacheDO> caches = Lists.newArrayList();
        for (InventoryRecord record : records) {
            Inventory inventory = queryInventory(
                    record.getTargetId(), record.getTargetType(), record.getSubKey());
            InventoryCacheDO cache = Inventory.toCache(inventory);
            caches.add(cache);
        }
        inventoryCacheDomainService.syncCaches(new InventoryCacheCmd(caches));
    }

    public boolean isEnough(InventoryOpContext context) {
        boolean nonEnough = false;
        for (InventorySkuOpDO sku : context.getCmd().getSkus()) {
            Inventory inventory = inventoryDao.getInventory(context.getTargetType().getCode(),
                    sku.getSkuId(),
                    sku.getSubKey());
            if (inventory.getTotalCount() < (inventory.getSaleCount() + sku.getCount())) {
                nonEnough = true;
                CommonLog.warn("库存预校验: 库存不足 skuId:{}, totalCount:{}, saleCount:{}, opCount:{}", sku.getSkuId(),
                        inventory.getTotalCount(), inventory.getSaleCount(), sku.getCount());
            } else {
                CommonLog.info("库存预校验: 库存充足 skuId:{}, totalCount:{}, saleCount:{}, opCount:{}", sku.getSkuId(),
                        inventory.getTotalCount(), inventory.getSaleCount(), sku.getCount());
            }
        }

        return !nonEnough;
    }

    @Transactional(rollbackFor = Exception.class)
    public void onDecrement(InventoryOpContext context) {
        List<InventoryRecord> records = Lists.newArrayList();

        for (InventorySkuOpDO sku : context.getCmd().getSkus()) {
            int cnt = inventoryDao.decrement(context.getTargetType().getCode(),
                    sku.getSkuId(),
                    sku.getSubKey(),
                    sku.getCount(), TimeUtil.now());
            if (cnt < 1) {
                throw ResultCode.INVENTORY_DECREMENT_FAIL.newException();
            }
            InventoryRecord record = memberSkuDataObjectFactory.buildRecord(context, InventoryOpTypeEnum.DECREMENT, sku);
            records.add(record);
        }

        int cnt = inventoryRecordDao.insertIgnoreBatch(records);
        if (cnt < records.size()) {
            throw ResultCode.INVENTORY_DECREMENT_DUPLICATED.newException();
        }

        CommonLog.info("完成扣减库存 context:{}", context);
        TransactionHelper.afterCommitExecute(() -> {
            ApplicationContextUtils.getContext().getBean(InventoryDomainService.class).sync(records);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void onRollback(InventoryOpContext context) {
        List<InventoryRecord> records = Lists.newArrayList();

        InventoryOpCmd cmd = context.getCmd();

        for (InventorySkuOpDO sku : context.getCmd().getSkus()) {
            InventoryRecord decrementRecord = inventoryRecordDao.queryRecord(cmd.getUserId(),
                    cmd.getOperateKey(),
                    Inventory.buildInventoryKey(cmd.getTargetType().getCode(), sku.getSkuId(), sku.getSubKey()),
                    InventoryOpTypeEnum.DECREMENT.getCode());
            if (decrementRecord == null) {
                throw ResultCode.INVENTORY_ROLLBACK_INVALID.newException();
            }
            int cnt = inventoryDao.rollback(context.getTargetType().getCode(),
                    sku.getSkuId(),
                    sku.getSubKey(),
                    sku.getCount(), TimeUtil.now());
            if (cnt < 1) {
                throw ResultCode.INVENTORY_ROLLBACK_FAIL.newException();
            }
            InventoryRecord record = memberSkuDataObjectFactory.buildRecord(context, InventoryOpTypeEnum.ROLLBACK, sku);
            records.add(record);
        }

        int cnt = inventoryRecordDao.insertIgnoreBatch(records);
        if (cnt < records.size()) {
            throw ResultCode.INVENTORY_DECREMENT_DUPLICATED.newException();
        }
        CommonLog.info("完成回补库存 context:{}", context);
        TransactionHelper.afterCommitExecute(() -> {
            ApplicationContextUtils.getContext().getBean(InventoryDomainService.class).sync(records);
        });
    }
}