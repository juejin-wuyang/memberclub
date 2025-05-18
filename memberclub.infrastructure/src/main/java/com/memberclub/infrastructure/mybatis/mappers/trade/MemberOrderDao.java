/**
 * @(#)MemberOrderDao.java, 十二月 15, 2024.
 * <p>
 * Copyright 2024 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.common.annotation.QueryMaster;
import com.memberclub.domain.entity.trade.MemberOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author wuyang
 */
@Mapper
@DS("tradeDataSource")
public interface MemberOrderDao extends BaseMapper<MemberOrder> {

    static final String TABLE_NAME = "member_order";

    Integer insertIgnoreBatch(List<MemberOrder> orders);

    // 更新履约状态
    @Update({"<script> UPDATE ", TABLE_NAME,
            " SET perform_status=#{toPerformStatus}, utime=#{utime} ",
            "WHERE user_id=#{userId} AND trade_id=#{tradeId} AND perform_status IN ",
            "<foreach collection='fromPerformStatuses' item='status' separator=',' open='(' close=')'> ",
            " #{status} ",
            "</foreach>",
            "</script>"})
    public int updatePerformStatus(@Param("userId") long userId,
                                   @Param("tradeId") String tradeId,
                                   @Param("toPerformStatus") int toPerformStatus,
                                   @Param("fromPerformStatuses") List<Integer> fromPerformStatuses,
                                   @Param("utime") long utime);

    // 更新履约到成功
    @Update({"<script> UPDATE ", TABLE_NAME,
            " SET perform_status=#{toPerformStatus}, status=#{status}, utime=#{utime}, stime=#{stime}, etime=#{etime} ",
            "WHERE user_id=#{userId} AND trade_id=#{tradeId} AND perform_status IN ",
            "<foreach collection='fromPerformStatuses' item='status' separator=',' open='(' close=')'> ",
            " #{status} ",
            "</foreach>",
            "</script>"})
    public int updateStatus2PerformSucc(@Param("userId") long userId,
                                        @Param("tradeId") String tradeId,
                                        @Param("stime") long stime,
                                        @Param("etime") long etime,
                                        @Param("status") int status,
                                        @Param("toPerformStatus") int toPerformStatus,
                                        @Param("fromPerformStatuses") List<Integer> fromPerformStatuses,
                                        @Param("utime") long utime);


    // 更新履约失败
    @Update({"<script> UPDATE ", TABLE_NAME,
            " SET perform_status=#{toPerformStatus},utime=#{utime} ",
            "WHERE user_id=#{userId} AND trade_id=#{tradeId} ",
            "</script>"})
    public int updateStatus2PerformFail(@Param("userId") long userId,
                                        @Param("tradeId") String tradeId,
                                        @Param("toPerformStatus") int toPerformStatus,
                                        @Param("utime") long utime);

    @Update({
            "UPDATE ", TABLE_NAME,
            " SET status=#{status}, utime=#{utime} ",
            " WHERE user_id=#{userId} AND trade_id=#{tradeId} and status<#{status}"
    })
    public int updateStatus2RefundSuccess(@Param("userId") long userId,
                                          @Param("tradeId") String tradeId,
                                          @Param("status") int status,
                                          @Param("utime") long utime);


    @Update({
            "UPDATE ", TABLE_NAME,
            " SET perform_status=#{performStatus}, utime=#{utime} ",
            " WHERE user_id=#{userId} AND trade_id=#{tradeId} "
    })
    public int updatePerformStatus2ReverseSuccess(@Param("userId") long userId,
                                                  @Param("tradeId") String tradeId,
                                                  @Param("performStatus") int performStatus,
                                                  @Param("utime") long utime);


    // 更新主状态到提单完成
    @Update({
            "UPDATE ", TABLE_NAME,
            " SET status=#{status}, extra=#{extra}, utime=#{utime}, order_id=#{orderId}",
            " ,act_price_fen=#{actPriceFen} WHERE user_id=#{userId} AND trade_id=#{tradeId} "
    })
    public int updateStatusOnSubmitSuccess(
            @Param("userId") long userId,
            @Param("tradeId") String tradeId,
            @Param("status") Integer status,
            @Param("orderId") String orderId,
            @Param("actPriceFen") Integer actPriceFen,
            @Param("extra") String extra,
            @Param("utime") long utime
    );


    @QueryMaster
    @Select({"SELECT * FROM ", TABLE_NAME,
            " WHERE user_id=#{userId} AND trade_id=#{tradeId} "})
    public MemberOrder selectByTradeId(@Param("userId") long userId,
                                       @Param("tradeId") String tradeId);


}