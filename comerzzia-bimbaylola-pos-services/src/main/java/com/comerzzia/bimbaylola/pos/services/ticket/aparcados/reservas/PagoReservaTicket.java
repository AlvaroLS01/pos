package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.bimbaylola.pos.persistence.reservas.ReservasPagoGiftCardBean;

/**
 * Objeto para contener todos los datos referentes a los pagos realizados en una Reserva.
 */
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class PagoReservaTicket{

	@XmlElement(name="linea")
	private String linea;
	@XmlElement(name="orden")
	private Integer orden;
	@XmlElement(name = "usuario")
	private String usuario;
	@XmlElement(name="uid_diario_caja")
	private String uidDiarioCaja;
	@XmlElement(name="fecha_actualizacion")
	private String fechaActualizacion;
	@XmlElement(name="codmedpag")
	private String codigoMedioPago;
	@XmlElement(name="desmedpag")
	private String descripcionMedioPago;
	@XmlElement(name="importe")
	private BigDecimal importe;
	@XmlElement(name="datosRespuestaPagoTarjeta")
	private DatosRespuestaTarjetaReservaTicket datosRespuestaTarjeta;
	@XmlElementWrapper(name = "giftcards")
	@XmlElement(name = "giftcard")
	protected List<ReservasPagoGiftCardBean> giftcards;
	
	public String getLinea(){
		return linea;
	}
	public void setLinea(String linea){
		this.linea = linea;
	}
	public Integer getOrden(){
		return orden;
	}
	public void setOrden(Integer orden){
		this.orden = orden;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getUidDiarioCaja(){
		return uidDiarioCaja;
	}
	public void setUidDiarioCaja(String uidDiarioCaja){
		this.uidDiarioCaja = uidDiarioCaja;
	}
	public String getFechaActualizacion(){
		return fechaActualizacion;
	}
	public void setFechaActualizacion(String fechaActualizacion){
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getCodigoMedioPago(){
		return codigoMedioPago;
	}
	public void setCodigoMedioPago(String codigoMedioPago){
		this.codigoMedioPago = codigoMedioPago;
	}
	public String getDescripcionMedioPago(){
		return descripcionMedioPago;
	}
	public void setDescripcionMedioPago(String descripcionMedioPago){
		this.descripcionMedioPago = descripcionMedioPago;
	}
	public BigDecimal getImporte(){
		return importe;
	}
	public void setImporte(BigDecimal importe){
		this.importe = importe;
	}
	public DatosRespuestaTarjetaReservaTicket getDatosRespuestaTarjeta(){
		return datosRespuestaTarjeta;
	}
	public void setDatosRespuestaTarjeta(
			DatosRespuestaTarjetaReservaTicket datosRespuestaTarjeta){
		this.datosRespuestaTarjeta = datosRespuestaTarjeta;
	}
    public List<ReservasPagoGiftCardBean> getGiftcards(){
    	return giftcards;
    }
    public void setGiftcards(List<ReservasPagoGiftCardBean> giftcards){
    	this.giftcards = giftcards;
    }
	
}
