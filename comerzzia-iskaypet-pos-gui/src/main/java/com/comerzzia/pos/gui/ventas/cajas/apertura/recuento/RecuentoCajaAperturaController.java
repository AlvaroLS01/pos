package com.comerzzia.pos.gui.ventas.cajas.apertura.recuento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.importe.BotonBotoneraImagenValorComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.FormularioRecuentoCajaBean;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.RecuentoCajaGui;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class RecuentoCajaAperturaController extends WindowController implements Initializable, IContenedorBotonera {

	private static final Logger log = Logger.getLogger(RecuentoCajaAperturaController.class.getName());

	protected Map<String, RecuentoCajaGui> recuentoPagosEfectivo;
	protected ObservableList<RecuentoCajaGui> recuentos;

	protected MedioPagoBean medioPagoSeleccionado;

	protected BotoneraComponent botoneraMediosPago;

	protected BotoneraComponent botoneraImportes;

	protected BotoneraComponent botoneraAccionesTabla;
	protected BigDecimal saldo;

	@FXML
	protected TextFieldImporte tfImporte, tfCantidad;
	@FXML
	protected Label lbTotal, lbMedioPago;
	@FXML
	protected Button btAceptar, btCancelar, btAnotar;
	@FXML
	protected TecladoNumerico tecladoNumerico;

	@FXML
	protected TableView<RecuentoCajaGui> tbRecuento;
	@FXML
	protected TableColumn<RecuentoCajaGui, Integer> tcRecuentoCantidad;
	@FXML
	protected TableColumn<RecuentoCajaGui, String> tcRecuentoMoneda;
	@FXML
	protected TableColumn<RecuentoCajaGui, String> tcRecuentoTotal;
	@FXML
	protected AnchorPane panelMediosPago;
	@FXML
	protected TabPane panelMedioPago;
	@FXML
	protected Tab panelPestanaPagoEfectivo;

	@FXML
	protected AnchorPane panelPagoEfectivo, panelMenuTabla, panelNumberPad;

	protected FormularioRecuentoCajaBean frRecuentoCaja;

	@Autowired
	private Sesion sesion;
	@Autowired
	private MediosPagosService mediosPagosService;

	/**
	 * Inicializa el componente tras su creación. No hay acceso al application desde este método.
	 *
	 * @param url
	 * @param rb
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.trace("initialize()");

		recuentoPagosEfectivo = new HashMap<>();

		frRecuentoCaja = SpringContext.getBean(FormularioRecuentoCajaBean.class);
		frRecuentoCaja.setFormField("importe", tfImporte);

		recuentos = FXCollections.observableList(new ArrayList<RecuentoCajaGui>());
		tbRecuento.setItems(recuentos);

		// Escondemos el teclado númerico
		panelNumberPad.setVisible(true);

		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbRecuento.setPlaceholder(new Label(""));

		tcRecuentoMoneda.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbRecuento", "tcRecuentoMoneda", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoTotal.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbRecuento", "tcRecuentoTotal", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcRecuentoCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbRecuento", "tcRecuentoCantidad", null, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		// Inicializamos la tabla para que escuche a un posible cambio de sus
		// valores
		tcRecuentoCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RecuentoCajaGui, Integer>, ObservableValue<Integer>>(){

			@Override
			public ObservableValue<Integer> call(TableColumn.CellDataFeatures<RecuentoCajaGui, Integer> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcRecuentoMoneda.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RecuentoCajaGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<RecuentoCajaGui, String> cdf) {
				return cdf.getValue().getMonedaProperty();
			}
		});
		tcRecuentoTotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RecuentoCajaGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<RecuentoCajaGui, String> cdf) {
				return cdf.getValue().getTotalProperty();
			}
		});

		log.trace("initialize() - Comprobando configuración para panel numérico");
		ocultarPanelNumerico();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		try {
			saldo = new BigDecimal("0.00");
			refrescarDatosPantalla();

			Dispositivos.abrirCajon();

			recuentos.clear();

			panelMedioPago.focusedProperty().addListener(new ChangeListener<Boolean>(){

				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
					if (t.booleanValue() == false && t1.booleanValue() == true) {
						medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
						lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
						Platform.runLater(new Runnable(){

							@Override
							public void run() {
								tfImporte.requestFocus();
							}
						});
					}
				}
			});

			// Establecemos el medio de pago por defecto
			medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
			lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
		}

		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. ", e);
			throw new InitializeGuiException(e);
		}
	}

	@Override
	public void initializeFocus() {
		tfImporte.requestFocus();
	}

	@Override
	public void initializeComponents() {
		try {
			initTecladoNumerico(tecladoNumerico);
			log.debug("inicializarComponentes() - Cargando medios de pago");
			saldo = BigDecimal.ZERO;
			lbTotal.setText(saldo.toString());

			log.debug("inicializarComponentes() - Configurando botonera");
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<>();
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelMenuTabla.getChildren().add(botoneraAccionesTabla);
			
			try {
				log.debug("inicializarComponentes() - Cargando panel de importes");
				PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
				botoneraImportes = new BotoneraComponent(panelBotoneraBean, null, panelPagoEfectivo.getPrefHeight(), this, BotonBotoneraImagenValorComponent.class);
				panelPagoEfectivo.getChildren().add(botoneraImportes);
			}
			catch (InitializeGuiException e) {
				log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
			}

			tfCantidad.focusedProperty().addListener(new ChangeListener<Boolean>(){

				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (oldValue) {
						BigDecimal cantidadNueva = FormatUtil.getInstance().desformateaBigDecimal(tfCantidad.getText(), 0);

						if (cantidadNueva == null || BigDecimalUtil.isIgualACero(cantidadNueva)) {
							cantidadNueva = BigDecimal.ONE;
						}

						tfCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidadNueva));
					}
				}
			});
			
			registrarAccionCerrarVentanaEscape();

			addSeleccionarTodoCampos();

			crearEventoEliminarTabla(tbRecuento);
		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error creando botonera para medio de pago. error : " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla pagos."), getStage());
		}

	}

	/**
	 * Función que refresca los totales en pantalla
	 */
	public void refrescarDatosPantalla() {
		tfCantidad.setText("1");
		tfImporte.clear();

		tbRecuento.getSelectionModel().selectLast();
		int indSeleccionado = tbRecuento.getSelectionModel().getSelectedIndex();
		tbRecuento.scrollTo(indSeleccionado);
		lbTotal.setText(FormatUtil.getInstance().formateaImporte(saldo));

	}

	/**
	 * Accion anotar una nueva linea de recuento a la caja
	 */
	public void accionBtAnotarLineaRecuento() {
		log.trace("accionBtAnotarLineaRecuento() - Anotamos línea de recuento...");

		frRecuentoCaja.setImporte(tfImporte.getText());

		if (validarDatosRecuento()) {
			if (tfCantidad.getText().isEmpty()) {
				tfCantidad.setText("1");
			}

			BigDecimal importe = FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
			importe = importe.setScale(2, RoundingMode.HALF_UP);
			accionAnotarRecuento(importe, tfCantidad.getText());
		}

	}

	public void accionAnotarRecuento(BigDecimal importe, String cantidad) {
		log.debug("accionAnotarRecuento() - Medio de pago: " + medioPagoSeleccionado + " // Cantidad: " + cantidad + " // Importe: " + importe);

		if (medioPagoSeleccionado == null) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay ninguna forma de pago seleccionada."), getStage());
			return;
		}
		CajaLineaRecuentoBean lineaRecuento = null;
		Integer cant = new Integer(cantidad);
		for (RecuentoCajaGui recuento : recuentos) {
			if (recuento.getLineaRecuento().getValor().equals(importe)) {
				lineaRecuento = recuento.getLineaRecuento();
				lineaRecuento.setCantidad(recuento.getLineaRecuento().getCantidad() + cant);
				recuentos.remove(recuento);
				break;
			}
		}
		if (lineaRecuento == null) {
			lineaRecuento = new CajaLineaRecuentoBean();
			lineaRecuento.setCantidad(cant);
		}
		lineaRecuento.setValor(importe);
		lineaRecuento.setCodMedioPago(MediosPagosService.medioPagoDefecto.getCodMedioPago());

		recuentos.add(new RecuentoCajaGui(lineaRecuento));
		saldo = saldo.add(importe.multiply(new BigDecimal(cantidad)));

		refrescarDatosPantalla();
		tfImporte.requestFocus();

	}

	/**
	 * Acción aceptar
	 */
	@FXML
	public void aceptar() {
		getDatos().put("saldo", saldo);
		getStage().close();

	}

	/**
	 * Acción cancelar
	 */
	@Override
	public void accionCancelar() {

		getStage().close();

	}

	/**
	 * Acción evento de teclado sobre campo importe
	 *
	 * @param event
	 */
	public void actionTfImporte(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			accionBtAnotarLineaRecuento();
		}
		if (event.getCode() == KeyCode.MULTIPLY) {
			cambiarCantidad();
		}
	}

	public void actionTfCantidad(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			tfImporte.selectAll();
			tfImporte.requestFocus();
		}
	}

	/**
	 * Preparamos la interfaz para una modificación de la cantidad
	 */
	public void cambiarCantidad() {
		log.debug("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		tfImporte.clear();
		tfCantidad.requestFocus();
		tfCantidad.selectAll();
	}

	/**
	 * Oculta el panel numérico
	 */
	protected void ocultarPanelNumerico() {
		if (!Variables.MODO_PAD_NUMERICO) {
			log.debug("ocultarPanelNumerico() - PAD Numerico off");
			panelNumberPad.setVisible(false);
		}
	}

	/**
	 * Método de control de acciones de página de recuento
	 *
	 * @param botonAccionado
	 *            botón pulsado
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
			case "ACCION_SELECIONAR_MEDIO_PAGO":
				log.debug("Acción cambiar medio de pago en pantalla");
				BotonBotoneraTextoComponent boton = (BotonBotoneraTextoComponent) botonAccionado;
				medioPagoSeleccionado = boton.getMedioPago();
				lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
				tfImporte.requestFocus();
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				log.debug("Acción seleccionar registro anterior de la tabla");
				accionIrAnteriorRegistroTabla();
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				log.debug("Acción seleccionar siguiente registro de la tabla");
				accionIrSiguienteRegistroTabla();
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				log.debug("Acción seleccionar último registro de la tabla");
				accionEventoEliminarTabla("");
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}
	}

	public void seleccionarMedioPago(HashMap<String, String> parametros) {
		if (parametros.containsKey("codMedioPago")) {
			String codMedioPago = parametros.get("codMedioPago");
			MedioPagoBean medioPago = mediosPagosService.getMedioPago(codMedioPago);
			if (medioPago != null) {
				medioPagoSeleccionado = medioPago;
				lbMedioPago.setText(medioPago.getDesMedioPago());
				tfImporte.requestFocus();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el medio de pago"), getStage());
				log.error("No se ha encontrado el medio de pago con código: " + codMedioPago);
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha especificado una acción correcta para este botón"), getStage());
			log.error("No existe el código del medio de pago para este botón.");
		}
	}

	/**
	 * Acción mover a anterior registro de la tabla
	 */
	protected void accionIrAnteriorRegistroTabla() {
		log.debug("accionIrAnteriorRegistroTabla() - Acción ejecutada");
		if (tbRecuento.getItems() != null && tbRecuento.getItems() != null) {
			int indice = tbRecuento.getSelectionModel().getSelectedIndex();
			if (indice > 0) {
				tbRecuento.getSelectionModel().select(indice - 1);
				tbRecuento.scrollTo(indice - 1); // Mueve el scroll para que se
				                                 // vea el registro
			}
		}
	}

	/**
	 * Acción mover a siguiente registro de la tabla
	 */
	protected void accionIrSiguienteRegistroTabla() {
		log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
		if (tbRecuento.getItems() != null && tbRecuento.getItems() != null) {
			int indice = tbRecuento.getSelectionModel().getSelectedIndex();
			if (indice < tbRecuento.getItems().size()) {
				tbRecuento.getSelectionModel().select(indice + 1);
				tbRecuento.scrollTo(indice + 1); // Mueve el scroll para que se
				                                 // vea el registro
			}
		}
	}

	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@Override
	public void accionEventoEliminarTabla(String idItem) {
		log.debug("accionBorrarRegistroTabla() - Acción ejecutada");
		if (tbRecuento.getItems() != null && tbRecuento.getItems() != null && tbRecuento.getItems().size() > 0) {
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Confirme operación."), I18N.getTexto("¿Desea eliminar el registro?"), getStage())) {
				int indSiguienteSeleccion = tbRecuento.getSelectionModel().getSelectedIndex();
				RecuentoCajaGui linea = tbRecuento.getSelectionModel().getSelectedItem();
				if (linea == null) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Deberá seleccionar una línea antes de continuar."), getStage());
					return;
				}

				recuentos.remove(linea);
				saldo = saldo.subtract(linea.getLineaRecuento().getTotal());

				refrescarDatosPantalla();

				tbRecuento.getSelectionModel().selectLast();// .select(0);
				int indUltimo = tbRecuento.getSelectionModel().getSelectedIndex();
				if (indSiguienteSeleccion > indUltimo) {
					indSiguienteSeleccion = indUltimo;
				}

				tbRecuento.requestFocus();
				tbRecuento.getSelectionModel().select(indSiguienteSeleccion);
				tbRecuento.getFocusModel().focus(indSiguienteSeleccion);
				tbRecuento.scrollTo(indSiguienteSeleccion);
			}
			else {
				log.debug("Se canceló la operación");
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Borrar pago"), I18N.getTexto("Sin pagos para eliminar. Prueba de un mensaje de información bastante largo."), getStage());
		}
	}

	// </editor-fold>

	/**
	 * Es llamado desde BotoneraComponent si hay botones de tipo PAGO
	 */
	public void seleccionarImporte(BigDecimal importe) {
		log.debug("seleccionarImporte() - " + importe.toPlainString());
		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
		accionAnotarRecuento(importe, tfCantidad.getText());
	}

	/**
	 * Valida los campos editables
	 *
	 * @return
	 */
	protected boolean validarDatosRecuento() {
		log.debug("validarFormularioDatosFiltro()");

		boolean valido;

		Set<ConstraintViolation<FormularioRecuentoCajaBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frRecuentoCaja);
		valido = !(constraintViolations.size() >= 1);
		return valido;
	}

	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSeleccionarTodoCampos() {
		addSeleccionarTodoEnFoco(tfCantidad);
		addSeleccionarTodoEnFoco(tfImporte);
	}

	/**
	 * Método auxuliar para añadir a un campo de texto la capacidad de seleccionar todo su texto cuando adquiere el foco
	 * 
	 * @param campo
	 */
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
}
