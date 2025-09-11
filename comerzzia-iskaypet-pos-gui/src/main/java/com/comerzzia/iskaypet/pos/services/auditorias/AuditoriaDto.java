package com.comerzzia.iskaypet.pos.services.auditorias;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "auditoria_motivos")
public class AuditoriaDto implements Cloneable{

	@XmlElement(name = "uidActividad")
	private String uidActividad;

	@XmlElement(name = "uidAuditoria")
	private String uidAuditoria;

	@XmlElement(name = "id_tipo_documento")
	private Long idTipoDocumento;

	@XmlElement(name = "codalm")
	private String codalm;

	@XmlElement(name = "codcaja")
	private String codcaja;

	@XmlElement(name = "usuario")
	private String usuario;

	@XmlElement(name = "descripcion")
	private String descripcion;

	@XmlElement(name = "fecha")
	private String fecha;

	@XmlElement(name = "tipo_auditoria")
	private String tipoAuditoria;

	@XmlElement(name = "cod_motivo")
	private String codMotivo;

	@XmlElement(name = "observaciones")
	private String observaciones;

	@XmlElement(name = "codart")
	private String codArticulo;

	@XmlElement(name = "desart")
	private String desArt;

	@XmlElement(name = "desglose1")
	private String desglose1;

	@XmlElement(name = "desglose2")
	private String desglose2;

	@XmlElement(name = "precio_inicial")
	private BigDecimal precioInicial;

	@XmlElement(name = "precio_final")
	private BigDecimal precioFinal;
	
    //GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
	@XmlElement(name = "totales")
	private AUOCTotales totales;
	@XmlElementWrapper (name = "lineas")
	@XmlElement(name = "linea")
	private List<AUOCLineas> lstLineas = new ArrayList<AUOCLineas>();
	
	
	public String getUidActividad() {
		return uidActividad;
	}

	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}

	public String getCodalm() {
		return codalm;
	}

	public void setCodalm(String codalm) {
		this.codalm = codalm;
	}

	public String getCodcaja() {
		return codcaja;
	}

	public void setCodcaja(String codcaja) {
		this.codcaja = codcaja;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getTipoAuditoria() {
		return tipoAuditoria;
	}

	public void setTipoAuditoria(String tipoAuditoria) {
		this.tipoAuditoria = tipoAuditoria;
	}

	public String getCodMotivo() {
		return codMotivo;
	}

	public void setCodMotivo(String codMotivo) {
		this.codMotivo = codMotivo;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getCodArticulo() {
		return codArticulo;
	}

	public void setCodArticulo(String codArticulo) {
		this.codArticulo = codArticulo;
	}

	public String getDesArt() {
		return desArt;
	}

	public void setDesArt(String desArt) {
		this.desArt = desArt;
	}

	public String getDesglose1() {
		return desglose1;
	}

	public void setDesglose1(String desglose1) {
		this.desglose1 = desglose1;
	}

	public String getDesglose2() {
		return desglose2;
	}

	public void setDesglose2(String desglose2) {
		this.desglose2 = desglose2;
	}

	public BigDecimal getPrecioInicial() {
		return precioInicial;
	}

	public void setPrecioInicial(BigDecimal precioInicial) {
		this.precioInicial = precioInicial;
	}

	public BigDecimal getPrecioFinal() {
		return precioFinal;
	}

	public void setPrecioFinal(BigDecimal precioFinal) {
		this.precioFinal = precioFinal;
	}

	public String getUidAuditoria() {
		return uidAuditoria;
	}

	public void setUidAuditoria(String uidAuditoria) {
		this.uidAuditoria = uidAuditoria;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public AUOCTotales getTotales() {
		return totales;
	}

	public void setTotales(AUOCTotales totales) {
		this.totales = totales;
	}

	public List<AUOCLineas> getLstLineas() {
		return lstLineas;
	}

	public void setLstLineas(List<AUOCLineas> lstLineas) {
		this.lstLineas = lstLineas;
	}


	@Override
	public AuditoriaDto clone() throws CloneNotSupportedException {
		return (AuditoriaDto) super.clone();
	}
	
	
	

}