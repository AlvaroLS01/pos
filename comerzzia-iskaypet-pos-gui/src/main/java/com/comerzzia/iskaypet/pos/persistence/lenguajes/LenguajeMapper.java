package com.comerzzia.iskaypet.pos.persistence.lenguajes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface LenguajeMapper {
    int countByExample(LenguajeExample example);

    int deleteByExample(LenguajeExample example);

    int deleteByPrimaryKey(LenguajeKey key);

    int insert(LenguajeBean record);

    int insertSelective(LenguajeBean record);

    List<LenguajeBean> selectByExampleWithRowbounds(LenguajeExample example, RowBounds rowBounds);

    List<LenguajeBean> selectByExample(LenguajeExample example);

    LenguajeBean selectByPrimaryKey(LenguajeKey key);

    int updateByExampleSelective(@Param("record") LenguajeBean record, @Param("example") LenguajeExample example);

    int updateByExample(@Param("record") LenguajeBean record, @Param("example") LenguajeExample example);

    int updateByPrimaryKeySelective(LenguajeBean record);

    int updateByPrimaryKey(LenguajeBean record);
}