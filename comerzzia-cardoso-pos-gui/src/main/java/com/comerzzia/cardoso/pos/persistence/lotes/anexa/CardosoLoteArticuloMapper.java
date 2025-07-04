package com.comerzzia.cardoso.pos.persistence.lotes.anexa;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public interface CardosoLoteArticuloMapper{

	int countByExample(CardosoLoteArticuloExample example);

	int deleteByExample(CardosoLoteArticuloExample example);

	int deleteByPrimaryKey(CardosoLoteArticuloKey key);

	int insert(CardosoLoteArticuloBean record);

	int insertSelective(CardosoLoteArticuloBean record);

	List<CardosoLoteArticuloBean> selectByExampleWithRowbounds(CardosoLoteArticuloExample example, RowBounds rowBounds);

	List<CardosoLoteArticuloBean> selectByExample(CardosoLoteArticuloExample example);

	CardosoLoteArticuloBean selectByPrimaryKey(CardosoLoteArticuloKey key);

	int updateByExampleSelective(@Param("record") CardosoLoteArticuloBean record, @Param("example") CardosoLoteArticuloExample example);

	int updateByExample(@Param("record") CardosoLoteArticuloBean record, @Param("example") CardosoLoteArticuloExample example);

	int updateByPrimaryKeySelective(CardosoLoteArticuloBean record);

	int updateByPrimaryKey(CardosoLoteArticuloBean record);
	
}