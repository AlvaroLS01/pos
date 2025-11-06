package com.comerzzia.dinosol.pos.gui.ventas.rascas.entregafaltantes;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.xml.bind.JAXBElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.documents.LocatorManager;
import com.comerzzia.core.servicios.documents.LocatorParseException;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.rascas.IntroduccionRascasController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.rascas.IntroduccionRascasView;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.DevolucionesController;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.ClientBuilder;
import com.comerzzia.rest.client.exceptions.RestConnectException;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestHttpException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Component
public class EntregaRascasFaltantesController extends Controller {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DevolucionesController.class.getName());

	@Autowired
	private Sesion sesion;

	@Autowired
	private VariablesServices variablesServices;

	@FXML
	protected TextField tfOperacion, tfTienda, tfCodCaja, tfCodDoc, tfDesDoc;

	@FXML
	protected Label lbMensajeError;

	@FXML
	protected Button btAceptar, btDoc;

	protected FormularioConsultaTicketBean frConsultaTicket;

	protected TicketManager ticketManager;

	@Autowired
	protected Documentos documentos;

	@Autowired
	private LocatorManager locatorManager;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);

		frConsultaTicket.setFormField("codTienda", tfTienda);
		frConsultaTicket.setFormField("codOperacion", tfOperacion);
		frConsultaTicket.setFormField("codCaja", tfCodCaja);
		frConsultaTicket.setFormField("tipoDoc", tfCodDoc);
	}

	@Override
	public void initializeComponents() {

		tfCodDoc.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
				if (oldValue) {
					procesarTipoDoc();
				}
			}
		});

		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfOperacion.setUserData(keyboardDataDto);
		tfTienda.setUserData(keyboardDataDto);
		tfCodCaja.setUserData(keyboardDataDto);
		tfCodDoc.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfOperacion);
		addSeleccionarTodoEnFoco(tfTienda);
		addSeleccionarTodoEnFoco(tfCodCaja);
		addSeleccionarTodoEnFoco(tfCodDoc);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = SpringContext.getBean(TicketManager.class);

		tfTienda.setText(sesion.getAplicacion().getTienda().getCodAlmacen());
		tfCodCaja.setText(sesion.getAplicacion().getCodCaja());
		tfOperacion.setText("");

		List<String> tiposDocumentoAbonables = documentos.getTiposDocumentoAbonables();
		if (tiposDocumentoAbonables.isEmpty()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado el tipo de documento nota de crédito en el entorno."), this.getStage());
			btAceptar.setDisable(true);
		}
		else {
			btAceptar.setDisable(false);
		}

		for (String tipoDoc : tiposDocumentoAbonables) {
			try {
				if (documentos.getDocumento(tipoDoc) != null) {
					TipoDocumentoBean docPreseleccion = documentos.getDocumento(tipoDoc);
					tfCodDoc.setText(docPreseleccion.getCodtipodocumento());
					tfDesDoc.setText(docPreseleccion.getDestipodocumento());
					break;
				}
			}
			catch (DocumentoException ex) {
				log.error("initializeForm() - No se ha encontrado el documento asociado", ex);
			}
		}

		frConsultaTicket.limpiarFormulario();
		lbMensajeError.setText("");
		btAceptar.setDisable(false);
	}

	@Override
	public void initializeFocus() {
		tfOperacion.requestFocus();
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
			accionAceptar();
		}
	}

	@FXML
	public void accionAceptar() {
		btAceptar.setDisable(true);
		
		lbMensajeError.setText("");
		if (validarFormularioConsultaCliente()) {

			ticketManager = SpringContext.getBean(TicketManager.class);
			String codTienda = tfTienda.getText();
			String codCaja = tfCodCaja.getText();
			String codigo = tfOperacion.getText();
			String codDoc = tfCodDoc.getText();

			try {
				if (ticketManager.comprobarConfigContador(documentos.getDocumentoAbono(codDoc).getCodtipodocumento())) {
					Long idTipoDocumento = documentos.getDocumento(codDoc).getIdTipoDocumento();
					new RecuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDocumento).start();
				}
				else {
					ticketManager.crearVentanaErrorContador(getStage());
				}
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
			}
		}
	}

	public class RecuperarTicketDevolucion extends BackgroundTask<Boolean> {

		private String codigo;
		private String codTienda, codCaja;
		private Long idTipoDoc;

		public RecuperarTicketDevolucion(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws Exception {
			return ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc);
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}
			btAceptar.setDisable(false);
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			recuperarTicketDevolucionSucceeded(res);
			super.succeeded();
		}

	}

	protected void recuperarTicketDevolucionSucceeded(boolean encontrado) {
		int numRascasConcedidos = ((DinoTicketManager) ticketManager).getRascasConcedidos(ticketManager.getTicketOrigen());
		if(numRascasConcedidos == 0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Este ticket no tiene rascas asociados para entregar."), getStage());
			return;
		}
		
		if (encontrado) {
			new SolicitarRascas(ticketManager.getTicketOrigen().getUidTicket()).start();
		}
		else {
			lbMensajeError.setText(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"));
		}
	}

	@FXML
	public void accionCancelar() {
		getApplication().getMainView().close();
	}

	protected boolean validarFormularioConsultaCliente() {
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultaTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		frConsultaTicket.setCodCaja(tfCodCaja.getText());
		frConsultaTicket.setCodOperacion(tfOperacion.getText());
		frConsultaTicket.setCodTienda(tfTienda.getText());
		frConsultaTicket.setCodDoc(tfCodDoc.getText());

		String codigo = frConsultaTicket.getCodOperacion();
		try {
			locatorManager.decode(codigo);
		}
		catch (LocatorParseException e) {
			// No es localizador válido
			if (codigo.length() > 10) {
				// No es codDocumento válido
				lbMensajeError.setText(I18N.getTexto("El código {0} no es un localizador o un código de documento válido", codigo));
				valido = false;
				return valido;
			}
		}

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioConsultaTicketBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultaTicket);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioConsultaTicketBean> next = constraintViolations.iterator().next();
			frConsultaTicket.setErrorStyle(next.getPropertyPath(), true);
			frConsultaTicket.setFocus(next.getPropertyPath());
			lbMensajeError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	@FXML
	public void accionBuscarTipoDoc() {
		datos = new HashMap<String, Object>();
		datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, documentos.getTiposDocumentoAbonables());

		getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

		if (datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)) {
			TipoDocumentoBean o = (TipoDocumentoBean) datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
			tfCodDoc.setText(o.getCodtipodocumento());
			tfDesDoc.setText(o.getDestipodocumento());
		}
	}

	protected void procesarTipoDoc() {
		String codDoc = tfCodDoc.getText();

		if (!codDoc.trim().isEmpty()) {
			try {
				TipoDocumentoBean documento = documentos.getDocumento(codDoc);
				if (!documentos.getTiposDocumentoAbonables().contains(documento.getCodtipodocumento())) {
					log.warn("Se seleccionó un tipo de documento no válido para la devolución.");
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), getStage());
					tfCodDoc.setText("");
					tfDesDoc.setText("");
				}
				else {
					tfCodDoc.setText(documento.getCodtipodocumento());
					tfDesDoc.setText(documento.getDestipodocumento());
				}
			}
			catch (DocumentoException ex) {
				log.error("Error obteniendo el tipo de documento", ex);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El tipo de documento indicado no existe en la base de datos."), getStage());
				tfCodDoc.setText("");
				tfDesDoc.setText("");
			}
			catch (NumberFormatException nfe) {
				log.error("El id de documento introducido no es válido.", nfe);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El id introducido no es válido."), getStage());
				tfCodDoc.setText("");
				tfDesDoc.setText("");
			}
		}
		else {
			tfDesDoc.setText("");
			tfCodDoc.setText("");
		}
	}

	@SuppressWarnings("rawtypes")
	protected void addSeleccionarTodoEnFoco(final TextField campo) {
		campo.focusedProperty().addListener(new ChangeListener<Boolean>(){

			@Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						if (campo.isFocused() && !campo.getText().isEmpty()) {
							campo.selectAll();
						}
					}
				});
			}
		});
	}

	public class SolicitarRascas extends BackgroundTask<RascasEntregadosResponse> {

		private String uidTicket;

		public SolicitarRascas(String uidTicket) {
			super();
			this.uidTicket = uidTicket;
		}

		@Override
		protected RascasEntregadosResponse call() throws Exception {
			return llamarRestRascas();
		}

		protected RascasEntregadosResponse llamarRestRascas() throws RestHttpException, RestConnectException, RestTimeoutException, RestException {
			log.debug("llamarRestRascas() - Realizando petición de rascas entregados");
			GenericType<JAXBElement<RascasEntregadosResponse>> genericType = new GenericType<JAXBElement<RascasEntregadosResponse>>(){
			};

			try {
				String restUrl = variablesServices.getVariableAsString(VariablesServices.REST_URL);
				WebTarget target = ClientBuilder.getClient().target(restUrl).path("/dinosol/rascas");

				String uidActividad = sesion.getAplicacion().getUidActividad();
				String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);

				target = target.queryParam("api_key", apiKey).queryParam("uid_actividad", uidActividad).queryParam("uid_ticket", uidTicket);
				log.info("llamarRestRascas() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
				RascasEntregadosResponse response = target.request().get(genericType).getValue();
				return response;
			}
			catch (BadRequestException e) {
				throw RestHttpException.establecerException(e);
			}
			catch (WebApplicationException e) {
				throw new RestHttpException(e.getResponse().getStatus(),
				        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
			catch (ProcessingException e) {
				if (e.getCause() instanceof ConnectException) {
					throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
				}
				else if (e.getCause() instanceof SocketTimeoutException) {
					throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
				}
				throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			RascasEntregadosResponse response = getValue();
			mostrarVentanaRascas(response);
		}
		
		@Override
		protected void failed() {
			super.failed();
			
			Throwable e = getException();
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al consultar los rascas que faltan por entregar. Contacte con un administrador."), e);

			btAceptar.setDisable(false);
		}

	}

	private void mostrarVentanaRascas(RascasEntregadosResponse response) {
		int rascasConcedidos = response.getRascasConcedidos();
		int rascasEntregados = response.getRascasEntregados();

		if (rascasConcedidos - rascasEntregados > 0) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(IntroduccionRascasController.PARAM_TICKET, ticketManager.getTicketOrigen());
			params.put(IntroduccionRascasController.PARAM_RASCAS, rascasConcedidos);
			params.put(IntroduccionRascasController.PARAM_RASCAS_ENTREGADOS, rascasEntregados);
			params.put(IntroduccionRascasController.PARAM_ULTIMA_LINEA, response.getUltimaLinea());

			getApplication().getMainView().showModalCentered(IntroduccionRascasView.class, params, getStage());
		}
		else {
			if(response.getUltimaLinea() >= 0) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ya se han entregado todos los rascas de la venta actual."), getStage());
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe esperar unos minutos, el ticket aún no ha sido procesado."), getStage());
			}
		}

		tfOperacion.setText("");
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				btAceptar.setDisable(false);
			}
		});
	}

}
