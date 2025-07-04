package com.comerzzia.cardoso.pos.persistence.agente;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP - AGENTES 
 */
public interface ClienteAgenteMapper{

	int countByExample(ClienteAgenteExample example);

	int deleteByExample(ClienteAgenteExample example);

	int deleteByPrimaryKey(ClienteAgenteKey key);

	int insert(ClienteAgenteKey record);

	int insertSelective(ClienteAgenteKey record);

	List<ClienteAgenteKey> selectByExampleWithRowbounds(ClienteAgenteExample example, RowBounds rowBounds);

	List<ClienteAgenteKey> selectByExample(ClienteAgenteExample example);

	int updateByExampleSelective(@Param("record") ClienteAgenteKey record, @Param("example") ClienteAgenteExample example);

	int updateByExample(@Param("record") ClienteAgenteKey record, @Param("example") ClienteAgenteExample example);
}