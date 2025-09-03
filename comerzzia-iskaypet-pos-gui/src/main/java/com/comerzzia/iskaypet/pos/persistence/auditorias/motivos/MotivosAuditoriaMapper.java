package com.comerzzia.iskaypet.pos.persistence.auditorias.motivos;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface MotivosAuditoriaMapper {
    long countByExample(MotivosAuditoriaExample example);

    int deleteByExample(MotivosAuditoriaExample example);

    int deleteByPrimaryKey(MotivosAuditoriaKey key);

    int insert(MotivosAuditoria row);

    int insertSelective(MotivosAuditoria row);

    List<MotivosAuditoria> selectByExampleWithRowbounds(MotivosAuditoriaExample example, RowBounds rowBounds);

    List<MotivosAuditoria> selectByExample(MotivosAuditoriaExample example);

    MotivosAuditoria selectByPrimaryKey(MotivosAuditoriaKey key);

    int updateByExampleSelective(@Param("row") MotivosAuditoria row, @Param("example") MotivosAuditoriaExample example);

    int updateByExample(@Param("row") MotivosAuditoria row, @Param("example") MotivosAuditoriaExample example);

    int updateByPrimaryKeySelective(MotivosAuditoria row);

    int updateByPrimaryKey(MotivosAuditoria row);
}