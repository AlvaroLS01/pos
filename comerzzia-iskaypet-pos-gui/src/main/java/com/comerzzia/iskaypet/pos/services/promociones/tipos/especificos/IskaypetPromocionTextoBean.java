package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.util.List;

import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.services.promociones.tipos.componente.CondicionPrincipalPromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionTextoBean;

@Primary
@Component
@Scope("prototype")
public class IskaypetPromocionTextoBean extends PromocionTextoBean {

	protected static final Logger log = Logger.getLogger(IskaypetPromocionTextoBean.class);

	public static final String PARAMETRO_TEXTO_PROMOCION = "textoPromocion";
	public static final String PARAMETRO_IMAGEN_PROMOCION = "imagenPromocion";
	public static final String PARAMETRO_VISIBLE_VENTAS = "visibleVentas";
	public static final String PARAMETRO_IMPRIMIR_APARTE = "imprimirAparte";
	public static final String PARAMETRO_LANGUAGE = "lang";

	private Boolean imprimirAparte;

	@Override
	public void leerDatosPromocion(byte[] datosPromocion) {
		try {
			XMLDocument xmlPromocion = new XMLDocument(datosPromocion);
			condiciones = new CondicionPrincipalPromoBean(xmlPromocion.getNodo("condicionLineas"));

			String storeLanguageCode = sesion.getAplicacion().getStoreLanguageCode();

			String textPromo = null;
			String textPromoDefault = null;
			List<XMLDocumentNode> textPromoNodes = xmlPromocion.getNodos(PARAMETRO_TEXTO_PROMOCION);
			for (XMLDocumentNode textPromoNode : textPromoNodes) {
				String textPromoLanguageCode = textPromoNode.getAtributoValue(PARAMETRO_LANGUAGE, true);
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
			}

			setTextoPromocion(textPromo);
			XMLDocumentNode nodoImagenPromocion = xmlPromocion.getNodo(PARAMETRO_IMAGEN_PROMOCION, true);
			if (nodoImagenPromocion != null) {
				setImagenPromocion(nodoImagenPromocion.getValue());
			}

			XMLDocumentNode nodoVisibleVentas = xmlPromocion.getNodo(PARAMETRO_VISIBLE_VENTAS, true);
			if (nodoVisibleVentas != null) {
				setVisibleVentas(nodoVisibleVentas.getValueAsBoolean());
			}

			XMLDocumentNode nodoimprimirAparte = xmlPromocion.getNodo(PARAMETRO_IMPRIMIR_APARTE, true);
			if (nodoimprimirAparte != null) {
				setImprimirAparte(nodoimprimirAparte.getValueAsBoolean());
			}

		}
		catch (XMLDocumentException e) {
			log.error("Error al leer los datos de la promoción de tipo texto: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean aplicarPromocion(DocumentoPromocionable documento) {
		log.trace("aplicarPromocion() - " + this);
		// Obtenemos las líneas aplicables según el filtro configurado
		FiltroLineasPromocion filtroLineas = createFiltroLineasPromocion(documento);
		filtroLineas.setFiltrarPromoExclusivas(false); // Da igual que las líneas tengan una promoción exclusiva
		LineasAplicablesPromoBean lineasAplicables = filtroLineas.getNumCombosCondicion(condiciones);
		if (lineasAplicables.isEmpty() && !condiciones.isVacio()) {
			log.trace(this + " aplicarPromocion() - La promoción no se puede aplicar por no existir líneas aplicables en el documento .");
			return false;
		}

		// Aplicamos la promoción sobre el ticket
		PromocionTicket promocionTicket = documento.getPromocion(getIdPromocion());
		if(promocionTicket == null) {
			promocionTicket = createPromocionTicket(customerCoupon);
			promocionTicket.putAdicional(PARAMETRO_IMPRIMIR_APARTE, getImprimirAparte());
			documento.addPromocion(promocionTicket);
		}

		if(customerCoupon != null) {
			CuponAplicadoTicket cupon = documento.getCuponAplicado(customerCoupon.getCouponCode());
			if (cupon != null){
				cupon.setTextoPromocion(promocionTicket.getTextoPromocion());
			}
		}

		return true;
	}

	public Boolean getImprimirAparte() {
		return imprimirAparte != null ? imprimirAparte : false;
	}

	public void setImprimirAparte(Boolean imprimirAparte) {
		this.imprimirAparte = imprimirAparte;
	}
}
