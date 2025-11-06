package com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.pos.util.xml.MarshallUtil;

@Component
public class ArticulosRecargaService {
	
	private Logger log = Logger.getLogger(ArticulosRecargaService.class);
	
	private XmlConfigArticulosRecarga xmlConfig;
	
	public XmlConfigArticulosRecarga getConfiguracion() throws Exception {
		if(xmlConfig != null) {
			return xmlConfig;
		}
		
		try {
			URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion("articulos_recargas.xml");
			File file = new File(url.getPath());
			byte[] fileContent = Files.readAllBytes(file.toPath());
			xmlConfig = (XmlConfigArticulosRecarga) MarshallUtil.leerXML(fileContent, XmlConfigArticulosRecarga.class);
			
			return xmlConfig;
		}
		catch(Exception e) {
			log.error("getConfiguracion() - Ha habido un error al leer el fichero de configuración de los artículos de recarga: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public URL getUrlImage(String codart) {
		try {
			URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion("imagenesRecarga/" + codart + ".png");
			return url;
		}
		catch(Exception e) {
			log.error("getUrlImage() - Ha habido un error al leer la imagen del artículo " + codart + ": " + e.getMessage());
			try {
				URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion("imagenesRecarga/no_definido.png");
				return url;
			}
			catch(Exception ex) {
				log.error("getUrlImage() - Ha habido un error al leer la imagen por defecto de recarga: " + e.getMessage());
				return null;
			}
		}
	}

}
