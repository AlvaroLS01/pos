package com.comerzzia.api.loyalty.persistence.collectives;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ColectivoMapper {
    int countByExample(ColectivoExample example);

    int deleteByExample(ColectivoExample example);

    int deleteByPrimaryKey(ColectivoKey key);

    int insert(ColectivoBean record);

    int insertSelective(ColectivoBean record);

    List<ColectivoBean> selectByExampleWithRowbounds(ColectivoExample example, RowBounds rowBounds);

    List<ColectivoBean> selectByExample(ColectivoExample example);

    ColectivoBean selectByPrimaryKey(ColectivoKey key);

    int updateByExampleSelective(@Param("record") ColectivoBean record, @Param("example") ColectivoExample example);

    int updateByExample(@Param("record") ColectivoBean record, @Param("example") ColectivoExample example);

    int updateByPrimaryKeySelective(ColectivoBean record);

    int updateByPrimaryKey(ColectivoBean record);

    ColectivoBean selectFromViewByPrimaryKey(ColectivoKey key);

    List<ColectivoBean> selectFromViewByExampleWithRowbounds(ColectivoExample example, RowBounds rowBounds);

    List<ColectivoBean> selectFromViewByExample(ColectivoExample example);
}