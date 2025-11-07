package com.comerzzia.api.loyalty.persistence.customers.links;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerLinksMapper {
    int countByExample(EnlaceFidelizadoExample example);

    int deleteByExample(EnlaceFidelizadoExample example);

    int deleteByPrimaryKey(EnlaceFidelizadoBean key);

    int insert(EnlaceFidelizadoBean record);

    int insertSelective(EnlaceFidelizadoBean record);
    
    EnlaceFidelizadoBean selectByPrimaryKey(EnlaceFidelizadoBean record);

    List<EnlaceFidelizadoBean> selectByExampleWithRowbounds(EnlaceFidelizadoExample example, RowBounds rowBounds);

    List<EnlaceFidelizadoBean> selectByExample(EnlaceFidelizadoExample example);

    int updateByExampleSelective(@Param("record") EnlaceFidelizadoBean record, @Param("example") EnlaceFidelizadoExample example);

    int updateByExample(@Param("record") EnlaceFidelizadoBean record, @Param("example") EnlaceFidelizadoExample example);
}