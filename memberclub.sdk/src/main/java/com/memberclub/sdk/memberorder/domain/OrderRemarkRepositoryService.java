package com.memberclub.sdk.memberorder.domain;

import com.memberclub.common.retry.Retryable;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.entity.trade.OrderRemark;
import com.memberclub.infrastructure.mybatis.mappers.trade.OrderRemarkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderRemarkRepositoryService {

    @Autowired
    private OrderRemarkDao orderRemarkDao;

    @Retryable(throwException = false)
    public void remark(OrderRemark orderRemark) {
        orderRemark.setUtime(TimeUtil.now());
        orderRemark.setCtime(TimeUtil.now());
        orderRemarkDao.insert(orderRemark);
    }
}
