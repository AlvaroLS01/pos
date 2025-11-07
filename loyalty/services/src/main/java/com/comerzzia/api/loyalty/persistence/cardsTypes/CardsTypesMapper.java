package com.comerzzia.api.loyalty.persistence.cardsTypes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CardsTypesMapper {
    int countByExample(TipoTarjetaExample example);

    int deleteByExample(TipoTarjetaExample example);

    int deleteByPrimaryKey(TipoTarjetaKey key);

    int insert(TipoTarjetaBean record);

    int insertSelective(TipoTarjetaBean record);

    List<TipoTarjetaBean> selectByExampleWithRowbounds(TipoTarjetaExample example, RowBounds rowBounds);

    List<TipoTarjetaBean> selectByExample(TipoTarjetaExample example);

    TipoTarjetaBean selectByPrimaryKey(TipoTarjetaKey key);

    int updateByExampleSelective(@Param("record") TipoTarjetaBean record, @Param("example") TipoTarjetaExample example);

    int updateByExample(@Param("record") TipoTarjetaBean record, @Param("example") TipoTarjetaExample example);

    int updateByPrimaryKeySelective(TipoTarjetaBean record);

    int updateByPrimaryKey(TipoTarjetaBean record);
}