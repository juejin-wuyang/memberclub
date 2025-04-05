package com.memberclub.sdk.redeem;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.log.CommonLog;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.dataobject.outer.OuterSubmitContext;
import com.memberclub.domain.dataobject.redeem.RedeemStatusEnum;
import com.memberclub.domain.entity.trade.Redeem;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.RedeemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedeemDomainService {
    @Autowired
    private RedeemDao redeemDao;

    public void validateRedeem(OuterSubmitContext context) {
        LambdaQueryWrapper<Redeem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Redeem::getCode, context.getCmd().getOuterId());

        Redeem redeem = redeemDao.selectOne(wrapper);
        if (redeem == null) {
            throw ResultCode.REDEEM_INVALID_ERROR.newException();
        }
        if (redeem.getStatus() > RedeemStatusEnum.INIT.getCode()) {
            throw ResultCode.REDEEM_USE_ERROR.newException("已兑换，无法再次兑换");
        }
    }

    public void onPreUse(OuterSubmitContext context) {
        LambdaUpdateWrapper<Redeem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Redeem::getCode, context.getCmd().getOuterId());
        wrapper.lt(Redeem::getStatus, RedeemStatusEnum.USING.getCode());
        wrapper.set(Redeem::getStatus, RedeemStatusEnum.USING.getCode());
        wrapper.set(Redeem::getUtime, TimeUtil.now());
        wrapper.set(Redeem::getUserId, context.getUserId());

        int cnt = redeemDao.update(null, wrapper);
        if (cnt <= 0) {
            throw ResultCode.REDEEM_USE_ERROR.newException("已兑换，无法再次兑换");
        }
        CommonLog.warn("可以兑换 code:{} cmd:{}", context.getCmd().getOuterId(), context.getCmd());
    }

    public void onUsed(OuterSubmitContext context) {
        LambdaUpdateWrapper<Redeem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Redeem::getCode, context.getCmd().getOuterId());
        wrapper.lt(Redeem::getStatus, RedeemStatusEnum.USED.getCode());
        wrapper.set(Redeem::getStatus, RedeemStatusEnum.USED.getCode());
        wrapper.set(Redeem::getUtime, TimeUtil.now());
        wrapper.set(Redeem::getRelatedId, context.getCmd().getOuterId());

        int cnt = redeemDao.update(null, wrapper);
        if (cnt <= 0) {
            throw ResultCode.REDEEM_USE_ERROR.newException("兑换失败");
        }
        CommonLog.warn("兑换成功 code:{}, context:{}", context.getCmd().getOuterId(), context);
    }
}
