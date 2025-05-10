/**
 * @(#)RedissonIdGenerator.java, 一月 17, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.id.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.infrastructure.id.IdGenerator;
import com.memberclub.infrastructure.id.IdTypeEnum;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.PostConstruct;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 Id 生成器", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
@ConditionalOnProperty(name = "memberclub.infrastructure.id", havingValue = "redisson")
public class RedissonIdGenerator implements IdGenerator {

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        for (IdTypeEnum value : IdTypeEnum.values()) {
            redissonClient.getIdGenerator(value.toKey())
                    .tryInit(RandomUtils.nextLong(1000000000000000L, 2000000000000000L), 1);
        }
    }

    @Override
    public Long generateId(IdTypeEnum idType) {
        return redissonClient.getIdGenerator(idType.toKey()).nextId();
    }
}