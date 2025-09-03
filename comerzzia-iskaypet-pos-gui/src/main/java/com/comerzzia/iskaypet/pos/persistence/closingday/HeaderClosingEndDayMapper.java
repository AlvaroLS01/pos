package com.comerzzia.iskaypet.pos.persistence.closingday;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP27.2 - AMPLIACIÓN DEL CIERRE DE FIN DE DÍA
 */
public interface HeaderClosingEndDayMapper{

	long countByExample(HeaderClosingEndDayExample example);

	int deleteByExample(HeaderClosingEndDayExample example);

	int deleteByPrimaryKey(HeaderClosingEndDayKey key);

	int insert(HeaderClosingEndDay record);

	int insertSelective(HeaderClosingEndDay record);

	List<HeaderClosingEndDay> selectByExampleWithRowbounds(HeaderClosingEndDayExample example, RowBounds rowBounds);

	List<HeaderClosingEndDay> selectByExample(HeaderClosingEndDayExample example);

	HeaderClosingEndDay selectByPrimaryKey(HeaderClosingEndDayKey key);

	int updateByExampleSelective(@Param("record") HeaderClosingEndDay record, @Param("example") HeaderClosingEndDayExample example);

	int updateByExample(@Param("record") HeaderClosingEndDay record, @Param("example") HeaderClosingEndDayExample example);

	int updateByPrimaryKeySelective(HeaderClosingEndDay record);

	int updateByPrimaryKey(HeaderClosingEndDay record);
	
}