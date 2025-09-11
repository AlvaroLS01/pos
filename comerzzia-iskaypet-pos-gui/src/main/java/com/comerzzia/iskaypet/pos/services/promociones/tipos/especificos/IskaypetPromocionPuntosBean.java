package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPuntosBean;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Component
@Scope("prototype")
@Primary
@SuppressWarnings({})
public class IskaypetPromocionPuntosBean extends PromocionPuntosBean {
 
	public static final String VARIABLE_APLICA_REDONDEO_PUNTOS = "POS.APLICA_REDONDEO_PUNTOS";

    protected BigDecimal porcentajePuntos;
	
	@Autowired
	private VariablesServices variablesServices;  

	@Override
    public boolean aplicarPromocion(DocumentoPromocionable documento) {
		log.trace("aplicarPromocion() - " + this);
		
		if(porcentajePuntos!=null) {
    		puntosEuros=porcentajePuntos;
    	}
    	if (puntosEuros == null || puntosEuros.equals(BigDecimal.ZERO)){
          log.trace (this + " aplicarPromocion() - La promoción no se puede aplicar porque los puntos por euros configurados no lo permiten: " + puntosEuros);
    	}

		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		filtroLineas.setFiltrarPromoExclusivas(false);

        setAdmitePromocionesLineasConPegatina((IskaypetTicketVentaAbono) documento, true);

        LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
		if (lineasAplicables.isEmpty()) {
			log.debug(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

        setAdmitePromocionesLineasConPegatina((IskaypetTicketVentaAbono) documento, false);

		BigDecimal importeAplicable = lineasAplicables.getImporteLineasConDto();
		if (importeAplicable.equals(BigDecimal.ZERO)) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar porque el importe aplicable es cero. ");
			return false;
		}

		BigDecimal puntos;
		if(porcentajePuntos!=null) {
			puntos = (importeAplicable.multiply(puntosEuros)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

			if(Boolean.TRUE.equals(getVariableProcesoRedondeoPuntos())) {
				if (Boolean.TRUE.equals(documento.getCabecera().esVenta())) {
					puntos = puntos.setScale(0, RoundingMode.CEILING);
				} else {
					puntos = puntos.setScale(0, RoundingMode.FLOOR);
				}
			}
		}else {
			if (Boolean.TRUE.equals(documento.getCabecera().esVenta())) {
				puntos = importeAplicable.divide(puntosEuros, 0, RoundingMode.CEILING);
			} else {
				puntos = importeAplicable.divide(puntosEuros, 0, RoundingMode.FLOOR);
			}
		}


		if (BigDecimalUtil.isIgualACero(puntos)) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar porque los puntos obtenidos son cero. ");
			return false;
		}
		else{
			// Aplicamos la promoción sobre el documento
			PromocionTicket promocionTicket = createPromocionTicket(customerCoupon);
			promocionTicket.setPuntos(puntos.abs());
			documento.addPromocion(promocionTicket);

			//TicketVenta ticket = (TicketVenta) documento.getCabecera().getTicket();
			//lineasAplicables.setLineasAplicables(ticket.getLineas()); Comentada ya que las lineas aplicables vienen correctamente indicadas al calcular la promocion
			lineasAplicables.aplicaPromocionPuntos(promocionTicket, puntosEuros);
			documento.addPuntos(puntos);
		}

		return true;
	}

    private void setAdmitePromocionesLineasConPegatina(IskaypetTicketVentaAbono documento, boolean admitePromociones) {
        for (LineaTicket lineaTicket : documento.getLineas()) {
            if (((IskaypetLineaTicket) lineaTicket).getPegatinaPromocional() != null) {
                lineaTicket.setAdmitePromociones(admitePromociones);
            }
        }
    }

    public Boolean getVariableProcesoRedondeoPuntos(){
    	log.debug("getVariableProcesoRedondeoPuntos() - GAP45.1.- Variable para control de proceso de redondeo de puntos....");
        return variablesServices.getVariableAsBoolean(VARIABLE_APLICA_REDONDEO_PUNTOS, false);
    }
    
	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
			XMLDocumentNode porcentajePuntosNodo = xmlPromocion.getNodo("porcentajePuntos", true);
			condiciones = new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas"));
			
			if (porcentajePuntosNodo != null && !porcentajePuntosNodo.getValue().isEmpty() && BigDecimalUtil.isMayorACero(xmlPromocion.getNodo("porcentajePuntos").getValueAsBigDecimal())) {					
				setPorcentajePuntos(xmlPromocion.getNodo("porcentajePuntos").getValueAsBigDecimal());
				String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();
				String textPromo = null;
				String textPromoDefault = null;
				List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos("textoPromocion");
				for (XMLDocumentNode textPromoNode : textPromoNodes) {
					String textPromoLanguageCode = textPromoNode.getAtributoValue("lang", true);
					if (StringUtils.isNotBlank(textPromoLanguageCode)) {
						if (textPromoLanguageCode.equals(storeLanguageCode)) {
							textPromo = textPromoNode.getValue();
							break;
						}
					}
					else {
						textPromoDefault = textPromoNode.getValue();
					}
				}

				if (StringUtils.isBlank(textPromo)) {
					textPromo = textPromoDefault;
					setTextoPromocion(textPromo);

				}
			}
			else {
				super.leerDatosPromocion(datosPromocion);
			}
		}
		catch (XMLDocumentException e) {
			log.error("Error al leer los datos de la promoción de tipo puntos: " + e.getMessage(), e);
		}
	}

	public BigDecimal getPorcentajePuntos() {
		return porcentajePuntos;
	}

	public void setPorcentajePuntos(BigDecimal porcentajePuntos) {
		this.porcentajePuntos = porcentajePuntos;
	}

}
