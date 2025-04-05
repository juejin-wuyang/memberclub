package com.memberclub.plugin.demomember.extension.outer;

import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.sdk.outer.extension.impl.DefaultOuterSubmitExtension;

import javax.annotation.PostConstruct;

@ExtensionProvider(desc = "DemoMember 外部购买下单扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.OUTER_SUBMIT_PURCHASE_SCENE})
})
public class DemoMemberOuterPurchaseSubmitExtension extends DefaultOuterSubmitExtension {

    @PostConstruct
    public void init() {
        defualtInit();
    }
    
}
