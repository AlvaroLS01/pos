package com.comerzzia.dinosol.pos.services.core.documentos.propiedades;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionDocumentoBean;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionLineaBean;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.DinoPropiedadesDocumentoService;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;

public class CargaRestricciones {

	private static final String PROPIEDAD_IMPORTE_MINIMO = "X_IMPORTE_MINIMO";
	private static final String PROPIEDAD_IMPORTE_MAXIMO = "IMPORTE_MAXIMO";
	private static final String PROPIEDAD_CANTIDAD = "X_CANTIDAD_MAXIMA";

	private static final String NOMBRE_XML = "restriccionesventa";

	protected static final Logger log = Logger.getLogger(CargaRestricciones.class);

	public static List<RestriccionLineaBean> restriccionLineas;
	public static RestriccionDocumentoBean restriccionDocumento;

	@Autowired
	public static DinoPropiedadesDocumentoService propiedadesService;

	/**
	 * Carga el objeto con los datos del XML en caso de no estar ya cargado.
	 * 
	 * @return restriccionLineas
	 */
	public static List<RestriccionLineaBean> cargarRestriccionesLineas() {
		if (restriccionLineas != null && restriccionLineas.isEmpty()) {
			restriccionLineas = getRestriccionesXml();
		}
		return restriccionLineas;
	}

	/**
	 * Carga el objeto con los datos de BD en caso de no estar ya cargado.
	 * 
	 * @param uidActividad
	 * @param idDocumento
	 * @return restriccionDocumento
	 */
	public static RestriccionDocumentoBean cargarRestriccionesDocumento(String uidActividad, Long idDocumento) {
		if (restriccionDocumento == null) {
			restriccionDocumento = getRestriccionesBd(uidActividad, idDocumento);
		}
		return restriccionDocumento;
	}

	/**
	 * Recorre un archivo XML para cargar las restricciones.
	 * 
	 * @return familiasRestringidasXML
	 */
	public static List<RestriccionLineaBean> getRestriccionesXml() {
		/* Traemos la ruta del archivo para poder cargarlo */
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion(NOMBRE_XML + ".xml");
		File file = new File(url.getPath());
		log.debug("Iniciamos la lectura del archivo " + NOMBRE_XML + " en la ruta : " + url.toString());

		List<RestriccionLineaBean> restricciones = new ArrayList<RestriccionLineaBean>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			/* Eliminamos los nodos vacíos y combinamos los adyacentes en caso de que los hubiera */
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("restriccionlinea");
			log.debug("El archivo XML contiene " + nList.getLength() + " restricciones");

			/* Almacenamos las familias en un listado para comparar */
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					RestriccionLineaBean linea = new RestriccionLineaBean();

					linea.setTiendas(eElement.getElementsByTagName("tiendas").item(0).getTextContent());
					linea.setCodArt(eElement.getElementsByTagName("codart").item(0).getTextContent());
					linea.setCantidadMaxima(new BigDecimal(eElement.getElementsByTagName("cantidadmaxima").item(0).getTextContent()));
					linea.setPrecioMaximo(new BigDecimal(eElement.getElementsByTagName("preciomaximo").item(0).getTextContent()));
					linea.setPrecioMinimo(new BigDecimal(eElement.getElementsByTagName("preciominimo").item(0).getTextContent()));

					restricciones.add(linea);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return restricciones;
	}

	/**
	 * Realiza una búsqueda en BD para traer todas las propiedades de un documento y cargamos un objeto con 3 de ellas
	 * (X_CANTIDAD_MAXIMA, IMPORTE_MAXIMO, X_IMPORTE_MINIMO)
	 * 
	 * @param uidActividad
	 * @param idDocumento
	 * @return restriccion
	 */
	public static RestriccionDocumentoBean getRestriccionesBd(String uidActividad, Long idDocumento) {
		RestriccionDocumentoBean restriccion = new RestriccionDocumentoBean();
		log.debug("Iniciamos la búsqueda de las propiedades de un documento con actividad : " + uidActividad);

		List<PropiedadDocumentoBean> listadoPropiedades = new ArrayList<PropiedadDocumentoBean>();
		try {
			propiedadesService.consultarPropiedadesTipoDocumento(uidActividad, idDocumento);
		}
		catch (Exception e) {
			String mensajeError = "Error al cargar las propiedades del documento : " + idDocumento;
			log.error("getRestriccionesBd() - " + mensajeError + " : " + e.getMessage());
		}
		for (PropiedadDocumentoBean propiedad : listadoPropiedades) {
			if (propiedad.getPropiedad().equals(PROPIEDAD_CANTIDAD)) {
				restriccion.setCantidadMaxima(new BigDecimal(propiedad.getValor()));
			}
			else if (propiedad.getPropiedad().equals(PROPIEDAD_IMPORTE_MAXIMO)) {
				restriccion.setImporteMaximo(new BigDecimal(propiedad.getValor()));
			}
			else if (propiedad.getPropiedad().equals(PROPIEDAD_IMPORTE_MINIMO)) {
				restriccion.setImporteMaximo(new BigDecimal(propiedad.getValor()));
			}
		}

		log.debug("Finalizada la búsqueda de las propiedades y la carga de estas. ");
		return restriccion;
	}

}
