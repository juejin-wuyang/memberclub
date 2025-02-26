package com.memberclub.plugin.douyinpkg.extension.aftersale;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.sdk.oncetask.aftersale.extension.DefaultExpireRefundTriggerExtension;

@ExtensionProvider(desc = "抖音券包 过期退触发扩展点", bizScenes =
        {@Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = {SceneEnum.AFTERSALE_EXPIRE_REFUND_TASK_TYPE})})
public class DouyinPkgExpireRefundTriggerExtension extends DefaultExpireRefundTriggerExtension {
}
