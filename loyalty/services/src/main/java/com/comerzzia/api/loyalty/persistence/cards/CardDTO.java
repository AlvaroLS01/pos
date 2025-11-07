package com.comerzzia.api.loyalty.persistence.cards;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comerzzia.api.core.persistence.util.SimpleDateAdapter;
import com.comerzzia.api.loyalty.persistence.accounts.Account;
import com.comerzzia.api.loyalty.persistence.cardsTypes.TipoTarjetaBean;
import com.comerzzia.api.loyalty.persistence.customers.FidelizadoBean;
import com.comerzzia.core.servicios.sesion.IDatosSesion;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;


@XmlAccessorType(XmlAccessType.FIELD)
public class CardDTO extends CardKey {
	private static final long serialVersionUID = 7207930313931788546L;

	private String numeroTarjeta;

	@Schema(type = "string", format = "date")
    @XmlJavaTypeAdapter(SimpleDateAdapter.class)
    private Date fechaEmision;

    private Date fechaActivacion;

    private Date fechaUltimoUso;

    private Date fechaBaja;

    private Date fechaAsignacionFidelizado;	
    
    @JsonInclude(Include.NON_NULL)
    private Account cuenta;
    
    @JsonInclude(Include.NON_NULL)
    private TipoTarjetaBean tipoTarjeta;
    
    @JsonInclude(Include.NON_NULL)
    private FidelizadoBean fidelizado;

    public CardDTO() {
    	
    }
    
	public CardDTO(IDatosSesion datosSesion) {
		super(datosSesion);
	}

	
	public String getNumeroTarjeta() {
		return numeroTarjeta;
	}

	public void setNumeroTarjeta(String numeroTarjeta) {
		this.numeroTarjeta = numeroTarjeta;
	}

	public Date getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public Date getFechaActivacion() {
		return fechaActivacion;
	}

	public void setFechaActivacion(Date fechaActivacion) {
		this.fechaActivacion = fechaActivacion;
	}

	public Date getFechaUltimoUso() {
		return fechaUltimoUso;
	}

	public void setFechaUltimoUso(Date fechaUltimoUso) {
		this.fechaUltimoUso = fechaUltimoUso;
	}

	public Date getFechaBaja() {
		return fechaBaja;
	}

	public void setFechaBaja(Date fechaBaja) {
		this.fechaBaja = fechaBaja;
	}

	public Date getFechaAsignacionFidelizado() {
		return fechaAsignacionFidelizado;
	}

	public void setFechaAsignacionFidelizado(Date fechaAsignacionFidelizado) {
		this.fechaAsignacionFidelizado = fechaAsignacionFidelizado;
	}

	public Account getCuenta() {
		return cuenta;
	}

	public void setCuenta(Account cuenta) {
		this.cuenta = cuenta;
	}

	public TipoTarjetaBean getTipoTarjeta() {
		return tipoTarjeta;
	}

	public void setTipoTarjeta(TipoTarjetaBean tipoTarjeta) {
		this.tipoTarjeta = tipoTarjeta;
	}

	public FidelizadoBean getFidelizado() {
		return fidelizado;
	}

	public void setFidelizado(FidelizadoBean fidelizado) {
		this.fidelizado = fidelizado;
	}
			 
    //FIN M�TODOS PERSONALIZADOS-----------------------------------------------

}