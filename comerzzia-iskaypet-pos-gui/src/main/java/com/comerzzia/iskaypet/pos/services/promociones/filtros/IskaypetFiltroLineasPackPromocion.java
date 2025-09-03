package com.comerzzia.iskaypet.pos.services.promociones.filtros;

import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.promociones.LineaDocumentoPromocionable;
import com.comerzzia.pos.services.promociones.filtro.FiltroLineasPackPromocion;
import com.comerzzia.pos.services.promociones.tipos.componente.GrupoComponentePromoBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.pack.AgrupacionPack;

@Component
@Primary
@Scope("prototype")
public class IskaypetFiltroLineasPackPromocion extends FiltroLineasPackPromocion {
	
	@Override
	protected AgrupacionPack procesarGrupo(GrupoComponentePromoBean grupo) {
		List<LineaDocumentoPromocionable> lineasAplicablesRegla = getLineasAplicablesGrupo(grupo, documento.getLineasDocumentoPromocionable());
		
		// ISK-XX Se comprueba que se puede aplicar la promoción a las líneas
		Iterator<LineaDocumentoPromocionable> it = lineasAplicablesRegla.iterator();
		while(it.hasNext()) {
			LineaDocumentoPromocionable linea = it.next();
			
			if (!linea.isAdmitePromociones()) {
				it.remove();
			}
		}
		// Fin ISK-XX
		
		if (lineasAplicablesRegla.isEmpty()) {
			return null; // No continuamos procesando, porque hay una condición del pack que no se cumple
		}
		
		AgrupacionPack agrupacionPack = crearAgrupacion(lineasAplicablesRegla, null, null);

		return agrupacionPack;
	}

}
