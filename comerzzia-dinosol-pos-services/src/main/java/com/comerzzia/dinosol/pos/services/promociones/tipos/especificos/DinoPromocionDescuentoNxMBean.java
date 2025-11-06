package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.pos.services.promociones.FiltroLineasAplicablesEstandar;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPromocion;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionDescuentoNxMBean;

@Component
@Primary
@Scope("prototype")
public class DinoPromocionDescuentoNxMBean extends PromocionDescuentoNxMBean {

	@Override
	protected FiltroLineasPromocion createFiltroLineasPromocion(DocumentoPromocionable documento) {
		FiltroLineasPromocion filtro = new FiltroLineasAplicablesEstandar();
		filtro.setDocumento(documento);
		filtro.setFiltrarPromoExclusivas(false);
		return filtro;
	}

}
