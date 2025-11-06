package com.comerzzia.dinosol.pos.persistence.encuestas;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EncuestaMapper {

	long countByExample(EncuestaExample example);

    int deleteByExample(EncuestaExample example);

    int deleteByPrimaryKey(EncuestaKey key);

    int insert(Encuesta row);

    int insertSelective(Encuesta row);

    List<Encuesta> selectByExampleWithRowbounds(EncuestaExample example, RowBounds rowBounds);

    List<Encuesta> selectByExample(EncuestaExample example);

    Encuesta selectByPrimaryKey(EncuestaKey key);

    int updateByExampleSelective(@Param("row") Encuesta row, @Param("example") EncuestaExample example);

    int updateByExample(@Param("row") Encuesta row, @Param("example") EncuestaExample example);

    int updateByPrimaryKeySelective(Encuesta row);

    int updateByPrimaryKey(Encuesta row);
}