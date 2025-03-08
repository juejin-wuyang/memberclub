/**
 * @(#)DefaultMemberShipDomainExtension.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.extension;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.memberclub.common.annotation.Route;
import com.memberclub.common.extension.ExtensionProvider;
import com.memberclub.common.log.CommonLog;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.entity.trade.MemberShip;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberShipDao;
import com.memberclub.sdk.membership.service.MemberShipDataObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * author: 掘金五阳
 */
@ExtensionProvider(desc = "默认身份扩展点实现类", bizScenes = {
        @Route(bizType = BizTypeEnum.DEFAULT),
})
public class DefaultMemberShipDomainExtension implements MemberShipDomainExtension {

    @Autowired
    private MemberShipDataObjectFactory memberShipDataObjectFactory;
    @Autowired
    private MemberShipDao memberShipDao;


    @Override
    public void onGrant(MemberShipDO memberShipDO, MemberShip memberShip) {
        int cnt = memberShipDao.insertIgnoreBatch(Lists.newArrayList(memberShip));
        if (cnt > 0) {
            CommonLog.info("成功写入会员资格:{}", memberShipDO);
            return;
        }
    }

    @Override
    public void onCancel(MemberShipDO memberShipDO, LambdaUpdateWrapper<MemberShip> wrapper) {
        int cnt = memberShipDao.update(null, wrapper);
        if (cnt <= 0) {
            CommonLog.error("取消会员资格失败:{}", memberShipDO);
            return;
        }
        CommonLog.error("取消会员资格成功:{}", memberShipDO);
    }
}