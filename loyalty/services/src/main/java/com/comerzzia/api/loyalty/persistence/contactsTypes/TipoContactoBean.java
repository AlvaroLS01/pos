package com.comerzzia.api.loyalty.persistence.contactsTypes;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.comerzzia.core.model.i18n.I18NBean;

public class TipoContactoBean extends TipoContactoKey {

	private static final long serialVersionUID = -669998867064670881L;
	
	public static final String CLASE_TIPOCON = "D_TIPO_CONTACTO_TBL";
	public static final String CLASE_DESTIPOCON = "D_TIPO_CONTACTO_TBL.DESTIPOCON";
	public static final String CLASE_CODTIPOCON = "D_TIPO_CONTACTO_TBL.CODTIPOCON";

	private String desTipoCon;

    private String puedeRecibirNotificaciones;

    private Long idBuzon;

    private String visible;

    private Short orden;

    private String protocolo;

    private String valorTest;
    
    private String buzonDescripcion;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    public static final String TIPO_EMAIL = "EMAIL";
	public static final String TIPO_GOOGLE_GCM = "GOOGLE_GCM";
	
	//Traducciones
	@XmlTransient
	private boolean traduccionesDesTipoConCargadas = false;
	@XmlTransient
	private boolean traduccionesCodTipoConCargadas = false;
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public String getDesTipoCon() {
        return desTipoCon;
    }

    public void setDesTipoCon(String desTipoCon) {
        this.desTipoCon = desTipoCon == null ? null : desTipoCon.trim();
    }

    public String getPuedeRecibirNotificaciones() {
		return puedeRecibirNotificaciones;
	}

	public void setPuedeRecibirNotificaciones(String puedeRecibirNotificaciones) {
		this.puedeRecibirNotificaciones = puedeRecibirNotificaciones;
	}

    public Long getIdBuzon() {
        return idBuzon;
    }

    public void setIdBuzon(Long idBuzon) {
        this.idBuzon = idBuzon;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible == null ? null : visible.trim();
    }

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo == null ? null : protocolo.trim();
    }

    public String getValorTest() {
		return valorTest;
	}

	public void setValorTest(String valorTest) {
		this.valorTest = valorTest;
	}

	public String getBuzonDescripcion() {
        return buzonDescripcion;
    }

    public void setBuzonDescripcion(String buzonDescripcion) {
        this.buzonDescripcion = buzonDescripcion == null ? null : buzonDescripcion.trim();
    }
    
    //INICIO MÉTODOS PERSONALIZADOS--------------------------------------------
    
    public List<I18NBean> getTraduccionesDesTipoCon() {
		return getMapaTraducciones().get(CLASE_DESTIPOCON);
	}

	public void setTraduccionesDesTipoCon(List<I18NBean> traduccionesDesTipoCon) {
		this.getMapaTraducciones().put(CLASE_DESTIPOCON, traduccionesDesTipoCon);
	}

	public List<I18NBean> getTraduccionesCodTipoCon() {
		return getMapaTraducciones().get(CLASE_CODTIPOCON);
	}

	public void setTraduccionesCodTipoCon(List<I18NBean> traduccionesCodTipoCon) {
		this.getMapaTraducciones().put(CLASE_CODTIPOCON, traduccionesCodTipoCon);
	}

	public boolean isTraduccionesDesTipoConCargadas() {
		return traduccionesDesTipoConCargadas;
	}

	public void setTraduccionesDesTipoConCargadas(
			boolean traduccionesDesTipoConCargadas) {
		this.traduccionesDesTipoConCargadas = traduccionesDesTipoConCargadas;
	}
	
	public boolean isTraduccionesCodTipoConCargadas() {
		return traduccionesCodTipoConCargadas;
	}

	public void setTraduccionesCodTipoConCargadas(
			boolean traduccionesCodTipoConCargadas) {
		this.traduccionesCodTipoConCargadas = traduccionesCodTipoConCargadas;
	}
	
    //FIN MÉTODOS PERSONALIZADOS-----------------------------------------------

	public String toString(){
    	return getDesTipoCon();
    }

	

}