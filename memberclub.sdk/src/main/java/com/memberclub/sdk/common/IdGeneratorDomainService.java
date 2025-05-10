package com.memberclub.sdk.common;

import com.memberclub.infrastructure.id.IdGenerator;
import com.memberclub.infrastructure.id.IdTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorDomainService {

    @Autowired
    private IdGenerator idGenerator;

    public Long generateId(IdTypeEnum idType) {
        Long id = idGenerator.generateId(idType);
        return id;
    }

    public Long generateOrderId(long userId) {
        Long id = generateId(IdTypeEnum.ORDER_TRADE_ID);
        return id * 100 + userId % 100;//订单Id末尾追加userId
    }
}
