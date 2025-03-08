/**
 * @(#)PerformItemGrantExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.execute;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.execute.ItemGroupGrantResult;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "MemberPerformItem 发放层 扩展点", type = ExtensionType.PERFORM_MAIN, must = true)
public interface MemberRightsGrantExtension extends BaseExtension {

    public ItemGroupGrantResult grant(PerformItemContext context, List<MemberPerformItemDO> items);
}