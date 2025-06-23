package com.comerzzia.bimbaylola.pos.services.dispositivofirma;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.core.util.xml.XMLDocumentNodeNotFoundException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.devices.config.ConfigDispositivosLoadException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class ByLDispositivosFirma {

	private static final Logger log = Logger.getLogger(ByLDispositivosFirma.class.getName());

	private static ByLDispositivosFirma instance = null;

	public static final String TAG_FIRMAS = "firmas";
	public static final String TAG_METODOS_CONEX = "metodosconexion";
	public static final String TAG_MODELO = "modelo";
	public static final String TAG_NOMBRE_CONEXION = "nombreconexion";
	public static final String TAG_PARAMETROS_CONFIGURACION = "parametrosConfiguracion";

	public static final String ATT_MODELO = "modelo";
	public static final String ATT_NOMBRE_CONEX = "nombreconexion";
	public static final String ATT_TIPO_CONEX = "tipoconexion";
	public static final String ATT_MANEJADOR = "manejador";
	public static final String OPCION_FIRMA = "FIRMA";

	@Autowired
	protected Sesion sesion;
	@Autowired
	private ApplicationContext context;

	protected HashMap<String, DispositivoFirma> dispositivosFirma;
	protected IFirma dispositivoFirmaActual;

	public ByLDispositivosFirma() {
	}

	public static ByLDispositivosFirma getInstance() {
		if (instance == null) {
			instance = new ByLDispositivosFirma();
		}
		return instance;
	}

	public static void setCustomInstance(ByLDispositivosFirma instance) {
		ByLDispositivosFirma.instance = instance;
	}

	public HashMap<String, DispositivoFirma> getDispositivosFirma() {
		return dispositivosFirma;
	}

	public void setDispositivosFirma(HashMap<String, DispositivoFirma> dispositivosFirma) {
		this.dispositivosFirma = dispositivosFirma;
	}

	public IFirma getDispositivoFirmaActual() {
		return dispositivoFirmaActual;
	}

	public void setDispositivoFirmaActual(IFirma dispositivoFirmaActual) {
		this.dispositivoFirmaActual = dispositivoFirmaActual;
	}

	public void cargarDispositivosFirma() {
		log.debug("cargarDispositivosFirma() - realizamos la carga de todos los dispositivos de firma disponibles");
		String tipConex, metConex, manejador;
		List<XMLDocumentNode> tiposDispositivosFirma;
		List<XMLDocumentNode> tiposMetConex;

		dispositivosFirma = new HashMap<String, DispositivoFirma>();

		dispositivosFirma.put(I18N.getTexto("NO USA"), null);

		HashMap<String, ByLConfiguracionModelo> listaConfiguracion;

		try {
			XMLDocument xmlDevices = cargarXMLConfDispositivos();
			XMLDocumentNode nodeDispositivos = xmlDevices.getRoot();
			XMLDocumentNode nodeFirmas = nodeDispositivos.getNodo(TAG_FIRMAS);

			tiposDispositivosFirma = nodeFirmas.getHijos();

			for (XMLDocumentNode nodeFirma : tiposDispositivosFirma) {
				tiposMetConex = nodeFirma.getNodo(TAG_METODOS_CONEX).getHijos();

				listaConfiguracion = new HashMap<String, ByLConfiguracionModelo>();
				for (XMLDocumentNode nodeMetConex : tiposMetConex) {
					metConex = nodeMetConex.getAtributoValue(ATT_NOMBRE_CONEX, true);
					tipConex = nodeMetConex.getAtributoValue(ATT_TIPO_CONEX, true);
					manejador = nodeMetConex.getAtributoValue(ATT_MANEJADOR, true);

					ByLConfiguracionModelo configuracion = new ByLConfiguracionModelo(nodeFirma, nodeMetConex, metConex, tipConex, manejador);
					listaConfiguracion.put(metConex, configuracion);
				}

				DispositivoFirma dispositivoFirma = new DispositivoFirma(nodeFirma.getAtributoValue(ATT_MODELO, true));
				dispositivoFirma.setListaConfiguracion(listaConfiguracion);
				dispositivosFirma.put(nodeFirma.getAtributoValue(ATT_MODELO, true), dispositivoFirma);
			}

			cargarDispositivoFirmaActual();
		}
		catch (ConfigDispositivosLoadException | XMLDocumentNodeNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void cargarDispositivoFirmaActual() {
		// Vemos si tiene ya configurado un dispositivo de firma para añadirlo a DispositivoFirmaActual
		log.debug("cargarDispositivoFirmaActual() - realizamos la carga del dispositivo de firma actual");
		sesion = SpringContext.getBean(Sesion.class);
		byte[] configuracionCaja = sesion.getAplicacion().getTiendaCaja().getConfiguracion();
		Document doc = null;

		if (configuracionCaja != null) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();

				doc = builder.parse(new ByteArrayInputStream(configuracionCaja));

				Element dispositivosNode = XMLDocumentUtils.getElement(doc.getDocumentElement(), "dispositivos", true);
				if (dispositivosNode != null) {
					Element firmaNode = XMLDocumentUtils.getElement(dispositivosNode, "firma", true);

					if (firmaNode != null) {

						String modelo = XMLDocumentUtils.getTagValueAsString(firmaNode, ATT_MODELO, true);
						String nombreConexion = XMLDocumentUtils.getTagValueAsString(firmaNode, ATT_NOMBRE_CONEX, true);

						IFirma dispositivoFirmaActual;
						if (modelo.equals(I18N.getTexto("NO USA"))) {
							dispositivoFirmaActual = new FirmaNoConfig();
							dispositivoFirmaActual.setModelo(modelo);
						}
						else {
							IFirma dispositivoFirma = ByLDispositivosFirma.getInstance().getDispositivosFirma().get(modelo);
							ByLConfiguracionModelo configuracionModelo = dispositivoFirma.getListaConfiguracion().get(nombreConexion);

							dispositivoFirmaActual = getDispositivoIFirma(configuracionModelo.getManejador());
							dispositivoFirmaActual.setConfiguracionActual(configuracionModelo);
							dispositivoFirmaActual.setModelo(modelo);

							dispositivoFirmaActual.iniciarDispositivoFirma();
							log.debug("cargarDispositivoFirmaActual() - Dispositivo de firma iniciado");
						}

						ByLDispositivosFirma.getInstance().setDispositivoFirmaActual(dispositivoFirmaActual);
					}
				}
			}
			catch (ParserConfigurationException | SAXException | IOException | XMLDocumentException e) {
				log.debug("cargarDispositivoFirmaActual() - Ha occurido un error en la carga del dispositivo de firma actual: " + e.getMessage());
			}
			catch (Exception e) {
				log.debug("cargarDispositivoFirmaActual() - Ha occurido un error no controlado en la carga del dispositivo de firma actual: " + e.getMessage());
			}

		}

	}

	protected XMLDocument cargarXMLConfDispositivos() throws ConfigDispositivosLoadException {
		XMLDocument xmlDocument;
		try {
			// Cargar fichero de configuración
			URL url = Thread.currentThread().getContextClassLoader().getResource("devices.xml");
			if (url == null) {
				log.error("cargarXMLConfDispositivos() - No se ha encontrado el fichero de configuración de dispositivos: devices.xml");
				throw new RuntimeException("No se ha encontrado el fichero de configuración de dispositivos: devices.xml");
			}
			xmlDocument = new XMLDocument(url);
			log.info("Usando URL [" + url.toString() + "] para configuración de dispositivos.");
			return xmlDocument;
		}
		catch (Exception e) {
			log.fatal("Error al cargar configuración de dispositivos : " + e.getMessage());
			throw new ConfigDispositivosLoadException("Error cargando fichero devices.xml con configuración de dispositivos. ", e);
		}
	}

	public IFirma getDispositivoIFirma(String manejador) {
		IFirma dispositivoFirma = null;
		try {
			Class<?> clazz = null;

			clazz = Class.forName(manejador);

			try {
				dispositivoFirma = (IFirma) context.getBean(clazz);
			}
			catch (Exception e) {
				dispositivoFirma = (IFirma) clazz.newInstance();
			}

		}
		catch (Exception e1) {
			e1.printStackTrace();
		}

		return dispositivoFirma;
	}

}
