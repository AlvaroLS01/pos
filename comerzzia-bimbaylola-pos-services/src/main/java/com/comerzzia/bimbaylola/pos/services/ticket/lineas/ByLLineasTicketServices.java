package com.comerzzia.bimbaylola.pos.services.ticket.lineas;

import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ByLLineasTicketServices extends LineasTicketServices {

	protected static final Logger log = Logger.getLogger(ByLLineasTicketServices.class.getName());


}
