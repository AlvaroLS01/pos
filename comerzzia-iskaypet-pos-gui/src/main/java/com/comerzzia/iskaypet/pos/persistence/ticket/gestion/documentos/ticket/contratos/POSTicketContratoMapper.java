package com.comerzzia.iskaypet.pos.persistence.ticket.gestion.documentos.ticket.contratos;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface POSTicketContratoMapper {
    long countByExample(POSTicketContratoExample example);

    int deleteByExample(POSTicketContratoExample example);

    int insert(POSTicketContrato record);

    int insertSelective(POSTicketContrato record);

    List<POSTicketContrato> selectByExampleWithRowbounds(POSTicketContratoExample example, RowBounds rowBounds);

    List<POSTicketContrato> selectByExample(POSTicketContratoExample example);

    int updateByExampleSelective(@Param("record") POSTicketContrato record, @Param("example") POSTicketContratoExample example);

    int updateByExample(@Param("record") POSTicketContrato record, @Param("example") POSTicketContratoExample example);
}