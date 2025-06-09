package com.comerzzia.pampling.pos.services.ticket.cabecera;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pampling.pos.services.fiscal.alemania.epos.EposOutput;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;

@Component
@Scope("prototype")
@Primary
public class PamplingCabeceraTicket extends CabeceraTicket {

	@XmlElement(name = "numero_recibo_fiscal")
	private String numReciboFiscal;

	@XmlElement(name = "fecha_recibo_fiscal")
	private String fechaReciboFiscal;

	@XmlElement(name = "printer_id")
	private String printerId;

	@XmlElement(name = "zrep_num")
	private String zRepNum;

	@XmlElement(name = "numero_recibo_fiscal_origen")
	private String numReciboFiscalOrigen;

	@XmlElement(name = "fecha_recibo_fiscal_origen")
	private String fechaReciboFiscalOrigen;

	@XmlElement(name = "printer_id_origen")
	private String printerIdOrigen;

	@XmlElement(name = "zrep_num_origen")
	private String zRepNumOrigen;

	private String idPedido;

	private boolean devolucionCompleta;

	@XmlElement(name = "TSE")
	private EposOutput tse;
	
	public String getNumReciboFiscal() {
		return numReciboFiscal;
	}

	public void setNumReciboFiscal(String numReciboFiscal) {
		this.numReciboFiscal = numReciboFiscal;
	}

	public String getFechaReciboFiscal() {
		return fechaReciboFiscal;
	}

	public void setFechaReciboFiscal(String fechaReciboFiscal) {
		this.fechaReciboFiscal = fechaReciboFiscal;
	}

	public String getPrinterId() {
		return printerId;
	}

	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}

	public String getzRepNum() {
		return zRepNum;
	}

	public void setzRepNum(String zRepNum) {
		this.zRepNum = zRepNum;
	}

	public String getNumReciboFiscalOrigen() {
		return numReciboFiscalOrigen;
	}

	public void setNumReciboFiscalOrigen(String numReciboFiscalOrigen) {
		this.numReciboFiscalOrigen = numReciboFiscalOrigen;
	}

	public String getFechaReciboFiscalOrigen() {
		return fechaReciboFiscalOrigen;
	}

	public void setFechaReciboFiscalOrigen(String fechaReciboFiscalOrigen) {
		this.fechaReciboFiscalOrigen = fechaReciboFiscalOrigen;
	}

	public String getPrinterIdOrigen() {
		return printerIdOrigen;
	}

	public void setPrinterIdOrigen(String printerIdOrigen) {
		this.printerIdOrigen = printerIdOrigen;
	}

	public String getzRepNumOrigen() {
		return zRepNumOrigen;
	}

	public void setzRepNumOrigen(String zRepNumOrigen) {
		this.zRepNumOrigen = zRepNumOrigen;
	}

	public String getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(String idPedido) {
		this.idPedido = idPedido;
	}

	public boolean isDevolucionCompleta() {
		return devolucionCompleta;
	}

	public void setDevolucionCompleta(boolean devolucionCompleta) {
		this.devolucionCompleta = devolucionCompleta;
	}

	public EposOutput getTse() {
		return tse;
	}

	public void setTse(EposOutput tse) {
		this.tse = tse;
	}
}
