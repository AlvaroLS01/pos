package com.comerzzia.iskaypet.pos.persistence.proformas.facturada;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ProformaFacturadaBeanMapper {
    long countByExample(ProformaFacturadaBeanExample example);

    int deleteByExample(ProformaFacturadaBeanExample example);

    int deleteByPrimaryKey(ProformaFacturadaBeanKey key);

    int insert(ProformaFacturadaBean row);

    int insertSelective(ProformaFacturadaBean row);

    List<ProformaFacturadaBean> selectByExampleWithRowbounds(ProformaFacturadaBeanExample example, RowBounds rowBounds);

    List<ProformaFacturadaBean> selectByExample(ProformaFacturadaBeanExample example);

    ProformaFacturadaBean selectByPrimaryKey(ProformaFacturadaBeanKey key);

    int updateByExampleSelective(@Param("row") ProformaFacturadaBean row, @Param("example") ProformaFacturadaBeanExample example);

    int updateByExample(@Param("row") ProformaFacturadaBean row, @Param("example") ProformaFacturadaBeanExample example);

    int updateByPrimaryKeySelective(ProformaFacturadaBean row);

    int updateByPrimaryKey(ProformaFacturadaBean row);
}