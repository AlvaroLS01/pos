package com.comerzzia.cardoso.pos.persistence.lotes;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 * GAP - PERSONALIZACIONES V3 - ARTÍCULOS PELIGROSOS
 */
public interface CardosoAtributosAdicionalesArticuloMapper{

	int countByExample(CardosoAtributosAdicionalesArticuloExample example);

	int deleteByExample(CardosoAtributosAdicionalesArticuloExample example);

	int deleteByPrimaryKey(CardosoAtributosAdicionalesArticuloKey key);

	int insert(CardosoAtributosAdicionalesArticuloBean record);

	int insertSelective(CardosoAtributosAdicionalesArticuloBean record);

	List<CardosoAtributosAdicionalesArticuloBean> selectByExampleWithRowbounds(CardosoAtributosAdicionalesArticuloExample example, RowBounds rowBounds);

	List<CardosoAtributosAdicionalesArticuloBean> selectByExample(CardosoAtributosAdicionalesArticuloExample example);

	CardosoAtributosAdicionalesArticuloBean selectByPrimaryKey(CardosoAtributosAdicionalesArticuloKey key);

	int updateByExampleSelective(@Param("record") CardosoAtributosAdicionalesArticuloBean record, @Param("example") CardosoAtributosAdicionalesArticuloExample example);

	int updateByExample(@Param("record") CardosoAtributosAdicionalesArticuloBean record, @Param("example") CardosoAtributosAdicionalesArticuloExample example);

	int updateByPrimaryKeySelective(CardosoAtributosAdicionalesArticuloBean record);

	int updateByPrimaryKey(CardosoAtributosAdicionalesArticuloBean record);
	
}