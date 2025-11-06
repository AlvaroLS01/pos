package com.comerzzia.dinosol.pos.services.promociones.filtros;

import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;


public class DinoFiltroLineasPromocionCabecera extends FiltroLineasPromocion {
	
	@Override
	protected LineasAplicablesPromoBean createLineasAplicables() {
		LineasAplicablesPromoBean aplicables = new DinoLineasAplicablesEditadasManualmentePromoBean();
		aplicables.setFiltroPromoExclusiva(filtrarPromoExclusivas);
		return aplicables;
	}

}
