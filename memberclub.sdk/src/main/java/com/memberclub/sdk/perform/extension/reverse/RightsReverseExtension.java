/**
 * @(#)PerformItemReverseExtension.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.perform.reverse.AssetsReverseResponse;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "资产逆向冻结扩展点", type = ExtensionType.REVERSE_PERFORM, must = true)
public interface RightsReverseExtension extends BaseExtension {

    public AssetsReverseResponse reverse(ReversePerformContext context,
                                         SubOrderReversePerformContext reverseInfo,
                                         List<PerformItemReverseInfo> items);

}