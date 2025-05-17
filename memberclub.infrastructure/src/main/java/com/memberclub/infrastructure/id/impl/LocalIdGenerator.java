/**
 * @(#)DefaultIdGenerator.java, 十二月 19, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认 Id 生成器", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT, scenes = SceneEnum.DEFAULT_SCENE)
})
@ConditionalOnProperty(name = "memberclub.infrastructure.id", havingValue = "local")
public class LocalIdGenerator implements IdGenerator {

    @Override
    public Long generateId(IdTypeEnum idTypeEnum) {
        // TODO: 2024/12/19 修改为其他 ID 生成器
        return Math.abs(RandomUtils.nextLong());
    }
}