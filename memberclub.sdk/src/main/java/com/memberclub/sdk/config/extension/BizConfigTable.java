/**
 * @(#)BizConfigTable.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.config.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;

/**
 * author: 掘金五阳
 */

@ExtensionConfig(desc = "通用配置扩展点", type = ExtensionType.COMMON, must = true)
public interface BizConfigTable extends BaseExtension {

    default LockMode getLockMode() {
        return LockMode.LOCK_USER;
    }

    default int renewableCount(int bizType) {
        return SwitchEnum.RENEWABLE.getInt(bizType);
    }
}