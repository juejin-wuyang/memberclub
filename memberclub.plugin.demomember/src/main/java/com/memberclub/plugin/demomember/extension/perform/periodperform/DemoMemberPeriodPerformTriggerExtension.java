/**
 * @(#)DemoMemberPeriodPerformTriggerExtension.java, 一月 27, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.plugin.demomember.extension.perform.periodperform;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.sdk.oncetask.periodperform.extension.impl.DefaultPeriodPerformTriggerExtension;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "DemoMember 周期履约触发扩展点", bizScenes =
        {@Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.PERIOD_PERFORM_TASK_TYPE})})
public class DemoMemberPeriodPerformTriggerExtension extends DefaultPeriodPerformTriggerExtension {
}