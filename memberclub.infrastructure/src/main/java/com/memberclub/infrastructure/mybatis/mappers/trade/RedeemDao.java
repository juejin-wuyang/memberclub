package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.domain.entity.trade.Redeem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("tradeDataSource")
public interface RedeemDao extends BaseMapper<Redeem> {

    /*@Update({
            "UPDATE redeem set user_id=#{userId}, related_id status=#{status}, utime=#{utime} where code=#{code} and status<#{status}"
    })
    public boolean use(@Param("userId") long userId, @Param("code") String code);*/
}
