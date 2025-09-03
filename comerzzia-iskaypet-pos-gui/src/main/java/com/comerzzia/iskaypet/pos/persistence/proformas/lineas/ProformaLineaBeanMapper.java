package com.comerzzia.iskaypet.pos.persistence.proformas.lineas;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ProformaLineaBeanMapper {
    long countByExample(ProformaLineaBeanExample example);

    int deleteByExample(ProformaLineaBeanExample example);

    int deleteByPrimaryKey(ProformaLineaBeanKey key);

    int insert(ProformaLineaBean row);

    int insertSelective(ProformaLineaBean row);

    List<ProformaLineaBean> selectByExampleWithRowbounds(ProformaLineaBeanExample example, RowBounds rowBounds);

    List<ProformaLineaBean> selectByExample(ProformaLineaBeanExample example);

    ProformaLineaBean selectByPrimaryKey(ProformaLineaBeanKey key);

    int updateByExampleSelective(@Param("row") ProformaLineaBean row, @Param("example") ProformaLineaBeanExample example);

    int updateByExample(@Param("row") ProformaLineaBean row, @Param("example") ProformaLineaBeanExample example);

    int updateByPrimaryKeySelective(ProformaLineaBean row);

    int updateByPrimaryKey(ProformaLineaBean row);
}