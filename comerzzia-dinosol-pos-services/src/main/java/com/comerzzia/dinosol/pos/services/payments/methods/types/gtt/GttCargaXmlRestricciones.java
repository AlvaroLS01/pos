package com.comerzzia.dinosol.pos.services.payments.methods.types.gtt;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import org.apache.log4j.Logger;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.librerias.gtt.client.dto.RestriccionesResponseDTO;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.excepciones.GttRestriccionesException;
import com.comerzzia.pos.util.xml.MarshallUtil;

public class GttCargaXmlRestricciones {

	public static RestriccionesResponseDTO restricciones;

	private static final String NOMBRE_XML = "restriccionesGtt";

	protected static final Logger log = Logger.getLogger(GttCargaXmlRestricciones.class);

	/**
	 * Carga el mapa con los datos del XML en caso de no estar ya cargado.
	 * 
	 * @throws GttRestriccionesException
	 */
	public static RestriccionesResponseDTO cargarMapaRestricciones() throws GttRestriccionesException {
		if (restricciones == null) {
			try {
				restricciones = getRestriccionesXml();
			}
			catch (GttRestriccionesException e) {
				log.error("cargarMapaRestricciones() - " + e.getMessage());
				throw new GttRestriccionesException(e.getMessage());
			}
		}
		return restricciones;
	}

	/**
	 * Recorre un archivo XML para cargar las restricciones.
	 * 
	 * @return familiasRestringidasXML
	 * @throws GttRestriccionesException
	 */
	public static RestriccionesResponseDTO getRestriccionesXml() throws GttRestriccionesException {
		/* Traemos la ruta del archivo para poder cargarlo */
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion(NOMBRE_XML + ".xml");
		File file = new File(url.getPath());
		log.debug("getRestriccionesXml() - Iniciamos la lectura del archivo " + NOMBRE_XML + " en la ruta : " + url.toString());

		try {
			byte[] fileContent = Files.readAllBytes(file.toPath());
			RestriccionesResponseDTO restricciones = (RestriccionesResponseDTO) MarshallUtil.leerXML(fileContent, RestriccionesResponseDTO.class);
			return restricciones;
		}
		catch (Exception e) {
			String mensajeError = "Error al cargar el XML de restricciones de GTT";
			log.error("getRestriccionesXml() - " + mensajeError);
			throw new GttRestriccionesException(mensajeError);
		}
	}

}
