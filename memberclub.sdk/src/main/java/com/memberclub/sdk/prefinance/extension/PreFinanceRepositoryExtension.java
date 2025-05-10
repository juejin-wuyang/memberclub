/**
 * @(#)PreFinanceDomainExtension.java, 一月 28, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.prefinance.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.prefinance.PreFinanceContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "预结算 Domain层扩展点", must = false, type = ExtensionType.PRE_FINANCE)
public interface PreFinanceRepositoryExtension extends BaseExtension {

    public void onCreateExpiredTask(PreFinanceContext context, List<OnceTaskDO> taskList);
}