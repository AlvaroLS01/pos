package com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie;

import java.math.BigDecimal;
import java.util.List;

public interface LineaNumerosSerie {
	BigDecimal getCantidad();

	String getDesArticulo();

	List<String> getNumerosSerie();
}
