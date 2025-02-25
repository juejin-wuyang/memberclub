package com.memberclub.sdk.perform.extension.reverse.impl;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.reverse.AssetsReverseResponse;
import com.memberclub.domain.context.perform.reverse.PerformItemReverseInfo;
import com.memberclub.domain.context.perform.reverse.ReversePerformContext;
import com.memberclub.domain.context.perform.reverse.SubOrderReversePerformContext;
import com.memberclub.sdk.perform.extension.reverse.AssetsReverseExtension;

import java.util.List;

@ExtensionProvider(desc = "默认资格类权益逆向扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = SceneEnum.RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE)
})
public class DefaultQualificationReverseExtension implements AssetsReverseExtension {

    @Override
    public AssetsReverseResponse reverse(ReversePerformContext context,
                                         SubOrderReversePerformContext reverseInfo,
                                         List<PerformItemReverseInfo> items) {
        AssetsReverseResponse response = new AssetsReverseResponse();
        for (PerformItemReverseInfo item : items) {
            CommonLog.warn("清理会员权益资格");
        }

        response.setSuccess(true);
        return response;
    }
}