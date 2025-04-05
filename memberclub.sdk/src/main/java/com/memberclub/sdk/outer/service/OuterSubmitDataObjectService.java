/**
 * @(#)OuterSubmitDataObjectService.java, 四月 05, 2025.
 * <p>
 * Copyright 2025 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.outer.service;

import com.memberclub.common.util.JsonUtils;
import com.memberclub.common.util.TimeUtil;
import com.memberclub.domain.dataobject.outer.*;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import com.memberclub.infrastructure.mapstruct.PurchaseConvertor;
import org.springframework.stereotype.Service;

/**
 * author: 掘金五阳
 */
@Service
public class OuterSubmitDataObjectService {

    public OuterSubmitContext buildContext(OuterSubmitCmd cmd) {
        OuterSubmitContext context = new OuterSubmitContext();
        context.setBizType(cmd.getBizType());
        context.setUserId(cmd.getUserId());
        context.setCmd(cmd);
        return context;
    }

    public OuterSubmitRecordDO buildRecord(OuterSubmitContext context) {
        OuterSubmitRecordDO record = new OuterSubmitRecordDO();
        record.setBizType(context.getBizType());
        record.setOuterId(context.getCmd().getOuterId());
        record.setUserId(context.getUserId());
        record.setCtime(TimeUtil.now());
        record.setUtime(TimeUtil.now());
        record.setOuterConfigId(context.getCmd().getOuterConfigId());
        record.setOuterType(context.getCmd().getOuterType());
        record.setStatus(OuterSubmitStatusEnum.INIT);
        record.setExtra(new OuterSubmitRecordExtraDO());
        record.getExtra().setSkus(context.getCmd().getSkus());
        record.setSkus(context.getCmd().getSkus());
        return record;
    }

    public OuterSubmitRecord buildRecord(OuterSubmitRecordDO recordDO) {
        OuterSubmitRecord record = PurchaseConvertor.INSTANCE.toOuterSubmitRecord(recordDO);
        record.setBizType(recordDO.getBizType().getCode());
        record.setOuterType(recordDO.getOuterType().getCode());
        record.setExtra(JsonUtils.toJson(recordDO.getExtra()));
        record.setStatus(recordDO.getStatus().getCode());
        return record;
    }
}