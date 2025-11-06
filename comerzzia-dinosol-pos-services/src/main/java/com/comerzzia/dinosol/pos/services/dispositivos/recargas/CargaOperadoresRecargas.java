package com.comerzzia.dinosol.pos.services.dispositivos.recargas;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.pos.persistence.dispositivos.recargas.OperadorDTO;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.DinoPropiedadesDocumentoService;

public class CargaOperadoresRecargas {

	private static final String NOMBRE_XML = "operadores_recargas";

	protected static final Logger log = Logger.getLogger(CargaOperadoresRecargas.class);

	public static List<OperadorDTO> operadoresLinea;

	@Autowired
	public static DinoPropiedadesDocumentoService propiedadesService;

	/**
	 * Carga el objeto con los datos del XML en caso de no estar ya cargado.
	 * 
	 * @return restriccionLineas
	 */
	public static List<OperadorDTO> cargarOperadoresLinea() {
		if (operadoresLinea != null && operadoresLinea.isEmpty()) {
			operadoresLinea = getOperadoresRecargasXml();
		}
		return operadoresLinea;
	}

	/**
	 * Recorre un archivo XML para cargar las restricciones.
	 * 
	 * @return familiasRestringidasXML
	 */
	public static List<OperadorDTO> getOperadoresRecargasXml() {
		/* Traemos la ruta del archivo para poder cargarlo */
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion(NOMBRE_XML + ".xml");
		File file = new File(url.getPath());
		log.debug("getOperadoresRecargasXml() - Iniciamos la lectura del archivo " + NOMBRE_XML + " en la ruta : " + url.toString());

		List<OperadorDTO> operadores = new ArrayList<OperadorDTO>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			/* Eliminamos los nodos vacíos y combinamos los adyacentes en caso de que los hubiera */
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("operador");
			log.debug("getOperadoresRecargasXml() - El archivo XML contiene " + nList.getLength() + " operadores");

			/* Almacenamos las familias en un listado para comparar */
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					OperadorDTO linea = new OperadorDTO();

					linea.setCodigoArticulo(eElement.getElementsByTagName("codart").item(0).getTextContent());
					linea.setEan(eElement.getElementsByTagName("ean").item(0).getTextContent());
					linea.setDescripcion(eElement.getElementsByTagName("descripcion").item(0).getTextContent());
					String importe = eElement.getElementsByTagName("importe").item(0).getTextContent();
					importe = importe.replaceAll(",", ".");
					if(StringUtils.isNotBlank(importe)) {
						linea.setImporte(new BigDecimal(importe));
						operadores.add(linea);
					}
					else {
						log.warn("getOperadoresRecargasXml() - Hay una línea sin importe declarado.");
					}
				}
			}

		}
		catch (Exception e) {
			String mensajeError = "Error al encontrar el archivo "+NOMBRE_XML+ ".xml en la ruta" + url.toString();
			log.error("getOperadoresRecargasXml() - : " + mensajeError + " " + e.getMessage(), e);
		}
		return operadores;
	}

}
