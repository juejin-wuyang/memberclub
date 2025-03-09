/**
 * @(#)BuildPerformContextExtension.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.perform.extension.build;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "履约拆单 扩展点", type = ExtensionType.PERFORM_MAIN, must = true)
public interface PerformSeparateOrderExtension extends BaseExtension {

    public void separateOrder(PerformContext context);

    default void buildTimeRange(PerformContext context) {
        for (SubOrderPerformContext subOrderPerformContext : context.getSubOrderPerformContexts()) {
            long stime = context.getBaseTime();
            long etime = context.getImmediatePerformEtime();
            if (context.getDelayPerformEtime() > 0) {
                etime = context.getDelayPerformEtime();
            }

            subOrderPerformContext.getSubOrder().setStime(stime);
            subOrderPerformContext.getSubOrder().setEtime(etime);
        }
        context.setStime(context.getSubOrderPerformContexts().get(0).getSubOrder().getStime());
        context.setEtime(context.getSubOrderPerformContexts().get(0).getSubOrder().getEtime());
    }
}