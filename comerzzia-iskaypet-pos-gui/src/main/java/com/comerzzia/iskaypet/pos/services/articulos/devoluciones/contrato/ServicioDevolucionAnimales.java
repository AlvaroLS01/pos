package com.comerzzia.iskaypet.pos.services.articulos.devoluciones.contrato;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class ServicioDevolucionAnimales {

	private Logger log = Logger.getLogger(ServicioDevolucionAnimales.class);

	@Autowired
	protected ArticulosService articulosService;

	@Autowired
	protected VariablesServices variablesServices;

	public static final String categoriaAnimales = "X_POS.CATEGORIA_ANIMALES";

	public boolean esArticuloMascota(String codart) throws LineaTicketException {

		try {
			// Consultamos las categorías definidas parqa los articulos en la variable para
			// saber si son de mascotas
			String valor = variablesServices.getVariableAsString(categoriaAnimales);
			List<String> valoresCategorias = null;
			if (valor != null) {
				String[] valores = valor.split(";");
				valoresCategorias = Arrays.asList(valores);
			}

			//Consultamos el artículo para poder obtener la categoría a la que pertenece
			ArticuloBean art = articulosService.consultarArticulo(codart);

			if (art != null && StringUtils.isNotBlank(art.getCodCategorizacion())) {
				if (valoresCategorias != null && !valoresCategorias.isEmpty()) {
					//Si está dentro de la lista de valores, no podremos hacer la devolución pues se trata de una mascota
					for(String codCategoriaMascota : valoresCategorias) {
						if(art.getCodCategorizacion().startsWith(codCategoriaMascota)) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			String msg = "Se ha producido un error consultando el artículo con código " + codart + ": "
					+ e.getMessage();
			log.error("esArticuloMascota() - " + msg, e);
			throw new LineaTicketException(I18N.getTexto("Error procesando devolución. Revise configuración."));

		}
		
		return false;
	}

}
