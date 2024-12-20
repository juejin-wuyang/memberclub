/**
 * @(#)MemberPerformItemDao.java, 十二月 17, 2024.
 * <p>
 * Copyright 2024 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.common.annotation.QueryMaster;
import com.memberclub.domain.entity.MemberPerformItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Mapper
public interface MemberPerformItemDao extends BaseMapper<MemberPerformItem> {

    public static final String TABLE = "member_perform_item";

    Integer insertIgnoreBatch(List<MemberPerformItem> items);

    @QueryMaster
    @Select({"SELECT * FROM ", TABLE, " WHERE user_id=#{userId} AND item_token=#{itemToken}"})
    public MemberPerformItem queryByItemToken(@Param("userId") long userId, @Param("itemToken") String itemToken);


    @QueryMaster
    @Select({"SELECT * FROM ", TABLE, " WHERE user_id=#{userId} AND trade_id=#{tradeId}"})
    public List<MemberPerformItem> selectByTradeId(@Param("userId") long userId, @Param("tradeId") String tradeId);


    @Update({"UPDATE ", TABLE, " SET status=#{status}, batch_code=#{batchCode} WHERE user_id=#{userId} AND item_token=#{itemToken}"})
    public int update2Status(@Param("userId") long userId,
                             @Param("itemToken") String itemToken,
                             @Param("batchCode") String batchCode,
                             @Param("status") int status);

}