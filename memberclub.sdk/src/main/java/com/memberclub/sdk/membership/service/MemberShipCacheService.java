/**
 * @(#)MemberShipCacheService.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.service;

import com.memberclub.common.log.CommonLog;
import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.JsonUtils;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.ShipTypeEnum;
import com.memberclub.domain.dataobject.membership.MemberShipUnionDO;
import com.memberclub.infrastructure.cache.CacheEnum;
import com.memberclub.infrastructure.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}