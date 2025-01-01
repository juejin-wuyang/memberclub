/**
 * @(#)BuildReverseInfoExtension.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.reverse;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;

/**
 * author: 掘金五阳
 */
public interface BuildReverseInfoExtension extends BaseExtension {

    public void buildAssets(ReversePerformContext context);

    public void buildTasks(ReversePerformContext context);

}