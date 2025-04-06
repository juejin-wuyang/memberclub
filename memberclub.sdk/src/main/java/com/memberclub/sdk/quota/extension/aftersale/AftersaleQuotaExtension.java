package com.memberclub.sdk.quota.extension.aftersale;

import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;

@ExtensionConfig(desc = "售后配额扩展点", must = false, type = ExtensionType.AFTERSALE)
public interface AftersaleQuotaExtension extends BaseExtension {

    public void buildUserTagOp(AftersaleQuotaExtensionContext context);
}
