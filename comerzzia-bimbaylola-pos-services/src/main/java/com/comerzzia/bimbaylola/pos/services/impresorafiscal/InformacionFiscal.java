package com.comerzzia.bimbaylola.pos.services.impresorafiscal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Primary
@XmlAccessorType(XmlAccessType.NONE)
public class InformacionFiscal {

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

	public InformacionFiscal() {
		super();
	}

	public InformacionFiscal(String numReciboFiscal, String fechaReciboFiscal, String printerId, String zRepNum, String numReciboFiscalOrigen, String fechaReciboFiscalOrigen, String printerIdOrigen,
	        String zRepNumOrigen) {
		super();
		this.numReciboFiscal = numReciboFiscal;
		this.fechaReciboFiscal = fechaReciboFiscal;
		this.printerId = printerId;
		this.zRepNum = zRepNum;
		this.numReciboFiscalOrigen = numReciboFiscalOrigen;
		this.fechaReciboFiscalOrigen = fechaReciboFiscalOrigen;
		this.printerIdOrigen = printerIdOrigen;
		this.zRepNumOrigen = zRepNumOrigen;
	}

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

}
