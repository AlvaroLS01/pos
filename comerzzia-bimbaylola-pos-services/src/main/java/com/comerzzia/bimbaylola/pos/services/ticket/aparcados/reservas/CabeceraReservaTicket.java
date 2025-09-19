package com.comerzzia.bimbaylola.pos.services.ticket.aparcados.reservas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.services.core.tiendas.Tienda;

/**
 * Objeto para contener todos los datos referentes a la cabecera de un Ticket Reserva.
 */
@Component
@Scope("prototype")
@XmlAccessorType(XmlAccessType.FIELD)
public class CabeceraReservaTicket{
	
	@XmlElement(name = "uid_ticket")
	private String uidApartado;
	@XmlElement(name = "uid_actividad")
	private String uidActividad;
	@XmlElement(name = "uid_diario_caja")
	private String uidDiarioCaja;
	@XmlElement(name = "id_ticket")
	private Long idTicket;
	@XmlElement(name = "id_reserva")
	private String idReserva;
	@XmlElement(name = "cod_ticket")
	private String codigoApartado;
	@XmlElement(name = "serie_ticket")
	private String serieTicket;
	@XmlElement(name = "estado_apartado")
	private Short estadoApartado;
	@XmlElement(name = "fecha")
	private String fecha;
	@XmlElement(name = "fecha_actualizacion")
	private String fechaActualizacion;
	@XmlElement(name = "tipo_documento")
	private String tipoDocumento;
	@XmlElement(name = "cod_tipo_documento")
	private String codigoTipoDocumento;
	@XmlElement(name = "des_tipo_documento")
	private String descripcionTipoDocumento;
	@XmlElement(name = "formato_impresion")
	private String formatoImpresion;
    @XmlElement(name="datos_fidelizacion")
    protected DatosFidelizadoReservaTicket datosFidelizado;
	
	@XmlElement(name = "cajero")
	protected UsuarioBean cajero;
	@XmlElement(name = "tienda")
	protected Tienda tienda;
	@XmlElement(name = "cliente")
	protected ClienteBean cliente;
	@XmlElement(name = "empresa")
    protected EmpresaBean empresa;
	@XmlElement(name = "totales")
    protected TotalesReservaTicket totales;
	
	@XmlElement(name = "especialesApartado")
	private CabeceraEspecialesReservaTicket especialesApartado;
	
	@XmlElement(name = "cod_caja")
	private String codCaja;
	
	@XmlElement(name = "esPagoAnticipo")
	protected String esPagoAnticipo;
	
	@XmlElement(name = "esCancelacion")
	protected String esCancelacion;
	
	public String getUidApartado(){
		return uidApartado;
	}
	public void setUidApartado(String uidApartado){
		this.uidApartado = uidApartado;
	}
	public Long getIdTicket(){
		return idTicket;
	}
	public void setIdTicket(Long idTicket){
		this.idTicket = idTicket;
	}
	public String getIdReserva(){
		return idReserva;
	}
	public void setIdReserva(String idReserva){
		this.idReserva = idReserva;
	}
	public String getCodigoApartado(){
		return codigoApartado;
	}
	public void setCodigoApartado(String codigoApartado){
		this.codigoApartado = codigoApartado;
	}
	public String getUidActividad(){
		return uidActividad;
	}
	public void setUidActividad(String uidActividad){
		this.uidActividad = uidActividad;
	}
	public String getUidDiarioCaja(){
		return uidDiarioCaja;
	}
	public void setUidDiarioCaja(String uidDiarioCaja){
		this.uidDiarioCaja = uidDiarioCaja;
	}
	public String getSerieTicket(){
		return serieTicket;
	}
	public void setSerieTicket(String serieTicket){
		this.serieTicket = serieTicket;
	}
	public String getFecha(){
		return fecha;
	}
	public void setFecha(String fecha){
		this.fecha = fecha;
	}
	public String getFechaActualizacion(){
		return fechaActualizacion;
	}
	public void setFechaActualizacion(String fechaActualizacion){
		this.fechaActualizacion = fechaActualizacion;
	}
	public String getTipoDocumento(){
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento){
		this.tipoDocumento = tipoDocumento;
	}
	public String getCodigoTipoDocumento(){
		return codigoTipoDocumento;
	}
	public void setCodigoTipoDocumento(String codigoTipoDocumento){
		this.codigoTipoDocumento = codigoTipoDocumento;
	}
	public String getDescripcionTipoDocumento(){
		return descripcionTipoDocumento;
	}
	public void setDescripcionTipoDocumento(String descripcionTipoDocumento){
		this.descripcionTipoDocumento = descripcionTipoDocumento;
	}
	public String getFormatoImpresion(){
		return formatoImpresion;
	}
	public void setFormatoImpresion(String formatoImpresion){
		this.formatoImpresion = formatoImpresion;
	}
	public Short getEstadoApartado(){
		return estadoApartado;
	}
	public void setEstadoApartado(Short estadoApartado){
		this.estadoApartado = estadoApartado;
	}
	public UsuarioBean getCajero(){
		return cajero;
	}
	public void setCajero(UsuarioBean cajero){
		this.cajero = cajero;
	}
	public Tienda getTienda(){
		return tienda;
	}
	public void setTienda(Tienda tienda){
		this.tienda = tienda;
	}
	public ClienteBean getCliente(){
		return cliente;
	}
	public void setCliente(ClienteBean cliente){
		this.cliente = cliente;
	}
	public EmpresaBean getEmpresa(){
		return empresa;
	}
	public void setEmpresa(EmpresaBean empresa){
		this.empresa = empresa;
	}
	public TotalesReservaTicket getTotales(){
		return totales;
	}
	public void setTotales(TotalesReservaTicket totales){
		this.totales = totales;
	}
	public CabeceraEspecialesReservaTicket getEspecialesApartado(){
		return especialesApartado;
	}
	public void setEspecialesApartado(CabeceraEspecialesReservaTicket especialesApartado){
		this.especialesApartado = especialesApartado;
	}
    public DatosFidelizadoReservaTicket getDatosFidelizado(){
    	return datosFidelizado;
    }
    public void setDatosFidelizado(DatosFidelizadoReservaTicket datosFidelizado){
    	this.datosFidelizado = datosFidelizado;
    }
	public String getCodCaja() {
		return codCaja;
	}
	public void setCodCaja(String codCaja) {
		this.codCaja = codCaja;
	}

	public String getEsPagoAnticipo() {
		return esPagoAnticipo;
	}

	public void setEsPagoAnticipo(String esPagoAnticipo) {
		this.esPagoAnticipo = esPagoAnticipo;
	}
	
	public String getEsCancelacion() {
		return esCancelacion;
	}
	
	public void setEsCancelacion(String esCancelacion) {
		this.esCancelacion = esCancelacion;
	}

}
