package com.comerzzia.dinosol.pos.services.ticket;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.api.virtualmoney.client.model.PinStatusResponse;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.dinosol.pos.services.sorteos.SorteosService;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.dinosol.pos.services.ticket.lineas.TarjetaRegaloDto;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;

@SuppressWarnings("deprecation")
@Component
@Primary
public class DinoTicketsService extends TicketsService {
	
	@Autowired
	private Sesion sesion;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	@Autowired
	private SorteosService sorteosService;

	private VirtualMoneyManager virtualMoneyManager;
	
    public TicketBean consultarUltimoTicket() throws TicketsServiceException {
		SqlSession sqlSession = new SqlSession();
	    try {
	    	log.debug("consultarUltimoTicket() - Consultando el último ticket en base de datos...");
	        sqlSession.openSession(SessionFactory.openSession());
			return ticketMapper.selectLastTicketInStoreMySQL(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getCodAlmacen(), sesion.getAplicacion().getCodCaja());
	    }
	    catch (Exception e) {
            String msg = "Se ha producido un error consultando el último ticket en base de datos " + e.getMessage();
            log.error("consultarUltimoTicket() - " + msg, e);
            throw new TicketsServiceException(e);
        }
        finally {
            sqlSession.close();
        }
	}
    
    @SuppressWarnings({ "rawtypes" })
	@Override
    public synchronized void registrarTicket(Ticket ticket, TipoDocumentoBean tipoDocumento, boolean procesarTicket) throws TicketsServiceException {
    	activarTarjetasRegalo(ticket);
    	
    	if(ticket instanceof TicketVentaAbono) {
    		((DinoCabeceraTicket) ticket.getCabecera()).setCuponesLeidos(null);
    		
    		try {
				sorteosService.solicitarParticipacionesSorteo(ticket, false);
			}
			catch (Exception e) {
				log.error("registrarTicket() - Ha habido un error al solicitar participaciones para el ticket en curso. No se podrán añadir: " + e.getMessage(), e);
			}
    		
    		guardarAuditoriasNoRegistradas(ticket);
    	}
    	
    	super.registrarTicket(ticket, tipoDocumento, procesarTicket);
    }

	@SuppressWarnings("rawtypes")
	private void guardarAuditoriasNoRegistradas(Ticket ticket) {
		List<AuditoriaDto> auditoriasNoRegistradas = ((DinoTicketVentaAbono) ticket).getAuditoriasNoRegistradas();
		if(auditoriasNoRegistradas != null && !auditoriasNoRegistradas.isEmpty()) {
			for(AuditoriaDto auditoria : auditoriasNoRegistradas) {
				auditoria.setUidTicket(ticket.getUidTicket());
				auditoriasService.guardarAuditoria(auditoria);
			}
		}
		((DinoTicketVentaAbono) ticket).clearAuditoriasNoRegistradas();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void activarTarjetasRegalo(Ticket ticket) {
		List<DinoLineaTicket> lineas = ticket.getLineas();
    	for(DinoLineaTicket linea : lineas) {
    		TarjetaRegaloDto tarjetaRegaloDto = linea.getTarjetaRegalo();
    		if(tarjetaRegaloDto != null && !TarjetaRegaloDto.ESTADO_ANULADA.equals(tarjetaRegaloDto.getEstado())) {
    			String numeroTarjeta = tarjetaRegaloDto.getNumeroTarjeta();
				log.debug("activarTarjetasRegalo() - Activando tarjeta " + numeroTarjeta);
    			inicializarVirtualMoneyManager(ticket);
    			try {
    				log.debug("activarTarjetasRegalo() - Activando tarjeta con número " + numeroTarjeta);
    				virtualMoneyManager.activate(tarjetaRegaloDto);
    				tarjetaRegaloDto.setEstado(TarjetaRegaloDto.ESTADO_ACTIVA);
    				
    				solicitarPinTarjeta(tarjetaRegaloDto, numeroTarjeta);
    			}
    			catch (Exception e) {
    				log.error("activarTarjetasRegalo() - Ha habido un error al activar la tarjeta: " + e.getMessage(), e);
    				tarjetaRegaloDto.setEstado(TarjetaRegaloDto.ESTADO_PENDIENTE_ACTIVAR);
    			}
    		}
    	}
	}

	protected void solicitarPinTarjeta(TarjetaRegaloDto tarjetaRegaloDto, String numeroTarjeta) {
		try {
			log.debug("activarTarjetasRegalo() - Solicitando PIN para tarjeta " + numeroTarjeta);
			PinStatusResponse pinTarjeta = virtualMoneyManager.solicitarPinTarjetaRegalo(tarjetaRegaloDto);
			String pin = pinTarjeta.getPin();
			tarjetaRegaloDto.setPin(pin);
		}
		catch (Exception e) {
			log.error("activarTarjetasRegalo() - Ha habido un error al solicitar el PIN: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	private void inicializarVirtualMoneyManager(Ticket ticket) {
		if(virtualMoneyManager == null) {
			PaymentMethodManager manager = null;
			/* Recorremos los medios de pago con configuración */
			for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
				/* Sacamos la configuración del medio de pago, comprobando el manager */
				if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getManager() != null && configuration.getManager() instanceof VirtualMoneyManager) {
					manager = configuration.getManager();
					virtualMoneyManager = (VirtualMoneyManager) manager;
					break;
				}
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (virtualMoneyManager != null) {
			try {
				virtualMoneyManager.initialize();
				virtualMoneyManager.setTicketData(ticket, null);
			}
			catch (Exception e) {
				log.error("inicializarVirtualMoneyManager() - Error al el manager de Virtual Money: " + e.getMessage(), e);
			}
		}
		else {
			log.error("inicializarVirtualMoneyManager() - No se ha encontrado configuración para Virtual Money");
		}
	}

}
