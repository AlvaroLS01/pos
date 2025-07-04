package com.comerzzia.cardoso.pos.services.balanza;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.balanza.TicketBalanzaBean;
import com.comerzzia.cardoso.pos.persistence.balanza.TicketBalanzaExample;
import com.comerzzia.cardoso.pos.persistence.balanza.TicketBalanzaMapper;
import com.comerzzia.cardoso.pos.services.balanza.exception.TicketBalanzaServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

/**
 * GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA
 * TABLA : X_TICKETS_BALANZA_TBL
 */
@Service
public class TicketBalanzaService{

	private static Logger log = Logger.getLogger(TicketBalanzaService.class);

	@Autowired
	private Sesion sesion;

	@Autowired
	private TicketBalanzaMapper ticketBalanzaMapper;

	/**
	 * Realiza una consulta a partir del código del ticket de balanza en la tabla "X_TICKETS_BALANZA_TBL".
	 * @param codigoTicketBalanza
	 * @return
	 * @throws TicketBalanzaServiceException
	 */
	public TicketBalanzaBean consultarTicketBalanza(String codigoTicketBalanza) throws TicketBalanzaServiceException{
		log.debug("consultarTicketBalanza() : GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA");
		
		try{
			log.debug("consultarTicketBalanza() - Consultando ticket balanza código:" + codigoTicketBalanza);

			TicketBalanzaExample example = new TicketBalanzaExample();
			example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
						.andNumTicketBalanzaEqualTo(codigoTicketBalanza);
			List<TicketBalanzaBean> ticketsBalanza = ticketBalanzaMapper.selectByExampleWithBLOBs(example);

			if(ticketsBalanza != null && !ticketsBalanza.isEmpty()){
				if(ticketsBalanza.size() > 1){
					throw new Exception(I18N.getTexto("Se han encontrado varias apariciones del ticket de balanza indicado en la base de datos."));
				}
				if(StringUtils.isNotBlank(ticketsBalanza.get(0).getUidTicket())){
					throw new Exception(I18N.getTexto("El ticket de balanza indicado ya ha sido procesado."));
				}
				return ticketsBalanza.get(0);
			}
			else{
				throw new Exception(I18N.getTexto("El ticket de balanza indicado no se ha encontrado en la base de datos."));
			}
		}
		catch(Exception e){
			throw new TicketBalanzaServiceException(e);
		}
	}
	
	/**
	 * Para que no se pueda volver a utilizar se marca el ticket de balanza como utilizado.
	 * @param uidTicketBalanza
	 * @param uidTicket
	 */
	public void marcarTicketBalanzaUtilizado(String uidTicketBalanza, String uidTicket){
		log.debug("marcarTicketBalanzaUtilizado() : GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA");
		
		try{
			log.debug("marcarTicketBalanzaProcesado() - uidTicketBalanza:" + uidTicketBalanza + " uidTicket:" + uidTicket);

			TicketBalanzaBean ticketbalanza;
			TicketBalanzaExample example = new TicketBalanzaExample();
			example.or().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad())
						.andUidTicketBalanzaEqualTo(uidTicketBalanza);
			List<TicketBalanzaBean> ticketsBalanza = ticketBalanzaMapper.selectByExampleWithBLOBs(example);

			if(ticketsBalanza != null && !ticketsBalanza.isEmpty()){
				if(ticketsBalanza.size() > 1){
					log.error("marcarTicketBalanzaProcesado() - Se han encontrado varias apariciones del ticket de balanza indicado en la base de datos.");
				}

				if(StringUtils.isNotBlank(ticketsBalanza.get(0).getUidTicket())){
					log.error("marcarTicketBalanzaProcesado() - El ticket de balanza indicado ya ha sido procesado.");
				}

				ticketbalanza = ticketsBalanza.get(0);

				/* Actualizamos la tabla de tickets balanza con el uid_ticket */
				ticketbalanza.setUidTicket(uidTicket);
				ticketBalanzaMapper.updateByPrimaryKey(ticketsBalanza.get(0));
			}
			else{
				log.error("marcarTicketBalanzaProcesado() - El ticket de balanza indicado no se ha encontrado en la base de datos.");
			}
		}
		catch(Exception e){
			log.error("marcarTicketBalanzaProcesado() - Error no controlado al actualizar la tabla de tickets balanza. Error:" + e.getMessage());
		}
	}

}
