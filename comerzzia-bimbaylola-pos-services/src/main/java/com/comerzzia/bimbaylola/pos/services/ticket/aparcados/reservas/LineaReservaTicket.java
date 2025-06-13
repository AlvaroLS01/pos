package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Objeto para contener la linea de un Ticket de Reserva.
 */
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class LineaReservaTicket{

	@XmlElement(name = "linea")
	private Integer linea;
	
	@XmlElement(name = "codart")
	private String codigoArticulo;
	@XmlElement(name = "desart")
	private String descripcionArticulo;
	@XmlElement(name = "desartampli")
	private String descripcionArticuloAmpliada;
	@XmlElement(name = "cantidad")
	private BigDecimal cantidad;
	@XmlElement(name = "cantidad_medida")
	private BigDecimal cantidadMedida;
	@XmlElement(name = "unidad_medida")
	private String unidadMedida;
	@XmlElement(name = "desglose1")
	private String desglose1;
	@XmlElement(name = "desglose2")
	private String desglose2;
	
	@XmlElement(name="usuario")
	private String nombreUsuario;
	@XmlElement(name="precio")
	private BigDecimal precio;
	@XmlElement(name = "precio_total")
	private BigDecimal precioTotal;
	@XmlElement(name = "precio_total_sin_dto")
	private BigDecimal precioTotalSinDto;
	@XmlElement(name = "precio_costo")
	private BigDecimal precioCosto;
	@XmlElement(name = "importe")
	private BigDecimal importe;
	@XmlElement(name = "importe_total")
	private BigDecimal importeTotal;
	@XmlElement(name = "descuento")
	private BigDecimal descuento;
	@XmlElement(name = "codImp")
	private String codigoImpuesto;
	@XmlElement(name = "cantidad_entregada")
	private Integer cantidadEntregada;
	@XmlElement(name = "cantidad_cancelada")
	private Integer cantidadCancelada;
	@XmlElement(name = "estado_linea_apartado")
	private Short estadoLineaApartado;
	@XmlElement(name = "fecha_actualizacion")
	private String fechaActualizacion;
	@XmlElement(name = "fecha_apartado_articulo")
	private String fechaApartadoArticulo;
	@XmlElement(name = "fecha_prevista_recogida")
	private String fechaPrevistaRecogida;
	
	@XmlElement(name="uid_ticket")
	private String uidTicket;
    @XmlAttribute(name = "idlinea")
    protected Integer idLinea;
    
        
	public Integer getLinea(){
		return linea;
	}
	public void setLinea(Integer linea){
		this.linea = linea;
	}
	public BigDecimal getCantidadMedida(){
		return cantidadMedida;
	}
	public void setCantidadMedida(BigDecimal cantidadMedida){
		this.cantidadMedida = cantidadMedida;
	}
	public String getUnidadMedida(){
		return unidadMedida;
	}
	public void setUnidadMedida(String unidadMedida){
		this.unidadMedida = unidadMedida;
	}
	public String getCodigoArticulo(){
		return codigoArticulo;
	}
	public void setCodigoArticulo(String codigoArticulo){
		this.codigoArticulo = codigoArticulo;
	}
	public String getDescripcionArticulo(){
		return descripcionArticulo;
	}
	public void setDescripcionArticulo(String descripcionArticulo){
		this.descripcionArticulo = descripcionArticulo;
	}
	public String getDescripcionArticuloAmpliada(){
		return descripcionArticuloAmpliada;
	}
	public void setDescripcionArticuloAmpliada(String descripcionArticuloAmpliada){
		this.descripcionArticuloAmpliada = descripcionArticuloAmpliada;
	}
	public BigDecimal getCantidad(){
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad){
		this.cantidad = cantidad;
	}
	public String getDesglose1(){
		return desglose1;
	}
	public void setDesglose1(String desglose1){
		this.desglose1 = desglose1;
	}
	public String getDesglose2(){
		return desglose2;
	}
	public void setDesglose2(String desglose2){
		this.desglose2 = desglose2;
	}
	public String getFechaApartadoArticulo(){
		return fechaApartadoArticulo;
	}
	public void setFechaApartadoArticulo(String fechaApartadoArticulo){
		this.fechaApartadoArticulo = fechaApartadoArticulo;
	}
	public String getFechaPrevistaRecogida(){
		return fechaPrevistaRecogida;
	}
	public void setFechaPrevistaRecogida(String fechaPrevistaRecogida){
		this.fechaPrevistaRecogida = fechaPrevistaRecogida;
	}
	public String getNombreUsuario(){
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario){
		this.nombreUsuario = nombreUsuario;
	}
	public BigDecimal getPrecio(){
		return precio;
	}
	public void setPrecio(BigDecimal precio){
		this.precio = precio;
	}
	public BigDecimal getPrecioTotal(){
		return precioTotal;
	}
	public void setPrecioTotal(BigDecimal precioTotal){
		this.precioTotal = precioTotal;
	}
	public BigDecimal getPrecioTotalSinDto(){
		return precioTotalSinDto;
	}
	public void setPrecioTotalSinDto(BigDecimal precioTotalSinDto){
		this.precioTotalSinDto = precioTotalSinDto;
	}
	public BigDecimal getPrecioCosto(){
		return precioCosto;
	}
	public void setPrecioCosto(BigDecimal precioCosto){
		this.precioCosto = precioCosto;
	}
	public BigDecimal getImporte(){
		return importe;
	}
	public void setImporte(BigDecimal importe){
		this.importe = importe;
	}
	public BigDecimal getImporteTotal(){
		return importeTotal;
	}
	public void setImporteTotal(BigDecimal importeTotal){
		this.importeTotal = importeTotal;
	}
	public BigDecimal getDescuento(){
		return descuento;
	}
	public void setDescuento(BigDecimal descuento){
		this.descuento = descuento;
	}
	public String getCodigoImpuesto(){
		return codigoImpuesto;
	}
	public void setCodigoImpuesto(String codigoImpuesto){
		this.codigoImpuesto = codigoImpuesto;
	}
	public Integer getCantidadEntregada(){
		return cantidadEntregada;
	}
	public void setCantidadEntregada(Integer cantidadEntregada){
		this.cantidadEntregada = cantidadEntregada;
	}
	public Integer getCantidadCancelada(){
		return cantidadCancelada;
	}
	public void setCantidadCancelada(Integer cantidadCancelada){
		this.cantidadCancelada = cantidadCancelada;
	}
	public Short getEstadoLineaApartado(){
		return estadoLineaApartado;
	}
	public void setEstadoLineaApartado(Short estadoLineaApartado){
		this.estadoLineaApartado = estadoLineaApartado;
	}
	public String getFechaActualizacion(){
		return fechaActualizacion;
	}
	public void setFechaActualizacion(String fechaActualizacion){
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getUidTicket(){
		return uidTicket;
	}
	public void setUidTicket(String uidTicket){
		this.uidTicket = uidTicket;
	}
	public Integer getIdLinea(){
		return idLinea;
	}
	public void setIdLinea(Integer idLinea){
		this.idLinea = idLinea;
	}
	
}
