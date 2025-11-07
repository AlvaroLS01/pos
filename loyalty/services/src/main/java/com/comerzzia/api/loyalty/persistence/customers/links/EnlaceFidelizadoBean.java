package com.comerzzia.api.loyalty.persistence.customers.links;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.comerzzia.core.util.base.MantenimientoBean;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "enlaceFidelizado")
public class EnlaceFidelizadoBean extends MantenimientoBean {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7722370746949061577L;

	private String uidActividad;

    private String idClase;

    private String idObjeto;

    private Long idFidelizado;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getIdClase() {
        return idClase;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase == null ? null : idClase.trim();
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto == null ? null : idObjeto.trim();
    }

    public Long getIdFidelizado() {
        return idFidelizado;
    }

    public void setIdFidelizado(Long idFidelizado) {
        this.idFidelizado = idFidelizado;
    }

	@Override
	protected void initNuevoBean() {}
}