/**
 * @(#)MemberSkuTest.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import com.memberclub.domain.common.BizTypeEnum;
import com.memberclub.domain.context.perform.PerformContext;
import com.memberclub.domain.context.perform.SubOrderPerformContext;
import com.memberclub.domain.dataobject.sku.SkuInfoDO;
import com.memberclub.domain.dto.sku.MemberSkuDTO;
import com.memberclub.infrastructure.mapstruct.PerformConvertor;
import org.junit.Test;

/**
 * @author 掘金五阳
 */
public class MemberSkuTest {


    @Test
    public void testMapper() {
        MemberSkuDTO dto = new MemberSkuDTO();
        dto.setBizType(1);
        dto.setSkuId(100001);

        SkuInfoDO memberSkuSnapshotDO = PerformConvertor.INSTANCE.toMemberSkuInfoDO(dto);

        System.out.println(memberSkuSnapshotDO);
    }

    @Test
    public void testMapper2() {
        PerformContext context = new PerformContext();
        SubOrderPerformContext subOrderPerformContext = new SubOrderPerformContext();

        context.setBizType(BizTypeEnum.DEMO_MEMBER);
    }
}