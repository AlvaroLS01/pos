package com.comerzzia.api.loyalty.persistence.customers.wishlists;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerWishListsMapper {
    int countByExample(ListaDeseosFidelizadoExample example);

    int deleteByExample(ListaDeseosFidelizadoExample example);

    int deleteByPrimaryKey(ListaDeseosFidelizadoKey key);

    int insert(ListaDeseosFidelizadoBean record);

    int insertSelective(ListaDeseosFidelizadoBean record);

    List<ListaDeseosFidelizadoBean> selectByExampleWithRowbounds(ListaDeseosFidelizadoExample example, RowBounds rowBounds);

    List<ListaDeseosFidelizadoBean> selectByExample(ListaDeseosFidelizadoExample example);

    ListaDeseosFidelizadoBean selectByPrimaryKey(ListaDeseosFidelizadoKey key);

    int updateByExampleSelective(@Param("record") ListaDeseosFidelizadoBean record, @Param("example") ListaDeseosFidelizadoExample example);

    int updateByExample(@Param("record") ListaDeseosFidelizadoBean record, @Param("example") ListaDeseosFidelizadoExample example);

    int updateByPrimaryKeySelective(ListaDeseosFidelizadoBean record);

    int updateByPrimaryKey(ListaDeseosFidelizadoBean record);
}