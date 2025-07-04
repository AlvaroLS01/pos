package com.comerzzia.cardoso.pos.services.sesion;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.promociones.Promocion;

@Primary
@Component
public class CARDOSOSesionPromociones extends SesionPromociones{

	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - PROMOCIONES MONOGRÁFICAS
	 */
	@Override
	protected void addPromocionActiva(Long idPromocion, Promocion promocion){
		super.addPromocionActiva(idPromocion, promocion);
//		if(promocion.getIdTipoPromocion().equals(CARDOSOPromocionesBuilder.ID_TIPO_PROMOCION_MONOGRAFICA)){
//			promocionesLineas.put(promocion.getIdPromocion(), (PromocionLinea) promocion);
//		}
	}

}
