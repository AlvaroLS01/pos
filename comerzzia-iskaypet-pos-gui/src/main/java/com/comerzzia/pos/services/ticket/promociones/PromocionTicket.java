/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */


package com.comerzzia.pos.services.ticket.promociones;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.xml.MapAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="promocion")
@Component
@Scope("prototype")
public class PromocionTicket extends PromocionTicketAbstract {
    @XmlElement (name = "idpromocion")
    protected Long idPromocion;
    
    @XmlElement (name = "tipopromocion")
    protected Long idTipoPromocion;

    @XmlElement (name = "tipo_dto")
    protected Long tipoDescuento;
    
    @XmlElement (name = "puntos")
    protected Integer puntos;
    
    @XmlElement (name = "texto_promocion")
    protected String textoPromocion;

    @XmlElement (name = "importe_total_ahorro")
    protected BigDecimal importeTotalAhorro;

	@XmlTransient
    protected Promocion promocion;
	
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Object> adicionales;
    
    protected String acceso;
    protected String codAcceso;
    protected Boolean exclusiva;
    protected String descripcionPromocion;
    
    public PromocionTicket() {
    }
    
    public PromocionTicket(Promocion promocion) {
        idPromocion = promocion.getIdPromocion();
        importeTotalAhorro = BigDecimal.ZERO;
        idTipoPromocion = promocion.getIdTipoPromocion();
        tipoDescuento = promocion.getTipoDto();
        descripcionPromocion = promocion.getDescripcion();
        this.promocion = promocion;
       
        textoPromocion = "";
        puntos = 0;
        exclusiva = promocion.getExclusiva();
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getImporteTotalAhorro()
	 */
    @Override
	public BigDecimal getImporteTotalAhorro() {
        return importeTotalAhorro;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getImporteTotalAhorroAsString()
	 */
    @Override
	public String getImporteTotalAhorroAsString(){
        return FormatUtil.getInstance().formateaImporte(importeTotalAhorro);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setImporteTotalAhorro(java.math.BigDecimal)
	 */
    @Override
	public void setImporteTotalAhorro(BigDecimal importeTotalAhorro) {
        this.importeTotalAhorro = importeTotalAhorro;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getIdPromocion()
	 */
    @Override
	public Long getIdPromocion() {
        return idPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setIdPromocion(java.lang.Long)
	 */
    @Override
	public void setIdPromocion(Long idPromocion) {
        this.idPromocion = idPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getIdTipoPromocion()
	 */
    @Override
	public Long getIdTipoPromocion() {
        return idTipoPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setIdTipoPromocion(java.lang.Long)
	 */
    @Override
	public void setIdTipoPromocion(Long idTipoPromocion) {
        this.idTipoPromocion = idTipoPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getTipoDescuento()
	 */
    @Override
	public Long getTipoDescuento() {
        return tipoDescuento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setTipoDescuento(java.lang.Long)
	 */
    @Override
	public void setTipoDescuento(Long tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getPuntos()
	 */
    @Override
	public Integer getPuntos() {
        return puntos;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setPuntos(java.lang.Integer)
	 */
    @Override
	public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getTextoPromocion()
	 */
    @Override
	public String getTextoPromocion() {
    	if(textoPromocion == null || textoPromocion.isEmpty()){
    		return descripcionPromocion;
    	}
        return textoPromocion;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setTextoPromocion(java.lang.String)
	 */
    @Override
	public void setTextoPromocion(String textoPromocion) {
        this.textoPromocion = textoPromocion;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getIdPromocionAsString()
	 */
    @Override
	public String getIdPromocionAsString(){
        return String.valueOf(idPromocion);
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getAcceso()
	 */
    @Override
	public String getAcceso() {
        return acceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setAcceso(java.lang.String)
	 */
    @Override
	public void setAcceso(String acceso) {
        this.acceso = acceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#getCodAcceso()
	 */
    @Override
	public String getCodAcceso() {
        return codAcceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setCodAcceso(java.lang.String)
	 */
    @Override
	public void setCodAcceso(String codAcceso) {
        this.codAcceso = codAcceso;
    }

    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#isExclusiva()
	 */
    @Override
	public boolean isExclusiva() {
        return exclusiva;
    }
    
	/* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#setExclusiva(boolean)
	 */
	@Override
	public void setExclusiva(boolean exclusiva) {
        this.exclusiva = exclusiva;
    }
    
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#isDescuentoMenosMargen()
	 */
    @Override
	public boolean isDescuentoMenosMargen(){
        return getTipoDescuento().equals(Promocion.TIPO_DTO_MENOS_MARGEN);
    }
    /* (non-Javadoc)
	 * @see com.comerzzia.pos.services.ticket.promociones.IPromocionTicket#isDescuentoMenosIngreso()
	 */
    @Override
	public boolean isDescuentoMenosIngreso(){
        return getTipoDescuento().equals(Promocion.TIPO_DTO_MENOS_INGRESO);
    }
    
    public Promocion getPromocion() {
		return promocion;
	}

	public void setPromocion(Promocion promocion) {
		this.promocion = promocion;
	}

	public Map<String, Object> getAdicionales() {
		return adicionales;
	}

	public void setAdicionales(Map<String, Object> adicionales) {
		this.adicionales = adicionales;
	}
	
	public void putAdicional(String key, Object value) {
		if(this.adicionales == null) {
			this.adicionales = new HashMap<String, Object>();
		}
		
		this.adicionales.put(key, value);
	}
}
