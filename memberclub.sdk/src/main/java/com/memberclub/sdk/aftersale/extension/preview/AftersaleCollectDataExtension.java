/**
 * @(#)AftersaleCollectDataExtension.java, 十二月 22, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.aftersale.extension.preview;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewCmd;
import com.memberclub.domain.context.aftersale.preview.AfterSalePreviewContext;

/**
 * @author wuyang
 */
@ExtensionConfig(desc = "售后预览获取上下文数据扩展点", type = ExtensionType.AFTERSALE, must = true)
public interface AftersaleCollectDataExtension extends BaseExtension {

    public AfterSalePreviewContext collect(AfterSalePreviewCmd cmd);

}