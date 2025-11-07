package com.comerzzia.api.loyalty.persistence.customers.wishlists.details;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoyalCustomerWishListDetailsMapper {
    int countByExample(ArticuloListaDeseosFidelizadoExample example);

    int deleteByExample(ArticuloListaDeseosFidelizadoExample example);

    int deleteByPrimaryKey(ArticuloListaDeseosFidelizadoKey key);

    int insert(ArticuloListaDeseosFidelizadoBean record);

    int insertSelective(ArticuloListaDeseosFidelizadoBean record);

    List<ArticuloListaDeseosFidelizadoBean> selectByExampleWithRowbounds(ArticuloListaDeseosFidelizadoExample example, RowBounds rowBounds);

    List<ArticuloListaDeseosFidelizadoBean> selectByExample(ArticuloListaDeseosFidelizadoExample example);

    ArticuloListaDeseosFidelizadoBean selectByPrimaryKey(ArticuloListaDeseosFidelizadoKey key);

    int updateByExampleSelective(@Param("record") ArticuloListaDeseosFidelizadoBean record, @Param("example") ArticuloListaDeseosFidelizadoExample example);

    int updateByExample(@Param("record") ArticuloListaDeseosFidelizadoBean record, @Param("example") ArticuloListaDeseosFidelizadoExample example);

    int updateByPrimaryKeySelective(ArticuloListaDeseosFidelizadoBean record);

    int updateByPrimaryKey(ArticuloListaDeseosFidelizadoBean record);
}