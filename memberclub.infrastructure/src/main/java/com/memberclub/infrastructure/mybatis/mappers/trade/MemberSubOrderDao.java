/**
 * @(#)MemberPerformHisDao.java, 十二月 14, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.common.annotation.QueryMaster;
import com.memberclub.domain.entity.trade.MemberSubOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author 掘金五阳
 */
@DS("tradeDataSource")
@Mapper
public interface MemberSubOrderDao extends BaseMapper<MemberSubOrder> {

    public static final String TABLE_NAME = "member_sub_order";

    Integer insertIgnoreBatch(List<MemberSubOrder> hisList);

    @Select("SELECT * FROM " + TABLE_NAME + " WHERE user_id = #{userId}")
    public List<MemberSubOrder> selectByUserId(@Param("userId") long userId);

    @QueryMaster
    @Select("SELECT * FROM " + TABLE_NAME + " WHERE user_id = #{userId} AND trade_id=#{tradeId} AND sku_id=#{skuId}")
    public MemberSubOrder selectBySkuId(@Param("userId") long userId,
                                        @Param("tradeId") String tradeId,
                                        @Param("skuId") long skuId);

    @QueryMaster
    @Select("SELECT * FROM " + TABLE_NAME + " WHERE user_id = #{userId} AND trade_id=#{tradeId} ")
    public List<MemberSubOrder> selectByTradeId(@Param("userId") long userId,
                                                @Param("tradeId") String tradeId);


    // TODO: 2025/1/5 待确认是否同步更新 extra
    @Update({"UPDATE " + TABLE_NAME + " SET  perform_status=#{toPerformStatus}",
            " ,stime=#{stime}, etime=#{etime}, extra=#{extra}",
            ",utime=#{utime} "
            , " WHERE user_id = #{userId} AND sub_trade_id =#{subTradeId}"})
    public int updatePerformStatusAndTimeAndExtra(@Param("userId") long userId,
                                                  @Param("subTradeId") Long subTradeId,
                                                  @Param("toPerformStatus") int toPerformStatus,
                                                  @Param("extra") String extra,
                                                  @Param("stime") long stime,
                                                  @Param("etime") long etime,
                                                  @Param("utime") long utime);

    // TODO: 2025/1/5 待确认是否同步更新 extra
    @Update({"UPDATE " + TABLE_NAME + " SET  perform_status=#{toPerformStatus}",
            ", extra=#{extra} ",
            ",utime=#{utime} "
            , " WHERE user_id = #{userId} AND sub_trade_id =#{subTradeId} AND status<#{toPerformStatus}"})
    public int updatePerformStatusAndExtra(@Param("userId") long userId,
                                           @Param("subTradeId") Long subTradeId,
                                           @Param("extra") String extra,
                                           @Param("toPerformStatus") int toPerformStatus,
                                           @Param("utime") long utime);

    @Update({"UPDATE ", TABLE_NAME,
            " SET  status=#{status}, perform_status=#{toPerformStatus} ",
            ", utime=#{utime} "
            , " WHERE user_id = #{userId} AND sub_trade_id =#{subTradeId} AND status<#{toPerformStatus}"})
    public int updateStatusAndPerformStatus(@Param("userId") long userId,
                                            @Param("subTradeId") Long subTradeId,
                                            @Param("status") int status,
                                            @Param("toPerformStatus") int toPerformStatus,
                                            @Param("utime") long utime);

    @Update({"UPDATE ", TABLE_NAME,
            " SET  perform_status=#{toPerformStatus} ",
            ", utime=#{utime} "
            , " WHERE user_id = #{userId} AND trade_id =#{tradeId} "})
    public int updatePerformStatus(@Param("userId") long userId,
                                   @Param("tradeId") String tradeId,
                                   @Param("toPerformStatus") int toPerformStatus,
                                   @Param("utime") long utime);

    @Update({
            "UPDATE ", TABLE_NAME,
            " SET status=#{status}, extra=#{extra}, utime=#{utime}, order_id=#{orderId}",
            " ,act_price_fen=#{actPriceFen} WHERE user_id=#{userId} AND sub_trade_id=#{subTradeId} "
    })
    public int updateStatusOnSubmitSuccess(
            @Param("userId") long userId,
            @Param("subTradeId") Long subTradeId,
            @Param("status") Integer status,
            @Param("orderId") String orderId,
            @Param("actPriceFen") Integer actPriceFen,
            @Param("extra") String extra,
            @Param("utime") long utime
    );

    @Update({
            "UPDATE ", TABLE_NAME,
            " SET status=#{status}, extra=#{extra}, utime=#{utime}",
            " WHERE user_id=#{userId} AND sub_trade_id=#{subTradeId} "
    })
    public int updateStatusOnRefundSuccess(
            @Param("userId") long userId,
            @Param("subTradeId") Long subTradeId,
            @Param("status") Integer status,
            @Param("extra") String extra,
            @Param("utime") long utime
    );
}