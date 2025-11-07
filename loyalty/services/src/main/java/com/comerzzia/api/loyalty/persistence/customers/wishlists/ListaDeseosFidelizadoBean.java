package com.comerzzia.api.loyalty.persistence.customers.wishlists;

import java.util.Date;
import java.util.List;

import com.comerzzia.api.loyalty.persistence.customers.wishlists.details.ArticuloListaDeseosFidelizadoBean;

public class ListaDeseosFidelizadoBean extends ListaDeseosFidelizadoKey {
    
	private static final long serialVersionUID = 8197495946730583409L;

	private Long idFidelizado;

    private String nombre;

    private Date fecha;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    private List<ArticuloListaDeseosFidelizadoBean> listaArticulos;
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

	
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    public List<ArticuloListaDeseosFidelizadoBean> getListaArticulos() {
		return listaArticulos;
	}

	public void setListaArticulos(
			List<ArticuloListaDeseosFidelizadoBean> listaArticulos) {
		this.listaArticulos = listaArticulos;
	}
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}