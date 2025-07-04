package com.comerzzia.cardoso.pos.services.ticket.lineas;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.format.FormatUtil;

@SuppressWarnings("serial")
@Component
@Primary
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class CARDOSOLineaTicketProfesional extends LineaTicket{

	/* GAP - PERSONALIZACIONES V3 - LOTES */
	@XmlElementWrapper(name = "lotes")
	@XmlElement(name = "lote")
	private List<CardosoLote> lotes;

	/* GAP - CAJERO AUXILIAR */
	@XmlElement(name = "cajero")
	protected UsuarioBean cajeroAux;
	@XmlElement(name = "datos_devolucion")
	protected CardosoDatosDevolucionBean datosDevolucion;

	/* GAP - PERSONALIZACIONES V3 - INTEGRACIÓN BALANZA BIZERBA */
	@XmlElement(name = "uid_ticket_balanza")
	protected String uidTicketBalanza;

	/* GAP - DESCUENTO TARIFA */
	@XmlElement(name = "descuentoTarifa")
	protected BigDecimal descuentoTarifa;
	
	public List<CardosoLote> getLotes(){
		return lotes;
	}

	public void setLotes(List<CardosoLote> lotes){
		this.lotes = lotes;
	}
	
	public UsuarioBean getCajeroAux(){
		return cajeroAux;
	}

	public void setCajeroAux(UsuarioBean cajeroAux){
		this.cajeroAux = cajeroAux;
	}

	public CardosoDatosDevolucionBean getDatosDevolucion(){
		return datosDevolucion;
	}

	public void setDatosDevolucion(CardosoDatosDevolucionBean datosDevolucion){
		this.datosDevolucion = datosDevolucion;
	}

	public BigDecimal getDescuentoTarifa(){
		return descuentoTarifa;
	}

	public void setDescuentoTarifa(BigDecimal descuentoTarifa){
		this.descuentoTarifa = descuentoTarifa;
	}

	public String getDescuentoTarifaAsString(){
		return FormatUtil.getInstance().formateaImporte(descuentoTarifa);
	}

	public String getUidTicketBalanza(){
		return uidTicketBalanza;
	}

	public void setUidTicketBalanza(String uidTicketBalanza){
		this.uidTicketBalanza = uidTicketBalanza;
	}

}
