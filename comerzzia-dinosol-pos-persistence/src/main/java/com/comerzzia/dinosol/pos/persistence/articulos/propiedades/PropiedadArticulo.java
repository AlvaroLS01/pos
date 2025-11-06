package com.comerzzia.dinosol.pos.persistence.articulos.propiedades;

public class PropiedadArticulo extends PropiedadArticuloKey {
    private String valor;

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor == null ? null : valor.trim();
    }
}