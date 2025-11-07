package com.comerzzia.api.loyalty.persistence.cards;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comerzzia.api.core.persistence.util.SimpleDateAdapter;
import com.comerzzia.core.servicios.sesion.IDatosSesion;

import io.swagger.v3.oas.annotations.media.Schema;


@XmlAccessorType(XmlAccessType.FIELD)
public class Card extends CardKey {
	private static final long serialVersionUID = 2669623094264848295L;

	private String numeroTarjeta;

	@Schema(type = "string", format = "date")
    @XmlJavaTypeAdapter(SimpleDateAdapter.class)
    private Date fechaEmision;

    private Long idCuentaTarjeta;

    private Date fechaActivacion;

    private Date fechaUltimoUso;

    private Date fechaBaja;

    private Long idFidelizado;

    private Date fechaAsignacionFidelizado;

    private String codTipoTarj;
        
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------
    public Card() {
    	
    }
    
	public Card(IDatosSesion datosSesion) {
		super(datosSesion);
	}

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta == null ? null : numeroTarjeta.trim();
    }
    
    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Long getIdCuentaTarjeta() {
        return idCuentaTarjeta;
    }

    public void setIdCuentaTarjeta(Long idCuentaTarjeta) {
        this.idCuentaTarjeta = idCuentaTarjeta;
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

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }

    public Date getFechaAsignacionFidelizado() {
        return fechaAsignacionFidelizado;
    }

    public void setFechaAsignacionFidelizado(Date fechaAsignacionFidelizado) {
        this.fechaAsignacionFidelizado = fechaAsignacionFidelizado;
    }

    public String getCodTipoTarj() {
        return codTipoTarj;
    }

    public void setCodTipoTarj(String codTipoTarj) {
        this.codTipoTarj = codTipoTarj == null ? null : codTipoTarj.trim();
    }

    
    //INICIO M�TODOS PERSONALIZADOS--------------------------------------------
	
	public boolean isActivo() {
		return fechaBaja == null || (fechaBaja != null && fechaBaja.after(new Date()));
	}
	
    
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((numeroTarjeta == null) ? 0 : numeroTarjeta.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Card other = (Card) obj;
	    if (numeroTarjeta == null) {
		    if (other.numeroTarjeta != null)
			    return false;
	    }
	    else if (!numeroTarjeta.equals(other.numeroTarjeta))
		    return false;
	    return true;
    }
			 
    //FIN M�TODOS PERSONALIZADOS-----------------------------------------------
	
	@Override
	public String toString() {
		return "Card [numeroTarjeta=" + numeroTarjeta + ", fechaEmision=" + fechaEmision + ", idCuentaTarjeta="
				+ idCuentaTarjeta + ", fechaActivacion=" + fechaActivacion + ", fechaUltimoUso=" + fechaUltimoUso
				+ ", fechaBaja=" + fechaBaja + ", idFidelizado=" + idFidelizado + ", fechaAsignacionFidelizado="
				+ fechaAsignacionFidelizado + ", codTipoTarj=" + codTipoTarj + "]";
	}

}