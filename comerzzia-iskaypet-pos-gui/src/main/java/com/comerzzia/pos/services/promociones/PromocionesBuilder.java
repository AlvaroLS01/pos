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

package com.comerzzia.pos.services.promociones;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.promociones.tipos.PromocionTipoBean;

@Component
public class PromocionesBuilder {

	protected static final Logger log = Logger.getLogger(PromocionesBuilder.class.getName());

	public Promocion create(PromocionBean promocionBean) throws PromocionesBuilderException {
		log.debug("create() - Instanciando promoción: " + promocionBean);
		try {
			PromocionTipoBean tipoPromocion = promocionBean.getTipoPromocion();
			tipoPromocion.parseConfiguracion();
			String manejador = tipoPromocion.getManejador();
			if (manejador == null) {
				throw new PromocionesBuilderException("No hay definido \"<Manejador>\" (className) dentro de la configuración del idTipoPromocion " + tipoPromocion.getIdTipoPromocion());
			}
			Promocion promocion = (Promocion) ContextHolder.getBean(manejador);
			promocion.init(promocionBean);
			return promocion;
		} catch (XMLDocumentException | ClassNotFoundException e) {
			throw new PromocionesBuilderException(e);
		}
	}
}
