package com.comerzzia.cardoso.pos.persistence.balanza;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 */
public interface TicketBalanzaMapper{

	int countByExample(TicketBalanzaExample example);

	int deleteByExample(TicketBalanzaExample example);

	int deleteByPrimaryKey(TicketBalanzaKey key);

	int insert(TicketBalanzaBean record);

	int insertSelective(TicketBalanzaBean record);

	List<TicketBalanzaBean> selectByExampleWithBLOBsWithRowbounds(TicketBalanzaExample example, RowBounds rowBounds);

	List<TicketBalanzaBean> selectByExampleWithBLOBs(TicketBalanzaExample example);

	List<TicketBalanzaBean> selectByExampleWithRowbounds(TicketBalanzaExample example, RowBounds rowBounds);

	List<TicketBalanzaBean> selectByExample(TicketBalanzaExample example);

	TicketBalanzaBean selectByPrimaryKey(TicketBalanzaKey key);

	int updateByExampleSelective(@Param("record") TicketBalanzaBean record, @Param("example") TicketBalanzaExample example);

	int updateByExampleWithBLOBs(@Param("record") TicketBalanzaBean record, @Param("example") TicketBalanzaExample example);

	int updateByExample(@Param("record") TicketBalanzaBean record, @Param("example") TicketBalanzaExample example);

	int updateByPrimaryKeySelective(TicketBalanzaBean record);

	int updateByPrimaryKeyWithBLOBs(TicketBalanzaBean record);

	int updateByPrimaryKey(TicketBalanzaBean record);

}