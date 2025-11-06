package com.comerzzia.dinosol.pos.persistence.encuestas.preguntas;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface PreguntaEncuestaMapper {
    long countByExample(PreguntaEncuestaExample example);

    int deleteByExample(PreguntaEncuestaExample example);

    int deleteByPrimaryKey(PreguntaEncuestaKey key);

    int insert(PreguntaEncuesta row);

    int insertSelective(PreguntaEncuesta row);

    List<PreguntaEncuesta> selectByExampleWithRowbounds(PreguntaEncuestaExample example, RowBounds rowBounds);

    List<PreguntaEncuesta> selectByExample(PreguntaEncuestaExample example);

    PreguntaEncuesta selectByPrimaryKey(PreguntaEncuestaKey key);

    int updateByExampleSelective(@Param("row") PreguntaEncuesta row, @Param("example") PreguntaEncuestaExample example);

    int updateByExample(@Param("row") PreguntaEncuesta row, @Param("example") PreguntaEncuestaExample example);

    int updateByPrimaryKeySelective(PreguntaEncuesta row);

    int updateByPrimaryKey(PreguntaEncuesta row);
}