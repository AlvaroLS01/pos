package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.consultastock;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.comerzzia.cardoso.pos.services.atractor.SincronizacionPortTypeProxy;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosView;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK
 */
@Component
public class ConsultaStockController extends WindowController implements Initializable{

	private static final Logger log = Logger.getLogger(ConsultaStockController.class.getName());

	public static final String ORIGEN_CONSULTA_STOCK = "ORIGEN_CONSULTA_STOCK";
	
	@FXML
	protected TextField tfArticulo;
	
	@Autowired
	private Sesion sesion;

	@Override
	public void initialize(URL url, ResourceBundle rb){
		log.debug("initialize() : GAP - PERSONALIZACIONES V3 - CONSULTA DE STOCK");
	}

	@Override
	public void initializeComponents() throws InitializeGuiException{
		registrarAccionCerrarVentanaEscape();
		registraEventoTeclado(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent arg0){
				if(arg0.getCode().equals(KeyCode.ENTER)){
					accionConsultaStock();
				}
			}
		}, KeyEvent.KEY_RELEASED);
	}

	@Override
	public void initializeForm() throws InitializeGuiException{}

	@Override
	public void initializeFocus(){
		tfArticulo.setText("");
		tfArticulo.requestFocus();
	}

	public void accionConsultaStock(){
		if(tfArticulo.getText().isEmpty()){
			tfArticulo.requestFocus();
			return;
		}

		XMLDocument xml = new XMLDocument();
		xml.getDocument().setXmlVersion("1.0");
		XMLDocumentNode root = new XMLDocumentNode(xml, "TPV");
		XMLDocumentNode usr = new XMLDocumentNode(xml, "USR");
		usr.setValue("TPV_CMZ");
		XMLDocumentNode id_pda = new XMLDocumentNode(xml, "ID_PDA");
		id_pda.setValue("TPV_CMZ");
		XMLDocumentNode op = new XMLDocumentNode(xml, "OP");
		op.setValue("STOCK");
		XMLDocumentNode codalm = new XMLDocumentNode(xml, "CODALM");
		codalm.setValue(sesion.getAplicacion().getCodAlmacen());
		XMLDocumentNode articulo = new XMLDocumentNode(xml, "ARTICULO");
		articulo.setValue(tfArticulo.getText());

		root.añadirHijo(usr);
		root.añadirHijo(id_pda);
		root.añadirHijo(op);
		root.añadirHijo(codalm);
		root.añadirHijo(articulo);

		xml.setRoot(root);

		ConsultaStockTask consultaStockTask = new ConsultaStockTask(xml);
		consultaStockTask.start();
	}

	protected class ConsultaStockTask extends BackgroundTask<String>{
		
		private final XMLDocument xmlStock;
		
		public ConsultaStockTask(XMLDocument xmlStock){
			this.xmlStock = xmlStock;
		}
		@Override
		protected String call() throws Exception{
			SincronizacionPortTypeProxy portTypeProxy = new SincronizacionPortTypeProxy(dameURL());
			return portTypeProxy.peticion(xmlStock.getString("iso-8859-1"));
		}

		@Override
		protected void succeeded(){
			super.succeeded();
			try{
				String respuesta = getValue();
				log.debug("succeeded() - RESPUESTA CONSULTA DE ARTÍCULOS : " + respuesta);
				xmlStock.setDocument(respuesta);
				
				/* Sacamos el dato del STOCK del XML : 
				 * <?xml version="1.0" encoding="UTF-8"?>
				 * <TPV>
				 * 	<STOCK>0</STOCK>
				 * </TPV>
				 * */
				String stockXML = "";
				Document doc = convertStringToXMLDocument(respuesta);
				if(doc != null){
					stockXML = doc.getFirstChild().getFirstChild().getTextContent();
				}
				if(StringUtils.isBlank(stockXML)){
					String msgError = "La respuesta recibida no tiene el formato correcto, no se ha podido obtener el stock del artículo.";
					throw new Exception(msgError);
				}
				
				String msgInfo = I18N.getTexto("El stock disponible del artículo es: ") + stockXML;
				VentanaDialogoComponent.crearVentanaInfo(msgInfo, getStage());
			}
			catch(Exception e){
				String msgError = I18N.getTexto("No se ha podido obtener el stock del artículo.") + "\r\n"  + e.getMessage();
				log.error("ConsultaStockTask/succeeded() - " + msgError, e);
				VentanaDialogoComponent.crearVentanaError(msgError, getStage());
			}
			tfArticulo.selectAll();
			tfArticulo.requestFocus();
		}

		@Override
		protected void failed(){
			super.failed();
			log.error("failed() - " + I18N.getTexto("Error consultando stock"));
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido obtener el stock del artículo: ") + getCMZException().getMessageI18N(), getStage());
			tfArticulo.selectAll();
			tfArticulo.requestFocus();
		}
	}

	public Document convertStringToXMLDocument(String xmlString){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try{
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void accionBuscarArticulo(){
		datos = new HashMap<>();
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE, sesion.getAplicacion().getTienda().getCliente());
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CODTARIFA, sesion.getAplicacion().getTienda().getCliente().getCodtar());
		//getDatos().put(ORIGEN_CONSULTA_STOCK, true);
		getDatos().put(SeleccionUsuarioController.PARAMETRO_ES_STOCK, Boolean.FALSE);
		getApplication().getMainView().showModal(BuscarArticulosView.class, getDatos());
		initializeFocus();
		if(datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)){
			tfArticulo.setText((String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_CODART));
		}
	}

	private String dameURL(){
		URL url = null;
		File file;
		String cmzHomePath = System.getenv("COMERZZIA_HOME"), wsprop = "ws.properties", resultado = "";
		cmzHomePath = cmzHomePath == null ? "" : cmzHomePath;
		try{
			if(!cmzHomePath.endsWith(File.separator)){
				cmzHomePath += File.separator;
			}
			file = new File(cmzHomePath + wsprop);
			if(file.exists()){
				url = file.toURI().toURL();
			}
		}
		catch(MalformedURLException e){
			url = null;
		}
		if(url == null){
			url = Thread.currentThread().getContextClassLoader().getResource(wsprop);
		}
		log.info("dameURL() - Consulta stock : Usando fichero [" + url.toString() + "] para configuracion de consulta de stock");
		try{
			file = new File(url.toURI());
		}
		catch(Exception e){
			log.debug("dameURL() - Consulta stock : Error al cargar el fichero de configuracion no encontrado : " + e.getMessage());
			return resultado;
		}
		if(!file.exists()){
			log.debug("dameURL() - Consulta stock : Fichero de configuracion no encontrado");
			return resultado;
		}
		Properties urlServicioStrock = new Properties();
		FileInputStream fichero;
		try{
			fichero = new FileInputStream(file);
			try{
				urlServicioStrock.load(fichero);
				resultado = urlServicioStrock.getProperty("Url");
			}
			finally{
				try{
					if(fichero != null){
						fichero.close();
					}
				}
				catch(Exception e2){
				}
			}

		}
		catch(Exception ex){
			log.debug("dameURL() - Consulta stock Error al leer la url : " + ex.getMessage());
			resultado = "";
		}
		log.debug("dameURL() - Consulta stock url servicio web : " + resultado);
		return resultado;
	}
	
}
