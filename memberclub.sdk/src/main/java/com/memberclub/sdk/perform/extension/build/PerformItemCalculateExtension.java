/**
 * @(#)PerformMapperExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.build;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.common.util.TimeRange;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.domain.dataobject.sku.SkuPerformItemConfigDO;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "MemberPerformItem 构建扩展点", type = ExtensionType.PERFORM_MAIN, must = true)
public interface PerformItemCalculateExtension extends BaseExtension {

    public MemberPerformItemDO toPerformItem(SkuPerformItemConfigDO performConfigDO);

    public TimeRange buildPeriod(long baseTime, MemberPerformItemDO itemDO);

    public TimeRange buildDelayPeriod(long stime, MemberPerformItemDO itemDO);
}