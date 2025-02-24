/**
 * @(#)PurchaseTestParam.java, 一月 19, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.starter.controller.vo.test;

import lombok.Data;

/**
 * author: 掘金五阳
 */
@Data
public class PurchaseSubmitRequest {

    private long skuId;

    private int buyCount = 1;

    private String previewToken;
}