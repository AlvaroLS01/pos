package com.comerzzia.iskaypet.pos.persistence.proformas.fidelizacion;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProformaFidelizacionBeanMapper {
    long countByExample(ProformaFidelizacionBeanExample example);

    int deleteByExample(ProformaFidelizacionBeanExample example);

    int deleteByPrimaryKey(ProformaFidelizacionBeanKey key);

    int insert(ProformaFidelizacionBeanKey row);

    int insertSelective(ProformaFidelizacionBeanKey row);

    List<ProformaFidelizacionBeanKey> selectByExampleWithRowbounds(ProformaFidelizacionBeanExample example, RowBounds rowBounds);

    List<ProformaFidelizacionBeanKey> selectByExample(ProformaFidelizacionBeanExample example);

    int updateByExampleSelective(@Param("row") ProformaFidelizacionBeanKey row, @Param("example") ProformaFidelizacionBeanExample example);

    int updateByExample(@Param("row") ProformaFidelizacionBeanKey row, @Param("example") ProformaFidelizacionBeanExample example);
}