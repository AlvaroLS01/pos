package com.comerzzia.dinosol.pos.services.payments.methods.types.siam.dto.response;

import java.math.BigDecimal;

public class SiamMensajeResponse {
	
	//Ej No DCC. 3OK5716300202341040618068      000000002900VT114033 4188202 1************7444 SC*3030A0000000031010 01 VISA AQUA DEBITO 0978CRE 1
	
	//Ej 	DCC. 3OK5716328005753040618068      000000009599VT115457 4966882 1************1070 SCP3030A0000000031010 00 Visa 0978CRE 2MAD000000108958***0,09***4,50***0,00
//	  msgRespuesta = "5OK4716328005753040618068      000000009599VT115457                          4966882 001************1070   SCP3030A0000000031010                  00  Visa                            0978CRE2MAD000000108958***0,09***4,50***0,00";

	//estado										//long 1	-->3
	protected String codigoRespuesta;				//long 2 	-->OK
	protected String centroAutorizador;				//long 1	-->5
	protected String secuencia;						//long 6	-->716300
	protected String autorizacion;					//long 6	-->202341
	protected String comercio;						//long 15	-->040618068      |Relleno de espacios a la derecha
	protected BigDecimal importe;					//long 12	-->000000002900
	protected String transaccion;					//long 2 	-->VT
	protected Integer hora;							//long 6	-->114033
	protected String nombreTitular;					//long 26	-->Fco Javier Garcia Lara    |Relleno de espacios a la derecha
	protected Integer binTarjeta;					//long 6	-->418820
	protected String indicadorTipoLectura;			//long 1 	-->2
	protected String relleno;						//long 1	--> _ |Espacio en blanco
	protected String tipoDispositivoContacless;		//long 2	--> __		|00/05/09/__ |Pueden ser espacios en blanco
	protected int identCentroProcesador;			//long 1	-->1		|1/2/3/_ | espacio en blanco es privado
	protected String panEnmascarado;				//long 19	-->************7444   |Relleno de espacios a la derecha
	protected String tarjetaEMV;					//long 1	-->S
	protected String modoEntradaDatos;				//long 1	-->C
	protected String modoVerificacionUsuario;		//long 1	-->*
	protected String ARC;							//long 4	-->3030
	protected String AID;							//long 32	-->A0000000031010                  |Relleno de espacios a la derecha
	protected String panSequenceNumber;				//long 4	-->01
	protected String applicationLabel;				//long 32	-->VISA AQUA DEBITO                |Relleno de espacios a la derecha
	protected String codigoMoneda;					//long 4	-->0978
	protected String indicadorCreDeb;				//long 3	-->CRE
	protected String transparencia;					//long 150	-->
	protected String DCC;							//long 1	-->1
	protected String monedaDCC;						//long 3	-->MAD
	protected BigDecimal importeDCC;				//long 12	-->000000108958
	protected String exchangeRate;					//long 7	-->***0,09
	protected String markUp;						//long 7	-->***4,50
	protected String commision;						//long 7	-->***0,00
	protected String entidad;						//long 20	-->
	protected String textoTicket;					//long 458	-->

	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}

	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}

	public String getCentroAutorizador() {
		return centroAutorizador;
	}

	public void setCentroAutorizador(String centroAutorizador) {
		this.centroAutorizador = centroAutorizador;
	}

	public String getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}

	public String getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

	public String getComercio() {
		return comercio;
	}

	public void setComercio(String comercio) {
		this.comercio = comercio;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getTransaccion() {
		return transaccion;
	}

	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion;
	}

	public Integer getHora() {
		return hora;
	}

	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public String getNombreTitular() {
		return nombreTitular;
	}

	public void setNombreTitular(String nombreTitular) {
		this.nombreTitular = nombreTitular;
	}

	public Integer getBinTarjeta() {
		return binTarjeta;
	}

	public void setBinTarjeta(Integer binTarjeta) {
		this.binTarjeta = binTarjeta;
	}

	public String getIndicadorTipoLectura() {
		return indicadorTipoLectura;
	}

	public void setIndicadorTipoLectura(String indicadorTipoLectura) {
		this.indicadorTipoLectura = indicadorTipoLectura;
	}

	public String getRelleno() {
		return relleno;
	}

	public void setRelleno(String relleno) {
		this.relleno = relleno;
	}

	public String getTipoDispositivoContacless() {
		return tipoDispositivoContacless;
	}

	public void setTipoDispositivoContacless(String tipoDispositivoContacless) {
		this.tipoDispositivoContacless = tipoDispositivoContacless;
	}

	public int getIdentCentroProcesador() {
		return identCentroProcesador;
	}

	public void setIdentCentroProcesador(int identCentroProcesador) {
		this.identCentroProcesador = identCentroProcesador;
	}

	public String getPanEnmascarado() {
		return panEnmascarado;
	}

	public void setPanEnmascarado(String panEnmascarado) {
		this.panEnmascarado = panEnmascarado;
	}

	public String getTarjetaEMV() {
		return tarjetaEMV;
	}

	public void setTarjetaEMV(String tarjetaEMV) {
		this.tarjetaEMV = tarjetaEMV;
	}

	public boolean isTarjetaEMV() {
		return tarjetaEMV.equals("S") ? true : false;
	}

	public String getModoEntradaDatos() {
		return modoEntradaDatos;
	}

	public void setModoEntradaDatos(String modoEntradaDatos) {
		this.modoEntradaDatos = modoEntradaDatos;
	}

	public String getModoVerificacionUsuario() {
		return modoVerificacionUsuario;
	}

	public void setModoVerificacionUsuario(String modoVerificacionUsuario) {
		this.modoVerificacionUsuario = modoVerificacionUsuario;
	}

	public String getARC() {
		return ARC;
	}

	public void setARC(String aRC) {
		ARC = aRC;
	}

	public String getAID() {
		return AID;
	}

	public void setAID(String aID) {
		AID = aID;
	}

	public String getPanSequenceNumber() {
		return panSequenceNumber;
	}

	public void setPanSequenceNumber(String panSequenceNumber) {
		this.panSequenceNumber = panSequenceNumber;
	}

	public String getApplicationLabel() {
		return applicationLabel;
	}

	public void setApplicationLabel(String applicationLabel) {
		this.applicationLabel = applicationLabel;
	}

	public String getCodigoMoneda() {
		return codigoMoneda;
	}

	public void setCodigoMoneda(String codigoMoneda) {
		this.codigoMoneda = codigoMoneda;
	}

	public String getIndicadorCreDeb() {
		return indicadorCreDeb;
	}

	public void setIndicadorCreDeb(String indicadorCreDeb) {
		this.indicadorCreDeb = indicadorCreDeb;
	}

	public String getTransparencia() {
		return transparencia;
	}

	public void setTransparencia(String transparencia) {
		this.transparencia = transparencia;
	}

	public String getDCC() {
		return DCC;
	}

	public void setDCC(String dCC) {
		DCC = dCC;
	}

	public boolean isDCC() {
		return DCC.equals("2") ? true : false; // 1->NO 2->SI 3->EUR ??????
	}

	public String getMonedaDCC() {
		return monedaDCC;
	}

	public void setMonedaDCC(String monedaDCC) {
		this.monedaDCC = monedaDCC;
	}

	public BigDecimal getImporteDCC() {
		return importeDCC;
	}

	public void setImporteDCC(BigDecimal importeDCC) {
		this.importeDCC = importeDCC;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getMarkUp() {
		return markUp;
	}

	public void setMarkUp(String markUp) {
		this.markUp = markUp;
	}

	public String getCommision() {
		return commision;
	}

	public void setCommision(String commision) {
		this.commision = commision;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getTextoTicket() {
		return textoTicket;
	}

	public void setTextoTicket(String textoTicket) {
		this.textoTicket = textoTicket;
	}

}