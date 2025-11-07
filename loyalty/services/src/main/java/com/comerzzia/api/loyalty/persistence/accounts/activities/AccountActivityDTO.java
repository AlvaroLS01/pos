package com.comerzzia.api.loyalty.persistence.accounts.activities;

public class AccountActivityDTO extends AccountActivity{

	private static final long serialVersionUID = 4503015323183471425L;

	
	private String numeroTarjeta;
	private String codTipoTarj;
	private String desTipoTarj;
	private String apellidosFidelizado;
	private String nombreFidelizado;
	private String codTipoDocumento;
	private String desTipoDocumento;
	private String codTicket;
	
	public String getNumeroTarjeta() {
   		return numeroTarjeta;
   	}

   	public void setNumeroTarjeta(String numeroTarjeta) {
   		this.numeroTarjeta = numeroTarjeta;
   	}
   	
   	public String getNombreFidelizado() {
   		return nombreFidelizado;
   	}


   	public void setNombreFidelizado(String nombreFidelizado) {
   		this.nombreFidelizado = (nombreFidelizado != null) ? nombreFidelizado : "";
   	}


   	public String getApellidosFidelizado() {
   		return apellidosFidelizado;
   	}

   	public void setApellidosFidelizado(String apellidosFidelizado) {
   		this.apellidosFidelizado = (apellidosFidelizado != null) ? apellidosFidelizado : "";
   	}
   	
   	public String getNombreCompletoFidelizado(){
   		return (this.nombreFidelizado != null ?this.nombreFidelizado : "") + " " + (this.apellidosFidelizado != null ?this.apellidosFidelizado :"");
   	}
   	
   	public String getCodTipoTarj() {
		return codTipoTarj;
	}

	public void setCodTipoTarj(String codTipoTarj) {
		this.codTipoTarj = codTipoTarj;
	}

	public String getDesTipoTarj() {
		return desTipoTarj;
	}

	public void setDesTipoTarj(String desTipoTarj) {
		this.desTipoTarj = desTipoTarj;
	}

	public String getCodTipoDocumento() {
		return codTipoDocumento;
	}

	public void setCodTipoDocumento(String codTipoDocumento) {
		this.codTipoDocumento = codTipoDocumento;
	}

	public String getDesTipoDocumento() {
		return desTipoDocumento;
	}

	public void setDesTipoDocumento(String desTipoDocumento) {
		this.desTipoDocumento = desTipoDocumento;
	}

	public String getCodTicket() {
		return codTicket;
	}

	public void setCodTicket(String codTicket) {
		this.codTicket = codTicket;
	}
	

	@Override
       public String toString() {
   	    return "idCuentaMovimiento = " + super.getIdCuentaMovimiento() + ", idCuentaTarjeta = " + getIdCuentaTarjeta() + ", fecha = " + getFecha() + ", idTarjeta = " + getIdTarjeta() + ", idUsuario = " + getIdUsuario()
   	            + ", documento = " + getDocumento() + ", concepto = " + getConcepto() + ", entrada = " + getEntrada() + ", salida = " + getSalida() + ", fechaProceso = " + getFechaProceso() + ", numeroTarjeta = " + numeroTarjeta
   	            + ", codtipo = " + codTipoTarj + ", estadoMovimiento = " + getEstadoMovimiento() + ", uidTransaccion = " + getUidTransaccion();
       }

}
