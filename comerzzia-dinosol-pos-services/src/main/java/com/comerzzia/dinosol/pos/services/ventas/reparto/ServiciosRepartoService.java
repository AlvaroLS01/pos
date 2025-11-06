package com.comerzzia.dinosol.pos.services.ventas.reparto;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServiciosRepartoDto;
import com.comerzzia.pos.util.xml.MarshallUtil;

@Component
public class ServiciosRepartoService {

	private Logger log = Logger.getLogger(ServiciosRepartoService.class);

	public static String FILENAME_APLICACIONES_REPARTO = "pos_aplicaciones_reparto.xml";

	private List<ServicioRepartoDto> serviciosReparto;

	public List<ServicioRepartoDto> getServiciosReparto() {
		if (serviciosReparto != null && !serviciosReparto.isEmpty()) {
			return serviciosReparto;
		}
		
		if(serviciosReparto != null && serviciosReparto.isEmpty()) {
			return null;
		}

		try {
			URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion(FILENAME_APLICACIONES_REPARTO);
			File file = new File(url.getPath());
			byte[] fileContent = Files.readAllBytes(file.toPath());
			serviciosReparto = ((ServiciosRepartoDto) MarshallUtil.leerXML(fileContent, ServiciosRepartoDto.class)).getServicios();

			return serviciosReparto;
		}
		catch (Exception e) {
			log.error("getServiciosReparto() - Ha habido un error al leer el fichero de configuración de los servicios de reparto: " + e.getMessage(), e);
			
			serviciosReparto = new ArrayList<ServicioRepartoDto>();
			
			return null;
		}
	}

	public URL getUrlImage(String imagen) {
		try {
			URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion("imagenesServiciosReparto/" + imagen);
			return url;
		}
		catch (Exception e) {
			log.error("getUrlImage() - Ha habido un error al leer la imagen del servicio de reparto " + imagen + ": " + e.getMessage());
			try {
				URL url = ComerzziaApp.get().obtenerUrlFicheroConfiguracion("imagenesServiciosReparto/no_definido.png");
				return url;
			}
			catch (Exception ex) {
				log.error("getUrlImage() - Ha habido un error al leer la imagen por defecto de servicios de reparto: " + e.getMessage());
				return null;
			}
		}
	}

	public ServicioRepartoDto getServicioReparto(String servicioReparto) {
		if (serviciosReparto == null) {
			return null;
		}
		
		for(ServicioRepartoDto servicio : serviciosReparto) {
			if(servicio.getNombre().equals(servicioReparto)) {
				return servicio;
			}
		}
		return null;
	}

}
