package com.memberclub.sdk.oncetask.aftersale.extension;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.dataobject.task.OnceTaskDO;

@ExtensionConfig(desc = "onceTask Domain层扩展点", must = false, type = ExtensionType.AFTERSALE)
public interface OnceTaskDomainExtension extends BaseExtension {

    public void onCreatedExpireRefundTask(PerformContext context, OnceTaskDO onceTaskDO);
}
