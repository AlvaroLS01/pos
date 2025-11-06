package com.comerzzia.dinosol.pos.services.ticket.anexo;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoBean;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Component
public class TicketsAnexosService {

	protected static final Logger log = Logger.getLogger(TicketsAnexosService.class);

	@Autowired
	private TicketAnexoMapper mapper;
	
	@Autowired
	private Sesion sesion;

	public TicketAnexoBean consultar(String uidTicket) throws AnexaTicketsServiceException {
		log.debug("consultar() - Consultando ticket anexo con UID: " + uidTicket);
		
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			
			TicketAnexoBean key = new TicketAnexoBean();
			key.setUidActividad(sesion.getAplicacion().getUidActividad());
			key.setUidTicket(uidTicket);
			
			return mapper.selectByPrimaryKey(key);
		}
		catch (Exception e) {
			log.error("consultar() - Ha habido un error al consultar el ticket anexo para el ticket " + uidTicket + ": " + e.getMessage(), e);
			throw new AnexaTicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void crear(SqlSession sqlSession, TicketAnexoBean ticketAnexo) throws AnexaTicketsServiceException {
		log.debug("crear() - Creando ticket anexo con UID: " + ticketAnexo.getUidTicket());
        try {
            mapper.insert(ticketAnexo);
        }
        catch (Exception e) {
            log.error("crear() - Se ha producido un error creando el ticket anexo para el ticket " + ticketAnexo.getUidTicket() + ": " + e.getMessage(), e);
            throw new AnexaTicketsServiceException("Se ha producido un error creando el ticket anexo para el ticket " + ticketAnexo.getUidTicket(), e);
        }
	}

}
