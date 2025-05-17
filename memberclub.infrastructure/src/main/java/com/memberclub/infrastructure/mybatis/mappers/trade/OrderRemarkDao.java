package com.memberclub.infrastructure.mybatis.mappers.trade;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memberclub.domain.entity.trade.OrderRemark;
import org.apache.ibatis.annotations.Mapper;

@DS("tradeDataSource")
@Mapper
public interface OrderRemarkDao extends BaseMapper<OrderRemark> {

    static final String TABLE_NAME = "order_remark";

}
