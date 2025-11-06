package com.comerzzia.dinosol.pos.gui.ventas.recargas;

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
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.TicketRecarga;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos.ArticulosRecargaService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
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
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
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
public class RecargasController extends RecargasAbstractController {

	private static final Logger log = Logger.getLogger(RecargasController.class.getName());

	public static final String PLANTILLA_RECARGA_MOVILES = "recarga_movil";

	@FXML
	protected TextField tfOperacion;
	
	@FXML
	protected TextField tfCodDoc;
	
	@FXML
	protected TextField tfDesDoc;

	@FXML
	protected TextFieldImporte tfMovil1;
	
	@FXML
	protected TextFieldImporte tfMovilConfirmar;

	@FXML
	private FlowPane fpMovil1;
	
	@FXML
	private FlowPane fpMovil2;
	
	@FXML
	private FlowPane fpImporteRecarga;
	
	@FXML
	private FlowPane fpAyuda;
	
	@FXML
	private FlowPane fpDocumento;

	@FXML
	private VBox vbPaso2;

	@FXML
	protected Label lbNumTicket;
	
	@FXML
	protected Label lbFechaRecarga;
	
	@FXML
	protected Label lbImporteRecarga;
	
	@FXML
	protected Label lbMensajeError;
	
	@FXML
	protected Label lbOperador;

	@FXML
	protected Button btAceptar;
	
	@FXML
	protected Button btDoc;
	
	@FXML
	protected Button btCerrar;

	@FXML
	protected TecladoNumerico tecladoNumerico;

	@Autowired
	private ArticulosRecargaService articulosRecargaService;
	
	private BigDecimal importeRecarga;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		frConsultaTicket.setFormField("codOperacion", tfOperacion);
		frConsultaTicket.setFormField("tipoDoc", tfCodDoc);
	}

	@Override
	public void initializeComponents() {
		importeRecarga = BigDecimal.ZERO;
		addSeleccionarTodoEnFoco(tfOperacion);
		addSeleccionarTodoEnFoco(tfCodDoc);
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
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		compruebaConfiguracion();

		muestraPaso1();

		visor.escribirLineaArriba(I18N.getTexto("--NUEVA RECARGA--"));

		btAceptar.setDisable(false);
		lbMensajeError.setText("");
		tfOperacion.setText("");
		tfMovil1.clear();
		tfMovilConfirmar.clear();

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
		btCerrar.setVisible(false);
		if (vbContenedor.getChildren().contains(vbPaso2))
			vbContenedor.getChildren().remove(vbPaso2);
		if (!vbContenedor.getChildren().contains(vbPaso1))
			vbContenedor.getChildren().add(0, vbPaso1);
		btCerrar.setText(I18N.getTexto("CERRAR"));
		tfOperacion.requestFocus();

	}

	protected void muestraPaso2() {
		tfMovil1.clear();
		tfMovilConfirmar.clear();
		btCerrar.setVisible(true);
		vbContenedor.getChildren().remove(vbPaso1);
		vbContenedor.getChildren().add(0, vbPaso2);
		btCerrar.setText(I18N.getTexto("CERRAR"));
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
				new RecuperarTicketRecarga(codigo, codTienda, codCaja, idTipoDocumento).start();
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}
	}

	public boolean comprobarFormulario() {
		boolean result = false;
		lbMensajeError.setText("");
		if (isMovilValido(tfMovil1.getText()) && isMovilValido(tfMovilConfirmar.getText()) && tfMovil1.getText().equals(tfMovilConfirmar.getText())) {
			result = true;
		}
		else if (!isMovilValido(tfMovil1.getText())) {
			String msg = I18N.getTexto("El número de móvil no es correcto.");
			VentanaDialogoComponent.crearVentanaError(msg, getStage());
		}
		else if (!tfMovil1.getText().equals(tfMovilConfirmar.getText())) {
			String msg = I18N.getTexto("Los números de teléfono introducidos no son iguales. Por favor, vuelta a introducirlos.");
			VentanaDialogoComponent.crearVentanaError(msg, getStage());
		}
		else {
			String msg = I18N.getTexto("Se debe de seleccionar un operador.");
			VentanaDialogoComponent.crearVentanaError(msg, getStage());
		}
		return result;
	}

	private boolean isMovilValido(String text) {
		return text.matches("\\d{9}") || text.matches("\\+\\d{11}");
	}

	public class RecuperarTicketRecarga extends BackgroundTask<Boolean> {

		private String codigo;
		private String codTienda;
		private String codCaja;
		private Long idTipoDoc;

		public RecuperarTicketRecarga(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idTipoDoc = idTipoDoc;
		}

		@SuppressWarnings("unchecked")
        @Override
		protected Boolean call() throws Exception {
			Boolean res = ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc, false);			
			
			int cantidad = 0;
			for(String codart : articulosRecargaService.getConfiguracion().getArticulos()) {
				for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
					if(linea.getCodArticulo().equals(codart) && BigDecimalUtil.isMayorACero(linea.getCantidad())) {
						cantidad++;
					}
				}
			}
			
			if(cantidad > 1) {
				res = false;
			}
			

			return res;
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
			else if (getException() instanceof java.lang.NullPointerException) {
				String msg = "No se ha encontrado el ticket con código: " + codigo;
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto(msg), getException());
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			recuperarTicketRecargaSucceeded(res);
			super.succeeded();
		}

	}

	@SuppressWarnings("unchecked")
	protected void recuperarTicketRecargaSucceeded(boolean encontrado) {
		if (encontrado) {
			try {
				boolean yaRecargado = comprobarRecargaUnica(ticketManager.getTicketOrigen().getUidTicket());

				if (yaRecargado) {
					String msg = I18N.getTexto("Este ticket ya ha sido utilizado para una recarga anteriormente, no puede utilizarse de nuevo.");
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto(msg), getStage());
					tfCodDoc.requestFocus();
					tfOperacion.requestFocus();
				}
				else {
					String codartRecarga = null;
					for (String codart : articulosRecargaService.getConfiguracion().getArticulosRecargaMovil()) {
						List<LineaTicket> lineas = ticketManager.getTicketOrigen().getLinea(codart);
						if (lineas.size() == 1) {
							codartRecarga = codart;
							importeRecarga = lineas.get(0).getImporteTotalConDto();
							break;
						}
					}

					operadorActual = recargasService.getOperador(codartRecarga, importeRecarga, true);
					if (operadorActual == null) {
						VentanaDialogoComponent.crearVentanaError(
						        "No es posible realizar la recarga debido a que ningún operador permite este importe en la recarga. Devuelva la venta y pruebe otro importe", getStage());
						muestraPaso1();
						return;
					}
					muestraPaso2();

					lbNumTicket.setText(ticketManager.getTicketOrigen().getCabecera().getCodTicket());
					lbFechaRecarga.setText(ticketManager.getTicketOrigen().getCabecera().getFechaAsLocale());
					lbFechaRecarga.getStyleClass().remove("lbError");
					if (!Fechas.equalsDate(ticketManager.getTicketOrigen().getCabecera().getFecha(), new Date())) {
						lbFechaRecarga.setText(lbFechaRecarga.getText() + " (Anterior a fecha actual)");
						lbFechaRecarga.getStyleClass().add("lbError");
					}
					lbImporteRecarga.setText(FormatUtil.getInstance().formateaImporte(importeRecarga));

					lbOperador.setText(operadorActual.getDescripcionr());
				}
			}
			catch (Exception e) {
				String msg = I18N.getTexto("Ha habido un error al recuperar el ticket de la recarga.");
				VentanaDialogoComponent.crearVentanaError(getStage(), msg, e);
				tfCodDoc.requestFocus();
				tfOperacion.requestFocus();
			}
		}
		else {
			String msg = I18N.getTexto("No se ha encontrado ningún ticket de recarga con esos datos.");
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
		if (!constraintViolations.isEmpty()) {
			ConstraintViolation<FormularioConsultaTicketBean> next = constraintViolations.iterator().next();
			frConsultaTicket.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(next.getMessage()), getStage());
			tfCodDoc.requestFocus();
			tfOperacion.requestFocus();
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
				String msg = I18N.getTexto("El tipo de documento indicado no existe en la base de datos.");
				VentanaDialogoComponent.crearVentanaError(getStage(), msg, ex);
				tfCodDoc.setText("");
				tfDesDoc.setText("");
			}
			catch (NumberFormatException nfe) {
				log.error("procesarTipoDoc() - El id de documento introducido no es válido.", nfe);
				String msg = I18N.getTexto("El id introducido no es válido.");
				VentanaDialogoComponent.crearVentanaError(getStage(), msg, nfe);
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
		visor.escribirLineaArriba(I18N.getTexto("---RECARGA FINALIZADA---"));
		visor.modoEspera();
		return super.canClose();
	}

	@Override
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
	public DatosPeticionRecargaDto generarPeticionRecarga() {
		DatosPeticionRecargaDto datosPeticion = new DatosPeticionRecargaDto();
		datosPeticion.setImporte(importeRecarga);
		datosPeticion.setTelefono(tfMovil1.getText());
		datosPeticion.setNumReferenciaProveedor(operadorActual.getEan());
		datosPeticion.setCodTicket(ticketManager.getTicketOrigen().getCabecera().getCodTicket());
		datosPeticion.setUsuario(sesion.getAplicacion().getCodAlmacen() + "-" + sesion.getAplicacion().getCodCaja());
		return datosPeticion;
	}

	@Override
	public DatosPeticionCancelacionRecargaDto generarPeticionCancelacionRecarga() {
		DatosPeticionCancelacionRecargaDto datosPeticion = new DatosPeticionCancelacionRecargaDto();
		datosPeticion.setImporte(importeRecarga);
		datosPeticion.setTelefono(tfMovil1.getText());
		datosPeticion.setNumReferenciaProveedor(operadorActual.getEan());
		return datosPeticion;
	}

	@Override
	protected void imprimirDocumentoRecarga(TicketRecarga ticketRecarga) {
		log.trace("imprimirDocumentoRecarga()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("documento", ticketRecarga);
			contextoTicket.put("esCopia", false);

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(PLANTILLA_RECARGA_MOVILES, contextoTicket);

			contextoTicket.put("esCopia", true);
			// Llamamos al servicio de impresión para la copia
			ServicioImpresion.imprimir(PLANTILLA_RECARGA_MOVILES, contextoTicket);
		}
		catch (DeviceException e) {
			log.error("imprimirDocumentoRecarga() - Error en la impresión del movimiento: " + e.getMessage(), e);
		}
	}

	@Override
	public void salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		recargasService.salvarDocumentoRecargaMovil(ticketRecarga, uidTicketOrigen);
	}

	@Override
	protected String getTipoRecarga() {
		return TicketRecarga.TIPO_RECARGA_MOVIL;
	}

}
