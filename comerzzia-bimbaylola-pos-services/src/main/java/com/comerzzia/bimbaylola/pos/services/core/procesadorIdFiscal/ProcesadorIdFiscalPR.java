package com.comerzzia.bimbaylola.pos.services.core.procesadorIdFiscal;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.ws.cliente.softek.TenderType;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxPosRequest;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxPosResponse;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerService;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerServiceLocator;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerServiceSoapBindingStub;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxType;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.profesional.SubtotalIvaTicketProfesional;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("rawtypes")
@Component
public class ProcesadorIdFiscalPR implements IProcesadorFiscal{

	protected static final Logger log = Logger.getLogger(ProcesadorIdFiscalPR.class);
	
	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private Sesion sesion;
	
	@Override
	public String obtenerIdFiscal(Ticket ticket) throws ProcesadorIdFiscalException {
		log.debug("obtenerIdFiscal() - Obteniendo número de identificación fiscal para PUERTO RICO ...");
		String identificadorFiscal = "";
		
		try{
			TxServerService locator = new TxServerServiceLocator();
			URL url = new URL(variablesServices.getVariableAsString(ByLVariablesServices.REST_URL_FISCAL_PUERTO_RICO));
			
			TxServerServiceSoapBindingStub stub = new TxServerServiceSoapBindingStub(url, locator);
			TxPosRequest request = new TxPosRequest();
			/* Información de la tienda */
			request.setMerchantId(variablesServices.getVariableAsString(ByLVariablesServices.MERCHANT_ID_PUERTO_RICO));
			request.setTerminalId(sesion.getSesionCaja().getCajaAbierta().getCodCaja());
			request.setTerminalPassword(sesion.getSesionCaja().getCajaAbierta().getCodAlm());
			/* Impuestos y totales */
			SubtotalIvaTicketProfesional subtotalIva = (SubtotalIvaTicketProfesional) ticket.getCabecera().getSubtotalesIva().get(0);
			request.setStateTax(subtotalIva.getCuota().abs());
			request.setMunicipalTax(subtotalIva.getCuotaRecargo().abs());
			request.setReducedStateTax(BigDecimal.ZERO); // Para Bimba siempre va a ser 0
			request.setSubTotal(subtotalIva.getBase().abs());
			request.setTotal(subtotalIva.getTotal().abs());
			/* Tipo de pago */
			if(ticket.getPagos() != null && !ticket.getPagos().isEmpty()) {
				request.setTenderType(((PagoTicket) ticket.getPagos().get(0)).isMedioPagoTarjeta() 
						? TenderType.CREDIT 
						: TenderType.CASH);			 	
			}
			else {
				request.setTenderType(TenderType.CASH);
			}
			/* Información sobre la transacción */
			Calendar calendar = Calendar.getInstance();
			if(ticket.getCabecera().getFecha() != null) {
				calendar.setTime(ticket.getCabecera().getFecha());
			}
			else {
				calendar.setTime(new Date());				
			}
			request.setTransactionDate(calendar);
			request.setTransactionType(ticket.isEsDevolucion() ? TxType.REFUND : TxType.SALE);
			
			log.debug("obtenerIdFiscal() - Realizando petición a SOFTEK");
			log.debug("obtenerIdFiscal() - MerchantID: " + request.getMerchantId());
			log.debug("obtenerIdFiscal() - TerminalID: " + request.getTerminalId());
			log.debug("obtenerIdFiscal() - TerminalPassword: " + request.getTerminalPassword());
			log.debug("obtenerIdFiscal() - StateTax: " + request.getStateTax());
			log.debug("obtenerIdFiscal() - MunicipalTax: " + request.getMunicipalTax());
			log.debug("obtenerIdFiscal() - ReducedStateTax: " + request.getReducedStateTax());
			log.debug("obtenerIdFiscal() - SubTotal: " + request.getSubTotal());
			log.debug("obtenerIdFiscal() - TenderType: " + request.getTenderType());
			log.debug("obtenerIdFiscal() - Total: " + request.getTotal());
			log.debug("obtenerIdFiscal() - TransactionDate: " + request.getTransactionDate());
			log.debug("obtenerIdFiscal() - TransactionType: " + request.getTransactionType());
			
			TxPosResponse response = stub.requestIVULoto(request);
			identificadorFiscal = response.getIvuLoto() != null 
					? response.getIvuLoto()
					: I18N.getTexto("Control number not available");
			
		} catch (Exception e){
			String msg = "Se ha producido un error procesando el identificador fiscal del ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("obtenerIdFiscal() - " + msg, e);
			throw new ProcesadorIdFiscalException(e.getMessage());
		}
		
		return identificadorFiscal;
	}

	@Override
	public byte[] procesarDocumentoFiscal(Ticket ticket) throws ProcesarDocumentoFiscalException {
		return null;
	}

}
