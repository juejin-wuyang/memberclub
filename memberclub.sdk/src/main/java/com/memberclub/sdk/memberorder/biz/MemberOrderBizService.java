package com.memberclub.sdk.memberorder.biz;

import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberOrderBizService {
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    //TODO 应该实现分页
    public List<MemberOrderDO> queryPayedOrders(long userId) {
        return memberOrderDomainService.queryPayedOrders(userId);
    }
}
