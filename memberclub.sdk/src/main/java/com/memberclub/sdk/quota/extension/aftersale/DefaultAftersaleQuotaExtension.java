package com.memberclub.sdk.quota.extension.aftersale;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.util.PeriodUtils;
import com.memberclub.common.util.TimeRange;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.usertag.UserTagKeyEnum;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.context.usertag.UserTagOpTypeEnum;
import com.memberclub.domain.dataobject.sku.UserTypeEnum;
import com.memberclub.infrastructure.dynamic_config.SwitchEnum;
import org.apache.commons.lang.StringUtils;

import java.util.List;

@ExtensionProvider(desc = "默认售后配额能力", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultAftersaleQuotaExtension implements AftersaleQuotaExtension {

    public static String buildPair(UserTagKeyEnum tagKey, Object value) {
        return String.format("%s:%s", tagKey.getName(), value.toString());
    }

    @Override
    public void buildUserTagOp(AftersaleQuotaExtensionContext context) {
        List<UserTagOpDO> usertagOps = null;
        if (context.getOpType() == UserTagOpTypeEnum.ADD ||
                context.getOpType() == UserTagOpTypeEnum.GET) {
            usertagOps = extractAndLoadUserTag(context);
        }
        context.setUserTagOpDOList(usertagOps);
    }

    private List<UserTagOpDO> extractAndLoadUserTag(AftersaleQuotaExtensionContext context) {
        long userId = context.getUserId();

        List<UserTagOpDO> usertags = Lists.newArrayList();

        UserTagOpDO tag = new UserTagOpDO();
        List<String> pairs = Lists.newArrayList();

        extractAndLoadUserTagType(context, pairs);
        extractAndLoadBizTypes(context, pairs);
        extractAndLoadUserTypes(context, userId, UserTypeEnum.USERID, pairs);
        extractAndLoadItemTypes(context, pairs);
        extractAndLoadPeriodTypesAndLoadExpiredTime(context, pairs, tag);

        tag.setKey(StringUtils.join(pairs, "_"));
        tag.setOpCount(context.getOpCount());
        tag.setTotalCount(SwitchEnum.AFTERSALE_QUOTA_MAX_VALUE.getLong(context.getBizType().getCode()));
        usertags.add(tag);

        return usertags;
    }

    private void extractAndLoadUserTagType(AftersaleQuotaExtensionContext context, List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.USER_TAG_TYPE, "aftersale_quota"));
    }

    private void extractAndLoadBizTypes(AftersaleQuotaExtensionContext context, List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.BIZTYPE, context.getBizType().getCode()));
    }

    private void extractAndLoadUserTypes(AftersaleQuotaExtensionContext context, long userId, UserTypeEnum userType, List<String> pairs) {
        if (userType == UserTypeEnum.USERID) {
            pairs.add(buildPair(UserTagKeyEnum.USERID, userId));
        }
    }

    private void extractAndLoadItemTypes(AftersaleQuotaExtensionContext context,
                                         List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.ITEM_TYPE, "total"));
    }

    private void extractAndLoadPeriodTypesAndLoadExpiredTime(AftersaleQuotaExtensionContext context,
                                                             List<String> pairs, UserTagOpDO userTagOpDO) {
        pairs.add(buildPair(UserTagKeyEnum.PERIOD_TYPE, "total"));
        TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(SwitchEnum.AFTERSALE_QUOTA_EXPIRE_DAYS.getInt(context.getBizType().getCode()));
        userTagOpDO.setExpireSeconds(timeRange.getEtime() - TimeUtil.now());
    }
}
