package com.comerzzia.api.loyalty.persistence.customers.wishlists.details;

import java.math.BigDecimal;

public class ArticuloListaDeseosFidelizadoBean extends ArticuloListaDeseosFidelizadoKey {
    
	private static final long serialVersionUID = 8928065561065234457L;

	private BigDecimal cantidad;

    private String unidadMedida;

    private BigDecimal cantidadMedida;
    
    
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    private String desArt;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida == null ? null : unidadMedida.trim();
    }

    public BigDecimal getCantidadMedida() {
        return cantidadMedida;
    }

    public void setCantidadMedida(BigDecimal cantidadMedida) {
        this.cantidadMedida = cantidadMedida;
    }

    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public String getDesArt() {
		return desArt;
	}

	public void setDesArt(String desArt) {
		this.desArt = desArt;
	}
	
	public boolean equals(Object obj){
		return super.equals(obj);
	}
	
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}