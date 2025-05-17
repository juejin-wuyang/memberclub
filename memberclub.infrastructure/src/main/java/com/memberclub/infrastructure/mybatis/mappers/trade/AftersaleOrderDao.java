/**
 * @(#)AftersaleOrderDao.java, 一月 01, 2025.
 * <p>
 * Copyright 2025 memberclub.com. All rights reserved.
 * memberclub.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.common.annotation.QueryMaster;
import com.memberclub.domain.entity.trade.AftersaleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * author: 掘金五阳
 */
@Mapper
@DS("tradeDataSource")
public interface AftersaleOrderDao extends BaseMapper<AftersaleOrder> {

    public static final String TABLE_NAME = "aftersale_order";

    Integer insertIgnoreBatch(List<AftersaleOrder> orders);

    @QueryMaster
    @Select({
            "SELECT * FROM ", TABLE_NAME,
            " WHERE user_id=#{userId} AND id=#{id} "
    })
    public AftersaleOrder queryById(@Param("userId") long userId, @Param("id") Long id);


    @QueryMaster
    @Select({
            "SELECT * FROM ", TABLE_NAME,
            " WHERE user_id=#{userId} AND preview_token=#{previewToken} "
    })
    public AftersaleOrder queryByPreviewToken(@Param("userId") long userId, @Param("previewToken") String previewToken);

    @QueryMaster
    @Select({
            "SELECT * FROM ", TABLE_NAME,
            " WHERE user_id=#{userId} AND trade_id=#{tradeId} "
    })
    public List<AftersaleOrder> queryByTradeId(@Param("userId") long userId, @Param("tradeId") String tradeId);


    @Update({
            "UPDATE ", TABLE_NAME, " SET status=#{status} ,utime=#{utime} ",
            " WHERE user_id=#{userId} AND id=#{id} "
    })
    public int updateStatus(@Param("userId") long userId, @Param("id") Long id,
                            @Param("status") int status, @Param("utime") long utime);

    @Update({
            "UPDATE ", TABLE_NAME, " SET status=#{status} ",
            " , act_refund_price_fen = #{actRefundPriceFen} ",
            " ,extra=#{extra}, utime=#{utime} ",
            " WHERE user_id=#{userId} AND id=#{id} "
    })
    public int updateStatusAndRefundPrice(@Param("userId") long userId, @Param("id") Long id,
                                          @Param("actRefundPriceFen") int actRefundPriceFen,
                                          @Param("extra") String extra,
                                          @Param("status") int status, @Param("utime") long utime);
}