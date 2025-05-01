package com.memberclub.sdk.purchase.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.memberclub.common.extension.ExtensionManager;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.context.purchase.PurchaseSubmitContext;
import com.memberclub.domain.context.purchase.common.MemberOrderStatusEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.domain.entity.trade.MemberOrder;
import com.memberclub.domain.exception.ResultCode;
import com.memberclub.infrastructure.mybatis.mappers.trade.MemberOrderDao;
import com.memberclub.sdk.config.extension.BizConfigTable;
import com.memberclub.sdk.memberorder.MemberOrderDataObjectBuildFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RenewDomainService {


    @Autowired
    private MemberOrderDao memberOrderDao;

    @Autowired
    private ExtensionManager extensionManager;

    @Autowired
    private MemberOrderDataObjectBuildFactory memberOrderDataObjectBuildFactory;

    /**
     * 查询生效中和待生效的会元单
     *
     * @return
     */
    public void buildNonExpiredOrders(PurchaseSubmitContext context) {
        context.setNonExpiredMemberOrderDOS(getNonExpiredMemberOrders(context.getUserId(), context.getBizType().getCode()));
    }

    public List<MemberOrderDO> getNonExpiredMemberOrders(long userId, int bizType) {
        long now = TimeUtil.now();
        LambdaQueryWrapper<MemberOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberOrder::getUserId, userId)
                .eq(MemberOrder::getBizType, bizType)
                .eq(MemberOrder::getPerformStatus, MemberOrderStatusEnum.PERFORMED.getCode())
                .ge(MemberOrder::getEtime, now)
        ;

        List<MemberOrder> orderList = memberOrderDao.selectList(wrapper);
        return memberOrderDataObjectBuildFactory.buildMemberOrderDOS(orderList);
    }


    public void validateRenewTimes(PurchaseSubmitContext context) {
        int renewTimes = extensionManager.getExtension(context.toDefaultBizScene(), BizConfigTable.class)
                .renewableCount(context.getBizType().getCode());
        if (context.getNonExpiredMemberOrderDOS() != null && context.getNonExpiredMemberOrderDOS().size() > renewTimes) {
            throw ResultCode.RENEW_EXCEED.newException("不允许再次续费购买");
        }
    }

    public void generateStartTime4RenewOrder(PurchaseSubmitContext context) {
        long stime = TimeUtil.now();
        for (MemberOrderDO order : context.getNonExpiredMemberOrderDOS()) {
            if (order.getEtime() + 1 > stime) {
                stime = order.getEtime() + 1;
            }
        }
        context.setStartTime(stime);
    }
}
