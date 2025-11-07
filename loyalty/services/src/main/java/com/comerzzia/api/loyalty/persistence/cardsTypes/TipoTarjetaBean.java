package com.comerzzia.api.loyalty.persistence.cardsTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.core.model.i18n.I18NBean;


public class TipoTarjetaBean extends TipoTarjetaKey {

	private static final long serialVersionUID = -3026132176576640558L;
	
	public static final String CLASE_TIPOTARJETA= "F_TARJETAS_TIPOS_TBL";
	public static final String CLASE_DESTIPOTARJETA = "F_TARJETAS_TIPOS_TBL.DESTIPOTARJ";

	private String destipotarj;

    private Boolean permiteVincular;

    private String prefijo;

    private Short longitudTotal;

    private Boolean permitePago;

    private Boolean visibleEnPago;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
        
    //Traducciones
  	@XmlTransient
  	private boolean traduccionesDesTipoTarjetaCargadas = false;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDestipotarj() {
        return destipotarj;
    }

    public void setDestipotarj(String destipotarj) {
        this.destipotarj = destipotarj == null ? null : destipotarj.trim();
    }

    public Boolean getPermiteVincular() {
        return permiteVincular;
    }

    public void setPermiteVincular(Boolean permiteVincular) {
        this.permiteVincular = permiteVincular;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo == null ? null : prefijo.trim();
    }

    public Short getLongitudTotal() {
        return longitudTotal;
    }

    public void setLongitudTotal(Short longitudTotal) {
        this.longitudTotal = longitudTotal;
    }

    public Boolean getPermitePago() {
        return permitePago;
    }

    public void setPermitePago(Boolean permitePago) {
        this.permitePago = permitePago;
    }

    public Boolean getVisibleEnPago() {
        return visibleEnPago;
    }

    public void setVisibleEnPago(Boolean visibleEnPago) {
        this.visibleEnPago = visibleEnPago;
    }

    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------

	public List<I18NBean> getTraduccionesDesTipoTarjeta() {
		return getMapaTraducciones().get(CLASE_DESTIPOTARJETA);
	}

	public void setTraduccionesDesTipoTarjeta(List<I18NBean> traduccionesDesTipoTarjeta) {
		this.getMapaTraducciones().put(CLASE_DESTIPOTARJETA, traduccionesDesTipoTarjeta);
	}

	public boolean isTraduccionesDesTipoTarjetaCargadas() {
		return traduccionesDesTipoTarjetaCargadas;
	}

	public void setTraduccionesDesTipoTarjetaCargadas(
			boolean traduccionesDesTipoTarjetaCargadas) {
		this.traduccionesDesTipoTarjetaCargadas = traduccionesDesTipoTarjetaCargadas;
	}
    
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

}