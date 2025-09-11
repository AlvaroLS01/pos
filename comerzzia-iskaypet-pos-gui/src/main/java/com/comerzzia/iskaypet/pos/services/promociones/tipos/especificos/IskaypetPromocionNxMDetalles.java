package com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionNxM;
//GAP 59.2 Mejora banner de promociones. Se personaliza esta clase porque el atributo es inacesible desde estandar
@Primary
@Component
@Scope("prototype")
public class IskaypetPromocionNxMDetalles
		extends com.comerzzia.pos.services.promociones.tipos.especificos.PromocionNxMDetalles {

	public List<DetallePromocionNxM> getListDetallesPromocion() {
		return this.detalles;
	}

}
