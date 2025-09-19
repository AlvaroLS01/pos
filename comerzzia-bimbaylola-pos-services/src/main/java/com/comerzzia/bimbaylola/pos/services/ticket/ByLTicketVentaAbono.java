package com.comerzzia.bimbaylola.pos.services.ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.ByL.backoffice.persistencia.fidelizacion.firma.FidelizadoFirmaBean;
import com.comerzzia.ByL.backoffice.persistencia.taxfree.TaxFreeVoucherBean;
import com.comerzzia.bimbaylola.pos.services.core.sesion.ByLSesionImpuestos;
import com.comerzzia.pos.persistence.core.impuestos.porcentajes.PorcentajeImpuestoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.ComparadorSubtotalesIvaTicketPorcentaje;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.config.SpringContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "ticket")
@Component
@Primary
@Scope("prototype")
public class ByLTicketVentaAbono extends TicketVentaAbono{
		
	public static final String COD_PAIS_SINGAPUR = "SG";
	public static final String COD_PAIS_ECUADOR = "EC";
	
	@XmlTransient
	private static final String TIPO_AXIS = "TIPO_AXIS";
	@XmlTransient
	private static final String TIPO_CONEXFLOW = "TIPO_CONEXFLOW";
	
	@XmlTransient
	private static final String MERCHANT_TICKET = "MERCHANT_TICKET";
	@XmlTransient
	private static final String CUSTOMER_TICKET = "CUSTOMER_TICKET";
	
	@XmlTransient
	private FidelizadoFirmaBean firmaFidelizado;
	
	@XmlTransient
	private TaxFreeVoucherBean taxFree ;
	
	@Autowired
	private ByLSesionImpuestos sesionImpuestos;
	
	public FidelizadoFirmaBean getFirmaFidelizado(){
		return firmaFidelizado;
	}

	public void setFirmaFidelizado(FidelizadoFirmaBean firmaFidelizado){
		this.firmaFidelizado = firmaFidelizado;
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

	public TaxFreeVoucherBean getTaxFree() {
		return taxFree;
	}

	public void setTaxFree(TaxFreeVoucherBean taxFree) {
		this.taxFree = taxFree;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recalcularSubtotalesIva() {
		// Construimos mapa con subtotales recorriendo todas las líneas del ticket
		Map<String, SubtotalIvaTicket> subtotales = new HashMap<>();
		Sesion sesion = SpringContext.getBean(Sesion.class);
		if (sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)) {
			try {
				sesionImpuestos.init(getCliente().getIdGrupoImpuestos());
			}
			catch (SesionInitException e) {
				e.printStackTrace();
			}
		}
		for (LineaTicket linea : lineas) {
			linea.recalcularImporteFinal();
			String codImpuesto = linea.getArticulo().getCodImpuesto();
			SubtotalIvaTicket subtotal = subtotales.get(codImpuesto);
			if (subtotal == null) {
				subtotal = crearSubTotalIvaTicket();
				PorcentajeImpuestoBean porcentajeImpuesto = sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_SINGAPUR) || sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(COD_PAIS_ECUADOR)
				        ? sesionImpuestos.getPorcentaje(getCliente().getIdTratImpuestos(), codImpuesto)
				        : sesion.getImpuestos().getPorcentaje(getCliente().getIdTratImpuestos(), codImpuesto);
				subtotal.setPorcentajeImpuestoBean(porcentajeImpuesto);
				subtotales.put(codImpuesto, subtotal);
			}
			subtotal.addLinea(linea);
		}

		// Añadimos cada subtotal a la lista de la cabecera del ticket
		getCabecera().getSubtotalesIva().clear();
		for (SubtotalIvaTicket subtotal : subtotales.values()) {
			// Recalculamos cada subtotal (impuestos, cuotas y totales)
			subtotal.recalcular();
			getCabecera().getSubtotalesIva().add(subtotal);
		}

		ComparadorSubtotalesIvaTicketPorcentaje comparador = new ComparadorSubtotalesIvaTicketPorcentaje();
		Collections.sort(getCabecera().getSubtotalesIva(), comparador);
	}
}
