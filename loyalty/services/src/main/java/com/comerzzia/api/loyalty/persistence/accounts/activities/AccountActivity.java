/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.api.loyalty.persistence.accounts.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.comerzzia.core.servicios.sesion.IDatosSesion;

public class AccountActivity extends AccountActivityKey {
	
	public static Integer MOVIMIENTO_ANULADO = -1;
	public static Integer MOVIMIENTO_PROVISIONAL = 0;
	public static Integer MOVIMIENTO_DEFINITIVO  = 1;

	private static final long serialVersionUID = -1483889365760816482L;

	private Long idCuentaTarjeta;

    private Date fecha;

    private Long idTarjeta;

    private Long idUsuario;

    private String documento;

    private String concepto;

    private Double entrada;

    private Double salida;

    private Date fechaProceso;

    private Integer estadoMovimiento;

    private String uidTransaccion;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------

    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------
	
	public AccountActivity() {
		
	}
	
	public AccountActivity(IDatosSesion datosSesion) {
		super(datosSesion);
	}


    public Long getIdCuentaTarjeta() {
        return idCuentaTarjeta;
    }

    public void setIdCuentaTarjeta(Long idCuentaTarjeta) {
        this.idCuentaTarjeta = idCuentaTarjeta;
    }

    public Date getFecha() {
        return fecha;
    }
    
    public String getFechaAsString(){
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	String fechaAsString = format.format(fecha);

    	return fechaAsString;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(Long idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento == null ? null : documento.trim();
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto == null ? null : concepto.trim();
    }

    public Double getEntrada() {
        return entrada;
    }

    public void setEntrada(Double entrada) {
        this.entrada = entrada;
    }

    public Double getSalida() {
        return salida;
    }

    public void setSalida(Double salida) {
        this.salida = salida;
    }

    public Date getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }

    public Integer getEstadoMovimiento() {
        return estadoMovimiento;
    }

    public void setEstadoMovimiento(Integer estadoMovimiento) {
        this.estadoMovimiento = estadoMovimiento;
    }

    public String getUidTransaccion() {
        return uidTransaccion;
    }

    public void setUidTransaccion(String uidTransaccion) {
        this.uidTransaccion = uidTransaccion == null ? null : uidTransaccion.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------

       
       //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}