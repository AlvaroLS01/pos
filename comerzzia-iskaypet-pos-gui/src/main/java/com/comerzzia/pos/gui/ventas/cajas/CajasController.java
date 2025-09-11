/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.cajas;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.eventos.EventoTecladoBean;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.login.LoginController;
import com.comerzzia.pos.core.gui.login.LoginView;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuariosView;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.view.ModalView;
import com.comerzzia.pos.gui.ventas.cajas.apertura.AperturaCajaView;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteView;
import com.comerzzia.pos.gui.ventas.cajas.cierre.CierreCajaView;
import com.comerzzia.pos.gui.ventas.cajas.recuentos.RecuentoCajaView;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.cajas.recuentos.CajaLineaRecuentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.empresas.EmpresaBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionCaja;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

@Component
public class CajasController extends Controller implements Initializable, IContenedorBotonera {

	// <editor-fold desc="Declaración de variables">
	private static final Logger log = Logger.getLogger(CajasController.class.getName());

	protected SesionCaja cajaSesion;

	// Variables de componente
	// botonera de menú
	protected BotoneraComponent botoneraMenu;
	// botonera de acciones de tabla Movimientos
	protected BotoneraComponent botoneraAccionesTablaMov;
	// botonera de acciones de tabla Ventas
	protected BotoneraComponent botoneraAccionesTablaVen;

	// Componentes de pantalla
	@FXML
	protected ImageView ivCaja;
	@FXML
	protected Label lbCaja;
	@FXML
	protected TextField tfUsuarioCajero, tfNombreCajero;

	@FXML
	protected TabPane tabPaneCierre;
	@FXML
	protected Tab tabMovimientos, tabVentas;
	@FXML
	protected AnchorPane panelBotonera, panelBotoneraTabVentas, panelBotoneraTabMovimientos;

	@FXML
	protected TableView<CajaVentasGui> tbVentas;
	@FXML
	protected TableView<CajaMovimientoBean> tbMovimientos;

	// Tabla de movimientos
	@FXML
	protected TableColumn<CajaMovimientoBean, String> tcMovimientosFecha, tcMovimientosConcepto, tcMovimientosDescripcion, tcMovimientosDocumento, tcMovimientosFormaPago;
	@FXML
	protected TableColumn<CajaMovimientoBean, BigDecimal> tcMovimientosEntrada, tcMovimientosSalida;

	// Tabla de ventas
	@FXML
	protected TableColumn<CajaVentasGui, String> tcVentasFecha, tcVentasDescripcion, tcVentasDocumento, tcVentasFormaPago, tcVentasCodDoc;
	@FXML
	protected TableColumn<CajaVentasGui, BigDecimal> tcVentasEntrada, tcVentasSalida;

	// Movimientos de la lista de movimientos de Venta
	protected ObservableList<CajaVentasGui> ventas;
	// Movimientos de la lista de movimientos no de ventas
	protected ObservableList<CajaMovimientoBean> movimientos;

	@Autowired
	private CajasService cajasService;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private VariablesServices variablesService;

	private ITicket ticketOperacion;

	@Autowired
	private Sesion sesion;

	final IVisor visor = Dispositivos.getInstance().getVisor();

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Creación e inicialización">
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbMovimientos.setPlaceholder(new Label(""));
		tbVentas.setPlaceholder(new Label(""));

		// Inicializamos las celdas de las tablas
		tcMovimientosFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbMovimientos", "tcMovimientosFecha", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcMovimientosEntrada.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbMovimientos", "tcMovimientosEntrada", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcMovimientosSalida.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbMovimientos", "tcMovimientosSalida", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcMovimientosConcepto.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbMovimientos", "tcMovimientosConcepto", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // para
		                                                                                                                                                                            // personalización
		                                                                                                                                                                            // de
		                                                                                                                                                                            // estilos
		tcMovimientosDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbMovimientos", "tcMovimientosDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // para
		                                                                                                                                                                                  // personalización
		                                                                                                                                                                                  // de
		                                                                                                                                                                                  // estilos
		tcMovimientosDocumento.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbMovimientos", "tcMovimientosDocumento", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // para
		                                                                                                                                                                              // personalización
		                                                                                                                                                                              // de
		                                                                                                                                                                              // estilos
		tcMovimientosFormaPago.setCellFactory(CellFactoryBuilder.createCellRendererCeldaMedioPago("tbMovimientos", "tcMovimientosFormaPago", CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // para
		                                                                                                                                                                                 // personalización
		                                                                                                                                                                                 // de
		                                                                                                                                                                                 // estilos

		tcVentasFecha.setCellFactory(CellFactoryBuilder.createCellRendererCeldaFechaHora("tbVentas", "tcVentasFecha", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcVentasEntrada.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbVentas", "tcVentasEntrada", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcVentasSalida.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbVentas", "tcVentasSalida", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcVentasCodDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbVentas", "tcVentasCodDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcVentasDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbVentas", "tcVentasDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // para
		                                                                                                                                                                   // personalización
		                                                                                                                                                                   // de
		                                                                                                                                                                   // estilos
		tcVentasDocumento.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbVentas", "tcVentasDocumento", null, CellFactoryBuilder.ESTILO_ALINEACION_DCHA)); // para
		                                                                                                                                                                // personalización
		                                                                                                                                                                // de
		                                                                                                                                                                // estilos
		tcVentasFormaPago.setCellFactory(CellFactoryBuilder.createCellRendererCeldaMedioPago("tbVentas", "tcVentasFormaPago", CellFactoryBuilder.ESTILO_ALINEACION_IZQ)); // Carga
		                                                                                                                                                                  // de
		                                                                                                                                                                  // la
		                                                                                                                                                                  // lista
		                                                                                                                                                                  // de
		                                                                                                                                                                  // medios
		                                                                                                                                                                  // de
		                                                                                                                                                                  // pago
		                                                                                                                                                                  // la
		                                                                                                                                                                  // descripción
		                                                                                                                                                                  // del
		                                                                                                                                                                  // medio
		                                                                                                                                                                  // de
		                                                                                                                                                                  // pago
		                                                                                                                                                                  // para
		                                                                                                                                                                  // el
		                                                                                                                                                                  // código
		                                                                                                                                                                  // del
		                                                                                                                                                                  // bean

		// Indicamos los campos de las tablas
		// Movimientos
		tcMovimientosFecha.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, String>("fecha"));
		tcMovimientosEntrada.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, BigDecimal>("cargo"));
		tcMovimientosSalida.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, BigDecimal>("abono"));
		tcMovimientosConcepto.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, String>("codConceptoMovimiento"));
		tcMovimientosDescripcion.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, String>("concepto"));
		tcMovimientosDocumento.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, String>("documento"));
		tcMovimientosFormaPago.setCellValueFactory(new PropertyValueFactory<CajaMovimientoBean, String>("codMedioPago"));

		// Ventas
		tcVentasFecha.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, String>("fecha"));
		tcVentasEntrada.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, BigDecimal>("cargo"));
		tcVentasSalida.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, BigDecimal>("abono"));
		tcVentasDescripcion.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, String>("concepto"));
		tcVentasDocumento.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, String>("documento"));
		tcVentasFormaPago.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, String>("codMedioPago"));
		tcVentasCodDoc.setCellValueFactory(new PropertyValueFactory<CajaVentasGui, String>("codDocumento"));
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		try {
			visor.limpiar();

			cajaSesion = sesion.getSesionCaja();

			// Actualizamos datos de BBDD de la caja si está abierta
			if (cajaSesion.isCajaAbierta()) {
				cajaSesion.actualizarDatosCaja();
			}

			// Actualizamos estado de caja en pantalla y botones activos
			actualizarEstadoCaja();

			// Poner cada lista de movimientos en cada tabla de la pestaña correspondiente
			refrescarDatosPantalla();

			// cajero
			tfNombreCajero.setText(sesion.getSesionUsuario().getUsuario().getDesusuario());
			tfUsuarioCajero.setText(sesion.getSesionUsuario().getUsuario().getUsuario());
		}
		catch (CajasServiceException e) {
			throw new InitializeGuiException(e.getMessageI18N(), e);
		}
		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. " + e.getMessage(), e);
			throw new InitializeGuiException(e);
		}

	}

	@Override
	public void initializeFocus() {
		tbMovimientos.requestFocus();
		tbMovimientos.getSelectionModel().selectLast();// .select(0);
		int indSeleccionado = tbMovimientos.getSelectionModel().getSelectedIndex();
		tbMovimientos.getFocusModel().focus(indSeleccionado);
		tbMovimientos.scrollTo(indSeleccionado);

		tbVentas.requestFocus();
		tbVentas.getSelectionModel().selectLast();// .select(0);
		indSeleccionado = tbVentas.getSelectionModel().getSelectedIndex();
		tbVentas.getFocusModel().focus(indSeleccionado);
		tbVentas.scrollTo(indSeleccionado);
		// tbVentas.getFocusModel().focus(0);
	}

	/**
	 * Inicializa componetes personalizados de pantalla: botoneras, etc
	 * 
	 * @throws InitializeGuiException
	 */
	@Override
	public void initializeComponents() throws InitializeGuiException {
		// Comprobamos que existe el tipo de documento asociado
		try {
			sesion.getAplicacion().getDocumentos().getDocumento(Documentos.CIERRE_CAJA);
		}
		catch (DocumentoException e) {
			throw new InitializeGuiException(I18N.getTexto("No está configurado el tipo de documento cierre de caja en el entorno."));
		}
		try {
			log.debug("inicializarComponentes() - Inicialización de componentes");

			// Registramos el evento de navegacion del panel de pestañas
			registrarEventosNavegacionPestanha(tabPaneCierre, this.getStage());

			log.debug("inicializarComponentes() - Carga de acciones de botonera inferior");
			try {
				PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
				botoneraMenu = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
				panelBotonera.getChildren().add(botoneraMenu);
			}
			catch (InitializeGuiException e) {
				log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
			}

			log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de movimientos");
			List<ConfiguracionBotonBean> listaAccionesTablaMov = cargarAccionesMovimientos();
			botoneraAccionesTablaMov = new BotoneraComponent(listaAccionesTablaMov.size(), 1, this, listaAccionesTablaMov, panelBotoneraTabMovimientos.getPrefWidth(),
			        panelBotoneraTabMovimientos.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			panelBotoneraTabMovimientos.getChildren().clear();
			panelBotoneraTabMovimientos.getChildren().add(botoneraAccionesTablaMov);

			log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de ventas");
			List<ConfiguracionBotonBean> listaAccionesTablaVen = cargarAccionesVentas();
			botoneraAccionesTablaVen = new BotoneraComponent(5, 1, this, listaAccionesTablaVen, panelBotoneraTabVentas.getPrefWidth(), panelBotoneraTabVentas.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotoneraTabVentas.getChildren().clear();
			panelBotoneraTabVentas.getChildren().add(botoneraAccionesTablaVen);

			crearEventoEliminarTabla(tbMovimientos);
			crearEventoImprimirTabla(tbMovimientos);
		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de gestiónd e cajas");
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
	}

	private void crearEventoImprimirTabla(TableView<CajaMovimientoBean> tabla) {
		EventHandler<KeyEvent> evh = new EventHandler<KeyEvent>(){

			@Override
			public void handle(javafx.scene.input.KeyEvent event) {
				if (event.getCode() == KeyCode.P) {
					accionTablaMovImprimir();
				}
			}
		};

		tabla.addEventFilter(KeyEvent.KEY_RELEASED, evh); // registramos el evento a la tabla
		// Añadimos el evento a la lista de eventos
		listaEventosNavegacion.add(new EventoTecladoBean(evh, KeyEvent.KEY_RELEASED, tabla, EventoTecladoBean.TYPE_FILTER));
	}

	private List<ConfiguracionBotonBean> cargarAccionesVentas() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_VEN_PRIMER_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_VEN_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_VEN_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_VEN_ULTIMO_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/imprimir-32.png", null, null, "ACCION_TABLA_VEN_IMPRIMIR", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	private List<ConfiguracionBotonBean> cargarAccionesMovimientos() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_MOV_PRIMER_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_MOV_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_MOV_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_MOV_ULTIMO_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/imprimir-32.png", null, null, "ACCION_TABLA_MOV_IMPRIMIR", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="AccionesMenu de botonera y menus de tabla">
	/**
	 * Ejecuta la acción pasada por parámetros.
	 *
	 * @param botonAccionado
	 *            botón que ha sido accionado
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());

		switch (botonAccionado.getClave()) {
		// BOTONERA TABLA MOVIMIENTOS
			case "ACCION_TABLA_MOV_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tbMovimientos);
				break;
			case "ACCION_TABLA_MOV_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tbMovimientos);
				break;
			case "ACCION_TABLA_MOV_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tbMovimientos);
				break;
			case "ACCION_TABLA_MOV_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tbMovimientos);
				break;
			case "ACCION_TABLA_MOV_BORRAR_REGISTRO":
				tbMovimientos.requestFocus();
				accionTablaMovEliminarRegistro();
				break;
			case "ACCION_TABLA_MOV_IMPRIMIR":
				tbMovimientos.requestFocus();
				accionTablaMovImprimir();
				break;

			// BOTONERA TABLA VENTAS
			case "ACCION_TABLA_VEN_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tbVentas);
				break;
			case "ACCION_TABLA_VEN_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tbVentas);
				break;
			case "ACCION_TABLA_VEN_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tbVentas);
				break;
			case "ACCION_TABLA_VEN_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tbVentas);
				break;
			case "ACCION_TABLA_VEN_IMPRIMIR":
				tbVentas.requestFocus();
				accionTablaVenImprimir();
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
				break;
		}

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
	}

	public void insertarApunte() {
		if(comprobarCierreCajaDiarioObligatorio()){			
			int registros = movimientos.size();
			getApplication().getMainView().showModalCentered(InsertarApunteView.class, this.getStage());
			refrescarDatosPantalla();
			
			// si los movimientos estaban vacios seleccionamos el ultimo registro
			// seleccionamos el ultimo registro de los movimientos
			if (registros != movimientos.size()) {
				tbMovimientos.requestFocus();
				tbMovimientos.getSelectionModel().selectLast();// .select(0);
				int indSeleccionado = tbMovimientos.getSelectionModel().getSelectedIndex();
				tbMovimientos.getFocusModel().focus(indSeleccionado);
				tbMovimientos.scrollTo(indSeleccionado);
			}
		}else{
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede insertar un apunte manual. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual),
			        getStage());
		}
	}
	
	public boolean comprobarCierreCajaDiarioObligatorio() {
    	Boolean obligatorio = variablesService.getVariableAsBoolean(VariablesServices.CAJA_CIERRE_CAJA_DIARIO_OBLIGATORIO, true);
    	if(obligatorio){
	    	Fecha fechaApertura = new Fecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
	    	Fecha fechaActual = new Fecha(new Date());
	    	if(!fechaApertura.equalsFecha(fechaActual)){
	    		return false;
	    	}
    	}
		return true;
	}

	public void abrirCierreCaja() {
		getApplication().getMainView().showModal(CierreCajaView.class);
		actualizarEstadoCaja();
	}

	private void cerrarFuncionesPendientes(Class<? extends ModalView> viewToShow) {
		boolean closeAllViewsExcept = getApplication().getMainView().closeAllViewsExcept(getView().getClass());
		if (closeAllViewsExcept) {
			getApplication().getMainView().showModal(viewToShow);
			try {
				getView().loadAndInitialize();
			}
			catch (InitializeGuiException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="AccionesMenu">
	public void imprimirUltimoCierre() {
		log.debug("accionImprimirCierreCaja()");
		try {
			Caja caja = cajasService.consultarUltimaCajaCerrada();
			cajasService.consultarMovimientos(caja);
			cajasService.consultarRecuento(caja);
			cajasService.consultarTotales(caja);
			caja.recalcularTotales();
			caja.recalcularTotalesRecuento();
			imprimirCierre(caja);
		}
		catch (CajasServiceException e) {
			log.error("Error recuperando el último cierre", e);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error recuperando el último cierre de caja para su impresión."), this.getStage());
		}
		catch (CajaEstadoException e) {
			VentanaDialogoComponent.crearVentanaAviso(e.getMessage(), this.getStage());
		}
	}

	@Override
	public void accionEventoEliminarTabla(String idTabla) {
		accionTablaMovEliminarRegistro();
	}

	private void accionTablaMovEliminarRegistro() {
		log.debug("accionTablaMovEliminarRegistro()");
		if (cajaSesion.isCajaAbierta()) {
			borrarMovimientoCaja();
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pueden borrar movimientos. La caja está cerrada."), getStage());
		}
	}

	protected void borrarMovimientoCaja() {
		int indSiguienteSeleccion = 0;
		CajaMovimientoBean selectedItem = tbMovimientos.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea de caja?"), getStage());
			if (!confirmar) {
				return;
			}
			try {
				indSiguienteSeleccion = tbMovimientos.getSelectionModel().getSelectedIndex();

				cajasService.eliminarMovimiento(selectedItem);
				cajaSesion.actualizarDatosCaja();
			}
			catch (CajasServiceException e) {
				log.error("Error eliminando la línea de caja", e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error eliminando la línea de caja."), e);
			}
			refrescarDatosPantalla();

			// seleccionamos el ultimo registro de los movimientos

			if (confirmar) {
				tbMovimientos.getSelectionModel().selectLast();// .select(0);
				int indUltimo = tbMovimientos.getSelectionModel().getSelectedIndex();
				if (indSiguienteSeleccion > indUltimo) {
					indSiguienteSeleccion = indUltimo;
				}

				tbMovimientos.requestFocus();
				tbMovimientos.getSelectionModel().select(indSiguienteSeleccion);
				tbMovimientos.getFocusModel().focus(indSiguienteSeleccion);
				tbMovimientos.scrollTo(indSiguienteSeleccion);
			}
		}
	}

	private void accionTablaMovImprimir() {
		log.debug("accionTablaMovImprimir()");
		if (tbMovimientos.getSelectionModel().getSelectedItem() != null) {
			imprimirMovimiento(tbMovimientos.getSelectionModel().getSelectedItem());
		}
	}

	@SuppressWarnings({ "unchecked" })
	public Class<? extends ITicket> getTicketClass(TipoDocumentoBean tipoDocumento) {
		String claseDocumento = tipoDocumento.getClaseDocumento();
		if (claseDocumento != null) {
			try {
				return (Class<? extends ITicket>) Class.forName(claseDocumento);
			}
			catch (ClassNotFoundException e) {
				log.error(String.format("getTicketClass() - Clase %s no encontrada, devolveremos TicketVentaAbono", claseDocumento));
			}
		}
		return TicketVentaAbono.class;
	}

	/**
	 * Devuelve la lista de clases que el Unmarshaller debe conocer. Además de la clase root, hay que pasarle la lista
	 * de superClasses de la root en orden descendente
	 */
	public List<Class<?>> getTicketClasses(TipoDocumentoBean tipoDocumento) {
		List<Class<?>> classes = new LinkedList<>();

		// Obtenemos la clase root
		Class<?> clazz = SpringContext.getBean(getTicketClass(tipoDocumento)).getClass();

		// Generamos lista de clases "ancestras" de la principal
		Class<?> superClass = clazz.getSuperclass();
		while (!superClass.equals(Object.class)) {
			classes.add(superClass);
			superClass = superClass.getSuperclass();
		}
		// Las ordenamos descendentemente
		Collections.reverse(classes);

		// Añadimos la clase principal y otras necesarias
		classes.add(clazz);
		classes.add(SpringContext.getBean(LineaTicket.class).getClass());
		classes.add(SpringContext.getBean(CabeceraTicket.class).getClass());
		classes.add(SpringContext.getBean(TotalesTicket.class).getClass());

		return classes;
	}

	private void accionTablaVenImprimir() {
		log.debug("accionTablaVenImprimir()");
		if (tbVentas.getSelectionModel().getSelectedItem() != null) {
			// imprimirMovimiento(tbVentas.getSelectionModel().getSelectedItem());
			imprimirVenta(tbVentas.getSelectionModel().getSelectedItem());
		}
	}

	public void abrirCaja() {
		log.debug("accionAperturaCaja()");
		if (cajaSesion.isCajaAbierta()) {
			VentanaDialogoComponent.crearVentanaError("Ya existe una caja abierta en el sistema.", getStage());
			return;
		}
		getApplication().getMainView().showModalCentered(AperturaCajaView.class, this.getStage());
		actualizarEstadoCaja();
		refrescarDatosPantalla();
	}

	// </editor-fold>
	public void refrescarDatosPantalla() {
		int iIndiceVentas = -1, iIndiceMovimientos = -1;
		if (tbMovimientos != null) {
			iIndiceMovimientos = tbMovimientos.getSelectionModel().getSelectedIndex();
			iIndiceVentas = tbVentas.getSelectionModel().getSelectedIndex();
		}

		ventas = FXCollections.observableList(new ArrayList<CajaVentasGui>());
		movimientos = FXCollections.observableList(new ArrayList<CajaMovimientoBean>());
		if (cajaSesion.isCajaAbierta()) {

			for (CajaMovimientoBean mov : cajaSesion.getCajaAbierta().getMovimientosVenta()) {
				ventas.add(new CajaVentasGui(mov));
			}

			movimientos.addAll(cajaSesion.getCajaAbierta().getMovimientosApuntes());
		}
		tbMovimientos.setItems(movimientos);
		tbVentas.setItems(ventas);

		tbMovimientos.getSelectionModel().select(iIndiceMovimientos);
		tbVentas.getSelectionModel().select(iIndiceVentas);
	}

	public void abrirRecuentoCaja() throws CajasServiceException {

		log.debug("abrirRecuentoCaja() - Abrir pantalla de recuento de caja.");
		boolean confirmacion = true;

		// La actual cuenta como 1, así que la desestimamos
		if (getApplication().getMainView().getSubViews().size() > 1) {
			confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Para realizar el recuento de caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"),
			        getStage());
		}
		if (confirmacion) {
			if (cajaSesion.existeRecuento(cajaSesion.getCajaAbierta())) {
				if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Ya existe un recuento de la caja. ¿Desea borrarlo y empezar de nuevo?"), getStage())) {
					if (cajaSesion.getCajaAbierta().getLineasRecuento() != null && !cajaSesion.getCajaAbierta().getLineasRecuento().isEmpty()) {
						cajaSesion.getCajaAbierta().setLineasRecuento(new ArrayList<CajaLineaRecuentoBean>());
					}
					cajaSesion.limpiarRecuentos(cajaSesion.getCajaAbierta());
				}
			}
			cerrarFuncionesPendientes(RecuentoCajaView.class);
		}

		refrescarDatosPantalla();
	}

	public void actualizarEstadoCaja() {
		// Comprobamos estado de caja
		if (!cajaSesion.isCajaAbierta()) {
			// Poner etiqueta de estado como Caja CERRADA
			lbCaja.setText(I18N.getTexto("Caja CERRADA"));
			// Desactivar botones de Cierre, Insertar apunte y Recuento (Activar el resto)
			botoneraMenu.setAccionDisabled("abrirCaja", false);
			botoneraMenu.setAccionDisabled("abrirCierreCaja", true);
			botoneraMenu.setAccionDisabled("abrirRecuentoCaja", true);
			botoneraMenu.setAccionDisabled("insertarApunte", true);
		}
		else {
			// Poner etiqueta de estado como Caja ABIERTA
			lbCaja.setText(I18N.getTexto("Caja ABIERTA"));
			// Desactivar botón de Apertura (Activar el resto)
			botoneraMenu.setAccionDisabled("abrirCaja", true);
			botoneraMenu.setAccionDisabled("abrirCierreCaja", false);
			botoneraMenu.setAccionDisabled("abrirRecuentoCaja", false);
			botoneraMenu.setAccionDisabled("insertarApunte", false);
		}
	}

	public void imprimirMovimiento(CajaMovimientoBean movimiento) {
		log.trace("imprimirMovimiento()");
		try {
			// Rellenamos los parametros
			Map<String, Object> contextoTicket = new HashMap<String, Object>();

			// Introducimos los parámetros que necesita el ticket para imprimir la información del cierre
			contextoTicket.put("movimiento", movimiento);
			contextoTicket.put("caja", sesion.getSesionCaja().getCajaAbierta().getCodCaja());
			contextoTicket.put("tienda", sesion.getAplicacion().getTienda().getCodAlmacen());
			contextoTicket.put("empleado", sesion.getSesionUsuario().getUsuario().getDesusuario());

			// Llamamos al servicio de impresión

			ServicioImpresion.imprimir(ServicioImpresion.PLANTILLA_MOVIMIENTO_CAJA, contextoTicket);
		}
		catch (DeviceException e) {
			log.error("Error en la impresión del movimiento", e);
		}
	}

	public void imprimirVenta(CajaVentasGui venta) {
		log.trace("imprimirVenta()");
		try {
			// cargamos el ticket y lo imprimimos
			TicketBean ticketConsultado = ticketsService.consultarTicket(venta.getIdDocumento(), sesion.getAplicacion().getUidActividad());
			if(ticketConsultado != null) {
				TipoDocumentoBean documento = sesion.getAplicacion().getDocumentos().getDocumento(ticketConsultado.getIdTipoDocumento());
	
				ticketOperacion = (TicketVenta) MarshallUtil.leerXML(ticketConsultado.getTicket(), getTicketClasses(documento).toArray(new Class[] {}));
	
				// Se reimprime la misma
				Map<String, Object> mapaParametros = new HashMap<String, Object>();
				mapaParametros.put("ticket", ticketOperacion);
				mapaParametros.put("urlQR", variablesService.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
	
				ServicioImpresion.imprimir(ticketOperacion.getCabecera().getFormatoImpresion(), mapaParametros);
			}
			else {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El ticket seleccionado no se ha encontrado en esta caja."), getStage());
			}
		}
		catch (TicketsServiceException ex) {
			log.error("refrescarDatosPantalla() - " + ex.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error leyendo información de ticket"), ex);
		}
		catch (DocumentoException e) {
			log.error("Error recuperando el tipo de documento del ticket.", e);
		}
		catch (DeviceException ex) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Fallo al imprimir ticket."), getStage());
		}
	}

	public void imprimirCierre(Caja caja) throws CajasServiceException {
		try {
			log.debug("imprimirCierre() - Imprimiendo ticket: " + caja.getUidDiarioCaja());
			// String printTicket = VelocityServices.getInstance().getPrintCierreCaja(caja);

			// Rellenamos los parametros
			EmpresaBean empresa = sesion.getAplicacion().getEmpresa();
			Date fechaImpresion = new Date();

			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("empresa", empresa);
			parametros.put("caja", caja);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(caja.getFechaApertura());
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
				parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()));
			}
			else {
				parametros.put("fechaApertura", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaApertura()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaApertura()));
			}
			calendar.setTime(caja.getFechaCierre());
			if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0) {
				parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()));
			}
			else {
				parametros.put("fechaCierre", FormatUtil.getInstance().formateaFechaCorta(caja.getFechaCierre()) + " " + FormatUtil.getInstance().formateaHora(caja.getFechaCierre()));
			}
			parametros.put("fechaImpresion", FormatUtil.getInstance().formateaFechaCorta(fechaImpresion) + " " + FormatUtil.getInstance().formateaHora(fechaImpresion));
			parametros.put("acumulados", new ArrayList(caja.getAcumulados().values()));

			// Llamamos al servicio de impresión
			ServicioImpresion.imprimir(ServicioImpresion.PLANTILLAS_CIERRE_CAJA, parametros);

		}
		catch (Exception e) {
			log.error("imprimirCierre() - Error imprimiendo  cierre de caja. Error inesperado: " + e.getMessage(), e);
			throw new CajasServiceException("error.service.cajas.print", e);
		}
	}

}
