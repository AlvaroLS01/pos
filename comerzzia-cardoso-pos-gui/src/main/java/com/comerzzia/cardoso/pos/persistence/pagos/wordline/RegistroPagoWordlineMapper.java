package com.comerzzia.cardoso.pos.persistence.pagos.wordline;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface RegistroPagoWordlineMapper {

	long countByExample(RegistroPagoWordlineExample example);

	int deleteByExample(RegistroPagoWordlineExample example);

	int deleteByPrimaryKey(RegistroPagoWordlineKey key);

	int insert(RegistroPagoWordline record);

	int insertSelective(RegistroPagoWordline record);

	List<RegistroPagoWordline> selectByExampleWithRowbounds(RegistroPagoWordlineExample example, RowBounds rowBounds);

	List<RegistroPagoWordline> selectByExample(RegistroPagoWordlineExample example);

	RegistroPagoWordline selectByPrimaryKey(RegistroPagoWordlineKey key);

	int updateByExampleSelective(@Param("record") RegistroPagoWordline record, @Param("example") RegistroPagoWordlineExample example);

	int updateByExample(@Param("record") RegistroPagoWordline record, @Param("example") RegistroPagoWordlineExample example);

	int updateByPrimaryKeySelective(RegistroPagoWordline record);

	int updateByPrimaryKey(RegistroPagoWordline record);
}