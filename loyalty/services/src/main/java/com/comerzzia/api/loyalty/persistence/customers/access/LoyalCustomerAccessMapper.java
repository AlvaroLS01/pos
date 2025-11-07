package com.comerzzia.api.loyalty.persistence.customers.access;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerAccessMapper {
    int countByExample(AccesoFidelizadoExample example);

    int deleteByExample(AccesoFidelizadoExample example);

    int deleteByPrimaryKey(AccesoFidelizadoKey key);

    int insert(AccesoFidelizadoBean record);

    int insertSelective(AccesoFidelizadoBean record);

    List<AccesoFidelizadoBean> selectByExampleWithRowbounds(AccesoFidelizadoExample example, RowBounds rowBounds);

    List<AccesoFidelizadoBean> selectByExample(AccesoFidelizadoExample example);

    AccesoFidelizadoBean selectByPrimaryKey(AccesoFidelizadoKey key);

    int updateByExampleSelective(@Param("record") AccesoFidelizadoBean record, @Param("example") AccesoFidelizadoExample example);

    int updateByExample(@Param("record") AccesoFidelizadoBean record, @Param("example") AccesoFidelizadoExample example);

    int updateByPrimaryKeySelective(AccesoFidelizadoBean record);

    int updateByPrimaryKey(AccesoFidelizadoBean record);
}