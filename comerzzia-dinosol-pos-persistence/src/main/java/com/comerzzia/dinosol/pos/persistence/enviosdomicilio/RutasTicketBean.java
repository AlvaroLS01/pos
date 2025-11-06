package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.pos.util.text.TextUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RutasTicketBean {

	private String uidTicketCzz;
	private String codTicketCzz;
	private String cajero;

	private String codTicket;

	private String idFidelizado;
	private String telefono;
	private String nombreCompleto;
	private String direccion;
	private String cp;
	private String poblacion;
	private String provincia;
	private String email;

	private String fechaCompra;
	private String horaCompra;
	private String fechaEntrega;
	private String horaEntrega;
	private String medioTransporte;
	private String rutaVehiculo;
	private String idVehiculo;

	private BigDecimal importeTotal;
	private String observaciones;
	private int totalBultos;
	private BigDecimal totalPorte;

	@XmlElementWrapper(name = "bultos")
	@XmlElement(name = "bulto")
	private List<RutasBultosBoletaBean> listadoBultos;

	@XmlElementWrapper(name = "tipoBultos")
	@XmlElement(name = "tipoBulto")
	private List<RutasTipoBultoBean> listadoTiposBultos;

	private String localizador;
	
	private boolean direccionForzada;

	public String getUidTicketCzz() {
		return uidTicketCzz;
	}

	public void setUidTicketCzz(String uidTicketCzz) {
		this.uidTicketCzz = uidTicketCzz;
	}

	public String getCodTicketCzz() {
		return codTicketCzz;
	}

	public void setCodTicketCzz(String codTicketCzz) {
		this.codTicketCzz = codTicketCzz;
	}

	public int getTotalBultos() {
		return totalBultos;
	}

	public void setTotalBultos(int totalBultos) {
		this.totalBultos = totalBultos;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}

	public String getIdFidelizado() {
		return idFidelizado;
	}

	public void setIdFidelizado(String idFidelizado) {
		this.idFidelizado = idFidelizado;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(String fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public String getHoraCompra() {
		return horaCompra;
	}

	public void setHoraCompra(String horaCompra) {
		this.horaCompra = horaCompra;
	}

	public String getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(String fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public String getHoraEntrega() {
		return horaEntrega;
	}

	public void setHoraEntrega(String horaEntrega) {
		this.horaEntrega = horaEntrega;
	}

	public String getMedioTransporte() {
		return medioTransporte;
	}

	public void setMedioTransporte(String medioTransporte) {
		this.medioTransporte = medioTransporte;
	}

	public String getRutaVehiculo() {
		return rutaVehiculo;
	}

	public void setRutaVehiculo(String rutaVehiculo) {
		this.rutaVehiculo = rutaVehiculo;
	}

	public String getIdVehiculo() {
		return idVehiculo;
	}

	public void setIdVehiculo(String idVehiculo) {
		this.idVehiculo = idVehiculo;
	}

	public BigDecimal getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(BigDecimal importeTotal) {
		this.importeTotal = importeTotal;
	}

	public List<RutasBultosBoletaBean> getListadoBultos() {
		return listadoBultos;
	}

	public void setListadoBultos(List<RutasBultosBoletaBean> listadoBultos) {
		this.listadoBultos = listadoBultos;
	}

	public List<RutasTipoBultoBean> getListadoTiposBultos() {
		return listadoTiposBultos;
	}

	public void setListadoTiposBultos(List<RutasTipoBultoBean> listadoTiposBultos) {
		this.listadoTiposBultos = listadoTiposBultos;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public List<String> getObservacionesDividida() {
		return TextUtils.getInstance().divideLines(observaciones, 40, "\n");
	}

	public List<String> getListaBultosString() {
		String bultosTexto = "";
		for (RutasTipoBultoBean bulto : listadoTiposBultos) {
			if (bulto.getCantidad() >= 1) {
				bultosTexto = bultosTexto + bulto.getNombre() + ":" + bulto.getCantidad() + " ";
			}
		}
		return TextUtils.getInstance().divideLines(bultosTexto, 40, "\n");
	}

	public BigDecimal getTotalPorte() {
		return totalPorte;
	}

	public void setTotalPorte(BigDecimal totalPorte) {
		this.totalPorte = totalPorte;
	}

	public String getLocalizador() {
		return localizador;
	}

	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}

	public String getCajero() {
		return cajero;
	}

	public void setCajero(String cajero) {
		this.cajero = cajero;
	}

	
	public boolean isDireccionForzada() {
		return direccionForzada;
	}

	
	public void setDireccionForzada(boolean direccionForzada) {
		this.direccionForzada = direccionForzada;
	}

}
