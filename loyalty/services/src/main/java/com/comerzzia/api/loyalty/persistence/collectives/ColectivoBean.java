package com.comerzzia.api.loyalty.persistence.collectives;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.api.loyalty.persistence.customers.collectives.ColectivosFidelizadoBean;
import com.comerzzia.core.model.i18n.I18NBean;

public class ColectivoBean extends ColectivoKey {

	private static final long serialVersionUID = 2070568982631824132L;
	
	public static final String CLASE_COLECTIVO = "D_COLECTIVOS_TBL";
	public static final String CLASE_DESCOL = "D_COLECTIVOS_TBL.DES_COLECTIVO";

	private String desColectivo;

    private String ambitoAplicacion;

    private String codtipcolectivo;

    private String destipcolectivo;
    
    private String privado;
    
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
	
    private boolean fidelizadosCargados = false;
	
    private List<ColectivosFidelizadoBean> fidelizados = new ArrayList<ColectivosFidelizadoBean>();
    
    private boolean campsMarketingCargados = false;
        
    //Traducciones
  	@XmlTransient
  	private boolean traduccionesDesColCargadas = false;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDesColectivo() {
        return desColectivo;
    }

    public void setDesColectivo(String desColectivo) {
        this.desColectivo = desColectivo == null ? null : desColectivo.trim();
    }

    public String getAmbitoAplicacion() {
        return ambitoAplicacion;
    }

    public void setAmbitoAplicacion(String ambitoAplicacion) {
        this.ambitoAplicacion = ambitoAplicacion == null ? null : ambitoAplicacion.trim();
    }

    public String getCodtipcolectivo() {
        return codtipcolectivo;
    }

    public void setCodtipcolectivo(String codtipcolectivo) {
        this.codtipcolectivo = codtipcolectivo == null ? null : codtipcolectivo.trim();
    }

    public String getDestipcolectivo() {
        return destipcolectivo;
    }

    public void setDestipcolectivo(String destipcolectivo) {
        this.destipcolectivo = destipcolectivo == null ? null : destipcolectivo.trim();
    }

    public String getPrivado() {
        return privado;
    }

    public void setPrivado(String privado) {
        this.privado = privado == null ? null : privado.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public boolean isFidelizadosCargados() {
		return fidelizadosCargados;
	}

	public void setFidelizadosCargados(boolean fidelizadosCargados) {
		this.fidelizadosCargados = fidelizadosCargados;
	}

	public List<ColectivosFidelizadoBean> getFidelizados() {
		return fidelizados;
	}

	public void setFidelizados(List<ColectivosFidelizadoBean> fidelizados) {
		this.fidelizados = fidelizados;
	}

	public boolean isCampsMarketingCargados() {
		return campsMarketingCargados;
	}

	public void setCampsMarketingCargados(boolean campsMarketingCargados) {
		this.campsMarketingCargados = campsMarketingCargados;
	}
	
	public List<I18NBean> getTraduccionesDesCol() {
		return getMapaTraducciones().get(CLASE_DESCOL);
	}

	public void setTraduccionesDesCol(List<I18NBean> traduccionesDesCol) {
		this.getMapaTraducciones().put(CLASE_DESCOL, traduccionesDesCol);
	}

	public boolean isTraduccionesDesColCargadas() {
		return traduccionesDesColCargadas;
	}

	public void setTraduccionesDesColCargadas(boolean traduccionesDesColCargadas) {
		this.traduccionesDesColCargadas = traduccionesDesColCargadas;
	}
	
	
	
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}