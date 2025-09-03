package com.comerzzia.iskaypet.pos.services.ticket.cabecera;

import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.ProformaXmlBean;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.TicketAparcadoXmlBean;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.iskaypet.pos.persistence.ticket.pagos.sipay.InfoSipayTransaction;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * GAP 12  - ISK-8   GESTIÓN DE LOTES GAPXX
 * GAP 107 - ISK-262 GLOVO (venta por plataforma digital)
 */
@Component
@Primary
@Scope("prototype")
@SuppressWarnings("rawtypes")
public class IskaypetCabeceraTicket extends CabeceraTicket {

	// Constantes
	public static final String VARIABLE_POS_DIVISA = "X_POS.DIVISA";
	public static final String VARIABLE_POS_DIVISA_DEFAULT = "EUR";

	// Inyecciones de dependencias de los servicios
	@Autowired
	@XmlTransient
	protected VariablesServices variablesServices;

	// Atributos de la clase
	@XmlElement(name = "divisa")
	protected String divisa;

	@XmlElement(name = "email_envio_ticket")
	protected String emailEnvioTicket;

	// ISK-182 GAP-63 - DEVOLUCIONES SIN DOCUMENTO ORIGEN
	@XmlElement(name = "datos_origen_aux")
	private DatosOrigenTicketBean datosOrigenFalsos;

	@XmlElement(name = "documento_cliente")
	protected String documentoCliente;
	
	// ISK-262 GAP-107 GLOVO (venta por plataforma digital)
	@XmlElement(name = "delivery")
	protected String delivery;

	// CZZ-115 - APARCAR TICKET
	@XmlElement(name = "ticket_aparcado")
	private TicketAparcadoXmlBean ticketAparcado;

	@XmlElement(name = "infoTransactionSipay")
	protected InfoSipayTransaction transactionsSipay;

	// CZZ-1498 - TOGO PROFORMAS
	@XmlElement(name = "proforma")
	private ProformaXmlBean proforma;

	@XmlElement(name = "total_articulos_impresos")
	protected BigDecimal totalArticulosImpresos;

	@Override
	public void inicializarCabecera(ITicket ticket) {
		super.inicializarCabecera(ticket);
		setDivisa();
	}

	public String getDivisa() {
		return divisa;
	}

	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}

	public void setDivisa() {
		String divisa = variablesServices.getVariableAsString(VARIABLE_POS_DIVISA);
		this.divisa = !divisa.isEmpty() ? divisa : VARIABLE_POS_DIVISA_DEFAULT;
	}

	public String getEmailEnvioTicket() {
		return emailEnvioTicket;
	}

	public void setEmailEnvioTicket(String emailEnvioTicket) {
		this.emailEnvioTicket = emailEnvioTicket;
	}

	public DatosOrigenTicketBean getDatosOrigenFalsos() {
		return datosOrigenFalsos;
	}

	public void setDatosOrigenFalsos(DatosOrigenTicketBean datosOrigenFalsos) {
		this.datosOrigenFalsos = datosOrigenFalsos;
	}

	public String getDocumentoCliente() {
		return documentoCliente;
	}

	public void setDocumentoCliente(String documentoCliente) {
		this.documentoCliente = documentoCliente;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public TicketAparcadoXmlBean getTicketAparcado() {
		return ticketAparcado;
	}

	public void setTicketAparcado(TicketAparcadoXmlBean ticketAparcado) {
		this.ticketAparcado = ticketAparcado;
	}

	public ProformaXmlBean getProforma() {
		return proforma;
	}

	public void setProforma(ProformaXmlBean proforma) {
		this.proforma = proforma;
	}

	public BigDecimal getTotalArticulosImpresos() {
		return totalArticulosImpresos != null ? totalArticulosImpresos.setScale(0, RoundingMode.HALF_UP) : getCantidadArticulos();
	}

	public void setTotalArticulosImpresos(BigDecimal totalArticulosImpresos) {
		this.totalArticulosImpresos = totalArticulosImpresos;
	}

	@Override
	public void setDatosFidelizado(FidelizacionBean tarjeta) {
		// Si se cambia los datos del fidelizado previamente identificado, volver a generar contrato
		if (datosFidelizado != null && !datosFidelizado.getNumTarjetaFidelizado().equals(tarjeta.getNumTarjetaFidelizado())) {
			for(Object obj : ticket.getLineas()) {
				if (obj instanceof IskaypetLineaTicket) {
					IskaypetLineaTicket linea = (IskaypetLineaTicket) obj;
					if (linea.getContratoAnimal() != null) {
						linea.setContratoAnimal(null);
					}
				}
			}
		}
		// Mantenemos el estandar
		super.setDatosFidelizado(tarjeta);
	}


	public InfoSipayTransaction getTransactionsSipay() {
		return transactionsSipay;
	}

	public void setTransactionsSipay(InfoSipayTransaction transactionsSipay) {
		this.transactionsSipay = transactionsSipay;
	}

}
