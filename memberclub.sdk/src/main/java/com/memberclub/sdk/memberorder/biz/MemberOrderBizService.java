package com.memberclub.sdk.memberorder.biz;

import com.memberclub.domain.context.aftersale.contant.AftersaleSourceEnum;
import com.memberclub.domain.dataobject.purchase.MemberOrderDO;
import com.memberclub.sdk.aftersale.service.AfterSaleBizService;
import com.memberclub.sdk.memberorder.domain.MemberOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberOrderBizService {
    @Autowired
    private MemberOrderDomainService memberOrderDomainService;

    @Autowired
    private AfterSaleBizService aftersaleBizService;

    //TODO 应该实现分页
    public List<MemberOrderAftersalePreviewDO> queryPayedOrders(long userId, AftersaleSourceEnum source) {
        List<MemberOrderDO> orders = memberOrderDomainService.queryPayedOrders(userId);

        List<MemberOrderAftersalePreviewDO> previewDOList = new ArrayList<MemberOrderAftersalePreviewDO>();
        for (MemberOrderDO order : orders) {
            MemberOrderAftersalePreviewDO previewDO = new MemberOrderAftersalePreviewDO();
            previewDOList.add(previewDO);
            previewDO.setMemberOrderDO(order);
            /*AfterSalePreviewCmd cmd = new AfterSalePreviewCmd();
            cmd.setUserId(userId);
            cmd.setSource(source);
            cmd.setOperator(String.valueOf(userId));
            cmd.setBizType(order.getBizType());
            cmd.setTradeId(order.getTradeId());*/
            //AfterSalePreviewResponse response = aftersaleBizService.preview(cmd);
            //previewDO.setPreviewResponse(response);
        }
        return previewDOList;
    }
}
