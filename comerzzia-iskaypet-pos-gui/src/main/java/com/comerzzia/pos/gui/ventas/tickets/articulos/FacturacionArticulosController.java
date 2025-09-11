/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.tickets.articulos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.POSURLHandler;
import com.comerzzia.pos.core.gui.POSURLHandler.URLMethodHandler;
import com.comerzzia.pos.core.gui.RestBackgroundTask;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.imagenArticulo.ImagenArticulo;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
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
import com.comerzzia.pos.dispositivo.comun.tarjeta.info.InfoTarjetaRegaloController;
import com.comerzzia.pos.dispositivo.comun.tarjeta.info.InfoTarjetaRegaloView;
import com.comerzzia.pos.dispositivo.comun.tarjeta.saldo.SaldoTarjetaRegaloController;
import com.comerzzia.pos.dispositivo.comun.tarjeta.saldo.SaldoTarjetaRegaloView;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.util.notifications.ScreenNotificationDto;
import com.comerzzia.pos.gui.util.notifications.ShowScreenNotificationsController;
import com.comerzzia.pos.gui.util.notifications.ShowScreenNotificationsView;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionTicketGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionticketsController;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionTicketView;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.gui.ventas.paneles.SeleccionarArticuloPanelesController;
import com.comerzzia.pos.gui.ventas.paneles.SeleccionarArticuloPanelesView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager.SalvarTicketCallback;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.facturarVentaCredito.FacturarVentaCreditoView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.NumerosSerieView;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosView;
import com.comerzzia.pos.gui.ventas.tickets.puntos.CanjeoPuntosController;
import com.comerzzia.pos.gui.ventas.tickets.puntos.CanjeoPuntosView;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketView;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.ConfigContadorBean;
import com.comerzzia.pos.persistence.core.config.configcontadores.parametros.ConfigContadorParametroBean;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.TicketExample;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajaRetiradaEfectivoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.config.configContadores.ServicioConfigContadores;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.impuestos.tratamientos.TratamientoImpuestoService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionImpuestos;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.sesion.coupons.application.CouponsApplicationResultDTO;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionTextoBean;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

@Component
public class FacturacionArticulosController extends Controller implements Initializable, IContenedorBotonera {

	@Autowired
	protected Sesion sesion;

	// <editor-fold desc="Declaración de variables">
	protected Logger log = Logger.getLogger(getClass());
	public static final String PERMISO_BORRAR_LINEA = "BORRAR LINEA";
	public static final String PERMISO_MODIFICAR_LINEA = "MODIFICAR LINEA";
	public static final String PERMISO_CANCELAR_VENTA = "CANCELAR VENTA";
	public static final String PERMISO_DEVOLUCIONES = "DEVOLUCIONES";
	public static final String PERMISO_USAR_GENERICOS = "USAR GENERICOS";

	public static final String TICKET_KEY = "TICKET_KEY";

	public static final String PARAMETRO_NUMERO_TARJETA = "NUMERO_TARJETA";
	

	public TicketManager ticketManager;

	protected IVisor visor;
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;

	@FXML
	protected TextField tfCodigoIntro;
	@FXML
	protected TextFieldImporte tfCantidadIntro;
	@FXML
	protected TextField tfPesoIntro;
	@FXML
	protected Label lblPeso;
	@FXML
	protected Label lbTotalCantidad;
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected AnchorPane panelMenuTabla; // Botonera de tabla
	@FXML
	protected AnchorPane panelNumberPad;
	@FXML
	public Label lbCodCliente, lbDesCliente, lbTotal, lbStatusTicketsAparcados, lbimagenTicketsAparcados, lbNombreFidelizado, lbNumFidelizado, lbSaldoFidelizado, lbNumTarjetaFidelizado,
	        lbSaldoTarjetaFidelizado, lbTitulo, lbTotalMensaje, lbNombreTarjetaFidelizado;
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
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasImporte;
	@FXML
	protected TableColumn<LineaTicketGui, BigDecimal> tcLineasDescuento;
	@FXML
	protected TableColumn<LineaTicketGui, String> tcVendedor;
	@FXML
	protected TecladoNumerico tecladoNumerico;
	@FXML
	protected AnchorPane panelImagen;
	@FXML
	protected ImagenArticulo imagenArticulo;

	protected ObservableList<LineaTicketGui> lineas;

	@FXML
	protected ImageView imgInfo;

	@FXML
	protected Button btBuscarArticulos;

	// botonera inferior de pantalla
	protected BotoneraComponent botonera;
	// botonera de acciones de tabla
	protected BotoneraComponent botoneraAccionesTabla;

	// Formulario de validación de codigo de barras
	protected FormularioLineaArticuloBean frValidacion, frValidacionBusqueda;

	/** Indica si se debe mostrar una ventana de información para notificar a usuarios que añaden usando cmzpos:// */
	protected boolean mostrarVentanaInfo = false;
	/** Guarda el valor de precio para nuevas inserciones usando cmzpos:// */
	protected BigDecimal changePrice = null;

	@Autowired
	private ArticulosService articulosService;
	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	@Autowired
	private CajasService cajasService;
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private GiftCardService giftCardService;
	
	@Autowired
	private SesionPromociones sesionPromociones;

	/** Flag para controlar que se está ejecutando el task de insertar */
	protected boolean insertandoLinea;

	protected BigDecimal peso = BigDecimal.ZERO;

	protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

	protected Double initialLabelTotalFontSize;
	
	@Autowired
	private TratamientoImpuestoService tratamientoImpuestoService;
	
	@Autowired
	protected ServicioConfigContadores countersConfigService;
	
	@Autowired
	protected ServicioContadores countersService;

	// </editor-fold>
	// <editor-fold desc="Creación e inicialización">
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");

		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbLineas.setPlaceholder(new Label(""));

		lineas = FXCollections.observableList(new ArrayList<LineaTicketGui>());

		tcLineasArticulo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasArticulo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDescripcion.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDescripcion", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDesglose1.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDesglose1", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasDesglose2.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasDesglose2", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasPVP.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasPVP", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasImporte", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaPorcentaje("tbLineas", "tcLineasDescuento", 2, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcVendedor.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcVendedor", 2, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasCantidad.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasCantidad", 3, CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		Boolean usaDescuentoEnLinea = variablesServices.getVariableAsBoolean(VariablesServices.TICKETS_USA_DESCUENTO_EN_LINEA);
		if (!usaDescuentoEnLinea) {
			tcLineasDescuento.setVisible(false);
		}
		Boolean vendedorVisible = variablesServices.getVariableAsBoolean(VariablesServices.TPV_COLUMNA_VENDEDOR_VISIBLE, false);
		if (!vendedorVisible) {
			tcVendedor.setVisible(false);
		}

		// Asignamos las lineas a la tabla
		tbLineas.setItems(lineas);

		// Definimos un factory para cada celda para aumentar el rendimiento
		tcLineasArticulo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getArtProperty();
			}
		});
		tcLineasDescripcion.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDescripcionProperty();
			}
		});
		tcLineasCantidad.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getCantidadProperty();
			}
		});
		tcLineasDesglose1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDesglose1Property();
			}
		});
		tcLineasDesglose2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getDesglose2Property();
			}
		});
		tcLineasPVP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getPvpProperty();
			}
		});
		tcLineasImporte.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getImporteTotalFinalProperty();
			}
		});
		tcLineasDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				return cdf.getValue().getDescuentoProperty();
			}
		});
		tcVendedor.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, String>, ObservableValue<String>>(){

			@Override
			public ObservableValue<String> call(CellDataFeatures<LineaTicketGui, String> cdf) {
				return cdf.getValue().getVendedorProperty();
			}
		});

		Callback<TableView<LineaTicketGui>, TableRow<LineaTicketGui>> callback = new Callback<TableView<LineaTicketGui>, TableRow<LineaTicketGui>>(){

			@Override
			public TableRow<LineaTicketGui> call(TableView<LineaTicketGui> p) {
				return new TableRow<LineaTicketGui>(){

					@Override
					protected void updateItem(LineaTicketGui linea, boolean empty) {
						super.updateItem(linea, empty);
						if (linea != null) {
							if (linea.isCupon()) {
								if (!getStyleClass().contains("cell-renderer-cupon")) {
									getStyleClass().add("cell-renderer-cupon");
								}
							}
							else if (linea.isLineaDocAjeno()) {
								if (!getStyleClass().contains("cell-renderer-lineaDocAjeno")) {
									getStyleClass().add("cell-renderer-lineaDocAjeno");
								}
							}
							else {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
								getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							}
						}
						else {
							getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
							getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));

						}
					}
				};
			}
		};
		tbLineas.setRowFactory(callback);

		// Ocultamos el Pad numérico si es necesario
		log.debug("initialize() - Comprobando configuración para panel numérico");
		if (!Variables.MODO_PAD_NUMERICO) {
			log.debug("initialize() - PAD Numerico off");
			panelNumberPad.setVisible(false);
			panelNumberPad.getChildren().clear();
		}

		addHandlerUrlAddItem();

		POSURLHandler.addMethodHandler("AddVoucherPromotionCode", new URLMethodHandler(){

			@Override
			public void onURLMethodCalled(String method, Map<String, String> params) {
				tfCodigoIntro.setText(params.get("VoucherId"));
				tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

				mostrarVentanaInfo = true;
				nuevoCodigoArticulo();
			}
		});
		POSURLHandler.addMethodHandler("AssignLoyaltyCard", new URLMethodHandler(){

			@Override
			public void onURLMethodCalled(String method, Map<String, String> params) {
				Dispositivos.getInstance().getFidelizacion().pedirTarjetaFidelizado(getStage(), new DispositivoCallback<FidelizacionBean>(){

					@Override
					public void onSuccess(FidelizacionBean tarjeta) {
						if (tarjeta.isBaja()) {
							VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
							tarjeta = null;
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
						}
						else {
							// Tarjeta válida - lo seteamos en el ticket
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Tarjeta de fidelización añadida correctamente"), getStage());
						}

						ticketManager.recalcularConPromociones();
						refrescarDatosPantalla();
					}

					@Override
					public void onFailure(Throwable e) {
						// Quitamos los datos de fidelizado
						FidelizacionBean tarjeta = null;
						ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
						ticketManager.recalcularConPromociones();
						refrescarDatosPantalla();
					}

				});
			}
		});
	}

	protected void addHandlerUrlAddItem() {
		POSURLHandler.addMethodHandler("AddItem", new URLMethodHandler(){

			@Override
			public void onURLMethodCalled(String method, Map<String, String> params) {
				tfCodigoIntro.setText(params.get("ItemId"));
				String cantidadStr = params.get("Units");
				if (cantidadStr != null) {
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(new BigDecimal(cantidadStr)));
				}
				else {
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
				}

				mostrarVentanaInfo = true;
				nuevoCodigoArticulo();

				String precioTotalStr = params.get("Price");
				if (precioTotalStr != null) {
					BigDecimal precioTotal = FormatUtil.getInstance().desformateaBigDecimal(precioTotalStr, 2);
					changePrice = precioTotal;
				}
			}
		});
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			initializeManager();
			visor = Dispositivos.getInstance().getVisor();
			initTecladoNumerico(tecladoNumerico);

			log.debug("inicializarComponentes() - Inicialización de componentes...");

			log.debug("inicializarComponentes() - Carga de acciones de botonera inferior");
			try {
				PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
				botonera = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
				panelBotonera.getChildren().add(botonera);
			}
			catch (InitializeGuiException e) {
				log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
			}

			// Botonera de Tabla
			log.debug("inicializarComponentes() - Carga de acciones de botonera de tabla de ventas");
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = cargarAccionesTabla();
			botoneraAccionesTabla = new BotoneraComponent(1, listaAccionesAccionesTabla.size(), this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(),
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

			// Inicializamos los formularios
			frValidacionBusqueda = new FormularioLineaArticuloBean();
			frValidacionBusqueda.setFormField("cantidad", tfCantidadIntro);

			frValidacion = SpringContext.getBean(FormularioLineaArticuloBean.class);
			frValidacion.setFormField("codArticulo", tfCodigoIntro);
			frValidacion.setFormField("cantidad", tfCantidadIntro);

			tfPesoIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ZERO, 3));

			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

			tfCantidadIntro.focusedProperty().addListener(new ChangeListener<Boolean>(){

				@Override
				public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
					if (oldValue) {
						formateaCantidad();
					}
				}
			});

			if (AppConfig.rutaImagenes != null) {
				if (!AppConfig.interfazInfo.isPantallaCompleta()) {
					if (AppConfig.interfazInfo.getAlto() == 768) {
						lbTotal.setMaxHeight(140);
						lbTotalMensaje.setLayoutY(49);
					}
					else if (AppConfig.interfazInfo.getAlto() < 768) {
						lbTotal.setMaxHeight(95);
						lbTotalMensaje.setLayoutY(95);
						lbTotal.getStyleClass().add("total-image");
					}
					applyTotalLabelStyle(lbTotal);
				}

				tbLineas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LineaTicketGui>(){

					@Override
					public void changed(ObservableValue<? extends LineaTicketGui> arg0, LineaTicketGui itemAntiguo, LineaTicketGui itemNuevo) {
						if (itemNuevo != null) {
							imagenArticulo.mostrarImagen(itemNuevo.getArticulo());
						}
					}
				});
			}
			else {
				panelImagen.setVisible(false);
				panelImagen.setManaged(false);
				panelImagen.getChildren().clear();
			}

			registraEventoTeclado(new EventHandler<KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.MULTIPLY) {
						if (tfCantidadIntro.isFocused()) {
							tfCodigoIntro.requestFocus();
							tfCodigoIntro.selectAll();
						}
						else {
							cambiarCantidad();
						}
					}
				}
			}, KeyEvent.KEY_RELEASED);

			addSeleccionarTodoCampos();

			consultarCopiaSeguridad();
		}
		catch (CargarPantallaException | SesionInitException | TicketsServiceException | DocumentoException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de venta de artículos");
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
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

	protected void consultarCopiaSeguridad() throws DocumentoException, TicketsServiceException {
		// Comprobamos si existens copias de seguridad de tickets en base de datos para esta pantalla y si es así
		// ofrecemos
		// la posibilidad de recuperarlo
		TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
		final TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);

		if (copiaSeguridad != null) {
			TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[] {}));

			if (ticketRecuperado != null) {
				if (ticketRecuperado.getIdTicket() != null) {
					if(ticketRecuperado.getPagos().isEmpty()) {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket sin finalizar. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					}
					else {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket guardado con pagos realizados. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					}
					
					try {
						ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);
						abrirPagos();
						return;
					}
					catch (Throwable e) {
						log.error("consultarCopiaSeguridad() - Ha habido un error al recuperar el ticket: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						return;
					}
				}

				if (!tieneArticuloGiftCard(ticketRecuperado, tipoDocumentoActivo)) {
					if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Existe una venta sin finalizar. ¿Desea recuperarla?"), getStage())) {
						try {
							ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);

							Platform.runLater(new Runnable(){

								@Override
								public void run() {
									visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
								}
							});
						}
						catch (Throwable e) {
							log.error("consultarCopiaSeguridad() - " + e.getMessage(), e);
							VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	protected boolean tieneArticuloGiftCard(TicketVentaAbono ticketRecuperado, TipoDocumentoBean tipoDocumentoActivo) {
		for (LineaTicket linea : ticketRecuperado.getLineas()) {
			if (giftCardService.isGiftCardItem(linea.getCodArticulo())) {
				return true;
			}
		}
		return false;
	}

	public void initializeManager() throws SesionInitException {
		ticketManager = SpringContext.getBean(TicketManager.class);
		ticketManager.init();
	}

	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); // "Home"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); // "Page Up"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); // "Page Down"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); // "End"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO", "REALIZAR_ACCION")); // "Delete"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_neg.png", null, null, "ACCION_TABLA_NEGAR_REGISTRO", "REALIZAR_ACCION")); // "Num Pad -"
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/cupon.png", null, null, "SEE_CUSTOMER_COUPONS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/zoom.png", null, null, "ABRIR_BUSQUEDA_ARTICULOS", "REALIZAR_ACCION"));
		return listaAcciones;
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando formulario...");
		try {

			// Comprobamos que exista una balanza configurada antes de solicitar el peso
			if (Dispositivos.getInstance().getBalanza() != null && Dispositivos.getInstance().getBalanza().getConfiguracion() != null) {
				if (Dispositivos.getInstance().getBalanza() instanceof BalanzaNoConfig) {
					log.debug("La balanza no está configurada");
				}
				else {
					if (scheduledThreadPoolExecutor == null && Dispositivos.getInstance().getBalanza().muestraPesoPantalla()) {
						lblPeso.setVisible(true);
						tfPesoIntro.setVisible(true);
						obtenerPeso();
					}
				}
			}

			lbNombreFidelizado.setVisible(false);
			lbNumFidelizado.setVisible(false);
			lbSaldoFidelizado.setVisible(false);

			lbTitulo.setText(I18N.getTexto("Ventas"));

			// Realizamos las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
			comprobarAperturaPantalla();

			if (ticketManager.getTicket() != null) {
				if (ticketManager.getTicket().getLineas() != null && ticketManager.getTicket().getLineas().isEmpty()) {
					visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
				}
				else {
					int ultimArticulo = ticketManager.getTicket().getLineas().size();
					LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
					escribirLineaEnVisor(linea);
				}
			}

			if (!ticketManager.isTicketAbierto()) {
				crearNuevoTicket();
				visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
				visor.modoEspera();
			}
			else {
				if (!sesion.getSesionCaja().getCajaAbierta().getUidDiarioCaja().equals(ticketManager.getTicket().getCabecera().getUidDiarioCaja())) {
					crearNuevoTicket();
				}
				visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
			}

			// El septimo botón de la tabla es negar. Si el sistema tiene valores positivos o negativos para el
			// documento, quitamos le botón y su acción asociada
			botoneraAccionesTabla.setAccionVisible("ACCION_TABLA_NEGAR_REGISTRO", ticketManager.getDocumentoActivo().isSignoLibre());
			
			// 8th button must be visible only if loyalty module is loaded
			botoneraAccionesTabla.setAccionVisible("SEE_CUSTOMER_COUPONS", sesionPromociones.isLoadedLoyaltyModule());

			tfCodigoIntro.clear();
			if (ticketManager.getTicket().getLineas().isEmpty()) {
				ClienteBean cliente = ticketManager.getTicket().getCliente();
				FidelizacionBean tarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				crearNuevoTicket();
				ticketManager.getTicket().setCliente(cliente);
				ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
			}
			refrescarDatosPantalla();
			actualizarTicketsAparcados();
		}
		catch (CajaEstadoException | CajasServiceException | PromocionesServiceException | DocumentoException e) {
			log.error("initializeForm() - Error inicializando formulario :" + e.getMessageI18N(), e);
			throw new InitializeGuiException(e.getMessageI18N(), e);
		}
		catch (InitializeGuiException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("initializeForm() - Error inesperado inicializando formulario. ", e);
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
		log.debug("comprobarAperturaPantalla()");
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

			boolean hayTicketsAparcados = true;
			try {
				hayTicketsAparcados = ticketManager.recuperarTicketsAparcados(ticketManager.getNuevoDocumentoActivo().getIdTipoDocumento()).size() > 0;
			}
			catch (Exception e) {
				log.error("comprobarAperturaPantalla() - No se ha podido comprobar si hay tickets aparcados: " + e.getMessage(), e);
			}

			if (!((ticketManager.getTicket() != null && ticketManager.getTicket().getLineas().size() > 0) || hayTicketsAparcados)) {
				String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
				String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual),
				        getStage());
				throw new InitializeGuiException(false);
			}
		}
		
		checkAvisoRetirada();
	}

	@Override
	public void initializeFocus() {
		tfCodigoIntro.requestFocus();
	}

	@Override
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		botonera.comprobarPermisosOperaciones();
		try {
			super.compruebaPermisos(PERMISO_BORRAR_LINEA);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_BORRAR_REGISTRO", true);
		}
		try {
			super.compruebaPermisos(PERMISO_MODIFICAR_LINEA);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_EDITAR_REGISTRO", true);
		}
		try {
			super.compruebaPermisos(PERMISO_DEVOLUCIONES);
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_NEGAR_REGISTRO", false);
		}
		catch (SinPermisosException ex) {
			botoneraAccionesTabla.setAccionDisabled("ACCION_TABLA_NEGAR_REGISTRO", true);
		}
	}

	@Override
	public boolean canClose() {
		int numLineas = tbLineas.getItems().size();
		if (numLineas > 0 || ticketManager.countTicketsAparcados() > 0) {
			try {
				super.compruebaPermisos(PERMISO_CANCELAR_VENTA);
				// Tiene permisos para cancelar, notificar que debe cerrar primero
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Existen tickets pendientes de confirmar. Antes debería finalizar la operación."), getStage());
			}
			catch (SinPermisosException ex) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Tiene tickets pendientes de confirmar. No tiene permisos para cancelar la venta."), getStage());
			}
			return false;
		}

		visor.escribirLineaArriba(I18N.getTexto("---CAJA CERRADA---"));
		visor.modoEspera();

		return true;
	}

	protected void crearNuevoTicket() throws PromocionesServiceException, DocumentoException {
		ticketManager.nuevoTicket();
	}

	// </editor-fold>
	// <editor-fold desc="Funciones relacionadas con interfaz GUI y manejo de pantalla">
	/**
	 * Acción de introducción de código desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfCodigoIntro(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			nuevoCodigoArticulo();
		}
		else if (AppConfig.modoDesarrollo && (event.isAltDown() && event.isControlDown() && event.getCode() == KeyCode.T)) {
			generarTicketsAleatorios();
		}
	}

	/**
	 * Acción que anula un ticket
	 */
	public void cancelarVenta() {
		log.debug("cancelarVenta()");
		try {
			boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar todas las líneas del ticket?"), getStage());
			if (!confirmacion) {
				return;
			}

			if (ticketManager.getTicket().getIdTicket() != null) {
				ticketManager.salvarTicketVacio();
			}
			ticketManager.eliminarTicketCompleto();

			// Restauramos la cantidad en la pantalla
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));

			refrescarDatosPantalla();
			initializeFocus();
			tbLineas.getSelectionModel().clearSelection();

			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();
		}
		catch (TicketsServiceException | PromocionesServiceException | DocumentoException ex) {
			log.error("accionAnularTicket() - Error inicializando nuevo ticket: " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
		}
	}

	/**
	 * Acción de introducción de cantidad desde la interfaz
	 *
	 * @param event
	 */
	public void actionTfCantidadIntro(KeyEvent event) {
		log.debug("actionTfCantidadIntro() - acción de introducción de cantidad de artículo");
		if (event.getCode() == KeyCode.ENTER) {
			tfCodigoIntro.requestFocus();
			tfCodigoIntro.selectAll();
		}
	}

	protected void formateaCantidad() {
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

	/**
	 * Preparamos la interfaz para un cambio de código tras cambiar una cantidad
	 */
	public void nuevaCantidad() {
		log.debug("nuevaCantidad() - preparamos la interfaz para un cambio de código tras cambiar una cantidad");
		tfCantidadIntro.setText(tfCantidadIntro.getText().replace("*", ""));
		if (tfCantidadIntro.getText().isEmpty()) {
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		}
	}

	/**
	 * Preparamos la interfaz para una modificación de la cantidad
	 */
	public void cambiarCantidad() {
		log.debug("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		tfCodigoIntro.setText(tfCodigoIntro.getText().replace("*", ""));
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				tfCantidadIntro.requestFocus();
			}
		});
		tfCantidadIntro.selectAll();
	}
	
	public boolean checkAvisoRetirada() {
		try {
			if(cajasService.validarImporteAvisoRetirada()){
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha superado el límite de efectivo. Deberá realizar una retirada"), getStage());
				return true;
			}							
		} catch (Exception e) {
			log.error("checkAvisoRetirada() - Excepción en cajasService : " + e.getCause(), e);
		}
		return false;
	}

	public boolean checkBloqueoRetirada() {
		try {
			if (cajasService.validarImporteBloqueoRetirada()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe realizar una retirada de efectivo para poder continuar"), getStage());
				return true;
			}
		} catch (CajasServiceException e) {
			log.error("checkBloqueoRetirada() Error en cajasService: " + e.getMessage(), e);
		}
		return false;
	}
	/**
	 * Añade un nuevo artículo
	 */
	public void nuevoCodigoArticulo() {
		// no dejar introducir líneas en un ticket nuevo si ha superado el importe de bloqueo de retirada
		if (tbLineas.getItems().size() == 0 && checkBloqueoRetirada()) {			
			tfCodigoIntro.clear();
			return;
		}
		
		// Validamos los datos
		if (!tfCodigoIntro.getText().trim().isEmpty()) {
			log.debug("nuevoCodigoArticulo() - Creando línea de artículo");

			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(tfCodigoIntro.getText().trim().toUpperCase());
			BigDecimal cantidad = frValidacion.getCantidadAsBigDecimal();
			tfCodigoIntro.clear();

			if (accionValidarFormulario() && cantidad != null && !BigDecimalUtil.isIgualACero(cantidad)) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				// Si es prefijo de tarjeta fidelizacion, marcamos la venta como fidelizado y llamamos al REST
				if (Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(frValidacion.getCodArticulo())) {

					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();

					Dispositivos.getInstance().getFidelizacion().cargarTarjetaFidelizado(frValidacion.getCodArticulo(), getStage(), new DispositivoCallback<FidelizacionBean>(){

						@Override
						public void onSuccess(FidelizacionBean tarjeta) {
							if (tarjeta.isBaja()) {
								VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
								tarjeta = null;
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							}
							else {
								// Tarjeta válida - lo seteamos en el ticket
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							}

							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}

						@Override
						public void onFailure(Throwable e) {
							// Los errores se muestran desde el código del dispositivo
							// Quitamos los datos de fidelizado
							FidelizacionBean tarjeta = null;
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}
					});
					return;
				}

				NuevoCodigoArticuloTask taskArticulo = SpringContext.getBean(NuevoCodigoArticuloTask.class, this, frValidacion.getCodArticulo(), cantidad); // anidada
				taskArticulo.start();
			}
		}
	}

	/**
	 * Método llamado desde la Botonera si hay algún botón de tipo ITEM
	 */
	public void addItem(String item) {
		SpringContext.getBean(NuevoCodigoArticuloTask.class, this, item, BigDecimal.ONE).start();
	}

	@Component
	@Scope("prototype")
	public class NuevoCodigoArticuloTask extends BackgroundTask<LineaTicket> {

		protected final String codArticulo;
		protected final BigDecimal cantidad;

		public NuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad) {
			super(false);
			this.codArticulo = codArticulo;
			this.cantidad = BigDecimalUtil.redondear(ticketManager.tratarSignoCantidad(cantidad, ticketManager.getTicket().getCabecera().getCodTipoDocumento()), 3);
		}

		@Override
		protected LineaTicket call() throws Exception {
			insertandoLinea = true;
			return nuevoArticuloTaskCall(codArticulo, cantidad);
		}

		@Override
		protected void succeeded() {
			try {
				super.succeeded();
				nuevoArticuloTaskSucceeded(getValue());
			}
			catch (java.lang.Exception e) {
				log.error("succeeded() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

		@Override
		protected void failed() {
			try {
				nuevoArticuloTaskFailed(getCMZException());
				super.failed();
			}
			catch (java.lang.Exception e) {
				log.error("failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
			}
		}

		@Override
		protected void done() {
			super.done();
			insertandoLinea = false;
		}

	}

	protected LineaTicket nuevoArticuloTaskCall(String codArticulo, BigDecimal cantidad) throws LineaTicketException, PromocionesServiceException, DocumentoException, CajasServiceException,
	        InitializeGuiException, CajaRetiradaEfectivoException {
		return insertarLineaVenta(codArticulo, null, null, cantidad);
	}

	protected void nuevoArticuloTaskSucceeded(LineaTicket value) {
		try {
			boolean esTarjetaRegalo = ticketManager.comprobarTarjetaRegaloLineaYaInsertada(value);

			if (esTarjetaRegalo) {
				recargarGiftcard();
			}
			else {
				int ultimArticulo = ticketManager.getTicket().getLineas().size();
				LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);

				if (changePrice != null) {
					linea.setPrecioTotalSinDto(changePrice);
					SesionImpuestos sesionImpuestos = sesion.getImpuestos();
					BigDecimal precioSinDto = sesionImpuestos.getPrecioSinImpuestos(linea.getCodImpuesto(), changePrice, linea.getCabecera().getCliente().getIdTratImpuestos());
					linea.setPrecioSinDto(precioSinDto);
					changePrice = null;
				}
			}

			LineaTicket linea = value;

			if (linea != null && !esTarjetaRegalo) { // No es cupón
				comprobarArticuloGenerico(value);
				if (linea.getGenerico()) {
					HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
					parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
					parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
					abrirVentanaEdicionArticulo(parametrosEdicionArticulo);

					if (parametrosEdicionArticulo.containsKey(EdicionArticuloController.CLAVE_CANCELADO)) {
						throw new LineaInsertadaNoPermitidaException(linea);
					}
				}
				ticketManager.recalcularConPromociones();
				comprobarLineaPrecioCero(value);

				visor.escribir(linea.getArticulo().getDesArticulo(), linea.getCantidadAsString() + " X " + FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
			}

			asignarNumerosSerie(linea);

			if (mostrarVentanaInfo) {
				if (linea == null) {
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El cupón se ha añadido correctamente."), getStage());
				}
				else {
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El artículo se ha añadido correctamente."), getStage());
				}
			}

			mostrarVentanaInfo = false;

			// Restauramos la cantidad en la pantalla
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
			if (!esTarjetaRegalo) {
				refrescarDatosPantalla();
			}
			visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));

			tbLineas.getSelectionModel().select(0);
		}
		catch (LineaInsertadaNoPermitidaException e) {
			ticketManager.getTicket().getLineas().remove(value);
			guardarCopiaSeguridad();
			if (e.getMessage() != null) {
				VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
			}
		}
	}

	protected void nuevoArticuloTaskFailed(com.comerzzia.pos.util.exception.Exception cmzException) {
		mostrarVentanaInfo = false;
		log.error("failed() - NuevoCodigoArticuloTask failed: " + cmzException, cmzException);
		VentanaDialogoComponent.crearVentanaError(cmzException.getMessageI18N(), getStage());
	}

	public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla() - Refrescando datos de pantalla...");

		BotonBotoneraComponent boton = botoneraAccionesTabla.getBotonBotonera("SEE_CUSTOMER_COUPONS");
		if(boton != null && boton instanceof BotonBotoneraSimpleComponent) {
			((BotonBotoneraSimpleComponent) boton).setDisable(true);
		}

		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if (datosFidelizado == null) {
			lbNombreTarjetaFidelizado.setText("");
			lbNombreTarjetaFidelizado.getStyleClass().remove("infoFidelizado");
			imgInfo.setVisible(false);
			lbNombreFidelizado.setVisible(false);
			lbNumTarjetaFidelizado.setText("");
			lbNumFidelizado.setVisible(false);
			lbSaldoTarjetaFidelizado.setText("");
			lbSaldoFidelizado.setVisible(false);
		}
		else {
			if (datosFidelizado.getNumTarjetaFidelizado() != null) {
				lbNumFidelizado.setVisible(true);
				lbNumTarjetaFidelizado.setText(datosFidelizado.getNumTarjetaFidelizado());
			}
			else {
				lbNumFidelizado.setVisible(false);
				lbNumTarjetaFidelizado.setText("");
			}
			if (datosFidelizado.getNombre() != null) {
				lbNombreFidelizado.setVisible(true);
				lbNombreTarjetaFidelizado.setText(datosFidelizado.getNombre() + " " + datosFidelizado.getApellido());
				lbNombreTarjetaFidelizado.getStyleClass().add("infoFidelizado");
				imgInfo.setVisible(true);
			}
			else {
				lbNombreTarjetaFidelizado.setText("");
				lbNombreTarjetaFidelizado.getStyleClass().remove("infoFidelizado");
				lbNombreFidelizado.setVisible(false);
				imgInfo.setVisible(false);
			}
			if (datosFidelizado.getSaldoTotal() != null && !BigDecimalUtil.isIgualACero(datosFidelizado.getSaldoTotal())) {
				lbSaldoFidelizado.setVisible(true);
				lbSaldoTarjetaFidelizado.setText(datosFidelizado.getSaldoTotal().toString());
			}
			else {
				lbSaldoTarjetaFidelizado.setText("");
				lbSaldoFidelizado.setVisible(false);
			}
			
			if(datosFidelizado.getAvailableCoupons() != null && !datosFidelizado.getAvailableCoupons().isEmpty()) {				
				if(boton != null && boton instanceof BotonBotoneraSimpleComponent) {
					((BotonBotoneraSimpleComponent) boton).setDisable(false);
				}
			}
		}

		String totalFormateado = FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getTotal());
		resetAutosizeLabelTotalFont();
		lbTotal.setText(totalFormateado);
		applyTotalLabelStyle(lbTotal);

		lbCodCliente.setText(((TicketVentaAbono) ticketManager.getTicket()).getCliente().getCodCliente());
		lbDesCliente.setText(((TicketVentaAbono) ticketManager.getTicket()).getCliente().getDesCliente());

		LineaTicketGui selectedItem = getLineaSeleccionada();
		lineas.clear();
		for (LineaTicket lineaTicket : ((TicketVentaAbono) ticketManager.getTicket()).getLineas()) {
			lineas.add(createLineaGui(lineaTicket));
		}
		for (CuponAplicadoTicket cupon : ((TicketVentaAbono) ticketManager.getTicket()).getCuponesAplicados()) {
			lineas.add(createLineaGui(cupon));
		}

		Collections.reverse(lineas);
		if (selectedItem != null) {
			tbLineas.getSelectionModel().select(lineas.indexOf(searchIdLinea(selectedItem)));
		}
		if (getLineaSeleccionada() == null) {
			tfCodigoIntro.requestFocus();
		}
		tbLineas.scrollTo(0);

		if (imagenArticulo != null) {
			imagenArticulo.setImage(null);
		}

		obtenerCantidadTotal();
	}

	protected void actualizarTicketsAparcados() {
	    int tiques = ticketManager.countTicketsAparcados();
		if (tiques > 0) {
			lbStatusTicketsAparcados.setText(I18N.getTexto("Hay tickets aparcados") + " (" + tiques + ")");
			lbimagenTicketsAparcados.setVisible(true);
		}
		else {
			lbStatusTicketsAparcados.setText(I18N.getTexto("No hay tickets aparcados"));
			lbimagenTicketsAparcados.setVisible(false);
		}
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

	protected void resetAutosizeLabelTotalFont() {
		if (initialLabelTotalFontSize == null) {
			initialLabelTotalFontSize = lbTotal.getFont().getSize();
		}
		lbTotal.getStyleClass().remove("total-reduced");
	}

	protected LineaTicketGui createLineaGui(CuponAplicadoTicket cupon) {
		return new LineaTicketGui(cupon);
	}

	protected LineaTicketGui createLineaGui(LineaTicket lineaTicket) {
		return new LineaTicketGui(lineaTicket);
	}

	protected LineaTicketGui searchIdLinea(LineaTicketGui selectedItem) {
		for (LineaTicketGui linea : lineas) {
			if (linea.getIdLinea() != null) {
				if (linea.getIdLinea().equals(selectedItem.getIdLinea())) {
					return linea;
				}
			}
			else {
				// Es cupón
				if (linea.getArticulo().equals(selectedItem.getArticulo())) {
					return linea;
				}
			}
		}
		return null;
	}

	// </editor-fold>
	// <editor-fold desc="Funciones relacionadas con botonera y botonera de tabla">
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
			case "ACCION_TABLA_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tbLineas);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tbLineas);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tbLineas);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tbLineas);
				break;
			case "ACCION_TABLA_BORRAR_REGISTRO":
				accionTablaEliminarRegistro();
				break;
			case "ACCION_TABLA_EDITAR_REGISTRO":
				accionTablaEditarRegistro();
				break;
			case "ACCION_TABLA_NEGAR_REGISTRO":
				accionNegarRegistroTabla();
				break;
			case "ABRIR_BUSQUEDA_ARTICULOS":
				abrirBusquedaArticulos();
				break;
			case "SEE_CUSTOMER_COUPONS":
				if(sesionPromociones.isLoadedLoyaltyModule()) {
					seeCustomerCoupons();
				} else {
					log.warn("realizarAccion() - El módulo de Fidelización no está cargado");
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El módulo de Fidelización no está cargado"),this.getStage());
				}
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

		ticketManager.getTicket().setCajero(sesion.getSesionUsuario().getUsuario());
		guardarCopiaSeguridad();

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
			cambiarClienteVenta(cliente);
			lbCodCliente.setText(((TicketVentaAbono) ticketManager.getTicket()).getCliente().getCodCliente());
			lbDesCliente.setText(((TicketVentaAbono) ticketManager.getTicket()).getCliente().getDesCliente());
		}
		initializeFocus();
	}

	protected void cambiarClienteVenta(ClienteBean cliente) {
		TratamientoImpuestoBean trat = null;
		TratamientoImpuestoBean shopTrat = null;
		TicketVentaAbono ticket = ((TicketVentaAbono) ticketManager.getTicket());
		try {
			trat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos());
			shopTrat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
		}catch(Exception ignore) {}
		if(trat == null || !trat.getCodpais().equals(ticket.getTienda().getCliente().getCodpais())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."), this.getStage());
		}else if(ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos().equals(trat.getIdTratImpuestos()) || VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",trat.getDestratimp(),shopTrat.getDestratimp()), this.getStage())) {			
			((TicketVentaAbono) ticketManager.getTicket()).setCliente(cliente);
		}
	}

	/**
	 * Acción Evento Editar registro
	 *
	 * @param idTabla
	 */
	@Override
	public void accionEventoEnterTabla(String idTabla) {
		log.debug("accionEventoEnterTabla()- id:" + idTabla);
		accionTablaEditarRegistro();
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
	 * Acción Evento Negar registro
	 *
	 * @param idTabla
	 */
	@Override
	public void accionEventoNegarTabla(String idTabla) {
		if (ticketManager.getDocumentoActivo().isSignoLibre()) {
			log.debug("accionEventoNegarTabla()- id:" + idTabla);
			accionNegarRegistroTabla();
		}
	}

	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@FXML
	protected void accionTablaEliminarRegistro() {
		log.debug("accionTablaEliminarRegistro() - ");
		try {
			if (!tbLineas.getItems().isEmpty() && getLineaSeleccionada() != null) {
				super.compruebaPermisos(PERMISO_BORRAR_LINEA);
				LineaTicketGui selectedItem = getLineaSeleccionada();
				if (selectedItem.isCupon()) {
					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();
				}
				else {
					int idLinea = getLineaSeleccionada().getIdLinea();
					ILineaTicket linea = ticketManager.getTicket().getLinea(idLinea);
					ILineaTicket lastLineMemory = null;
					if (linea.isEditable()) {
						boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
						if (!confirmar) {
							return;
						}
						if(ticketManager.getTicket().getLineas().size() == 1) {
							lastLineMemory = ((LineaTicket) ticketManager.getTicket().getLineas().get(0)).clone();
						}
						ticketManager.eliminarLineaArticulo(idLinea);

						int ultimArticulo = ticketManager.getTicket().getLineas().size();
						if (ultimArticulo > 0) {
							LineaTicket ultimaLinea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
							escribirLineaEnVisor(ultimaLinea);
						}
						else {
							visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
						}

						guardarCopiaSeguridad();
						seleccionarSiguienteLinea();
						refrescarDatosPantalla();

						if (ticketManager.getTicket().getLineas().size() > 0) {
							visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
						}
						else {
							visor.modoEspera();
						}
					}
					else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
					}

					if (ticketManager.getTicket().getIdTicket() != null && ticketManager.getTicket().getLineas().isEmpty()) {
						if(lastLineMemory != null) {
							ticketManager.getTicket().getLineas().add(lastLineMemory);
						}
						ticketManager.salvarTicketVacio();	
						try {
							ticketManager.eliminarTicketCompleto();
						} catch (Exception e) {
							log.error("Ha ocurrido un error al eliminar el ticket ", e);
						}
					}
				}
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea"), getStage());
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
				LineaTicketGui selectedItem = getLineaSeleccionada();
				if (selectedItem.isCupon()) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
				}
				else {
					int linea = selectedItem.getIdLinea();
					if (linea > 0) {
						ILineaTicket lineaTicket = ticketManager.getTicket().getLinea(linea);
						if (lineaTicket != null) {
							if (lineaTicket.isEditable()) {
								// Creamos la ventana de edición de artículos
								HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
								parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, lineaTicket);
								parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
								abrirVentanaEdicionArticulo(parametrosEdicionArticulo);

								ticketManager.recalcularConPromociones();
								guardarCopiaSeguridad();
								escribirLineaEnVisor((LineaTicket) lineaTicket);
								refrescarDatosPantalla();
								visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
							}
							else {
								VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
							}
						}
					}
				}
			}
		}
		catch (SinPermisosException ex) {
			log.debug("accionTablaEditarRegistro() - El usuario no tiene permisos para modificar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para modificar una línea"), getStage());
		}
	}

	protected void abrirVentanaEdicionArticulo(HashMap<String, Object> parametrosEdicionArticulo) {
		getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, this.getStage());
	}

	/**
	 * Acción negar registro de la tabla
	 */
	// @FXML
	protected void accionNegarRegistroTabla() {
		try {
			log.debug("accionNegarRegistroTabla() - ");
			super.compruebaPermisos(PERMISO_DEVOLUCIONES);
			LineaTicketGui lineaSeleccionada = getLineaSeleccionada();
			if (lineaSeleccionada != null) {
				if (lineaSeleccionada.isCupon()) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
				}
				else {
					int idLinea = lineaSeleccionada.getIdLinea();
					ILineaTicket linea = ticketManager.getTicket().getLinea(idLinea);
					if (linea.isEditable()) {
						ticketManager.negarLineaArticulo(idLinea);
						escribirLineaEnVisor((LineaTicket) linea);
						guardarCopiaSeguridad();
						refrescarDatosPantalla();
						visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
					}
					else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea no se puede modificar."), this.getStage());
					}
				}
			}
		}
		catch (LineaTicketException e) {
			log.error("accionNegarRegistroTabla() - Error recalculando importe de línea: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessageI18N(), e);
		}
		catch (SinPermisosException ex) {
			log.debug("accionNegarRegistroTabla() - El usuario no tiene permisos para realizar devolución");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para realizar una devolución"), getStage());
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

	public void abrirBusquedaArticulos() {
		log.debug("abrirBusquedaArticulos()");

		/*
		 * // Validamos el campo cantidad antes de iniciar la búsqueda. Si el campo es vacío lo seteamos a 1 sin
		 * devolver // error if (tfCantidadIntro.getText().trim().equals("")) {
		 * tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3)); }
		 */

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
		abrirVentanaBusquedaArticulos();
		initializeFocus();

		if (datos.containsKey(BuscarArticulosController.PARAMETRO_SALIDA_CODART)) {
			String codArt = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_CODART);
			String desglose1 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE1);
			String desglose2 = (String) getDatos().get(BuscarArticulosController.PARAMETRO_SALIDA_DESGLOSE2);

			frValidacionBusqueda.setCantidad(tfCantidadIntro.getText());
			try {
				if (accionValidarFormularioBusqueda()) {
					if (ticketManager.comprobarTarjetaRegalo(codArt)) {
						insertarLineaVenta(codArt, desglose1, desglose2, BigDecimal.ONE);

						recargarGiftcard();
					}
					else {
						LineaTicket linea = insertarLineaVenta(codArt, desglose1, desglose2, frValidacionBusqueda.getCantidadAsBigDecimal());

						comprobarArticuloGenerico(linea);

						if (linea.getGenerico()) {
							HashMap<String, Object> parametrosEdicionArticulo = new HashMap<>();
							parametrosEdicionArticulo.put(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO, linea);
							parametrosEdicionArticulo.put(FacturacionArticulosController.TICKET_KEY, ticketManager);
							abrirVentanaEdicionArticulo(parametrosEdicionArticulo);

							if (parametrosEdicionArticulo.containsKey(EdicionArticuloController.CLAVE_CANCELADO)) {
								throw new LineaInsertadaNoPermitidaException(linea);
							}
						}

						comprobarLineaPrecioCero(linea);

						// Comprobamos si es necesario asignar números de serie
						asignarNumerosSerie(linea);

						ticketManager.recalcularConPromociones();
					}

					// Restauramos la cantidad en la pantalla
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
					refrescarDatosPantalla();

					if (ticketManager.getTicket().getLineas().size() > 0) {
						tbLineas.getSelectionModel().select(0);

						int ultimArticulo = ticketManager.getTicket().getLineas().size();
						LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
						escribirLineaEnVisor(linea);

						visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
					}
				}
			}
			catch (LineaTicketException ex) {
				log.error("abrirBusquedaArticulos() - ACCION BUSQUEDA ARTICULOS - Error registrando línea de ticket");
				VentanaDialogoComponent.crearVentanaError(ex.getLocalizedMessage(), this.getScene().getWindow());
			}
			catch (TarjetaRegaloException e) {
				log.error(e);
				VentanaDialogoComponent.crearVentanaError(e.getMessageI18N(), getStage());
			}
			catch (LineaInsertadaNoPermitidaException e) {
				ticketManager.getTicket().getLineas().remove(e.getLinea());
				guardarCopiaSeguridad();
				if (e.getMessage() != null) {
					VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
				}
			}
			catch (CajaRetiradaEfectivoException e) {
				VentanaDialogoComponent.crearVentanaError(e.getMessageDefault(), getStage());
			}
			catch (Exception e) {
				log.error("abrirBusquedaArticulos() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido insertar la línea"), e);
			}
		}
	}

	protected void abrirVentanaBusquedaArticulos() {
		getApplication().getMainView().showModal(BuscarArticulosView.class, getDatos());
	}

	protected void escribirLineaEnVisor(LineaTicket linea) {
		String desc = linea.getArticulo().getDesArticulo();
		visor.escribir(desc, linea.getCantidadAsString() + " X " + FormatUtil.getInstance().formateaImporte(linea.getPrecioTotalConDto()));
	}

	// </editor-fold>
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

	/**
	 * Método que aparca el ticket en base de datos
	 */
	public void aparcarTicket() {
		log.debug("aparcarTicket()");
		if (!ticketManager.isTicketVacio()) { // Si el ticket no es vacío se puede aparcar
			try {
				log.debug("accionAparcarTicket()");

				// Se borra la copia de seguridad para que no de fallos de violación de claves al guardar el ticket
				// aparcado en la misma tabla
				copiaSeguridadTicketService.guardarBackupTicketActivo(new TicketVentaAbono());

				// Comprobamos que el ticket tiene almenos un artículo
				ticketManager.aparcarTicket();

				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El ticket ha sido aparcado."), getStage());

				try {
					getView().loadAndInitialize();
					actualizarTicketsAparcados();
					guardarCopiaSeguridad();

					visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
					visor.modoEspera();
				}
				catch (InitializeGuiException e) {
					VentanaDialogoComponent.crearVentanaError(getStage(), e);
				}
			}
			catch (TicketsServiceException ex) {
				log.error("accionAparcarTicket()");
				VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageDefault(), ex);
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), getStage());
		}
	}

	public void recuperarTicket() {
		log.trace("recuperarTicket()");
		if (ticketManager.isTicketVacio()) { // Solo si el ticket esta vacío, podemos recuperar el ticket
			getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
			getDatos().put(RecuperarTicketController.PARAMETRO_TIPO_DOCUMENTO, ticketManager.getDocumentoActivo().getIdTipoDocumento());
			getApplication().getMainView().showModalCentered(RecuperarTicketView.class, getDatos(), this.getStage());

			try {
				getView().loadAndInitialize();
				visor.escribir(I18N.getTexto("TOTAL A PAGAR"), ticketManager.getTicket().getTotales().getTotalAsString());
				visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
				guardarCopiaSeguridad();
			}
			catch (InitializeGuiException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no está vacío."), this.getScene().getWindow());
		}
	}

	public void abrirPagos() {
		log.trace("abrirPagos()");
		if (insertandoLinea) {
			return;
		}
		if (!ticketManager.isTicketVacio()) {
			if (validarNumerosSerie()) {
				log.debug("abrirPagos() - El ticket tiene líneas");

				Dispositivos.getInstance().getFidelizacion().ignorarTarjetaFidelizado();
				
				if(sesionPromociones.isLoadedLoyaltyModule()) {
					useCustomerCoupons();
				}
				
				showTextPromotions();

				getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
				abrirVentanaPagos();
				initializeFocus();

				if (!getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
					try {
						crearNuevoTicket();				
					} catch (Exception e) {
						log.error("abrirPagos() - Excepción al inicializar ticket : " + e.getCause(), e);
					}
				}
				
				refrescarDatosPantalla();
				cerrarPantallaPagos();
				
				if (!getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
					if (!checkBloqueoRetirada()) {
						checkAvisoRetirada();
					}
				}
			}
		}
		else {
			log.warn("abrirPagos() - Ticket vacio");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), this.getStage());
		}
	}

	@SuppressWarnings("unchecked")
	protected void showTextPromotions() {
		List<ScreenNotificationDto> notifications = new ArrayList<ScreenNotificationDto>();
		
		List<PromocionTicket> promotions = ticketManager.getTicket().getPromociones();
		if(promotions != null) {
			for(PromocionTicket promotion : promotions) {
				Promocion activePromotion = sesionPromociones.getPromocionActiva(promotion.getIdPromocion());
				if(activePromotion instanceof PromocionTextoBean) {
					String titlePromotion = "#" + activePromotion.getIdPromocion() + " - " + activePromotion.getDescripcion();
					ScreenNotificationDto dto = new ScreenNotificationDto("info", titlePromotion, activePromotion.getTextoPromocion());
					notifications.add(dto);
				}
			}
		}
		
		if(!notifications.isEmpty()) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ShowScreenNotificationsController.PARAM_NOTIFICATIONS, notifications);
			params.put(ShowScreenNotificationsController.PARAM_TITLE, I18N.getTexto("Información para el cliente"));
			getApplication().getMainView().showModalCentered(ShowScreenNotificationsView.class, params, getStage());
		}
	}

	protected void cerrarPantallaPagos() {
		getView().resetSubViews();
	}

	protected void abrirVentanaPagos() {
		getApplication().getMainView().showModal(PagosView.class, getDatos());
	}
	
	public void canjearPuntos() {
		log.trace("canjearPuntos()");
		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		String numTarjetaFidelizado = (datosFidelizado == null) ? null : datosFidelizado.getNumTarjetaFidelizado();
		if (!ticketManager.isTicketVacio() && numTarjetaFidelizado != null) {
			log.debug("abrirPagosPuntos() - El ticket tiene líneas y posee tarjeta fidelizado");
			if(datosFidelizado.getSaldoTotal().compareTo(BigDecimal.ZERO)>0) {
				inicializarVentanaCanjeoPuntos();
			} else {
				log.warn("canjearPuntos() - No dispone de puntos");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Fidelizado no dispone de puntos"),this.getStage());
			}
		} else if (!ticketManager.isTicketVacio() && numTarjetaFidelizado == null) {
			log.warn("canjearPuntos() - Debe insertar tarjeta fidelizado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe insertar tarjeta fidelizado"),this.getStage());
		} else if (ticketManager.isTicketVacio() && numTarjetaFidelizado != null) {
			log.warn("canjearPuntos() - Ticket vacio");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo"),this.getStage());
		} else {
			log.warn("canjearPuntos() - Ticket vacio y debe insertar tarjeta fidelizado");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo y debe insertar tarjeta fidelizado"),this.getStage());
		}
	}

	protected void inicializarVentanaCanjeoPuntos() {
		realizarProcesosSaldoEntradaPantalla();

		updateRemoteCoupons();

		ticketManager.recalcularConPromociones();
		guardarCopiaSeguridad();
		refrescarDatosPantalla();
	}

	private void realizarProcesosSaldoEntradaPantalla() {
		CabeceraTicket cabecera = (CabeceraTicket) ticketManager.getTicket().getCabecera();
		FidelizacionBean datosFidelizado = cabecera.getDatosFidelizado();
		if (datosFidelizado != null) {
			if (datosFidelizado.getSaldoTotal().compareTo(BigDecimal.ZERO) > 0) {
				realizaPagoPuntos(datosFidelizado);
			}
		}
	}

	private void realizaPagoPuntos(FidelizacionBean datosFidelizado) {
		log.debug("realizaPagoPuntos() - Procede a canjear los puntos");
		BigDecimal puntosPorEuro = BigDecimal.valueOf(Double.valueOf(
				variablesServices.getVariableAsString(VariablesServices.FIDELIZACION_PUNTOS_FACTOR_CONVERSION)));
		
		BigDecimal saldoFidelizado = datosFidelizado.getSaldoTotal();
		BigDecimal precioVenta = ticketManager.getTicket().getTotales().getTotal();
		BigDecimal dineroDispEnPuntos = saldoFidelizado.multiply(puntosPorEuro).setScale(2, RoundingMode.DOWN);
		
		BigDecimal puntosMaximosCompra = BigDecimal.ZERO;
		if(precioVenta.compareTo(dineroDispEnPuntos)>0) {
			puntosMaximosCompra = saldoFidelizado;
		} else {
			puntosMaximosCompra = precioVenta.divide(puntosPorEuro, 0, RoundingMode.DOWN);
		}
		
		getDatos().put(CanjeoPuntosController.PUNTOS_DISPONIBLES, datosFidelizado.getSaldoTotal());
		getDatos().put(CanjeoPuntosController.PUNTOS_MAXIMOS, puntosMaximosCompra);
		getDatos().put(CanjeoPuntosController.VARIABLE_FIDELIZADO, datosFidelizado);
		getApplication().getMainView().showModalCentered(CanjeoPuntosView.class, getDatos(), this.getStage());
		getDatos().remove(CanjeoPuntosController.VARIABLE_FIDELIZADO);
	}
	
	public void updateRemoteCoupons() {
		log.debug("updateRemoteCoupons() - Actualizando cupones y fidelizado de manera remota");
		FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		
		// se actualiza el fidelizado con el número de tarjeta actual
		tfCodigoIntro.setText(fidelizado.getNumTarjetaFidelizado());
		nuevoCodigoArticulo();
	}
	
	public void labelArticle(String uidEtiqueta) {
		LineaTicketGui selectedLine = getLineaSeleccionada();
		if (selectedLine != null) {
			if (selectedLine.isCupon()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), this.getStage());
			}
			else {
				int lineId = selectedLine.getIdLinea();
				ILineaTicket line = ticketManager.getTicket().getLinea(lineId);
				if (line.isEditable()) {
					ticketManager.toggleArticleLabel(lineId, uidEtiqueta);
					escribirLineaEnVisor((LineaTicket) line);
					guardarCopiaSeguridad();
					refrescarDatosPantalla();
					visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
				}
				else {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea no se puede modificar."), this.getStage());
				}
			}
		}
	}

	public void abrirGestionTickets(HashMap<String, String> parametros) {
		if (parametros.containsKey("idAccion")) {
			String idAccion = parametros.get("idAccion");
			if (getDatos() == null) {
				this.datos = new HashMap<String, Object>();
			}
			getDatos().put(GestionticketsController.PARAMETRO_ENTRADA_TIPO_DOC, Documentos.FACTURA_SIMPLIFICADA);
			POSApplication.getInstance().getMainView().showActionView(Long.parseLong(idAccion), getDatos());
		}
		else {
			log.error("No llegó la acción asociada al botón.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se pudo ejecutar la acción seleccionada."), this.getStage());
		}
	}

	public void recargarGiftcard() {
		log.info("recargarGiftcard()");

		final LineaTicket lineaTicket = (LineaTicket) ticketManager.getTicket().getLineas().get(0);
		
    	HashMap<String, Object> parametros= new HashMap<>();     
    	parametros.put(CodigoTarjetaController.PARAMETRO_IN_TEXTOCABECERA, I18N.getTexto("Lea o escriba el código de barras de la tarjeta de regalo"));
    	parametros.put(CodigoTarjetaController.PARAMETRO_TIPO_TARJETA, "GIFTCARD");
    	
    	POSApplication posApplication = POSApplication.getInstance();
    	posApplication.getMainView().showModalCentered(CodigoTarjetaView.class, parametros, getStage());
        String numTarjeta = (String)parametros.get(CodigoTarjetaController.PARAMETRO_NUM_TARJETA);
        
        BackgroundTask<GiftCardBean> task = new BackgroundTask<GiftCardBean>(){
        	@Override
        	protected GiftCardBean call() throws Exception {
        		return giftCardService.getGiftCard(numTarjeta);
        	}
        	
        	@Override
        	protected void succeeded() {
        		super.succeeded();
        		
        		GiftCardBean tarjeta = getValue();
        		
        		HashMap<String, Object> parametros = new HashMap<>();

				parametros.clear();
				parametros.put(SaldoTarjetaRegaloController.PARAMETRO_ENTRADA_TARJETA, tarjeta);
				parametros.put(SaldoTarjetaRegaloController.PARAMETRO_ENTRADA_RECARGA, lineaTicket.getImporteTotalConDto());
				parametros.put(SaldoTarjetaRegaloController.PARAMETRO_ENTRADA_RECARGA_VARIABLE, lineaTicket.getArticulo().getGenerico());
				getApplication().getMainView().showModalCentered(SaldoTarjetaRegaloView.class, parametros, getStage());
				BigDecimal saldoRecarga = (BigDecimal) parametros.get(SaldoTarjetaRegaloController.PARAMETRO_SALIDA_RECARGA);
				if (saldoRecarga != null && BigDecimalUtil.isMayorACero(saldoRecarga)) {
					tarjeta.setImporteRecarga(saldoRecarga);
					SesionImpuestos sesionImpuestos = sesion.getImpuestos();
					BigDecimal precioSinDto = sesionImpuestos.getPrecioSinImpuestos(lineaTicket.getCodImpuesto(), saldoRecarga, lineaTicket.getCabecera().getCliente().getIdTratImpuestos());
					lineaTicket.setPrecioSinDto(precioSinDto);
					lineaTicket.setPrecioTotalSinDto(saldoRecarga);
					lineaTicket.recalcularImporteFinal();
					ticketManager.getTicket().getCabecera().agnadirTarjetaRegalo(tarjeta);
					ticketManager.setEsRecargaTarjetaRegalo(true);
					ticketManager.getTicket().getTotales().recalcular();

					abrirPagos();
				}
				else {
					if (saldoRecarga != null && !BigDecimalUtil.isMayorACero(saldoRecarga)) {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El importe de recarga debe ser mayor que 0"), getStage());
					}
					try {
						ticketManager.eliminarTicketCompleto();
					}
					catch (Exception e) {
						log.error("recargarGiftcard() - Error while ticket is deleting: " + e.getMessage(), e);
					}
					refrescarDatosPantalla();
				}
        	}
        	
        	@Override
        	protected void failed() {
        		super.failed();
        		
				try {
					ticketManager.eliminarTicketCompleto();
					refrescarDatosPantalla();
					initializeFocus();
					tbLineas.getSelectionModel().clearSelection();

					visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
					visor.modoEspera();
				}
				catch (Exception e) {
					log.error("recargarGiftcard() - Error inicializando nuevo ticket: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), e);
				}
        	}
		};
		
		task.start();
		try {
			task.get();
		}
		catch (Exception e) {
			log.error("recargarGiftcard() - Error executing task: " + e.getMessage(), e);
		}

	}

	public void consultarSaldoGiftCard() {
		log.info("consultarSaldoGiftCard()");
		
		HashMap<String, Object> parametros= new HashMap<>();     
    	parametros.put(CodigoTarjetaController.PARAMETRO_IN_TEXTOCABECERA, I18N.getTexto("Lea o escriba el código de barras de la tarjeta de regalo"));
    	parametros.put(CodigoTarjetaController.PARAMETRO_TIPO_TARJETA, "GIFTCARD");
    	
    	POSApplication posApplication = POSApplication.getInstance();
    	posApplication.getMainView().showModalCentered(CodigoTarjetaView.class, parametros, getStage());
        String numTarjeta = (String)parametros.get(CodigoTarjetaController.PARAMETRO_NUM_TARJETA);
		
        if(numTarjeta != null) {        	
        	BackgroundTask<GiftCardBean> task = new BackgroundTask<GiftCardBean>(){
        		@Override
        		protected GiftCardBean call() throws Exception {
        			return giftCardService.getGiftCard(numTarjeta);
        		}
        		
        		@Override
        		protected void succeeded() {
        			super.succeeded();
        			
        			HashMap<String, Object> parametros = new HashMap<>();
        			parametros.clear();
        			parametros.put(InfoTarjetaRegaloController.PARAMETRO_ENTRADA_TARJETA, getValue());
        			getApplication().getMainView().showModalCentered(InfoTarjetaRegaloView.class, parametros, getStage());
        		}
        		
        		@Override
        		protected void failed() {
        			super.failed();
        			
        			try {
        				refrescarDatosPantalla();
        				initializeFocus();
        				
        			}
        			catch (Exception e) {
        				log.error("recargarGiftcard() - Error refrescando pantalla: " + e.getMessage(), e);
        				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), e);
        			}
        		}
        	};
        	
        	task.start();
        	try {
        		task.get();
        	}
        	catch (Exception e) {
        		log.error("recargarGiftcard() - Error executing task: " + e.getMessage(), e);
        	}
        }
	}

	public void fidelizacion() {
		log.debug("fidelizacion()");
		Dispositivos.getInstance().getFidelizacion().pedirTarjetaFidelizado(getStage(), new DispositivoCallback<FidelizacionBean>(){

			@Override
			public void onSuccess(FidelizacionBean tarjeta) {
				if (tarjeta.isBaja()) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
					tarjeta = null;
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				}
				else {
					// Tarjeta válida - lo seteamos en el ticket
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				}

				ticketManager.recalcularConPromociones();
				guardarCopiaSeguridad();
				refrescarDatosPantalla();
			}

			@Override
			public void onFailure(Throwable e) {
				// Los errores se muestran desde el código del dispositivo
				// Quitamos los datos de fidelizado
				FidelizacionBean tarjeta = null;
				ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				guardarCopiaSeguridad();
				ticketManager.recalcularConPromociones();
				refrescarDatosPantalla();
			}

		});

	}

	public void facturarDocumentoVentaCredito() {
		HashMap<String, Object> parametros = new HashMap<>();
		parametros.put(TICKET_KEY, ticketManager);

		getApplication().getMainView().showModalCentered(FacturarVentaCreditoView.class, parametros, this.getStage());

		refrescarDatosPantalla();
	}

	public void generarTicketsAleatorios() {
		Random rand = new Random();
		final int MAX_TICKETS = 50;
		final int MAX_LINEAS = 30;
		final int MAX_CANTIDAD = 3;

		// int maxTickets = rand.nextInt(MAX_TICKETS) + 1;
		int maxLineas = rand.nextInt(MAX_LINEAS) + 1;

		List<ArticuloCodBarraBean> listaArticulos = null;
		try {
			listaArticulos = articulosService.consultarCodigoBarras();
		}
		catch (ArticulosServiceException e) {
			log.debug("generarTicketsAleatorios() - Error consultando el listado de articulos: ", e);
		}
		int totalArt = listaArticulos.size();

		int count = 0;
		generarTicketAleatorio(MAX_TICKETS, count, rand, MAX_CANTIDAD, maxLineas, listaArticulos, totalArt);
	}

	protected void generarTicketAleatorio(final int MAX_TICKETS, final int count, final Random rand, final int MAX_CANTIDAD, final int maxLineas, final List<ArticuloCodBarraBean> listaArticulos,
	        final int totalArt) {
		int linea;
		for (linea = 0; linea < maxLineas; linea++) { // Número de líneas por cada ticket

			BigDecimal cantidad = new BigDecimal(rand.nextInt(MAX_CANTIDAD) + 1);

			int posicion = rand.nextInt(totalArt);
			String codBarra = listaArticulos.get(posicion).getCodigoBarras();

			try {
				insertarLineaVenta(codBarra, null, null, cantidad);
			}
			catch (Exception e) {
				log.debug("generarTicketsAleatorios() - Añadiendo linea al ticketManager: ", e);
			}
			ticketManager.recalcularConPromociones();
			refrescarDatosPantalla();
		}

		ticketManager.nuevaLineaPago(MediosPagosService.medioPagoDefecto.getCodMedioPago(), ticketManager.getTicket().getTotales().getTotalAPagar(), true, true, null, true);

		ticketManager.salvarTicket(getStage(), new SalvarTicketCallback(){

			@Override
			public void onSucceeded() {
				ticketManager.finalizarTicket();
				try {
					crearNuevoTicket();
				}
				catch (PromocionesServiceException | DocumentoException  e) {
					log.debug("generarTicketsAleatorios() - Generando nuevo ticket de venta: ", e);
				}
				refrescarDatosPantalla();

				if (count < MAX_TICKETS) {
					generarTicketAleatorio(MAX_TICKETS, count + 1, rand, MAX_CANTIDAD, maxLineas, listaArticulos, totalArt);
				}
			}

			@Override
			public void onFailure(Exception e) {
				log.debug("generarTicketsAleatorios() - Error  ", e);
			}
		});
	}

	public synchronized LineaTicket insertarLineaVenta(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad) throws LineaTicketException, PromocionesServiceException,
	        DocumentoException, CajasServiceException, CajaRetiradaEfectivoException {
		if (ticketManager.getTicket().getLineas().isEmpty()) {
			// Es la primera linea así que llamamos a nuevoTicket()
			log.debug("insertarLineaVenta() - Se inserta la primera línea al ticket por lo que inicializamos nuevo ticket");

			ClienteBean cliente = ticketManager.getTicket().getCliente();
			FidelizacionBean tarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			crearNuevoTicket();
			ticketManager.getTicket().setCliente(cliente);
			ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
		}
		LineaTicket linea = ticketManager.nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, nCantidad, getStage(), null, false);

		if (!validarLinea(linea)) {
			ticketManager.eliminarLineaArticulo(linea.getIdLinea());
		}

		guardarCopiaSeguridad();

		return linea;
	}

	protected boolean validarLinea(LineaTicket linea) {
		return true;
	}

	protected void comprobarLineaPrecioCero(LineaTicket linea) throws LineaInsertadaNoPermitidaException {
		Boolean permiteVentaPrecioCero = variablesServices.getVariableAsBoolean(VariablesServices.TPV_PERMITIR_VENTA_PRECIO_CERO, true);
		// Comprobamos tarifa es distinta de null
		if (linea.getTarifa().getVersion() == null) { // Si la versión es null, es cero y no viene de BD
			log.debug("comprobarLineaPrecioCero() - La versión de la tarifa de la linea es null");
			if (permiteVentaPrecioCero) {
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n"
				        + I18N.getTexto("¿Desea vender el artículo a precio 0?"), getStage());
				if (!vender) {
					throw new LineaInsertadaNoPermitidaException(linea);
				}
				else {
					return;
				}
			}
			else {
				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("El artículo \"{0} - {1}\" no está tarificado.", linea.getCodArticulo(), linea.getDesArticulo()) + "\n"
				        + I18N.getTexto("No está permitida la venta a precio 0."), linea);
			}
		}

		// Comprobamos precio cero
		if (BigDecimalUtil.isIgualACero(linea.getPrecioTotalSinDto()) && !linea.getGenerico()) {
			if (permiteVentaPrecioCero) {
				boolean vender = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea vender el artículo a precio 0?"), getStage());
				if (!vender) {
					throw new LineaInsertadaNoPermitidaException(linea);
				}
				else {
					return;
				}
			}
			else {
				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No está permitida la venta a precio 0."), linea);
			}
		}
	}

	protected void comprobarArticuloGenerico(LineaTicket linea) throws LineaInsertadaNoPermitidaException {
		// Si el artículo es genérico y no tiene permiso, no se puede insertar
		if (linea.getGenerico()) {
			try {
				compruebaPermisos(PERMISO_USAR_GENERICOS);
			}
			catch (SinPermisosException e) {
				throw new LineaInsertadaNoPermitidaException(I18N.getTexto("No tiene permisos para usar articulos genéricos"), linea);
			}
		}
	}

	public static class LineaInsertadaNoPermitidaException extends Exception {

		private static final long serialVersionUID = 1L;
		private LineaTicket linea;

		public LineaInsertadaNoPermitidaException(LineaTicket linea) {
			super();
			this.linea = linea;
		}

		public LineaInsertadaNoPermitidaException(String msg, LineaTicket linea) {
			super(msg);
			this.linea = linea;
		}

		public LineaTicket getLinea() {
			return linea;
		}
	}

	protected void guardarCopiaSeguridad() {
		this.ticketManager.guardarCopiaSeguridadTicket();
	}

	@SuppressWarnings("unchecked")
	protected void asignarNumerosSerie(LineaTicket linea) {
		if (linea != null && linea.getArticulo().getNumerosSerie()) {
			getDatos().put(NumerosSerieController.PARAMETRO_NUMEROS_SERIE_DOCUMENTO_ORIGEN, new ArrayList<String>());
			getDatos().put(NumerosSerieController.PARAMETRO_LINEA_DOCUMENTO_ACTIVO, linea);
			getApplication().getMainView().showModalCentered(NumerosSerieView.class, getDatos(), getStage());
			List<String> numerosSerie = (List<String>) getDatos().get(NumerosSerieController.PARAMETRO_LISTA_NUMEROS_SERIES_ASIGNADOS);
			linea.setNumerosSerie(numerosSerie);
			ticketManager.guardarCopiaSeguridadTicket();
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean validarNumerosSerie() {
		boolean valido = true;

		for (LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			ArticuloBean articulo = linea.getArticulo();

			// Si no es válido no hacemos las comprobaciones para los números de serie
			// Si quedan números de serie por asignar, mostramos la pantalla de números de serie
			if (quedanNumerosSeriePorAsignar(linea, articulo.getNumerosSerie())) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Quedan números de serie por asignar. Por favor, asígnelos antes de seguir."), getStage());

				asignarNumerosSerie(linea);

				/*
				 * if(linea.getNumerosSerie() != null && linea.getNumerosSerie().isEmpty()) { return false; }
				 */
				valido = false;
			}
		}

		return valido;
	}

	protected boolean quedanNumerosSeriePorAsignar(LineaTicket linea, boolean usaNumerosSerie) {
		return usaNumerosSerie && (linea.getNumerosSerie() == null || linea.getNumerosSerie().size() < linea.getCantidad().abs().intValue());
	}

	public void obtenerPeso() {
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable(){

			IBalanza balanza = Dispositivos.getInstance().getBalanza();

			@Override
			public void run() {
				try {
					BigDecimal pesoNuevo = BigDecimal.valueOf(balanza.getPeso(BigDecimal.ZERO));
					if (!BigDecimalUtil.isIgual(peso, pesoNuevo)) {
						peso = pesoNuevo;
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								tfPesoIntro.setText(FormatUtil.getInstance().formateaNumero(peso, 3));
							}
						});
					}
				}
				catch (Exception e) {
					scheduledThreadPoolExecutor = null;
					log.error("ScheduledThreadPoolExecutor() - Error al solicitar el peso.", e);
				}
			}
		}, 0, AppConfig.milisegundosPeticionPeso, TimeUnit.MILLISECONDS);
	}

	protected void obtenerCantidadTotal() {
		TicketVentaAbono ticket = (TicketVentaAbono) ticketManager.getTicket();
		BigDecimal cantidad = ticket.getCantidadTotal();
		lbTotalCantidad.setText(FormatUtil.getInstance().formateaNumero(cantidad.abs()));
	}

	@FXML
	public void accionVerFidelizado() {
		if (ticketManager.getTicket().getCabecera().getDatosFidelizado() != null) {
			String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			
			final String numeroTarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado();
			Long idFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado().getIdFidelizado();
			if (idFidelizado != null) {
				Map<String, Object> datos = new HashMap<String, Object>();
				datos.put(CodigoTarjetaController.PARAMETRO_ID_FIDELIZADO, idFidelizado);
				datos.put(CodigoTarjetaController.PARAMETRO_MODO, "CONSULTA");
				datos.put(TICKET_KEY,ticketManager);
				datos.put(PARAMETRO_NUMERO_TARJETA, numeroTarjeta);
				getApplication().getMainView().showActionView(AppConfig.accionFidelizado, (HashMap<String, Object>) datos);
				return;
			}
			ConsultarFidelizadoRequestRest consulta = new ConsultarFidelizadoRequestRest(apiKey, uidActividad, numeroTarjeta);

			ConsultarIdFidelizadoTask consultarFidelizadoTask = SpringContext.getBean(ConsultarIdFidelizadoTask.class, consulta, new RestBackgroundTask.FailedCallback<Long>(){

				@Override
				public void succeeded(Long result) {
					Map<String, Object> datos = new HashMap<String, Object>();
					datos.put(CodigoTarjetaController.PARAMETRO_ID_FIDELIZADO, result);
					datos.put(CodigoTarjetaController.PARAMETRO_MODO, "CONSULTA");
					datos.put(PARAMETRO_NUMERO_TARJETA, numeroTarjeta);
					getApplication().getMainView().showActionView(AppConfig.accionFidelizado, (HashMap<String, Object>) datos);
				}

				@Override
				public void failed(Throwable throwable) {
				}
			}, getStage());

			consultarFidelizadoTask.start();
		}
	}

	@SuppressWarnings("unchecked")
    public void abrirSeleccionManual() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(SeleccionarArticuloPanelesController.PARAM_TICKET_MANAGER, ticketManager);
		
		// uncomment when it's neccesary to show previous added items
		// params.put(SeleccionarArticuloPanelesController.PARAM_LINES, lineas);
		
		getApplication().getMainView().showModal(SeleccionarArticuloPanelesView.class, params);

		// uncomment when it's neccesary to delete previous added items
		// if(params.containsKey(SeleccionarArticuloPanelesController.PARAM_LINES)) {
		// 	params.remove(SeleccionarArticuloPanelesController.PARAM_LINES);
		// }
		
		List<String> codarts = (List<String>) params.get(SeleccionarArticuloPanelesController.PARAM_ARTICULOS_INSERTADOS);
		if (codarts != null) {			
			for (String codart : codarts) {
				tfCodigoIntro.setText(codart);
				nuevoCodigoArticulo();
				try {
					Thread.sleep(50L);
				}
				catch (InterruptedException e) {}
			}
		}
		
		refrescarDatosPantalla();
	}
	
	@SuppressWarnings("unchecked")
	public void seeCustomerCoupons() {
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			List<CustomerCouponDTO> availableCoupons = ticketManager.getTicket().getCabecera().getDatosFidelizado().getAvailableCoupons();
			params.put(CustomerCouponsController.PARAM_CUSTOMER_COUPONS, availableCoupons);
			List<CustomerCouponDTO> activeCoupons = ticketManager.getTicket().getCabecera().getDatosFidelizado().getActiveCoupons();
			params.put(CustomerCouponsController.PARAM_ACTIVE_COUPONS, activeCoupons);
			
			getApplication().getMainView().showModal(CustomerCouponsView.class, params, getStage());
			
			List<CustomerCouponDTO> coupons = (List<CustomerCouponDTO>) params.get(CustomerCouponsController.PARAM_CUSTOMER_COUPONS);
			ticketManager.getTicket().getCabecera().getDatosFidelizado().setActiveCoupons(coupons);
		}
		catch (Exception e) {
			log.error("seeCustomerCoupons() - ERROR: " + e.getMessage(), e);
			
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al mostrar la pantalla de cupones del fidelizado. Por favor, contacte con el administrador."), e);
		}
	}

	protected void useCustomerCoupons() {
		FidelizacionBean customerData = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		
		if(customerData != null && customerData.getAvailableCoupons() != null && !customerData.getAvailableCoupons().isEmpty()) {
			seeCustomerCoupons();
			
			DocumentoPromocionable ticket = (DocumentoPromocionable) ticketManager.getTicket();
			CouponsApplicationResultDTO result = sesion.getSesionPromociones().applyCoupons(customerData.getActiveCoupons(), ticket, false);
			
			ticketManager.getTicket().getCabecera().getTotales().recalcular();
			
			if(result != null && result.getAppliedCoupons() != null && !result.getAppliedCoupons().isEmpty()) {
				int appliedCoupons = result.getAppliedCoupons().size();
				String message = "";
				if(appliedCoupons == 1) {					
					message = I18N.getTexto("Se ha podido aplicar un cupón.", appliedCoupons);
				}
				else {
					message = I18N.getTexto("Se han podido aplicar {0} cupones.", appliedCoupons);
				}
				
				VentanaDialogoComponent.crearVentanaInfo(null, message, getStage());
			}
		}
	}
	

	public void viewLastSale(){
		log.trace("viewLastSale()");
		String activityUid = sesion.getAplicacion().getUidActividad();
		TipoDocumentoBean activeDocument = ticketManager.getDocumentoActivo();
		String storeCode = sesion.getAplicacion().getCodAlmacen();
		String tillCode = sesion.getSesionCaja().getCajaAbierta().getCodCaja();
		HashMap<String, Object> ticketData = new HashMap<String, Object>();
		//Realizamos la consulta
		TicketBean ticket = null;
		try {
			ConfigContadorBean counterConfig = countersConfigService.consultar(ticketManager.getDocumentoActivo().getIdContador());
			Map<String, String> counterParams = new HashMap<>();
            
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_CODEMP,sesion.getAplicacion().getEmpresa().getCodEmpresa());
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_CODALM,storeCode);
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_CODSERIE,storeCode);
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_CODCAJA,tillCode);
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_CODDOC,activeDocument.getCodtipodocumento());
			counterParams.put(ConfigContadorParametroBean.PARAMETRO_PERIODO,((new Fecha()).getAño().toString()));
			
			ContadorBean counter = countersService.consultarContador(counterConfig, null, counterParams, activityUid);
			TicketExample example = new TicketExample();
            TicketExample.Criteria criteria = example.createCriteria();
            criteria.andUidActividadEqualTo(activityUid);
            criteria.andIdTicketEqualTo(counter.getValor());
            criteria.andIdTipoDocumentoEqualTo(activeDocument.getIdTipoDocumento());
            criteria.andCodAlmacenEqualTo(storeCode);
            criteria.andCodcajaEqualTo(tillCode);
            example.setOrderByClause("FECHA DESC");
			ticket = ticketsService.consultarTicket(example);
			if(ticket == null) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha podido encontrar ningún ticket"), getStage());
				
			}else {
			//Abrimos la pantalla de Detalle Gestion
				GestionTicketGui ticketLine = new GestionTicketGui(ticket);
				ObservableList<GestionTicketGui> tickets = FXCollections.observableList(new ArrayList<GestionTicketGui>());
				tickets.add(ticketLine);
				
				ticketData.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKETS, tickets);        
				if(ticketLine.getTicketXML()!=null){
					 ticketData.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKET_XML, ticketLine.getTicketXML());
		        }		
				 
				getApplication().getMainView().showModalCentered(DetalleGestionTicketView.class, ticketData, getStage());
			}
		} catch (Exception e) {
			log.error("viewLastSale() - Error: Se ha producido un error recuperando el último ticket", e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha ocurrido un error consultando el último ticket emitido"), e);
		}
		
	}

}
