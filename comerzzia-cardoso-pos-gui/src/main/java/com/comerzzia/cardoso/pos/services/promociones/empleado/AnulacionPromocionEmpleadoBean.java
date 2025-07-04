package com.comerzzia.cardoso.pos.services.promociones.empleado;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "parametros")
public class AnulacionPromocionEmpleadoBean {

	@XmlElement
	private Long idPromocion;
	@XmlElement
	private String numTarjeta;
	@XmlElement
	private String uidTransaccion;
	@XmlElement
	private String referenciaUso;
	@XmlElement
	private BigDecimal importeUso;
	@XmlElement
	private BigDecimal importeTotal;

	public Long getIdPromocion() {
		return idPromocion;
	}

	public void setIdPromocion(Long idPromocion) {
		this.idPromocion = idPromocion;
	}

	public String getNumTarjeta() {
		return numTarjeta;
	}

	public void setNumTarjeta(String numTarjeta) {
		this.numTarjeta = numTarjeta;
	}

	public String getUidTransaccion() {
		return uidTransaccion;
	}

	public void setUidTransaccion(String uidTransaccion) {
		this.uidTransaccion = uidTransaccion;
	}

	public String getReferenciaUso() {
		return referenciaUso;
	}

	public void setReferenciaUso(String referenciaUso) {
		this.referenciaUso = referenciaUso;
	}

	public BigDecimal getImporteUso() {
		return importeUso;
	}

	public void setImporteUso(BigDecimal importeUso) {
		this.importeUso = importeUso;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

}
