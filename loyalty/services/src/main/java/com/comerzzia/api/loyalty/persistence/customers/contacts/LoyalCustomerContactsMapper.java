package com.comerzzia.api.loyalty.persistence.customers.contacts;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerContactsMapper {
    int countByExample(TiposContactoFidelizadoExample example);

    int deleteByExample(TiposContactoFidelizadoExample example);

    int deleteByPrimaryKey(TiposContactoFidelizadoKey key);

    int insert(TiposContactoFidelizadoBean record);

    int insertSelective(TiposContactoFidelizadoBean record);

    List<TiposContactoFidelizadoBean> selectByExampleWithRowbounds(TiposContactoFidelizadoExample example, RowBounds rowBounds);

    List<TiposContactoFidelizadoBean> selectByExample(TiposContactoFidelizadoExample example);

    TiposContactoFidelizadoBean selectByPrimaryKey(TiposContactoFidelizadoKey key);

    int updateByExampleSelective(@Param("record") TiposContactoFidelizadoBean record, @Param("example") TiposContactoFidelizadoExample example);

    int updateByExample(@Param("record") TiposContactoFidelizadoBean record, @Param("example") TiposContactoFidelizadoExample example);

    int updateByPrimaryKeySelective(TiposContactoFidelizadoBean record);

    int updateByPrimaryKey(TiposContactoFidelizadoBean record);
}