package com.comerzzia.dinosol.pos.persistence.enviosdomicilio;

public class RutasBultosBoletaBean {

	private String nombre;
	private int cantidad;
	private String codBar;
	private int matriculaControl;
	private String numeroBulto;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public String getCodBar() {
		return codBar;
	}
	public void setCodBar(String codBar) {
		this.codBar = codBar;
	}
	public int getMatriculaControl() {
		return matriculaControl;
	}
	public void setMatriculaControl(int matriculaControl) {
		this.matriculaControl = matriculaControl;
	}
	public String getNumeroBulto() {
		return numeroBulto;
	}
	public void setNumeroBulto(String numeroBulto) {
		this.numeroBulto = numeroBulto;
	}
}
