/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.devoluciones;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.login.LoginView;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuariosView;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaController;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaView;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.DevolucionArticulosView;
import com.comerzzia.pos.gui.ventas.devoluciones.articulos.LineaTicketAbonoGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionticketsController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController.LineaInsertadaNoPermitidaException;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FormularioLineaArticuloBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieView;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosView;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionCambioException;
import com.comerzzia.pos.services.ticket.lineas.LineaDevolucionNuevoArticuloException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class IntroduccionArticulosController extends Controller implements IContenedorBotonera {

	// <editor-fold desc="Declaración de variables">
	private static final Logger log = Logger.getLogger(IntroduccionArticulosController.class.getName());
	public static final String PERMISO_BORRAR_LINEA = "BORRAR LINEA";
	public static final String PERMISO_MODIFICAR_LINEA = "MODIFICAR LINEA";
	public static final String PERMISO_CANCELAR_VENTA = "CANCELAR VENTA";
	public static final String PERMISO_DEVOLUCIONES = "DEVOLUCIONES";
	public static final String PERMISO_CAMBIAR_ARTICULOS = "PERMITIR CAMBIO ARTICULOS";
	public static final String PERMISO_INTRODUCIR_ARTICULOS_NUEVOS = "INTRODUCIR ARTICULOS NUEVOS";

	public static final String TICKET_KEY = "TICKET_KEY";
	protected TicketManager ticketManager;

	final IVisor visor = Dispositivos.getInstance().getVisor();
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;

	@Autowired
	private Sesion sesion;
	@Autowired
	protected Documentos documentos;

	@FXML
	protected TextField tfCodigoIntro;
	@FXML
	protected TextField tfCantidadIntro;
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected AnchorPane panelMenuTabla; // Botonera de tabla
	@FXML
	protected AnchorPane panelNumberPad;
	@FXML
	protected Label lbTotalCantidad;
	@FXML
	protected Label lbCodCliente, lbDesCliente, lbTotal;
	@FXML
	protected TableView<LineaTicketGui> tbLineas;
	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasArticulo;

	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasDescripcion;

	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasDesglose1;

	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasDesglose2;

	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasCantidad;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasPVP;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasDto;

	// Formulario de validación de codigo de barras
	protected FormularioLineaArticuloBean frValidacion, frValidacionBusqueda;

	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasImporte;

	@FXML
	protected TecladoNumerico tecladoNumerico;

	protected ObservableList<LineaTicketGui> lineas;

	// botonera inferior de pantalla
	protected BotoneraComponent botonera;
	// botonera de acciones de tabla
	protected BotoneraComponent botoneraAccionesTabla;

	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private GiftCardService giftCardService;
	
	protected TipoDocumentoBean tipoDocumentoInicial;

	@SuppressWarnings("unchecked")
    @Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");

		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbLineas.setPlaceholder(new Label(""));

		lineas = FXCollections.observableList(new ArrayList<LineaTicketGui>());

		// CENTRADO CON ESTILO A LA DERECHA
		tcLineasArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasArticulo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDesglose1", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDesglose2", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasPVP.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasPVP", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasDto.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasDto", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasImporte", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasCantidad", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		// Asignamos las lineas a la tabla
		tbLineas.setItems(lineas);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcLineasArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getArtProperty();
			}
		});
		tcLineasDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcLineasCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcLineasDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcLineasDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcLineasPVP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getPvpProperty();
			}
		});
		tcLineasDto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getDescuentoProperty();
			}
		});
		tcLineasImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteTotalFinalProperty();
			}
		});

		// Ocultamos el Pad numérico si es necesario
		log.debug("initialize() - Comprobando configuración para panel numérico");
		if (!Variables.MODO_PAD_NUMERICO) {
			log.debug("initialize() - PAD Numerico off");
			panelNumberPad.setVisible(false);			
			panelNumberPad.getChildren().clear();
		}
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

		try {
			ticketManager = SpringContext.getBean(TicketManager.class);
			ticketManager.init();
			initTecladoNumerico(tecladoNumerico);

			log.debug("inicializarComponentes() - Inicialización de componentes...");

			log.debug("inicializarComponentes() - Carga de acciones de botonera inferior");

			PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
			botonera = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
			panelBotonera.getChildren().add(botonera);

			// Botonera de Tabla
			log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de ventas");
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
			botoneraAccionesTabla = new BotoneraComponent(1, 6, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelMenuTabla.getChildren().add(botoneraAccionesTabla);

			log.debug("inicializarComponentes() - registrando acciones de la tabla principal");
			crearEventoEnterTabla(tbLineas);
			crearEventoNegarTabla(tbLineas);
			crearEventoEliminarTabla(tbLineas);
			crearEventoNavegacionTabla(tbLineas);

			log.debug("inicializarComponentes() - Configuración de la tabla");
			if (sesion.getAplicacion().isDesglose1Activo()) { // Si hay desglose 1, establecemos el texto
				tcLineasDesglose1.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE1_TITULO)));
			}
			else { // si no hay desgloses, compactamos la línea
				tcLineasDesglose1.setVisible(false);
			}
			if (sesion.getAplicacion().isDesglose2Activo()) { // Si hay desglose 1, establecemos el texto
				tcLineasDesglose2.setText(I18N.getTexto(variablesServices.getVariableAsString(VariablesServices.ARTICULO_DESGLOSE2_TITULO)));
			}
			else { // si no hay desgloses, compactamos la línea
				tcLineasDesglose2.setVisible(false);
			}

			frValidacionBusqueda = SpringContext.getBean(FormularioLineaArticuloBean.class);
			frValidacionBusqueda.setFormField("cantidad", tfCantidadIntro);
			// Inicializamos los formularios

			frValidacion = new FormularioLineaArticuloBean();
			frValidacion.setFormField("codArticulo", tfCodigoIntro);
			frValidacion.setFormField("cantidad", tfCantidadIntro);

			registraEventoTeclado(new EventHandler<KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.MULTIPLY) {
						cambiarCantidad();
					}
				}
			}, KeyEvent.KEY_RELEASED);

			addSeleccionarTodoCampos();

		}
		catch (CargarPantallaException | SesionInitException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de venta de artículos");
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando formulario...");
		try {
			tfCodigoIntro.setText("");
			tbLineas.getSelectionModel().clearSelection();

			boolean cajaAbierta = sesion.getSesionCaja().isCajaAbierta();
			comprobarAperturaPantalla();

			if (cajaAbierta) {
				HashMap<String, Object> datos = getView().getParentView().getController().getDatos();
				if (datos.containsKey(TICKET_KEY)) {
					ticketManager = (TicketManager) datos.get(TICKET_KEY);
					if (!ticketManager.isTicketAbierto()) {
						try {
							ticketManager.nuevoTicket();
							ticketManager.setEsDevolucion(true);
							ticketManager.setDocumentoActivo(documentos.getDocumentoAbono(ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento()));
						}
						catch (PromocionesServiceException | DocumentoException ex) {
							log.error("initializeForm() - Error inicializando ticket", ex);
							VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
						}
					}
					else {
						if (!sesion.getSesionCaja().getCajaAbierta().getUidDiarioCaja().equals(ticketManager.getTicket().getCabecera().getUidDiarioCaja())) {
							try {
								ticketManager.nuevoTicket();
								ticketManager.setEsDevolucion(true);
								ticketManager.setDocumentoActivo(documentos.getDocumentoAbono(ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento()));
							}
							catch (PromocionesServiceException | DocumentoException ex) {
								log.error("initializeForm() - Error inicializando ticket", ex);
								VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
							}
						}
						ticketManager.setEsDevolucion(true);
					}
					
					if(tipoDocumentoInicial == null) {
						tipoDocumentoInicial = documentos.getDocumento(((TicketVentaAbono) ticketManager.getTicket()).getCabecera().getTipoDocumento());
						 
					}
				}
				if (ticketManager == null) {
					log.error("initializeForm() -----No se ha inicializado el ticket manager-----");
					throw new InitializeGuiException();
				}

				// Comprobamos si la operación es una devolución de tarjeta regalo
				if (ticketManager.esDevolucionRecargaTarjetaRegalo()) {
					GiftCardBean tarjeta = giftCardService.getGiftCard(ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo());
					// Comprobamos si la tarjeta en cuestión está dada de baja
					if (tarjeta != null) {
						if (tarjeta.isBaja()) {
							VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("La tarjeta introducida está dada de baja."), getStage());
						}
					}
					else {
						VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El número de tarjeta no es válido."), getStage());
					}
				}

				if (ticketManager.getTicket() != null && ticketManager.getTicket().getLineas() != null && !ticketManager.getTicket().getLineas().isEmpty()) {
					escribirUltimaLineaEnVisor();
				}
				refrescarDatosPantalla();
			}
		}
		catch (CajaEstadoException | CajasServiceException e) {
			log.error("initializeForm() - Error de caja : " + e.getMessageI18N());
			throw new InitializeGuiException(e.getMessageI18N(), e);
		}
		catch (RestException | RestHttpException e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(I18N.getTexto("Ha ocurrido un error al conectar con el servidor para consultar la tarjeta regalo"), e);
		}
		catch (InitializeGuiException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(e);
		}
	}

	/**
	 * Realiza las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
	 * 
	 * @throws CajasServiceException
	 * @throws CajaEstadoException
	 * @throws InitializeGuiException
	 */
	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
		if (!sesion.getSesionCaja().isCajaAbierta()) {
			Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true);
			if (aperturaAutomatica) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
				sesion.getSesionCaja().abrirCajaAutomatica();
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
				throw new InitializeGuiException(false);
			}
		}

		if (!ticketManager.comprobarCierreCajaDiarioObligatorio()) {
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual),
			        getStage());
			throw new InitializeGuiException(false);
		}
	}

	protected void escribirUltimaLineaEnVisor() {
		int ultimArticulo = ticketManager.getTicket().getLineas().size();
		if (ultimArticulo > 0) {
			LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
			String desc = linea.getArticulo().getDesArticulo();
			visor.escribir(desc, linea.getCantidadAsString() + " X " + FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
		}
	}

	@Override
	public void initializeFocus() {
		tfCodigoIntro.requestFocus();
	}

	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		try {
			super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", false);
		}
	}

	/**
	 * Acción de introducción de código desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfCodigoIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.MULTIPLY) {
			cambiarCantidad();
		}
		if (event.getCode() == KeyCode.ENTER) {
			nuevoCodigoArticulo();
		}
	}

	/**
	 * Acción de introducción de cantidad desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfCantidadIntro(KeyEvent event) {
		log.debug("actionTfCantidadIntro() - acción de introducción de cantidad de artículo");
		if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.MULTIPLY) {
			nuevaCantidad();
			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(tfCodigoIntro.getText().trim());
			if (accionValidarFormulario()) {
				BigDecimal bigDecimal = FormatUtil.getInstance().desformateaBigDecimal(tfCantidadIntro.getText().trim());
				bigDecimal = bigDecimal.abs();
				tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(bigDecimal, 3));
			}
			else {
				tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
				cambiarCantidad();
			}
		}
	}

	/**
	 * Preparamos la interfaz para un cambio de código tras cambiar una cantidad
	 */
	public void nuevaCantidad() {
		log.debug("nuevaCantidad() - preparamos la interfaz para un cambio de código tras cambiar una cantidad");
		tfCantidadIntro.setText(tfCantidadIntro.getText().replace("*", ""));
		if (tfCantidadIntro.getText().isEmpty()) {
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		}
		tfCodigoIntro.requestFocus();
		tfCodigoIntro.selectAll();
	}

	/**
	 * Preparamos la interfaz para una modificación de la cantidad
	 */
	public void cambiarCantidad() {
		log.debug("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		tfCodigoIntro.setText(tfCodigoIntro.getText().replace("*", ""));
		tfCantidadIntro.requestFocus();
		tfCantidadIntro.selectAll();
	}

	/**
	 * Añade un nuevo artículo
	 */
	public void nuevoCodigoArticulo() {
		log.debug("nuevoCodigoArticulo() - Creando línea de artículo");

		// Validamos los datos
		if (!tfCodigoIntro.getText().trim().isEmpty()) {
			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(tfCodigoIntro.getText().trim().toUpperCase());
			tfCodigoIntro.clear();
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

			if (accionValidarFormulario()) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				new NuevoCodigoArticuloTask(frValidacion.getCodArticulo(), frValidacion.getCantidadAsBigDecimal()).start();
			}
		}
	}

	protected boolean accionValidarFormulario() {
		// Limpiamos los errores que pudiese tener el formulario
		frValidacion.clearErrorStyle();

		// Validamos el formulario
		Set<ConstraintViolation<FormularioLineaArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frValidacion);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioLineaArticuloBean> next = constraintViolations.iterator().next();
			frValidacion.setErrorStyle(next.getPropertyPath(), true);
			frValidacion.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		}

		BigDecimal cantidad = frValidacion.getCantidadAsBigDecimal();
		if (cantidad == null) {
			return false;
		}

		BigDecimal max = new BigDecimal(10000000);
		if (BigDecimalUtil.isMayorOrIgual(frValidacion.getCantidadAsBigDecimal(), max)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La cantidad debe ser menor que {0}", FormatUtil.getInstance().formateaNumero(max)), getStage());
			return false;
		}

		return true;
	}

	protected LineaTicket nuevaLineaArticulo(String codart, String desglose1, String desglose2, BigDecimal cantidad) throws LineaTicketException {
		return ticketManager.nuevaLineaArticulo(codart, desglose1, desglose2, cantidad, null);
	}

	protected class NuevoCodigoArticuloTask extends BackgroundTask<LineaTicket> {

		private String codArticulo;
		private final BigDecimal cantidad;
		private LineaTicket linea;

		public NuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad) {
			this.codArticulo = codArticulo;
			this.cantidad = cantidad;
		}

		@Override
		protected LineaTicket call() throws Exception {
			linea = nuevaLineaArticulo(codArticulo, null, null, cantidad);
			linea.setAdmitePromociones(false);
			visor.escribir(linea.getDesArticulo(), FormatUtil.getInstance().formateaNumero(cantidad) + " X " + FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
			return linea;
		}

		@Override
		protected void succeeded() {
			try {
				refrescarDatosPantalla();
				super.succeeded();

				boolean esTarjetaRegalo = ticketManager.comprobarTarjetaRegaloLineaYaInsertada(getValue());

				if (esTarjetaRegalo) {
					if (!consultarGiftCard()) {
						try {
							ticketManager.eliminarTicketCompleto();

							refrescarDatosPantalla();
						}
						catch (TicketsServiceException | PromocionesServiceException | DocumentoException e) {
							log.error("succeeded() - Excepción capturada" + e.getMessage(), e);
						}
					}
				}

				asignarNumerosSerie(linea);

				tbLineas.getSelectionModel().select(0);
			}
			catch (LineaInsertadaNoPermitidaException e) {
				log.error("Error en el proceso de tarjeta regalo", e);
				VentanaDialogoComponent.crearVentanaError(IntroduccionArticulosController.this.getStage(), "Error en el proceso de tarjeta regalo.", e);
			}
		}

		@Override
		protected void failed() {
			super.failed();

			if (getCMZException() instanceof LineaDevolucionCambioException && tienePermisosCambioArticulo()) {
				introducirArticuloCambio(codArticulo, null, null, cantidad);
			}
			else if (getCMZException() instanceof LineaDevolucionNuevoArticuloException && tienePermisosIntroducirNuevoArticulo()) {
				introducirNuevoArticulo(codArticulo, null, null, cantidad);
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getCMZException().getMessageI18N(), getStage());
			}
		}
	}

	protected void introducirNuevoArticulo(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad) {
		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se va a introducir un artículo que no estaba en la venta original. ¿Está seguro?"), getStage())) {
			introducirLineaPositiva(codArticulo, desglose1, desglose2, cantidad, false);
		}
	}

	protected void introducirArticuloCambio(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad) {
		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Este artículo ya está completamente devuelto en la venta original. Se introducirá en positivo. ¿Está seguro?"), getStage())) {
			introducirLineaPositiva(codArticulo, desglose1, desglose2, cantidad, true);
		}
	}

	protected void introducirLineaPositiva(String codArticulo, String desglose1, String desglose2, BigDecimal cantidad, boolean esCambio) {
		try {
			cambiarTipoDocumento();
			ticketManager.nuevaLineaArticulo(codArticulo, desglose1, desglose2, cantidad, getStage(), null, true);
			ticketManager.recalcularConPromociones();
			refrescarDatosPantalla();
		}
		catch (Exception e) {
			log.error("introducirArticuloCambio() - Ha habido un error al introducir la línea: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al introducir la línea"), e);
		}
	}

	protected boolean tienePermisosCambioArticulo() {
		boolean tienePermisosCambio = false;
		try {
			if(!tipoDocumentoInicial.isSignoPositivo()){
				compruebaPermisos(PERMISO_CAMBIAR_ARTICULOS);
				tienePermisosCambio = true;
			}else {
				log.debug("No es posible añadir una linea de venta a una devolución con el signo forzado a positivo.");
			}
		}
		catch (SinPermisosException e) {
		}
		return tienePermisosCambio;
	}

	protected boolean tienePermisosIntroducirNuevoArticulo() {
		boolean tienePermisosCambio = false;
		try {
			if(!tipoDocumentoInicial.isSignoPositivo()){	
				compruebaPermisos(PERMISO_INTRODUCIR_ARTICULOS_NUEVOS);
				tienePermisosCambio = true;
			}else {
				log.debug("No es posible añadir una linea de venta a una devolución con el signo forzado a positivo.");
			}
		}
		catch (SinPermisosException e) {
		}
		return tienePermisosCambio;
	}

	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); // "Home"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); // "Page Up"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); // "Page Down"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); // "End"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); // "Delete"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
		// BOTONERA TABLA TICKET DEVOLUCIÓN
			case "ACCION_TABLA_PRIMER_REGISTRO":
				accionTablaPrimerRegistro();
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaAnteriorRegistro();
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaSiguienteRegistro();
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro();
				break;
			case "ACCION_TABLA_BORRAR_REGISTRO":
				accionTablaEliminarRegistro();
				break;
			case "ACCION_TABLA_EDITAR_REGISTRO":
				accionTablaEditarRegistro();
				break;
			case "CANCELAR_VENTA":
				accionCancelarDevolucion();
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}
	}

	public void accionCancelarDevolucion() {

		try {
			super.compruebaPermisos(PERMISO_CANCELAR_VENTA);
			if (confirmaAnularDevolucion()) {
		        
		        if(ticketManager.getTicket().getIdTicket() != null) {
		        	ticketManager.salvarTicketVacio();
		        }
				
				ticketManager.finalizarTicket();
				try {
					eliminarEventosTeclado();
					getView().getParentView().loadAndInitialize();
				}
				catch (InitializeGuiException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e);
				}
			}
		}
		catch (SinPermisosException e) {
			log.debug("accionCancelarDevolucion() - El usuario no tiene permisos para cancelar la devolución.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para cancelar la devolución."), getStage());
		}
	}

	protected boolean confirmaAnularDevolucion() {
		return VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se anulará la devolución, ¿seguro que desea continuar?"), this.getStage());

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
    public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");
		checkDocumentType();

		tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		tfCodigoIntro.setText("");

		lbCodCliente.setText(((TicketVenta) ticketManager.getTicket()).getCliente().getCodCliente());
		lbDesCliente.setText(((TicketVenta) ticketManager.getTicket()).getCliente().getDesCliente());
		lbTotal.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getTotal()));
		applyTotalLabelStyle(lbTotal);
		
		LineaTicketGui selectedItem = getLineaSeleccionada();
		lineas.clear();
		for (LineaTicketAbstract lineaTicket : (List<LineaTicketAbstract>) (ticketManager.getTicket()).getLineas()) {
			lineas.add(new LineaTicketAbonoGui(lineaTicket));
		}
		Collections.reverse(lineas);
		if (selectedItem != null) {
			tbLineas.getSelectionModel().select(lineas.indexOf(searchIdLinea(selectedItem.getIdLinea())));
		}
		else {
			tbLineas.getSelectionModel().selectFirst();
		}
		if (getLineaSeleccionada() == null) {
			tfCodigoIntro.requestFocus();
		}
		tbLineas.scrollTo(0);

		actualizarVisibilidadBtnEditar();
		obtenerCantidadTotal();
	}
	
	protected void applyTotalLabelStyle(Label label) {
		try {
			String text = label.getText();
			
			label.getStyleClass().clear();
			label.getStyleClass().add("label");
			label.getStyleClass().add("total");
			
			if(text.length()>=15) {
				label.getStyleClass().add("total-reduced-27");
			} else if(text.length()>=13) {
				label.getStyleClass().add("total-reduced-30");
			} else if(text.length()>=11) {
				label.getStyleClass().add("total-reduced-35");
			} else if(text.length()>=8) {
				label.getStyleClass().add("total-reduced-40");
			} else {
				label.getStyleClass().add("total-reduced");
			}
		} catch (Exception e) {
			log.debug("applyTotalLabelStyle() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

	protected void checkDocumentType() {
		try {
			boolean newLine = false;
			for(LineaTicket linea:(List<LineaTicket>)ticketManager.getTicket().getLineas()) {
				if(linea.getLineaDocumentoOrigen()==null) {
					newLine = true;
					break;
				}
			}
			if(!newLine) {
				TipoDocumentoBean document = documentos.getDocumentoAbono(ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento());
				if(!ticketManager.getDocumentoActivo().getIdTipoDocumento().equals(document.getIdTipoDocumento())) {					
					ticketManager.setDocumentoActivo(document);
					ticketManager.getTicket().getCabecera().setUidTicketEnlace(ticketManager.getTicketOrigen().getUidTicket());
					ticketManager.recalcularConPromociones();
				}
			}
			
		} catch (DocumentoException e) {
			log.error("checkDocumentType() - Ha habido un error al cambiar el tipo de documento del ticket: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al cambiar el tipo de documento del ticket. Contacte con el administrador."), e);
		}
		
	}

	protected void actualizarVisibilidadBtnEditar() {
		if (!tienePermisosCambioArticulo() && !tienePermisosIntroducirNuevoArticulo()) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", true);
		}
		else {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", false);
		}
	}

	protected LineaTicketGui getLineaSeleccionada() {
		LineaTicketGui linea = tbLineas.getSelectionModel().getSelectedItem();
		if (linea == null) {
			tbLineas.getSelectionModel().selectFirst();
			linea = tbLineas.getSelectionModel().getSelectedItem();
		}
		return linea;
	}

	protected void seleccionarSiguienteLinea() {
		int index = tbLineas.getSelectionModel().getSelectedIndex();
		if (index == -1) {
			getLineaSeleccionada();
			return;
		}
		if (index + 1 >= tbLineas.getItems().size()) {
			tbLineas.getSelectionModel().select(index - 1);
		}
		else {
			tbLineas.getSelectionModel().select(index + 1);
		}
	}

	protected LineaTicketGui searchIdLinea(Integer idLinea) {
		for (LineaTicketGui linea : lineas) {
			if (linea.getIdLinea().equals(idLinea)) {
				return linea;
			}
		}
		return null;
	}

	/**
	 * Acción Evento Eliminar registro
	 *
	 * @param idTabla
	 */
	@Override
	public void accionEventoEliminarTabla(String idTabla) {
		log.debug("accionEventoEliminarTabla()- id:" + idTabla);
		accionTablaEliminarRegistro();
	}

	/**
	 * Acción mover a primer registro de la tabla
	 */
	@FXML
	protected void accionTablaPrimerRegistro() {
		log.debug("accionTablaPrimerRegistro() - Acción ejecutada");
		if (tbLineas.getItems() != null && !tbLineas.getItems().isEmpty()) {
			tbLineas.getSelectionModel().select(0);
			tbLineas.scrollTo(0); // Mueve el scroll para que se vea el registro
		}
	}

	/**
	 * Acción mover a anterior registro de la tabla
	 */
	@FXML
	protected void accionTablaAnteriorRegistro() {
		log.debug("accionTablaAnteriorRegistro() - Acción ejecutada");
		if (tbLineas.getItems() != null && !tbLineas.getItems().isEmpty()) {
			int indice = tbLineas.getSelectionModel().getSelectedIndex();
			if (indice > 0) {
				tbLineas.getSelectionModel().select(indice - 1);
				tbLineas.scrollTo(indice - 1); // Mueve el scroll para que se vea el registro
			}
		}
	}

	/**
	 * Acción mover a siguiente registro de la tabla
	 */
	@FXML
	protected void accionTablaSiguienteRegistro() {
		log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
		if (tbLineas.getItems() != null && !tbLineas.getItems().isEmpty()) {
			int indice = tbLineas.getSelectionModel().getSelectedIndex();
			if (indice < tbLineas.getItems().size()) {
				tbLineas.getSelectionModel().select(indice + 1);
				tbLineas.scrollTo(indice + 1); // Mueve el scroll para que se vea el registro
			}
		}
	}

	/**
	 * Acción mover a último registro de la tabla
	 */
	@FXML
	protected void accionTablaUltimoRegistro() {
		log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
		if (tbLineas.getItems() != null && !tbLineas.getItems().isEmpty()) {
			tbLineas.getSelectionModel().select(tbLineas.getItems().size() - 1);
			tbLineas.scrollTo(tbLineas.getItems().size() - 1); // Mueve el scroll para que se vea el registro
		}
	}

	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@FXML
	protected void accionTablaEliminarRegistro() {
		log.debug("accionTablaEliminarRegistro() - ");
		try {
			LineaTicketGui selectedItem = getLineaSeleccionada();
			if (!tbLineas.getItems().isEmpty() && selectedItem != null) {
				super.compruebaPermisos(PERMISO_BORRAR_LINEA);
				boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
				if (!confirmar) {
					return;
				}
				ticketManager.eliminarLineaArticulo(selectedItem.getIdLinea());
				seleccionarSiguienteLinea();
				refrescarDatosPantalla();
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea."), getStage());
		}
	}

	/**
	 * Acción editar registro de la tabla
	 */
	@FXML
	protected void accionTablaEditarRegistro() {
		try {
			log.debug("accionTablaEditarRegistro() - Acción ejecutada");
			super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
			if (tbLineas.getItems() != null && getLineaSeleccionada() != null) {
				int linea = getLineaSeleccionada().getIdLinea();
				if (linea > 0) {
					ILineaTicket lineaTicket = ticketManager.getTicket().getLinea(linea);
					if (BigDecimalUtil.isMayorACero(lineaTicket.getCantidad())) {
						// Creamos la ventana de edición de artículos
						HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
						parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaTicket);
						parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_APLICAR_PROMOCIONES, false);
						parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
						getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, this.getStage());

						((LineaTicket) lineaTicket).setCantidad(lineaTicket.getCantidad().abs());
						((LineaTicket) lineaTicket).recalcularImporteFinal();
						ticketManager.recalcularConPromociones();

						refrescarDatosPantalla();
					}
					else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede editar una línea con cantidad negativa."), getStage());
					}
				}
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEditarRegistro() - El usuario no tiene permisos para modificar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para modificar una línea."), getStage());
		}
	}

	public void abrirBusquedaArticulos() {
		log.debug("abrirBusquedaArticulos()");

		// Validamos el campo cantidad antes de iniciar la búsqueda. Si el campo es vacío lo seteamos a 1 sin devolver
		// error
		if (tfCantidadIntro.getText().trim().equals("")) {
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		}
		// Validamos que hay introducida una cantidad válida de artículos . Nota : También valida el campo código
		// introducido. Podemos crear otro metodo de validación para que no lo haga
		frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
		if (!accionValidarFormularioBusqueda()) {
			return; // Si la validación de la cantidad no es satisfactoria, no realizamos la búsqueda
		}

		datos = new HashMap<>();
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE, ticketManager.getTicket().getCliente());
		getDatos().put(BuscarArticulosController.PARAMETRO_ENTRADA_CODTARIFA, ticketManager.getTarifaDefault());
		getDatos().put(BuscarArticulosController.PARAM_MODAL, Boolean.TRUE);
		getDatos().put(SeleccionUsuarioController.PARAMETRO_ES_STOCK, Boolean.FALSE);
		getApplication().getMainView().showModal(BuscarArticulosView.class, datos);
		initializeFocus();

		if (datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)) {
			log.debug("realizarAccion() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
			String codArt = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_CODART);
			String desglose1 = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1);
			String desglose2 = (String) datos.get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2);

			frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
			try {
				if (accionValidarFormularioBusqueda()) {
					LineaTicket lineaTicket = nuevaLineaArticulo(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
					ticketManager.getTicket().getTotales().recalcular();
					refrescarDatosPantalla();
					visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));

					asignarNumerosSerie(lineaTicket);

					visor.escribir(lineaTicket.getDesArticulo(), lineaTicket.getCantidadAsString() + " X " + FormatUtil.getInstance().formateaImporte(lineaTicket.getPrecioTotalConDto()));
				}
			}
			catch (LineaTicketException ex) {
				if (ex instanceof LineaDevolucionCambioException && tienePermisosCambioArticulo()) {
					introducirArticuloCambio(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
				}
				else if (ex instanceof LineaDevolucionNuevoArticuloException && tienePermisosIntroducirNuevoArticulo()) {
					introducirNuevoArticulo(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());
				}
				else {
					log.error("realizarAccion() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
					VentanaDialogoComponent.crearVentanaError(ex.getLocalizedMessage(), this.getScene().getWindow());
				}

			}
		}
	}

	protected boolean accionValidarFormularioBusqueda() {
		// Limpiamos los errores que pudiese tener el formulario
		frValidacionBusqueda.clearErrorStyle();

		// Validamos el formulario de validación de búsqueda
		Set<ConstraintViolation<FormularioLineaArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frValidacionBusqueda);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioLineaArticuloBean> next = constraintViolations.iterator().next();
			frValidacionBusqueda.setErrorStyle(next.getPropertyPath(), true);
			frValidacionBusqueda.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		}
		return true;
	}

	public void cambiarCajero() {
		HashMap<String, Object> parametrosCambiarCajero = new HashMap<>();

		if (AppConfig.loginBotonera) {
			parametrosCambiarCajero.put(SeleccionUsuarioController.PARAMETRO_MODO_PANTALLA_CAJERO, "S");
			getApplication().getMainView().showModal(SeleccionUsuariosView.class, parametrosCambiarCajero);
		}
		else {
			parametrosCambiarCajero.put(LoginController.PARAMETRO_ENTRADA_ES_MODO_SELECCION_CAJERO, "S");
			getApplication().getMainView().showModal(LoginView.class, parametrosCambiarCajero);
		}
		initializeFocus();
	}

	public void abrirBusquedaClientes() {
		if (ticketManager.getTicket().getLineas().size() > 0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede cambiar el cliente de un ticket con líneas."), getStage());
			return;
		}
		// Accion ir a la pantalla de consulta de clientes
		HashMap<String, Object> parametrosBusquedaCliente = new HashMap<>();
		parametrosBusquedaCliente.put(ConsultaClienteController.MODO_MODAL, true);
		getApplication().getMainView().showModal(ConsultaClienteView.class, parametrosBusquedaCliente);
		ClienteBean cliente = (ClienteBean) parametrosBusquedaCliente.get(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE);
		if (cliente != null) {
			ticketManager.getTicket().setCliente(cliente);
			lbCodCliente.setText(ticketManager.getTicket().getCliente().getCodCliente());
			lbDesCliente.setText(ticketManager.getTicket().getCliente().getDesCliente());
		}
		initializeFocus();
	}

	public void abrirPagos() {
		log.trace("abrirPagos()");
		if (!ticketManager.isTicketVacio()) { // Si el ticket no es vacío se puede aparcar
			if (validarTicket()) {
				log.debug("abrirPagos() - El ticket tiene líneas");
				getDatos().put(TICKET_KEY, ticketManager);
				getDatos().put(PagosController.TIPO_DOC_INICIAL, tipoDocumentoInicial);
				
				getApplication().getMainView().showModal(PagosView.class, getDatos());
				try {
					getView().resetSubViews();
					if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
						initializeForm();
					}
					else {
						try {
							ticketManager.nuevoTicket();
							ticketManager.setEsDevolucion(true);
							ticketManager.setDocumentoActivo(documentos.getDocumentoAbono(ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento()));
						}
						catch (Exception e) {
							log.error("abrirPagos() - Ha habido un error al inicializar un nuevo ticket para borrar la copia de seguridad: " + e.getMessage(), e);
						}
						getView().getParentView().loadAndInitialize();
					}
				}
				catch (InitializeGuiException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e);
				}
			}
		}
		else {
			log.warn("abrirPagos() - Ticket vacio");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), this.getStage());
		}
	}

	protected void cambiarTipoDocumento() {
		try {
			String tipoDocumentoOrigen = ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento();
			ticketManager.setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(tipoDocumentoOrigen));
			ticketManager.getTicket().getCabecera().setUidTicketEnlace(null);
		}
		catch (Exception e) {
			log.error("cambiarTipoDocumento() - Ha habido un error al cambiar el tipo de documento del ticket: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al cambiar el tipo de documento del ticket. Contacte con el administrador."), e);
		}
	}

	public void verDocumentoOrigen() {
		getView().getController().getDatos().put(TICKET_KEY, ticketManager);
		getApplication().getMainView().showModal(DevolucionArticulosView.class, getView().getController().getDatos());
		refrescarDatosPantalla();
		visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
		escribirUltimaLineaEnVisor();
	}

	public void abrirGestionTickets(HashMap<String, String> parametros) {

		if (parametros.containsKey("idAccion")) {
			String idAccion = parametros.get("idAccion");
			if (getDatos() == null) {
				this.datos = new HashMap<String, Object>();
			}

			getDatos().put(GestionticketsController.PARAMETRO_ENTRADA_TIPO_DOC, ticketManager.getDocumentoActivo().getCodtipodocumento());
			POSApplication.getInstance().getMainView().showActionView(Long.parseLong(idAccion), getDatos());
		}
		else {
			log.error("No llegó la acción asociada al botón.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo ejecutar la acción seleccionada."), this.getStage());
		}
	}

	public boolean consultarGiftCard() {
		log.info("consultarGiftCard()");

		boolean operacionCorrecta, operacionCancelada = true;

		HashMap<String, Object> parametros = new HashMap<>();

		do {
			operacionCorrecta = true;
			parametros.clear();
			parametros.put(CodigoTarjetaController.PARAMETRO_IN_TEXTOCABECERA, I18N.getTexto("Lea o escriba el código de barras de la tarjeta Giftcard"));
			parametros.put(CodigoTarjetaController.PARAMETRO_TIPO_TARJETA, "GIFTCARD");
			getApplication().getMainView().showModalCentered(CodigoTarjetaView.class, parametros, this.getStage());

			if (parametros.containsKey(CodigoTarjetaController.PARAMETRO_NUM_TARJETA)) {
				String numTarjeta = (String) parametros.get(CodigoTarjetaController.PARAMETRO_NUM_TARJETA);
				operacionCorrecta = comprobarGiftCard(numTarjeta);
			}
			else {
				operacionCancelada = false;
			}
		}
		while (!operacionCorrecta);

		return operacionCorrecta && operacionCancelada;
	}

	protected void obtenerCantidadTotal() {
		TicketVentaAbono ticket = (TicketVentaAbono) ticketManager.getTicket();
		BigDecimal cantidad = ticket.getCantidadTotal();
		lbTotalCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidad.abs()));
	}

	protected boolean comprobarGiftCard(String numTarjeta) {
		log.info("comprobarGiftCard()");

		boolean operacionCorrecta = true;
		try {
			GiftCardBean tarjeta = giftCardService.getGiftCard(numTarjeta);

			// Si encuentra la tarjeta.
			if (tarjeta != null) {
				if (numTarjeta.equals(ticketManager.getTicketOrigen().getCabecera().getTarjetaRegalo().getNumTarjetaRegalo())) {
					ticketManager.getTicket().getCabecera().agnadirTarjetaRegalo(tarjeta);
					ticketManager.getTicket().getCabecera().getTarjetaRegalo().setImporteRecarga(ticketManager.getTicket().getTotales().getTotalAPagar());
					ticketManager.setEsDevolucionTarjetaRegalo(true);
					operacionCorrecta = true;
				}
				else {
					log.error("comprobarGiftCard() -" + I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
					throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
				}
			}
			else {
				log.error("comprobarGiftCard() - " + I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
				throw new TarjetaRegaloException(I18N.getTexto("El número de tarjeta no coincide con el de la operación original"));
			}
		}
		catch (UnsupportedOperationException ex) {
			log.debug("comprobarGiftCard() - El dispositivo Giftcard no está configurado o no soporta la operación");
			operacionCorrecta = false;
		}
		catch (Exception ex) {
			log.debug("comprobarGiftCard() - Error consultando saldo", ex);
			VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(ex.getMessage(), this.getStage());
			operacionCorrecta = false;
		}

		return operacionCorrecta;
	}

	@Override
	public boolean canClose() {
		int numLineas = tbLineas.getItems().size();
		if (numLineas > 0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Existen tickets pendientes de confirmar. Antes debería finalizar la operación."), getStage());
			return false;
		}

		return true;
	}

	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSeleccionarTodoCampos() {
		addSeleccionarTodoEnFoco(tfCodigoIntro);
		addSeleccionarTodoEnFoco(tfCantidadIntro);
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

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(LineaTicket linea) {
		if (linea.getArticulo().getNumerosSerie()) {
			getDatos()
			        .put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, ((LineaTicket) ticketManager.getTicketOrigen().getLinea(linea.getLineaDocumentoOrigen())).getNumerosSerie());
			getDatos().put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
			getApplication().getMainView().showModalCentered(NumerosSerieView.class, getDatos(), getStage());
			List<String> numerosSerie = (List<String>) getDatos().get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
			linea.setNumerosSerie(numerosSerie);
		}
	}

	protected boolean quedanNumerosSeriePorAsignar(LineaTicket linea, boolean usaNumerosSerie) {
		return usaNumerosSerie && (linea.getNumerosSerie() == null || linea.getNumerosSerie().size() < linea.getCantidad().abs().intValue());
	}

	@SuppressWarnings("unchecked")
	protected boolean validarTicket() {
		boolean valido = true;

		for (LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			ArticuloBean articulo = linea.getArticulo();

			// Si no es válido no hacemos las comprobaciones para los números de serie
			// Si quedan números de serie por asignar, mostramos la pantalla de números de serie
			if (quedanNumerosSeriePorAsignar(linea, articulo.getNumerosSerie())) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Quedan números de serie por asignar. Por favor, asígnelos antes de seguir."), getStage());

				asignarNumerosSerie(linea);

				/*
				 * if (quedanNumerosSeriePorAsignar(linea,articulo.getNumerosSerie())) { return false; }
				 */
				valido = false;
			}
		}

		return valido;
	}
	
	@Override
	public void onClose() {
		super.onClose();
    	tipoDocumentoInicial = null;
	}

}
