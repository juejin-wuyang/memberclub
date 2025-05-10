/**
 * @(#)MemberShipDomainExtension.java, 二月 02, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.sdk.membership.extension;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.memberclub.common.extension.BaseExtension;
import com.memberclub.common.extension.ExtensionConfig;
import com.memberclub.common.extension.ExtensionType;
import com.memberclub.domain.dataobject.membership.MemberShipDO;
import com.memberclub.domain.entity.trade.MemberShip;

/**
 * author: 掘金五阳
 */
@ExtensionConfig(desc = "会员资格扩展点", must = false, type = ExtensionType.PERFORM_MAIN)
public interface MemberShipRepositoryExtension extends BaseExtension {

    public void onGrant(MemberShipDO memberShipDO, MemberShip memberShip);

    public void onCancel(MemberShipDO memberShipDO, LambdaUpdateWrapper<MemberShip> wrapper);

}