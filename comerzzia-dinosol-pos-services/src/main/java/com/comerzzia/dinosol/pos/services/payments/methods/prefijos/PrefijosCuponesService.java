package com.comerzzia.dinosol.pos.services.payments.methods.prefijos;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;

@Component
public class PrefijosCuponesService {

	private Logger log = Logger.getLogger(PrefijosCuponesService.class);

	private Map<String, List<String>> prefijos;

	public String getMedioPagoPrefijo(String codigoCupon) {
		log.debug("getMedioPagoPrefijo() - Consultando medio de pago asociado a " + codigoCupon);

		if (prefijos == null) {
			leerXmlConfiguracion();
		}

		String resultado = null;

		for (String codMedioPago : prefijos.keySet()) {
			List<String> listaPrefijos = prefijos.get(codMedioPago);
			for (String prefijo : listaPrefijos) {
				if (codigoCupon.startsWith(prefijo)) {
					resultado = codMedioPago;
					break;
				}
			}
		}

		return resultado;
	}

	private void leerXmlConfiguracion() {
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("prefijos_cupones.xml");
		File file = new File(url.getPath());
		log.debug("Iniciamos la lectura del archivo prefijos_mediospago.xml en la ruta : " + url.toString());

		prefijos = new HashMap<String, List<String>>();
		try {
			XMLDocument xml = new XMLDocument(file);

			for (XMLDocumentNode nodoXml : xml.getRoot().getHijos()) {
				String codMedioPago = nodoXml.getNodo("codMedioPago").getValue();
				List<String> listaPrefijos = new ArrayList<String>();
				for (XMLDocumentNode nodoPrefijo : nodoXml.getNodo("prefijos").getHijos()) {
					String prefijo = nodoPrefijo.getValue();
					listaPrefijos.add(prefijo);
				}

				prefijos.put(codMedioPago, listaPrefijos);
			}
		}
		catch (Exception e) {
			log.error("leerXmlConfiguracion() - Ha habido un error al cargar el XML de configuración: " + e.getMessage(), e);
		}
	}

}
