package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoNxMBean;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaCandidataTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

/**
 * 	ISK-329 GAP 156 NxM Combinado
 *
 */
@Component
@Primary
@Scope("prototype")
public class IskaypetPromocionDescuentoNxMBean extends PromocionDescuentoNxMBean {
	
	protected static final Logger log = Logger.getLogger(IskaypetPromocionDescuentoNxMBean.class);
	
	protected GrupoComponentePromoBean aplicacion;
	protected CondicionPrincipalPromoBean condiciones;
	

	@Override
    public void analizarLineasAplicables(DocumentoPromocionable documento) {
    	log.trace("analizarLineasAplicables() - " + this);
    	
        // Obtenemos las líneas de condición según el filtro configurado
        FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);        
        LineasAplicablesPromoBean lineasCondicion = filtroLineas.getNumCombosCondicion(condiciones);

        // Obtenemos las líneas de condición según el filtro configurado
        if (lineasCondicion.isEmpty() || lineasCondicion.getNumCombos() == 0) {
            log.trace(this + " analizarLineasAplicables() - La promoción no se puede aplicar por no existir las líneas que cumplan las condiciones en el documento .");
            return;
        }
        
        filtroLineas.setFiltrarPromoExclusivas(true);
        LineasAplicablesPromoBean lineasAplicables = filtroLineas.getLineasAplicablesGrupo(getAplicacion(), true);
        if (lineasAplicables.isEmpty()) {
            log.trace(this + " analizarLineasAplicables() - La promoción no se puede aplicar por no existir las líneas que cumplan las condiciones en el documento .");
            return;
        }

        if (BigDecimalUtil.isMenor(lineasAplicables.getCantidadArticulos(), condicionN)){
            log.trace(this + " analizarLineasAplicables() - La promoción no se puede aplicar porque las líneas no suman la cantidad N configurada: " + lineasAplicables.getCantidadArticulos());
            return;
        }
        
        // Ordenamos las líneas aplicables por precio descendente
        lineasAplicables.ordenarLineasPrecioDesc();
        
        // Registramos promoción candidata en todas las líneas
        PromocionLineaCandidataTicket candidata = new PromocionLineaCandidataTicket(lineasAplicables, lineasCondicion, this);
        for (LineaDocumentoPromocionable linea : lineasAplicables.getLineasAplicables()) {
            linea.addPromocionAplicable(candidata);
        }
    }
	
	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
            XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
            condiciones = (new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas")));
            setCondicionN(xmlPromocion.getNodo("condicionN").getValueAsBigDecimal()); 
            setCondicionM(xmlPromocion.getNodo("condicionM").getValueAsBigDecimal());
            
            aplicacion = new GrupoComponentePromoBean(xmlPromocion.getNodo("aplicacion"));
            
            if (BigDecimalUtil.isMenorOrIgual(getCondicionN(), getCondicionM())) {
            	throw new IllegalArgumentException("La condición N debe ser mayor que M. Actualmente, N:" + getCondicionN() + ", M:"+getCondicionM());
            }
            if (BigDecimalUtil.isMenorOrIgualACero(getCondicionN())) {
            	throw new IllegalArgumentException("La condición N debe ser distinta de cero y positiva.");
            }
            if (BigDecimalUtil.isMenorOrIgualACero(getCondicionM())) {
            	throw new IllegalArgumentException("La condición M debe ser distinta de cero y positiva.");
            }
            
            try{
            	setPorcentajeDto(xmlPromocion.getNodo("descuentoPorcentaje").getValueAsBigDecimal());
            }
            catch(NumberFormatException e){
            	log.error("No se ha podido obtener el porcentaje de descuento como BigDecimal.");
            }
            
            try{
            	setPrecioUnidadRegalada(xmlPromocion.getNodo("precioUnidadRegalada").getValueAsBigDecimal());
            }
            catch(Exception e){
            	log.error("No se ha podido obtener el precio de la unidad regalada como BigDecimal.");
            }
            
            
            String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();
            
            String textPromo = null;
            String textPromoDefault = null;
        	List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos("textoPromocion");
        	for(XMLDocumentNode textPromoNode : textPromoNodes) {
        		String textPromoLanguageCode = textPromoNode.getAtributoValue("lang", true);
        		if(StringUtils.isNotBlank(textPromoLanguageCode)){
        			if(textPromoLanguageCode.equals(storeLanguageCode)) {
        				textPromo = textPromoNode.getValue();
        				break;
        			}
        		}else {
        			textPromoDefault = textPromoNode.getValue();
        		}
        	}

        	if(StringUtils.isBlank(textPromo)) {
        		textPromo = textPromoDefault;
        	}

            setTextoPromocion(textPromo);
            
            cantidadDto = getCondicionN().subtract(getCondicionM());
        }
        catch (XMLDocumentException e) {
            log.error("Error al leer los datos de la promoción de tipo descuento  NxM: " + e.getMessage(), e);
        }
	}
	
    public GrupoComponentePromoBean getAplicacion() {
        return aplicacion;
    }

    public void setAplicacion(GrupoComponentePromoBean aplicacion) {
        this.aplicacion = aplicacion;
    }

}
