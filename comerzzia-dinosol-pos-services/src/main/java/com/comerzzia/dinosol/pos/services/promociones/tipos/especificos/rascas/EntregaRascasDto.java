package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.rascas;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entrega_rascas")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntregaRascasDto {

	@XmlElement(name = "uid_ticket")
	private String uidTicket;

	@XmlElement(name = "cod_ticket")
	private String codTicket;

	private int linea;

	@XmlElement(name = "cod_almacen")
	private String codAlm;

	@XmlElement(name = "cod_caja")
	private String codCaja;

	@XmlElement(name = "id_cajero")
	private Long idCajero;
	
	@XmlElement(name = "importe_total")
	private BigDecimal importeTotal;

	@XmlElement(name = "rascas_concedidos")
	private int rascasConcedidos;
	
	@XmlElement(name = "sobres_extras")
	private int sobreExtras;

	@XmlElementWrapper(name = "rascas_entregados")
	@XmlElement(name = "codigo_rasca")
	private List<String> rascasEntregados;

	public String getUidTicket() {
		return uidTicket;
	}

	public void setUidTicket(String uidTicket) {
		this.uidTicket = uidTicket;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	public String getCodAlm() {
		return codAlm;
	}

	public void setCodAlm(String codAlm) {
		this.codAlm = codAlm;
	}

	public String getCodCaja() {
		return codCaja;
	}

	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public int getRascasConcedidos() {
		return rascasConcedidos;
	}

	public void setRascasConcedidos(int rascasConcedidos) {
		this.rascasConcedidos = rascasConcedidos;
	}
	
	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public int getSobreExtras() {
		return sobreExtras;
	}

	public void setSobreExtras(int sobreExtras) {
		this.sobreExtras = sobreExtras;
	}

	public List<String> getRascasEntregados() {
		return rascasEntregados;
	}

	public void setRascasEntregados(List<String> rascasEntregados) {
		this.rascasEntregados = rascasEntregados;
	}

	public Long getIdCajero() {
		return idCajero;
	}

	public void setIdCajero(Long idCajero) {
		this.idCajero = idCajero;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

}
