package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.recuperarticket;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.DomicilioOfflineDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.EnvioDomicilioView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADController;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RecuperarTicketSadBusquedaException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasServiciosException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import feign.RetryableException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Esta es la pantalla a la que se entra y en la que se busca el ticket
 *
 */
@Component
public class RecuperarTicketSadController extends WindowController {

	private static final Logger log = Logger.getLogger(RecuperarTicketSadController.class);

	@Autowired
	protected Sesion sesion;

	@FXML
	protected TextField tfOperacion, tfCodDoc, tfDesDoc;
	@FXML
	protected Button btAceptar, btDoc;
	@FXML
	protected Label lbMensajeError;

	protected TicketManager ticketManager;

	private DomicilioApi domicilioService;

	private String sherpaAdressCode;
	private Boolean realizarAlta;

	private KeyboardDataDto keyboardDataDto;

	public static final String COD_DOCU_SAD = "DOCSAD";

	@Autowired
	protected VariablesServices variablesServices;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setVisibleAlInicio(false);
		keyboardDataDto.setPintarPiePantalla(true);
		tfOperacion.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfOperacion);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = SpringContext.getBean(TicketManager.class);

		limpiarCampos();

		/* Bloqueamos los campos superiores para que se tenga que usar el buscador */
		tfCodDoc.setEditable(false);
		tfDesDoc.setEditable(false);

		lbMensajeError.setText("");
	}

	@Override
	public void initializeFocus() {
		tfOperacion.requestFocus();
	}

	/**
	 * Limpiamos los campos de la pantalla
	 */
	public void limpiarCampos() {
		try {
			TipoDocumentoBean tipoDocumento = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
			tfCodDoc.setText(tipoDocumento.getCodtipodocumento());
			tfDesDoc.setText(tipoDocumento.getDestipodocumento());
		}
		catch (DocumentoException e) {
			log.error("initializeForm() - Ha habido un error al recuperar el tipo de documento: " + e.getMessage(), e);
		}
		tfOperacion.clear();

		sherpaAdressCode = null;
		realizarAlta = false;
	}

	@FXML
	public void accionBuscar() {
		/* Limpiamos los errores anteriores */
		lbMensajeError.setText("");

		/* Validamos los datos de la pantalla y devolvemos el error en caso de producirse */
		String mensajeError = validarDatosPantalla();
		if (StringUtils.isNotEmpty(mensajeError)) {
			log.error("accionBuscar() - " + mensajeError);
			lbMensajeError.setText(I18N.getTexto(mensajeError));
		}
		else {
			String codTienda = sesion.getAplicacion().getTienda().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();
			String codigo = tfOperacion.getText();
			String codDoc = tfCodDoc.getText();

			try {
				Long idTipoDocumento = sesion.getAplicacion().getDocumentos().getDocumento(codDoc).getIdTipoDocumento();
				new RecuperarTicketServicioDomicilio(codigo, codTienda, codCaja, idTipoDocumento).start();

				tfOperacion.requestFocus();
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}
	}

	/**
	 * Valida los campos del formulario antes de realizar la petición.
	 * 
	 * @return Boolean
	 */
	public String validarDatosPantalla() {
		/* Realizamos las comprobaciones de los campos del formulario */
		if (tfCodDoc.getText() == null || tfCodDoc.getText().isEmpty()) {
			return I18N.getTexto("El campo de 'Tipo de documento/Código' debe de estar relleno");
		}
		if (tfDesDoc.getText() == null || tfDesDoc.getText().isEmpty()) {
			return I18N.getTexto("El campo de 'Tipo de documento/Descripción' debe de estar relleno");
		}
		if (StringUtils.isBlank(tfOperacion.getText())) {
			return I18N.getTexto("El campo de 'Cod. documento/Localizador' debe de estar relleno");
		}
		return "";
	}

	public class RecuperarTicketServicioDomicilio extends BackgroundTask<Boolean> {

		private String codigo;
		private String codTienda, codCaja;
		private Long idTipoDoc;

		public RecuperarTicketServicioDomicilio(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws TicketsServiceException, RecuperarTicketSadBusquedaException {
			/* Recuperamos el ticket */
			log.debug("RecuperarTicketServicioDomicilio::call() - Iniciando recuperación del documento...");
			return ((DinoTicketManager) ticketManager).recuperarTicketServicioDomicilio(codigo, codTienda, codCaja, idTipoDoc);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			
			Boolean recuperado = getValue();
			DomicilioResponse domicilio = null;

			if (recuperado) {

				boolean isImporteMenorImporteMinimo = isImporteMenorImporteMinimo();
				if(isImporteMenorImporteMinimo) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El valor de la compra no supera el importe mínimo para poder enviar a domicilio."), getStage());
					tfOperacion.clear();
					domicilio = null;
				}
				else {
					/* En caso de tener Fidelizado cargado, probamos a buscar por su número de tarjeta */
					if (ticketManager.getTicketOrigen().getCabecera().getDatosFidelizado() != null) {
						
						if (domicilioService == null) {
							try {
								cargarServiciosSherpa();
							}
							catch (RutasServiciosException e) {
								log.error("RecuperarTicketServicioDomicilio() - " + e.getMessage());
							}
						}
						try {
							String posType = SherpaApiBuilder.getSherpaPosType();
							String sherpaShop = sesion.getAplicacion().getCodAlmacen();
							String sherpaTpv = sesion.getAplicacion().getCodCaja();
							String numTarjetaFidelizado = ticketManager.getTicketOrigen().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado();
							
							log.debug("RecuperarTicketServicioDomicilio:: Consultando en Sherpa los domicilios del fidelizado: " + numTarjetaFidelizado);
							domicilio = domicilioService.getDomicilio(posType, sherpaShop, sherpaTpv, numTarjetaFidelizado);
						}
						catch (Exception e) {
							log.error("RecuperarTicketServicioDomicilio::succeeded() - No se han encontrado datos del Fidelizado que contiene el Ticket en SAD: " + e.getMessage(), e);
							
							if(e instanceof RetryableException) {
								log.debug("RecuperarTicketServicioDomicilio::succeeded() - Sin conexión al servicio");
								domicilio = new DomicilioOfflineDto();
							}
						}
					}
	
					/*
					 * Si hemos recuperado el ticket pero no tenía Fidelizado o no esta en Sherpa/Domicilios, procedemos a
					 * buscar
					 */
					if (domicilio == null) {
						/* Vamos a la pantalla de buscar Fidelizado */
						getApplication().getMainView().showModalCentered(EnvioDomicilioUsuarioView.class, getDatos(), getStage());
	
						if (getDatos().containsKey(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO)) {
							domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO);
						}
						else if (getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)) {
							realizarAlta = true;
							if (getDatos().containsKey(EnvioDomicilioKeys.SHERPA_ADDRESS)) {
								sherpaAdressCode = (String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS);
							}
							domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
						}
						else {
							tfOperacion.clear();
						}
					}
				}
			}

			if (domicilio != null) {
				irPantallaEnvios(domicilio);
			}
		}

		private boolean isImporteMenorImporteMinimo() {
			BigDecimal importeOriginal = ticketManager.getTicketOrigen().getTotales().getTotalAPagar();
			BigDecimal importeMinimo = variablesServices.getVariableAsBigDecimal("X_POS.IMPORTE_MINIMO_SAD");
			boolean isImporteMenorImporteMinimo = BigDecimalUtil.isMenor(importeOriginal, importeMinimo);
			return isImporteMenorImporteMinimo;
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof TicketsServiceException) {
				log.error("RecuperarTicketServicioDomicilio/failed() - " + getException().getMessage());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(getException().getMessage()), getStage());
			}
			else if (getException() instanceof RecuperarTicketSadBusquedaException) {
				log.info("RecuperarTicketServicioDomicilio/failed() - " + getException().getMessage());
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(getException().getMessage()), getStage());
			}
			tfOperacion.requestFocus();
		}
	}

	/**
	 * Realiza la carga de los servicios necesarios en la clase para consultar en Sherpa.
	 * 
	 * @throws RutasServiciosException
	 */
	public void cargarServiciosSherpa() throws RutasServiciosException {
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();
	}

	/**
	 * Nos lleva a la pantalla de envíos a domicilio.
	 */
	public void irPantallaEnvios(DomicilioResponse respuesta) {		
		if(respuesta instanceof DomicilioOfflineDto) {
			abrirAltaUsuarioOffline();
		}
		
		if (realizarAlta) {
			/* Enviamos el objeto de manera diferente en caso de haber realizado el alta */
			getDatos().put(EnvioDomicilioKeys.ALTA_DIRECCION_SAD, respuesta);
			if (StringUtils.isNotBlank(sherpaAdressCode)) {
				getDatos().put(EnvioDomicilioKeys.SHERPA_ADDRESS, sherpaAdressCode);
			}
		}
		else {
			if(!(respuesta instanceof DomicilioOfflineDto)) {
				/* Enviamos el objeto con los datos del usuario */
				getDatos().put(EnvioDomicilioKeys.DOMICILIOS, respuesta);
			}
		}
		/* Enviamos un boolean para indicar si está recuperado el ticket o no */
		getDatos().put(EnvioDomicilioKeys.TICKET_RECUPERADO, true);
		/* Enviamos el ticketManager para poder realizar la lógica de SAD */
		getDatos().put(EnvioDomicilioKeys.TICKET_VENTA, ticketManager);
		getApplication().getMainView().showModal(EnvioDomicilioView.class, getDatos());

		if (!getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
			/* Al volver realizamos la limpieza de los campos */
			limpiarCampos();
		}
	}

	@FXML
	public void accionBuscarTipoDoc() {
		datos = new HashMap<String, Object>();
		try {
			List<String> docs = new ArrayList<String>();
			for (String documentos : sesion.getAplicacion().getDocumentos().getDocumento("NC").getTiposDocumentosOrigen()) {
				docs.add(documentos);
			}
			/* Rescatamos los tipos de documentos posibles */
			datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, docs);

			getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

			if (datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)) {
				TipoDocumentoBean o = (TipoDocumentoBean) datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
				tfCodDoc.setText(o.getCodtipodocumento());
				tfDesDoc.setText(o.getDestipodocumento());
			}
			/* Devolvemos el focus después de realizar la operación de selección de tipo de documento */
			tfOperacion.requestFocus();

		}
		catch (DocumentoException e) {
			String mensajeError = "Error recuperando los posibles documentos origen de la nota de crédito";
			log.error("accionBuscarTipoDoc() - " + mensajeError + " - " + e.getMessage());
			lbMensajeError.setText(I18N.getTexto(mensajeError));
		}
	}

	/**
	 * Permite lanzar la acción buscar al pulsar el "Enter".
	 * 
	 * @param e
	 */
	public void accionAceptarIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			accionBuscar();
		}
	}
	
	private void abrirAltaUsuarioOffline() {
		getDatos().put(AltaUsuarioSADController.PARAM_OFFLINE, true);
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());
	}

}
