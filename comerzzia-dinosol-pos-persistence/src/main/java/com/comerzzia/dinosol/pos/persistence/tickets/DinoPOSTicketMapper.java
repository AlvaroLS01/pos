package com.comerzzia.dinosol.pos.persistence.tickets;

import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;

import com.comerzzia.pos.persistence.tickets.POSTicketMapper;
import com.comerzzia.pos.persistence.tickets.TicketBean;

@Primary
public interface DinoPOSTicketMapper extends POSTicketMapper {
	
	TicketBean selectLastTicketInStoreMySQL(@Param("uidActividad") String uidActividad, @Param("codAlmacen") String codAlmacen, @Param("codcaja") String codCaja);

}
