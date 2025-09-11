package com.comerzzia.iskaypet.pos.persistence.proformas.lineas.auditorias;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface AuditoriaLineaProformaBeanMapper {
    long countByExample(AuditoriaLineaProformaBeanExample example);

    int deleteByExample(AuditoriaLineaProformaBeanExample example);

    int deleteByPrimaryKey(AuditoriaLineaProformaBeanKey key);

    int insert(AuditoriaLineaProformaBean row);

    int insertSelective(AuditoriaLineaProformaBean row);

    List<AuditoriaLineaProformaBean> selectByExampleWithRowbounds(AuditoriaLineaProformaBeanExample example, RowBounds rowBounds);

    List<AuditoriaLineaProformaBean> selectByExample(AuditoriaLineaProformaBeanExample example);

    AuditoriaLineaProformaBean selectByPrimaryKey(AuditoriaLineaProformaBeanKey key);

    int updateByExampleSelective(@Param("row") AuditoriaLineaProformaBean row, @Param("example") AuditoriaLineaProformaBeanExample example);

    int updateByExample(@Param("row") AuditoriaLineaProformaBean row, @Param("example") AuditoriaLineaProformaBeanExample example);

    int updateByPrimaryKeySelective(AuditoriaLineaProformaBean row);

    int updateByPrimaryKey(AuditoriaLineaProformaBean row);
}