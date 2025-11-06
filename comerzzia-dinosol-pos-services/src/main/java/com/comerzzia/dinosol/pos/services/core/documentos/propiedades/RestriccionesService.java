package com.comerzzia.dinosol.pos.services.core.documentos.propiedades;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionDocumentoBean;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionLineaBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;

@Component
public class RestriccionesService {

	private static final String PROPIEDAD_IMPORTE_MAXIMO = "IMPORTE_MAXIMO";
	private static final String PROPIEDAD_CANTIDAD = "X_CANTIDAD_MAXIMA";

	private static final String NOMBRE_XML = "restriccionesventa";

	protected static final Logger log = Logger.getLogger(RestriccionesService.class);

	public List<RestriccionLineaBean> restriccionLineas = new ArrayList<RestriccionLineaBean>();
	public RestriccionDocumentoBean restriccionDocumento;

	@Autowired
	protected Sesion sesion;

	/**
	 * Carga el objeto con los datos del XML en caso de no estar ya cargado.
	 * 
	 * @return restriccionLineas
	 * @throws RestriccionesException 
	 */
	public List<RestriccionLineaBean> cargarRestriccionesLineas() throws RestriccionesException {
		if (restriccionLineas.isEmpty()) {
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
	public RestriccionDocumentoBean cargarRestriccionesDocumento(TipoDocumentoBean tipoDocumento) {
		if (restriccionDocumento == null) {
			restriccionDocumento = getRestriccionesBd(tipoDocumento);
		}
		return restriccionDocumento;
	}

	/**
	 * Recorre un archivo XML para cargar las restricciones.
	 * 
	 * @return familiasRestringidasXML
	 * @throws RestriccionesException 
	 */
    public List<RestriccionLineaBean> getRestriccionesXml() throws RestriccionesException {
		/* Traemos la ruta del archivo para poder cargarlo */
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = null;
		try{
			url = comerzziaApp.obtenerUrlFicheroConfiguracion(NOMBRE_XML + ".xml");
		}
		catch(Exception e){
			log.debug("getRestriccionesXML() - " + e.getMessage());
			throw new RestriccionesException(e.getMessage());
		}
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

					/*
					 * Realizamos comprobaciones porque en caso de venir null o vacío no se deberá tener en cuenta estas
					 * variables
					 */
					if (eElement.getElementsByTagName("cantidadmaxima").item(0).getTextContent() != null && !eElement.getElementsByTagName("cantidadmaxima").item(0).getTextContent().isEmpty()) {
						linea.setCantidadMaxima(new BigDecimal(eElement.getElementsByTagName("cantidadmaxima").item(0).getTextContent()));
					}
					if (eElement.getElementsByTagName("preciomaximo").item(0).getTextContent() != null && !eElement.getElementsByTagName("preciomaximo").item(0).getTextContent().isEmpty()) {
						linea.setPrecioMaximo(new BigDecimal(eElement.getElementsByTagName("preciomaximo").item(0).getTextContent()));
					}
					if (eElement.getElementsByTagName("preciominimo").item(0).getTextContent() != null && !eElement.getElementsByTagName("preciominimo").item(0).getTextContent().isEmpty()) {
						linea.setPrecioMinimo(new BigDecimal(eElement.getElementsByTagName("preciominimo").item(0).getTextContent()));
					}

					restricciones.add(linea);
				}
			}

		}
		catch (Exception e) {
			log.error("getRestriccionesXml() - Ha habido un error cargando las restricciones de venta: " + e.getMessage(), e);
		}
		return restricciones;
	}

	/**
	 * Cargamos las propiedades a partir del objeto rescatado en sesión.
	 * 
	 * @param uidActividad
	 * @param idDocumento
	 * @return restriccion
	 */
	public RestriccionDocumentoBean getRestriccionesBd(TipoDocumentoBean tipoDocumento) {
		RestriccionDocumentoBean restriccion = new RestriccionDocumentoBean();
		/* Cargamos el objeto a partir del mapa de propiedades. */
		Map<String, PropiedadDocumentoBean> mapaPropiedades = tipoDocumento.getPropiedades();
		for (Map.Entry<String, PropiedadDocumentoBean> entry : mapaPropiedades.entrySet()) {
			if (entry.getKey().equals(PROPIEDAD_CANTIDAD)) {
				restriccion.setCantidadMaxima(new BigDecimal(entry.getValue().getValor()));
			}
			else if (entry.getKey().equals(PROPIEDAD_IMPORTE_MAXIMO)) {
				restriccion.setImporteMaximo(new BigDecimal(entry.getValue().getValor()));
			}
		}

		return restriccion;
	}

	public String validadorLineas(LineaTicket linea) throws RestriccionesException {
		
		if(restriccionLineas == null && restriccionLineas.isEmpty()){
			try {
				restriccionLineas = cargarRestriccionesLineas();
			}
			catch (RestriccionesException e) {
				log.error("initializeForm() - " + e.getMessage());
				throw new RestriccionesException(e.getMessage());
			}
		}
		
		String codigoArticulo = linea.getArticulo().getCodArticulo();
		String codigoTienda = sesion.getAplicacion().getTienda().getCodAlmacen();
		Map<String, RestriccionLineaBean> resultadosPorPrioridad = new HashMap<String, RestriccionLineaBean>();
		if (!restriccionLineas.isEmpty()) {
			/*
			 * Buscamos las restricciones para el articulo según la tienda y las cargamos en un nuevo listado.
			 */
			for (RestriccionLineaBean restriccion : restriccionLineas) {
				String tienda = restriccion.getTiendas();
				String articulo = restriccion.getCodArt();
				/* Comprobamos que contiene para la tienda y para el artículo */
				if(tienda.contains(codigoTienda) || "*".equals(tienda)){
					if(articulo.contains(codigoArticulo) || "*".equals(articulo)){
						/* Guardamos en el mapa según las prioridades 
						 * 1.- Tienda = 9999 --- Articulo = 08
						 * 2.- Tienda = 9999 --- Articulo = "*"
						 * 3.- Tienda = "*" --- Articulo = 08
						 * 4.- Tienda = "*" --- Articulo = "*" */
						if(!"*".equals(tienda) && !"*".equals(articulo)){
							resultadosPorPrioridad.put("A", restriccion);
						}
						else if(!"*".equals(tienda) && "*".equals(articulo)) {
							resultadosPorPrioridad.put("B", restriccion);
						}
						else if("*".equals(tienda) && !"*".equals(articulo)){
							resultadosPorPrioridad.put("C", restriccion);
						}
						else if("*".equals(tienda) && "*".equals(articulo)){
							resultadosPorPrioridad.put("D", restriccion);
						}
					}
				}
			}
			
			if(!resultadosPorPrioridad.isEmpty()){
				/* Seleccionamos la restricción según la prioridad */
				RestriccionLineaBean restriccion = null;
				if(resultadosPorPrioridad.containsKey("A")){
					restriccion = resultadosPorPrioridad.get("A");
				}
				if(resultadosPorPrioridad.containsKey("B") && restriccion == null){
					restriccion = resultadosPorPrioridad.get("B");		
				}
				if(resultadosPorPrioridad.containsKey("C") && restriccion == null){
					restriccion = resultadosPorPrioridad.get("C");
				}
				if(resultadosPorPrioridad.containsKey("D") && restriccion == null){
					restriccion = resultadosPorPrioridad.get("D");
				}
				
				if(restriccion != null){
					/* Sacamos los datos de la linea y la comparamos con las restricciones. */
					BigDecimal cantidadArticulos = linea.getCantidad();
					if (restriccion.getCantidadMaxima() != null) {
						if (BigDecimalUtil.isMayor(cantidadArticulos.abs(), restriccion.getCantidadMaxima().abs())) {
							return "Superada la cantidad máxima de artículos por linea (" + restriccion.getCantidadMaxima() + ")";
						}
					}
					/* No aplicamos las restricciones de importe a las lineas con descuento. */
					if (BigDecimalUtil.isIgualACero(linea.getDescuento()) && BigDecimalUtil.isIgualACero(linea.getDescuentoManual())) {
						BigDecimal precioLinea = linea.getPrecioSinDto();
						if (restriccion.getPrecioMaximo() != null) {
							if (BigDecimalUtil.isMayor(precioLinea.abs(), restriccion.getPrecioMaximo().abs())) {
								return "Superado el precio máximo por linea (" + restriccion.getPrecioMaximo() + ")";
							}
						}
						if (restriccion.getPrecioMinimo() != null) {
							if (BigDecimalUtil.isMenor(precioLinea.abs(), restriccion.getPrecioMinimo().abs())) {
								return "El precio de la linea es inferior al mínimo establecido (" + restriccion.getPrecioMinimo() + ")";
							}
						}
					}		
				}
			}
		}
		/* Si no se ha provocado ninguna restricción se valida la linea. */
		return null;
	}

}
