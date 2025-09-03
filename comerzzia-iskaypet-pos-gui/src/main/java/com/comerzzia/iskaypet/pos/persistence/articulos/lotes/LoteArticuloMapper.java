package com.comerzzia.iskaypet.pos.persistence.articulos.lotes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LoteArticuloMapper {
    long countByExample(LoteArticuloExample example);

    int deleteByExample(LoteArticuloExample example);

    int deleteByPrimaryKey(LoteArticuloKey key);

    int insert(LoteArticulo row);

    int insertSelective(LoteArticulo row);

    List<LoteArticulo> selectByExampleWithRowbounds(LoteArticuloExample example, RowBounds rowBounds);

    List<LoteArticulo> selectByExample(LoteArticuloExample example);

    LoteArticulo selectByPrimaryKey(LoteArticuloKey key);

    int updateByExampleSelective(@Param("row") LoteArticulo row, @Param("example") LoteArticuloExample example);

    int updateByExample(@Param("row") LoteArticulo row, @Param("example") LoteArticuloExample example);

    int updateByPrimaryKeySelective(LoteArticulo row);

    int updateByPrimaryKey(LoteArticulo row);
}