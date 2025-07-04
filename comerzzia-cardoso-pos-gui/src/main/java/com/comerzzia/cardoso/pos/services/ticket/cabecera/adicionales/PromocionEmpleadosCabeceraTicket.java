package com.comerzzia.cardoso.pos.services.ticket.cabecera.adicionales;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.util.format.FormatUtil;

/**
 * GAP V3 - PROMOCIONES ESPECIALES : PROMOCIONES MONOGRÁFICAS Y DE EMPLEADOS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "promocion")
public class PromocionEmpleadosCabeceraTicket{

	@XmlElement(name = "id_promocion")
	protected Long idPromocion;
	@XmlTransient
	protected Long tipoPromocion;
	@XmlElement(name = "tipo_dto")
	protected Long tipoDto;
	@XmlElement(name = "texto_promocion")
	protected String descripcion;
	@XmlElement(name = "uid_transaccion")
	protected String uidTransaccion;
	@XmlElement(name = "importe_ahorro")
	protected BigDecimal importeAhorro;
	@XmlElement(name = "importe_total_ahorro")
	protected BigDecimal importeTotalAhorro;
	@XmlElement(name = "porcentaje_descuento")
	protected BigDecimal descuento;

	@XmlTransient
	protected BigDecimal totalVenta;
	@XmlTransient
	protected String apiKeyPromocionEmpleados;
	@XmlTransient
	protected PromocionBean promocionEmpleado;

	public Long getIdPromocion(){
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion){
		this.idPromocion = idPromocion;
	}

	public Long getTipoPromocion(){
		return tipoPromocion;
	}

	public void setTipoPromocion(Long tipoPromocion){
		this.tipoPromocion = tipoPromocion;
	}

	public String getDescripcion(){
		return descripcion;
	}

	public void setDescripcion(String descripcion){
		this.descripcion = descripcion;
	}

	public Long getTipoDto(){
		return tipoDto;
	}

	public void setTipoDto(Long tipoDto){
		this.tipoDto = tipoDto;
	}

	public String getUidTransaccion(){
		return uidTransaccion;
	}

	public void setUidTransaccion(String uidTransaccion){
		this.uidTransaccion = uidTransaccion;
	}

	public BigDecimal getTotalVenta(){
		return totalVenta;
	}

	public void setTotalVenta(BigDecimal totalVenta){
		this.totalVenta = totalVenta;
	}

	public BigDecimal getImporteTotalAhorro(){
		return importeTotalAhorro;
	}

	public void setImporteTotalAhorro(BigDecimal importeTotalAhorro){
		this.importeTotalAhorro = importeTotalAhorro;
	}

	public String getImporteTotalAhorroAsString(){
		return FormatUtil.getInstance().formateaImporte(importeTotalAhorro);
	}

	public BigDecimal getImporteAhorro(){
		return importeAhorro;
	}

	public void setImporteAhorro(BigDecimal importeAhorro){
		this.importeAhorro = importeAhorro;
	}

	public String getImporteAhorroAsString(){
		return FormatUtil.getInstance().formateaImporte(importeAhorro);
	}

	public BigDecimal getDescuento(){
		return descuento;
	}

	public void setDescuento(BigDecimal descuento){
		this.descuento = descuento;
	}

	public PromocionBean getPromocionEmpleado(){
		return promocionEmpleado;
	}

	public void setPromocionEmpleado(PromocionBean promocionEmpleado){
		this.promocionEmpleado = promocionEmpleado;
	}

	public String getApiKeyPromocionEmpleados(){
		return apiKeyPromocionEmpleados;
	}

	public void setApiKeyPromocionEmpleados(String apiKeyPromocionEmpleados){
		this.apiKeyPromocionEmpleados = apiKeyPromocionEmpleados;
	}

}
