package com.comerzzia.api.loyalty.persistence.collectivesTypes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TipoColectivoMapper {
    int countByExample(TipoColectivoExample example);

    int deleteByExample(TipoColectivoExample example);

    int deleteByPrimaryKey(TipoColectivoKey key);

    int insert(TipoColectivoBean record);

    int insertSelective(TipoColectivoBean record);

    List<TipoColectivoBean> selectByExampleWithRowbounds(TipoColectivoExample example, RowBounds rowBounds);

    List<TipoColectivoBean> selectByExample(TipoColectivoExample example);

    TipoColectivoBean selectByPrimaryKey(TipoColectivoKey key);

    int updateByExampleSelective(@Param("record") TipoColectivoBean record, @Param("example") TipoColectivoExample example);

    int updateByExample(@Param("record") TipoColectivoBean record, @Param("example") TipoColectivoExample example);

    int updateByPrimaryKeySelective(TipoColectivoBean record);

    int updateByPrimaryKey(TipoColectivoBean record);
}