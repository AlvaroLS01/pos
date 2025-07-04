package com.comerzzia.cardoso.pos.persistence.tarifas;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP - DESCUENTO TARIFA
 */
public interface TarifaDetalleAnexaMapper{

	int countByExample(TarifaDetalleAnexaExample example);

	int deleteByExample(TarifaDetalleAnexaExample example);

	int deleteByPrimaryKey(TarifaDetalleAnexaKey key);

	int insert(TarifaDetalleAnexaBean record);

	int insertSelective(TarifaDetalleAnexaBean record);

	List<TarifaDetalleAnexaBean> selectByExampleWithRowbounds(TarifaDetalleAnexaExample example, RowBounds rowBounds);

	List<TarifaDetalleAnexaBean> selectByExample(TarifaDetalleAnexaExample example);

	TarifaDetalleAnexaBean selectByPrimaryKey(TarifaDetalleAnexaKey key);

	int updateByExampleSelective(@Param("record") TarifaDetalleAnexaBean record, @Param("example") TarifaDetalleAnexaExample example);

	int updateByExample(@Param("record") TarifaDetalleAnexaBean record, @Param("example") TarifaDetalleAnexaExample example);

	int updateByPrimaryKeySelective(TarifaDetalleAnexaBean record);

	int updateByPrimaryKey(TarifaDetalleAnexaBean record);
	
}