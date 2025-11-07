package com.comerzzia.api.loyalty.persistence.accounts;

import java.util.Date;

import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class Account extends AccountKey {

	private static final long serialVersionUID = 7625207540432277626L;

	private Integer estado;

    private Double saldo;

    private Date fechaActualizacion;

    private Double saldoProvisional;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
   
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------
	public Account() {	
	}
	
	public Account(IDatosSesion datosSesion) {
		super(datosSesion);
	}

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Double getSaldoProvisional() {
        return saldoProvisional;
    }

    public void setSaldoProvisional(Double saldoProvisional) {
        this.saldoProvisional = saldoProvisional;
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
	    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}