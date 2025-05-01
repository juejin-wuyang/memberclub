/**
 * @(#)DefaultQuotaExtension.java, 一月 31, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.quota.extension;

import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.util.PeriodUtils;
import com.memberclub.common.util.TimeRange;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.usertag.UserTagKeyEnum;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.context.usertag.UserTagOpTypeEnum;
import com.memberclub.domain.dataobject.sku.UserTypeEnum;
import com.memberclub.domain.dataobject.sku.restrict.RestrictItemType;
import com.memberclub.domain.dataobject.sku.restrict.RestrictPeriodType;
import com.memberclub.domain.dataobject.sku.restrict.SkuRestrictItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认的配额扩展点", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT)
})
public class DefaultQuotaExtension implements QuotaExtension {

    public static final String TOTAL = "total";

    public static String buildPair(UserTagKeyEnum tagKey, Object value) {
        return String.format("%s:%s", tagKey.getName(), value.toString());
    }

    @Override
    public void buildUserTagOp(QuotaExtensionContext context) {
        List<UserTagOpDO> usertagOps = null;
        if (context.getOpType() == UserTagOpTypeEnum.ADD ||
                context.getOpType() == UserTagOpTypeEnum.GET) {
            usertagOps = extractAndLoadUserTag(context);
        } else if (context.getOpType() == UserTagOpTypeEnum.DEL) {
            usertagOps = extractAndLoadUserTag(context);
        }
        context.setUserTagOpDOList(usertagOps);
    }

    private List<UserTagOpDO> extractAndLoadUserTag(QuotaExtensionContext context) {
        long userId = context.getUserId();
        List<SkuAndRestrictInfo> skus = context.getSkus();
        List<UserTagOpDO> usertags = Lists.newArrayList();
        for (SkuAndRestrictInfo skuInfoDO : skus) {
            if (skuInfoDO.getRestrictInfo() == null ||
                    !skuInfoDO.getRestrictInfo().isEnable() ||
                    CollectionUtils.isEmpty(skuInfoDO.getRestrictInfo().getRestrictItems())) {
                continue;
            }

            List<SkuRestrictItem> restrictItems = skuInfoDO.getRestrictInfo().getRestrictItems();
            for (SkuRestrictItem restrictItem : restrictItems) {
                List<UserTypeEnum> userTypes = CollectionUtils.isEmpty(restrictItem.getUserTypes()) ?
                        Lists.newArrayList(UserTypeEnum.USERID) : restrictItem.getUserTypes();
                for (UserTypeEnum userType : userTypes) {
                    UserTagOpDO tag = new UserTagOpDO();
                    List<String> pairs = Lists.newArrayList();

                    extractAndLoadUserTagType(context, pairs);
                    extractAndLoadBizTypes(context, pairs);
                    extractAndLoadUserTypes(context, userId, userType, pairs);
                    extractAndLoadItemTypes(context, skuInfoDO, restrictItem, pairs);
                    extractAndLoadPeriodTypesAndLoadExpiredTime(context, restrictItem, pairs, tag);

                    tag.setSkuId(skuInfoDO.getSkuId());
                    tag.setKey(StringUtils.join(pairs, "_"));
                    tag.setOpCount(skuInfoDO.getBuyCount());
                    tag.setTotalCount(restrictItem.getTotal());
                    usertags.add(tag);
                }
            }
        }
        return usertags;
    }

    private void extractAndLoadUserTagType(QuotaExtensionContext context, List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.USER_TAG_TYPE, "quota"));
    }

    private void extractAndLoadBizTypes(QuotaExtensionContext context, List<String> pairs) {
        pairs.add(buildPair(UserTagKeyEnum.BIZTYPE, context.getBizType().getCode()));
    }

    private void extractAndLoadUserTypes(QuotaExtensionContext context, long userId, UserTypeEnum userType, List<String> pairs) {
        if (userType == UserTypeEnum.USERID) {
            pairs.add(buildPair(UserTagKeyEnum.USERID, userId));
        }
    }

    private void extractAndLoadItemTypes(QuotaExtensionContext context,
                                         SkuAndRestrictInfo skuInfoDO, SkuRestrictItem restrictItem, List<String> pairs) {
        if (restrictItem.getItemType() != null) {
            if (restrictItem.getItemType() == RestrictItemType.TOTAL) {
                pairs.add(buildPair(UserTagKeyEnum.ITEM_TYPE, TOTAL));
            }
            if (restrictItem.getItemType() == RestrictItemType.SKU) {
                pairs.add(buildPair(UserTagKeyEnum.ITEM_TYPE, skuInfoDO.getSkuId()));
            }
        }
    }

    private void extractAndLoadPeriodTypesAndLoadExpiredTime(QuotaExtensionContext context, SkuRestrictItem restrictItem, List<String> pairs, UserTagOpDO userTagOpDO) {
        if (restrictItem.getPeriodType() != null) {
            if (restrictItem.getPeriodType() == RestrictPeriodType.TOTAL) {
                pairs.add(buildPair(UserTagKeyEnum.PERIOD_TYPE, TOTAL));
                TimeRange timeRange = PeriodUtils.buildTimeRangeFromBaseTime(restrictItem.getPeriodCount());
                userTagOpDO.setExpireSeconds(timeRange.getEtime() - context.getStartTime());
            }
        }
    }

}