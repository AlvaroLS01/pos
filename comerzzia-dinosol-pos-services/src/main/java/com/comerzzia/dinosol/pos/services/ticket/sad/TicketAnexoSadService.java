package com.comerzzia.dinosol.pos.services.ticket.sad;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadBean;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Component
@SuppressWarnings("deprecation")
public class TicketAnexoSadService {

	protected static final Logger log = Logger.getLogger(TicketAnexoSadService.class);

	@Autowired
	private TicketAnexoSadMapper mapper;
	
	@Autowired
	private Sesion sesion;

    public TicketAnexoSadBean consultar(String uidTicket) throws TicketAnexoSadServiceException {
		log.debug("consultar() - Consultando ticket anexo con UID: " + uidTicket);
		
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			
			TicketAnexoSadBean key = new TicketAnexoSadBean();
			key.setUidActividad(sesion.getAplicacion().getUidActividad());
			key.setUidTicket(uidTicket);
			
			return mapper.selectByPrimaryKey(key);
		}
		catch (Exception e) {
			log.error("consultar() - Ha habido un error al consultar el ticket anexo para el ticket " + uidTicket + ": " + e.getMessage(), e);
			throw new TicketAnexoSadServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void crear(SqlSession sqlSession, TicketAnexoSadBean ticketAnexo) throws TicketAnexoSadServiceException {
		log.debug("crear() - Creando ticket anexo con UID: " + ticketAnexo.getUidTicket());
        try {
            mapper.insert(ticketAnexo);
        }
        catch (Exception e) {
            log.error("crear() - Se ha producido un error creando el ticket anexo para el ticket " + ticketAnexo.getUidTicket() + ": " + e.getMessage(), e);
            throw new TicketAnexoSadServiceException("Se ha producido un error creando el ticket anexo para el ticket " + ticketAnexo.getUidTicket(), e);
        }
	}

}
