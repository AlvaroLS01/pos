package com.comerzzia.dinosol.pos.gui.ventas.contenidosdigitales;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fechas;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.cancelacion.DatosPeticionCancelacionRecargaDto;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosPeticionRecargaDto;
import com.comerzzia.dinosol.pos.gui.ventas.recargas.RecargasAbstractController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.TicketRecarga;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos.ArticulosRecargaService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

@Component
public class ContenidosDigitalesController extends RecargasAbstractController {

	private static final Logger log = Logger.getLogger(ContenidosDigitalesController.class.getName());

	@FXML
	protected TextField tfOperacion;
	
	@FXML
	protected TextField tfCodDoc;
	
	@FXML
	protected TextField tfDesDoc;
	
	@FXML
	protected TextField tfPan;
	
	@FXML
	protected TextField tfConfirmePan;

	@FXML
	private FlowPane fpTicketPosacard, fpCodigoTarjeta, fpPan, fpImportePosa, fpImportePin, fpAyuda, fpDocumento;

	@FXML
	private VBox vbPosa, vbPin;

	@FXML
	protected Label lbNumTicketPosa, lbNumTicketPin, lbFechaActivacion, lbFechaSolPin, lbImportePosa, lbImporteSolPin, lbFechaSolicitud, lbCodTarjeta, lbMensajeError, lbOperadorPosCard,
	        lbServicePinPrinting;

	@FXML
	protected Button btAceptar, btDoc, btCerrarPosa, btCerrarPin, btActivar, btSolicitar;

	@Autowired
	private ArticulosRecargaService articulosRecargaService;

	private boolean isPosa = false;
	private boolean isPin = false;

	public static final String PLANTILLA_ACTIVACION_POSCARD = "activacion_posacard";
	public static final String PLANTILLA_SOLICITUD_PIN = "solicitud_pin";

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		frConsultaTicket.setFormField("codOperacion", tfOperacion);
		frConsultaTicket.setFormField("tipoDoc", tfCodDoc);
	}

	@Override
	public void initializeComponents() {
		addSeleccionarTodoEnFoco(tfOperacion);
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
		tfCodDoc.setUserData(keyboardDataDto);

		addSeleccionarTodoEnFoco(tfOperacion);
		addSeleccionarTodoEnFoco(tfCodDoc);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		compruebaConfiguracion();

		muestraPaso1();

		visor.escribirLineaArriba(I18N.getTexto("--NUEVA ACTIVACIÓN--"));

		btAceptar.setDisable(false);
		lbMensajeError.setText("");
		tfOperacion.setText("");
		tfPan.clear();
		tfConfirmePan.clear();

		try {
			docNotaCredito = sesion.getAplicacion().getDocumentos().getDocumento("NC");
			btAceptar.setDisable(false);
		}
		catch (DocumentoException e) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado el tipo de documento nota de crédito en el entorno."), this.getStage());
			btAceptar.setDisable(true);
		}

		if (docNotaCredito != null) {
			List<String> tiposDoc = docNotaCredito.getTiposDocumentosOrigen();
			if (!tiposDoc.isEmpty()) {
				for (String tipoDoc : tiposDoc) {
					try {
						if (sesion.getAplicacion().getDocumentos().getDocumento(tipoDoc) != null) {
							TipoDocumentoBean docPreseleccion = sesion.getAplicacion().getDocumentos().getDocumento(tipoDoc);
							tfCodDoc.setText(docPreseleccion.getCodtipodocumento());
							tfDesDoc.setText(docPreseleccion.getDestipodocumento());
							break;
						}
					}
					catch (DocumentoException ex) {
						log.error("No se ha encontrado el documento asociado", ex);
					}
				}
			}
			lbMensajeError.setText("");
		}
	}

	protected void muestraPaso1() {
		tfOperacion.setText("");
		btCerrarPin.setVisible(false);
		btCerrarPosa.setVisible(false);
		if (vbContenedor.getChildren().contains(vbPosa))
			vbContenedor.getChildren().remove(vbPosa);
		if (vbContenedor.getChildren().contains(vbPin))
			vbContenedor.getChildren().remove(vbPin);
		if (!vbContenedor.getChildren().contains(vbPaso1))
			vbContenedor.getChildren().add(0, vbPaso1);
		btCerrarPin.setText(I18N.getTexto("CERRAR"));
		btCerrarPosa.setText(I18N.getTexto("CERRAR"));
		tfOperacion.requestFocus();
		isPosa = false;
		isPin = false;
	}

	protected void muestraFormularioPosaCard() throws Exception {
		tfPan.clear();
		tfConfirmePan.clear();
		btCerrarPosa.setVisible(true);
		btCerrarPosa.setText(I18N.getTexto("CERRAR"));
		lbNumTicketPosa.setText(ticketManager.getTicketOrigen().getCabecera().getCodTicket());
		lbFechaActivacion.setText(ticketManager.getTicketOrigen().getCabecera().getFechaAsLocale());
		lbFechaActivacion.getStyleClass().remove("lbError");
		if (!Fechas.equalsDate(ticketManager.getTicketOrigen().getCabecera().getFecha(), new Date())) {
			lbFechaActivacion.setText(lbFechaActivacion.getText() + " (Anterior a fecha actual)");
			lbFechaActivacion.getStyleClass().add("lbError");

		}

		lbOperadorPosCard.setText(operadorActual.getDescripcion());

		LineaTicket linea = null;
		for (String codart : articulosRecargaService.getConfiguracion().getArticulosPosaCard()) {
			if (ticketManager.getTicketOrigen().getLinea(codart).size() > 0) {
				linea = (LineaTicket) ticketManager.getTicketOrigen().getLinea(codart).get(0);
			}
		}
		String importeTotal = linea.getImporteTotalConDtoAsString();
		lbImportePosa.setText(importeTotal);

		vbContenedor.getChildren().add(0, vbPosa);

		isPosa = true;
		isPin = false;
	}

	protected void muestraFormularioPinPrinting() throws Exception {
		btCerrarPin.setVisible(true);
		btCerrarPin.setText(I18N.getTexto("CERRAR"));
		lbNumTicketPin.setText(ticketManager.getTicketOrigen().getCabecera().getCodTicket());
		lbFechaSolPin.setText(ticketManager.getTicketOrigen().getCabecera().getFechaAsLocale());
		lbFechaSolPin.getStyleClass().remove("lbError");
		if (!Fechas.equalsDate(ticketManager.getTicketOrigen().getCabecera().getFecha(), new Date())) {
			lbFechaSolPin.setText(lbFechaSolPin.getText() + " (Anterior a fecha actual)");
			lbFechaSolPin.getStyleClass().add("lbError");

		}

		lbServicePinPrinting.setText(operadorActual.getDescripcion());

		LineaTicket linea = null;
		for (String codart : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()) {
			if (!ticketManager.getTicketOrigen().getLinea(codart).isEmpty()) {
				linea = (LineaTicket) ticketManager.getTicketOrigen().getLinea(codart).get(0);
			}
		}
		String importe = linea.getImporteTotalConDtoAsString();
		lbImporteSolPin.setText(importe);
		vbContenedor.getChildren().add(0, vbPin);

		isPosa = false;
		isPin = true;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	protected void muestraPaso2() {
		vbContenedor.getChildren().remove(vbPaso1);
		if (vbContenedor.getChildren().contains(vbPosa))
			vbContenedor.getChildren().remove(vbPosa);
		if (vbContenedor.getChildren().contains(vbPin))
			vbContenedor.getChildren().remove(vbPin);

		try {
			for (LineaTicket lineaTicket : (List<LineaTicket>) ticketManager.getTicketOrigen().getLineas()) {
				for (String codart : articulosRecargaService.getConfiguracion().getArticulosPosaCard()) {
					if (ticketManager.getTicketOrigen().getLinea(codart).size() > 0) {
						muestraFormularioPosaCard();
						break;
					}
				}

				for (String codart : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()) {
					if (ticketManager.getTicketOrigen().getLinea(codart).size() > 0) {
						muestraFormularioPinPrinting();
						break;
					}
				}
			}
		}
		catch (Exception e) {
			log.error("muestraPaso2() - Ha habido un error al mostrar la pantalla de introducción de datos: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al mostrar la pantalla de introducción de datos: " + e.getMessage()), e);
		}
	}

	@Override
	public void initializeFocus() {
		tfOperacion.requestFocus();
	}

	@FXML
	public void accionBuscar() {
		lbMensajeError.setText("");
		if (validarFormularioConsultaCliente()) {

			ticketManager = SpringContext.getBean(TicketManager.class);
			String codTienda = sesion.getAplicacion().getTienda().getCodAlmacen();
			String codCaja = sesion.getAplicacion().getCodCaja();
			String codigo = tfOperacion.getText();
			String codDoc = tfCodDoc.getText();

			try {
				Long idTipoDocumento = sesion.getAplicacion().getDocumentos().getDocumento(codDoc).getIdTipoDocumento();
				new RecuperarTicketContenidoDigital(codigo, codTienda, codCaja, idTipoDocumento).start();
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean isCodTarjetaValido(String text) {
		return (text.length() >= 13 && text.length() <= 30);
	}

	public class RecuperarTicketContenidoDigital extends BackgroundTask<Boolean> {

		private String codigo;
		private String codTienda, codCaja;
		private Long idTipoDoc;

		public RecuperarTicketContenidoDigital(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws Exception {
			return ((DinoTicketManager) ticketManager).recuperarTicketContenidoDigital(codigo, codTienda, codCaja, idTipoDoc);
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			recuperarTicketContenidoDigitalSucceeded(res);
			super.succeeded();
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else if (getException() instanceof java.lang.NullPointerException) {
				String msg = "No se ha encontrado el ticket con código: " + codigo;
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(msg), getException());
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}
			tfCodDoc.requestFocus();
			tfOperacion.requestFocus();
		}
	}

	@SuppressWarnings("unchecked")
	protected void recuperarTicketContenidoDigitalSucceeded(boolean encontrado) {
		if (encontrado) {
			try {
				boolean yaActivado = comprobarRecargaUnica(ticketManager.getTicketOrigen().getUidTicket());

				if (yaActivado) {
					String msg = I18N.getTexto("Este ticket ya ha sido utilizado anteriormente. No puede utilizarse de nuevo.");
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(msg), getStage());
					tfCodDoc.requestFocus();
					tfOperacion.requestFocus();
				}
				else {
					BigDecimal importeRecarga = null;
					String codartRecarga = null;
					for (String codart : articulosRecargaService.getConfiguracion().getArticulosPosaCard()) {
						List<LineaTicket> lineas = ticketManager.getTicketOrigen().getLinea(codart);
						if (lineas.size() == 1) {
							codartRecarga = codart;
							importeRecarga = lineas.get(0).getImporteTotalConDto();
							break;
						}
					}
					for (String codart : articulosRecargaService.getConfiguracion().getArticulosPinPrinting()) {
						List<LineaTicket> lineas = ticketManager.getTicketOrigen().getLinea(codart);
						if (lineas.size() == 1) {
							codartRecarga = codart;
							importeRecarga = lineas.get(0).getImporteTotalConDto();
							break;
						}
					}

					operadorActual = recargasService.getOperador(codartRecarga, importeRecarga, true);
					if (operadorActual == null) {
						String mensaje = I18N.getTexto("No es posible realizar la recarga debido a que ningún operador permite este importe en la recarga. Devuelva la venta y pruebe otro importe");
						VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
						muestraPaso1();
						return;
					}

					muestraPaso2();
				}
			}
			catch (Exception e) {
				String msg = I18N.getTexto("Ha habido un error al recuperar el ticket de la venta de contenidos digitales.");
				VentanaDialogoComponent.crearVentanaError(getStage(), msg, e);
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}
		else {
			String msg = I18N.getTexto("No se ha encontrado ningún ticket de venta de contenidos digitales con esos datos.");
			VentanaDialogoComponent.crearVentanaError(msg, getStage());
			tfCodDoc.requestFocus();
			tfOperacion.requestFocus();
		}
	}

	@FXML
	public void accionCancelar() {
		getApplication().getMainView().close();
		tfOperacion.requestFocus();
	}

	protected boolean validarFormularioConsultaCliente() {
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultaTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		frConsultaTicket.setCodCaja(sesion.getAplicacion().getCodCaja());
		frConsultaTicket.setCodOperacion(tfOperacion.getText());
		frConsultaTicket.setCodTienda(sesion.getAplicacion().getCodAlmacen());
		frConsultaTicket.setCodDoc(tfCodDoc.getText());

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
		try {
			datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, new ArrayList<String>(sesion.getAplicacion().getDocumentos().getDocumento("NC").getTiposDocumentosOrigen()));

			getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

			if (datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)) {
				TipoDocumentoBean o = (TipoDocumentoBean) datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
				tfCodDoc.setText(o.getCodtipodocumento());
				tfDesDoc.setText(o.getDestipodocumento());
			}
		}
		catch (DocumentoException e) {
			log.error("accionBuscarTipoDoc() - Error recuperando los posibles documentos origen de la nota de crédito: " + e.getMessage(), e);
		}
	}

	protected void procesarTipoDoc() {

		String codDoc = tfCodDoc.getText();

		if (!codDoc.trim().isEmpty()) {
			try {
				TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(codDoc);
				if (docNotaCredito != null) {
					if (!docNotaCredito.getTiposDocumentosOrigen().contains(documento.getCodtipodocumento())) {
						log.warn("procesarTipoDoc() - Se seleccionó un tipo de documento no válido para la devolución.");
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), getStage());
						tfCodDoc.setText("");
						tfDesDoc.setText("");
					}
					else {
						tfCodDoc.setText(documento.getCodtipodocumento());
						tfDesDoc.setText(documento.getDestipodocumento());
					}
				}
				else {
					tfCodDoc.setText("");
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El documento seleccionado no es válido."), this.getStage());
				}
			}
			catch (DocumentoException ex) {
				log.error("procesarTipoDoc() - Error obteniendo el tipo de documento", ex);
				lbMensajeError.setText(I18N.getTexto("El tipo de documento indicado no existe en la base de datos."));
				tfCodDoc.setText("");
				tfDesDoc.setText("");
			}
			catch (NumberFormatException nfe) {
				log.error("procesarTipoDoc() - El id de documento introducido no es válido.", nfe);
				lbMensajeError.setText(I18N.getTexto("El id introducido no es válido."));
				tfCodDoc.setText("");
				tfDesDoc.setText("");
			}
		}
		else {
			tfDesDoc.setText("");
			tfCodDoc.setText("");
		}
	}

	public void accionCerrar() {
		if (vbContenedor.getChildren().contains(vbPaso1))
			getStage().close();
		else
			muestraPaso1();
	}

	@Override
	public boolean canClose() {
		visor.escribirLineaArriba(I18N.getTexto("---ACTIVACIÓN FINALIZADA---"));
		visor.modoEspera();
		return super.canClose();
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

	@Override
	public boolean comprobarFormulario() {
		boolean result = false;
		lbMensajeError.setText("");

		if (!tfPan.getText().equals(tfConfirmePan.getText())) {
			String msg = I18N.getTexto(I18N.getTexto("Los códigos PAN introducidos no coinciden."));
			VentanaDialogoComponent.crearVentanaError(msg, getStage());
		}
		else {
			result = true;
		}
		return result;
	}

	@Override
	public DatosPeticionRecargaDto generarPeticionRecarga() {
		DatosPeticionRecargaDto datosPeticion = new DatosPeticionRecargaDto();
		datosPeticion.setImporte(operadorActual.getImporte());
		datosPeticion.setNumReferenciaProveedor(operadorActual.getEan());
		datosPeticion.setCodTicket(ticketManager.getTicketOrigen().getCabecera().getCodTicket());
		datosPeticion.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
		if (isPosa) {
			datosPeticion.setTelefono(tfPan.getText());
		}
		datosPeticion.setUsuario(sesion.getAplicacion().getCodAlmacen() + "-" + sesion.getAplicacion().getCodCaja());
		return datosPeticion;
	}

	@Override
	public DatosPeticionCancelacionRecargaDto generarPeticionCancelacionRecarga() {
		DatosPeticionCancelacionRecargaDto datosPeticion = new DatosPeticionCancelacionRecargaDto();
		datosPeticion.setImporte(operadorActual.getImporte());
		datosPeticion.setNumReferenciaProveedor(operadorActual.getEan());
		return datosPeticion;
	}

	@Override
	protected void imprimirDocumentoRecarga(TicketRecarga ticketRecarga) {
		if(isPin) {
			imprimirDocumentoPinPrinting(ticketRecarga);
		}
		else if (isPosa) {
			imprimirDocumentoActivacion(ticketRecarga);
		}
		else {
			log.error("imprimirDocumentoRecarga() - No se ha podido imprimir el ticket de recarga.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido imprimir el ticket de recarga."), getStage());
		}
	}

	private void imprimirDocumentoActivacion(TicketRecarga ticketRecarga) {
		log.trace("imprimirDocumentoActivacion()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("documento", ticketRecarga);
			contextoTicket.put("esCopia", false);

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(PLANTILLA_ACTIVACION_POSCARD, contextoTicket);

			contextoTicket.put("esCopia", true);
			// Llamamos al servicio de impresión para la copia
			ServicioImpresion.imprimir(PLANTILLA_ACTIVACION_POSCARD, contextoTicket);

		}
		catch (DeviceException e) {
			log.error("imprimirMovimiento() - Error en la impresión del movimiento: " + e.getMessage(), e);
		}
	}

	private void imprimirDocumentoPinPrinting(TicketRecarga ticketSolicitudPIN) {
		log.trace("imprimirDocumentoActivacion()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("documento", ticketSolicitudPIN);
			contextoTicket.put("esCopia", false);

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(PLANTILLA_SOLICITUD_PIN, contextoTicket);

			contextoTicket.put("esCopia", true);
			// Llamamos al servicio de impresión para la copia
			ServicioImpresion.imprimir(PLANTILLA_SOLICITUD_PIN, contextoTicket);

		}
		catch (DeviceException e) {
			log.error("imprimirDocumentoPinPrinting() - Error en la impresión del movimiento: " + e.getMessage(), e);
		}
	}

	@Override
	public void salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		if(isPin) {
			recargasService.salvarDocumentoRecargaPinPrinting(ticketRecarga, uidTicketOrigen);
		}
		else {
			recargasService.salvarDocumentoRecargaPOSA(ticketRecarga, uidTicketOrigen);
		}
	}

	@Override
	protected String getTipoRecarga() {
		if(isPin) {
			return TicketRecarga.TIPO_RECARGA_PIN_PRINTING;
		}
		else {
			return TicketRecarga.TIPO_RECARGA_POSA;
		}
	}

}
