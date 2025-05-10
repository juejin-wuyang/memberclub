/**
 * @(#)GenerateAfterSalePlanDigestExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "构建售后计算摘要扩展点", type = ExtensionType.AFTERSALE, must = true)
public interface GenerateAfterSalePlanDigestExtension extends BaseExtension {

    public void generateDigest(AfterSalePreviewContext context);

}