package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.domain.entity.trade.OuterSubmitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("tradeDataSource")
public interface OuterSubmitRecordDao extends BaseMapper<OuterSubmitRecord> {

    @Select({
            "SELECT * FROM outer_submit_record where user_id =#{userId} AND outer_id=#{outerId}"
    })
    public OuterSubmitRecord selectByOutId(@Param("userId") long userId, @Param("outerId") String outerId);
}
