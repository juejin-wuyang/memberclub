/**
 * @(#)IdGenerator.java, 十二月 19, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.id;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;

/**
 * @author wuyang
 */
@ExtensionConfig(desc = "分布式 ID 生成扩展点", type = ExtensionType.COMMON, must = true)
public interface IdGenerator extends BaseExtension {

    public Long generateId(IdTypeEnum idType);
}