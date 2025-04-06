package com.memberclub.sdk.quota.extension.aftersale;

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.usertag.UserTagOpDO;
import com.memberclub.domain.context.usertag.UserTagOpTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class AftersaleQuotaExtensionContext {

    long userId;
    List<UserTagOpDO> userTagOpDOList;
    private BizTypeEnum bizType;
    private UserTagOpTypeEnum opType;

    private int opCount;
}
