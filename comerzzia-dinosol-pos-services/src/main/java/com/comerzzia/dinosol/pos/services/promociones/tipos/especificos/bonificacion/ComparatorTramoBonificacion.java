package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.bonificacion;

import java.util.Comparator;

public class ComparatorTramoBonificacion implements Comparator<TramoBonificacion> {

	@Override
	public int compare(TramoBonificacion o1, TramoBonificacion o2) {
		return o1.getImporte().compareTo(o2.getImporte());
	}

}
