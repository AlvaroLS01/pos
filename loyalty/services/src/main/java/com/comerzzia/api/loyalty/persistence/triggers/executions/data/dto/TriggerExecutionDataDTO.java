package com.comerzzia.api.loyalty.persistence.triggers.executions.data.dto;

import java.util.Date;

import com.comerzzia.api.loyalty.persistence.triggers.executions.data.TriggerExecutionDataKey;

public class TriggerExecutionDataDTO extends TriggerExecutionDataKey{
	
	protected String nombre;
	
	protected String apellidos;
	
	protected String domicilio;
	
	protected String poblacion;
	
	protected String localidad;
	
	protected String provincia;
	
	protected String cp;
	
	protected String codPais;
	
	protected String documento;
	
	private String observaciones;

    private Date fechaNacimiento;

    private String sexo;

    private String codEstCivil;

    private Date fechaAlta;

    private Date fechaModificacion;

    private Date fechaBaja;

    private String codFidelizado;

    private String codTipoIden;
    
    private boolean activo = true;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
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

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getCodEstCivil() {
		return codEstCivil;
	}

	public void setCodEstCivil(String codEstCivil) {
		this.codEstCivil = codEstCivil;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public String getCodFidelizado() {
		return codFidelizado;
	}

	public void setCodFidelizado(String codFidelizado) {
		this.codFidelizado = codFidelizado;
	}

	public String getCodTipoIden() {
		return codTipoIden;
	}

	public void setCodTipoIden(String codTipoIden) {
		this.codTipoIden = codTipoIden;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	
}
