package com.comerzzia.dinosol.pos.gui.ventas.solicitudfacturacion;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.comerzzia.dinosol.librerias.sap.client.DatosConexionSapDto;
import com.comerzzia.dinosol.librerias.sap.client.PeticionComprobacionActualizacionClienteSAP;
import com.comerzzia.dinosol.librerias.sap.client.PeticionSolicitudFacturaClienteSAP;
import com.comerzzia.dinosol.librerias.sap.client.RespuestaComprobacionActualizacionClienteSAP;
import com.comerzzia.dinosol.librerias.sap.client.RespuestaSolicitudFacturaClienteSAP;
import com.comerzzia.dinosol.librerias.sap.client.SAPClient;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.services.codPostales.DinoCodPostalesServices;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.services.codPostales.CodPostalesException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@Component
public class EnvioSolicitudFacturaController extends WindowController {

	private Logger log = Logger.getLogger(EnvioSolicitudFacturaController.class);

	private static final String CLIENTE_FACTURACION_SAP = "clienteFacturacionSap";
	
	private static final String RESULT_CODE_OK = "OK";
	private static final String RESULT_CODE_KO = "KO";
	private static final String RESULT_CODE_ERROR = "ERROR";

	@FXML
	protected Label lbTitulo;

	@FXML
	protected TextField tfNumeroTicket, tfNombre, tfNif, tfCalle, tfCP, tfCiudad, tfProvincia, tfTelefono, tfEmail;

	@FXML
	protected CheckBox cbEnviarCopiaTienda;
	
	@FXML
	protected Button btAceptar, btCancelar;

	@Autowired
	private Sesion sesion;

	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	protected DinoCodPostalesServices dinoCodPostalesServices;

	private String codTicket;
	
	private boolean esAltaCliente;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setPintarPiePantalla(true);
		tfNumeroTicket.setUserData(keyboardDataDto);
		tfNombre.setUserData(keyboardDataDto);
		tfNif.setUserData(keyboardDataDto);
		tfCalle.setUserData(keyboardDataDto);
		tfCP.setUserData(keyboardDataDto);
		tfTelefono.setUserData(keyboardDataDto);
		tfCiudad.setUserData(keyboardDataDto);
		tfProvincia.setUserData(keyboardDataDto);
		tfEmail.setUserData(keyboardDataDto);
		
		tfCP.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				autorellenarProvincia();
            }
		});
	
	}

	@Override
	public void initializeFocus() {
		btAceptar.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfNumeroTicket.setEditable(false);
		cbEnviarCopiaTienda.setSelected(false);
		
		setTfEditable(true);		
		esAltaCliente = false;

		limpiarCampos();

		RespuestaComprobacionActualizacionClienteSAP datosClienteFacturacion = (RespuestaComprobacionActualizacionClienteSAP) getDatos().get(CLIENTE_FACTURACION_SAP);
		if (datosClienteFacturacion == null || (datosClienteFacturacion.getNumeroIdentificacion() == null || datosClienteFacturacion.getNumeroIdentificacion().isEmpty())) {
			esAltaCliente = true;

			if (getDatos().containsKey(ComprobacionClienteFacturaController.NIF_BUSQUEDA)) {
				String nif = (String) getDatos().get(ComprobacionClienteFacturaController.NIF_BUSQUEDA);
				tfNif.setText(nif);

				getDatos().remove(ComprobacionClienteFacturaController.NIF_BUSQUEDA);
			}
		}
		else {
			if (datosClienteFacturacion != null) {
				rellenarFomulario(datosClienteFacturacion);
				tfEmail.setEditable(true);
			}
		}
		
		if(!esAltaCliente) {
			bloquearFormulario();
		}

		codTicket = (String) getDatos().get(SolicitudFacturacionController.PARAM_CODTICKET);
		tfNumeroTicket.setText(codTicket);
	}

	private void bloquearFormulario() {
		log.debug("bloquearFormulario() - Bloquear formulario de edición de datos del cliente.");
		setTfEditable(false);
		tfEmail.setEditable(true);
	}

	public void rellenarFomulario(RespuestaComprobacionActualizacionClienteSAP datosClienteFacturacion) {

		if (datosClienteFacturacion != null) {			
			if ((datosClienteFacturacion.getNombre() != null)) {
				if (!(datosClienteFacturacion.getNombre()).isEmpty()) {
					tfNombre.setText(datosClienteFacturacion.getNombre());
				}
				else {
					datosClienteFacturacion.setNombre(tfNombre.getText());
				}
			}
			else {
				datosClienteFacturacion.setNombre(tfNombre.getText());
			}

			if (datosClienteFacturacion.getNumeroIdentificacion() != null) {
				tfNif.setText(datosClienteFacturacion.getNumeroIdentificacion());
			}
			else {
				datosClienteFacturacion.setNumeroIdentificacion(tfNif.getText());

			}

			if (!(datosClienteFacturacion.getCalle().isEmpty())) {
				tfCalle.setText(datosClienteFacturacion.getCalle());
			}
			else {
				datosClienteFacturacion.setCalle(tfCalle.getText());
			}

			if (datosClienteFacturacion.getCodigoPostal() != null && !datosClienteFacturacion.getCodigoPostal().isEmpty()) {
				tfCP.setText(datosClienteFacturacion.getCodigoPostal());
				autorellenarProvincia();
			}
			else {
				tfCP.setText("");
				tfProvincia.setText("");
				tfCiudad.setText("");
			}

			if (!(datosClienteFacturacion.getPoblación().isEmpty())) {
				tfCiudad.setText(datosClienteFacturacion.getPoblación());
			}
			else {
				datosClienteFacturacion.setPoblación(tfCiudad.getText());
			}

			if (!(datosClienteFacturacion.getTelefono().isEmpty())) {
				tfTelefono.setText(datosClienteFacturacion.getTelefono());
			}
			else {
				datosClienteFacturacion.setTelefono(tfTelefono.getText());
			}

			if (!(datosClienteFacturacion.getEmail().isEmpty())) {
				tfEmail.setText(datosClienteFacturacion.getEmail());
			}
			else {
				datosClienteFacturacion.setEmail(tfEmail.getText());
			}
		}

	}

	@FXML
	public void accionCancelar() {
		getStage().close();
	}

	@FXML
	public void accionAceptar() {
		/* Validamos los campos de formulario */
		String mensajeValidador = validarCampos();
		if (StringUtils.isNotBlank(mensajeValidador)) {
			log.info("accionAceptar() - " + mensajeValidador);
			VentanaDialogoComponent.crearVentanaAviso(mensajeValidador, getStage());
			return;
		}

		if (esAltaCliente) {
			new CrearClienteTask().start();
		}
		else {
			new SolicitudFacturaClienteTask().start();
		}
	}

	public String validarCampos() {
		if (StringUtils.isBlank(tfNombre.getText())) {
			return "El nombre del cliente es obligatorio.";
		}
		if (StringUtils.isBlank(tfNif.getText())) {
			return "El NIF del cliente es obligatorio.";
		}
		if (StringUtils.isBlank(tfCalle.getText())) {
			return "La calle es obligatoria.";
		}
		if (StringUtils.isBlank(tfCP.getText())) {
			return "El código postal es obligatorio.";
		}
		if (StringUtils.isBlank(tfCiudad.getText())) {
			return "La ciudad es obligatoria.";
		}
		if (StringUtils.isBlank(tfProvincia.getText())) {
			return "La provincia es obligatoria.";
		}
		
//		if (StringUtils.isBlank(tfTelefono.getText())) {
//			return "El teléfono es obligatorio.";
//		}
		
		if(!cbEnviarCopiaTienda.isSelected() && StringUtils.isBlank(tfEmail.getText())) {
			return I18N.getTexto("Debe indicar el correo electrónico o solicitar el envío de la copia a tienda.");
		}
		
		if (StringUtils.isNotBlank(tfEmail.getText())) {
			/* Patrón para validar el Email introducido */
			Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
			Matcher mather = pattern.matcher(tfEmail.getText());
			/* Si es diferente de true, es que está mal escrito */
			if (mather.find() != true) {
				return "El formato del correo electrónico no es válido.";
			}
		}
		
		return "";
	}

	private class SolicitudFacturaClienteTask extends BackgroundTask<RespuestaSolicitudFacturaClienteSAP> {

		public SolicitudFacturaClienteTask() {
			super();
		}

		@Override
		protected RespuestaSolicitudFacturaClienteSAP call() throws Exception {
			DatosConexionSapDto datosConexion = new DatosConexionSapDto();
			String urlBase = variablesServices.getVariableAsString(SAPClient.ID_VARIABLE_URL_WS_SAP);
			datosConexion.setUrl(urlBase);
			String username = variablesServices.getVariableAsString(SAPClient.ID_USERNAME_WS_SAP);
			datosConexion.setUsername(username);
			String password = variablesServices.getVariableAsString(SAPClient.ID_PASSWORD_WS_SAP);
			datosConexion.setPassword(password);

			SAPClient clienteSap = new SAPClient();
			PeticionSolicitudFacturaClienteSAP peticionSolicitud = crearPeticionSolicitud();
			return clienteSap.solicitarFactura(datosConexion, peticionSolicitud);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			RespuestaSolicitudFacturaClienteSAP facturaSolicitada = getValue();
			if ((RESULT_CODE_OK).equals(facturaSolicitada.getResultCode())) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(facturaSolicitada.getMessageCode()), getStage());
			}
			else if ((RESULT_CODE_KO).equals(facturaSolicitada.getResultCode()) || (RESULT_CODE_ERROR).equals(facturaSolicitada.getResultCode())) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(facturaSolicitada.getMessageCode()), getStage());
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(facturaSolicitada.getResultCode() + " - " + facturaSolicitada.getMessageCode()), getStage());
			}

			getStage().close();

		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al solicitar la factura para el cliente con NIF: " + tfNif.getText();
			log.error("SolicitudFacturaClienteTask() - " + mensajeError + " - " + exception.getMessage(), getException());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
			
			getStage().close();
		}

	}

	public PeticionSolicitudFacturaClienteSAP crearPeticionSolicitud() {
		PeticionSolicitudFacturaClienteSAP peticion = new PeticionSolicitudFacturaClienteSAP();
		peticion.setCity(tfCiudad.getText());
		peticion.setEmail(tfEmail.getText());
		peticion.setName(tfNombre.getText());
		peticion.setNif(tfNif.getText());
		peticion.setOrderId(codTicket.replaceAll("/", ""));
		peticion.setPhone(tfTelefono.getText());
		peticion.setPostCode(tfCP.getText());
		peticion.setProvince(tfProvincia.getText());
		peticion.setStreet(tfCalle.getText());

		if (cbEnviarCopiaTienda.isSelected()) {
			String emailTienda = sesion.getAplicacion().getTienda().getCliente().getEmail();
			if(StringUtils.isNotBlank(emailTienda)) {
				peticion.setEmail2(emailTienda);
			}
		}

		return peticion;
	}

	protected void autorellenarProvincia() {
		log.debug("autorellenarProvincia() - Rellenando campo Provincia a partir de los dos primeros dígitos del código postal");

		String codigoPostal = tfCP.getText();
		try {
			if (StringUtils.isNotBlank(codigoPostal)) {
				log.debug("autorellenarProvincia() - Código postal relleno: " + codigoPostal);
				
				String provincia = dinoCodPostalesServices.buscaProvinciaPorCP(codigoPostal);
				tfProvincia.setText(provincia);
			}
		}
		catch (CodPostalesException e) {
			log.error("autorellenarProvincia() - Error rellenando el campo Provincia: " + e.getMessage(), e);
			tfProvincia.setText("");
		}
	}
	
	
	protected void setTfEditable(boolean editable) {
		tfNombre.setEditable(editable);
		tfNif.setEditable(editable);
		tfCalle.setEditable(editable);
		tfCP.setEditable(editable);
		tfTelefono.setEditable(editable);
		tfCiudad.setEditable(editable);
		tfProvincia.setEditable(editable);
		tfEmail.setEditable(editable);
	}
	
	
	private class CrearClienteTask extends BackgroundTask<RespuestaComprobacionActualizacionClienteSAP> {

		public CrearClienteTask() {
			super();
		}
		
		@Override
		protected RespuestaComprobacionActualizacionClienteSAP call() throws Exception {
			DatosConexionSapDto datosConexion = new DatosConexionSapDto();
			String urlBase = variablesServices.getVariableAsString(SAPClient.ID_VARIABLE_URL_WS_SAP);
			datosConexion.setUrl(urlBase);
			String username = variablesServices.getVariableAsString(SAPClient.ID_USERNAME_WS_SAP);
			datosConexion.setUsername(username);
			String password = variablesServices.getVariableAsString(SAPClient.ID_PASSWORD_WS_SAP);
			datosConexion.setPassword(password);

			SAPClient clienteSap = new SAPClient();
			return clienteSap.actualizarCrearCliente(datosConexion, crearPeticionSolicitudCrearModCliente());
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			RespuestaComprobacionActualizacionClienteSAP cliente = getValue();
			
			if (("OK").equals(cliente.getResultCode())) {	
				
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Se ha creado el cliente correctamente, se procede a solicitar la factura."), getStage());
				
				DatosConexionSapDto datosConexion = new DatosConexionSapDto();
				String urlBase = variablesServices.getVariableAsString(SAPClient.ID_VARIABLE_URL_WS_SAP);
				datosConexion.setUrl(urlBase);
				String username = variablesServices.getVariableAsString(SAPClient.ID_USERNAME_WS_SAP);
				datosConexion.setUsername(username);
				String password = variablesServices.getVariableAsString(SAPClient.ID_PASSWORD_WS_SAP);
				datosConexion.setPassword(password);

				SAPClient clienteSap = new SAPClient();
				PeticionSolicitudFacturaClienteSAP peticionSolicitud = crearPeticionSolicitud();

				RespuestaSolicitudFacturaClienteSAP facturaSolicitada = null;
				try {
					facturaSolicitada = clienteSap.solicitarFactura(datosConexion, peticionSolicitud);

					if (("OK").equals(facturaSolicitada.getResultCode())) {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(facturaSolicitada.getMessageCode()), getStage());
					}

					if (("KO").equals(facturaSolicitada.getResultCode())) {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto(facturaSolicitada.getMessageCode()), getStage());
					}

				}
				catch (Exception e) {
					String mensajeError = "Se ha producido un error al solicitar la factura para el cliente con NIF: " + tfNif.getText();
					log.error("CrearClienteTask() - " + mensajeError + " - " + e.getMessage(), getException());
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
				}
				
				getStage().close();
			}
			else if (("KO").equals(cliente.getResultCode())) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(cliente.getMessageCode()), getStage());
			}
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al crear el cliente con NIF: "+tfNif.getText();
			log.error("CrearClienteTask() - " + mensajeError + " - " + exception.getMessage(), getException());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}

	}
	
	public PeticionComprobacionActualizacionClienteSAP crearPeticionSolicitudCrearModCliente() {
		PeticionComprobacionActualizacionClienteSAP peticion = new PeticionComprobacionActualizacionClienteSAP();
		peticion.setCity(tfCiudad.getText());
		String email = tfEmail.getText();
		if(StringUtils.isBlank(email)) {
			email = sesion.getAplicacion().getTienda().getCliente().getEmail(); 
		}
		peticion.setEmail(email);
		peticion.setName(tfNombre.getText());
		peticion.setNif(tfNif.getText());
		peticion.setOrderId(codTicket.replaceAll("/", ""));
		peticion.setPhone(tfTelefono.getText());
		peticion.setPostCode(tfCP.getText());
		peticion.setProvince(tfProvincia.getText());
		peticion.setStreet(tfCalle.getText());

		return peticion;
	}

	private void limpiarCampos() {
		tfCiudad.setText("");
		tfEmail.setText("");
		tfNombre.setText("");
		tfNif.setText("");
		tfTelefono.setText("");
		tfCP.setText("");
		tfProvincia.setText("");
		tfCalle.setText("");
	}
}
