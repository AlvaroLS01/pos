package com.comerzzia.cardoso.pos.services.ticket.cabecera;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.cardoso.pos.gui.promociones.monograficas.bean.PromocionMonograficaCabeceraTicket;
import com.comerzzia.cardoso.pos.services.agente.ClienteAgenteService;
import com.comerzzia.cardoso.pos.services.agente.exception.ClienteAgenteException;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.DatosValesPromocionalesBean;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales.PromocionEmpleadosCabeceraTicket;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.util.format.FormatUtil;

@Component
@Primary
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class CARDOSOCabeceraTicket extends CabeceraTicket{

	// GAP - PERSONALIZACIONES V3 - SERIE ALBARÁN
	@XmlElement(name = "serie_albaran")
	protected String serieAlbaran;

	// GAP - PERSONALIZACIONES V3 - ARTÍCULOS PELIGROSOS
	@XmlElement(name = "numArticulosPeligrosos")
	private Integer numArticulosPeligrosos;

	// GAP - PERSONALIZACIONES V3 - VALE PROMOCIONAL
	@XmlElement(name = "vale_promocion")
	private DatosValesPromocionalesBean datosValesPromocionales;

	// GAP - AGENTES
	@Autowired
	@XmlTransient
	private ClienteAgenteService agenteService;
	@XmlElement(name = "codAgente")
	private String codAgente;
	
	//TAXFREE
	@XmlElement(name = "taxfree_barcode")
	private String idTaxfree;
	
	@XmlElement(name = "pasaporte")
	private String pasaporte;
	
	public void setCliente(ClienteBean cliente){
		super.setCliente(cliente);
		String codigoAgente = "";
		try{
			codigoAgente = agenteService.getClienteAgente(cliente.getCodCliente());
		}
		catch(ClienteAgenteException e){
			codigoAgente = "";
		}
		setCodAgente(codigoAgente);
	}
	
	// GAP XX - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS
	
	@XmlElement(name = "total_recargo")
	protected BigDecimal totalRecargo;
	@XmlElement(name = "base_sin_descuento")
	protected BigDecimal baseSinDescuento;
	@XmlElement(name = "total_sin_descuento")
	protected BigDecimal totalSinDescuento;
	
	/* EMPLEADOS */
	@XmlElement(name = "promocion_empleado")
	protected PromocionEmpleadosCabeceraTicket datosDescuentoPromocionEmpleados;
	
	/* MONOGRÁFICAS */
	@XmlElement(name = "descuento_monografica")
	protected PromocionMonograficaCabeceraTicket datosDescuentoPromocionMonografica;

	// GAP XX - REALIZAR DEVOLUCIONES SIN DOCUMENTO ORIGEN 
	@XmlElement(name = "datos_origen_aux")
	private DatosOrigenTicketBean datosOrigenFalsos;
	
	//TODO
	public String getDescuentoCabeceraAsString(){
		if(datosDescuentoPromocionEmpleados != null){
			return FormatUtil.getInstance().formateaNumero(datosDescuentoPromocionEmpleados.getDescuento(), 2);
		}
		return FormatUtil.getInstance().formateaNumero(BigDecimal.ZERO, 2);
	}

	public PromocionEmpleadosCabeceraTicket getDatosDescuentoPromocionEmpleados(){
		return datosDescuentoPromocionEmpleados;
	}

	public void setDatosDescuentoPromocionEmpleados(PromocionEmpleadosCabeceraTicket datosDescuentoPromocionEmpleados){
		this.datosDescuentoPromocionEmpleados = datosDescuentoPromocionEmpleados;
	}

	public PromocionMonograficaCabeceraTicket getDatosDescuentoPromocionMonografica(){
		return datosDescuentoPromocionMonografica;
	}

	public void setDatosDescuentoPromocionMonografica(PromocionMonograficaCabeceraTicket datosDescuentoPromocionMonografica){
		this.datosDescuentoPromocionMonografica = datosDescuentoPromocionMonografica;
	}

	public CARDOSOCabeceraTicket(){
		super();
		if(datosValesPromocionales == null){
			datosValesPromocionales = new DatosValesPromocionalesBean();
			if(datosValesPromocionales.getFechaInicio() == null){
				datosValesPromocionales = null;
			}
		}
	}

	public String getSerieAlbaran(){
		return serieAlbaran;
	}

	public void setSerieAlbaran(String serieAlbaran){
		this.serieAlbaran = serieAlbaran;
	}

	public BigDecimal getBaseSinDescuento(){
		return baseSinDescuento;
	}

	public void setBaseSinDescuento(BigDecimal baseSinDescuento){
		this.baseSinDescuento = baseSinDescuento;
	}

	public BigDecimal getTotalSinDescuento(){
		return totalSinDescuento;
	}

	public void setTotalSinDescuento(BigDecimal totalSinDescuento){
		this.totalSinDescuento = totalSinDescuento;
	}

	public BigDecimal getTotalRecargo(){
		return totalRecargo;
	}

	public void setTotalRecargo(BigDecimal totalRecargo){
		this.totalRecargo = totalRecargo;
	}

	public Integer getNumArticulosPeligrosos(){
		return numArticulosPeligrosos;
	}

	public void setNumArticulosPeligrosos(Integer numArticulosPeligrosos){
		this.numArticulosPeligrosos = numArticulosPeligrosos;
	}

	public String getCodAgente(){
		return codAgente;
	}

	public void setCodAgente(String codAgente){
		this.codAgente = codAgente;
	}

	public DatosValesPromocionalesBean getDatosValesPromocionales(){
		return datosValesPromocionales;
	}

	public void setDatosValesPromocionales(DatosValesPromocionalesBean datosValesPromocionales){
		this.datosValesPromocionales = datosValesPromocionales;
	}

	public DatosOrigenTicketBean getDatosOrigenFalsos(){
		return datosOrigenFalsos;
	}

	public void setDatosOrigenFalsos(DatosOrigenTicketBean datosOrigenFalsos){
		this.datosOrigenFalsos = datosOrigenFalsos;
	}

	public String getIdTaxfree() {
		return idTaxfree;
	}

	public void setIdTaxfree(String idTaxfree) {
		this.idTaxfree = idTaxfree;
	}

	public String getPasaporte() {
		return pasaporte;
	}

	public void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte;
	}

}