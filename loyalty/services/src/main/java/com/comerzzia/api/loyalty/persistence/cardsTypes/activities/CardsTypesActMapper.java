package com.comerzzia.api.loyalty.persistence.cardsTypes.activities;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CardsTypesActMapper {
    int countByExample(TipoActTarjetaExample example);

    int deleteByExample(TipoActTarjetaExample example);

    int deleteByPrimaryKey(TipoActTarjetaKey key);

    int insert(TipoActTarjetaBean record);

    int insertSelective(TipoActTarjetaBean record);

    List<TipoActTarjetaBean> selectByExampleWithRowbounds(TipoActTarjetaExample example, RowBounds rowBounds);

    List<TipoActTarjetaBean> selectByExample(TipoActTarjetaExample example);

    TipoActTarjetaBean selectByPrimaryKey(TipoActTarjetaKey key);

    int updateByExampleSelective(@Param("record") TipoActTarjetaBean record, @Param("example") TipoActTarjetaExample example);

    int updateByExample(@Param("record") TipoActTarjetaBean record, @Param("example") TipoActTarjetaExample example);

    int updateByPrimaryKeySelective(TipoActTarjetaBean record);

    int updateByPrimaryKey(TipoActTarjetaBean record);

    TipoActTarjetaBean selectFromViewByPrimaryKey(TipoActTarjetaKey key);

    List<TipoActTarjetaBean> selectFromViewByExampleWithRowbounds(TipoActTarjetaExample example, RowBounds rowBounds);

    List<TipoActTarjetaBean> selectFromViewByExample(TipoActTarjetaExample example);
}