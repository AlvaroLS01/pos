package com.comerzzia.api.loyalty.persistence.customers.collectives;

public class ColectivosFidelizadoBean extends ColectivosFidelizadoKey{

	private static final long serialVersionUID = 840590466187487071L;
	
	private String desColectivo;
	private String codtipcolectivo;
	private String destipcolectivo;
	private Boolean privado;
	private String imagenColectivo="";
	private String textoColectivo="";
	
	
	public String getDesColectivo() {
		return desColectivo;
	}
	public void setDesColectivo(String desColectivo) {
		this.desColectivo = desColectivo;
	}
	public String getCodtipcolectivo() {
		return codtipcolectivo;
	}
	public void setCodtipcolectivo(String codtipcolectivo) {
		this.codtipcolectivo = codtipcolectivo;
	}
	public String getDestipcolectivo() {
		return destipcolectivo;
	}
	public void setDestipcolectivo(String destipcolectivo) {
		this.destipcolectivo = destipcolectivo;
	}
	public Boolean getPrivado() {
		return privado;
	}
	public void setPrivado(Boolean privado) {
		this.privado = privado;
	}
	public String getImagenColectivo() {
		return imagenColectivo;
	}
	public void setImagenColectivo(String imagenColectivo) {
		this.imagenColectivo = imagenColectivo;
	}
	public String getTextoColectivo() {
		return textoColectivo;
	}
	public void setTextoColectivo(String textoColectivo) {
		this.textoColectivo = textoColectivo;
	}

}
