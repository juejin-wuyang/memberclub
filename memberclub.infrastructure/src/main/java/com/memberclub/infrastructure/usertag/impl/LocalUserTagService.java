/**
 * @(#)LocalUserTagService.java, 一月 30, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.usertag.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.domain.context.usertag.*;
import com.memberclub.infrastructure.usertag.UserTagService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author: 掘金五阳
 */
@ConditionalOnProperty(name = "memberclub.infrastructure.usertag", havingValue = "local", matchIfMissing = false)
@Service
public class LocalUserTagService implements UserTagService {

    private Map<String, Long> map = Maps.newConcurrentMap();

    @Override
    public UserTagOpResponse operate(UserTagOpCmd cmd) {
        UserTagOpResponse response = new UserTagOpResponse();
        response.setSuccess(true);
        if (cmd.getOpType() == UserTagOpTypeEnum.ADD) {
            for (UserTagOpDO tag : cmd.getTags()) {
                map.compute(tag.getKey(), (k, o) -> {
                    if (o != null) {
                        return o + tag.getOpCount();
                    }
                    return (long) tag.getOpCount();
                });
            }

        } else if (cmd.getOpType() == UserTagOpTypeEnum.DEL) {
            for (UserTagOpDO tag : cmd.getTags()) {
                Object v = map.remove(tag.getKey());
                if (v == null) {
                    response.setSuccess(false);
                }
            }
        } else if (cmd.getOpType() == UserTagOpTypeEnum.GET) {
            List<String> keys = CollectionUtilEx.mapToList(cmd.getTags(), UserTagOpDO::getKey);
            List<UserTagDO> userTags = Lists.newArrayList();

            for (int i = 0; i < keys.size(); i++) {
                UserTagDO userTag = new UserTagDO();
                userTag.setKey(keys.get(i));
                Long count = map.getOrDefault(userTag.getKey(), 0L);
                userTag.setCount(count);
                userTags.add(userTag);
            }
            response.setSuccess(true);
            response.setTags(userTags);
        }
        return response;
    }
}