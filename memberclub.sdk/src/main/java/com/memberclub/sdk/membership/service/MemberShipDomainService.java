/**
 * @(#)MemberShipDomainService.java, 二月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.CollectionUtilEx;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.common.BizScene;
import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.common.ShipTypeEnum;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.dataobject.membership.MemberShipItemDO;
import com.memberclub.domain.dataobject.membership.MemberShipStatusEnum;
import com.memberclub.domain.dataobject.membership.MemberShipUnionDO;
import com.memberclub.domain.entity.trade.MemberShip;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberShipDao;
import com.memberclub.sdk.membership.extension.MemberShipRepositoryExtension;
import com.memberclub.sdk.util.TransactionHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * author: 掘金五阳
 */
@Service
public class MemberShipDomainService {

    @Autowired
    private MemberShipDao memberShipDao;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MemberShipDataObjectFactory memberShipDataObjectFactory;

    @Autowired
    private MemberShipCacheService memberShipCacheService;


    public MemberShipDO getMemberShipDO(long userId, String grantCode) {
        LambdaQueryWrapper<MemberShip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberShip::getGrantCode, grantCode);
        wrapper.eq(MemberShip::getUserId, userId);
        MemberShip memberShip = memberShipDao.selectOne(wrapper);
        return memberShipDataObjectFactory.buildMemberShipDO(memberShip);
    }


    @Transactional(rollbackFor = Exception.class)
    public void grant(MemberShipDO memberShipDO) {
        memberShipDO.onFinish();
        MemberShip memberShip = memberShipDataObjectFactory.buildMemberShip(memberShipDO);
        extensionManager.getExtension(BizScene.of(memberShipDO.getBizType()),
                MemberShipRepositoryExtension.class).onGrant(memberShipDO, memberShip);

        MemberShipUnionDO memberShipUnionDO = union(memberShipDO);
        if (memberShipUnionDO == null) {
            throw ResultCode.PERFORM_ITEM_GRANT_ERROR.newException("会员资格同步失败");
        }

        TransactionHelper.afterCommitExecute(() -> {
            memberShipCacheService.sync(memberShipUnionDO);
        });
    }

    public MemberShipUnionDO union(MemberShipDO memberShipDO) {
        List<MemberShip> memberShips = queryActiveMemberShips(memberShipDO.getUserId(), memberShipDO.getBizType(), memberShipDO.getShipType());
        MemberShipUnionDO memberShipUnionDO = new MemberShipUnionDO();
        memberShipUnionDO.setBizType(memberShipDO.getBizType().getCode());
        memberShipUnionDO.setShipType(memberShipDO.getShipType().getCode());
        memberShipUnionDO.setUserId(memberShipDO.getUserId());
        memberShipUnionDO.setItems(
                CollectionUtilEx.mapToList(memberShips, (m) -> {
                    MemberShipItemDO itemDO = new MemberShipItemDO();
                    itemDO.setEtime(m.getEtime());
                    itemDO.setStime(m.getStime());
                    itemDO.setGrantCode(m.getGrantCode());
                    itemDO.setUsedCount(m.getUsedCount());
                    itemDO.setTotalCount(m.getTotalCount());
                    return itemDO;
                })
        );
        if (CollectionUtils.isEmpty(memberShipUnionDO.getItems())) {
            CommonLog.error("未找到生效中的会员资格");
            return null;
        }
        long minStime = memberShipUnionDO.getItems().
                stream().min(Comparator.comparingLong(MemberShipItemDO::getStime)).get().getStime();
        long maxEtime = memberShipUnionDO.getItems().
                stream().max(Comparator.comparingLong(MemberShipItemDO::getEtime)).get().getEtime();
        memberShipUnionDO.setStime(minStime);
        memberShipUnionDO.setEtime(maxEtime);
        return memberShipUnionDO;
    }

    public List<MemberShip> queryActiveMemberShips(long userId, BizTypeEnum bizType, ShipTypeEnum shipTypeEnum) {
        LambdaQueryWrapper<MemberShip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberShip::getUserId, userId);
        wrapper.eq(MemberShip::getBizType, bizType.getCode());
        wrapper.eq(MemberShip::getShipType, shipTypeEnum.getCode());
        wrapper.eq(MemberShip::getStatus, MemberShipStatusEnum.FINISH.getCode());
        wrapper.ge(MemberShip::getEtime, TimeUtil.now());

        List<MemberShip> memberShips = memberShipDao.selectList(wrapper);
        return memberShips;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancel(MemberShipDO memberShipDO) {
        if (memberShipDO.getStatus() == MemberShipStatusEnum.CANCEL) {
            CommonLog.warn("会员资格发放记录已经取消, 无需再次取消 memberShipDO:{}", memberShipDO);
            return;
        }
        memberShipDO.onCancel();

        LambdaUpdateWrapper<MemberShip> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberShip::getUserId, memberShipDO.getUserId());
        wrapper.eq(MemberShip::getItemToken, memberShipDO.getItemToken());
        wrapper.set(MemberShip::getStatus, memberShipDO.getStatus().getCode());
        wrapper.set(MemberShip::getUtime, memberShipDO.getUtime());

        extensionManager.getExtension(BizScene.of(memberShipDO.getBizType()),
                MemberShipRepositoryExtension.class).onCancel(memberShipDO, wrapper);

        MemberShipUnionDO memberShipUnionDO = union(memberShipDO);

        TransactionHelper.afterCommitExecute(() -> {
            if (memberShipUnionDO == null) {
                memberShipCacheService.remove(memberShipDO.getBizType(), memberShipDO.getShipType(), memberShipDO.getUserId());
            } else {
                memberShipCacheService.sync(memberShipUnionDO);
            }
        });
    }
}