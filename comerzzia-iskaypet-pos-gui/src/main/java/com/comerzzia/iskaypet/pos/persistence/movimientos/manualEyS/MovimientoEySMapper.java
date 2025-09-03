package com.comerzzia.iskaypet.pos.persistence.movimientos.manualEyS;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MovimientoEySMapper {
    long countByExample(MovimientoEySExample example);

    int deleteByExample(MovimientoEySExample example);

    int deleteByPrimaryKey(MovimientoEySKey key);

    int insert(MovimientoEyS record);

    int insertSelective(MovimientoEyS record);

    List<MovimientoEyS> selectByExample(MovimientoEySExample example);

    MovimientoEyS selectByPrimaryKey(MovimientoEySKey key);

    int updateByExampleSelective(@Param("record") MovimientoEyS record, @Param("example") MovimientoEySExample example);

    int updateByExample(@Param("record") MovimientoEyS record, @Param("example") MovimientoEySExample example);

    int updateByPrimaryKeySelective(MovimientoEyS record);

    int updateByPrimaryKey(MovimientoEyS record);
}