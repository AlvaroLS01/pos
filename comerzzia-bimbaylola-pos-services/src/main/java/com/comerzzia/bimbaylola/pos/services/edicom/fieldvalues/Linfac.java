package com.comerzzia.bimbaylola.pos.services.edicom.fieldvalues;

import java.math.BigDecimal;

import com.comerzzia.bimbaylola.pos.services.edicom.util.EdicomFormat;

public class Linfac {
	/*
	 * POR CADA LINEA HAY QUE PASAR UN LINFAC Y UN IMPFACLIN
	 */
	private final String LABEL_LIN = "LINFAC";
	/*
	 * RELLENABLES
	 */
	private String idlin;
	private BigDecimal cantidad;
	private String unidadMedida; // VALOR FIJO
	private BigDecimal importeLinea; // CANTIDAD * IMPORTE
	private String descripcion; 
	private String iditem; 
	private String infoAdicional1; // COLOR
	private String infoAdicional2; // TALLA
	private BigDecimal precioUnitario;
	/*
	 * VALORES FIJOS
	 */
	private String codAdicional;
	private String infoAdicional3;
	private String codGln;
	private String tipoStandar;
	private String marcaItem;
	private String modeloItem;
	private String mandanteId;
	private BigDecimal mandanteTipoIdentificacion;
	private String dvMandante;
	private BigDecimal valorRegalo;
	private String regaloTipo;
	private String cantidadBase;
	private String unidadMedidaBase;
	private String descripcionExt;
	private String packCant;
	private BigDecimal cantidadBien;
	private BigDecimal cantidadBienUnidadMedida;
	private BigDecimal adicMandato;
	private BigDecimal adicTransporte;
	private BigDecimal tipoIngreso;
	private String numeroRadAcep;
	private String numeroRemesa;
	private BigDecimal valorFlete;
	private BigDecimal cantTrans;
	private String udTrans;
	private String ordenCompra;
	private String notMatricInm;
	private String notValorCom;
	private String extendedIdItem;
	private String periodoFechaInicio;
	private BigDecimal periodoCodDescripcion;
	private String periodoDescripcion;
	
	
	public Linfac() {
		tipoStandar = "999";
		unidadMedida = "94";
	}
	
	public String getIdlin() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(idlin, 15);
	}
	
	public String getCantidad() {
		return EdicomFormat.rellenaCerosNumericos(cantidad, 15, 4);
	}
	
	public String getUnidadMedida() {
		return unidadMedida;
	}
	
	public String getImporteLinea() {
		return EdicomFormat.rellenaCerosNumericos(importeLinea, 15, 6);
	}
	
	public String getDescripcion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(descripcion, 250);
	}
	
	public String getIditem() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(iditem, 50);
	}
	
	public String getInfoAdicional1() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(infoAdicional1, 125);
	}
	
	public String getInfoAdicional2() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(infoAdicional2, 125);
	}
	
	public String getPrecioUnitario() {
		return EdicomFormat.rellenaCerosNumericos(precioUnitario, 15, 6);
	}
	
	public String getCodAdicional() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codAdicional, 125);
	}
	
	public String getInfoAdicional3() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(infoAdicional3, 10000);
	}
	
	public String getCodGln() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(codGln, 13);
	}
	
	public String getTipoStandar() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(tipoStandar, 13);
	}
	
	public String getMarcaItem() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(marcaItem, 125);
	}
	
	public String getModeloItem() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(modeloItem, 125);
	}
	
	public String getMandanteId() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(mandanteId, 12);
	}
	
	public String getMandanteTipoIdentificacion() {
		return EdicomFormat.rellenaCerosNumericos(mandanteTipoIdentificacion, 2, 0);
	}
	
	public String getDvMandante() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(dvMandante, 1);
	}
	
	public String getValorRegalo() {
		return EdicomFormat.rellenaCerosNumericos(valorRegalo, 15, 6);
	}
	
	public String getRegaloTipo() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(regaloTipo, 2);
	}
	
	public String getCantidadBase() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(cantidadBase, 6);
	}
	
	public String getUnidadMedidaBase() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(unidadMedidaBase, 3);
	}
	
	public String getDescripcionExt() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(descripcionExt, 300);
	}
	
	public String getPackCant() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(packCant, 7);
	}
	
	public String getCantidadBien() {
		return EdicomFormat.rellenaCerosNumericos(cantidadBien, 15, 4);
	}
	
	public String getCantidadBienUnidadMedida() {
		return EdicomFormat.rellenaCerosNumericos(cantidadBienUnidadMedida, 15, 4);
	}
	
	public String getAdicMandato() {
		return EdicomFormat.rellenaCerosNumericos(adicMandato, 2, 0);
	}
	
	public String getAdicTransporte() {
		return EdicomFormat.rellenaCerosNumericos(adicTransporte, 2, 0);
	}
	
	public String getTipoIngreso() {
		return EdicomFormat.rellenaCerosNumericos(tipoIngreso, 1, 0);
	}
	
	public String getNumeroRadAcep() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroRadAcep, 125);
	}
	
	public String getNumeroRemesa() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(numeroRemesa, 125);
	}
	
	public String getValorFlete() {
		return EdicomFormat.rellenaCerosNumericos(valorFlete, 15, 4);
	}
	
	public String getCantTrans() {
		return EdicomFormat.rellenaCerosNumericos(cantTrans, 15, 0);
	}
	
	public String getUdTrans() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(udTrans, 3);
	}
	
	public String getOrdenCompra() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(ordenCompra, 100);
	}
	
	public String getNotMatricInm() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(notMatricInm, 30);
	}
	
	public String getNotValorCom() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(notValorCom, 20);
	}
	
	public String getExtendedIdItem() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(extendedIdItem, 150);
	}
	
	public String getPeriodoFechaInicio() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(periodoFechaInicio, 8);
	}
	
	public String getPeriodoCodDescripcion() {
		return EdicomFormat.rellenaCerosNumericos(periodoCodDescripcion, 1, 0);
	}
	
	public String getPeriodoDescripcion() {
		return EdicomFormat.rellenaEspaciosAlfanumericos(periodoDescripcion, 17);
	}
	
	public void setIdlin(String idlin) {
		this.idlin = idlin;
	}
	
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	
	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}
	
	public void setImporteLinea(BigDecimal importeLinea) {
		this.importeLinea = importeLinea;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setIditem(String iditem) {
		this.iditem = iditem;
	}
	
	public void setInfoAdicional1(String infoAdicional1) {
		this.infoAdicional1 = infoAdicional1;
	}
	
	public void setInfoAdicional2(String infoAdicional2) {
		this.infoAdicional2 = infoAdicional2;
	}
	
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	
	public void setCodAdicional(String codAdicional) {
		this.codAdicional = codAdicional;
	}
	
	public void setInfoAdicional3(String infoAdicional3) {
		this.infoAdicional3 = infoAdicional3;
	}
	
	public void setCodGln(String codGln) {
		this.codGln = codGln;
	}
	
	public void setTipoStandar(String tipoStandar) {
		this.tipoStandar = tipoStandar;
	}
	
	public void setMarcaItem(String marcaItem) {
		this.marcaItem = marcaItem;
	}
	
	public void setModeloItem(String modeloItem) {
		this.modeloItem = modeloItem;
	}
	
	public void setMandanteId(String mandanteId) {
		this.mandanteId = mandanteId;
	}
	
	public void setMandanteTipoIdentificacion(BigDecimal mandanteTipoIdentificacion) {
		this.mandanteTipoIdentificacion = mandanteTipoIdentificacion;
	}
	
	public void setDvMandante(String dvMandante) {
		this.dvMandante = dvMandante;
	}
	
	public void setValorRegalo(BigDecimal valorRegalo) {
		this.valorRegalo = valorRegalo;
	}
	
	public void setRegaloTipo(String regaloTipo) {
		this.regaloTipo = regaloTipo;
	}
	
	public void setCantidadBase(String cantidadBase) {
		this.cantidadBase = cantidadBase;
	}
	
	public void setUnidadMedidaBase(String unidadMedidaBase) {
		this.unidadMedidaBase = unidadMedidaBase;
	}
	
	public void setDescripcionExt(String descripcionExt) {
		this.descripcionExt = descripcionExt;
	}
	
	public void setPackCant(String packCant) {
		this.packCant = packCant;
	}
	
	public void setCantidadBien(BigDecimal cantidadBien) {
		this.cantidadBien = cantidadBien;
	}
	
	public void setCantidadBienUnidadMedida(BigDecimal cantidadBienUnidadMedida) {
		this.cantidadBienUnidadMedida = cantidadBienUnidadMedida;
	}
	
	public void setAdicMandato(BigDecimal adicMandato) {
		this.adicMandato = adicMandato;
	}
	
	public void setAdicTransporte(BigDecimal adicTransporte) {
		this.adicTransporte = adicTransporte;
	}
	
	public void setTipoIngreso(BigDecimal tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}
	
	public void setNumeroRadAcep(String numeroRadAcep) {
		this.numeroRadAcep = numeroRadAcep;
	}
	
	public void setNumeroRemesa(String numeroRemesa) {
		this.numeroRemesa = numeroRemesa;
	}
	
	public void setValorFlete(BigDecimal valorFlete) {
		this.valorFlete = valorFlete;
	}
	
	public void setCantTrans(BigDecimal cantTrans) {
		this.cantTrans = cantTrans;
	}
	
	public void setUdTrans(String udTrans) {
		this.udTrans = udTrans;
	}
	
	public void setOrdenCompra(String ordenCompra) {
		this.ordenCompra = ordenCompra;
	}
	
	public void setNotMatricInm(String notMatricInm) {
		this.notMatricInm = notMatricInm;
	}
	
	public void setNotValorCom(String notValorCom) {
		this.notValorCom = notValorCom;
	}
	
	public void setExtendedIdItem(String extendedIdItem) {
		this.extendedIdItem = extendedIdItem;
	}
	
	public void setPeriodoFechaInicio(String periodoFechaInicio) {
		this.periodoFechaInicio = periodoFechaInicio;
	}
	
	public void setPeriodoCodDescripcion(BigDecimal periodoCodDescripcion) {
		this.periodoCodDescripcion = periodoCodDescripcion;
	}
	
	public void setPeriodoDescripcion(String periodoDescripcion) {
		this.periodoDescripcion = periodoDescripcion;
	}

	@Override
	public String toString() {
		return LABEL_LIN + "|" + getIdlin() + "|" + getCantidad() + "|" + getUnidadMedida() + "|" + getImporteLinea() + "|" + getDescripcion() + "|" + getIditem() + "|" + getInfoAdicional1()
		        + "|" + getInfoAdicional2() + "|" + getPrecioUnitario() + "|" + getCodAdicional() + "|" + getInfoAdicional3() + "|" + getCodGln() + "|" + getTipoStandar() + "|" + getMarcaItem() + "|"
		        + getModeloItem() + "|" + getMandanteId() + "|" + getMandanteTipoIdentificacion() + "|" + getDvMandante() + "|" + getValorRegalo() + "|" + getRegaloTipo() + "|" + getCantidadBase()
		        + "|" + getUnidadMedidaBase() + "|" + getDescripcionExt() + "|" + getPackCant() + "|" + getCantidadBien() + "|" + getCantidadBienUnidadMedida() + "|" + getAdicMandato() + "|"
		        + getAdicTransporte() + "|" + getTipoIngreso() + "|" + getNumeroRadAcep() + "|" + getNumeroRemesa() + "|" + getValorFlete() + "|" + getCantTrans() + "|" + getUdTrans() + "|"
		        + getOrdenCompra() + "|" + getNotMatricInm() + "|" + getNotValorCom() + "|" + getExtendedIdItem() + "|" + getPeriodoFechaInicio() + "|" + getPeriodoCodDescripcion() + "|"
		        + getPeriodoDescripcion() + "|";
	}


}
