package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos;

import com.comerzzia.api.loyalty.client.CouponsApi;
import com.comerzzia.api.loyalty.client.model.CouponDetail;
import com.comerzzia.api.loyalty.client.model.CouponLink;
import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.fidelizados.FidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.core.model.clases.parametros.valoresobjetos.ValorParametroObjeto;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.api.errorhandlers.ApiClientException;
import com.comerzzia.core.servicios.clases.parametros.valoresobjeto.ValoresParametrosObjetosService;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.core.util.numeros.Numero;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ContextoValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostal;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalException;
import com.comerzzia.iskaypet.librerias.utils.validadores.tiposcodigopostal.ValidadorCodigoPostalFactory;
import com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion;
import com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaView;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.canjeospuntos.ArticlesPointsManager;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaDto;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketController;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.MotivoAuditoriaTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.auditoria.ticket.motivos.CargarMotivosController;
import com.comerzzia.iskaypet.pos.gui.ventas.cajas.IskaypetCajasConceptosEnum;
import com.comerzzia.iskaypet.pos.gui.ventas.plataformadigital.DeliveryManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.contrato.ContratoAnimalView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.descuentogeneral.DescuentoGeneralController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.descuentogeneral.DescuentoGeneralView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion.IskaypetEdicionArticuloController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion.puntos.EdicionArticuloPuntosController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.edicion.puntos.EdicionArticuloPuntosView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.inyectables.InyectableView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.AsignarLoteView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.lotes.LoteArticuloManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.previsualizacion.PrevisualizacionTicketController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.previsualizacion.PrevisualizacionTicketView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.promociones.IskaypetPromocionesAplicablesController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.trazabilidad.seleccion.AsignarTrazabilidadView;
import com.comerzzia.iskaypet.pos.persistence.fidelizacion.IskaypetCustomerCouponDTO;
import com.comerzzia.iskaypet.pos.persistence.promotionsticker.PromotionalStickerXML;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.DetailPets;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriaLineaTicket;
import com.comerzzia.iskaypet.pos.services.auditorias.AuditoriasService;
import com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService;
import com.comerzzia.iskaypet.pos.services.core.sesion.IskaypetSesionPromociones;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.ProformaRestService;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaDTO;
import com.comerzzia.iskaypet.pos.services.proformas.rest.classes.ProformaHeaderDTO;
import com.comerzzia.iskaypet.pos.services.promociones.tipos.especificos.IskaypetPromocionNxMDetalles;
import com.comerzzia.iskaypet.pos.services.promotionsticker.PromotionStickerService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.articulos.ArticuloPromoCominadoYPackService;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.TicketAparcadoXmlBean;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.canjeoPuntos.ArticlesPointsXMLBean;
import com.comerzzia.iskaypet.pos.util.formatter.IskaypetFormatter;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.simboloCargando.VentanaCargando;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.fidelizacion.Fidelizacion;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoView;
import com.comerzzia.pos.gui.ventas.cajas.apuntes.InsertarApunteView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FormularioLineaArticuloBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.coupons.CustomerCouponsView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketView;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.CustomerCouponDTO;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajaRetiradaEfectivoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.cajas.conceptos.CajaConceptosServices;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.sesion.coupons.types.CouponTypeDTO;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.cupones.CuponAplicationException;
import com.comerzzia.pos.services.cupones.CuponUseException;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionNxMDetalles;
import com.comerzzia.pos.services.promociones.tipos.especificos.PromocionPackBean;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.DetallePromocionNxM;
import com.comerzzia.pos.services.promociones.tipos.especificos.detalles.LineaDetallePromocionNxM;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_PROFORMAS_SELECCION;
import static com.comerzzia.iskaypet.pos.devices.proformas.seleccion.SeleccionProformaController.PARAM_PROFORMA_SELECCIONADA;
import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.apuntes.IskaypetInsertarApunteController.*;
import static com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.IskaypetPagosController.ACCION_BUSQUEDA_CLIENTE;
import static com.comerzzia.iskaypet.pos.services.core.fidelizacion.exception.FidelizacionService.convertApiToPos;
import static com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO;
import static com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController.PARAMETRO_ENTRADA_CLIENTE;
import static com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController.ACCION_CANCELAR;

/**
 * GAP46 - CANJEO ARTÍCULOS POR PUNTOS
 * GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1
 * GAP 12 - ISK-8 GESTIÓN DE LOTES
 * GAP 107 - ISK-262 GLOVO (venta por plataforma digital)
 * GAPXX - SOLUCIÓN ERROR PANTALLA CUPONES
 */
@SuppressWarnings("all")
@Component
@Primary
public class IskaypetFacturacionArticulosController extends FacturacionArticulosController {

	public static final String LINEA_ENVIADA = "LINEA_ENVIADA";
	public static final String FIDELIZADO_CONTRATO = "FIDELIZADO_CONTRATO";

	public static final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";
	public static final String TIPO_DOCUMENTO_ENVIADO = "TIPO_DOCUMENTO_ENVIADO";

	public static final String MOSTRAR_PANTALLA_PROMO = "PROMO.MOSTRAR_APLICABLES";
	public static final String PROMO_APLICABLES_CONSULTADAS = "PROMO_APLICABLES_CONSULTADAS";
	public static final String VARIABLE_MOSTRAR_PANTALLA_PROMO_3 = "POS.X_MENSAJE_REGALO";
	public static final String VARIABLE_MOSTRAR_PANTALLA_PROMO_3_DEF = "N";
	public static final String ESTILO_ETIQUETAS_PROMOCIONES = "lbPromociones"; // ISK-110
	public static final String X_POS_CANJEO_PUNTOS_VISIBLE = "X_POS.CANJEO_PUNTOS_VISIBLE"; // GAP46 - CANJEO ARTÍCULOS POR PUNTOS
	public static final String X_POS_CONTRATO_VISIBLE = "X_POS.CONTRATO_VISIBLE"; // GAP46 - CANJEO ARTÍCULOS POR PUNTOS
	public static final String PERMITE_BUSCADOR_GENERICO_FIDELIZACION = "X_POS.PERMITE_BUSQUEDA_GENERAL_FIDELIZACION";
	public static final String TIPO_ESTABLECIMIENTO_CLINICA = "CLINICA";
	public static final String VENTA_MEDICAMENTO_SIN_SERVICIO_ASOCIADO = "X_POS.VENTA_MEDICAMENTO_SIN_SERVICIO_ASOCIADO";

	public static final String PARAMETRO_MODAL = "modal";

	public static final String COD_COLOR_LINE_ANIMAL = "-fx-background-color: #EAACF7;";

	public static final String CLASS_CELL_RENDERER_RESALTED = "cell-renderer-resalted";

	public static final String PARAM_LISTA_AUDIT_EDICION_PRECIO = "listaAuditoriasEdicionPrecio";

	public static final String ACCION_PREVISUALIZACION = "PREVISUALIZACION";

	public static final String PARAM_DETAILS_PETS = "PARAM_DETAILS_PETS";



	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	protected PromocionesService promocionService;

	@Autowired
	protected CajaConceptosServices cajaConceptosServices;

	protected MotivoAuditoriaDto motivo;

	@Autowired
	private IskaypetSesionPromociones sesionPromociones;

	@Autowired
	private AuditoriasService auditoriasService;

	@Autowired
	private ArticulosService articulosService;

	@Autowired
	private CajasService cajasService;

	@Autowired
	private IskaypetTicketService iskaypetTicketService;

	@Autowired
	private IskaypetEvicertiaService evicertiaService;

	@Autowired
	private LoteArticuloManager loteArticuloManager;

	@Autowired
	private InyectableArticuloManager inyectableArticuloManager;

	// ISK-262 GAP-107 GLOVO (venta por plataforma digital)
	@Autowired
	private DeliveryManager deliveryManager;

	@Autowired
	private Sesion sesion;

	@Autowired
	private ArticuloPromoCominadoYPackService artPromoCombiPackService;
	protected HashMap<String, String> mapArtDesPromoCombiCondiones;
	protected HashMap<String, String> mapArtDesPromoPackAplicacion;

	@Autowired
	private FidelizacionService fidelizacionService;

	@Autowired
	private ProformaRestService proformaRestService;

	@FXML
	public HBox panelPromocionesNoFidelizado;

	@FXML
	public HBox panelPromocionesFidelizado;

	@FXML
	public Label lbDescuentoGeneral;
	@FXML
	public Label lbColectivosFidelizado;

	@FXML
	public Label lbColectivos;

	@FXML
	protected TableColumn<LineaTicketGui, String> tcLineasLote;

	@FXML
	protected ImageView imgCancelar;

	protected ClienteBean clienteBusqueda;

	private BigDecimal descuento;

	private FidelizadoBean fidelizado;

	private VentanaCargando ventana;

	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;

	//GAP 172 TRAZABILIDAD ANIMALES
	@Autowired
	protected TrazabilidadMascotasService trazabilidadMascotasService;

	@Autowired
	protected ValoresParametrosObjetosService valoresParametrosObjetosService;

	@Autowired
	protected LineasTicketServices lineasTicketServices;

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		Callback<TableView<LineaTicketGui>, TableRow<LineaTicketGui>> callback = new Callback<TableView<LineaTicketGui>, TableRow<LineaTicketGui>>() {

			@Override
			public TableRow<LineaTicketGui> call(TableView<LineaTicketGui> p) {
				return new TableRow<LineaTicketGui>() {

					@Override
					protected void updateItem(LineaTicketGui linea, boolean empty) {
						super.updateItem(linea, empty);

						if (linea != null) {
							ILineaTicket line = ticketManager.getTicket().getLinea(linea.getIdLinea());
							if (((IskaypetTicketManager) ticketManager).isFidelizadoSeleccionado() && linea instanceof IskaypetLineaTicketGui && ((IskaypetLineaTicketGui) linea).getMascota()) {
								IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) line;
								if (iskLinea.getContratoAnimal() != null && iskLinea.isRequiereContrato()) {
									setStyle(COD_COLOR_LINE_ANIMAL);
									// B2B2B2
								} else {
									setStyle("-fx-background-color: #f68989;");
									// DD5353 a33b2a
								}
							}
							if (linea.isCupon()) {
								if (!getStyleClass().contains("cell-renderer-cupon")) {
									getStyleClass().add("cell-renderer-cupon");
								}
							} else if (linea.isLineaDocAjeno()) {
								if (!getStyleClass().contains("cell-renderer-lineaDocAjeno")) {
									getStyleClass().add("cell-renderer-lineaDocAjeno");
								}
							} else {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
								getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							}

							// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
							if (line instanceof IskaypetLineaTicket) {
								IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) line;
								if (articlePointsManager.isLineWithArticlePoints((TicketVentaAbono) ticketManager.getTicket(), iskLinea) != null) {
									if (articlePointsManager.isLineWithArticlePointsUsed((TicketVentaAbono) ticketManager.getTicket(), iskLinea.getIdLinea()) != null) {
										setStyle(ArticlesPointsManager.COD_COLOR_LINE_SELECTED);
									} else {
										setStyle(ArticlesPointsManager.COD_COLOR_LINE);
									}
								}
							}
						} else {
							getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
							getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							setStyle("");
						}
					}

				};
			}
		};

		tbLineas.setRowFactory(callback);

		tbLineas.getSelectionModel().selectedItemProperty().addListener((arg0, itemAntiguo, itemNuevo) -> {
			if (itemNuevo != null) {
				BotonBotoneraComponent boton = botoneraAccionesTabla.getBotonBotonera("CONTRATO");
				if (boton != null && boton instanceof BotonBotoneraSimpleComponent) {
					boton.setDisable(!(itemNuevo instanceof IskaypetLineaTicketGui) || !((IskaypetLineaTicketGui) itemNuevo).getMascota());
				}
				lineaSeleccionadaChanged();
			}
		});

		// GAP 12 - ISK-8 GESTIÓN DE LOTES - Columna de lotes
		tcLineasLote.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLineas", "tcLineasLote", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcLineasLote.setCellValueFactory(cdf -> {
			if (cdf.getValue() instanceof IskaypetLineaTicketGui) {
				return ((IskaypetLineaTicketGui) cdf.getValue()).getLoteProperty();
			}
			return new SimpleStringProperty("");
		});

		tcLineasPVP.setCellFactory(column -> new TableCell<LineaTicketGui, BigDecimal>() {

			@Override
			protected void updateItem(BigDecimal item, boolean empty) {
				super.updateItem(item, empty);
				setAlignment(Pos.CENTER_RIGHT);
				if (empty || item == null) {
					setText(null);
					setStyle("");
					getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
				} else {
					setText(FormatUtil.getInstance().formateaNumero(item, 2));
					setStyle("-fx-padding: 0px 8px 0px 0px;");
					IskaypetLineaTicket linea = getLineaFromTablRow(ticketManager, getTableRow());
					if (linea != null) {
						// Si se ha modificaco el precio (descuento manual), se resalta la celda
						if (!BigDecimalUtil.isIgual(linea.getPrecioTotalTarifaOrigen(), linea.getPrecioTotalSinDto())) {
							getStyleClass().add(CLASS_CELL_RENDERER_RESALTED);
						} else {
							getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
						}
					}
				}
			}
		});

		tcLineasDescuento.setCellFactory(column -> new TableCell<LineaTicketGui, BigDecimal>() {

			@Override
			protected void updateItem(BigDecimal item, boolean empty) {
				super.updateItem(item, empty);
				setAlignment(Pos.CENTER_RIGHT);
				if (empty || item == null) {
					setText(null);
					setStyle("");
					getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
				} else {
					setText(FormatUtil.getInstance().formateaNumero(item, 2) + " %");
					setStyle("-fx-padding: 0px 8px 0px 0px;");
					IskaypetLineaTicket linea = getLineaFromTablRow(ticketManager, getTableRow());
					if (linea != null) {
						// Si el descuento es mayor que 0, se resalta la celda
						if (BigDecimalUtil.isMayorACero(linea.getDescuento())) {
							getStyleClass().add(CLASS_CELL_RENDERER_RESALTED);
						} else {
							getStyleClass().removeAll(Collections.singleton(CLASS_CELL_RENDERER_RESALTED));
						}
					}
				}
			}
		});

	}

	public static IskaypetLineaTicket getLineaFromTablRow(TicketManager ticketManager, TableRow<LineaTicketGui> fila) {
		if (fila != null) {
			LineaTicketGui lineaGui = fila.getItem();
			if (lineaGui != null) {
				return (IskaypetLineaTicket) ticketManager.getTicket().getLinea(lineaGui.getIdLinea());
			}
		}
		return null;
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
				String tipoEstablecimiento = getTipoEstablecimiento();
				String panelBotoneraPath = (StringUtils.isNotBlank(tipoEstablecimiento) ? String.join("_", "",tipoEstablecimiento, "panel.xml") : "_panel.xml");
				PanelBotoneraBean panelBotoneraBean = getView().loadBotonera(panelBotoneraPath);
				botonera = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
				panelBotonera.getChildren().add(botonera);
			} catch (InitializeGuiException e) {
				log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
			}

			// Si la botonera no se ha creado, la creamos con el panel por defecto
			if (botonera == null) {
				try {
					PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
					botonera = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
					panelBotonera.getChildren().add(botonera);
				} catch (InitializeGuiException e) {
					log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
				}
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

			deliveryManager.inicializarValores();
		} catch (CargarPantallaException | SesionInitException | TicketsServiceException | DocumentoException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de venta de artículos");
			VentanaDialogoComponent.crearVentanaError("Error cargando pantalla. Para mas información consulte el log.", getStage());
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		clienteBusqueda = (ClienteBean) getDatos().get(PARAMETRO_ENTRADA_CLIENTE);
		if (clienteBusqueda == null) {
			clienteBusqueda = sesion.getAplicacion().getTienda().getCliente();
		}

		limpiarPanelPromociones();

		motivo = new MotivoAuditoriaDto();

		try {
			((IskaypetTicketManager) ticketManager).comprobarGeneracionATCUD();
		} catch (Exception e) {
			throw new InitializeGuiException(e);
		}

		tfCantidadIntro.setTextFormatter(IskaypetFormatter.getIntegerFormat());

	}

/* ANTIRIOR AL GAP 172 TRAZABILIDAD ANIMALES
 * 	protected boolean validarLinea(LineaTicket linea) {

		if (linea != null && ((IskaypetLineaTicket) linea).isMascota()) {
			if (hayFidelizadoSeleccionado()) {
				//Comprobar si requiere trazabilidad y contrato


				return true;
			}
			else {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Debe tener un cliente asociado para poder realizar ventas de mascotas"), getStage());
				return false;
			}
		}
		else {
			return true;
		}

	}
*/

	@Override
	protected LineaTicketGui createLineaGui(LineaTicket lineaTicket) {
		return new IskaypetLineaTicketGui(lineaTicket);
	}

	@Override
	public void refrescarDatosPantalla() {
		limpiarPanelPromociones();
		super.refrescarDatosPantalla();

		// Valores por defecto
		log.debug("refrescarDatosPantalla() - Limpiando datos de colectivos");
		lbColectivos.setVisible(false);
		lbColectivosFidelizado.setVisible(false);
		lbColectivosFidelizado.setText("");

		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();

		if(fidelizado == null || datosFidelizado == null || ticketManager.isEsDevolucion()) {
			imgCancelar.setVisible(false);
			imgCancelar.setCursor(null);
		}

		if (datosFidelizado != null) {
			log.debug("refrescarDatosPantalla() - Cargando datos de fidelizado desde el ticket");
			// Si hay datos de fidelizado en el ticket y no hay datos de fidelizado en la sesión, los cargamos
			if (fidelizado == null) {
				log.debug("refrescarDatosPantalla() - Cargando datos de fidelizado desde el ticket");
				cargarFidelizado(datosFidelizado.getIdFidelizado());
			}

			if (fidelizado != null && datosFidelizado.getNombre() != null
					&& ticketManager instanceof IskaypetTicketManager && !((IskaypetTicketManager) ticketManager).esTicketProforma()
					&& !((IskaypetTicketManager) ticketManager).tieneMedicamentosConRecetaLineas((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas())
					&& !((IskaypetTicketManager) ticketManager).esMascotaLinea((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas())
			) {
				imgCancelar.setVisible(true);
				imgCancelar.setCursor(Cursor.HAND);
			} else {
				imgCancelar.setVisible(false);
				imgCancelar.setCursor(null);
			}

			if (fidelizado != null && !CollectionUtils.isEmpty(fidelizado.getColectivos())) {
				log.debug("refrescarDatosPantalla() - Cargando datos de colectivos desde el fidelizado");
				lbColectivos.setVisible(true);
				lbColectivosFidelizado.setVisible(true);
				lbColectivosFidelizado.setText(fidelizado.getColectivos().stream().map(ColectivosFidelizadoBean::getDesColectivo).collect(Collectors.joining(", ")));
			} else if (!CollectionUtils.isEmpty(datosFidelizado.getCodColectivos())) {
				log.debug("refrescarDatosPantalla() - Cargando datos de colectivos desde el ticket");
				lbColectivos.setVisible(true);
				lbColectivosFidelizado.setVisible(true);
				lbColectivosFidelizado.setText(String.join(", ", datosFidelizado.getCodColectivos()));
			} else {
				log.debug("refrescarDatosPantalla() - No hay colectivos asociados al fidelizado");
				lbColectivosFidelizado.setText("");
				lbColectivos.setVisible(false);
				lbColectivosFidelizado.setVisible(false);
			}

		}

		completarPanel();
	}

	@Override
	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
		log.debug("comprobarAperturaPantalla()");
		if (!sesion.getSesionCaja().isCajaAbierta()) {
			if (!variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true)) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
				throw new InitializeGuiException(false);
			}
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
			sesion.getSesionCaja().abrirCajaAutomatica();
		}

		if (!ticketManager.comprobarCierreCajaDiarioObligatorio()) {
			boolean hayTicketsAparcados = true;
			try {
				hayTicketsAparcados = !ticketManager.recuperarTicketsAparcados(ticketManager.getNuevoDocumentoActivo().getIdTipoDocumento()).isEmpty();
			} catch (Exception e) {
				log.error("comprobarAperturaPantalla() - No se ha podido comprobar si hay tickets aparcados: " + e.getMessage(), e);
			}

			if ((ticketManager.getTicket() == null || ticketManager.getTicket().getLineas().size() <= 0) && !hayTicketsAparcados) {
				String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
				String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
				VentanaDialogoComponent.crearVentanaError(
						I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", new Object[]{fechaCaja, fechaActual}), getStage());
				throw new InitializeGuiException(false);
			}
		}

		if (!checkBloqueoRetirada()) {
			checkAvisoRetirada();
		}
	}


	//CZZ-490
	@Override
	public void recuperarTicket() {
		log.trace("recuperarTicket()");
		if (ticketManager.isTicketVacio()) { // Solo si el ticket esta vacÃ­o, podemos recuperar el ticket
			try {
				getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
				getDatos().put(RecuperarTicketController.PARAMETRO_TIPO_DOCUMENTO, ticketManager.getNuevoDocumentoActivo().getIdTipoDocumento());
				getApplication().getMainView().showModalCentered(RecuperarTicketView.class, getDatos(), this.getStage());
			} catch (Exception e) {
				log.error("recuperarTicket() - Ha habido un error al abrir la pantalla de tickets aparcados : " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}

			try {
				getView().loadAndInitialize();
				visor.escribir(I18N.getTexto("TOTAL A PAGAR"), ticketManager.getTicket().getTotales().getTotalAsString());
				visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
				guardarCopiaSeguridad();
			}
			catch (InitializeGuiException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
			// CZZ-490 - Cuando recupera ticket, si tiene contrato animal se abre la pantalla de Evicertia
			// pero sin salir la pantalla de envío y carga.
			boolean requiereEvicertia =
			    (!ticketManager.isTicketVacio()
			        && evicertiaService.tieneContratoAnimalLineas((IskaypetTicketVentaAbono) ticketManager.getTicket()))
			    || Boolean.TRUE.equals(getDatos().get(PagosController.ACCION_CANCELAR));

			while (requiereEvicertia) {
			    if (!gestionarOperacionEvicertia(false)) {
			        return;
			    }
			    // Volvemos a evaluar si hay que repetir por ACCION_CANCELAR
			    requiereEvicertia = Boolean.TRUE.equals(getDatos().get(PagosController.ACCION_CANCELAR));
			}
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no está vacío."), this.getScene().getWindow());
		}
	}


	public void abrirFormularioContrato() {
		log.debug("abrirFormularioContrato()");

		if (!ticketManager.getTicket().getLineas().isEmpty() && ((IskaypetLineaTicketGui) getLineaSeleccionada()).getMascota()) {

			FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();

			if (fidelizado == null) {
				log.error("abrirFormularioContrato() - No hay fidelizado asociado al ticket.");
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha encontrado el fidelizado"), getStage());
				return;
			}

			// Antes de mostrar el contrato, es obligatorio verificar los datos del fidelizado
			String msgDatosFidelizadoError = validarDatosFidelizadoContrato(fidelizado);
			if (StringUtils.isNotBlank(msgDatosFidelizadoError)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Faltan datos del fidelizado, debe completarlos antes de continuar: " + msgDatosFidelizadoError), getStage());
				return;
			}

			getDatos().put(LINEA_ENVIADA, ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea()));
			getDatos().put(FIDELIZADO_CONTRATO, fidelizado);

			getApplication().getMainView().showModalCentered(ContratoAnimalView.class, getDatos(), getStage());

			refrescarDatosPantalla();
		} else {
			if (ticketManager.getTicket().getLineas().isEmpty()) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No puede hacer contratos de un ticket vacio"), getStage());
				return;
			}
			if (!((IskaypetLineaTicketGui) getLineaSeleccionada()).mascota) {
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Solo puede realizar contratos a mascotas"), getStage());
				return;
			}
		}

	}

	@Override
	public void abrirPagos() {
		log.debug("abrirPagos() - Abriendo pagos...");
		if (!insertandoLinea) {
			if (!ticketManager.isTicketVacio()) {
				List<IskaypetLineaTicket> lineas = (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas();

				String tipoEstablecimiento = getTipoEstablecimiento();
				if (StringUtils.isNotBlank(tipoEstablecimiento) && TIPO_ESTABLECIMIENTO_CLINICA.equalsIgnoreCase(tipoEstablecimiento)
						&& ticketManager instanceof  IskaypetTicketManager) {
					boolean tieneServicios = ((IskaypetTicketManager) ticketManager).tieneServiciosLineas(lineas);
					boolean tieneMedicamentos = ((IskaypetTicketManager) ticketManager).tieneMedicamentosLineas(lineas);
					boolean permiteVentaSinMedicamentos = variablesServices.getVariableAsBoolean(VENTA_MEDICAMENTO_SIN_SERVICIO_ASOCIADO, true);
					if (tieneMedicamentos && !tieneServicios && !permiteVentaSinMedicamentos) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede realizar la venta de medicamentos sin servicios asociados."), getStage());
						return;
					}
				}

				log.debug("abrirPagos() - Comprobando si es factura completa para validar datos de facturacion, documento activo: " + ticketManager.getDocumentoActivo().getCodtipodocumento());
				if (ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA) && ticketManager instanceof IskaypetTicketManager) {
					if (!((IskaypetTicketManager) ticketManager).validarDatosFidelizadoFactura(ticketManager.getTicket().getCabecera().getDatosFidelizado(), getStage())) {
						log.error("abrirPagos() - No se puede abrir pagos, datos de facturacion imcompletos");
						return;
					}
					log.debug("abrirPagos() - Datos de facturacion validados");
					((IskaypetTicketManager) ticketManager).guardarDatosFacturacion(ticketManager.getTicket().getCabecera().getDatosFidelizado());
					log.debug("abrirPagos() - Datos de facturacion guardados");
				}

				// GAP 12 - ISK-8 GESTIÓN DE LOTES
				// Comprobar si hay medicamentos sin lote asignado
				List<IskaypetLineaTicket> lineasMedicamentosSinLote;
				try {
					lineasMedicamentosSinLote = loteArticuloManager.devuelveLineasSinLote((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas(),
							ticketManager.getTicket().getCabecera().getDatosFidelizado(), false);
				} catch (Exception e) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error comprobando medicamentos."), getStage());
					return;
				}

				if (!lineasMedicamentosSinLote.isEmpty()) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay medicamentos sin lote asignado."), getStage());

					// Agrupamos los medicamentos sin lote por codigo de articulo
					HashMap<String, List<IskaypetLineaTicket>> mapMedicamentosSinLote = new HashMap<>();
					for (IskaypetLineaTicket lineaMedicamentoSinLote : lineasMedicamentosSinLote) {
						mapMedicamentosSinLote
								.computeIfAbsent(lineaMedicamentoSinLote.getCodArticulo(), k -> new ArrayList<>())
								.add(lineaMedicamentoSinLote);
					}

					// Recorremos la inserción de lotes agrupados por codigo de articulo
					for (Map.Entry<String, List<IskaypetLineaTicket>> entry : mapMedicamentosSinLote.entrySet()) {
						getDatos().put(AsignarLoteController.CLAVE_PARAMETRO_LISTA_LINEAS, entry.getValue());
						getDatos().put(AsignarLoteController.CLAVE_TICKET_MANAGER, ticketManager);
						getApplication().getMainView().showModalCentered(AsignarLoteView.class, getDatos(), this.getStage());

						Boolean seHaCanceladoSeleccionLotes = (Boolean) getDatos().get(AsignarLoteController.CLAVE_SE_HA_CANCELADO_PANTALLA);
						if (seHaCanceladoSeleccionLotes != null && seHaCanceladoSeleccionLotes) {
							refrescarDatosPantalla();
							return;
						}

						// Después de asignar lotes, refrescamos pantalla
						refrescarDatosPantalla();
					}

				}
				// fin GAP 12 - ISK-8 GESTIÓN DE LOTES

				// Se comprueba si hay inyectables sin asignar y se procede a asignar
				boolean procesado = procesarLineasInyectable();
				if (!procesado) {
					log.debug("abrirPagos() - Se ha cancelado la asignación de inyectables");
					return;
				}

				//GAP 172 TRAZABILIDAD ANIMALES
				List<IskaypetLineaTicket> lineasAnimalSinTrazabilidad = trazabilidadMascotasService.getLineasRequiereTrazabilidadNoAsignadas((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas());
				if (!lineasAnimalSinTrazabilidad.isEmpty()) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay animales sin identificación asignada."), getStage());
					getDatos().put(AsignarTrazabilidadController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasAnimalSinTrazabilidad);
					getDatos().put(AsignarTrazabilidadController.CLAVE_TICKET_MANAGER, ticketManager);
					getApplication().getMainView().showModalCentered(AsignarTrazabilidadView.class, getDatos(), this.getStage());

					if (getDatos().containsKey(AsignarTrazabilidadController.CLAVE_PARAMETRO_CANCELAR)) {
						return;
					}
				}

				boolean allLinesPointsRedeemed = true;

				for (IskaypetLineaTicket linea : lineas) {
					if (linea.isMascota() && linea.getContratoAnimal() == null && linea.isRequiereContrato()) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay mascotas sin contrato asociado."), getStage());
						return;
					}
					if (linea.getArticlePoints() == null || linea.getArticlePoints().getReedem().equals(ArticlesPointsXMLBean.VALUE_REEDEM_KO)) {
						allLinesPointsRedeemed = false;
					}
				}

				log.debug("abrirPagos() - Todos los articulos de puntos redimidos:  " + (allLinesPointsRedeemed ? "Si" : "No") + ", Total ticket : "
						+ ticketManager.getTicket().getTotales().getTotalAsString() + "...");

				// ISK-247 No se puede finalizar la venta si el total del ticket es 0 y todos los artículos han sido canjeados por
				// puntos
				if (BigDecimalUtil.isMenorOrIgualACero(ticketManager.getTicket().getTotales().getTotal()) && allLinesPointsRedeemed) {
					VentanaDialogoComponent.crearVentanaAviso(
							I18N.getTexto("La venta contiene únicamente artículos canjeados por puntos, por lo que el importe total debe ser mayor que 0 para finalizar la operación"), getStage());
					return;
				}

				// Si el boton cancelar de la pantalla de promociones es pulsado, no deja avanzar
				Boolean accionCancelar = (Boolean) getDatos().get(IskaypetPromocionesAplicablesController.ACCION_CANCELAR_PROMOS);
				if (accionCancelar != null && accionCancelar) {
					return;
				}

			}
		}

		// Se muestra la pantalla de previsualización de ticket y se comprueba si se ha cancelado
		if (!previsualizacion()) {
			return;
		}



		//CZZ-490
		if (!gestionarOperacionEvicertia(true)) return;


		// Si se ha cancelado el pago y se requiere busqueda de fidelizado
		Boolean esCancelar = (Boolean) getDatos().getOrDefault(ACCION_CANCELAR, false);
		Boolean esBusquedaCliente = (Boolean) getDatos().getOrDefault(ACCION_BUSQUEDA_CLIENTE, false);
		if (esCancelar && esBusquedaCliente) {
			fidelizacion();
		}
		if(esCancelar) {
			if(evicertiaService.tieneContratoAnimalLineas((IskaypetTicketVentaAbono) ticketManager.getTicket())) {
				if (!gestionarOperacionEvicertia(false)) return;
			}
		}

	}

	/**
	 *  CZZ-490 -  Gestionar las acciones de la pantalla de Resumen de operaciones de Evicertia, Salir, reenviar, aparcar...
	 * @param ventanaProgreso
	 * @return
	 */
	private boolean gestionarOperacionEvicertia(boolean ventanaProgreso) {
		log.debug("gestionarOperacionEvicertia()");
	    do {
	    	//Si el proceso viene tras aparcar ticket (ventanaProgreso a false) y se pulsa reenviar, se tiene que cambiar para que envíe.
	    	if(!ventanaProgreso && IskaypetEvicertiaService.OPERACION_REENVIAR.equals(getDatos().get(IskaypetEvicertiaService.OPERACION))){
	    		ventanaProgreso=true;
	    	}

	        evicertiaService.openEvicertiaView(ventanaProgreso,(TicketVentaAbono) ticketManager.getTicket(), getStage(),getDatos(),getApplication().getMainView());
	    } while (IskaypetEvicertiaService.OPERACION_REENVIAR.equals(getDatos().get(IskaypetEvicertiaService.OPERACION)));

	    if (IskaypetEvicertiaService.OPERACION_SALIR.equals(getDatos().get(IskaypetEvicertiaService.OPERACION))) {
	        return false;
	    }

	    if (IskaypetEvicertiaService.OPERACION_APARCAR.equals(getDatos().get(IskaypetEvicertiaService.OPERACION))) {
	        aparcarTicket();
	        return false;
	    }

		// CZZ-490 Estándar pero sin pantalla de cupones de fidelizados
		abrirPagosSinCupones();
	    return true;

	}

	/**
	 * CZZ-490 - Mismo método de estándar ocultando que al abrirPagos se abra la pantalla de cupones
	 */
	public void abrirPagosSinCupones() {
		log.trace("abrirPagosSinCupones()");
		if (insertandoLinea) {
			return;
		}
		if (!ticketManager.isTicketVacio()) {
			if (validarNumerosSerie()) {
				log.debug("abrirPagosSinCupones() - El ticket tiene lineas");

				Dispositivos.getInstance().getFidelizacion().ignorarTarjetaFidelizado();

				//CZZ-490
//				if(sesionPromociones.isLoadedLoyaltyModule()) {
//					useCustomerCoupons();
//				}

				showTextPromotions();

				getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
				abrirVentanaPagos();
				initializeFocus();

				if (!getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
					try {
						crearNuevoTicket();
					} catch (Exception e) {
						log.error("abrirPagosSinCupones() - Excepción al inicializar ticket : " + e.getCause(), e);
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
			log.warn("abrirPagosSinCupones() - Ticket vacio");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene lineas de articulo."), this.getStage());
		}
	}


	@Override
	public void cancelarVenta() {
		log.debug("cancelarVenta()");
		try {
			boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar todas las líneas del ticket?"), getStage());
			if (!confirmacion) {
				return;
			}

			if (!ticketManager.getTicket().getLineas().isEmpty()) {
				log.debug("cancelarVenta() - Procendiendo a anular la venta del ticket con contenido");

				try {
					HashMap<String, Object> datos = new HashMap<>();
					datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
					AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
				} catch (InitializeGuiException e) {
					if (e.isMostrarError()) {
						log.error("accionAceptar() - Ha habido un error al autorizar el cambio de precio: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al autorizar el cambio de precio. Contacte con un administrador."), e);
					}
					return;
				}

				getDatos().put(TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_ANULACION_ORDEN_COMPLETA);
				getDatos().put(LINEA_ENVIADA, ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea()));
				getDatos().put(MotivoAuditoriaTicketController.ACTIVAR_CANCELAR, Boolean.TRUE);

				getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, getDatos(), getStage());

				// Si se ha cancelado la auditoria se cancela la cancelación de ticket
				if ((Boolean) getDatos().getOrDefault(MotivoAuditoriaTicketController.CANCELAR, false)) {
					return;
				}

				motivo = (MotivoAuditoriaDto) getDatos().get(MotivoAuditoriaTicketController.MOTIVO);

				crearAuditoriaCancelarVenta(AuditoriasService.TIPO_AUDITORIA_ANULACION_ORDEN_COMPLETA);
			}

			if (ticketManager.getTicket() != null) {
				for (IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
					if (pago.isEliminable()) {
						log.error("cancelarVenta() - Se ha intentado cancelar una venta para la que existen pagos eliminables asociados.");
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede cancelar una venta con pagos asociados."), getStage());
						return;
					}
				}
			}

//			if (ticketManager.getTicket().getIdTicket() != null) {
//				ticketManager.salvarTicketVacio();
//			}
			ticketManager.eliminarTicketCompleto();

			// Restauramos la cantidad en la pantalla
			tfCantidadIntro.setText(String.valueOf(1));

			if(fidelizado != null) {
                ticketManager.getTicket().getCabecera().setDatosFidelizado((String) null);
                fidelizado = null;
			}

			refrescarDatosPantalla();
			initializeFocus();
			tbLineas.getSelectionModel().clearSelection();

			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();
		} catch (TicketsServiceException | PromocionesServiceException | DocumentoException ex) {
			log.error("accionAnularTicket() - Error inicializando nuevo ticket: " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageI18N(), ex);
		}
    }

	private void crearAuditoriaCancelarVenta(String tipoDoc) {
		AuditoriaDto auditoria = new AuditoriaDto();
		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());

		log.debug("crearAuditoriaCancelarVenta() - Guardando auditoria");

		try {
			// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
			// Se añade el ticket para esta audiotria, para el resto será null, ya van en las lineas.
			auditoriasService.generarAuditoria(auditoria, tipoDoc, ticketManager, Boolean.TRUE);
		} catch (ContadorServiceException e) {
			log.error("crearAuditoriaCancelarVenta() - error al insertar la auditoria" + e.getMessage(), e);
		} catch (EmpresaException e) {
			log.error("crearAuditoriaCancelarVenta() - Empresa no encontrada " + e.getMessage(), e);

		} catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaCancelarVenta() - Tipo de documento no encontrado " + e.getMessage(), e);

		} catch (DocumentoException e) {
			log.error("crearAuditoriaCambioPrecio() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
	}

	public void crearAuditoriaNegarRegistro(IskaypetLineaTicket linea, String tipoDoc) {
		AuditoriaDto auditoria = setearDatosAuditoria(null, null, linea);

		auditoria.setCodMotivo(motivo.getCodigo().toString());
		auditoria.setObservaciones(motivo.getDescripcion());

		try {
			auditoriasService.generarAuditoria(auditoria, tipoDoc, null, Boolean.TRUE);
		} catch (ContadorServiceException e) {
			log.error("crearAuditoriaCancelarVenta() - error al insertar la auditoria" + e.getMessage(), e);
		} catch (EmpresaException e) {
			log.error("crearAuditoriaCancelarVenta() - Empresa no encontrada " + e.getMessage(), e);

		} catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaCancelarVenta() - Tipo de documento no encontrado " + e.getMessage(), e);

		} catch (DocumentoException e) {
			log.error("crearAuditoriaCancelarVenta() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
	}

	public AuditoriaDto setearDatosAuditoria(BigDecimal importeTotalConDtoOriginal, BigDecimal precioConDtoLineaOriginal, IskaypetLineaTicket iskLinea) {
		AuditoriaDto auditoria = new AuditoriaDto();

		auditoria.setCodArticulo(iskLinea.getCodArticulo());
		auditoria.setDesArt(iskLinea.getDesArticulo());
		auditoria.setDesglose1(iskLinea.getDesglose1());
		auditoria.setDesglose2(iskLinea.getDesglose2());

		// Si se trata de una auditoría de cambio de precio o descuento manual, cogemos los precios
		if ((iskLinea.tieneCambioPrecioManual() || iskLinea.tieneDescuentoManual()) && precioConDtoLineaOriginal != null) {
			auditoria.setPrecioInicial(precioConDtoLineaOriginal);
			auditoria.setPrecioFinal(iskLinea.getPrecioTotalSinDto());

		} else { // Si se trata de una auditoría de negación de registro o devolución, cogemos los importes
			if (importeTotalConDtoOriginal == null) {
				auditoria.setPrecioInicial(iskLinea.getImporteTotalConDto());
			} else {
				auditoria.setPrecioInicial(importeTotalConDtoOriginal);
			}
			auditoria.setPrecioFinal(iskLinea.getImporteTotalConDto());
		}

		return auditoria;
	}

	@Override
	@FXML
	protected void accionNegarRegistroTabla() {
		IskaypetLineaTicket iskLinea = (IskaypetLineaTicket) ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea());

		// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
		if (getLineaSeleccionada() != null) {
			if (articlePointsManager.isLineWithArticlePoints((TicketVentaAbono) ticketManager.getTicket(), iskLinea) != null) {
				String msgAviso = I18N.getTexto("No se pueden negar lineas con puntos aplicados.");
				VentanaDialogoComponent.crearVentanaAviso(msgAviso, this.getStage());
				return;
			}
		}

		super.accionNegarRegistroTabla();

		if (iskLinea.getCantidad().intValue() < 0) {
			getDatos().put(IskaypetFacturacionArticulosController.TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
			getDatos().put(IskaypetFacturacionArticulosController.LINEA_ENVIADA, ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea()));

			getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, getDatos(), getStage());
			motivo = (MotivoAuditoriaDto) getDatos().get(CargarMotivosController.MOTIVO);

			crearAuditoriaNegarRegistro(iskLinea, AuditoriasService.TIPO_AUDITORIA_DEVOLUCION);
		}

	}
	// ······························ Se comenta por cambio de funcionalidad ISK-69 ······························//
	// private Boolean mostrarPromosAplicables() {
	// log.info("IskaypetFacturacionArticulosController/abrirPagos() - comprobando si hay que mostrar la pantall de
	// promociones");
	// /* Primero comprobamos la variable que indica si podemos entrar en la pantalla*/
	// boolean mostrarPromoAplicables = false;
	//
	// mostrarPromoAplicables = mostrarPantallaPromo();
	//
	// if(mostrarPromoAplicables){
	// log.info("IskaypetFacturacionArticulosController/abrirPagos() - Recuperando promociones totales y promociones que se
	// van a aplicar");
	// /* PASO 1 : Obtenemos las promociones activas, y las promociones que se van a aplicar */
	// IskaypetSesionPromociones sesionPromo = SpringContext.getBean(IskaypetSesionPromociones.class);
	// Map<Long, Promocion> promocionesActivas = sesionPromo.getPromocionesActivas();
	// Map<Long, Promocion> promocionesActivasCopy = new HashMap<Long, Promocion>();
	//
	// for (Map.Entry<Long, Promocion> entry : promocionesActivas.entrySet()) {
	// promocionesActivasCopy.put(entry.getKey(), entry.getValue());
	// }
	//
	// log.info("IskaypetFacturacionArticulosController/abrirPagos() - Número de Promociones : " +
	// promocionesActivas.size());
	// if(promocionesActivas != null && !promocionesActivas.isEmpty()){
	// filtrarPromocionesAplicables(promocionesActivasCopy);
	// }
	// }
	// return true;
	// }
	//
	// /* Comprueba si existe la variable en BBDD para mostrar la pantalla, Si la variable no se ha encontrado o es una N,
	// no se muestra pantalla de promos aplicables*/
	// private boolean mostrarPantallaPromo() {
	// log.info("mostrarPantallaPromo() - consultando en la BBDD si tenemos que mostrar la pantalla");
	// boolean mostrarPromoAplicables;
	// try{
	// mostrarPromoAplicables = variablesServices.getVariableAsBoolean(MOSTRAR_PANTALLA_PROMO, false);
	// }
	// catch(Exception e){
	// log.error("mostrarPantallaPromo() - No se ha encontrado la variable " + MOSTRAR_PANTALLA_PROMO +" : " +
	// e.getMessage(), e);
	// mostrarPromoAplicables = false;
	// }
	// return mostrarPromoAplicables;
	// }
	//
	// //Comprueba las promociones aplicadas, y las elimina de la lista para no mostrarlas si estan aplicadas
	// @SuppressWarnings("unchecked")
	// private void filtrarPromocionesAplicables(Map<Long, Promocion> promocionesActivasCopy) {
	// List<PromocionTicket> promocionesUsadas = (List<PromocionTicket>) ticketManager.getTicket().getPromociones();
	// log.info("filtrarPromocionesAplicables() - Número de Promociones Aplicadas : " + promocionesUsadas.size());
	//
	// /* PASO 2 : Con los listados de promociones obtenidos, pasamos a restar a la parte activa las usadas */
	// for(PromocionTicket promo : promocionesUsadas){
	// promocionesActivasCopy.remove(promo.getIdPromocion());
	// }
	//
	// if(promocionesActivasCopy != null && !promocionesActivasCopy.isEmpty()){
	// /* PASO 3 : Pasamos las promociones aplicables pero no aplicadas a la pantalla */
	// getDatos().put(PROMO_APLICABLES_CONSULTADAS, promocionesActivasCopy);
	// getApplication().getMainView().showModalCentered(IskaypetPromocionesAplicablesView.class, getDatos(), getStage());
	// }
	//
	// }

	protected void limpiarPanelPromociones() {
		panelPromocionesNoFidelizado.getChildren().clear();
		panelPromocionesFidelizado.getChildren().clear();
	}

	protected void lineaSeleccionadaChanged() {

		LineaTicketGui lineaSeleccionada = tbLineas.getSelectionModel().getSelectedItem();

		limpiarPanelPromociones();

		if (mapArtDesPromoCombiCondiones == null && mapArtDesPromoPackAplicacion == null) {
			mapArtDesPromoCombiCondiones = artPromoCombiPackService.cargarArticulosCombinados();
			mapArtDesPromoPackAplicacion = artPromoCombiPackService.cargarArticulosPack();
		}

		if (lineaSeleccionada != null) {
			try {

				if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery()) {
					log.debug("lineaSeleccionadaChanged() - Venta delivery, no se evalúan promociones");
					return;
				}

				if (!lineaSeleccionada.isCupon()) {
					evaluarPromocion(lineaSeleccionada.getArticulo(), lineaSeleccionada.getDesglose1(), lineaSeleccionada.getDesglose2Property().getValue());
				}
				// Para conservar las medidas y no se esté cambiando de tamaño la tabla
				completarPanel();
			} catch (Exception e) {
				log.error("lineaSeleccionadaChanged() - Ha ocurrido un error consultando las promociones para este artículo: " + e.getMessage(), e);
				//
				// Label lbDatos = construirLabelTipoPromocion(I18N.getTexto("Datos de la Promocion") + ": ");
				// Label lbError = new Label(I18N.getTexto("Error"));
				// FlowPane flowPane = constuirFlowPanePromocion();
				// flowPane.getChildren().add(lbDatos);
				// flowPane.getChildren().add(lbError);
				// panelPromociones.getChildren().add(flowPane);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void evaluarPromocion(String sCodart, String sDesglose1, String sDesglose2) {
		try {
			// GAPXX - SOLUCIÓN ERRORES LINEAS : Guardamos el contador anterior antes de realizar las lineas de promoción.
			Integer countLinesBefore = ((IskaypetTicketManager) ticketManager).getContadorLineas();

			limpiarPanelPromociones();
			((IskaypetTicketManager) ticketManager).inicializarTicketConsultaPromocion();
			TicketVenta ticketAux = (TicketVenta) ((IskaypetTicketManager) ticketManager).getTicketConsultaPromociones();

			// Asignamos el cliente
			clienteBusqueda = clienteBusqueda != null ? clienteBusqueda : sesion.getAplicacion().getTienda().getCliente();
			ticketAux.setCliente(clienteBusqueda);

			// Insertamos el articulo en el ticket con cantidad 50 para las promociones de NxM
			((IskaypetTicketManager) ticketManager).nuevaLineaArticuloConsultaPromocion(sCodart, sDesglose1, sDesglose2, new BigDecimal(50), null, null, false, true);
			((IskaypetTicketManager) ticketManager).recalcularConPromocionesConsulta();
			muestraDatosPromocion(ticketAux, false);

			// Rellenamos los datos de fidelizado para que muestre las promociones de fidelizado
			FidelizacionBean datosFidelizacion = new FidelizacionBean();
			datosFidelizacion.setActiva(true);
			ticketAux.getCabecera().setDatosFidelizado(datosFidelizacion);
			((IskaypetTicketManager) ticketManager).recalcularConPromocionesConsulta();
			ticketManager.recalcularConPromociones();
			muestraDatosPromocion(ticketAux, true);

			// Añadimos paneles vacíos para que la visualización sea siempre la misma, evitando diferentes interlineados entre los
			// paneles de las promociones
			completarPanel();

			// GAPXX - SOLUCIÓN ERRORES LINEAS : Volvemos a setear el valor anterior.
			((IskaypetTicketManager) ticketManager).setContadorLineas(countLinesBefore);
		} catch (PromocionesServiceException | DocumentoException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error creando el ticket de venta."), e);
			log.error("Error creando ticket", ex);
		} catch (LineaTicketException e) {
			LineaTicketException ex = new LineaTicketException(I18N.getTexto("Error insertando línea."), e);
			log.error("Error insertando línea", ex);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void muestraDatosPromocion(TicketVenta ticketAux, boolean mostrandoPromoFidelizado) {
		LineaTicketGui lineaSeleccionada = tbLineas.getSelectionModel().getSelectedItem();
		String codArticulo = lineaSeleccionada.getArticulo();
		// LineaTicket linea = null;
		// for(LineaTicket lineaTicket : (List<LineaTicket>) ticketAux.getLineas()) {
		// if(lineaTicket.getCodArticulo().equals(codArticulo)) {
		// linea = lineaTicket;
		// break;
		// }
		// }
		LineaTicket linea = (LineaTicket) ticketAux.getLineas().get(0);
		Promocion promocion = null;

		boolean crearBanner = false;
		String textoPromocionBanner = null;
		if (linea != null && linea.getPromociones() != null && !linea.getPromociones().isEmpty()) {
			breakBucle:
			for (PromocionLineaTicket promoTicket : linea.getPromociones()) {
				Long idPromo = promoTicket.getIdTipoPromocion();

				switch (idPromo.toString()) {
					case "1":
					case "2":
					case "3":
					case "5":
					case "10":
						promocion = ticketAux.getPromocion(promoTicket.getIdPromocion()).getPromocion();
						break breakBucle;
				}
			}
		}

		// GAP 59.2 MEJORA BANNER DE PROMOCIONES

		if (promocion != null) {
			limpiarPanelPromociones();
			String textoPromocion = promocion.getTextoPromocion();

			// GAP 59: Si no existe el texto promoción , que muestre en el banner la descripción.
			if (StringUtils.isBlank(textoPromocion)) {
				textoPromocion = promocion.getDescripcion();
			}

			if (promocion instanceof PromocionPackBean) {
				textoPromocion = textoPromocion + " PVP Pack: " + BigDecimalUtil.redondear(((PromocionPackBean) promocion).getPrecioPack(), 2);
			}

			if (promocion instanceof PromocionNxMDetalles) {

				// En caso de ser promo NXM se fuerza usar la descripción de la agrupación en la que está el artículo
				for (DetallePromocionNxM detallePromo : ((IskaypetPromocionNxMDetalles) promocion).getListDetallesPromocion()) {

					// En caso de que la agrupación no tenga Texto Promoción pasaremos a la siguiente
					if (detallePromo.getDetalle().getTextoPromocion() == null) {
						continue;
					}

					// Se busca que la agrupación tenga dentro el artículo, y se setea el Texto Promoción de la agrupación
					// como texto del banner
					for (LineaDetallePromocionNxM lineaAgrupacion : detallePromo.getLineasAgrupacion()) {
						if (codArticulo.equals(lineaAgrupacion.getCodArticulo())) {
							textoPromocion = detallePromo.getDetalle().getTextoPromocion();
						}
					}
				}

				// Se completa el texto del banner
				// Esta funcionalidad qeuda comentada por IER - 117
				// List<DetallePromocionNxM> lstDetPromotionNxM = ((IskaypetPromocionNxMDetalles)promocion).getListDetallesPromocion();
				// if(lstDetPromotionNxM.get(0).getTipoDto().equalsIgnoreCase("Descuento")) {
				// textoPromocion = textoPromocion + " DTO: "+ BigDecimalUtil.redondear(lstDetPromotionNxM.get(0).getDescuento(), 2) + "
				// %";
				// }else {
				// textoPromocion = textoPromocion + " PVP DTO: "+ BigDecimalUtil.redondear(lstDetPromotionNxM.get(0).getDescuento(),
				// 2);
				// }
			}

			String labelTipoPromocion = null;
			if (mostrandoPromoFidelizado && ticketAux.getCabecera().getDatosFidelizado() != null && promocion.getSoloFidelizacion()) {
				labelTipoPromocion = " " + I18N.getTexto("Promoción fidelizado:");
			}
			// Comentado desde el GAP 59.2
			// else if (!mostrandoPromoFidelizado && !promocion.getSoloFidelizacion()) {
			// labelTipoPromocion = " " + I18N.getTexto("Promoción NO fidelizado:");
			// }

			if (StringUtils.isNotBlank(labelTipoPromocion) || StringUtils.isNotBlank(textoPromocion)) {
				Label lbTipoPromocion = construirLabelTipoPromocion(labelTipoPromocion);
				Label lbTextoPromocion = new Label(textoPromocion);
				lbTextoPromocion.getStyleClass().add(ESTILO_ETIQUETAS_PROMOCIONES);
				if (textoPromocion.length() > 85) {
					lbTextoPromocion.setPrefWidth(470.0);
				}

				FlowPane flowPane = constuirFlowPanePromocion();
				flowPane.getChildren().add(lbTipoPromocion);
				flowPane.getChildren().add(lbTextoPromocion);
				if (mostrandoPromoFidelizado) {
					panelPromocionesFidelizado.getChildren().add(flowPane);
				} else {
					panelPromocionesNoFidelizado.getChildren().add(flowPane);
				}

				FlowPane flowPaneDetalles = construirFlowPaneDetallePromocion(promocion, linea);
				if (flowPaneDetalles != null) {
					if (mostrandoPromoFidelizado) {
						panelPromocionesFidelizado.getChildren().add(flowPaneDetalles);
					} else {
						panelPromocionesNoFidelizado.getChildren().add(flowPaneDetalles);
					}
				}
			}
		} else {
			boolean soloFidelizado = false;
			if (mapArtDesPromoCombiCondiones != null && !mapArtDesPromoCombiCondiones.isEmpty() || mapArtDesPromoPackAplicacion != null && !mapArtDesPromoPackAplicacion.isEmpty()) {
				if (mapArtDesPromoPackAplicacion.containsKey(linea.getArticulo().getCodArticulo()) || mapArtDesPromoPackAplicacion.containsKey("*" + linea.getArticulo().getCodArticulo())) {
					// Tiene una promocion activa de pack
					textoPromocionBanner = mapArtDesPromoPackAplicacion.get(linea.getArticulo().getCodArticulo());
					if (StringUtils.isBlank(textoPromocionBanner)) {
						textoPromocionBanner = mapArtDesPromoPackAplicacion.get("*" + linea.getArticulo().getCodArticulo());
						soloFidelizado = true;
					}
					crearBanner = true;
				} else if (mapArtDesPromoCombiCondiones.containsKey(linea.getArticulo().getCodArticulo()) || mapArtDesPromoCombiCondiones.containsKey("*" + linea.getArticulo().getCodArticulo())) {
					// Tiene una promocion activa de combinado de linea
					textoPromocionBanner = mapArtDesPromoCombiCondiones.get(linea.getArticulo().getCodArticulo());
					if (StringUtils.isBlank(textoPromocionBanner)) {
						textoPromocionBanner = mapArtDesPromoCombiCondiones.get("*" + linea.getArticulo().getCodArticulo());
						soloFidelizado = true;
					}
					crearBanner = true;
				}
			}

			if (crearBanner) {
				limpiarPanelPromociones();

				// GAP 59: Si no existe el texto promoción , que muestre en el banner la descripción.
				if (StringUtils.isBlank(textoPromocionBanner)) {
					textoPromocionBanner = "No existe descripcion de la promo";
					return;
				}

				String labelTipoPromocion = null;
				if (mostrandoPromoFidelizado && ticketAux.getCabecera().getDatosFidelizado() != null && soloFidelizado) {
					labelTipoPromocion = " " + I18N.getTexto("Promoción fidelizado:");
				}

				if (StringUtils.isNotBlank(labelTipoPromocion) || StringUtils.isNotBlank(textoPromocionBanner)) {
					Label lbTipoPromocion = construirLabelTipoPromocion(labelTipoPromocion);
					Label lbTextoPromocion = new Label(textoPromocionBanner);
					lbTextoPromocion.getStyleClass().add(ESTILO_ETIQUETAS_PROMOCIONES);
					if (textoPromocionBanner.length() > 85) {
						lbTextoPromocion.setPrefWidth(470.0);
					}

					FlowPane flowPane = constuirFlowPanePromocion();
					flowPane.getChildren().add(lbTipoPromocion);
					flowPane.getChildren().add(lbTextoPromocion);
					if (mostrandoPromoFidelizado) {
						panelPromocionesFidelizado.getChildren().add(flowPane);
					} else {
						panelPromocionesNoFidelizado.getChildren().add(flowPane);
					}

					FlowPane flowPaneDetalles = construirFlowPaneDetallePromocion(promocion, linea);
					if (flowPaneDetalles != null) {
						if (mostrandoPromoFidelizado) {
							panelPromocionesFidelizado.getChildren().add(flowPaneDetalles);
						} else {
							panelPromocionesNoFidelizado.getChildren().add(flowPaneDetalles);
						}
					}
				}
			}

		}

	}

	/*
	 * private DetallePromocion getDetalle(Map<PromocionDetalleKey, DetallePromocion> detalles, PromocionDetalleKey key) {
	 * for (PromocionDetalleKey detalleKey : detalles.keySet()) { if
	 * (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && key.getDesglose1().equals(detalleKey.getDesglose1()) &&
	 * key.getDesglose2().equals(detalleKey.getDesglose2())) { return detalles.get(detalleKey); } } for (PromocionDetalleKey
	 * detalleKey : detalles.keySet()) { if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) &&
	 * "*".equals(detalleKey.getDesglose1()) && key.getDesglose2().equals(detalleKey.getDesglose2())) { return
	 * detalles.get(detalleKey); } } for (PromocionDetalleKey detalleKey : detalles.keySet()) { if
	 * (detalleKey.getCodArticulo().equals(key.getCodArticulo()) && key.getDesglose1().equals(detalleKey.getDesglose1()) &&
	 * "*".equals(detalleKey.getDesglose2())) { return detalles.get(detalleKey); } } for (PromocionDetalleKey detalleKey :
	 * detalles.keySet()) { if (detalleKey.getCodArticulo().equals(key.getCodArticulo()) &&
	 * "*".equals(detalleKey.getDesglose1()) && "*".equals(detalleKey.getDesglose2())) { return detalles.get(detalleKey); }
	 * } return null; }
	 */

	protected String getPvpPromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = null;
		if (promocionPresentaPrecio(promocion) || promocionPresentaDescuento(promocion)) {
			pvpPromocion = linea.getPrecioTotalConDtoAsString();
		}
		return pvpPromocion;
	}

	protected boolean promocionPresentaDescuento(Promocion promocion) {
		String presentaDescuentoFinalStr = promocion.getPromocionBean().getTipoPromocion().getConfiguracionMap().get("PresentaDescuentoFinal");
		if ("S".equals(presentaDescuentoFinalStr) || "true".equals(presentaDescuentoFinalStr)) {
			return true;
		}
		return false;
	}

	protected boolean promocionPresentaPrecio(Promocion promocion) {
		String presentaPrecioFinalStr = promocion.getPromocionBean().getTipoPromocion().getConfiguracionMap().get("PresentaPrecioFinal");
		if ("S".equals(presentaPrecioFinalStr) || "true".equals(presentaPrecioFinalStr)) {
			return true;
		}
		return false;
	}

	protected Label construirLabelDetalleDto() {
		return construirLabelDetallePromocion(" " + I18N.getTexto("DTO.") + ": ");
	}

	protected FlowPane construirFlowPaneDetallePromocion(Promocion promocion, LineaTicket linea) {
		String pvpPromocion = null;
		String dtoPromocion = null;
		if (promocion != null) {
			pvpPromocion = getPvpPromocion(promocion, linea);
			dtoPromocion = promocionPresentaDescuento(promocion) ? linea.getDescuento().toString() + "%" : null;
		}

		if (StringUtils.isNotBlank(pvpPromocion) || StringUtils.isNotBlank(dtoPromocion)) {
			FlowPane flowPaneDetalles = constuirFlowPanePromocion();
			Label lbVacio = construirLabelTipoPromocion("");
			flowPaneDetalles.getChildren().add(lbVacio);

			if (StringUtils.isNotBlank(dtoPromocion)) {
				Label lbLabelDto = construirLabelDetalleDto();
				Label lbDto = construirLabelDetalleValorPromocion(dtoPromocion);
				flowPaneDetalles.getChildren().add(lbLabelDto);
				flowPaneDetalles.getChildren().add(lbDto);
			}

			if (StringUtils.isNotBlank(pvpPromocion) && StringUtils.isNotBlank(dtoPromocion)) {
				Label lbVacioIntermedio = new Label("");
				flowPaneDetalles.getChildren().add(lbVacioIntermedio);
			}

			if (StringUtils.isNotBlank(pvpPromocion)) {
				Label lbLabelPvp = constuirLabelDetallePrecio();
				Label lbPvp = construirLabelDetalleValorPromocion(pvpPromocion);
				flowPaneDetalles.getChildren().add(lbLabelPvp);
				flowPaneDetalles.getChildren().add(lbPvp);
			}

			return flowPaneDetalles;
		}

		return null;
	}

	protected Label construirLabelDetalleValorPromocion(String pvpPromocion) {
		Label label = new Label(pvpPromocion);
		label.setAlignment(Pos.BASELINE_LEFT);
		label.getStyleClass().add(ESTILO_ETIQUETAS_PROMOCIONES);
		return label;
	}

	protected Label construirLabelTipoPromocion(String labelTipoPromocion) {
		Label lbTipoPromocion = new Label();
		lbTipoPromocion.setText(labelTipoPromocion);
		lbTipoPromocion.getStyleClass().add(ESTILO_ETIQUETAS_PROMOCIONES);
		lbTipoPromocion.setAlignment(Pos.BASELINE_LEFT);
		return lbTipoPromocion;
	}

	protected Label construirLabelDetallePromocion(String texto) {
		Label label = new Label(texto);
		label.getStyleClass().add(ESTILO_ETIQUETAS_PROMOCIONES);
		label.setAlignment(Pos.BASELINE_LEFT);
		return label;
	}

	protected Label constuirLabelDetallePrecio() {
		return construirLabelDetallePromocion(" " + I18N.getTexto("PVP") + ": ");
	}

	protected FlowPane constuirFlowPanePromocion() {
		FlowPane flowPane = new FlowPane();
		flowPane.setPrefHeight(120.0);
		flowPane.setOrientation(Orientation.VERTICAL);
		flowPane.setHgap(1.0);
		flowPane.setAlignment(Pos.BASELINE_LEFT);
		flowPane.setHgap(0);
		return flowPane;
	}

	protected void completarPanel() {
		int panelesActuales = panelPromocionesNoFidelizado.getChildren().size();
		if (panelesActuales == 0) {
			FlowPane flowPane = constuirFlowPanePromocion();
			flowPane.getChildren().add(new Label(""));
			panelPromocionesNoFidelizado.getChildren().add(flowPane);
		}
		panelesActuales = panelPromocionesFidelizado.getChildren().size();
		if (panelesActuales == 0) {
			FlowPane flowPane = constuirFlowPanePromocion();
			flowPane.getChildren().add(new Label(""));
			panelPromocionesFidelizado.getChildren().add(flowPane);
		}
	}

	public void aplicarDescuentoGeneral() {
		log.info("aplicarDescuentoGeneral() - Acción ejecutada");

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede aplicar descuento general a una proforma"), getStage());
			return;
		}

		if (!ticketManager.isTicketVacio()) {
			try {
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);

				getDatos().put(TIPO_DOCUMENTO_ENVIADO, AuditoriasService.TIPO_AUDITORIA_DESCUENTO_GENERAL);
				getDatos().put(LINEA_ENVIADA, ticketManager.getTicket().getLinea(getLineaSeleccionada().getIdLinea()));
				getDatos().put(MotivoAuditoriaTicketController.ACTIVAR_CANCELAR, Boolean.TRUE);
				getApplication().getMainView().showModalCentered(MotivoAuditoriaTicketView.class, getDatos(), getStage());

				if ((Boolean) getDatos().getOrDefault(MotivoAuditoriaTicketController.CANCELAR, false)) {
					return;
				}

				motivo = (MotivoAuditoriaDto) getDatos().get(MotivoAuditoriaTicketController.MOTIVO);
				getApplication().getMainView().showModalCentered(DescuentoGeneralView.class, getDatos(), getStage());

				descuento = (BigDecimal) getDatos().get(DescuentoGeneralController.PARAMETRO_DESCUENTO);

				if (descuento != null) {
					log.debug("establecerDescuentoLineas() - Establecer descuento a las líneas");
					List<IskaypetLineaTicket> lineas = (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas();
					BigDecimal precioTotalInicial, precioTotalConDescuentoGeneral;
					for (IskaypetLineaTicket linea : lineas) {
						precioTotalInicial = linea.getPrecioTotalSinDto();
						precioTotalConDescuentoGeneral = BigDecimalUtil.menosPorcentajeR(precioTotalInicial, descuento);

						BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(linea.getCodImpuesto(), precioTotalConDescuentoGeneral,
								ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());

						linea.setPrecioSinDto(precioSinImpuestos);
						linea.setPrecioTotalSinDto(precioTotalConDescuentoGeneral);
						linea.recalcularImporteFinal();

						crearAuditoriaDescuentoGeneral(linea, precioTotalConDescuentoGeneral, precioTotalInicial);
					}
					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();
				}
			} catch (InitializeGuiException e) {
				if (e.isMostrarError()) {
					log.error("accionAceptar() - Ha habido un error al autorizar el descuento general: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al autorizar el descuento general. Contacte con un administrador."), e);
				}
			} catch (Exception e) {
				log.error("accionFactura() - Excepción no controlada : " + e.getCause(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error: ") + e.getMessage(), e);
			}
		} else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), getStage());
		}

	}

	@Override
	public boolean checkAvisoRetirada() {
		try {
			if (cajasService.validarImporteAvisoRetirada()) {
				BigDecimal importeRetirar = dineroRetirar();
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha superado el límite de efectivo. Deberá realizar una retirada de ") + importeRetirar + "€", getStage());
				abrirRetiradaEfectivo(importeRetirar, true);
				return true;
			}
		} catch (CajasServiceException e) {
			log.error("checkAvisoRetirada() - Excepción en cajasService : " + e.getCause(), e);
		} catch (InitializeGuiException initializeGuiException) {
			if (initializeGuiException.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
			}
		}

		return false;

	}

	@Override
	public boolean checkBloqueoRetirada() {
		try {
			if (cajasService.validarImporteBloqueoRetirada()) {
				BigDecimal importeRetirar = dineroRetirar();
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Debe realizar una retirada de efectivo, de ") + importeRetirar + I18N.getTexto("€, para poder continuar"), getStage());
				abrirRetiradaEfectivo(importeRetirar, false);
				return true;
			}
		} catch (CajasServiceException cajasServiceException) {
			log.error("checkBloqueoRetirada() Error en cajasService: " + cajasServiceException.getMessage(), cajasServiceException);
		} catch (InitializeGuiException initializeGuiException) {
			if (initializeGuiException.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
			}
		}

		return false;
	}

	@Override
	public void fidelizacion() {
		log.debug("fidelizacion()");

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede modificar el cliente en una proforma"), getStage());
			return;
		}

		if (ticketManager.getTicket().getCabecera().getDatosFidelizado() != null && articlePointsManager.isTicketWithArticlePointsRedeemed(ticketManager.getTicket().getLineas())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Para volver a cargar un cliente debe eliminar del ticket los artículos que han sido canjeados por puntos"), getStage());

		} else if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery() && !ticketManager.isTicketVacio()) {
			// ISK-262 GAP-107 GLOVO - No se permite cambiar de fidelizado en ventas delivery con líneas
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se permite la modificación del cliente en esta operación"), getStage());
		} else {
			if (fidelizacionService.isOfflineMode()) {
				// OFFLINE FLOW
				log.debug("fidelizacion() - offline mode => se abre ventana en modo offline");

				getDatos().remove(PARAMETRO_FIDELIZADO_SELECCIONADO);
				getApplication().getMainView().showModalCentered(BusquedaFidelizadoView.class, getDatos(), getStage());

				// Aseguramos que el objeto es tipo FidelizacionBean
				Object obj = getDatos().get(PARAMETRO_FIDELIZADO_SELECCIONADO);
				FidelizacionBean tarjetaOffline = null;
				if (obj instanceof FidelizacionBean) {
					tarjetaOffline = (FidelizacionBean) obj;
				} else if (obj instanceof FidelizadoBean) {
					tarjetaOffline = convertApiToPos((FidelizadoBean) obj);
					getDatos().put(PARAMETRO_FIDELIZADO_SELECCIONADO, tarjetaOffline);
				}
				if (tarjetaOffline == null) {
					log.debug("fidelizacion() - no se ha seleccionado ningun fidelizado. ignorar");
					return;
				}

				// replicar 'onSuccess' del online flow:
				if (tarjetaOffline.isBaja()) {
					VentanaDialogoComponent.crearVentanaError(
							I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjetaOffline.getNumTarjetaFidelizado()),
							getStage()
					);
					tarjetaOffline = null;
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjetaOffline);
				} else if (deliveryManager.fidelizadoGeneraVentaDelivery(tarjetaOffline)) {
					if (!ticketManager.isTicketVacio()) {
						VentanaDialogoComponent.crearVentanaError(
								I18N.getTexto("No se puede seleccionar este cliente con artículos en la cesta. Para este tipo de cliente debe introducir primero el cliente con la cesta vacía."),
								getStage()
						);
					} else {
						String codColectivoDelivery = deliveryManager.getCodColectivoVentaDelivery(tarjetaOffline);

						((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(codColectivoDelivery);
						((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(false);

						ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjetaOffline);
						cargarFidelizado(tarjetaOffline.getIdFidelizado());
					}
				} else {
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjetaOffline);
					cargarFidelizado(tarjetaOffline.getIdFidelizado());
				}

				if (!deliveryManager.fidelizadoGeneraVentaDelivery(ticketManager.getTicket().getCabecera().getDatosFidelizado())) {
					((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(null);
					((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(true);
				}

				ticketManager.recalcularConPromociones();
				guardarCopiaSeguridad();
				refrescarDatosPantalla();
			} else {
				// ONLINE FLOW
				Dispositivos.getInstance().getFidelizacion().pedirTarjetaFidelizado(getStage(), new DispositivoCallback<FidelizacionBean>() {

					public void onSuccess(FidelizacionBean tarjeta) {
						if (tarjeta.isBaja()) {
							VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
							tarjeta = null;
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
						} else if (deliveryManager.fidelizadoGeneraVentaDelivery(tarjeta)) {
							// ISK-262 GAP-107 GLOVO - Se permite cambiar a cliente delivery siempre que no haya líneas
							if (!ticketManager.isTicketVacio()) {
								VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede seleccionar este cliente con artículos en la cesta. Para este tipo de cliente debe de introducir primero el cliente con la cesta vacía y luego los artículos."), getStage());
							} else {
								String codColectivoDelivery = deliveryManager.getCodColectivoVentaDelivery(tarjeta);

								((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(codColectivoDelivery);
								((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(false);

								if (StringUtils.isNotBlank(tarjeta.getNumTarjetaFidelizado())) {
									tarjeta.setNumTarjetaFidelizado(tarjeta.getNumTarjetaFidelizado());
									log.debug("Copiando el número de tarjeta " + tarjeta.getNumTarjetaFidelizado() + " en el objeto a guardar");
								}

								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
								cargarFidelizado(tarjeta.getIdFidelizado());
							}
						}
						else {
							if (StringUtils.isNotBlank(tarjeta.getNumTarjetaFidelizado())) {
								tarjeta.setNumTarjetaFidelizado(tarjeta.getNumTarjetaFidelizado());
								log.debug("Copiando el número de tarjeta " + tarjeta.getNumTarjetaFidelizado() + " en el objeto a guardar");
							}
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							cargarFidelizado(tarjeta.getIdFidelizado());
						}

						// ISK-262 GAP-107 GLOVO - Se borran datos de delivery si el nuevo cliente asignado no es delivery
						if (!deliveryManager.fidelizadoGeneraVentaDelivery(ticketManager.getTicket().getCabecera().getDatosFidelizado())) {
							((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(null);
							((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(true);
						}

						//guardar numero de tarjeta con su fidelizado
						if (fidelizado != null) {
							if (StringUtils.isNotBlank(tarjeta.getNumTarjetaFidelizado())) {
								fidelizado.setNumeroTarjeta(tarjeta.getNumTarjetaFidelizado());
								log.debug("guardado numero de tarjeta => " + tarjeta.getNumTarjetaFidelizado() + " para fidelizado: " + fidelizado.getCodFidelizado());
								fidelizacionService.guardarFidelizadoOffline(fidelizado, sesion);
							}
						}

						ticketManager.recalcularConPromociones();
						guardarCopiaSeguridad();
						refrescarDatosPantalla();
					}

					public void onFailure(Throwable e) {
						FidelizacionBean tarjeta = null;
						ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
						guardarCopiaSeguridad();
						ticketManager.recalcularConPromociones();
						refrescarDatosPantalla();
					}
				});
			}
		}
	}

	public void cargarFidelizado(Long idFidelizado) {

		FidelizadoBean fidelizadoOrigen = fidelizado;
		fidelizado = null;

		//  offline flow
		if (fidelizacionService.isOfflineMode()) {
			log.debug("cargarFidelizado() - OFFLINE, llamando a DB local con idFidelizado: " + idFidelizado);

			try {
				FidelizadoBean offlineBean;
				if (idFidelizado != null) {
					offlineBean = fidelizacionService.buscarFidelizadoOfflinePorId(idFidelizado);
				} else {
					// Fall back - buscar por numeroTarjeta si id=null
					String numTarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado();
					offlineBean = fidelizacionService.buscarFidelizadoOfflinePorNumeroTarjeta(numTarjeta);
				}

				if (offlineBean != null) {
					FidelizacionBean posBean = convertApiToPos(offlineBean);
					ticketManager.getTicket().getCabecera().setDatosFidelizado(posBean);
					fidelizado = offlineBean;

					//coupons
					if (fidelizadoOrigen != null && !fidelizadoOrigen.getIdFidelizado().equals(fidelizado.getIdFidelizado())
							&& ticketManager.getTicket().getCuponesAplicados() != null
							&& !ticketManager.getTicket().getCuponesAplicados().isEmpty()) {

						List<CuponAplicadoTicket> cupones = ticketManager.getTicket().getCuponesAplicados();
						for (CuponAplicadoTicket cupon : cupones) {
							sesionPromociones.deleteCoupon(cupon.getCodigo(), (DocumentoPromocionable) ticketManager.getTicket());
						}
						ticketManager.recalcularConPromociones();
						refrescarDatosPantalla();
					}
				}
			} catch (Exception e) {
				fidelizado = null;
				log.error("Error cargando fidelizado en offline mode", e);
			}
			return;
		}
		//ONLINE FLOW
		try {
			String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			fidelizado = FidelizadosRest.getFidelizadoPorId(new FidelizadoRequestRest(apiKey, uidActividad, idFidelizado));

			if(fidelizado != null){
				FidelizacionBean headerFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				headerFidelizado.putAdicional(IskaypetEvicertiaService.COD_LENGUAJE, fidelizado.getCodlengua());
				if (StringUtils.isBlank(fidelizado.getNumeroTarjeta())) {
					// obtener numeroTarjeta del ticket header.
					if (headerFidelizado != null && StringUtils.isNotBlank(headerFidelizado.getNumTarjetaFidelizado())) {
						String fallbackNumero = headerFidelizado.getNumTarjetaFidelizado();
						fidelizado.setNumeroTarjeta(fallbackNumero);
						log.debug("cargarFidelizado() - Rellenando número de tarjeta con fallback: " + fallbackNumero);
						fidelizacionService.guardarFidelizadoOffline(fidelizado, sesion);
					} else {
						log.warn("cargarFidelizado() - No se encontró un número de tarjeta de respaldo en el header.");
					}
				}
			}

			// Si el fidelizado es distinto y tenía cupones aplicados, restablecemos y recalculamos
			if (fidelizadoOrigen != null && fidelizado != null && !fidelizadoOrigen.getIdFidelizado().equals(fidelizado.getIdFidelizado())
					&& ticketManager.getTicket().getCuponesAplicados() != null && !ticketManager.getTicket().getCuponesAplicados().isEmpty()) {

				List<CuponAplicadoTicket> lstCuponesAplicados = ticketManager.getTicket().getCuponesAplicados();
				for (CuponAplicadoTicket cuponAplicado : lstCuponesAplicados) {
					sesionPromociones.deleteCoupon(cuponAplicado.getCodigo(), (DocumentoPromocionable) ticketManager.getTicket());
				}

				ticketManager.recalcularConPromociones();
				refrescarDatosPantalla();

			}

		} catch (RestException e) {
			log.error("Error buscando online los datos del fidelizado, tratamos de buscarlo offline");
			fidelizado = fidelizacionService.buscarFidelizadoOfflinePorId(idFidelizado);
		} catch (Exception e) {
			fidelizado = null;
			log.error("Error cargando datos de fidelizado por id", e);
		}
	}

	private BigDecimal dineroRetirar() throws CajasServiceException {
		String CANTIDAD_RETIRADA_EFECTIVO = "X.POS.CANTIDAD_RETIRADA_EFECTIVO";
		return variablesServices.getVariableAsBigDecimal(CANTIDAD_RETIRADA_EFECTIVO).setScale(2, RoundingMode.HALF_UP);
	}

	private void abrirRetiradaEfectivo(BigDecimal importe, Boolean esAviso) throws InitializeGuiException {
		if (BigDecimalUtil.isMayor(importe, BigDecimal.ZERO)) {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, false);
			datos.put(AVISO_RETIRADA, esAviso);
			AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
			super.abrirCajon();
			CajaConceptoBean conceptoBean = cajaConceptosServices.getConceptoCaja(IskaypetCajasConceptosEnum.RETIRADA_EFECTIVO.getCodConcepto());
			if (conceptoBean != null) {
				getDatos().put(CONCEPTO_SELECCIONADO, conceptoBean);
				getDatos().put(TITULO_CALCULADORA, IskaypetCajasConceptosEnum.RETIRADA_EFECTIVO.getDesConcepto());
				getDatos().put(IMPORTE_RETIRAR, importe);
				getDatos().put(AVISO_RETIRADA, esAviso);
				getApplication().getMainView().showModalCentered(InsertarApunteView.class, getDatos(), getStage());
			}
		}
	}

	@Override
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_BORRAR_REGISTRO_MOD", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/descuento_general.png", null, null, "ACCION_DESCUENTO_GENERAL", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/cupon.png", null, null, "SEE_CUSTOMER_COUPONS", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/zoom.png", null, null, "ABRIR_BUSQUEDA_ARTICULOS", "REALIZAR_ACCION"));

		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/previsualizacion.png", null, null, ACCION_PREVISUALIZACION, "REALIZAR_ACCION"));

		// GAP 105 Botones por instancia en el POS
		if (checkActivarBotonesPanel(X_POS_CANJEO_PUNTOS_VISIBLE)) {
			listaAcciones.add(new ConfiguracionBotonBean(ArticlesPointsManager.ICON_ACCION_CANJEAR_PUNTOS_ART, null, null, ArticlesPointsManager.ACCION_CANJEAR_PUNTOS_ART, "REALIZAR_ACCION"));
		}

		if (checkActivarBotonesPanel(X_POS_CONTRATO_VISIBLE)) {
			listaAcciones.add(new ConfiguracionBotonBean("iconos/contract.png", null, null, "CONTRATO", "REALIZAR_ACCION"));
		}

		return listaAcciones;
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		super.realizarAccion(botonAccionado);
		switch (botonAccionado.getClave()) {
			case "CONTRATO":
				abrirFormularioContrato();
				break;
			case "ACCION_TABLA_BORRAR_REGISTRO_MOD":
				accionTablaEliminarRegistro();
				break;
			case "ACCION_DESCUENTO_GENERAL":
				aplicarDescuentoGeneral();
				break;
			// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
			case ArticlesPointsManager.ACCION_CANJEAR_PUNTOS_ART:
				usedButtonArticlePoints();
				break;
			case ACCION_PREVISUALIZACION:
				abrirPrevisualizacion();
				break;
			default:
				break;
		}
	}

	/*
	 * #################################################################################################################
	 * ########################################## GAP46 - CANJEO ARTÍCULOS POR PUNTOS ##################################
	 * #################################################################################################################
	 */

	// GAP62 - PEGATINAS PROMOCIONALES
	@Autowired
	protected PromotionStickerService promotionStickerService;

	@Override
	public synchronized LineaTicket insertarLineaVenta(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad)
			throws LineaTicketException, PromocionesServiceException, DocumentoException, CajasServiceException, CajaRetiradaEfectivoException {

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			if (((IskaypetTicketManager) ticketManager).esArticulo(sCodart))  {
				throw new LineaTicketException(I18N.getTexto("No se puede insertar un artículo en una proforma"), null);
			}
		}

		// GAP62 - PEGATINAS PROMOCIONALES
		// Comprobamos si es una pegatina especial lo que se ha introducido, en ese caso, sacamos el objeto para scar el
		// codArticulo, ponemos desgloses a *, y cantidad fija a uno.
		PromotionalStickerXML isPromotionStickerArticle = null;
		try {
			isPromotionStickerArticle = promotionStickerService.processPromotionStickerArticleInsert(sCodart);
			if (isPromotionStickerArticle != null) {
				sCodart = isPromotionStickerArticle.getCodArticle();
				sDesglose1 = "*";
				sDesglose2 = "*";
				nCantidad = BigDecimal.ONE;
			}
		} catch (Exception ignored) {
		}

		List<LineaTicket> lineasTicket = insertarLineaVentaGetLista(sCodart, sDesglose1, sDesglose2, nCantidad, isPromotionStickerArticle);
		LineaTicket lineaTicket = null;
		if (!lineasTicket.isEmpty()) {
			lineaTicket = lineasTicket.get(0);
		}
		return lineaTicket;

	}

	@Autowired
	protected ArticlesPointsManager articlePointsManager;

	/**
	 * Este método tiene la lógica de {@link #insertarLineaVenta(String, String, String, BigDecimal)}, se ha pasado a un
	 * método que devuelve la lista de líneas para operar con inserciones de Iskaypet de múltiples líneas. Para otros
	 * procedimientos estándares se seguirá llamando a ese método, que devendrá en este:
	 *
	 * @param isPromotionStickerArticle Objeto que contiene la información de si el articulo a insertar es una pegatina promocional.
	 */
	public synchronized List<LineaTicket> insertarLineaVentaGetLista(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad, PromotionalStickerXML isPromotionStickerArticle)
			throws LineaTicketException, PromocionesServiceException, DocumentoException, CajasServiceException,
			CajaRetiradaEfectivoException {

		boolean esMedicamento = false;
		boolean debeImprimir = true;

		// GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1
		// Lógica para insertar en el ticket un número de lineas equivalente a la
		// cantidad introducida.
		List<LineaTicket> listLines = new ArrayList<>();
		int quantity;
		try {
			quantity = nCantidad.intValue();
		} catch (Exception e) {
			quantity = 1;
		}

		for (int i = 0; i < quantity; i++) {
			LineaTicket linea = insertarLineaVentaIskaypet(sCodart, sDesglose1, sDesglose2, BigDecimal.ONE, isPromotionStickerArticle);
			//Si la linea es null significaría que se ha aplicado un cupon de navision
			if (linea != null) {
				if (i == 0) {
					esMedicamento = loteArticuloManager.esArticuloMedicamento(linea.getCodArticulo(), ticketManager.getTicket().getCabecera().getDatosFidelizado(), false);
					// Si es un medicamento, comprobamos si se debe imprimir
					if (esMedicamento) {
						debeImprimir = variablesServices.getVariableAsBoolean(IskaypetLineaTicket.IMPRIMIR_MEDICAMENTOS, true);
					}
				}
				if (linea instanceof IskaypetLineaTicket) {
					((IskaypetLineaTicket) linea).setImprimir(debeImprimir);
				}
				listLines.add(linea);
			}
		}

		// GAP 12 - ISK-8 GESTIÓN DE LOTES
		// Seleccionamos las líneas que hayan sido añadidas al ticket (lineas de medicamentos)
		List<LineaTicket> lineasMedicamentos = loteArticuloManager.devuelveLineasAnadidasATicket(listLines, ticketManager);
		if (!lineasMedicamentos.isEmpty()) {
			// Si la lista de medicamentos no está vacía, seguimos con el proceso
			if (esMedicamento) {
				getDatos().put(AsignarLoteController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasMedicamentos);
				getDatos().put(AsignarLoteController.CLAVE_TICKET_MANAGER, ticketManager);
				getApplication().getMainView().showModalCentered(AsignarLoteView.class, getDatos(), this.getStage());
			}
		}
		// fin GAP 12 - ISK-8

		// Se comprueba si se debe procesar el articulo si es inyectable
		Map<String, List<LineaTicket>> mapInyectables = inyectableArticuloManager.getLineasInyectablesNoInsertadas(listLines);
		for(String codArt : mapInyectables.keySet()) {
			List<LineaTicket> lineasSinInyectable = mapInyectables.get(codArt);
			procesarLineasInyectable(sCodart, lineasSinInyectable);
		}


		//GAP 172 TRAZABILIDAD ANIMALES
		validarTrazabilidadMascotas(listLines);

		return listLines;
	}


	public synchronized LineaTicket insertarLineaVentaIskaypet(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad, PromotionalStickerXML isPromotionStickerArticle)
			throws LineaTicketException, PromocionesServiceException, DocumentoException {

		if (ticketManager.getTicket().getLineas().isEmpty()) {
			log.debug("insertarLineaVenta() - Se inserta la primera línea al ticket por lo que inicializamos nuevo ticket");
			ClienteBean cliente = ticketManager.getTicket().getCliente();
			FidelizacionBean tarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			// ISK-262 GAP-107 GLOVO - Evita que se pierda <delivery> al llamar a #crearNuevoTicket()
			String delivery = ((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).getDelivery();

			crearNuevoTicket();
			ticketManager.getTicket().setCliente(cliente);
			ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);

			((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(delivery);
		}

		// GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS
		// Personalizamos nuevaLineaArticulo para controlar especificamente la excepcion de ArticuloNotFoundException
		LineaTicket linea = null;/* ticketManager.nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, nCantidad, getStage(), null, false); */
		try {
			linea = ((IskaypetTicketManager) ticketManager).nuevaLineaArticuloIskaypet(sCodart, sDesglose1, sDesglose2, nCantidad, getStage(), null, false);
		} catch (ArticuloNotFoundException e) {
			validarCuponNavision(sCodart);
		}
		// GAP62 - PEGATINAS PROMOCIONALES
		// Calculamos el descuento en la linea y seteamos el objeto.
		if (isPromotionStickerArticle != null) {
			try {
				((IskaypetLineaTicket) linea).setPegatinaPromocional(isPromotionStickerArticle);
				((IskaypetLineaTicket) linea).setArticlePoints(null);
				promotionStickerService.applyDiscountPromotionalSticker(((IskaypetLineaTicket) linea), ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
			} catch (Exception ignored) {
			}
			((IskaypetLineaTicket) linea).setPegatinaPromocional(isPromotionStickerArticle);
		}

		if (!validarLinea(linea)) {
			ticketManager.eliminarLineaArticulo(linea.getIdLinea());
		} else {
			// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
			// Generamos los datos del artículo de puntos en caso de ser válido.
			articlePointsManager.processLineArticlePoints(this.getStage(), (TicketVentaAbono) ticketManager.getTicket(), linea, ArticlesPointsManager.ACTION_INSERT, true);
		}

		guardarCopiaSeguridad();

		return linea;
	}

	@FXML
	protected void usedButtonArticlePoints() {

		if (getLineaSeleccionada() == null) {
			String msgInfo = I18N.getTexto("Debe seleccionar una línea del documento para poder realizar el proceso de canjeo de puntos.");
			VentanaDialogoComponent.crearVentanaAviso(msgInfo, this.getStage());
			return;
		}

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede realizar el canjeo de articulos por puntos a un ticket proforma"), getStage());
			return;
		}

		// //TODO descomentar si no se puede canjear por puntos en venta delivery
		// if(((IskaypetTicketManager)ticketManager).isTicketVentaDelivery()) {
		// String msgInfo = I18N.getTexto("No se permite canjear línea por puntos en una venta de plataforma digital");
		// VentanaDialogoComponent.crearVentanaAviso(msgInfo, this.getStage());
		// return;
		// }

		// Sacamos el objeto de la linea seleccionada.
		LineaTicket lineaSeleccionada = null;
		if (ticketManager.getTicket().getLineas() != null && !ticketManager.getTicket().getLineas().isEmpty()) {
			List<LineaTicket> listLineasTicket = (List<LineaTicket>) ticketManager.getTicket().getLineas();
			for (LineaTicket lineaTicket : listLineasTicket) {
				if (getLineaSeleccionada().getIdLinea().equals(lineaTicket.getIdLinea())) {
					lineaSeleccionada = lineaTicket;
					break;
				}
			}
		}

		if (lineaSeleccionada != null && lineaSeleccionada instanceof IskaypetLineaTicket
				&& articlePointsManager.canUsedButtonArticlePoints(getStage(), (TicketVentaAbono) ticketManager.getTicket(), (IskaypetLineaTicket) lineaSeleccionada, true)) {

			List<LineaTicket> lineasCanjeables = getLineasCanjeablesTicket((TicketVentaAbono) ticketManager.getTicket(), lineaSeleccionada.getCodArticulo());

			if(lineasCanjeables.size() == 1) {
				articlePointsManager.processLineArticlePoints(this.getStage(), (TicketVentaAbono) ticketManager.getTicket(), lineaSeleccionada, ArticlesPointsManager.ACTION_UPDATE, true);
			} else {

				getDatos().put(EdicionArticuloPuntosController.LINEAS_CANJEABLES, lineasCanjeables.size());
				getDatos().put(EdicionArticuloPuntosController.TICKET, ticketManager.getTicket());
				getDatos().put(EdicionArticuloPuntosController.LINEA_SELECCIONADA, lineaSeleccionada);
				getApplication().getMainView().showModalCentered(EdicionArticuloPuntosView.class, getDatos(), this.getStage());


				Object valorLineasCanjeo = getDatos().get(EdicionArticuloPuntosController.CANTIDAD_CANJEO);
				int lineasCanjeadas = (valorLineasCanjeo != null) ? Integer.parseInt(valorLineasCanjeo.toString()) : 0;

				for (int i = 0; i < lineasCanjeadas; ++i) {
					// Marcamos como usado y reprocesamos la linea.
					articlePointsManager.processLineArticlePoints(this.getStage(), (TicketVentaAbono) ticketManager.getTicket(), lineasCanjeables.get(i), ArticlesPointsManager.ACTION_UPDATE, false);
				}

				if (lineasCanjeadas != 0) {
					mostrarMensajeInfoCanjeo((IskaypetLineaTicket) lineaSeleccionada, BigDecimal.valueOf(lineasCanjeadas));
				}
			}
			ticketManager.recalcularConPromociones();
			ticketManager.guardarCopiaSeguridadTicket();
			refrescarDatosPantalla();
		}

	}

	public List<LineaTicket> getLineasCanjeablesTicket(TicketVentaAbono ticket, String itemCode) {
		List<LineaTicket> listLineasSinCanjear = new ArrayList<LineaTicket>();
		List<LineaTicket> listLineTicket = ticket.getLineas();
		for (LineaTicket lineaTicket : listLineTicket) {
			if (itemCode.equalsIgnoreCase(lineaTicket.getCodArticulo()) &&
				articlePointsManager.canUsedButtonArticlePoints(getStage(), (TicketVentaAbono) ticketManager.getTicket(), (IskaypetLineaTicket) lineaTicket, false) &&
				articlePointsManager.checkIsReedem(getStage(), ((IskaypetLineaTicket) lineaTicket).getArticlePoints(), false)) {
					listLineasSinCanjear.add(lineaTicket);
			}
		}
		return listLineasSinCanjear;
	}

	private void mostrarMensajeInfoCanjeo(IskaypetLineaTicket lineaSeleccionada, BigDecimal lineasCanjeadas) {
		ArticlesPointsXMLBean articlesPoints = ((IskaypetLineaTicket) lineaSeleccionada).getArticlePoints();
		String msgInfo = I18N.getTexto("Artículo canjeado '") + lineaSeleccionada.getCodArticulo() + " - " + lineaSeleccionada.getDesArticulo() + "'"
				+ "\n- " + I18N.getTexto("Puntos usados : ") + articlesPoints.getPoints()
				.multiply(lineasCanjeadas) + "\n- " + I18N.getTexto("Precio total canjeado : ")
				+ articlesPoints.getValue().multiply(lineasCanjeadas).setScale(2, RoundingMode.HALF_UP) + "\n";
		VentanaDialogoComponent.crearVentanaInfo(msgInfo, getStage());
	}

	@FXML
	protected void accionTablaEliminarRegistro() {
		log.debug("accionTablaEliminarRegistro() - ");
		try {

			if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede eliminar un registro de una proforma"), getStage());
				return;
			}

			if (!tbLineas.getItems().isEmpty() && getLineaSeleccionada() != null) {
				super.compruebaPermisos(PERMISO_BORRAR_LINEA);
				LineaTicketGui selectedItem = getLineaSeleccionada();
				if (selectedItem.isCupon()) {
					sesionPromociones.deleteCoupon(selectedItem.getArticulo(), (DocumentoPromocionable) ticketManager.getTicket());
					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();
				} else {
					int idLinea = getLineaSeleccionada().getIdLinea();
					ILineaTicket linea = ticketManager.getTicket().getLinea(idLinea);
					ILineaTicket lastLineMemory = null;
					if (linea.isEditable()) {
						boolean confirmar = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de querer eliminar esta línea del ticket?"), getStage());
						if (!confirmar) {
							return;
						}
						if (ticketManager.getTicket().getLineas().size() == 1) {
							lastLineMemory = ((LineaTicket) ticketManager.getTicket().getLineas().get(0)).clone();
						}
						ticketManager.eliminarLineaArticulo(idLinea);

						// GAP46 - CANJEO ARTÍCULOS POR PUNTOS
						articlePointsManager.processLineArticlePoints(this.getStage(), (TicketVentaAbono) ticketManager.getTicket(), (LineaTicket) linea, ArticlesPointsManager.ACTION_DELETE, true);

						int ultimArticulo = ticketManager.getTicket().getLineas().size();
						if (ultimArticulo > 0) {
							LineaTicket ultimaLinea = (LineaTicket) ticketManager.getTicket().getLineas().get(ultimArticulo - 1);
							escribirLineaEnVisor(ultimaLinea);
						} else {
							visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
						}
						guardarCopiaSeguridad();
						seleccionarSiguienteLinea();
						refrescarDatosPantalla();
						if (!ticketManager.getTicket().getLineas().isEmpty()) {
							visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
						} else {
							visor.modoEspera();
						}
					} else {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), getStage());
					}
					if (ticketManager.getTicket().getIdTicket() != null && ticketManager.getTicket().getLineas().isEmpty()) {
						if (lastLineMemory != null) {
							ticketManager.getTicket().getLineas().add(lastLineMemory);
						}
						//ticketManager.salvarTicketVacio();
						try {
							ticketManager.eliminarTicketCompleto();
						} catch (Exception e) {
							log.error("Ha ocurrido un error al eliminar el ticket ", e);
						}
					}
				}
			}
		} catch (SinPermisosException ex) {
			log.debug("accionTablaEliminarRegistro() - El usuario no tiene permisos para eliminar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para borrar una línea"), getStage());
		} catch (CuponUseException | CuponesServiceException | CuponAplicationException e) {
			log.error("accionTablaEliminarRegistro() - No se ha podido eliminar el cupón");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido eliminar el cupón"), getStage());
		}
	}

	/*
	 * GAPXX - BLOQUEO DE LINEAS EN VENTA CON CANTIDAD SUPERIOR A 1 Al no poder tener mas de una cantidad por linea, esta
	 * lógica no tiene sentido.
	 */
	@Override
	protected void accionTablaEditarRegistro() {
		log.debug("accionTablaEditarRegistro() - Acción ejecutada");
		try {
			// Comprombamos si el usuario tiene permisos para modificar la línea
			super.compruebaPermisos("MODIFICAR LINEA");

			if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede modificar una línea de un ticket proforma"), getStage());
				return;
			}

			// Comprobamos que hay líneas en la tabla
			if (tbLineas.getItems() == null || tbLineas.getItems().isEmpty()) {
				log.debug("accionTablaEditarRegistro() - No hay líneas en la tabla");
				return;
			}

			// Comprobamos que hay una línea seleccionada
			LineaTicketGui selectedItem = getLineaSeleccionada();
			if (selectedItem == null) {
				log.debug("accionTablaEditarRegistro() - No se ha seleccionado ninguna línea");
			}

			// Comprobamos que no es una linea de articulo por puntos
			if (articlePointsManager.isLineWithArticlePointsUsed((TicketVentaAbono) ticketManager.getTicket(), getLineaSeleccionada().getIdLinea()) != null) {
				log.debug("accionTablaEditarRegistro() - No se pueden editar lineas con puntos aplicados.");
				String msgAviso = I18N.getTexto("No se pueden editar lineas con puntos aplicados.");
				VentanaDialogoComponent.crearVentanaAviso(msgAviso, this.getStage());
				return;
			}

			// Continua con la edición de la línea del estandar
			if (selectedItem.isCupon()) {
				log.debug("accionTablaEditarRegistro() - No se puede modificar una línea de cupón");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), getStage());
				return;
			}

			ILineaTicket lineaTicket = ticketManager.getTicket().getLinea(selectedItem.getIdLinea());
			if (lineaTicket == null) {
				log.debug("accionTablaEditarRegistro() - No se ha encontrado la línea seleccionada");
				return;
			}

			if (!lineaTicket.isEditable()) {
				log.debug("accionTablaEditarRegistro() - La línea seleccionada no se puede modificar");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea seleccionada no se puede modificar."), getStage());
				return;
			}

			HashMap<String, Object> parametrosEdicionArticulo = new HashMap();
			parametrosEdicionArticulo.put("Articulo", lineaTicket);
			parametrosEdicionArticulo.put("TICKET_KEY", ticketManager);
			abrirVentanaEdicionArticulo(parametrosEdicionArticulo);
			ticketManager.recalcularConPromociones();
			guardarCopiaSeguridad();
			escribirLineaEnVisor((LineaTicket) lineaTicket);
			visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));

			log.debug("accionTablaEditarRegistro() - Línea modificada correctamente");
		} catch (SinPermisosException var5) {
			log.debug("accionTablaEditarRegistro() - El usuario no tiene permisos para modificar línea");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No tiene permisos para modificar una línea"), getStage());
		}
	}

	/**
	 * CZZ-493 - Sobreescrito método de fallo de nuevo artículo para gestionar los errores de la api
	 */
	@Override
	protected void nuevoArticuloTaskFailed(com.comerzzia.pos.util.exception.Exception cmzException) {
		mostrarVentanaInfo = false;
		log.error("failed() - NuevoCodigoArticuloTask failed: " + cmzException, cmzException);
		String msg = cmzException.getMessageI18N();
		if(cmzException.getCause() instanceof ApiClientException) {
			msg = procesarMensajeErrorApi(cmzException.getMessageI18N());
		}

		VentanaDialogoComponent.crearVentanaError(msg, getStage());
	}

	/**
	 * CZZ-493 - Traduccion de textos de error de la api
	 */
	private String procesarMensajeErrorApi(String message) {
		log.debug("procesarMensajeErrorApi()");
		if (message == null)
			return null;

		// Ejemplo: The coupon 22222 is associated with a loyal customer
		Pattern pattern = Pattern.compile("The coupon ([\\dA-Z]+) is associated with a loyal customer and cannot be used in an anonymous sale");
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			String couponCode = matcher.group(1);
			return I18N.getTexto("El cupón {0} está asociado a un cliente fidelizado y no puede utilizarse de forma anónima",couponCode);
		}

		// Ejemplo: Record not found
		pattern = Pattern.compile("Record not found");
		matcher = pattern.matcher(message);
		if (matcher.matches()) {
			return I18N.getTexto("Registro no encontrado");
		}

		// Ejemplo: The coupon will not be active until 09/02/24
		pattern = Pattern.compile("The coupon will not be active until ([^\\\\s]+)");
		matcher = pattern.matcher(message);
		if (matcher.matches()) {
			String fecha = matcher.group(1);
			return I18N.getTexto("El cupón no se activará hasta {0}",fecha);
		}

		return message;

}



	@Override
	protected void abrirVentanaEdicionArticulo(HashMap<String, Object> parametrosEdicionArticulo) {

		LineaTicket lineaTicket = (LineaTicket) parametrosEdicionArticulo.get(EdicionArticuloController.CLAVE_PARAMETRO_ARTICULO);
		getApplication().getMainView().showModalCentered(EdicionArticuloView.class, parametrosEdicionArticulo, this.getStage());

		/* TEMP NUEVAS LÍNEAS */

		try {

			boolean aplicarPromociones = true;

			// ISK-249 GAP 101 Edición de cantidad en POS
			// Tras cerrar ventana de edición de artículos se insertan las líneas
			// correspondientes a la cantidad-1, en caso de que cantidad>1
			BigDecimal cantidadTotalTfCantidad = (BigDecimal) parametrosEdicionArticulo.get(IskaypetEdicionArticuloController.CLAVE_CANTIDAD_SELECCIONADA_TEXTFIELD);

			List<AuditoriaLineaTicket> listaAuditLineasEdicionPrecio = null;
			if (parametrosEdicionArticulo.containsKey(PARAM_LISTA_AUDIT_EDICION_PRECIO) && parametrosEdicionArticulo.get(PARAM_LISTA_AUDIT_EDICION_PRECIO) != null) {
				listaAuditLineasEdicionPrecio = (List<AuditoriaLineaTicket>) parametrosEdicionArticulo.get(PARAM_LISTA_AUDIT_EDICION_PRECIO);
			}

			if (cantidadTotalTfCantidad != null && BigDecimalUtil.isMayor(cantidadTotalTfCantidad, BigDecimal.ONE)) {
				BigDecimal numeroArticulosAnadir = cantidadTotalTfCantidad.subtract(BigDecimal.ONE);

				getLineasNuevasListEdicion(lineaTicket, numeroArticulosAnadir, listaAuditLineasEdicionPrecio, aplicarPromociones);
			} else {
				// Si la cantidad no ha cambiado, refrescamos por si ha habido cambio de precio
				refrescarDatosPantalla();
			}
		} catch (CajaRetiradaEfectivoException | CajasServiceException | DocumentoException |
				 PromocionesServiceException | LineaTicketException e) {
			log.error("accionTablaEditarRegistro() - Error al añadir línea", e);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error al añadir línea."), getStage());
		}

	}

	protected List<LineaTicket> getLineasNuevasListEdicion(LineaTicket lineaTicket, BigDecimal numeroArticulosAnadir, List<AuditoriaLineaTicket> listaAuditLineasEdicionPrecio, boolean aplicarPromociones) throws LineaTicketException, CajaRetiradaEfectivoException, CajasServiceException, DocumentoException, PromocionesServiceException {
		List<LineaTicket> lineasGet = null;
		BackgroundTask<List<LineaTicket>> task = new BackgroundTask<List<LineaTicket>>() {

			@Override
			protected List<LineaTicket> call() throws LineaTicketException {
				List<LineaTicket> lineasNuevas = null;
				try {
					lineasNuevas = insertarLineaVentaGetLista(lineaTicket.getCodArticulo(), lineaTicket.getDesglose1(), lineaTicket.getDesglose2(), numeroArticulosAnadir, null);
				} catch (LineaTicketException | PromocionesServiceException | DocumentoException |
						 CajasServiceException | CajaRetiradaEfectivoException e) {
					log.error("getLineasNuevasList() - Error cargando las lineas en el ticket");
					throw new LineaTicketException(e.getMessage());
				}
				return lineasNuevas;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("getLineasNuevasList() - seteando lineas el ticket");
				List<LineaTicket> lineasGet = getValue();

				for (LineaTicket lineaNueva : lineasGet) {

					// descuentos
					lineaNueva.setDescuentoManual(lineaTicket.getDescuentoManual());

					// Auditoría Cambio Precio (AUCP)
					if (listaAuditLineasEdicionPrecio != null && !listaAuditLineasEdicionPrecio.isEmpty() && lineaNueva instanceof IskaypetLineaTicket) {
						IskaypetLineaTicket lineaNuevaIsk = (IskaypetLineaTicket) lineaNueva;
						AuditoriaLineaTicket auditoria = listaAuditLineasEdicionPrecio.get(0);
						auditoriasService.addAuditoriaLinea(lineaNuevaIsk,auditoria.getTipo(), auditoria.getUidAuditoria(), auditoria.getCodigo());
						listaAuditLineasEdicionPrecio.remove(0);
					}

					// Copiado de de EdicionArticulos
					BigDecimal precioSinImpuestos = sesion.getImpuestos().getPrecioSinImpuestos(lineaTicket.getCodImpuesto(), lineaTicket.getPrecioTotalSinDto(),
							ticketManager.getTicket().getCabecera().getCliente().getIdTratImpuestos());
					lineaNueva.setPrecioSinDto(precioSinImpuestos);
					lineaNueva.setPrecioTotalSinDto(lineaTicket.getPrecioTotalSinDto());
					lineaNueva.setDescuentoManual(lineaTicket.getDescuentoManual());
					lineaNueva.setCantidad(ticketManager.tratarSignoCantidad(BigDecimal.ONE, lineaTicket.getCabecera().getCodTipoDocumento()));
					lineaNueva.setVendedor(lineaTicket.getVendedor());
					lineaNueva.setDesArticulo(lineaTicket.getDesArticulo());
					lineaNueva.setNumerosSerie(lineaTicket.getNumerosSerie());

					if (ticketManager instanceof IskaypetTicketManager && lineaNueva instanceof  IskaypetLineaTicket && ((IskaypetLineaTicket) lineaNueva).getInyectable() != null) {
						log.debug("getLineasNuevasList() - Se ha añadido una línea inyectable, se debe recalcular el importe");
						BigDecimal cantidadConvertida = ((IskaypetLineaTicket) lineaNueva).getInyectable().getCantidadConvertida();
						((IskaypetTicketManager) ticketManager).calcularImporteCantidadConvertida((IskaypetLineaTicket) lineaNueva, lineaNueva.getPrecioTotalTarifaOrigen(), cantidadConvertida);
					}

					lineaNueva.recalcularImporteFinal();
					if (aplicarPromociones) {
						ticketManager.recalcularConPromociones();
					}
				}
				refrescarDatosPantalla();

			}

			@Override
			protected void failed() {
			}
		};
		task.start();
		return lineasGet;

	}

	public String validarDatosFidelizadoContrato(FidelizacionBean fidelizado) {
		String msgError = "";
		// Primero sacamos los campos del fidelizado necesarios para el contrato
		String nombre = fidelizado.getNombre();
		String apellidos = fidelizado.getApellido();
		String documento = fidelizado.getDocumento();
		String direccion = fidelizado.getDomicilio();
		String cp = fidelizado.getCp();
		String tlf = "", email = "";

		Map<String, Object> adicionales = fidelizado.getAdicionales();
		if (adicionales != null && !adicionales.isEmpty()) {
			if (adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_EMAIL)) {
				log.debug("EMAIL: " + adicionales.get(IskaypetFidelizacion.PARAMETRO_EMAIL));
				email = (String) adicionales.get(IskaypetFidelizacion.PARAMETRO_EMAIL);
			}

			if (adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_MOVIL)) {
				log.debug("MOVIL: " + adicionales.get(IskaypetFidelizacion.PARAMETRO_MOVIL));
				tlf = (String) adicionales.get(IskaypetFidelizacion.PARAMETRO_MOVIL);
			}
			if (tlf.isEmpty() && adicionales.containsKey(IskaypetFidelizacion.PARAMETRO_TELEFONO)) {
				log.debug("TELEFONO: " + adicionales.get(IskaypetFidelizacion.PARAMETRO_TELEFONO));
				tlf = (String) adicionales.get(IskaypetFidelizacion.PARAMETRO_TELEFONO);
			}

		}
		// Componemos la cadena para indicar que datos faltan del fidelizado
		if (StringUtils.isBlank(nombre)) {
			msgError = msgError + "\n - Nombre";
		}
		if (StringUtils.isBlank(apellidos)) {
			msgError = msgError + "\n - Apellidos";
		}
		if (StringUtils.isBlank(documento)) {
			msgError = msgError + "\n - Doc.Identificación";
		}
		if (StringUtils.isBlank(direccion)) {
			msgError = msgError + "\n - Dirección";
		}
		if (StringUtils.isBlank(cp)) {
			msgError = msgError + "\n - C.Postal";
		}

		// Validar código postal
		ValidadorCodigoPostal validadorCP = ValidadorCodigoPostalFactory.getValidadorCodigoPostal(fidelizado.getCodPais());
		ContextoValidadorCodigoPostal context = new ContextoValidadorCodigoPostal();
		context.setValidadorCodigoPostal(validadorCP);
		try {
			context.ejecutaValidacionPostal(cp);
		} catch (ValidadorCodigoPostalException e) {
			String error;
			if(!fidelizado.getCodPais().equals("ES") && !fidelizado.getCodPais().equals("PT")) {
				error = I18N.getTexto("Código postal no válido: debe tener entre 4 y 8 caracteres, con letras, números y un guion (no al inicio ni al final).");
			} else {
				error = I18N.getTexto("El código postal del cliente no es válido. Por favor, asegúrese de que el código tenga el formato correcto {0} y solo contenga números.", e.getMessage());
			}
			msgError = msgError + "\n - " +  error;
		}

		if (StringUtils.isBlank(tlf)) {
			msgError = msgError + "\n - Teléfono";
		}
		if (StringUtils.isBlank(email)) {
			msgError = msgError + "\n - Email";
		}

		return msgError;

	}

	// GAP 105 BOTONES POR INSTANCIA EN EL POS
	public boolean checkActivarBotonesPanel(String idVariable) {
		// Si la variable no existe por defecto se activan
		String valor = variablesServices.getVariableAsString(idVariable);
		boolean activo = true;
		if (StringUtils.isNotBlank(valor)) {
			activo = variablesServices.getVariableAsBoolean(idVariable);
		}
		return activo;
	}

	// GAP N - CREAR AUDITORIA GENERAL
	private void crearAuditoriaDescuentoGeneral(IskaypetLineaTicket linea, BigDecimal importeDescuentoGeneral, BigDecimal precioTotalInicial) {
		try {
			log.debug("crearAuditoriaDescuentoGeneral() - Guardando auditoria");
			AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setCodMotivo(motivo.getCodigo().toString());
			auditoria.setObservaciones(motivo.getDescripcion());
			auditoria.setPrecioInicial(precioTotalInicial);
			auditoria.setPrecioFinal(importeDescuentoGeneral);
			auditoria.setCodArticulo(linea.getCodArticulo());
			auditoria.setDesArt(linea.getDesArticulo());
			auditoria.setDesglose1(linea.getDesglose1());
			auditoria.setDesglose2(linea.getDesglose2());
			auditoria = auditoriasService.generarAuditoria(auditoria, AuditoriasService.TIPO_AUDITORIA_DESCUENTO_GENERAL, null, Boolean.TRUE);
			String uidAuditoria = auditoria.getUidAuditoria();

			// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
			auditoriasService.addAuditoriaLinea(linea, AuditoriasService.TIPO_AUDITORIA_DESCUENTO_GENERAL, uidAuditoria, auditoria.getCodMotivo());

		} catch (ContadorServiceException e) {
			log.error("crearAuditoriaDescuentoGeneral() - error al insertar la auditoria" + e.getMessage(), e);
		} catch (EmpresaException e) {
			log.error("crearAuditoriaDescuentoGeneral() - Empresa no encontrada " + e.getMessage(), e);

		} catch (TipoDocumentoNotFoundException e) {
			log.error("crearAuditoriaDescuentoGeneral() - Tipo de documento no encontrado " + e.getMessage(), e);

		} catch (DocumentoException e) {
			log.error("crearAuditoriaDescuentoGeneral() - error al recuperar el tipo de documento " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void consultarCopiaSeguridad() throws DocumentoException, TicketsServiceException {
		// Comprobamos si existens copias de seguridad de tickets en base de datos para esta pantalla y si es así
		// ofrecemos
		// la posibilidad de recuperarlo
		log.debug("consultarCopiaSeguridad() - Consultando copia de seguridad en inicialización de pantalla...");
		TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_SIMPLIFICADA);
		final TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);

		if (copiaSeguridad != null) {
			log.debug("consultarCopiaSeguridad() - Se ha encontrado una copia de seguridad");
			try {
				log.debug("consultarCopiaSeguridad() - Copia de seguridad: " + new String(copiaSeguridad.getTicket(), StandardCharsets.UTF_8));
			} catch (Exception e) {
				log.error("consultarCopiaSeguridad() - Ha ocurrido un error parseando la copia de seguridad: " + e.getMessage(), e);
			}
			TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(copiaSeguridad.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[]{}));

			if (ticketRecuperado != null) {
				if (ticketRecuperado.getIdTicket() != null) {
					log.info("consultarCopiaSeguridad() - El id ticket de la copia de seguridad es " + ticketRecuperado.getIdTicket());
					if (ticketRecuperado.getPagos().isEmpty()) {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket sin finalizar. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					} else {
						VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Existe un ticket guardado con pagos realizados. Se tiene que terminar ese ticket antes de poder vender."), getStage());
					}

					try {
						ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);
						iskaypetTicketService.deshacerContadorIdTicket(ticketManager.getTicket(), tipoDocumentoActivo);
						ticketRecuperado.setIdTicket(null);
						ticketRecuperado.getCabecera().setCodTicket(null);
						log.info("consultarCopiaSeguridad() - Se va a abrir la pantalla de pagos, ya que hay id ticket asignado...");
						abrirPagos();
						return;
					} catch (Throwable e) {
						log.error("consultarCopiaSeguridad() - Ha habido un error al recuperar el ticket: " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						return;
					}
				}

				if (!tieneArticuloGiftCard(ticketRecuperado, tipoDocumentoActivo)) {
					log.debug("consultarCopiaSeguridad() - Se va a preguntar al usuario si quiere recuperar la copia de seguridad");
					if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Existe una venta sin finalizar. ¿Desea recuperarla?"), getStage())) {
						log.debug("consultarCopiaSeguridad() - El usuario ha aceptado la recuperación de la copia de seguridad");
						try {
							ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);

							Platform.runLater(() -> visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket()))));
						} catch (Throwable e) {
							log.error("consultarCopiaSeguridad() - " + e.getMessage(), e);
							VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
						}
					} else {
						try {
							for (IskaypetLineaTicket lineaOrigen : (List<IskaypetLineaTicket>) ((TicketVenta) ticketRecuperado).getLineas()) {
								// GAP62 - PEGATINAS PROMOCIONALES
								// En caso de la linea ser una pegatina promocional, debemos ponerla como habilitada de nuevo ya que estamos eliminando
								// su uso.
								if (lineaOrigen.getPegatinaPromocional() != null) {
									promotionStickerService.processPromotionStickerArticleDelete(lineaOrigen.getPegatinaPromocional().getEan());
									lineaOrigen.setPegatinaPromocional(null);
								}
							}
						} catch (Exception e) {
							// Actualmente no devolvemos error.
						}

						log.debug("consultarCopiaSeguridad() - El usuario no ha aceptado la recuperación de la copia de seguridad");
					}
				}
			}
		}
	}

	/*
	 * ################################################################################################################
	 * ############################### GAP147 - USO DE CUPONES DE INTEGRACION NAV EN POS ##############################
	 * ################################################################################################################
	 */

	private void validarCuponNavision(String sCodart) throws LineaTicketException {
		try {
			IskaypetCustomerCouponDTO customerCouponDTOAplicar = null;
			// Si tenemos fidelizado isnertado en el POS buscamos sobre N cupones dispobles
			if (ticketManager.getTicket().getCabecera().getDatosFidelizado() != null) {
				List<CustomerCouponDTO> availableCoupons = ticketManager.getTicket().getCabecera().getDatosFidelizado().getAvailableCoupons();
				int maxBusqueda = availableCoupons.size();
				if (maxBusqueda >= 50) {
					maxBusqueda = 50;
				}
				for (int i = 0; i < maxBusqueda/* poner un valor tope */; i++) {
					if (StringUtils.isNotBlank(((IskaypetCustomerCouponDTO) availableCoupons.get(i)).getCouponCodeNavision())) {
						if (sCodart.equalsIgnoreCase(((IskaypetCustomerCouponDTO) availableCoupons.get(i)).getCouponCodeNavision())) {
							customerCouponDTOAplicar = (IskaypetCustomerCouponDTO) availableCoupons.get(i);
							break;
						}
					}
				}
				// Si no está en los n cupones que tiene disponible el fidelizado, consultamos en central
				if (customerCouponDTOAplicar == null) {
					customerCouponDTOAplicar = aplicarBusquedaSinFidelizado(sCodart);
				}
			} else {
				customerCouponDTOAplicar = aplicarBusquedaSinFidelizado(sCodart);
			}
			// Si el cupon existe preguntamos si quiere aplicarlo a la venta
			if (customerCouponDTOAplicar != null) {

				// Cambiamos el mensaje por parte por fallo en traducción.
				String msgConfirm1 = "";
				if (sesion.getAplicacion().getUidActividad().contains("KIWOKO")) {
					msgConfirm1 = I18N.getTexto("Se selecciono un cupón navision con el valor");
				} else {
					msgConfirm1 = I18N.getTexto("Se selecciono un cupón SBO con el valor");
				}

				String msgConfirm2 = I18N.getTexto("¿Está seguro de querer insertarlo en esta venta?");
				String msgConfirm = msgConfirm1 + " " + Numero.formatea(customerCouponDTOAplicar.getBalance(), 2) + "\n" + msgConfirm2;
				if (VentanaDialogoComponent.crearVentanaConfirmacion(msgConfirm, getStage())) {
					try {
						sesionPromociones.aplicarCupon(customerCouponDTOAplicar, (TicketVentaAbono) ticketManager.getTicket());
						ticketManager.recalcularConPromociones();
					} catch (CuponUseException e1) {
						log.warn("insertarLineaVentaIskaypet() - Error en la aplicación del cupón -" + e1.getMessageI18N());
						throw new LineaTicketException(e1.getMessageI18N(), e1);
					} catch (CuponesServiceException e1) {
						log.warn("insertarLineaVentaIskaypet() - Error en la aplicación del cupón -" + e1.getMessageI18N());
						throw new LineaTicketException(e1.getMessageI18N(), e1);
					} catch (CuponAplicationException e1) {
						log.warn("insertarLineaVentaIskaypet() - Error en la aplicación del cupón -" + e1.getMessageI18N());
						throw new LineaTicketException(e1.getMessageI18N(), e1);
					}
				}
			} else {
				// Si cancela la busqueda seguimos como estándar
				log.warn("nuevaLineaArticulo() - Artículo no encontrado " + sCodart);
				throw new ArticuloNotFoundException(I18N.getTexto("El artículo consultado no se encuentra en el sistema"));
			}
		} catch (LineaTicketException e) {
			throw e;
		} catch (Exception e) {
			log.error("insertarLineaVentaIskaypet() - Error en la busqueda del codigo introducido" + e.getMessage());
			throw new LineaTicketException(e.getMessage());
		}
	}

	private IskaypetCustomerCouponDTO aplicarBusquedaSinFidelizado(String sCodart)
			throws EmpresaException, Exception, IllegalAccessException, InvocationTargetException, LineaTicketException, ArticuloNotFoundException {

		Sesion sesion = SpringContext.getBean(Sesion.class);
		ComerzziaApiManager apiManager = SpringContext.getBean(ComerzziaApiManager.class);

		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(sesion.getAplicacion().getStoreLanguageCode()));
		CouponsApi api = apiManager.getClient(datosSesion, "CouponsApi");
		IskaypetCustomerCouponDTO customerCouponDTO = null;
		if (VentanaDialogoComponent.crearVentanaConfirmacion("", I18N.getTexto("No se ha encontrado el código indicado como artículo en el sistema \n¿Desea buscarlo en central como un cupón?"),
				getStage())) {
			Map<String, CouponTypeDTO> mapPrefijosCupon = sesionPromociones.getCouponType();
			if (mapPrefijosCupon != null) {
				for (CouponTypeDTO couponType : mapPrefijosCupon.values()) {
					CouponDetail coupon = api.getCoupon(couponType.getPrefix() + sCodart);
					if (coupon != null) {
						// Indicamos que si que tiene que aplicar las validaciones de estándar para su uso
						customerCouponDTO = new IskaypetCustomerCouponDTO(coupon.getCouponCode(), true);
						BeanUtils.copyProperties(customerCouponDTO, coupon);
						for (CouponLink customerCouponDTO2 : coupon.getLinks()) {
							if ("NAVISION_COUPON".equalsIgnoreCase(customerCouponDTO2.getClassId())) {
								customerCouponDTO.setCouponCodeNavision(customerCouponDTO2.getObjectId());
							}
							if ("COMPRA_MINIMA".equalsIgnoreCase(customerCouponDTO2.getClassId())) {
								customerCouponDTO.setCompraMinima(customerCouponDTO2.getObjectId());
							}
						}
						break;
					}
				}
			}
		} else {
			throw new ArticuloNotFoundException("El artículo consultado no se encuentra en el sistema");
		}
		return customerCouponDTO;
	}

	@Override
	protected void useCustomerCoupons() {

		// ISK-262 GAP-107 GLOVO - No se permite el uso de cupones
		if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery()) {
			return;
		}

		super.useCustomerCoupons();
	}

	@Override
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
			tfCantidadIntro.setText("1");
			tfCodigoIntro.clear();

			if (accionValidarFormulario() && cantidad != null && !BigDecimalUtil.isIgualACero(cantidad)) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				if (variablesServices.getVariableAsBoolean(PERMITE_BUSCADOR_GENERICO_FIDELIZACION, false) && !procesarTarjetaFidelizacion()) {
					return;
				}

				NuevoCodigoArticuloTask taskArticulo = SpringContext.getBean(NuevoCodigoArticuloTask.class, this, frValidacion.getCodArticulo(), cantidad); // anidada
				taskArticulo.start();

			}
		}
	}


	protected boolean procesarTarjetaFidelizacion() {

		// 1. Validamos que el artículo es un prefijo de tarjeta de fidelización
		if (!Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(frValidacion.getCodArticulo())) {
			return true;
		}


		// 2. Validamos que no haya líneas en el ticket en caso de que sea un cliente delivery previamente identificado
		if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery() && !ticketManager.isTicketVacio()) {
			// ISK-262 GAP-107 GLOVO - No se permite cambiar de fidelizado en ventas delivery con líneas
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se permite la modificación del cliente en esta operación"), getStage());
			// Como hay líneas en el ticket de una venta delivery, se devuelve false para no continuar con la inserción de la línea
			return false;
		}

		// 3. Recalculamos el ticket y refrescamos la pantalla
		ticketManager.recalcularConPromociones();
		refrescarDatosPantalla();

		// 4. Cargamos la tarjeta de fidelización si existe en la base de datos central
		// Si es prefijo de tarjeta fidelizacion, marcamos la venta como fidelizado y llamamos al REST
		Dispositivos.getInstance().getFidelizacion().cargarTarjetaFidelizado(frValidacion.getCodArticulo(), getStage(), new DispositivoCallback<FidelizacionBean>() {

			@Override
			public void onSuccess(FidelizacionBean tarjeta) {
				if (tarjeta.isBaja()) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
					tarjeta = null;
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				} else if (deliveryManager.fidelizadoGeneraVentaDelivery(tarjeta)) {
					// ISK-262 GAP-107 GLOVO - Se permite cambiar a cliente delivery siempre que no haya líneas
					if (!ticketManager.isTicketVacio()) {
						VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede seleccionar este cliente con artículos en la cesta. Para este tipo de cliente debe de introducir primero el cliente con la cesta vacía y luego los artículos."), getStage());
					} else {
						String codColectivoDelivery = deliveryManager.getCodColectivoVentaDelivery(tarjeta);

						((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(codColectivoDelivery);
						((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(false);

						ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
						cargarFidelizado(tarjeta.getIdFidelizado());
					}

				} else {
					// Tarjeta válida - lo seteamos en el ticket
					ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				}

				// ISK-262 GAP-107 GLOVO - Se borran datos de delivery si el nuevo cliente asignado no es delivery
				if (!deliveryManager.fidelizadoGeneraVentaDelivery(ticketManager.getTicket().getCabecera().getDatosFidelizado())) {
					((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setDelivery(null);
					((TicketVentaAbono) ticketManager.getTicket()).setAdmitePromociones(true);
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
		// Como se ha lanzado la tarea de carga de tarjeta de fidelización, se devuelve false para no continuar con la inserción de la línea
		return false;
	}

	protected boolean accionValidarFormulario() {
		this.frValidacion.clearErrorStyle();

		try {
			loteArticuloManager.esArticuloMedicamento(frValidacion.getCodArticulo(),
					ticketManager.getTicket().getCabecera().getDatosFidelizado(), false);
		} catch (LineaTicketException e) {
			log.error(e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(e.getMessageI18N(), this.getScene().getWindow());
			return false;
		}

		Set<ConstraintViolation<FormularioLineaArticuloBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(this.frValidacion, new Class[0]);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioLineaArticuloBean> next = (ConstraintViolation)constraintViolations.iterator().next();
			this.frValidacion.setErrorStyle(next.getPropertyPath(), true);
			this.frValidacion.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaError(next.getMessage(), this.getScene().getWindow());
			return false;
		} else {
			BigDecimal cantidad = this.frValidacion.getCantidadAsBigDecimal();
			if (cantidad == null) {
				return false;
			} else {
				BigDecimal max = new BigDecimal(50);
				if (BigDecimalUtil.isMayor(this.frValidacion.getCantidadAsBigDecimal(), max)) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La cantidad debe ser menor que {0}", new Object[]{FormatUtil.getInstance().formateaNumero(max)}), this.getStage());
					return false;
				} else {
					return true;
				}
			}
		}
    }

	@Component
	@Primary
	@Scope("prototype")
	public class IskaypetNuevoCodigoArticuloTask extends NuevoCodigoArticuloTask {
		public IskaypetNuevoCodigoArticuloTask(String codArticulo, BigDecimal cantidad) {
			super(codArticulo, cantidad);
			mostrarVentanaCargando = true;
		}
	}

	protected void abrirPrevisualizacion() {
		previsualizacion();
	}

	protected Boolean previsualizacion() {

		if (ticketManager.isTicketVacio()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede previsualizar un ticket vacío"), getStage());
			return false;
		}

		HashMap<String, Object> datos = new HashMap<>();
		datos.put(PrevisualizacionTicketController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(PrevisualizacionTicketView.class, datos, getStage());
		return !(Boolean) datos.getOrDefault(PrevisualizacionTicketController.ACCION_CANCELAR, false);
	}

	@Override
    public void seeCustomerCoupons(){
        try{
            HashMap<String, Object> params = new HashMap<String, Object>();

            // Cupones que tiene el fidelizado.
            FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();

            IskaypetFidelizacion iskaypetFidelizacion = new IskaypetFidelizacion();
            iskaypetFidelizacion.getCustomerCoupons(fidelizado);

            List<CustomerCouponDTO> listCustomerCoupons = ticketManager.getTicket().getCabecera().getDatosFidelizado().getAvailableCoupons();
            params.put(CustomerCouponsController.PARAM_CUSTOMER_COUPONS, listCustomerCoupons);

            // Cupones que tiene activos el fidelizado.
            List<CustomerCouponDTO> listActiveCustomerCoupon = new ArrayList<CustomerCouponDTO>();
            List<CuponAplicadoTicket> listActiveTicketCoupon = ticketManager.getTicket().getCuponesAplicados();
            for(CuponAplicadoTicket activeTicketCoupon : listActiveTicketCoupon){
                String couponCodeActiveTicketCoupon = activeTicketCoupon.getCodigo();
                for(CustomerCouponDTO customerCoupon : listCustomerCoupons){
                    String couponCodeCustomerCoupon = customerCoupon.getCouponCode();
                    if(couponCodeActiveTicketCoupon.equalsIgnoreCase(couponCodeCustomerCoupon)){
                        listActiveCustomerCoupon.add(customerCoupon);
                        break;
                    }
                }
            }
            params.put(CustomerCouponsController.PARAM_ACTIVE_COUPONS, listActiveCustomerCoupon);

            getApplication().getMainView().showModal(CustomerCouponsView.class, params, getStage());

            List<CustomerCouponDTO> coupons = (List<CustomerCouponDTO>) params.get(CustomerCouponsController.PARAM_CUSTOMER_COUPONS);
            ticketManager.getTicket().getCabecera().getDatosFidelizado().setActiveCoupons(coupons);

            //CZZ-490
            aplicarCupones(coupons);
        }
        catch(Exception e){
            log.error("seeCustomerCoupons() - ERROR: " + e.getMessage(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al mostrar la pantalla de cupones del fidelizado. Por favor, contacte con el administrador."), e);
        }
    }

	/**
	 * CZZ-490 - Aplica una lista de cupones al ticket actual. Muestra una ventana de información si el cupón se aplica correctamenteo una ventana de error si ocurre una excepción.
	 */
	private void aplicarCupones(List<CustomerCouponDTO> coupons) throws CuponesServiceException, CuponAplicationException, LineaTicketException {
		for (CustomerCouponDTO cupon : coupons) {
			try {
				TicketVentaAbono ticket = (TicketVentaAbono) ticketManager.getTicket();
				if (!sesionPromociones.aplicarCupon(cupon, ticket)) {
					throw new LineaTicketException(I18N.getTexto("No se ha podido aplicar el cupón."));
				}

				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Se ha podido aplicar un cupón."), getStage());

				ticket.getTotales().recalcular();
			} catch (CuponUseException |  CuponesServiceException | CuponAplicationException e) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
			}
		}
		refrescarDatosPantalla();
	}




	@Override
	public void abrirCajon() {
		try {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
			AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
			super.abrirCajon();
		} catch (InitializeGuiException initializeGuiException) {
			if (initializeGuiException.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), initializeGuiException);
			}
		}
	}

	/* ######################################################################################################################### */
	/* ###################### GAP 172 TRAZABILIDAD ANIMALES #################################################################### */
	/* ######################################################################################################################### */

	public void validarTrazabilidadMascotas(List<LineaTicket> listLines) throws LineaTicketException {
		//Todas las lineas son iguales en iskaypet, con una de la cantidad n introducidad nos vale
		// Verificamos si la lista de lineas es nula o vacía, en cuyo caso no hacemos nada
		if (listLines == null || listLines.isEmpty() || !(listLines.get(0) instanceof IskaypetLineaTicket) ||
				!((IskaypetLineaTicket) listLines.get(0)).isMascota()) {
			return;
		}

		IskaypetLineaTicket lineaTicket = (IskaypetLineaTicket) listLines.get(0);
		if (trazabilidadMascotasService.requiereClienteIdentificado(lineaTicket) && !((IskaypetTicketManager) ticketManager).isFidelizadoSeleccionado()) {
			trazabilidadMascotasService.eliminarLineasTicket(listLines, ticketManager, AsignarTrazabilidadController.MENSAJE_ERROR_NECESITA_CLIENTE);
		}

		// Comprobamos la configuración de región y la trazabilidad disponible en la tienda
		trazabilidadMascotasService.validarConfiguracion(listLines, ticketManager);

		List<LineaTicket> lineasControlTrazabilidad = trazabilidadMascotasService.consultarLineasRequierenTrazabilidad(listLines);

		try {
			//Consultamos los detalles de la mascota
			DetailPets details = trazabilidadMascotasService.consultarTrazabilidad(((IskaypetLineaTicket) listLines.get(0)));

			// Si no hay detalles o no requiere ningun identificador, no hacemos nada, o si no hay lineas que requieran trazabilidad
			if (details == null || (!details.getIdChipAsBoolean() && !details.getIdAnillaAsBoolean() && !details.getIdCitesAsBoolean()) || lineasControlTrazabilidad.isEmpty()) {
				return;
			}

			// Comprobamos que al validar la linea, requiera trazabilidad y abirmos pantalla de requerirla
			// Todas las lineas estan agupadas, con la primera nos vale
			IskaypetLineaTicket lineaControl = (IskaypetLineaTicket) lineasControlTrazabilidad.get(0);
			if (lineaControl.isRequiereIdentificacion()) {
				// Si la lista de medicamentos no está vacía, seguimos con el proceso
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(PARAM_DETAILS_PETS, details);
				map.put(AsignarTrazabilidadController.CLAVE_PARAMETRO_LISTA_LINEAS, lineasControlTrazabilidad);
				map.put(AsignarTrazabilidadController.CLAVE_TICKET_MANAGER, ticketManager);
				getApplication().getMainView().showModalCentered(AsignarTrazabilidadView.class, map, this.getStage());
			}
		} catch (Exception e) {
			throw new LineaTicketException(I18N.getTexto(AsignarTrazabilidadController.MENSAJE_ERROR_MASCOTA_DETALLE), e);
		}
	}

	@Override
	public void aparcarTicket() {
		log.trace("aparcarTicket() - Aparcando ticket");

		if (ticketManager.isTicketVacio()) {
			log.debug("aparcarTicket() - El ticket no contiene líneas de artículo.");
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), getStage());
			return;
		}

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede aparcar un ticket de una proforma, anule el ticket"), getStage());
			return;
		}

		try {
			log.debug("accionAparcarTicket()");

			// Comprobamos si el ticket no ha sido aparcado ya, y registramos la información necesaria para el ticket aparcado
			ICabeceraTicket cabeceraTicket = ticketManager.getTicket().getCabecera();
			if (cabeceraTicket instanceof IskaypetCabeceraTicket && ((IskaypetCabeceraTicket) cabeceraTicket).getTicketAparcado() == null) {
				TicketAparcadoXmlBean ticketAparcado = new TicketAparcadoXmlBean();
				ticketAparcado.setCaja(sesion.getAplicacion().getCodCaja());
				ticketAparcado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
				ticketAparcado.setFecha(new Date());
				((IskaypetCabeceraTicket) cabeceraTicket).setTicketAparcado(ticketAparcado);
			}

			copiaSeguridadTicketService.guardarBackupTicketActivo(new TicketVentaAbono());
			ticketManager.aparcarTicket();
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El ticket ha sido aparcado."), getStage());

			// Inicializamos un nuevo ticket
			getView().loadAndInitialize();
			actualizarTicketsAparcados();
			guardarCopiaSeguridad();

			imgCancelar.setVisible(false);

			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();

			log.debug("accionAparcarTicket() - Ticket aparcado");
		} catch (TicketsServiceException var3) {
			TicketsServiceException ex = var3;
			log.error("accionAparcarTicket()");
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessageDefault(), ex);
		} catch (InitializeGuiException var2) {
			InitializeGuiException e = var2;
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
    }

	@Override
	protected void actualizarTicketsAparcados() {
		if (ticketManager instanceof IskaypetTicketManager) {
			int tiques = ((IskaypetTicketManager) ticketManager).countTicketsAparcadosTotales();
			if (tiques > 0) {
				lbStatusTicketsAparcados.setText(I18N.getTexto("Hay tickets aparcados") + " (" + tiques + ")");
				lbimagenTicketsAparcados.setVisible(true);
			} else {
				lbStatusTicketsAparcados.setText(I18N.getTexto("No hay tickets aparcados"));
				lbimagenTicketsAparcados.setVisible(false);
			}
		} else {
			super.actualizarTicketsAparcados();
		}
	}

	/* ################################################################################################################ */
	/* ######################################### CZZ - 1542 PROFORMAS ################################################# */
	/* ################################################################################################################ */

	@FXML
	public void busquedaSeleccionProformas() {
		log.info("busquedaSeleccionProformas() - Acción ejecutada");

		if (!ticketManager.getTicket().getLineas().isEmpty()) {
			String mensaje = I18N.getTexto("No se puede cargar una proforma si hay líneas en el ticket");
			log.warn("busquedaSeleccionProformas() - " + mensaje);
			VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
			return;
		}

		try {
			List<ProformaHeaderDTO> listaProformas = proformaRestService.listarProformas(sesion, ProformaRestService.TIPO_VENTA);

			if (listaProformas == null || listaProformas.isEmpty()) {
				log.warn("busquedaSeleccionProformas() - No se han encontrado proformas");
				VentanaDialogoComponent.crearVentanaConfirmacionUnBoton(I18N.getTexto("No se han encontrado proformas"), getStage());
				return;
			}

			ProformaDTO proforma = null;
			if (listaProformas.size() == 1) {
				log.debug("busquedaSeleccionProformas() - Se ha encontrado una única proforma, se selecciona automáticamente");
				ProformaHeaderDTO proformaSeleccionada = listaProformas.get(0);
				proforma = proformaRestService.obtenerProformaCompleta(sesion, proformaSeleccionada.getIdProforma());
			} else {
				log.debug("busquedaSeleccionProformas() - Se han encontrado varias proformas, se muestra la ventana de selección");
				getDatos().put(PARAM_PROFORMAS_SELECCION, listaProformas);
				getApplication().getMainView().showModalCentered(SeleccionProformaView.class, getDatos(), getStage());
				proforma = (ProformaDTO) getDatos().getOrDefault(PARAM_PROFORMA_SELECCIONADA, null);
			}
			log.info("busquedaSeleccionProformas() - Proforma seleccionada: " + proforma);

			if (proforma == null) {
				log.warn("busquedaSeleccionProformas() - No se seleccionó una proforma válida");
				return;
			}

			// Seteamos el fidelizado a null antes de generar la proforma
			fidelizado = null;

			if (ticketManager instanceof IskaypetTicketManager) {
				log.debug("busquedaSeleccionProformas() - Generando ticket desde proforma");
				((IskaypetTicketManager) ticketManager).generarTicketDesdeProforma(proforma, getStage());
				log.debug("busquedaSeleccionProformas() - Ticket generado correctamente");
			}
			refrescarDatosPantalla();
			log.debug("busquedaSeleccionProformas() - Proforma cargada correctamente");
		} catch (Exception e) {
			log.error("busquedaSeleccionProformas() - Error al obtener proformas: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Error al obtener proformas"), e);
		}
	}

	@Override
	public void abrirVentanaBusquedaArticulos() {
		if (ticketManager instanceof IskaypetTicketManager) {
			getDatos().put(PARAM_PROFORMA_SELECCIONADA, ((IskaypetTicketManager) ticketManager).esTicketProforma());
		}
		super.abrirVentanaBusquedaArticulos();
	}

	private String getTipoEstablecimiento() {
		try {
			DatosSesionBean datosSesion = new DatosSesionBean();
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			ValorParametroObjeto parametro = valoresParametrosObjetosService.consultar("D_ALMACENES_TBL.CODALM", sesion.getAplicacion().getCodAlmacen(), "TIPO_ESTABLECIMIENTO", datosSesion);
			log.debug("getTipoEstablecimiento() - Tipo de establecimiento: " + parametro.getValor());
			return parametro.getValor();
		} catch (Exception e) {
			log.error("getTipoEstablecimiento() - Error al consultar el tipo de establecimiento: " + e.getMessage(), e);
			return null;
	 	}
	}

	public boolean procesarLineasInyectable() {
		log.debug("procesarLineasInyectable() - Se comprueba si hay líneas inyectables en el ticket, y se procesan");
		List<LineaTicket> lineas = ticketManager.getTicket().getLineas();
		Map<String, List<LineaTicket>> mapLineasSinInyectar = inyectableArticuloManager.getLineasInyectablesNoInsertadas(lineas);
		if (mapLineasSinInyectar.isEmpty()) {
			log.debug("procesarLineasInyectable() - No hay líneas inyectables en el ticket");
			return true;
		}

		VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Hay artículos inyectables sin información asignada."), getStage());
		for (Map.Entry<String, List<LineaTicket>> entry : mapLineasSinInyectar.entrySet()) {
			String codArtLinea = entry.getKey();
			List<LineaTicket> lineasInyectables = entry.getValue();
			log.debug("procesarLineasInyectable() - Se procesan las líneas inyectables del artículo: " + codArtLinea);
			boolean procesado = procesarLineasInyectable(lineasInyectables);
			log.debug("procesarLineasInyectable() - Se han procesado las líneas inyectables del artículo: " + codArtLinea + " - Resultado: " + procesado);
			return procesado;
		}
		return true;
	}

	public boolean procesarLineasInyectable(String codArtLinea, List<LineaTicket> lineas) {
		log.debug("procesarLineasInyectable() - Se comprueba si el artículo es inyectable");
		boolean esInyectable = inyectableArticuloManager.esArticuloInyectable(codArtLinea);
		if (esInyectable) {
			log.debug("procesarLineasInyectable() - Se abre la pantalla de inyectables");
			boolean procesado = procesarLineasInyectable(lineas);
			log.debug("procesarLineasInyectable() - Se cierra la pantalla de inyectables");
			return procesado;
		}
		return true;
	}

	public boolean procesarLineasInyectable(List<LineaTicket> lineas) {
		log.debug("procesarLineasInyectable() - Se abre la pantalla de inyectables");
		getDatos().put(InyectableController.CLAVE_PARAMETRO_LISTA_LINEAS, lineas);
		getDatos().put(InyectableController.CLAVE_TICKET_MANAGER, ticketManager);
		getApplication().getMainView().showModalCentered(InyectableView.class, getDatos(), this.getStage());
		boolean procesado = !(boolean) getDatos().getOrDefault(InyectableController.CLAVE_CANCELAR, false);
		log.debug("procesarLineasInyectable() - Se cierra la pantalla de inyectables, resultado: " + procesado);
		return procesado;
	}

	@FXML
	public void accionQuitarFidelizado() throws PromocionesServiceException, ArticuloNotFoundException, LineaTicketException {

		if(!VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Desea quitar el fidelizado?"), getStage())){
			return;
		}

		ticketManager.getTicket().getCabecera().setDatosFidelizado((String) null);
		fidelizado = null;

		resetearLineasFidelizado(new ArrayList<>(lineas));

		guardarCopiaSeguridad();
		refrescarDatosPantalla();
	}

	private void resetearLineasFidelizado(List<LineaTicketGui> lineasTicket){
		for (LineaTicketGui linea : lineasTicket) {
			tbLineas.getSelectionModel().select(linea);
			if (linea.isCupon()) {
				accionTablaEliminarRegistro();
			} else {
				LineaTicket lineaArticulo = (LineaTicket) ticketManager.getTicket().getLinea(linea.getIdLinea());
				ArticlesPointsXMLBean puntosLinea = ((IskaypetLineaTicket) lineaArticulo).getArticlePoints();

				if(puntosLinea != null && (puntosLinea.getPoints().compareTo(BigDecimal.ZERO) == 0
						|| puntosLinea.VALUE_REEDEM_OK.equalsIgnoreCase(puntosLinea.getReedem()))) {

					articlePointsManager.processLineArticlePoints(getStage(),
							(TicketVentaAbono) ticketManager.getTicket(), lineaArticulo,
							ArticlesPointsManager.ACTION_DELETE, true);

					articlePointsManager.processLineArticlePoints(getStage(),
							(TicketVentaAbono) ticketManager.getTicket(), lineaArticulo,
							ArticlesPointsManager.ACTION_INSERT, true);

					((IskaypetLineaTicket) lineaArticulo).getArticlePoints().setReedem(ArticlesPointsXMLBean.VALUE_REEDEM_KO);
					lineaArticulo.setPrecioTotalSinDto(lineaArticulo.getPrecioTotalTarifaOrigen());
					lineaArticulo.setAdmitePromociones(true);
				}
			}
		}
	}

}
