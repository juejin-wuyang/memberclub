/**
 * @(#)OuterSubmitExtension.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.outer.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "外部下单扩展点", type = ExtensionType.OUTER_SUBMIT, must = false)
public interface OuterSubmitExtension extends BaseExtension {
    
}