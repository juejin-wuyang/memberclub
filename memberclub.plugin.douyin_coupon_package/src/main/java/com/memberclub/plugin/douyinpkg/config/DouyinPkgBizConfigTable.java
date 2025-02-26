package com.memberclub.plugin.douyinpkg.config;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.common.LockMode;
import com.memberclub.sdk.config.extension.BizConfigTable;

@ExtensionProvider(desc = "Demo 业务配置表", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE)
})
public class DouyinPkgBizConfigTable implements BizConfigTable {

    public LockMode getLockMode() {
        return LockMode.LOCK_ORDER;
    }
}