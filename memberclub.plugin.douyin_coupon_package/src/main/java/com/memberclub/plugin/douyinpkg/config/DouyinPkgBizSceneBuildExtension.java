package com.memberclub.plugin.douyinpkg.config;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.BizSceneBuildExtension;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.PerformItemContext;

@ExtensionProvider(desc = "抖音券包 bizScene构建", bizScenes = {
        @Route(bizType = BizTypeEnum.DOUYIN_COUPON_PACKAGE, scenes = SceneEnum.DEFAULT_SCENE)
})
public class DouyinPkgBizSceneBuildExtension implements BizSceneBuildExtension {


    @Override
    public String buildSeparateOrderScene(PerformContext context) {
        if (context.getMemberSubOrders().get(0).getPerformConfig().getConfigs().get(0).getCycle() > 1) {
            return SceneEnum.SCENE_MUTIL_PERIOD_CARD.getValue();
        }
        return SceneEnum.SCENE_MONTH_CARD.getValue();
    }

    @Override
    public String buildPerformItemGrantExtensionScene(PerformItemContext context) {
        return context.getRightType() + "";
    }
}