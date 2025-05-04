/**
 * @(#)DefaultNewMemberExtension.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.newmember.extension;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.usertag.UserTagKeyEnum;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.dataobject.newmember.NewMemberMarkContext;
import com.memberclub.domain.dataobject.sku.UserTypeEnum;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认新会员扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultNewMemberExtension implements NewMemberExtension {

    public static String buildPair(UserTagKeyEnum tagKey, Object value) {
        return String.format("%s:%s", tagKey.getName(), value.toString());
    }

    @Override
    public void buildUserTagOpList(NewMemberMarkContext context) {
        if (context.getSkuNewMemberInfo() == null ||
                !context.getSkuNewMemberInfo().isNewMemberMarkEnable()) {
            CommonLog.info("该商品无需标记新会员 context:{}", context);
            return;
        }

        List<UserTagOpDO> userTagOpDOList = Lists.newArrayList();
        for (UserTypeEnum userType : context.getSkuNewMemberInfo().getUserTypes()) {
            UserTagOpDO userTagOpDO = new UserTagOpDO();
            List<String> pairs = Lists.newArrayList();

            extractAndLoadUserTagType(pairs);
            extractAndLoadBizType(context, pairs);
            extractAndLoadUserType(context, userType, pairs);

            userTagOpDO.setKey(StringUtils.join(pairs, "_"));
            userTagOpDO.setOpCount(1);
            userTagOpDO.setSkuId(context.getSkuId());
            userTagOpDO.setTotalCount(99999999L);//不限
            userTagOpDO.setExpireSeconds(SwitchEnum.NEW_MEMBER_USER_TAG_TIMEOUT.getLong(context.getBizType().getCode()));
            userTagOpDOList.add(userTagOpDO);
        }
        context.setUserTagOpDOList(userTagOpDOList);
    }

    private void extractAndLoadUserTagType(List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.USER_TAG_TYPE, "newmember"));
    }

    private void extractAndLoadUserType(NewMemberMarkContext context, UserTypeEnum userType, List<String> pairs) {
        if (userType == UserTypeEnum.USERID) {
            pairs.add(buildPair(UserTagKeyEnum.USERID, context.getUserId()));
        }
    }

    private void extractAndLoadBizType(NewMemberMarkContext context, List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.BIZTYPE, context.getBizType().getCode()));
    }
}