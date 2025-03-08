/**
 * @(#)MemberShipCacheService.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.service;

import com.google.common.collect.Lists;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.ShipTypeEnum;
import com.memberclub.domain.dataobject.membership.MemberShipItemDO;
import com.memberclub.domain.dataobject.membership.MemberShipUnionDO;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class MemberShipCacheService {

    @Autowired
    private CacheService cacheService;

    @Retryable(throwException = false)
    public void sync(MemberShipUnionDO memberShipUnionDO) {
        String key = String.format("%s_%s_%s", memberShipUnionDO.getBizType(), memberShipUnionDO.getShipType(), memberShipUnionDO.getUserId());

        cacheService.put(CacheEnum.membership, key, memberShipUnionDO);
        CommonLog.info("记录会员资格缓存 {}", JsonUtils.toJson(memberShipUnionDO));
    }

    @Retryable(throwException = false)
    public void remove(BizTypeEnum bizType, ShipTypeEnum shipType, long userId) {
        String key = String.format("%s_%s_%s", bizType.getCode(), shipType.getCode(), userId);
        cacheService.del(CacheEnum.membership, key);
        CommonLog.info("删除会员资格缓存 key:{}", key);
    }

    /**
     * 查询场景应该如下
     * 已知当前业务身份，要查询哪类会员资格和userId，查询当前生效的会员资格。
     * 查询到以后，根据当前资格类型，判断是否还具备资格（根据资格享用总次数 大于 已用次数）
     * <p>
     * 场景场景包括：
     * 1. 是否还在会员生效期（会员身份）
     * 2. 会员价资格
     * 3. 会员领取某类券的资格（这类资格还会绑定一个领券活动）
     * 4.
     *
     * @param bizType
     * @param shipType
     * @param userId
     * @param now
     * @return
     */
    public MemberShipUnionDO get(BizTypeEnum bizType, ShipTypeEnum shipType, long userId, long now) {
        String key = String.format("%s_%s_%s", bizType.getCode(), shipType.getCode(), userId);
        MemberShipUnionDO unionDO = cacheService.get(CacheEnum.membership, key);
        if (unionDO == null) {
            return unionDO;
        }
        List<MemberShipItemDO> activeItems = Lists.newArrayList();
        for (MemberShipItemDO item : unionDO.getItems()) {
            if (item.getStime() <= now && now <= item.getEtime()) {
                activeItems.add(item);
            }
        }
        unionDO.setItems(activeItems);
        return unionDO;
    }
}