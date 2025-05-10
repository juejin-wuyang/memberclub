/**
 * @(#)RealtimeCalculateUsageExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;
import com.memberclub.domain.context.aftersale.preview.ItemUsage;

import java.util.Map;

/**
 * @author wuyang
 */
@ExtensionConfig(desc = "实时计算售后金额 扩展点", type = ExtensionType.AFTERSALE, must = false)
public interface RealtimeCalculateUsageExtension extends BaseExtension {

    public Map<String, ItemUsage> calculateItemUsage(AfterSalePreviewContext context);

}