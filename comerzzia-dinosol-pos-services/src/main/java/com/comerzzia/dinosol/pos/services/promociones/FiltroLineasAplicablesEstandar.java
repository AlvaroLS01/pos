package com.comerzzia.dinosol.pos.services.promociones;

import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.filtro.LineasAplicablesPromoBean;

public class FiltroLineasAplicablesEstandar extends FiltroLineasPromocion {
	
	@Override
	protected LineasAplicablesPromoBean createLineasAplicables() {
		LineasAplicablesPromoBean aplicables = new LineasAplicablesPromoBean();
		aplicables.setFiltroPromoExclusiva(filtrarPromoExclusivas);
		return aplicables;
	}

}
