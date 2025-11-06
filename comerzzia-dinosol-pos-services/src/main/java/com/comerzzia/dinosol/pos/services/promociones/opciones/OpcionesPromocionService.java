package com.comerzzia.dinosol.pos.services.promociones.opciones;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;

@Component
public class OpcionesPromocionService {
	
	private Logger log = Logger.getLogger(OpcionesPromocionService.class);
	
	private String tituloVisor;
	
	private List<OpcionPromocionesDto> opciones;
	
	public String getTituloVisor() {
		if(tituloVisor == null) {
			leerXmlConfiguracion();
		}
		return tituloVisor;
	}

	public List<OpcionPromocionesDto> getOpciones() {
		if(opciones == null) {
			leerXmlConfiguracion();
		}
		return opciones;
	}

	public void leerXmlConfiguracion() {
		opciones = new ArrayList<OpcionPromocionesDto>();
		
		try {
			ComerzziaApp comerzziaApp = ComerzziaApp.get();
			URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("opciones_promociones.xml");
			File file = new File(url.getPath());
			log.debug("leerXmlConfiguracion() - Iniciamos la lectura del archivo opciones_promociones.xml en la ruta : " + url.toString());
			
			XMLDocument xml = new XMLDocument(file);
			XMLDocumentNode root = xml.getRoot();

			tituloVisor = root.getNodo("titulo_visor_cliente").getValue();
			
			XMLDocumentNode nodoOpciones = root.getNodo("opciones");
			for(XMLDocumentNode nodoOpcion : nodoOpciones.getHijos()) {
				OpcionPromocionesDto opcion = new OpcionPromocionesDto();
				opcion.setTitulo(nodoOpcion.getNodo("titulo").getValue());
				opcion.setTextoTicket(nodoOpcion.getNodo("texto_ticket").getValue());
				
				String promocionesSap = nodoOpcion.getNodo("promociones").getValue();
				String [] promocionesSapSplit = promocionesSap.split(",");
				for(int i = 0 ; i < promocionesSapSplit.length ; i++) {
					String idPromocionSap = promocionesSapSplit[i];
					opcion.addPromocion(idPromocionSap);
				}				
				
				opciones.add(opcion);
			}
			
			log.debug("leerXmlConfiguracion() - Configuración de opciones leída: " + opciones);
		}
		catch (Exception e) {
			log.error("leerXmlConfiguracion() - Ha habido un error al cargar el XML de configuración: " + e.getMessage(), e);
		}
	}

}
