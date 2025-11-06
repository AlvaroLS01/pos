package com.comerzzia.dinosol.pos.gui.ventas.solicitudfacturacion;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.dinosol.librerias.sap.client.DatosConexionSapDto;
import com.comerzzia.dinosol.librerias.sap.client.RespuestaComprobacionActualizacionClienteSAP;
import com.comerzzia.dinosol.librerias.sap.client.SAPClient;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class ComprobacionClienteFacturaController extends WindowController {

	private Logger log = Logger.getLogger(ComprobacionClienteFacturaController.class);

	public static final String CLIENTE_FACTURACION_SAP = "clienteFacturacionSap";
	public static final String ACCION_CANCELAR = "accionCancelar";
	public static final String NIF_BUSQUEDA = "nifBusqueda";

	@FXML
	protected Label lbTitulo, lbError;
	@FXML
	protected TextField tfNif;
	@FXML
	protected Button btAceptar, btCancelar;

	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	private Sesion sesion;

	@Autowired
	private TiposIdentService tiposIdentService;

	// @Autowired
	// private TicketManager ticketManager;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		registrarAccionCerrarVentanaEscape();

		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfNif.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfNif);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		tfNif.clear();
	}

	@Override
	public void initializeFocus() {
		tfNif.requestFocus();
	}

	public void limpiarDatosPantalla() {
		tfNif.clear();
	}

	protected boolean validarFormulario() throws TiposIdentNotFoundException, TiposIdentServiceException {
		Boolean sResultado = null;
		if (tfNif.getText().isEmpty()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El NIF es obligatorio"), getStage());
		}
		else {
			String codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
			List<TiposIdentBean> tiposIdentificacion = tiposIdentService.consultarTiposIdent(null, true, codPais);
			TiposIdentBean tipoIden = tiposIdentificacion.get(0);

			if (tipoIden != null && StringUtils.isNotBlank(tipoIden.getCodTipoIden()) && StringUtils.isNotBlank(tipoIden.getClaseValidacion()) && StringUtils.isNotBlank(tfNif.getText())) {
				String claseValidacion = tipoIden.getClaseValidacion();
				String cif = tfNif.getText();
				try {
					IValidadorDocumentoIdentificacion validadorDocumentoIdentificacion = (IValidadorDocumentoIdentificacion) Class.forName(claseValidacion).newInstance();
					if (!validadorDocumentoIdentificacion.validarDocumentoIdentificacion(cif)) {
						sResultado = false;
					}
					else {
						sResultado = true;
					}
				}
				catch (Exception e) {
					log.error("validarDocumento() - Ha habido un error al intentar validar el documento: " + e.getMessage());
				}
			}
			else {
				sResultado = true;
			}
		}
		return sResultado;
	}

	@FXML
	public void accionCancelar() {
		getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
		getStage().close();
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
			accionAceptar();
		}
	}

	@FXML
	public void accionAceptar() {
		log.debug("accionAceptar() - Se va a comprobar si existe el cliente con NIF: " + tfNif.getText());
		if (tfNif.getText() == null || tfNif.getText().isEmpty()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El NIF es obligatorio"), getStage());
		}
		else {
			new ComprobacionClienteTask().start();
		}
	}

	private class ComprobacionClienteTask extends BackgroundTask<RespuestaComprobacionActualizacionClienteSAP> {

		public ComprobacionClienteTask() {
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
			return clienteSap.getDatosCliente(datosConexion, tfNif.getText());
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			RespuestaComprobacionActualizacionClienteSAP clienteFacturacion = getValue();

			getDatos().put(CLIENTE_FACTURACION_SAP, clienteFacturacion);
			getDatos().put(NIF_BUSQUEDA, tfNif.getText());
			getStage().close();
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error al consulta el cliente con NIF: " + tfNif.getText();
			log.error("ComprobacionClienteTask() - " + mensajeError + " - " + exception.getMessage(), getException());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}

	}

}
