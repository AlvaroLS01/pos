package com.comerzzia.dinosol.pos.services.tarjetasregalo;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;

@Component
public class TarjetasRegaloService {
	
	private Logger log = Logger.getLogger(TarjetasRegaloService.class);

	private Map<String, TipoTarjetaRegaloDto> tiposTarjetaRegalo;
	
	public TipoTarjetaRegaloDto getTipoTarjeta(String codart) {
		if(tiposTarjetaRegalo == null) {
			leerTiposTarjetas();
		}
		return tiposTarjetaRegalo.get(codart);
	}

	private void leerTiposTarjetas() {
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("prefijos_tarjetasregalos.xml");
		File file = new File(url.getPath());
		log.debug("leerTiposTarjetas() - Iniciamos la lectura del archivo prefijos_tarjetasregalos.xml en la ruta : " + url.toString());

		tiposTarjetaRegalo = new HashMap<String, TipoTarjetaRegaloDto>();
		try {
			XMLDocument xml = new XMLDocument(file);

			for (XMLDocumentNode nodoXml : xml.getRoot().getHijos()) {
				String codart = nodoXml.getNodo("codart").getValue();
				String prefijo = nodoXml.getNodo("prefijo").getValue();
				BigDecimal importeMinimo = nodoXml.getNodo("importe_minimo").getValueAsBigDecimal();
				BigDecimal importeMaximo = nodoXml.getNodo("importe_maximo").getValueAsBigDecimal();
				Integer diasVigencia = nodoXml.getNodo("dias_vigencias").getValueAsInteger();
				
				TipoTarjetaRegaloDto tipoTarjetaRegaloDto = new TipoTarjetaRegaloDto();
				tipoTarjetaRegaloDto.setCodArticulo(codart);
				tipoTarjetaRegaloDto.setPrefijo(prefijo);
				tipoTarjetaRegaloDto.setImporteMinimo(importeMinimo);
				tipoTarjetaRegaloDto.setImporteMaximo(importeMaximo);
				tipoTarjetaRegaloDto.setDiasVigencia(diasVigencia);
				
				tiposTarjetaRegalo.put(codart, tipoTarjetaRegaloDto);
			}
		}
		catch (Exception e) {
			log.error("leerXmlConfiguracion() - Ha habido un error al cargar el XML de configuración: " + e.getMessage(), e);
		}
	}
	
}
