package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados;

import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosExample;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TicketContratosMapper {
    int countByExample(TicketContratosExample example);

    int deleteByExample(TicketContratosExample example);

    int deleteByPrimaryKey(TicketContratosKey key);

    int insert(TicketContratosBean record);

    int insertSelective(TicketContratosBean record);

    List<TicketContratosBean> selectByExampleWithBLOBsWithRowbounds(TicketContratosExample example, RowBounds rowBounds);

    List<TicketContratosBean> selectByExampleWithBLOBs(TicketContratosExample example);

    List<TicketContratosBean> selectByExampleWithRowbounds(TicketContratosExample example, RowBounds rowBounds);

    List<TicketContratosBean> selectByExample(TicketContratosExample example);

    TicketContratosBean selectByPrimaryKey(TicketContratosKey key);

    int updateByExampleSelective(@Param("record") TicketContratosBean record, @Param("example") TicketContratosExample example);

    int updateByExampleWithBLOBs(@Param("record") TicketContratosBean record, @Param("example") TicketContratosExample example);

    int updateByExample(@Param("record") TicketContratosBean record, @Param("example") TicketContratosExample example);

    int updateByPrimaryKeySelective(TicketContratosBean record);

    int updateByPrimaryKeyWithBLOBs(TicketContratosBean record);

    int updateByPrimaryKey(TicketContratosBean record);
}