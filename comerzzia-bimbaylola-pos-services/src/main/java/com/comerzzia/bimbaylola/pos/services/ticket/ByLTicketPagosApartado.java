package com.comerzzia.bimbaylola.pos.services.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.pos.services.ticket.TicketPagosApartado;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;

@Component
@Scope("prototype")
@Primary
public class ByLTicketPagosApartado extends TicketPagosApartado{

	@XmlTransient
	private static final String TIPO_AXIS = "TIPO_AXIS";
	@XmlTransient
	private static final String TIPO_CONEXFLOW = "TIPO_CONEXFLOW";
	@XmlTransient
	private static final String MERCHANT_TICKET = "MERCHANT_TICKET";
	@XmlTransient
	private static final String CUSTOMER_TICKET = "CUSTOMER_TICKET";
	
	public ByLTicketPagosApartado(){
		super();
		cabecera = SpringContext.getBean(ByLCabeceraTicket.class);
		pagos = new ArrayList<>();
	}

	/**
	 * Comprueba en los datos de respuesta del pago de la tarjeta, si están incluidos en los
	 * adicionales el tipo de pasarela Conexflow, en caso de estar significa que se ha pagado con el 
	 * con lo cual en las plantillas deberá diferenciar.
	 * @param pago : Objeto que contiene los datos del pago.
	 * @return Boolean
	 */
	public Boolean esPagoConexflow(PagoTicket pago){
		if(pago.getDatosRespuestaPagoTarjeta() != null){
			if(pago.getDatosRespuestaPagoTarjeta().getAdicionales() != null &&
					!pago.getDatosRespuestaPagoTarjeta().getAdicionales().isEmpty()){
				if(pago.getDatosRespuestaPagoTarjeta().getAdicionales().containsKey(TIPO_CONEXFLOW)){
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Comprueba en los datos de respuesta del pago de la tarjeta, si están incluidos en los
	 * adicionales el tipo de pasarela Axis, en caso de estar significa que se ha pagado con el 
	 * con lo cual en las plantillas deberá diferenciar.
	 * @param pago : Objeto que contiene los datos del pago.
	 * @return Boolean
	 */
	public Boolean esPagoAxis(PagoTicket pago){
		if(pago.getDatosRespuestaPagoTarjeta() != null){
			if(pago.getDatosRespuestaPagoTarjeta().getAdicionales() != null &&
					!pago.getDatosRespuestaPagoTarjeta().getAdicionales().isEmpty()){
				if(pago.getDatosRespuestaPagoTarjeta().getAdicionales().containsKey(TIPO_AXIS)){
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Rescata de los datos de respuesta de tarjeta de un pago el CustomerTicket que
	 * devuelve un pago con pasarela Axis.
	 * @param pago : Objeto que contiene los datos del pago.
	 * @return List<String>
	 */
	public List<String> getCustomerTicket(PagoTicket pago){
		List<String> listadoCustomerTicket = new ArrayList<String>();
		if(pago.getDatosRespuestaPagoTarjeta() != null){
			if(pago.getDatosRespuestaPagoTarjeta().getAdicionales() != null &&
					!pago.getDatosRespuestaPagoTarjeta().getAdicionales().isEmpty()){
				for(Map.Entry<String, String> entry : pago.getDatosRespuestaPagoTarjeta().getAdicionales().entrySet()){
					if(CUSTOMER_TICKET.equals(entry.getKey())){
						/* Cortamos el listado por el salto de linea */
						String[] ticketRespuestaTarjeta = entry.getValue().split("\n");
						for(int i = 0 ; i < ticketRespuestaTarjeta.length ; i++){
							if(ticketRespuestaTarjeta[i].length() < 40 && 
									ticketRespuestaTarjeta[i].length() > 1){
								listadoCustomerTicket.add(ticketRespuestaTarjeta[i].trim());
							}
						}
					}
				}
			}
		}
		if(listadoCustomerTicket.isEmpty()){
			return null;
		}else{
			return listadoCustomerTicket;
		}
	}
	
	/**
	 * Rescata de los datos de respuesta de tarjeta de un pago el CustomerTicket que
	 * devuelve un pago con pasarela Conexflow.
	 * @param pago : Objeto que contiene los datos del pago.
	 * @return List<String>
	 */
	public List<String> getMerchantTicket(PagoTicket pago){
		List<String> listadoMerchantTicket = new ArrayList<String>();
		if(pago.getDatosRespuestaPagoTarjeta() != null){
			if(pago.getDatosRespuestaPagoTarjeta().getAdicionales() != null &&
					!pago.getDatosRespuestaPagoTarjeta().getAdicionales().isEmpty()){
				for(Map.Entry<String, String> entry : pago.getDatosRespuestaPagoTarjeta().getAdicionales().entrySet()){
					if(MERCHANT_TICKET.equals(entry.getKey())){
						/* Cortamos el listado por el salto de linea */
						String[] ticketRespuestaTarjeta = entry.getValue().split("\n");
						for(int i = 0 ; i < ticketRespuestaTarjeta.length ; i++){
							if(ticketRespuestaTarjeta[i].length() < 40 && 
									ticketRespuestaTarjeta[i].length() > 1){
								listadoMerchantTicket.add(ticketRespuestaTarjeta[i].trim());
							}
						}
					}
				}
			}
		}
		if(listadoMerchantTicket.isEmpty()){
			return null;
		}else{
			return listadoMerchantTicket;
		}
	}
	
}
