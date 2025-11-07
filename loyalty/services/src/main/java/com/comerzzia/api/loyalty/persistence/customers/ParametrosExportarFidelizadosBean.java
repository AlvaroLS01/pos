package com.comerzzia.api.loyalty.persistence.customers;

import java.util.Date;
import java.util.List;

import com.comerzzia.core.util.base.ParametrosBuscarBean;

public class ParametrosExportarFidelizadosBean extends ParametrosBuscarBean{

	private static final long serialVersionUID = -635105746509286609L;

	private Date fechaNacimientoDesde, fechaNacimientoHasta;
	
	private String sexo;
	private String poblacion;
	private String localidad;
	private String provincia;
	private String cp;
	private String codPais, desPais;
	private List<String> lstEtiquetasDisponibles;
	private List<String> lstEtiquetasSeleccionadas;
	private String operadorEtiquetas;
	private String codColectivo, desColectivo;
	private boolean permiteNotificaciones, checkFechaNacimientoDesde, checkFechaNacimientoHasta;
	private String uidActividad;
	
	
	public Date getFechaNacimientoDesde() {
		return fechaNacimientoDesde;
	}
	public void setFechaNacimientoDesde(Date fechaNacimientoDesde) {
		this.fechaNacimientoDesde = fechaNacimientoDesde;
	}
	public Date getFechaNacimientoHasta() {
		return fechaNacimientoHasta;
	}
	public void setFechaNacimientoHasta(Date fechaNacimientoHasta) {
		this.fechaNacimientoHasta = fechaNacimientoHasta;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getPoblacion() {
		return poblacion;
	}
	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getCodPais() {
		return codPais;
	}
	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}
	public String getDesPais() {
		return desPais;
	}
	public void setDesPais(String desPais) {
		this.desPais = desPais;
	}
	public List<String> getLstEtiquetasDisponibles() {
		return lstEtiquetasDisponibles;
	}
	public void setLstEtiquetasDisponibles(List<String> lstEtiquetasDisponibles) {
		this.lstEtiquetasDisponibles = lstEtiquetasDisponibles;
	}
	public String getCodColectivo() {
		return codColectivo;
	}
	public void setCodColectivo(String codColectivo) {
		this.codColectivo = codColectivo;
	}
	public String getDesColectivo() {
		return desColectivo;
	}
	public void setDesColectivo(String desColectivo) {
		this.desColectivo = desColectivo;
	}
	public boolean isPermiteNotificaciones() {
		return permiteNotificaciones;
	}
	public void setPermiteNotificaciones(boolean permiteNotificaciones) {
		this.permiteNotificaciones = permiteNotificaciones;
	}
	public boolean isCheckFechaNacimientoDesde() {
		return checkFechaNacimientoDesde;
	}
	public void setCheckFechaNacimientoDesde(boolean checkFechaNacimientoDesde) {
		this.checkFechaNacimientoDesde = checkFechaNacimientoDesde;
	}
	public boolean isCheckFechaNacimientoHasta() {
		return checkFechaNacimientoHasta;
	}
	public void setCheckFechaNacimientoHasta(boolean checkFechaNacimientoHasta) {
		this.checkFechaNacimientoHasta = checkFechaNacimientoHasta;
	}
	public String getOperadorEtiquetas() {
		return operadorEtiquetas;
	}
	public void setOperadorEtiquetas(String operadorEtiquetas) {
		this.operadorEtiquetas = operadorEtiquetas;
	}
	public List<String> getLstEtiquetasSeleccionadas() {
		return lstEtiquetasSeleccionadas;
	}
	public void setLstEtiquetasSeleccionadas(
			List<String> lstEtiquetasSeleccionadas) {
		this.lstEtiquetasSeleccionadas = lstEtiquetasSeleccionadas;
	}
	public String getUidActividad() {
		return uidActividad;
	}
	public void setUidActividad(String uidActividad) {
		this.uidActividad = uidActividad;
	}
	
}
