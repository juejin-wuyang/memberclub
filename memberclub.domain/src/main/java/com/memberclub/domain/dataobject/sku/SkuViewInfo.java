/**
 * @(#)SkuDisplayDO.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.domain.dataobject.sku;

import lombok.Data;

/**
 * @author 掘金五阳
 */
@Data
public class SkuViewInfo {

    private String displayImage;

    private String displayName;

    private String displayDesc;

    private String internalName;

    private String internalDesc;
}