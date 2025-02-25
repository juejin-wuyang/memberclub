package com.memberclub.sdk.perform.extension.execute.impl;

import com.google.common.collect.Maps;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.common.SceneEnum;
import com.memberclub.domain.context.perform.PerformItemContext;
import com.memberclub.domain.context.perform.execute.ItemGrantResult;
import com.memberclub.domain.context.perform.execute.ItemGroupGrantResult;
import com.memberclub.domain.dataobject.perform.MemberPerformItemDO;
import com.memberclub.sdk.perform.extension.execute.AssetsGrantExtension;

import java.util.List;
import java.util.Map;

@ExtensionProvider(desc = "资格类权益默认发放扩展点实现", bizScenes =
        {@Route(bizType = BizTypeEnum.DEMO_MEMBER, scenes = {SceneEnum.RIGHT_TYPE_SCENE_MEMBER_DISCOUNT_PRICE})})
public class DefaultQualificationRightsGrantExtension implements AssetsGrantExtension {

    @Override
    public ItemGroupGrantResult grant(PerformItemContext context, List<MemberPerformItemDO> items) {
        ItemGroupGrantResult result = new ItemGroupGrantResult();
        Map<String, ItemGrantResult> grantMap = Maps.newHashMap();
        result.setGrantMap(grantMap);
        for (MemberPerformItemDO item : items) {
            ItemGrantResult itemGrantResult = new ItemGrantResult();
            itemGrantResult.setBatchCode(item.getItemToken());
            grantMap.put(itemGrantResult.getBatchCode(), itemGrantResult);
        }
        CommonLog.warn("资格类权益发放完成");
        //TODO 异构资格类权益履约数据到缓存！

        return result;
    }

}