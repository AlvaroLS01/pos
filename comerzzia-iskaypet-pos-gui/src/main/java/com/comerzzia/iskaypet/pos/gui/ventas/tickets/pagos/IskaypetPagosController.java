package com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos;

import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.servicios.variables.Variables;
import com.comerzzia.core.util.tipoidentificacion.IValidadorDocumentoIdentificacion;
import com.comerzzia.iskaypet.librerias.sipay.client.constants.SipayConstants;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.IskaypetFidelizadoRest;
import com.comerzzia.iskaypet.pos.api.rest.client.fidelizados.response.IskaypetResponseGetTarjetaRegaloRest;
import com.comerzzia.iskaypet.pos.devices.IskaypetFidelizacion;
import com.comerzzia.iskaypet.pos.devices.comun.tarjeta.IskaypetCodigoTarjetaController;
import com.comerzzia.iskaypet.pos.devices.tarjeta.sipay.TefSipayManager;
import com.comerzzia.iskaypet.pos.gui.autorizacion.AutorizacionGerenteUtils;
import com.comerzzia.iskaypet.pos.gui.ventas.plataformadigital.DeliveryManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.IskaypetTicketManager;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.EmailConstants;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.confirmacion.EmailConfirmacionView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.email.seleccion.EmailSeleccionView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.nif.PagoNifPtView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.observaciones.PagoObservacionesView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.origen.PagosOrigenView;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.valedevolucionmanual.ValeDevolucionManualController;
import com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.valedevolucionmanual.ValeDevolucionManualView;
import com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados.TicketContratosBean;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.AcumuladosPromo;
import com.comerzzia.iskaypet.pos.persistence.ticket.lineas.DtoPromocion;
import com.comerzzia.iskaypet.pos.persistence.valesdevolucion.ValeDevolucion;
import com.comerzzia.iskaypet.pos.services.cuponespuntos.CuponesPuntosService;
import com.comerzzia.iskaypet.pos.services.evicertia.IskaypetEvicertiaService;
import com.comerzzia.iskaypet.pos.services.payments.methods.types.ValeDevManager;
import com.comerzzia.iskaypet.pos.services.proformas.rest.ProformaRestService;
import com.comerzzia.iskaypet.pos.services.promotionsticker.PromotionStickerService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketService;
import com.comerzzia.iskaypet.pos.services.ticket.IskaypetTicketVentaAbono;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.IskaypetCabeceraTicket;
import com.comerzzia.iskaypet.pos.services.ticket.cabecera.adicionales.DatosOrigenTicketBean;
import com.comerzzia.iskaypet.pos.services.ticket.contrato.trazabilidad.TrazabilidadMascotasService;
import com.comerzzia.iskaypet.pos.services.ticket.cupones.IskaypetCuponEmitidoTicket;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.DtoPromocionesService;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.services.valesdevolucion.ValesDevolucionService;
import com.comerzzia.iskaypet.pos.services.valesdevolucion.exception.ValesDevolucionNotFoundException;
import com.comerzzia.iskaypet.pos.services.valesdevolucion.exception.ValesFormatExcepcion;
import com.comerzzia.iskaypet.pos.services.ventas.pagos.mascotas.ServicioContratoMascotas;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.importe.BotonBotoneraImagenValorComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.LineaPanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaController;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.FormularioImportePagosBean;
import com.comerzzia.pos.gui.ventas.tickets.pagos.NoCerrarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos.VentanaCambioView;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentsOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentsErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentsCompletedEvent;
import com.comerzzia.pos.services.payments.events.PaymentsSelectEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.GiftCardManager;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.cabecera.DatosDocumentoOrigenTicket;
import com.comerzzia.pos.services.ticket.cabecera.ICabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.SubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.comerzzia.iskaypet.pos.gui.ventas.cajas.contadora.ContadoraTipoPagosEnum.*;
import static com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.detalle.IskaypetDetalleGestionticketsController.*;
import static com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.cambioPagos.IskaypetVentanaCambioController.ACCION_CONFIRMAR;
import static com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.nif.PagoNifPtController.*;
import static com.comerzzia.iskaypet.pos.gui.ventas.tickets.pagos.observaciones.PagoObservacionesController.PAGO_OBSERVACIONES;
import static com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos.VentanaCambioController.*;


/**
 * GAP 107 - ISK-262 GLOVO (venta por plataforma digital)
 */
@Component
@Primary
@SuppressWarnings({ "unchecked", "rawtypes" })
public class IskaypetPagosController extends PagosController {

        protected Logger log = Logger.getLogger(getClass());

       private PaymentErrorEvent ultimoErrorPago;

	public static final String MEDIOS_PAGOS_SIN_AUTORIZACION = "X_POS.MEDIOS_PAGOS_SIN_AUTORIZACION";
	public static final String MEDIOS_PAGOS_MOSTRAR_CAMBIO = "X_POS.MEDIOS_PAGOS_MOSTRAR_CAMBIO";
	public static final String MEDIOS_PAGOS_PERMITEN_CAMBIO = "X_POS.MEDIOS_PAGOS_PERMITEN_CAMBIO";
	public static final String MEDIOS_PAGOS_DEVOLUCION_EFECTIVO = "X_POS.MEDIOS_PAGOS_DEVOLUCION_EFECTIVO";
	public static final String MEDIOS_PAGOS_DEVOLUCION_MISMO_MEDIO = "X_POS.MEDIOS_PAGOS_DEVOLUCION_MISMO_MEDIO";
	public static final String MEDIOS_PAGOS_DEVOLUCION_SUPERAR_ORIGEN = "X_POS.MEDIOS_PAGOS_DEVOLUCION_SUPERAR_ORIGEN";
	public static final String MEDIOS_PAGOS_PERMITEN_PAGO_MIXTO = "X_POS.MEDIOS_PAGOS_PERMITEN_PAGO_MIXTO";
	public static final String MEDIOS_PAGOS_PERMITEN_DEVO_MIXTA = "X_POS.MEDIOS_PAGOS_PERMITEN_DEVO_MIXTA";
	public static final String ENVIO_EMAIL_VARIABLE = "X.ENVIO_EMAIL_TICKET";
	public static final String PEDIR_VALE_MANUAL = "X_POS.PEDIR_VALE_MANUAL";
    public static final String PARAMETRO_OBSERVACIONES_VACIO = "Vacío";
    public static final String ACCION_BUSQUEDA_CLIENTE = "BUSQUEDA_CLIENTE";
    public static final String QR_TEMPORAL = "QR_TEMPORAL";

	public static final String BOTON_CLAVE_SELECCIONAR_MEDIO_PAGO = "seleccionarMedioPago";
	public static final String BOTON_CLAVE_TOOGLE_MEDIO_PAGO_OTROS = "toggleBotonMedioPagoOtros";
	public static final String BOTON_PARAMETRO_MEDIO_PAGO = "codMedioPago";
	public static final String BOTON_PARAMETRO_PLATAFORMA_DIGITAL = "PLATAFORMA_DIGITAL";
	public static final String BOTON_PARAMETRO_TIPO_PAGO = "TIPO_PAGO";
	public static final String RUTA_IMAGEN_OTROS = "iconos/otros.png";
	public static final String RUTA_IMAGEN_OTROS_CERRAR = "iconos/otros-cerrar.png";

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;

	final IVisor visor = Dispositivos.getInstance().getVisor();

	@Autowired
	private MediosPagosService mediosPagosService;
	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private TiposIdentService tiposIdentService;

	@Autowired
	private IskaypetEvicertiaService evicertiaService;
	@Autowired
	private IskaypetTicketService iskaypetTicketService;

	@Autowired
	protected ServicioContratoMascotas servicioContratoMascotas;

	//GAP 169 DEVOLUCIÓN DE PROMOCIONES
	@Autowired
	private CuponesPuntosService cuponesPuntosService;

	@Autowired
	protected PromocionesService promocionesService;

	@FXML
	private Button btCambio;

	@FXML
	private Label lbObservaciones;
	@FXML
	private Label lbObservacionesDetalle;
	@FXML
	private Button btEditar;
	@FXML
	private Button btEliminar;

	@FXML
	private AnchorPane panelNIF;
	@FXML
	private Label lbNifDetalle;
	@FXML
	private Button btEditarNif;
	@FXML
	private Button btEliminarNif;

	protected TipoDocumentoBean documentoActivoOriginal;

	@Autowired
	private DeliveryManager deliveryManager;
	protected BotonBotoneraComponent botonPagoDelivery;
	/**
	 * medpag del colectivo de delivery seleccionado del fidelizado, null en cualquier otro caso
	 */
	protected MedioPagoBean medioPagoDelivery;

	protected String accionSeleccionada;

	protected Boolean esVistaInicialOtroMedioPago;

	@Autowired
	protected DtoPromocionesService dtoPromocionesService;

	//GAP 176 vales devolucion
	@Autowired
	protected ValesDevolucionService valesDevolucionService;

	//GAP 172 TRAZABILIDAD ANIMALES
	@Autowired
	protected TrazabilidadMascotasService trazabilidadMascotasService;

	@Autowired
	protected TefSipayManager tefSipayManager;

	@Autowired
	protected ProformaRestService proformaRestService;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);
		mostrarFormaDeCambio(false);
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		lbObservaciones.setVisible(false);
		btEditar.setVisible(false);
		btEliminar.setVisible(false);
		lbObservacionesDetalle.setText("");
		lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
		lbDocActivo.setText(I18N.getTexto(ticketManager.getDocumentoActivo().getDestipodocumento()));
		documentoActivoOriginal = ticketManager.getDocumentoActivo();
		accionSeleccionada = "";

		// ISK-262 GAP-107 GLOVO - Se sobreescribe comportamiento estándar de
		// selección de primer medpag por delivery en caso de ser venta delivery
		if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery()) {
			medioPagoSeleccionado = medioPagoDelivery;
			lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
		}

		if (((IskaypetTicketManager) ticketManager).isTicketVentaIntercompany()) {
			medioPagoSeleccionado =  MediosPagosService.mediosPago.get(TRANSFERENCIA.getCodPago());
			lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
			toggleBotonMedioPagoOtros();
		}

        //GAP 176 Vales devolucion
        // Eliminamos los que lleven guardados más de una hora
        try {
            valesDevolucionService.eliminarValesDevExpirados();
        } catch (Exception e) {
            log.error("initializeForm() - No se han podido borrar los vales de devolución con más de una hora... " + e.getMessage(), e);
        }
        // Validar posibles pagos pendientes en Sipay.
		validatePendigPaymentSipay();
	}

	@Override
	public void initializeFocus() {
		super.initializeFocus();

		// ISK-262 GAP-107 GLOVO - Se cambia label de botón de medio de pago DESPUÉS DE inicializar botones
		if (botonPagoDelivery != null) {
			Label lbTextoBoton = ((Label) ((HBox) ((AnchorPane) botonPagoDelivery.getBtAccion().getChildrenUnmodifiable().get(0)).getChildren().get(1)).getChildren().get(0));
			lbTextoBoton.setText(medioPagoDelivery.getDesMedioPago());

			try {
				ImageView ivBoton = ((ImageView) ((HBox) ((AnchorPane) botonPagoDelivery.getBtAccion().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(0));
				Image imgMedPag = POSApplication.getInstance().createImage("iconos/medpag-" + medioPagoDelivery.getDesMedioPago() + ".png");
				if (imgMedPag == null) {
					imgMedPag = POSApplication.getInstance().createImage("iconos/pago-online.png");
				}
				ivBoton.setImage(imgMedPag);
			}
			catch (Exception e) {
				log.debug("initializeFocus() - Error al cargar imagen: " + e.getMessage(), e);
			}

		}

		if (AppConfig.pais.equalsIgnoreCase(PAGO_COD_PAIS_PT)) {

			// Valores por defecto
			lbNifDetalle.setText(PAGO_NIF_DOC_DEFAULT);
			panelNIF.setVisible(true);
			btEditarNif.setDisable(false);
			btEliminarNif.setDisable(false);

			// Una vez cargado el form y el focus, se comprueba si se debe mostrar el NIF en el panel
			boolean esDevolucionSinOrigen = isDevolucion() && ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;
			if (!ticketManager.isEsDevolucion() || esDevolucionSinOrigen) {
				modificarNifCabecera(null);
				adicionarNIF();
			}
			else if (ticketManager.isEsDevolucion()) {

				// En devoluciones con doc origen no se puede modificar el NIF
				btEditarNif.setDisable(true);
				btEliminarNif.setDisable(true);

				String documento = ((IskaypetCabeceraTicket) ticketManager.getTicketOrigen().getCabecera()).getDocumentoCliente();
				if (StringUtils.isBlank(documento) && ticketManager.getTicketOrigen().getDatosFacturacion() != null) {
					documento = ticketManager.getTicketOrigen().getDatosFacturacion().getCif();
				}

				if (StringUtils.isNotBlank(documento)) {
					modificarNifCabecera(documento);
					lbNifDetalle.setText(documento);
				}
			}

		}

	}

	private void mostrarFormaDeCambio(boolean value) {
		// Se setea visibilidad de los paneles de forma de cambio y su botonera
		ObservableList<Node> list = btCambio.getParent().getParent().getChildrenUnmodifiable();
		list.forEach(el -> {
			if (el instanceof FlowPane) {
				el.setVisible(value);
			}
		});
	}

	/*
	 * GAP 63 - DEVOLUCIÓN SIN DOC ORIGEN Con doc origen -> Se deshabilitan medios de pago no presentes en el ticket
	 * original Sin doc origen -> Únicamente se habilita efectivo //TODO BCR tocar aquí para des/habilitar vale como medio
	 * de pago
	 */
	@Override
	protected void comprobarConfiguracionBotoneraPago(AnchorPane panel) {
		ObservableList<Node> children = panel.getChildren();

		AtomicBoolean medioPagoEstaSeleccionado = new AtomicBoolean(false);

		if (children != null && !children.isEmpty()) {

			// Se inicializa la variable que indica si la vista es la inicial para los botones de pago otros
			esVistaInicialOtroMedioPago = true;

			children.forEach(botonera -> {
				if (botonera instanceof BotoneraComponent) {
					Collection<BotonBotoneraComponent> lstBotones = ((BotoneraComponent) botonera).getMapConfiguracionesBotones().values();

					for (BotonBotoneraComponent boton : lstBotones) {
						String codMedioPago = MediosPagosService.medioPagoDefecto.getCodMedioPago();
						String codColectivoDelivery = null;
						String tipoPago = null;
						boolean esPagoPlataformaDigital = false;

						if (!panel.equals(panelPagoEfectivo)) {

							if (StringUtils.isNotBlank(boton.getClave()) && boton.getClave().equals(BOTON_CLAVE_SELECCIONAR_MEDIO_PAGO)) {
								String codMedioPagoParam = (String) boton.getParametro(BOTON_PARAMETRO_MEDIO_PAGO);
								String plataformaDigital = (String) boton.getParametro(BOTON_PARAMETRO_PLATAFORMA_DIGITAL);
								tipoPago = (String) boton.getParametro(BOTON_PARAMETRO_TIPO_PAGO);

								// ISK-262 GAP-107 GLOVO - En caso de ser venta delivery se selecciona el medpag asignado a delivery
								// Este medpagtiene el parámetro PLATAFORMA_DIGITAL == S en xml
								if ("S".equals(plataformaDigital) && !ticketManager.isEsDevolucion()) {
									esPagoPlataformaDigital = true;
									codColectivoDelivery = ((IskaypetTicketManager) ticketManager).getCodDelivery();
								}
								else if (StringUtils.isNotBlank(codMedioPagoParam)) {
									codMedioPago = codMedioPagoParam;
								}
							}

							if (boton instanceof BotonBotoneraTextoComponent) {
								codMedioPago = ((BotonBotoneraTextoComponent) boton).getMedioPago().getCodMedioPago();
							}

							cambiarVisibilidadBotonPagoOtros(codMedioPago, tipoPago, boton);

						}

						// ISK-262 GAP-107 GLOVO - En ventas delivery se activa el medpag de delivery si existe
						if (esPagoPlataformaDigital) {
							boolean tieneCodColectivo = StringUtils.isNotBlank(codColectivoDelivery);
							String textoBoton = boton.getConfiguracionBoton().getTexto();

							if (tieneCodColectivo && codColectivoDelivery.equals(textoBoton)) {
								boton.setDisable(false);
								boton.setVisible(true);

								medioPagoDelivery = MediosPagosService.mediosPago.get(deliveryManager.getCodMedPagDeColectivo(codColectivoDelivery));
								boton.getConfiguracionBoton().setParametro(BOTON_PARAMETRO_MEDIO_PAGO, medioPagoDelivery.getCodMedioPago());

								botonPagoDelivery = boton;
							} else {
								boton.setDisable(true);
								boton.setVisible(false);
							}
						}


						// Se deshabilitan medios de pago no presentes para su uso según la configuración
						if ((isDevolucion() && !paymentsManager.isPaymentMethodAvailableForReturn(codMedioPago)) || (!isDevolucion() && !paymentsManager.isPaymentMethodAvailable(codMedioPago))) {
							boton.setDisable(true);
						}
						else {
							boton.setDisable(false);
						}

						// Se quitan visibilidad a medios de pago no activos para su uso según la configuración
						MedioPagoBean medioPago = MediosPagosService.mediosPago.get(codMedioPago);
						if (medioPago == null || !medioPago.getActivo()) {
							boton.setVisible(false);
						}

						// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
						if (isDevolucion()) {
							boolean esDevolucionSinDocOrigen = ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;

							String codMedPagVale = "";
							for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
								if (StringUtils.isNotBlank(configuration.getControlClass()) && configuration.getControlClass().equals("valeDevManager")) {
									codMedPagVale = configuration.getPaymentCode();
								}
							}

							boolean esVentaPlanesSalud = contienePlanesSalud();

							if (esDevolucionSinDocOrigen) {
								// En devoluciones libres solo se permite efectivo y vale y en planes de salud solo transferencia
								if (esVentaPlanesSalud && TRANSFERENCIA.getCodPago().equals(codMedioPago)) {
									seleccionarMedioPagoPorDefecto(codMedioPago, boton, false);
								} else if (!esVentaPlanesSalud && EFECTIVO.getCodPago().equals(codMedioPago)) {
									seleccionarMedioPagoPorDefecto(codMedioPago, boton, false);
								} else if (!esVentaPlanesSalud && codMedioPago.equals(codMedPagVale)) {
									boton.setDisable(false);
									log.debug("comprobarConfiguracionBotoneraPago() - Se selecciona vale de devolución como medio de pago");
								} else {
									boton.setDisable(true);
									log.debug("comprobarConfiguracionBotoneraPago() - Se deshabilita medio de pago: " + codMedioPago);
								}
							} else if (((IskaypetTicketManager) ticketManager).isDevolucionDelivery()) {
								if (EFECTIVO.getCodPago().equals(codMedioPago)
										|| boton.getConfiguracionBoton().getTexto().equals(((IskaypetCabeceraTicket) ticketManager.getTicketOrigen().getCabecera()).getDelivery())) {
									boton.setDisable(false);
									boton.setVisible(true);
									medioPagoSeleccionado = MediosPagosService.mediosPago.get(codMedioPago);
									lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
								}
								else {
									boton.setDisable(true);
								}
							} else if (!esVentaPlanesSalud && comprobarExistenciaMediosPagoDevolucionEfectivo(ticketManager.getTicketOrigen().getPagos())) {
								// Se comprueba si existen medios de pago en el ticket origen que solo permiten devolución en efectivo
                                if(EFECTIVO.getCodPago().equals(codMedioPago)){
                                    boton.setDisable(false);
                                    medioPagoSeleccionado = MediosPagosService.mediosPago.get(codMedioPago);
                                } else {
                                    boton.setDisable(true);
                                }
                                if (medioPagoSeleccionado != null && comprobarDevolucionMismoMedio(codMedioPago)) {
                                    boton.setDisable(false);
                                    seleccionarPrimerMedioDePagoDelTicket(medioPagoEstaSeleccionado, codMedioPago);
                                }
                            } else if (!esVentaPlanesSalud
                                    && !comprobarExistenciaMediosPagoDevolucionEfectivo(ticketManager.getTicketOrigen().getPagos())
                                    && comprobarExistenciaDevolucionMismoMedio(ticketManager.getTicketOrigen().getPagos())
                                    && comprobarDevolucionMismoMedio(codMedioPago)) {
                                boton.setDisable(false);
                                seleccionarPrimerMedioDePagoDelTicket(medioPagoEstaSeleccionado, codMedioPago);

                            } else if (esVentaPlanesSalud) {
								log.debug("comprobarConfiguracionBotoneraPago() - En devoluciones de planes de salud solo se permite transferencia");
								// En devoluciones de planes de salud solo se permite transferencia
								if (TRANSFERENCIA.getCodPago().equals(codMedioPago)) {
									log.debug("comprobarConfiguracionBotoneraPago() - Se selecciona transferencia como medio de pago");
									seleccionarMedioPagoPorDefecto(codMedioPago, boton, false);
								} else {
									log.debug("comprobarConfiguracionBotoneraPago() - Se deshabilita medio de pago: " + codMedioPago);
									boton.setDisable(true);
								}
							} else {
								// En devoluciones con doc origen solo se permiten los medios de pago usados en ticket origen y vale
								Set<String> mediosPagoTicketOrigen = new HashSet<>();
								for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicketOrigen().getPagos()) {
									mediosPagoTicketOrigen.add(pago.getCodMedioPago());
								}

								// GAPXX - GENERAR VALE DE DEVOLUCIÓN
								// Se deshabilita medio pago por defecto
								boton.setDisable(true);

								// Si el medio de pago está en el ticket origen o es VALE DEVOLUCION, se habilita
								if (mediosPagoTicketOrigen.contains(codMedioPago) ||
										(codMedioPago.equals(codMedPagVale) && !comprobarDevolucionMismoMedio(codMedioPago))) {
									boton.setDisable(false);
								}

								// Se elige por defecto el primer medio de pago contenido en el ticket origen que encontremos
								if (!medioPagoEstaSeleccionado.get() && mediosPagoTicketOrigen.contains(codMedioPago)) {
									medioPagoSeleccionado = MediosPagosService.mediosPago.get(codMedioPago);
									lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
									medioPagoEstaSeleccionado.set(true);
								}

							}
						}

						// CZZ-120 - Solo se permite agrupar en la venta
						if (boton.getClave().equals(BOTON_CLAVE_TOOGLE_MEDIO_PAGO_OTROS)) {
							boton.setDisable(ticketManager.isEsDevolucion());
						}
					}
				}
			});

			// Una vez cargada la botonera, se indica que la vista ya no es la inicial para los botones de pago otros
			esVistaInicialOtroMedioPago = false;
		}
	}

    private void seleccionarPrimerMedioDePagoDelTicket(AtomicBoolean medioPagoEstaSeleccionado, String codMedioPago) {
        Set<String> mediosPagoTicketOrigen = new HashSet<>();
        for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicketOrigen().getPagos()) {
            mediosPagoTicketOrigen.add(pago.getCodMedioPago());
        }

        if (!medioPagoEstaSeleccionado.get() && mediosPagoTicketOrigen.contains(codMedioPago)) {
            medioPagoSeleccionado = MediosPagosService.mediosPago.get(codMedioPago);
            lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
            medioPagoEstaSeleccionado.set(true);
        }
    }

    private void seleccionarMedioPagoPorDefecto(String codMedioPago, BotonBotoneraComponent boton, boolean disable) {
		log.debug("seleccionarMedioPago() - Se selecciona medio de pago: " + codMedioPago);
		medioPagoSeleccionado = MediosPagosService.mediosPago.get(codMedioPago);
		lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
		boton.setDisable(disable);
		log.debug("seleccionarMedioPago() - Se ha seleccionado medio de pago: " + medioPagoSeleccionado.getCodMedioPago() + " - " + medioPagoSeleccionado.getDesMedioPago());
	}

	// Método que comprueba si existen medios de pago en el ticket origen que solo permiten devolución en efectivo
	private boolean comprobarExistenciaMediosPagoDevolucionEfectivo(List<PagoTicket> pagos) {
		String variable = variablesServices.getVariableAsString(MEDIOS_PAGOS_DEVOLUCION_EFECTIVO);
		for (String codMedioPago : variable.split(";")) {
			if (pagos.stream().anyMatch(p -> p.getCodMedioPago().equals(codMedioPago))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void enablePaymentsMethods() {
		comprobarConfiguracionBotoneraPago(panelMediosPago);
		super.enablePaymentsMethods();
	}

	@Override
	public void anotarPago(BigDecimal importe) {
		if (ticketManager.getTicketOrigen() != null) {
			if (!comprobarMediosPagoOrigen()) {
				tfImporte.requestFocus();
				return;
			}
		}

		this.log.debug("anotarPago() - Anotando pago: medio de pago:" + medioPagoSeleccionado + " // Importe: " + importe);
		if (medioPagoSeleccionado == null || medioPagoSeleccionado.getCodMedioPago().equals("9999")) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Seleccione medio de pago"), this.getStage());
			return;
		}

		if (importe.compareTo(BigDecimal.ZERO) == 0) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede anotar un pago de 0€"), this.getStage());
			return;
		}

		//GAP 172 TRAZABILIDAD ANIMALES
		if(!ticketManager.isEsDevolucion() && (isFacturaCompletaVentaAnimal() || isFacturaCompletaVentaPlanesSalud())) {
			return;
		}

		// Se comprueba si es una venta intercompany y se está intentando realizar un pago con transferencia sin factura completa
		if (!isDevolucion() && ((IskaypetTicketManager) ticketManager).isTicketVentaIntercompany() && medioPagoSeleccionado.getCodMedioPago().equals(TRANSFERENCIA.getCodPago()) &&
				!ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Solo se puede realizar una venta INTERCOMPANY con factura completa."), this.getStage());
			return;
		}

		// Se comprueba si el importe supera el máximo permitido para el medio de pago efectivo
		String messageSuperaImporte = comprobarSuperaImporteMaximoEfectivo(importe);
		if (StringUtils.isNotBlank(messageSuperaImporte)) {
			VentanaDialogoComponent.crearVentanaError(messageSuperaImporte, getStage());
			return;
		}

		if(!permitePagoMixto(medioPagoSeleccionado, isDevolucion())) {
			String mensaje = comprobarPagoIntegro(medioPagoSeleccionado, ticketManager.getTicket().getPagos(), importe);
			if (StringUtils.isNotBlank(mensaje)) {
				VentanaDialogoComponent.crearVentanaAviso(mensaje, getStage());
				return;
			}
		}

		if (ticketManager.comprobarImporteMaximoOperacion(getStage())) {
			if (!comprobarMedioPagoAdmiteCambios(medioPagoSeleccionado) && BigDecimalUtil.isMayor(importe.abs(), ticketManager.getTicket().getTotales().getPendiente().abs())) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El medio de pago seleccionado no permite introducir un importe superior al total del documento"), this.getStage());
				tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
			}
			else {
				incluirPagoTicket(importe);
				visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
				escribirVisor();
				tfImporte.requestFocus();
			}
		}
	}

	// Este método se llama solo cuando no permite pago mixto, para comprobar si el importe es igual al total a pagar (pago integro)
	private String comprobarPagoIntegro(MedioPagoBean medioPagoSeleccionado, List<PagoTicket> pagos, BigDecimal importe) {
		log.debug("comprobarPagoIntegro() - Comprobando si el pago es íntegro para el medio de pago seleccionado " + medioPagoSeleccionado.getCodMedioPago());
		if(pagos.stream().anyMatch(p -> !p.getCodMedioPago().equals(medioPagoSeleccionado.getCodMedioPago()))) {
			log.debug("comprobarPagoIntegro() - El pago no es íntegro para el medio de pago seleccionado " + medioPagoSeleccionado.getCodMedioPago() + " porque contiene otros medios de pago");
			return I18N.getTexto("El medio de pago seleccionado no permite pagos mixtos.");
		}

		if (!BigDecimalUtil.isIgual(importe, ticketManager.getTicket().getTotales().getTotalAPagar().abs())) {
			log.debug("comprobarPagoIntegro() - El pago no es íntegro para el medio de pago seleccionado " + medioPagoSeleccionado.getCodMedioPago() + " porque el importe no es igual al total a pagar");
			return I18N.getTexto("El medio de pago seleccionado solo admite pagos integros (total a pagar).");
		}
		ticketManager.getTicket().getTotales().getTotalAPagar();
		log.debug("comprobarPagoIntegro() - El pago es íntegro para el medio de pago seleccionado " + medioPagoSeleccionado.getCodMedioPago());
		return null;
	}

	private boolean permitePagoMixto(MedioPagoBean medioPagoSeleccionado, boolean isDevolucion) {

		String variable = variablesServices.getVariableAsString(isDevolucion ? MEDIOS_PAGOS_PERMITEN_DEVO_MIXTA : MEDIOS_PAGOS_PERMITEN_PAGO_MIXTO);

		// Si no hay variable, no se permite pago mixto
		if(StringUtils.isBlank(variable)) {
			return false;
		}

		// Si el medio de pago seleccionado está en la variable, se permite pago mixto
		for(String codMedioPago : variable.split(";")) {
			if(codMedioPago.equals(medioPagoSeleccionado.getCodMedioPago())) {
				return true;
			}
		}

		return false;

	}

	private boolean comprobarMedioPagoAdmiteCambios(MedioPagoBean medioPagoSeleccionado) {

		String medioPagosPermitenCambio = variablesServices.getVariableAsString(MEDIOS_PAGOS_PERMITEN_CAMBIO);
		if (StringUtils.isNotBlank(medioPagosPermitenCambio)) {
			return Arrays.asList(medioPagosPermitenCambio.split(";")).contains(medioPagoSeleccionado.getCodMedioPago());
		}
		return false;
	}

	private boolean comprobarDevolucionMismoMedio(String codMedioPagoActual){
		String mediosPagosDevolucionMismoMedio = variablesServices.getVariableAsString(MEDIOS_PAGOS_DEVOLUCION_MISMO_MEDIO);

		if(StringUtils.isBlank(mediosPagosDevolucionMismoMedio)) {
			return false;
		}

		// Si el medio de pago está en la variable solo se permite ese medio en devoluciones
		for(String codMedioPago : mediosPagosDevolucionMismoMedio.split(";")) {
			if(codMedioPago.equals(codMedioPagoActual)) {
				return true;
			}
		}
		return false;
	}

    private boolean comprobarExistenciaDevolucionMismoMedio(List<PagoTicket> pagos) {
        String variable = variablesServices.getVariableAsString(MEDIOS_PAGOS_DEVOLUCION_MISMO_MEDIO);
        for (String codMedioPago : variable.split(";")) {
            if (pagos.stream().anyMatch(p -> p.getCodMedioPago().equals(codMedioPago))) {
                return true;
            }
        }
        return false;
    }

	private boolean comprobarMediosPagoOrigen() {
		log.debug("comprobarMediosPagoOrigen() - Comprobando si es posible anotar el pago según los medios de pago del ticket origen.");

		List<IPagoTicket> pagosOrigen = (List<IPagoTicket>) ticketManager.getTicketOrigen().getPagos().stream()
		        .filter(p -> ((PagoTicket) p).getCodMedioPago().equals(medioPagoSeleccionado.getCodMedioPago())).collect(Collectors.toList());

		boolean medioPagoExisteEnOrigen = !pagosOrigen.isEmpty();

		boolean esDevolucionSinDocOrigen = isDevolucion() && ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null;

		// ISK-182 GAP-63 - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
		// En devolución libre no existe doc origen, por lo que no se puede comprobar medio de pago
		if (!esDevolucionSinDocOrigen) {

			// Si el importe a insertar supera el total del ticket a devolver, no se puede anotar el pago
			BigDecimal acumuladoLineasTicket = calcularAcumuladoLineasTicket();
			BigDecimal acumuladoPagosTicket = calcularAcumuladoPagosTicket();
			BigDecimal importeAnadir = FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText()).abs();
			BigDecimal nuevoAcumuladoPagosTicket = acumuladoPagosTicket.add(importeAnadir);
			if (nuevoAcumuladoPagosTicket.compareTo(acumuladoLineasTicket.abs()) > 0) {
				String texto = I18N.getTexto("La cantidad que se ha anotado supera el importe total de las lineas a devolver");
				VentanaDialogoComponent.crearVentanaError(texto, getStage());
				return false;
			}

            if (medioPagoExisteEnOrigen) {

				BigDecimal importeOrigen = pagosOrigen.stream().map(IPagoTicket::getImporte).reduce(BigDecimal.ZERO, BigDecimal::add);
				BigDecimal importeTotalMedioPago = calculaAcumuladoMedioPago().abs();
                if (BigDecimalUtil.isMayor(importeTotalMedioPago, importeOrigen)) {

					// Si no permite superar el importe del origen, no se puede anotar el pago
					if ( !permiteSuperarImporteOrigen(medioPagoSeleccionado.getCodMedioPago())) {
						String texto = I18N.getTexto("El importe a devolver por este medio de pago es superior al de origen");
						VentanaDialogoComponent.crearVentanaAviso(texto, getStage());
						return false;
					} else {
						String texto = I18N.getTexto("El método de pago a devolver supera la cantidad usada en origen. \n¿Está seguro de querer anotar el pago?");
						if (!VentanaDialogoComponent.crearVentanaConfirmacion(texto, getStage())) {
							return false;
						}
					}

                }
            } else {
				String texto = I18N.getTexto("El método de pago a devolver es distinto al ticket origen. \n¿Está seguro de querer anotar el pago?");
				if (!VentanaDialogoComponent.crearVentanaConfirmacion(texto, getStage())) {
					return false;
				}

			}

		}

		return true;
	}

	private Boolean permiteSuperarImporteOrigen(String codMedioPagoSeleccionado) {
		log.debug("permiteSuperarImporteOrigen() - Comprobando si el medio de pago seleccionado " + codMedioPagoSeleccionado + " permite superar el importe del ticket origen.");
		String variable = variablesServices.getVariableAsString(MEDIOS_PAGOS_DEVOLUCION_SUPERAR_ORIGEN);

		if (StringUtils.isBlank(variable)) {
			return false;
		}

		for (String codMedioPago : variable.split(";")) {
			if (codMedioPago.equals(codMedioPagoSeleccionado)) {
				log.debug("permiteSuperarImporteOrigen() - El medio de pago seleccionado " + codMedioPagoSeleccionado + " permite superar el importe del ticket origen.");
				return true;
			}
		}

		log.debug("permiteSuperarImporteOrigen() - El medio de pago seleccionado " + codMedioPagoSeleccionado + " no permite superar el importe del ticket origen.");
		return false;
	}

	private BigDecimal calcularAcumuladoLineasTicket() {
		List<LineaTicket> lineas = ticketManager.getTicket().getLineas();
		BigDecimal sumaImportes = BigDecimal.ZERO;
		for (LineaTicket linea : lineas) {
			sumaImportes = sumaImportes.add(linea.getImporteTotalConDto());
		}
		return sumaImportes.abs();

	}

	private BigDecimal calcularAcumuladoPagosTicket() {
		List<PagoTicket> pagos = ticketManager.getTicket().getPagos();
		BigDecimal sumaImportes = BigDecimal.ZERO;
		for (PagoTicket pago : pagos) {
			sumaImportes = sumaImportes.add(pago.getImporte());
		}
		return sumaImportes.abs();
	}

	private BigDecimal calculaAcumuladoMedioPago() {
		String codMedioPagoSeleccionado = medioPagoSeleccionado.getCodMedioPago();
		log.debug("calculaAcumuladoMedioPago() - Calculando acumulado de medio de pago: " + codMedioPagoSeleccionado);

		BigDecimal importeTotalMedioPago = BigDecimal.ZERO;
		BigDecimal importeNuevo = FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText()).abs();
		importeTotalMedioPago = importeTotalMedioPago.add(importeNuevo);
		for (IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
			if (pago.getCodMedioPago().equals(codMedioPagoSeleccionado)) {
				importeTotalMedioPago = importeTotalMedioPago.add(pago.getImporte().abs());
			}
		}

		log.debug("calculaTotalMedioPago() - Acumulado: " + importeTotalMedioPago);

		return importeTotalMedioPago;
	}

	@Override
	public void seleccionarMedioPago(HashMap<String, String> parametros) {

		try {
			if (parametros.containsKey(BOTON_PARAMETRO_MEDIO_PAGO)) {
				String codMedioPago = parametros.get(BOTON_PARAMETRO_MEDIO_PAGO);
				MedioPagoBean medioPago = mediosPagosService.getMedioPago(codMedioPago);
				if (medioPago != null) {

					// ISK-262 GAP-107 GLOVO - Si es una venta delivery y se intenta cambiar a otro medio de pago da error
					if (!isDevolucion() && ((IskaypetTicketManager) ticketManager).isTicketVentaDelivery()) {
						if (!codMedioPago.equals(medioPagoDelivery.getCodMedioPago())) {
							VentanaDialogoComponent.crearVentanaError(
							        I18N.getTexto("En esta operación sólo es posible utilizar el medio de pago: {0}", I18N.getTexto(medioPagoDelivery.getDesMedioPago())), getStage());
						}
						return;
					}

					// Si es una venta, y el medio de pago es transferencia
					if (!isDevolucion() && medioPago.getCodMedioPago().equals(TRANSFERENCIA.getCodPago())) {
						// See comprueba que el cliente esté identificado
						if (ticketManager.getTicket().getCabecera().getDatosFidelizado() == null) {
							// En caso de no estar identificado, se obliga a identificarlo
							String mensaje = I18N.getTexto("Para poder seleccionar el pago por transferencia es necesario identificar al cliente");
							VentanaDialogoComponent.crearVentanaAviso(mensaje, getStage());
							getDatos().put(ACCION_BUSQUEDA_CLIENTE, Boolean.TRUE);
							getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
							getStage().close();
							return;
						}
						else {
							// En caso de estar identificado, se comprueba que este seleccionada factura completa
							if (!ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
								String mensaje = I18N.getTexto("Para poder seleccionar el pago por transferencia es necesario que sea factura completa");
								VentanaDialogoComponent.crearVentanaAviso(mensaje, getStage());
								return;
							}
						}
					}

					if (isDevolucion() && contienePlanesSalud() && !medioPago.getCodMedioPago().equals(TRANSFERENCIA.getCodPago())) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("En devoluciones de Planes de Salud solo se puede pagar con TRANSFERENCIA"), getStage());
						return;
					}

					if (((IskaypetTicketManager) ticketManager).isTicketVentaIntercompany() && !medioPago.getCodMedioPago().equals(TRANSFERENCIA.getCodPago())) {
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Cliente INTERCOMPANY solo puede pagar con TRANSFERENCIA"), getStage());
						return;
					}

					if (!getMedioPagosSinAutorizacion().contains(codMedioPago)) {
						HashMap<String, Object> datos = new HashMap<>();
						datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
						AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);
					}

					if (isDevolucion() && ((IskaypetTicketManager) ticketManager).isDevolucionDelivery()
					&& (((IskaypetCabeceraTicket) ticketManager.getTicketOrigen().getCabecera()).getDelivery()).equals(medioPago.getDesMedioPago())) {
						VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Esta opción NO abona el importe al Cliente ¿Estás conforme?"), getStage());
					}

					medioPagoSeleccionado = medioPago;
					paymentsManager.select(medioPago.getCodMedioPago());
					lbMedioPago.setText(I18N.getTexto(medioPago.getDesMedioPago()));
					if (parametros.containsKey("valor")) {
						String valor = parametros.get("valor");

						try {
							BigDecimal importe = new BigDecimal(valor);
							anotarPago(importe);
						}
						catch (Exception e) {
							log.error("El valor configurado no se puede formatear: " + valor);
						}
					}
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
		catch (InitializeGuiException e) {
			if (e.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al autorizar el medio de pago. Contacte con un administrador"), getStage());
			}
		}
	}

	@Override
	protected void selectDefaultPaymentMethod() {
		// ISK-262 GAP-107 GLOVO - funcionamiento estándar excepto para venta delivery, que selecciona medpag delivery
		MedioPagoBean proximaSeleccionMedioPago = MediosPagosService.medioPagoDefecto;

		if (((IskaypetTicketManager) ticketManager).isTicketVentaDelivery()) {
			proximaSeleccionMedioPago = medioPagoDelivery;
		}

		if (((IskaypetTicketManager) ticketManager).isTicketVentaIntercompany()) {
			proximaSeleccionMedioPago = MediosPagosService.mediosPago.get(TRANSFERENCIA.getCodPago());
		}

		// Estándar excepto I18N y asignación medioPagoSeleccionado
		tbPagos.getSelectionModel().selectLast();
		medioPagoSeleccionado = proximaSeleccionMedioPago;
		lbMedioPago.setText(I18N.getTexto(medioPagoSeleccionado.getDesMedioPago()));
		tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
		lbSaldo.setText("");
		tfImporte.requestFocus();
	}

	private List<String> getMedioPagosSinAutorizacion() {
		String medioPagos = variablesServices.getVariableAsString(MEDIOS_PAGOS_SIN_AUTORIZACION);
		if (StringUtils.isNotBlank(medioPagos)) {
			return Arrays.asList(medioPagos.split(";"));
		}
		return new ArrayList<>();
	}

	@Override
	public void aceptar() throws DocumentoException {

		//Actualizamos los valores de los indetificadores en el caso de que la linea lo contenga en la tabla X_ITEMS_PETS_IDENT_TBL
		trazabilidadMascotasService.actualizaEstadoIdentificador(ticketManager);


		if (comprobarExistenciaMostrarCambioPagos()) {
			HashMap<String, Object> datos = new HashMap();
			datos.put(PARAMETRO_CANTIDAD_TOTAL, ticketManager.getTicket().getCabecera().getCantidadArticulos());
			datos.put(PARAMETRO_ENTRADA_CAMBIO, lbCambio.getText());
			datos.put(PARAMETRO_ENTRADA_FORMA_PAGO_CAMBIO, lbMedioPagoVuelta.getText());
			datos.put(PARAMETRO_ENTRADA_ENTREGADO, lbEntregado.getText());
			datos.put(PARAMETRO_ENTRADA_TOTAL, lbTotal.getText());
			datos.put(ACCION_CONFIRMAR, true);
			getApplication().getMainView().showModalCentered(VentanaCambioView.class, datos, getStage());

			if (datos.containsKey(ACCION_CANCELAR) && (Boolean) datos.get(ACCION_CANCELAR)) {
				return;
			}
		}

		// ISK-149
		String emailFidelizado = "";
		DatosSesionBean datosSesion = new DatosSesionBean();
		try {
			datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
			FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			if (datosFidelizado != null && datosFidelizado.getAdicionales() != null) {

				if (datosFidelizado.getAdicionales().containsKey(IskaypetFidelizacion.PARAMETRO_EMAIL)) {
					emailFidelizado = (String) datosFidelizado.getAdicionales().get(IskaypetFidelizacion.PARAMETRO_EMAIL);
				}
				if (StringUtils.isBlank(emailFidelizado)) {
					String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);

					ConsultarFidelizadoRequestRest request = new ConsultarFidelizadoRequestRest(apiKey, datosSesion.getUidActividad());
					request.setIdFidelizado(ticketManager.getTicket().getCabecera().getDatosFidelizado().getIdFidelizado().toString());
					request.setNumeroTarjeta(ticketManager.getTicket().getCabecera().getDatosFidelizado().getNumTarjetaFidelizado());
					List<TiposContactoFidelizadoBean> contactos = FidelizadosRest.getContactos(request);

					for (TiposContactoFidelizadoBean contacto : contactos) {
						if (IskaypetFidelizacion.PARAMETRO_EMAIL.equalsIgnoreCase(contacto.getCodTipoCon())) {
							emailFidelizado = contacto.getValor();
							log.debug("aceptar() - Email fidelizado recuperado: " + emailFidelizado);
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			log.error("aceptar() - Ha ocurrido un error recuperando los datos de contacto del fidelizado. No se precargará el email en la pantalla modal." + e.getMessage(), e);
		}
		if (ticketManager.getTicket().getCabecera().getDatosFidelizado() != null) {
			boolean paperless = ticketManager.getTicket().getCabecera().getDatosFidelizado().getPaperLess();
			// Si es paperless (ticket digital), se envia directamente
			if (paperless && StringUtils.isNotBlank(emailFidelizado) && emailTieneFormatoCorrecto(emailFidelizado)) {
				accionSeleccionada = EmailConstants.ACCION_EMAIL;
				((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setEmailEnvioTicket(emailFidelizado);
			} else {
				if(paperless && StringUtils.isBlank(emailFidelizado)){
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El cliente tiene el check de ticket digital marcado pero no tiene mail relleno"), getStage());
				}
				if (paperless && StringUtils.isNotBlank(emailFidelizado) && !emailTieneFormatoCorrecto(emailFidelizado)) {
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("El email no contiene un formato correcto"), getStage());
				}
				// En este caso, se pregunta que se desea, si imprimir, enviar al mail o imprimir y enviar.
				getDatos().put(EmailConstants.PARAM_EMAIL_FIDELIZADO_API, emailFidelizado);
				getApplication().getMainView().showModalCentered(EmailSeleccionView.class, getDatos(), getStage());

				accionSeleccionada = (String) getDatos().get(EmailConstants.ACCION_SELECCIONADA);
				if (accionSeleccionada.equalsIgnoreCase(EmailConstants.ACCION_CANCELAR)) {
					return;
				}

				String emailIntroducido = (String) getDatos().get(EmailConstants.PARAM_EMAIL_FIDELIZADO_MODAL);
				if (StringUtils.isNotBlank(emailIntroducido)) {
					((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setEmailEnvioTicket(emailIntroducido);
				}
			}

			// Si es factura completa, guardamos las observaciones en caso de que existan
			if (ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
				String observaciones = lbObservacionesDetalle.getText();
				if (StringUtils.isNotBlank(observaciones) && !observaciones.equals(I18N.getTexto(PARAMETRO_OBSERVACIONES_VACIO))) {
					ticketManager.getTicket().getCabecera().getDatosFidelizado().putAdicional(PAGO_OBSERVACIONES, observaciones);
				}
			}

		}
		else {
			String enviarEmailVariable = variablesServices.getVariableAsString(ENVIO_EMAIL_VARIABLE);
			if (StringUtils.isNotBlank(enviarEmailVariable) && "S".equalsIgnoreCase(enviarEmailVariable)) {

				Boolean repetir = true;
				while (repetir) {

					getApplication().getMainView().showModalCentered(EmailSeleccionView.class, getDatos(), getStage());
					accionSeleccionada = (String) getDatos().get(EmailConstants.ACCION_SELECCIONADA);

					// Si se cancela, no continuamos
					if (EmailConstants.ACCION_CANCELAR.equalsIgnoreCase(accionSeleccionada)) {
						return;
					}

					// Si es envio por mail, se pide confirmación de mail, si es solo imprimir se continua
					if (EmailConstants.ACCION_IMPRIMIR.equalsIgnoreCase(accionSeleccionada)) {
						repetir = false;
					} else {
						String emailIntroducido = (String) getDatos().get(EmailConstants.PARAM_EMAIL_FIDELIZADO_MODAL);
						getDatos().put(EmailConstants.PARAM_EMAIL_CONFIRMACION, emailIntroducido);
						getApplication().getMainView().showModalCentered(EmailConfirmacionView.class, getDatos(), getStage());
						String accionSeleccionadaConfirmacion = (String) getDatos().get(EmailConstants.ACCION_SELECCIONADA);
						if (accionSeleccionadaConfirmacion.equalsIgnoreCase(EmailConstants.ACCION_ACEPTAR_CONFIRMACION)) {
							emailIntroducido = (String) getDatos().get(EmailConstants.PARAM_EMAIL_FIDELIZADO_MODAL);
							((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).setEmailEnvioTicket(emailIntroducido);
							repetir = false;
						}
					}
				}
			}
		}

		//GAP 169 DEVOLUCIÓN DE PROMOCIONES
		devolucionDePromociones();
		if(evicertiaService.tieneContratoAnimalLineas((IskaypetTicketVentaAbono) ticketManager.getTicket())) {
			actualizarMetodosFirmaTicket();
		}

		super.aceptar();
	}

	/**
	 * CZZ-1355  Actualiza el método de firma de los contratos asociados a las líneas del ticket.
	 * Recorre todas las líneas del ticket actual y, si tienen un contrato animal,
	 * consulta el contrato realizado y actualiza su método de firma.
	 */
	private void actualizarMetodosFirmaTicket() {
	    String uidTicket = ticketManager.getTicket().getCabecera().getUidTicket();

	    for (Object lineaTicket : ticketManager.getTicket().getLineas()) {
	        IskaypetLineaTicket linea = (IskaypetLineaTicket) lineaTicket;

	        if (linea.getContratoAnimal() != null) {
	            TicketContratosBean contrato = servicioContratoMascotas.getContratosRealizadoByPrimaryKey(uidTicket, linea.getIdLinea());

	            if (contrato != null) {
	                linea.getContratoAnimal().setMetodoFirma(contrato.getMetodoFirma());
	            }
	        }
	    }
	}

	private boolean emailTieneFormatoCorrecto(String email) {
        return EmailConstants.EMAIL_REGEX.matcher(email).find();
    }

	@Override
	protected void imprimir() {
		String mail = ((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).getEmailEnvioTicket();

		if ((EmailConstants.ACCION_EMAIL.equalsIgnoreCase(accionSeleccionada))) {
			if (requiereAbrirCajon(ticketManager.getTicket(), Boolean.FALSE)) {
                abrirCajon();
            }
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Enviado ticket por correo electronico a {0}", mail), getStage());
		} else {
			if(EmailConstants.ACCION_IMPRIMIR_EMAIL.equalsIgnoreCase(accionSeleccionada)){
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Enviado ticket por correo electronico a {0}", mail), getStage());
			}

			// Parseamos las lineas del ticket por si contienen caracteres especiales
			parsearLineasCaracteresEspeciales(ticketManager.getTicket().getLineas());
			parsearPromocionesCaracteresEspeciales(ticketManager.getTicket().getPromociones());
			ofuscarNombreCajero(ticketManager.getTicket());
			imprimirEstandar();

			// Ponemos esto para que se impriman los cupones generados por diferencia
			try {
				List<CuponEmitidoTicket> cuponesEmitidos = ((TicketVentaAbono) ticketManager.getTicket()).getCuponesEmitidos();

				if (!BigDecimalUtil.isMayorACero(ticketManager.getTicket().getTotales().getTotal()) && cuponesEmitidos != null && !cuponesEmitidos.isEmpty()) {

					Map<String, Object> mapaParametrosCupon = new HashMap<>();
					mapaParametrosCupon.put(TICKET, ticketManager.getTicket());
					FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
					mapaParametrosCupon.put(PAPERLESS, datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
					for (CuponEmitidoTicket cupon : cuponesEmitidos) {
						// Ponemos esta condición para que sólo se haga con los cupones por diferencia
						if (cupon.getIdPromocionOrigen() == null) {
							mapaParametrosCupon.put("cupon", cupon);
							SimpleDateFormat df = new SimpleDateFormat();
							mapaParametrosCupon.put("fechaEmision", df.format(ticketManager.getTicket().getCabecera().getFecha()));

							Date fechaInicio = cupon.getFechaInicio();
							if (fechaInicio == null || fechaInicio.before(ticketManager.getTicket().getCabecera().getFecha())) {
								mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(ticketManager.getTicket().getCabecera().getFecha()));
							}
							else {
								mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(fechaInicio));
							}
							Date fechaFin = cupon.getFechaFin();
							mapaParametrosCupon.put("fechaFin", FormatUtil.getInstance().formateaFecha(fechaFin));

							if (cupon.getMaximoUsos() != null) {
								mapaParametrosCupon.put("maximoUsos", cupon.getMaximoUsos().toString());
							}
							else {
								mapaParametrosCupon.put("maximoUsos", "");
							}

							 // ISK-372 Formatear el importe a x.xxx,xx
					        String importeCuponFormateado = getImporteCuponFormateado(cupon);
					        mapaParametrosCupon.put("importeCuponFormateado", importeCuponFormateado);

							ServicioImpresion.imprimir(PLANTILLA_CUPON, mapaParametrosCupon);
						}
					}
				}
			}
			catch (Exception e) {
				log.error("imprimir() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
				VentanaDialogoComponent.crearVentanaError(getStage(),
				        I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir.") + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("El error es: ") + e.getMessage(), e);
			}
		}
	}

	protected void imprimirEstandar() {
		while (true) {
			try {
				String formatoImpresion = ticketManager.getTicket().getCabecera().getFormatoImpresion();
				if (formatoImpresion.equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
					log.info("imprimir() - Formato de impresión no configurado, no se imprimirá.");
					return;
				}

				Map<String, Object> mapaParametrosCupon = new HashMap();
				mapaParametrosCupon.put(TICKET, ticketManager.getTicket());
				mapaParametrosCupon.put(LINEAS, generarLineasAgrupadas((List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas(), false));
				mapaParametrosCupon.put(IMPUESTOS, generarImpuestosAgrupados((List<SubtotalIvaTicket>) ticketManager.getTicket().getCabecera().getSubtotalesIva()));
				mapaParametrosCupon.put(BIG_DECIMAL_100, BigDecimalUtil.CIEN);
				mapaParametrosCupon.put(APERTURA_CAJON, requiereAbrirCajon(ticketManager.getTicket(), Boolean.FALSE));
				mapaParametrosCupon.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));
                mapaParametrosCupon.put(URL_QR, variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
                mapaParametrosCupon.put(URL_TEMP_QR, variablesServices.getVariableAsString(QR_TEMPORAL));
				FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				mapaParametrosCupon.put(PAPERLESS, datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
				if (ticketManager.getTicket().getCabecera().getCodTipoDocumento().equals(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getCodtipodocumento())) {
					mapaParametrosCupon.put(EMPRESA, sesion.getAplicacion().getEmpresa());
				}

				ServicioImpresion.imprimir(formatoImpresion, mapaParametrosCupon);

				List cupones;
				if (BigDecimalUtil.isMayorACero(ticketManager.getTicket().getTotales().getTotal())) {
					cupones = ((TicketVentaAbono) ticketManager.getTicket()).getCuponesEmitidos();
					if (!cupones.isEmpty()) {
						mapaParametrosCupon = new HashMap();
						mapaParametrosCupon.put(TICKET, ticketManager.getTicket());
						mapaParametrosCupon.put(IMPRIMIR_LOGO, requierImprimirLogo(variablesServices));
						datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
						mapaParametrosCupon.put(PAPERLESS, datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());

						for (Iterator var15 = cupones.iterator(); var15.hasNext(); ServicioImpresion.imprimir("cupon_promocion", mapaParametrosCupon)) {
							CuponEmitidoTicket cupon = (CuponEmitidoTicket) var15.next();
							mapaParametrosCupon.put("cupon", cupon);
							SimpleDateFormat df = new SimpleDateFormat();
							mapaParametrosCupon.put("fechaEmision", df.format(ticketManager.getTicket().getCabecera().getFecha()));
							Date fechaInicio = cupon.getFechaInicio();
							if (fechaInicio != null && !fechaInicio.before(ticketManager.getTicket().getCabecera().getFecha())) {
								mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(fechaInicio));
							}
							else {
								mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(ticketManager.getTicket().getCabecera().getFecha()));
							}

							Date fechaFin = cupon.getFechaFin();
							mapaParametrosCupon.put("fechaFin", FormatUtil.getInstance().formateaFecha(fechaFin));
							if (cupon.getMaximoUsos() != null) {
								mapaParametrosCupon.put("maximoUsos", cupon.getMaximoUsos().toString());
							}
							else {
								mapaParametrosCupon.put("maximoUsos", "");
							}

							// ISK-372 Formatear el importe a x.xxx,xx
					        String importeCuponFormateado = getImporteCuponFormateado(cupon);
					        mapaParametrosCupon.put("importeCuponFormateado", importeCuponFormateado);
						}
					}

					if (!ticketManager.isEsDevolucion()) {
						if (mediosPagosService.isCodMedioPagoVale(ticketManager.getTicket().getTotales().getCambio().getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
						        && !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())) {
							printVale(ticketManager.getTicket().getTotales().getCambio());
						}
					}
					else if (documentos.isDocumentoAbono(ticketManager.getTicket().getCabecera().getCodTipoDocumento())) {
						List<PagoTicket> pagos = ticketManager.getTicket().getPagos();

						for (PagoTicket pago : pagos) {
							if (mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
							        && BigDecimalUtil.isMenorACero(pago.getImporte())) {
								printVale(pago);
							}
						}
					}
				}
				else {
					cupones = ticketManager.getTicket().getPagos();
					Iterator var12 = cupones.iterator();

					while (var12.hasNext()) {
						PagoTicket pago = (PagoTicket) var12.next();
						if (mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
						        && !BigDecimalUtil.isMayorACero(pago.getImporte())) {
							printVale(pago);
						}
					}
				}
			}
			catch (Exception var9) {
				log.error("imprimir() - " + var9.getClass().getName() + " - " + var9.getLocalizedMessage(), var9);
				VentanaDialogoComponent.crearVentanaError(getStage(),
				        I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir.") + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("El error es: ") + var9.getMessage(), var9);
			}

			return;
		}
	}

	public String getImporteCuponFormateado(CuponEmitidoTicket cupon) {
		BigDecimal importeOriginal = cupon.getImporteCupon();
		if (importeOriginal == null) return "0,00"; // prevenir null

		BigDecimal importeCupon = importeOriginal.setScale(2, RoundingMode.HALF_UP);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.'); // separador de miles
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

		String importeCuponFormateado = decimalFormat.format(importeCupon);

		log.info("Se ha formateado el importe de " + importeOriginal + " a " + importeCuponFormateado);

		return importeCuponFormateado;
	}

	@Override
	public void accionFactura() {
		log.debug("accionFactura()");
		log.debug("accionFactura() - Pagos cubiertos");

		try {
			if (ticketManager.comprobarConfigContador(Documentos.FACTURA_COMPLETA)) {
				FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();

				if (datosFidelizado == null) {
					VentanaDialogoComponent.crearVentanaInfo("Para generar una factura completa es necesario haber identificado al cliente en la ventana de venta", getStage());
					if (ticketManager.getTicket().getIdTicket() != null){
						iskaypetTicketService.deshacerContadorIdTicket(ticketManager.getTicket() , documentoActivoOriginal);
					}
					getDatos().put(ACCION_BUSQUEDA_CLIENTE, Boolean.TRUE);
					getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
					getStage().close();
					return;
				}

				if (ticketManager instanceof IskaypetTicketManager &&  ((IskaypetTicketManager) ticketManager).validarDatosFidelizadoFactura(datosFidelizado, getStage())) {
					log.debug("accionFactura() - Datos de fidelizado correctos");
					((IskaypetTicketManager) ticketManager).guardarDatosFacturacion(datosFidelizado);
					log.debug("accionFactura() - Datos de facturación guardados");
					cargarObservaciones();

					if (!ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
						ticketManager.setDocumentoActivo(sesion.getAplicacion().getDocumentos().getDocumento(ticketManager.getDocumentoActivo().getTipoDocumentoFacturaDirecta()));
					}

					lbDocActivo.setText(I18N.getTexto(ticketManager.getDocumentoActivo().getDestipodocumento()));

					cargarBotoneraDatosAdicionales();

					if (AppConfig.pais.equalsIgnoreCase("PT")) {
						lbNifDetalle.setText(datosFidelizado.getDocumento());
						btEditarNif.setDisable(true);
						btEliminarNif.setDisable(true);
						modificarNifCabecera(null);
					}

					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Factura completa generada correctamente"), getStage());
				}
			}
			else {
				ticketManager.crearVentanaErrorContador(getStage());
			}
		}
		catch (Exception e) {
			log.error("accionFactura() - Excepción no controlada : " + e.getCause(), e);
		}
	}

	private void cargarObservaciones()  {
		log.debug("cargarObservaciones() - Cargando observaciones");
		try {
			DatosFactura cliente = ticketManager.getTicket().getCliente().getDatosFactura();
			List<TiposIdentBean> tiposIdent = tiposIdentService.consultarTiposIdent(null, true, cliente.getPais());
			TiposIdentBean tipo = tiposIdent.stream().filter(el -> el.getCodTipoIden().equals(cliente.getTipoIdentificacion())).findFirst().orElse(null);
			if (tipo != null && tipo.getEmpresa()) {
				log.debug("cargarObservaciones() - Cargando observaciones por ser tipo empresa");
				HashMap<String, Object> datos = new HashMap<>();
				getApplication().getMainView().showModalCentered(PagoObservacionesView.class, datos, getStage());
				lbObservaciones.setVisible(true);
				btEditar.setVisible(true);
				btEliminar.setVisible(true);
				lbObservacionesDetalle.setText(datos.containsKey(PAGO_OBSERVACIONES) ? (String) datos.get(PAGO_OBSERVACIONES) : I18N.getTexto(PARAMETRO_OBSERVACIONES_VACIO));
				log.debug("cargarObservaciones() - Observaciones cargadas: " + lbObservacionesDetalle.getText());
			}
		} catch (TiposIdentServiceException | TiposIdentNotFoundException e) {
			log.error("Ha ocurrido un error al cargar los tipos de identificación", e);
		}
	}

	@Override
	protected void cargarBotoneraDatosAdicionales() {
		panelBotoneraDatosAdicionales.setPrefWidth(150.0);
		super.cargarBotoneraDatosAdicionales();
	}


	protected void guardarDatosFactura(FidelizacionBean cliente) {
		DatosFactura datosFactura = new DatosFactura();

		datosFactura.setNombre(String.join(" ", cliente.getNombre(), cliente.getApellido()));
		datosFactura.setTipoIdentificacion(cliente.getCodTipoIden());
		datosFactura.setCif(cliente.getDocumento());
		datosFactura.setCp(cliente.getCp());
		datosFactura.setDomicilio(cliente.getDomicilio());
		datosFactura.setProvincia(cliente.getProvincia());
		datosFactura.setPoblacion(cliente.getPoblacion());
		datosFactura.setLocalidad(cliente.getLocalidad());
		datosFactura.setPais(cliente.getCodPais());

		((TicketVenta) ticketManager.getTicket()).setDatosFacturacion(datosFactura);
	}

	protected String comprobarSuperaImporteMaximoEfectivo(BigDecimal importe) {

		String message = null;
		if (medioPagoSeleccionado != null && medioPagoSeleccionado.getEfectivo() && medioPagoSeleccionado.getManual() && BigDecimalUtil.isMayor(importe, BigDecimal.ZERO)) {

			String codPaisCliente = ticketManager.getTicket().getCliente().getDatosFactura() == null ? ticketManager.getTicket().getCliente().getCodpais()
			        : ticketManager.getTicket().getCliente().getDatosFactura().getPais();

			importeMaxEfectivo = sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(codPaisCliente) ? ticketManager.getDocumentoActivo().getImporteMaximoEfectivo()
			        : ticketManager.getDocumentoActivo().getImporteMaximoEfectivoEx();

			if (importeMaxEfectivo != null) {
				if (BigDecimalUtil.isMayorOrIgual(ticketManager.getTicket().getTotales().getTotalAPagar(), importeMaxEfectivo)) {
					message = I18N.getTexto("La cantidad total del ticket a pagar supera el límite máximo permitido de {0}€ para pagos en efectivo", importeMaxEfectivo);
				}
				else {
					BigDecimal importeEfectivo = BigDecimal.ZERO;
					for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicket().getPagos()) {
						if (pago.getMedioPago().getEfectivo() && pago.getMedioPago().getManual()) {
							importeEfectivo = importeEfectivo.add(pago.getImporte().abs());
						}
					}
					importeEfectivo = importeEfectivo.add(importe);
					if (BigDecimalUtil.isMayorOrIgual(importeEfectivo, importeMaxEfectivo)) {
						message = I18N.getTexto("La cantidad que se quiere pagar en efectivo supera el máximo permitido {0}€", importeMaxEfectivo);
					}
				}
			}

		}
		return message;
	}

	@Override
	protected Boolean superaImporteMaximoEfectivo() {

		String codPaisCliente = ticketManager.getTicket().getCliente().getDatosFactura() == null ? ticketManager.getTicket().getCliente().getCodpais()
		        : ticketManager.getTicket().getCliente().getDatosFactura().getPais();

		importeMaxEfectivo = sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(codPaisCliente) ? ticketManager.getDocumentoActivo().getImporteMaximoEfectivo()
		        : ticketManager.getDocumentoActivo().getImporteMaximoEfectivoEx();

		Boolean superaImporte = Boolean.FALSE;
		if (importeMaxEfectivo != null) {
			BigDecimal importeEfectivo = BigDecimal.ZERO;
			for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicket().getPagos()) {
				if (pago.getMedioPago().getEfectivo() && pago.getMedioPago().getManual()) {
					importeEfectivo = importeEfectivo.add(pago.getImporte().abs());
					if (BigDecimalUtil.isMayorOrIgual(importeEfectivo, importeMaxEfectivo)) {
						superaImporte = Boolean.TRUE;
						break;
					}
				}
			}
		}

		return superaImporte;
	}

	private boolean comprobarExistenciaMostrarCambioPagos() {
		List<PagoTicket> pagos = ticketManager.getTicket().getPagos();

		if (pagos != null && !pagos.isEmpty()) {
			String mostrarCambio = variablesServices.getVariableAsString(MEDIOS_PAGOS_MOSTRAR_CAMBIO);
			String[] mediosMostrarCambios = mostrarCambio.split(";");

			for (PagoTicket pago : pagos) {
				if (Arrays.asList(mediosMostrarCambios).contains(pago.getMedioPago().getCodMedioPago())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void accionCancelar() {
		boolean hayPagos = false;
		for (IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
			if (pago.isEliminable()) {
				hayPagos = true;
				break;
			}
		}

		if (hayPagos) {
			log.warn("accionCancelar() - Se ha intentado cancelar un ticket con pagos eliminables.");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se han efectuados pagos. Debe cancelarlos para volver atrás."), getStage());
		}
		else {

			// ISK-192 GAP-63 Devolución de ticket sin doc origen

			// Además de los datos origen falso, se setean temporalmente datos doc origen para que procese el documento de
			// devolución de forma estándar
			DatosOrigenTicketBean datosOrigenDevolucionFalsos = ((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen();

			if (datosOrigenDevolucionFalsos != null) {
				DatosDocumentoOrigenTicket docOrigen = new DatosDocumentoOrigenTicket();
				docOrigen.setSerie(datosOrigenDevolucionFalsos.getSerie());
				docOrigen.setCaja(datosOrigenDevolucionFalsos.getCodCaja());
				docOrigen.setNumFactura(datosOrigenDevolucionFalsos.getNumFactura());
				docOrigen.setIdTipoDoc(datosOrigenDevolucionFalsos.getIdTipoDocumento());
				docOrigen.setCodTipoDoc(datosOrigenDevolucionFalsos.getCodTipoDocumento());
				docOrigen.setUidTicket(datosOrigenDevolucionFalsos.getUidTicket());
				docOrigen.setCodTicket(datosOrigenDevolucionFalsos.getCodTicket());
				ticketManager.getTicketOrigen().getCabecera().setDatosDocOrigen(docOrigen);
			}
			// Fin ISK-192 GAP-63

			log.debug("accionCancelar() - Cancelando ticket sin pagos eliminables.");
			try {
				realizarComprobacionesTicketCierrePantalla();
			}
			catch (NoCerrarPantallaException e) {
				return;
			}

			if (documentoActivoOriginal != null && !documentoActivoOriginal.equals(ticketManager.getDocumentoActivo())
			        && ticketManager.comprobarConfigContador(documentoActivoOriginal.getCodtipodocumento())) {
				ticketManager.setDocumentoActivo(documentoActivoOriginal);
				ticketManager.getTicket().getCabecera().getCliente().setDatosFactura(null);
			}

			if (ticketManager.getTicket().getIdTicket() != null){
				iskaypetTicketService.deshacerContadorIdTicket(ticketManager.getTicket() , documentoActivoOriginal);
			}

			visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
			escribirVisor();
			getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
			getStage().close();
		}
	}

	private void gestionarPagoSipay() {
		try {
			if (StringUtils.isNotBlank(medioPagoSeleccionado.getCodMedioPago()) &&
					SipayConstants.MANAGER_CONTROL_CLASS.equals(medioPagoSeleccionado.getClaseControl()) &&
					BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente())) {
				aceptar();
			}
		}
		catch (Exception e) {
			log.error("gestionarPagoSipay() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
		}
	}

        @Override
        protected void processPaymentOk(PaymentsOkEvent event) {
                PaymentOkEvent eventOk = event.getOkEvent();
               if (!eventOk.isCanceled()) {
                       addPayment(eventOk);
               } else {
                       deletePayment(eventOk);
               }

               if (BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente())) {
                       ultimoErrorPago = null;
               }

               Platform.runLater(() -> {
                       refrescarDatosPantalla();
                       // Si se ha pagado a través de Sipay y el importe restante a pagar es 0, se acepta automáticamente
                       gestionarPagoSipay();
                       if (!isDevolucion()) {
                               selectDefaultPaymentMethod();
                       }
                       boolean pendiente = !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente());
                       btAceptar.setDisable(ultimoErrorPago != null || pendiente);
               });
       }

       @Override
       protected void processPaymentError(PaymentsErrorEvent event) {
               super.processPaymentError(event);
               ultimoErrorPago = event.getErrorEvent();
               btAceptar.setDisable(true);
       }

       @Override
       protected void finishSale(final PaymentsCompletedEvent event) {
               boolean pendiente = !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente());
               if (ultimoErrorPago != null || pendiente) {
                       btAceptar.setDisable(true);
                       btCancelar.setDisable(false);
               } else {
                       super.finishSale(event);
               }
       }

	/*
	 * ##############################################################################################################
	 * ########################################## GAPXX - GENERAR VALE DE DEVOLUCIÓN ################################
	 * ##############################################################################################################
	 */

	@Override
	protected void selectPayment(PaymentsSelectEvent event) {
		PaymentMethodManager source = (PaymentMethodManager) event.getEventSelect().getSource();

		MedioPagoBean medioPago = mediosPagosService.getMedioPago(source.getPaymentCode());
		medioPagoSeleccionado = medioPago;
		lbSaldo.setText("");
		lbMedioPago.setText(I18N.getTexto(medioPago.getDesMedioPago()));

		if (source instanceof GiftCardManager || (!ticketManager.isEsDevolucion() && source instanceof ValeDevManager)) {
			askGiftCardNumber(source);
		}
		else {
			selectCustomPaymentMethod(event.getEventSelect());
		}
	}

	@Override
	protected void addPayment(PaymentOkEvent eventOk) {
		int numeroPagos = pagos.size();

		BigDecimal amount = eventOk.getAmount();
		String paymentCode = ((PaymentMethodManager) eventOk.getSource()).getPaymentCode();
		Integer paymentId = eventOk.getPaymentId();
		boolean removable = eventOk.isRemovable();
		MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(paymentCode);
		// boolean cashFlowRecorded = ((PaymentMethodManager) eventOk.getSource()).recordCashFlowImmediately();
		log.debug("addPayment() - Se añadirá el nuevo pago. [PaymentCode: " + paymentCode + ", amount: " + amount.toString() + ", paymentId: " + paymentId + ", removable: " + removable + "]");
		PagoTicket payment = ticketManager.nuevaLineaPago(paymentCode, amount, true, removable, paymentId, true);
		payment.setMovimientoCajaInsertado(false); //no registramos el movimiento hasta finalizar el ticket.
		if (ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO) < 0) {
			amount = amount.negate();
		}

		if (eventOk.getSource() instanceof GiftCardManager || (!ticketManager.isEsDevolucion() && eventOk.getSource() instanceof ValeDevManager)
		        || (ticketManager.isEsDevolucion() && eventOk.getSource() instanceof ValeDevManager && numeroPagos < ticketManager.getTicket().getPagos().size())) {
			log.debug("addPayment() - GiftCardManager payment.");
			GiftCardBean giftCard = (GiftCardBean) eventOk.getExtendedData().get(GiftCardManager.PARAM_TARJETA);
			payment.addGiftcardBean(amount, giftCard);

			if (eventOk.getExtendedData().containsKey(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION) && eventOk.getExtendedData().get(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION) != null)
				payment.addExtendedData(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION, eventOk.getExtendedData().get(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION));

			if (eventOk.getExtendedData().containsKey(ValeDevManager.PARAM_TARJETA_FECHA_BAJA) && eventOk.getExtendedData().get(ValeDevManager.PARAM_TARJETA_FECHA_BAJA) != null)
				payment.addExtendedData(ValeDevManager.PARAM_TARJETA_FECHA_BAJA, eventOk.getExtendedData().get(ValeDevManager.PARAM_TARJETA_FECHA_BAJA));

			GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(giftCard);
			asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
		}
		else {
			addCustomPaymentData(eventOk, payment);
		}

		if (paymentMethod.getTarjetaCredito() != null && paymentMethod.getTarjetaCredito()) {
			if (eventOk.getExtendedData().containsKey(BasicPaymentMethodManager.PARAM_RESPONSE_TEF)) {
				log.debug("addPayment() - Adding extended data.");
				DatosRespuestaPagoTarjeta datosRespuestaPagoTarjeta = (DatosRespuestaPagoTarjeta) eventOk.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
				payment.setDatosRespuestaPagoTarjeta(datosRespuestaPagoTarjeta);
				for (String key : eventOk.getExtendedData().keySet()) {
					payment.addExtendedData(key, eventOk.getExtendedData().get(key));
				}
			}
		}
		ticketManager.guardarCopiaSeguridadTicket();
	}

	@Override
	public void asociarPagoTarjetaRegalo(Boolean venta, GiftCardBean giftCard) {
		if (giftCard != null) {
			if (Boolean.TRUE.equals(venta)) {
				// Si es venta se resta el importe de la tarjeta al valor del saldo
				BigDecimal saldo = giftCard.getSaldoTotal().subtract(giftCard.getImportePago());

				Platform.runLater(() -> lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(giftCard.getSaldoTotal()) + ")"));

				// Si pendiente es 0 , importe es 0
				if (BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente())) {
					tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
				}
				else {
					// Si saldo pendiente menor que saldo tarjeta , importe sera saldo pendiente en caso contrario sera
					// el saldo de la tarjeta
					BigDecimal importe = ticketManager.getTicket().getTotales().getPendiente().compareTo(saldo) < 0 ? ticketManager.getTicket().getTotales().getPendiente() : saldo;
					tfImporte.setText(FormatUtil.getInstance().formateaImporte(importe));
				}
			}
			else {
				try {
					String saldoString = FormatUtil.getInstance().formateaImporte(giftCard.getSaldoTotal());
					Platform.runLater(() -> lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + saldoString + ")"));
				}
				catch (Exception ignored) {
				}
				tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
			}
		}
	}

	/*
	 * ##############################################################################################################
	 * #################################### GAP 106 VALE DE DEVOLUCIÓN ENTRADA MANUAL ###############################
	 * ##############################################################################################################
	 */

	@Override
	protected void askGiftCardNumber(PaymentMethodManager source) {
		String numTarjeta = "";
		try {
			HashMap<String, Object> parametros = new HashMap<>();
			parametros.put(CodigoTarjetaController.PARAMETRO_IN_TEXTOCABECERA, I18N.getTexto("Lea o escriba el código de barras del vale"));
            parametros.put(IskaypetCodigoTarjetaController.PARAMETRO_IN_TEXTONUMERO, I18N.getTexto("Vale") + ":");
			parametros.put(CodigoTarjetaController.PARAMETRO_TIPO_TARJETA, "GIFTCARD");

			POSApplication posApplication = POSApplication.getInstance();
			posApplication.getMainView().showModalCentered(CodigoTarjetaView.class, parametros, getStage());

			// GAP 106 VALE DE DEVOLUCIÓN ENTRADA MANUAL
			numTarjeta = (String) parametros.get(CodigoTarjetaController.PARAMETRO_NUM_TARJETA);
			if (StringUtils.isBlank(numTarjeta) || !numTarjeta.matches("^(?=.*[0-9])[A-Za-z0-9]+$")) {
				throw new ValesFormatExcepcion();
			}

			String apiKey = variablesServices.getVariableAsString(Variables.WEBSERVICES_APIKEY);
			String uidActividad = sesion.getAplicacion().getUidActividad();
			ConsultarFidelizadoRequestRest paramConsulta = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
			paramConsulta.setNumeroTarjeta(numTarjeta);

			IskaypetResponseGetTarjetaRegaloRest result = null;
			try {
				result = IskaypetFidelizadoRest.getIskaypetTarjetaRegalo(paramConsulta);
			} catch (Exception e) {
				if (e instanceof RestHttpException) {
					if (!e.getMessage().toUpperCase().contains("NO SE HA ENCONTRADO LA TARJETA")) {
						throw e;
					}
				} else {
					throw e;
				}
			}

			GiftCardBean tarjetaRegalo = SpringContext.getBean(GiftCardBean.class);
			ValeDevolucion valeDevolucion = null;

			if (result != null) {

				((ValeDevManager) source).setConsultadoEnLocal(false);

				tarjetaRegalo.setNumTarjetaRegalo(result.getNumeroTarjeta());
				tarjetaRegalo.setBaja(result.getBaja().equals("S"));
				tarjetaRegalo.setActiva(result.getActiva().equals("S"));
				tarjetaRegalo.setSaldo(BigDecimal.valueOf(result.getSaldo()));
				tarjetaRegalo.setSaldoProvisional(result.getSaldoProvisional() != null ? BigDecimal.valueOf(result.getSaldoProvisional()) : BigDecimal.ZERO);
				tarjetaRegalo.setCodTipoTarjeta(result.getTipoTarjeta() != null ? result.getTipoTarjeta().getCodtipotarj() : null);

			} else {
				//GAP 176 Si la tarjeta no la encontramos en central y no hay problemas de conexion, la buscamos en tienda
				valeDevolucion = valesDevolucionService.consultarValeDevolucion(numTarjeta);

				List<PagoTicket> lstPagos = ticketManager.getTicket().getPagos();
				//Sacamos los vales de devolucion de la lista de pagos
				List<PagoTicket> lstValesDevolucion = lstPagos.stream().filter(pagoTicket -> pagoTicket.getCodMedioPago().equals(VALE_DEVOLUCION.getCodPago())).collect(Collectors.toList());

				BigDecimal importePagadoConValeDev = BigDecimal.ZERO;

				String numeroTarjetaConsultada = valeDevolucion.getNumeroTarjeta();
				//sacamos los pagos realizados con la tarjeta consultada
				for (PagoTicket pagoValeDev : lstValesDevolucion) {
					Optional<GiftCardBean> giftCardValeDev = pagoValeDev.getGiftcards().stream().filter(giftCard -> giftCard.getNumTarjetaRegalo().equalsIgnoreCase(numeroTarjetaConsultada)).findFirst();

					//Si está en el pago la tarjeta de vale devolucion a usar agregamos el importe del pago al acumulado
					if (giftCardValeDev.isPresent()) {
						pagoValeDev.getGiftcards().get(0).setSaldo(pagoValeDev.getGiftcards().get(0).getSaldo().subtract(importePagadoConValeDev));
						importePagadoConValeDev = importePagadoConValeDev.add(pagoValeDev.getImporte());
					}
				}

				((ValeDevManager) source).setConsultadoEnLocal(true);

				tarjetaRegalo.setNumTarjetaRegalo(valeDevolucion.getNumeroTarjeta());
				tarjetaRegalo.setBaja(false);
				tarjetaRegalo.setActiva(true);

				//Cargamos la tarjeta con el saldo disponible para los pagos
				if (BigDecimalUtil.isIgualACero(importePagadoConValeDev)) {
					tarjetaRegalo.setSaldo(valeDevolucion.getSaldoDisponible());
				} else {
					tarjetaRegalo.setSaldo(valeDevolucion.getSaldoDisponible().subtract(importePagadoConValeDev));
				}
				tarjetaRegalo.setSaldoProvisional(BigDecimal.ZERO);
				tarjetaRegalo.setCodTipoTarjeta("V");

			}

			if (tarjetaRegalo.isBaja()) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El vale introducido está dado de baja."), getStage());
			} else {
				MedioPagoBean medioPago = mediosPagosService.getMedioPago(source.getPaymentCode());

				lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()) + ")");
				lbMedioPago.setText(I18N.getTexto(medioPago.getDesMedioPago()));
				medioPagoSeleccionado = medioPago;

				GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(tarjetaRegalo);

				if (result != null) {

					if (result.getFechaActivacion() != null) {
						source.addParameter(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION, result.getFechaActivacion());
					}
					if (result.getFechaBaja() != null) {
						source.addParameter(ValeDevManager.PARAM_TARJETA_FECHA_BAJA, result.getFechaBaja());
					}

				} else {
					//GAP 176 si es vale devolucion consultado en tienda rellenamos parametros a partir del vale devolucion
					source.addParameter(ValeDevManager.PARAM_TARJETA_FECHA_ACTIVACION, valeDevolucion.getFechaActivacion());
				}
				if (tarjetaRegaloPago != null && valeDevolucion == null) {
					asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
				} else {
					lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()) + ")");
					if (Boolean.TRUE.equals(ticketManager.getTicket().getCabecera().esVenta())
                            && BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(), tarjetaRegalo.getSaldoTotal())) {
                        tfImporte.setText(FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()));
                    }
                }
			}

			source.addParameter(GiftCardManager.PARAM_TARJETA, tarjetaRegalo);

		} catch (Exception e) {
			log.error("askGiftCardNumber() - Ha habido un error al pedir el (vale) número de tarjeta: " + e.getMessage(), e);

			if (e instanceof RestHttpException) {
				// Personalización GAP 106 VALE DE DEVOLUCIÓN ENTRADA MANUAL
				String message = I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición.") + System.lineSeparator() + System.lineSeparator() + e.getMessage();
				VentanaDialogoComponent.crearVentanaError(getStage(), message, e);
				// Fin personalización
			} else if (e instanceof RestConnectException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido conectar con el servidor"), e);
			} else if (e instanceof RestTimeoutException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("El servidor ha tardado demasiado tiempo en responder"), e);
			} else if (e instanceof RestException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
			} else if (e instanceof ValesFormatExcepcion) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Vale incorrecto"), getStage());
			} else if (e instanceof ValesDevolucionNotFoundException) {
				if(Boolean.TRUE.equals(variablesServices.getVariableAsBoolean(PEDIR_VALE_MANUAL))){
					((ValeDevManager) source).setConsultadoEnLocal(false);
					valeDevolucionEntradaManual(source, numTarjeta);
				} else {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Vale no encontrado"), getStage());
				}
			}
			selectDefaultPaymentMethod();
		}
	}

	private boolean comprobarVMYaCanjeado(String numTarjeta) {
		boolean vmCanjeado = false;
		List<PagoTicket> lstPagos = ticketManager.getTicket().getPagos();
		List<PagoTicket> lstValesDevolucion = lstPagos.stream().filter(pagoTicket -> pagoTicket.getCodMedioPago().equals(VALE_DEVOLUCION.getCodPago())).collect(Collectors.toList());
		for (PagoTicket pagoValeDev : lstValesDevolucion) {
			Optional<GiftCardBean> giftCardValeDev = pagoValeDev.getGiftcards().stream().filter(giftCard -> giftCard.getNumTarjetaRegalo().equalsIgnoreCase(numTarjeta)).findFirst();
			if (giftCardValeDev.isPresent()) {
				vmCanjeado = true;
				break;
			}
		}
		return vmCanjeado;
	}

	private void valeDevolucionEntradaManual(PaymentMethodManager source, String numTarjeta) {
		try {
			if (BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente())) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe restante está completo."), getStage());
				return;
			}
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("No se ha encontrado el vale con número {0} \r\n¿Desea realiza la entrada manual?", numTarjeta), getStage())) {

				if(comprobarVMYaCanjeado(numTarjeta)) {
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Vale manual ya canjeado"), getStage());
					return;
				}

				BigDecimal importeDevManual;

				HashMap<String, Object> datos = new HashMap<>();
				datos.put(AutorizacionGerenteUtils.PARAMETRO_REQUIERE_GERENTE, true);
				AutorizacionGerenteUtils.muestraPantallaAutorizacion(getApplication().getMainView(), getStage(), datos);

				HashMap<String, Object> parametrosDevManual = new HashMap<>();
				parametrosDevManual.put(ValeDevolucionManualController.NUM_TARJETA_DEV_MANUAL, numTarjeta);
				getApplication().getMainView().showModalCentered(ValeDevolucionManualView.class, parametrosDevManual, getStage());

				if (parametrosDevManual.containsKey(ValeDevolucionManualController.CANCELADO)) {
					return;
				}
				importeDevManual = (BigDecimal) parametrosDevManual.get(ValeDevolucionManualController.IMPORTE_DEV_MANUAL);

				GiftCardBean tarjetaRegalo = SpringContext.getBean(GiftCardBean.class);
				tarjetaRegalo.setNumTarjetaRegalo(numTarjeta);
				tarjetaRegalo.setBaja(false);
				tarjetaRegalo.setActiva(true);
				tarjetaRegalo.setSaldoProvisional(BigDecimal.ZERO);
				tarjetaRegalo.setSaldo(importeDevManual);
				tarjetaRegalo.setSaldoProvisional(BigDecimal.ZERO);
				// Indicamos que es una VM nueva para cambiar luego su uidTransaccion a la hora de anotar el pago
				tarjetaRegalo.setCodTipoTarjeta("VM*");
                MedioPagoBean medioPago = mediosPagosService.getMedioPago(source.getPaymentCode());
                lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()) + ")");
                lbMedioPago.setText(I18N.getTexto(medioPago.getDesMedioPago()));
                medioPagoSeleccionado = medioPago;

                GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(tarjetaRegalo);

                if (tarjetaRegaloPago != null) {
                    asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
                }
                else {
                    lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()) + ")");
                    if (Boolean.TRUE.equals(ticketManager.getTicket().getCabecera().esVenta()) && BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(), tarjetaRegalo.getSaldoTotal())) {
                            tfImporte.setText(FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()));
                        }

                }

                source.addParameter(GiftCardManager.PARAM_TARJETA, tarjetaRegalo);
				anotarPagoValeDevManual(FormatUtil.getInstance().desformateaBigDecimal(tfImporte.getText()).abs());
			}
		}
		catch (InitializeGuiException e) {
			log.error("Error al inicializar la pantalla de entrada manual de vale devolución", e);
		}
	}

	public void anotarPagoValeDevManual(BigDecimal importe) {
		incluirPagoTicketValeDevManual(importe);

		visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
		escribirVisor();

		tfImporte.requestFocus();
	}

	protected void incluirPagoTicketValeDevManual(BigDecimal importe) {

		PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(sesion.getAplicacion().getTienda().getTiendaBean().getCodMedioPagoPorDefecto());
		if (Boolean.TRUE.equals(ticketManager.getTicket().getCabecera().esVenta())) {
			pay(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
		}
		else {
			returnAmount(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
		}
	}


	/*
	 * ##############################################################################################################
	 * ############################################ GAP62 - PEGATINAS PROMOCIONALES #################################
	 * ##############################################################################################################
	 */

	@Autowired
	protected PromotionStickerService promotionStickerService;

	@Override
	protected void accionSalvarTicketSucceeded(boolean repiteOperacion) {

		try {
			promotionStickerService.deletePromotionalSticker(ticketManager);

		}
		catch (Exception ignored) {
		}

        //GAP 176 Salvar vale devolucion en tienda
        List<PagoTicket> lstPagos = ticketManager.getTicket().getPagos();
        List<PagoTicket> lstValesDevolucion = lstPagos.stream().filter(pagoTicket -> pagoTicket.getCodMedioPago().equals(VALE_DEVOLUCION.getCodPago())).collect(Collectors.toList());
        try {
            if(lstValesDevolucion != null && !lstValesDevolucion.isEmpty()) {
                valesDevolucionService.guardarValesDevolucion((IskaypetTicketManager)ticketManager, lstValesDevolucion);
            }
        }catch(Exception e) {
            VentanaDialogoComponent.crearVentanaError(getStage(), "Ha ocurrido un error al intentar guardar el vale de devolución en tienda", e);
        }

		if (ticketManager instanceof IskaypetTicketManager && ((IskaypetTicketManager) ticketManager).esTicketProforma()) {
			ICabeceraTicket cabeceraTicket = ticketManager.getTicket().getCabecera();
			if (cabeceraTicket instanceof IskaypetCabeceraTicket) {
				proformaRestService.facturarProformaEnSegundoPlano(sesion, ((IskaypetCabeceraTicket) cabeceraTicket).getProforma().getIdProforma());
			}
		}
        super.accionSalvarTicketSucceeded(repiteOperacion);
	}

	@FXML
	protected void accionEditarObservaciones() {
		HashMap<String, Object> datos = new HashMap<>();

		String observaciones = lbObservacionesDetalle.getText();
		observaciones = StringUtils.defaultIfBlank(observaciones, "");

		if (!observaciones.equals(I18N.getTexto(PARAMETRO_OBSERVACIONES_VACIO))) {
			datos.put(PAGO_OBSERVACIONES, observaciones);
		}

		getApplication().getMainView().showModalCentered(PagoObservacionesView.class, datos, getStage());
		lbObservacionesDetalle.setText(datos.containsKey(PAGO_OBSERVACIONES) ? (String) datos.get(PAGO_OBSERVACIONES) : I18N.getTexto(PARAMETRO_OBSERVACIONES_VACIO));
	}

	@FXML
	protected void accionEliminarObservaciones() {

		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de eliminar las observaciones?"), getStage())) {
			lbObservacionesDetalle.setText(I18N.getTexto(PARAMETRO_OBSERVACIONES_VACIO));
		}
	}

	public void adicionarNIF() {
		String mensaje = I18N.getTexto("¿Necesita el cliente el NIF en el recibo de venta?");
		if (VentanaDialogoComponent.crearVentanaConfirmacion(mensaje, getStage(), I18N.getTexto("Sí"), I18N.getTexto("No"))) {
			FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			if (fidelizado != null) {
				// Verificamos que el cliente tenga un documento de identificación valido
				if (!validarValoresObligatoriosIdentificacionClienteVenta(fidelizado) || !validarDocumentoIdentificacionClienteVenta(fidelizado)) {
					getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
					getStage().close();
					return;
				}
				modificarNifCabecera(fidelizado.getDocumento());
				lbNifDetalle.setText(fidelizado.getDocumento());
				btEditarNif.setDisable(true);
				btEliminarNif.setDisable(false);
			}
			else {
				accionEditarNif();
			}
		}
		else {
			FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			if (fidelizado != null) {
				btEditarNif.setDisable(true);
				btEliminarNif.setDisable(true);
			}
		}
	}

	private boolean validarValoresObligatoriosIdentificacionClienteVenta(FidelizacionBean fidelizado) {
		List<String> errorMessages = new ArrayList<>();
		if (StringUtils.isBlank(fidelizado.getCodTipoIden()))
			errorMessages.add(I18N.getTexto("tipo de documento"));
		if (StringUtils.isBlank(fidelizado.getDocumento()))
			errorMessages.add(I18N.getTexto("documento"));
		if (StringUtils.isBlank(fidelizado.getCodPais()))
			errorMessages.add(I18N.getTexto("país"));

		if (errorMessages.size() == 1) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Es necesario rellenar el siguiente dato en la ficha del cliente: ") + String.join(", ", errorMessages), getStage());
			return false;
		}
		else if (errorMessages.size() > 1) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Es necesario rellenar los siguientes datos en la ficha del cliente: ") + String.join(", ", errorMessages), getStage());
			return false;
		}
		return true;
	}

	public boolean validarDocumentoIdentificacionClienteVenta(FidelizacionBean fidelizado) {
		try {

			List<TiposIdentBean> tiposDocumentos = tiposIdentService.consultarTiposIdent(null, true, fidelizado.getCodPais().toUpperCase());
			TiposIdentBean tipoDocumento = tiposDocumentos.stream().filter(t -> t.getCodTipoIden().equalsIgnoreCase(fidelizado.getCodTipoIden())).findFirst().orElse(null);
			if (tipoDocumento != null && tipoDocumento.getClaseValidacion() != null) {
				IValidadorDocumentoIdentificacion validarDocumento = (IValidadorDocumentoIdentificacion) Class.forName(tipoDocumento.getClaseValidacion()).newInstance();
				if (!validarDocumento.validarDocumentoIdentificacion(fidelizado.getDocumento())) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Es necesario corregir el documento en la ficha del cliente"), getStage());
					return false;
				}
			}
		}
		catch (Exception ignored) {
		}

		return true;

	}

	public void accionEditarNif() {
		HashMap<String, Object> datos = new HashMap<>();
		if (!lbNifDetalle.getText().equalsIgnoreCase(PAGO_NIF_DOC_DEFAULT)) {
			datos.put(PAGO_NIF, lbNifDetalle.getText());
		}
		getApplication().getMainView().showModalCentered(PagoNifPtView.class, datos, getStage());
		if (datos.containsKey(PAGO_NIF)) {
			String nif = (String) datos.get(PAGO_NIF);
			lbNifDetalle.setText(nif);
			modificarNifCabecera(nif);
		}
	}

	public void accionEliminarNif() {
		modificarNifCabecera(null);
		lbNifDetalle.setText(PAGO_NIF_DOC_DEFAULT);
		FidelizacionBean fidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if (fidelizado != null) {
			btEditarNif.setDisable(true);
			btEliminarNif.setDisable(true);
		}
	}

	private void modificarNifCabecera(String nif) {
		ICabeceraTicket cabecera = ticketManager.getTicket().getCabecera();
		if (cabecera instanceof IskaypetCabeceraTicket) {
			((IskaypetCabeceraTicket) cabecera).setDocumentoCliente(nif);
		}
	}

	/*
	 * ##############################################################################################################
	 * ######################### GAP 169 DEVOLUCIÓN DE PROMOCIONES ##################################################
	 * ##############################################################################################################
	 */

	public void devolucionDePromociones() {
		//en devolcuiones no se hace prorrateo
		if(ticketManager.getTicket().isEsDevolucion()){
			generarCuponesDevolucion();
			return;
		}

		HashMap<String, AcumuladosPromo> mapTotalPromociones = new HashMap<String, AcumuladosPromo>();
		Integer indicePromoNXM = 0;
		log.debug("devolucionDePromociones() - Recorriendo promociones aplicadas en el ticket para acumular descuentos");
		//recorremos las promociones aplicadas en el ticket para acumular el importe total de ahorro,
		//ya que por ejemplo las NxM aparecen tantas veces como combos aplicados
		if(ticketManager.getTicket().getPromociones() != null) {

            //recorremos las promociones y vamos acumulando los descuentos en el mapa
            for (PromocionTicket promocionTicket : (Iterable<PromocionTicket>) ticketManager.getTicket().getPromociones()) {
                //Si es descuento tipo 2 (descuento compra futura) no debemos agregarlo al mapa por petición del cliente
                // ya que no quieren realizar el prorrateo en devoluciones para este tipo de descuento
                if (promocionTicket.getTipoDescuento() != 2L) {
                    //si no lo tenemos en el mapa la insertamos
                    if (!mapTotalPromociones.containsKey(promocionTicket.getIdPromocion().toString()) || promocionTicket.getIdTipoPromocion() == 2L) {
                        AcumuladosPromo infoPromo = new AcumuladosPromo();
                        infoPromo.setIdPromocion(promocionTicket.getIdPromocion());
                        infoPromo.setIdTipoPromo(promocionTicket.getIdTipoPromocion());
                        infoPromo.setTotalAcumulado(promocionTicket.getImporteTotalAhorro());

                        String key = promocionTicket.getIdPromocion().toString();

                        //Si es NxM inicializamos las lineas de promo ahorro
                        if (promocionTicket.getIdTipoPromocion() == 2L) {

                            List<Integer> idLineas = new ArrayList<Integer>();

                            for (Entry<String, Object> entry : promocionTicket.getAdicionales().entrySet()) {

                                if (entry.getKey().startsWith("linea")) {
                                    idLineas.add((Integer) entry.getValue());
                                }
                            }

                            infoPromo.setLineasAplicadas(idLineas);

                            //Agregamos al valor de la key (idpromocion) + * el valor del indice.
                            key += "*" + indicePromoNXM.toString();
                            indicePromoNXM++;
                        }
                        mapTotalPromociones.put(key, infoPromo);
                        log.debug("devolucionDePromociones() - Nueva promoción acumulada: key=" + key + ", totalAhorro=" + infoPromo.getTotalAcumulado());

                    } else {//si ya la tenemos en el mapa sumamos el total de ahorro
                        AcumuladosPromo infoPromo = mapTotalPromociones.get(promocionTicket.getIdPromocion().toString());
                        infoPromo.setTotalAcumulado(infoPromo.getTotalAcumulado().add(promocionTicket.getImporteTotalAhorro()));
                    }
                }

            }
		}

		 //recorremos el mapa con los descuentos de las promociones acumulados y calculamos el prorrateo
		 for (Entry<String, AcumuladosPromo> entry : mapTotalPromociones.entrySet()) {

				String idPromocionString = entry.getKey();
				Long idTipoPromocion = entry.getValue().getIdTipoPromo();
				boolean prorrateoNecesario = isProrrateoNecesario(idPromocionString, idTipoPromocion);

				//llamamos a la función que realiza el reparto de descuentos en las líneas
				calculaProrrateoPromocion(idPromocionString, prorrateoNecesario,entry.getValue());
		 }

	 	//Prorrteamos el valor de los cupones en las lineas
	 	recalculoImporteProrrateoCupones();
	}

	//calcula el importe de los cupones a generar en la devolución
	private void recalculoImporteProrrateoCupones() {
		for(IskaypetLineaTicket linea : (List<IskaypetLineaTicket>)ticketManager.getTicket().getLineas()) {
			if(linea.getDtoPromociones() != null) {
				BigDecimal precioFinalLinea = linea.getPrecioTotalSinDto();
				BigDecimal importeCupon = BigDecimal.ZERO;
				boolean cuponAplicado = false;
				for (DtoPromocion dtoPromocion : linea.getDtoPromociones()) {
					if(dtoPromocion.getItTipoPromocion()!= 12L) {
						precioFinalLinea = precioFinalLinea.subtract(dtoPromocion.getImporteTotalDtoProrrateado());
					}else {
						//acumulamos el importe del cupón y establecmeos el prorrateo a 0 para contemplar el caso de uso de varios cupones en la venta
						importeCupon = importeCupon.add(dtoPromocion.getImporteTotalDtoProrrateado());
						dtoPromocion.setImporteTotalDtoProrrateado(BigDecimal.ZERO);
						cuponAplicado = true;
					}
				}

				//si el descuento del cupon en la línea es mayor que el resultante tras quitar los prorrateos,
				//el importe del cupon deber ese resultante.
				if(BigDecimalUtil.isMayor(importeCupon, precioFinalLinea) ||
					(cuponAplicado && BigDecimalUtil.isIgualACero(importeCupon))) {
					importeCupon = precioFinalLinea;
				}

				//establecemos el importe del cupon al primer registro de cupon en el porrateo, el resto se quedará a 0
				for (DtoPromocion dtoPromocion : linea.getDtoPromociones()) {
					if(dtoPromocion.getItTipoPromocion()== 12L) {
						dtoPromocion.setImporteTotalDtoProrrateado(importeCupon);
						break;
					}
				}
			}
		}

	}

	//el prorrateo lo hacemos con las promos de tipo 2:NxM, 5:Dto combindao de lineas con condiciones de artículo y 7:descuento combinado de cabecera
	public boolean isProrrateoNecesario(String idPromocionString, Long idTipoPromocion) {
		boolean prorrateoNecesario = false;

		if(idTipoPromocion == 2 || idTipoPromocion == 5 || idTipoPromocion == 7)
		{
			prorrateoNecesario = true;

			//en caso del descuento combinado de lína comprobamos si debemos realizar el prorrateo, ya que solo lo haremos cuando tengamos articulos en condición
			if(idTipoPromocion == 5) {
				//cardamos las condiciones de la promocion
				//si tenemos artículos en las condiciones debemos calcular el prorrateo
				dtoPromocionesService.cargarPromocion(new Long(idPromocionString));

				prorrateoNecesario = dtoPromocionesService.getCodArtArticulosCondidion().size() > 0;
			}else {
				//El objeto se queda cargado si se aplica luego otra promo de otro tipo lo eliminamos para sus posteriores comprobaciones
				dtoPromocionesService.getCodArtArticulosCondidion().clear();
			}
		}
		return prorrateoNecesario;
	}

	protected Long stringToLongSplit(String idPromocionString) {
		String idPromocionAux = idPromocionString.split("\\*")[0];
		return new Long(idPromocionAux);

	}

	protected void calculaProrrateoPromocion(String idPromocionString, Boolean prorrateoNecesario, AcumuladosPromo acumuladosPromo) {

		//split para NXM para sacar el idPromocion sin el *
		Long idPromocion = stringToLongSplit(idPromocionString);
		log.debug("calculaProrrateoPromocion() - Iniciando cálculo para promoción id=" + idPromocion);

		if(prorrateoNecesario) {
			//Guuardamos las lineas susceptibles de dto en esta lista para no recorrer nuevament el ticket
			List<IskaypetLineaTicket> lineasDeLaPromocion = new ArrayList<IskaypetLineaTicket>();
			//1.-si hay que prorratear debemos obtener el total sin dto de los artículo implicados en la promo
			BigDecimal totalArticulosPromoSinDto = BigDecimal.ZERO;
			for(IskaypetLineaTicket linea : (List<IskaypetLineaTicket>)ticketManager.getTicket().getLineas()) {
				if(linea.getPromociones() != null){
					for (PromocionLineaTicket promo : linea.getPromociones()) {
						if(promo.getIdPromocion().equals(idPromocion)) {
							//si tipo promo 2 y la linea no está en el acumulado --> continue;
							if(promo.getIdTipoPromocion() == 2) {
								boolean esLineaAcumPromocion = acumuladosPromo.getLineasAplicadas().stream().anyMatch(lineaAcumPromo -> lineaAcumPromo == linea.getIdLinea());
								if(!esLineaAcumPromocion) {
									continue;
								}
							}
							totalArticulosPromoSinDto = totalArticulosPromoSinDto.add(linea.getPrecioTotalSinDto());
							lineasDeLaPromocion.add(linea);
							break;
						}
					}
				}
			}
			log.debug("calculaProrrateoPromocion() - Total de artículos sin DTO para promo " + idPromocion + " = " + totalArticulosPromoSinDto);

			//Comprobamos si hay condicion para los articulos si existen debemos buscarlos
			if(dtoPromocionesService.getCodArtArticulosCondidion() != null && !dtoPromocionesService.getCodArtArticulosCondidion().isEmpty()) {
				int vecesPromocionAplicada = 0, numCondionantesRevisar = 0;

				//Primero ver las veces que se aplica la promocion en todo el ticket
				for(IskaypetLineaTicket lineaCl : (List<IskaypetLineaTicket>)ticketManager.getTicket().getLineas()) {
					if (lineaCl.getPromociones() != null) {
						for (PromocionLineaTicket promo : lineaCl.getPromociones()) {
							if(Objects.equals(promo.getIdPromocion(), idPromocion)) {
								vecesPromocionAplicada++;
							}
						}
					}
				}

				if (dtoPromocionesService.getCantidadAplicacion() != null && !BigDecimalUtil.isIgualACero(dtoPromocionesService.getCantidadAplicacion())) {
					// Redondeamos hacia arriba, sin decimales, si existen significa que hay articulos que aplican pero no están todos y hay que tener un cuenta el desencadenante
				    log.debug("calculaProrrateoPromocion() - Veces que se aplica la promoción: " + vecesPromocionAplicada);
					BigDecimal nPromocionAplicada = new BigDecimal(vecesPromocionAplicada);
					if (!BigDecimalUtil.isIgual(dtoPromocionesService.getCantidadAplicacion(), BigDecimal.ZERO)) {
						nPromocionAplicada = nPromocionAplicada.divide(dtoPromocionesService.getCantidadAplicacion(),RoundingMode.UP);
						log.debug("calculaProrrateoPromocion() - nPromocionAplicada tras división: " + nPromocionAplicada);
					}
					numCondionantesRevisar = nPromocionAplicada.intValue();
				}
				else {
					//en caso de que la cantidad de aplicación sea 0, siginifa que la promo se aplica sin limite y solo
					//necesitamos que se valide una vez la condicion de artículos
					numCondionantesRevisar = 1;
				}

				//Si la promo indidica una cantidad de condicion, multiplicamos lel numero de condiciones a revisar por esa cantidad de condicion
				if(dtoPromocionesService.getCantidadCondicion() != null && !BigDecimalUtil.isIgualACero(dtoPromocionesService.getCantidadCondicion())) {
					numCondionantesRevisar = dtoPromocionesService.getCantidadCondicion().intValue() * numCondionantesRevisar;
				}

				int numCondicionatesRevisados = 0;
				for (IskaypetLineaTicket lineaCl : (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas()) {
					//Compurebo si los articulos de la condicion de la promo es la linea sin promoción
					if (dtoPromocionesService.getCodArtArticulosCondidion().contains(lineaCl.getCodArticulo())) {
						//comprobamos que si el artículo tiene aplicada la promocion recibida por parámetros
						boolean promoAplicada = false;
						for (PromocionLineaTicket promo : lineaCl.getPromociones()) {
							if(Objects.equals(promo.getIdPromocion(), idPromocion)) {
								promoAplicada = true;
								break;
							}
						}

						//si tenemos la promoción aplicada, no podemos añadirlo a la lista, ya que se añadio en el bucle anterior
						if (!promoAplicada && numCondionantesRevisar > numCondicionatesRevisados) {
							//Inicializamos el dto con la promocion que esta aplicacando y ya existe referencia a la promocion
							totalArticulosPromoSinDto = totalArticulosPromoSinDto.add(lineaCl.getPrecioTotalSinDto());
							numCondicionatesRevisados++;
							lineasDeLaPromocion.add(lineaCl);
						}
					    //si ya hemos revisado todos los condicionantes nos salimos del bucle
						if(numCondionantesRevisar <= numCondicionatesRevisados) {
							break;
						}
					}
				}
			}

			//2.-Calculamos el importe de descuento prorrateado
			BigDecimal totalProrrateoAcumulado = BigDecimal.ZERO;
			for (IskaypetLineaTicket lineaPromocion : lineasDeLaPromocion) {
				totalProrrateoAcumulado = totalProrrateoAcumulado.add(calcularImportePromocionProrrateadoLinea(lineaPromocion, idPromocion, totalArticulosPromoSinDto, acumuladosPromo));
			}
	        log.debug("calculaProrrateoPromocion() - Total descuento prorrateado acumulado=" + totalProrrateoAcumulado);
			if(!BigDecimalUtil.isIgual(totalProrrateoAcumulado,acumuladosPromo.getTotalAcumulado())) {
				BigDecimal diferencia = acumuladosPromo.getTotalAcumulado().subtract(totalProrrateoAcumulado);
	            log.debug("calculaProrrateoPromocion() - Diferencia en prorrateo=" + diferencia);
				if(!lineasDeLaPromocion.isEmpty()) {
					for(DtoPromocion dto :  lineasDeLaPromocion.get(lineasDeLaPromocion.size()-1).getDtoPromociones()) {
						if(Objects.equals(dto.getIdPromocion(), idPromocion)) {
							dto.setImporteTotalDtoProrrateado(BigDecimalUtil.redondear(dto.getImporteTotalDtoProrrateado().add(diferencia),2));
							break;
						}
					}
				}
			}
		}
		else { //en el caso de que no haya prorrateo cargamos el importe de descuento de la promocion
			log.debug("calculaProrrateoPromocion() - Prorrateo no necesario, asignando descuento directamente a cada línea");
			for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas()) {
				if (linea.getPromociones() != null) {
					PromocionLineaTicket promoAplicada = null;
					BigDecimal dtoAplicado = BigDecimal.ZERO;
					for (PromocionLineaTicket promo : linea.getPromociones()) {
						if(Objects.equals(promo.getIdPromocion(), idPromocion)) {
							promoAplicada = promo;
							dtoAplicado = dtoAplicado.add(promo.getImporteTotalDtoMenosMargen().add(promo.getImporteTotalDtoMenosIngreso()));
						}
					}
					if (promoAplicada != null && !BigDecimalUtil.isIgualACero(dtoAplicado)) {
						log.debug("calculaProrrateoPromocion() - Asignando descuento directo a línea " + linea.getIdLinea() + ": dto=" + dtoAplicado);
						setDescuentoLinea(linea, promoAplicada, dtoAplicado);
					}

				}
			}
		}
	}

	private void setDescuentoLinea(IskaypetLineaTicket linea, PromocionLineaTicket promo, BigDecimal dto) {
		//Usamos los dos margenes de descuento. Si es uno el otro vendrá a 0
	    log.debug("setDescuentoLinea() - Calculado dto para promoción " + promo.getIdPromocion() + ": " + dto);
		//si es una promocion de aplicacion de cupon y el importe de descuento es creo no la incluimos
		if (promo.getIdTipoPromocion() == 12L && BigDecimalUtil.isIgualACero(dto)) {
			log.debug("setDescuentoLinea() - Descuento cero para promoción de cupón, se omite.");
			return;
		}

		if(linea.getDtoPromociones() == null) {
			linea.setDtoPromociones(new ArrayList<>());
		}
		DtoPromocion dtoPromocion = new DtoPromocion();
		dtoPromocion.setIdPromocion(promo.getIdPromocion());
		dtoPromocion.setItTipoPromocion(promo.getIdTipoPromocion());
		dtoPromocion.setPrecioSinDto(linea.getPrecioSinDto());
		dtoPromocion.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
		dtoPromocion.setImporteTotalDtoProrrateado(BigDecimalUtil.redondear(dto, 2, 0));
		dtoPromocion.setAccesoCupon(StringUtils.isNotBlank(promo.getAcceso()) && StringUtils.equals("CUPON", promo.getAcceso())? Boolean.TRUE: Boolean.FALSE);
		linea.getDtoPromociones().add(dtoPromocion);
		log.debug("setDescuentoLinea() - Asignado descuento en línea " + linea.getIdLinea() + ": dto=" + dto);
	}

	private BigDecimal calcularImportePromocionProrrateadoLinea(IskaypetLineaTicket linea, Long idPromo, BigDecimal importeTotalAcumuladoSinDto, AcumuladosPromo acumuladosPromo) {

		//Si no hay informacion creamos la linea de descuento
		if(linea.getDtoPromociones() == null) {
			linea.setDtoPromociones(new ArrayList<DtoPromocion>());
		}

		//Calculamos el peso
		BigDecimal peso = (linea.getPrecioTotalSinDto().multiply(new BigDecimal(100)));
		if (!BigDecimalUtil.isIgual(importeTotalAcumuladoSinDto, BigDecimal.ZERO)) {
			peso = peso.divide(importeTotalAcumuladoSinDto, 4, RoundingMode.HALF_UP);
		}
		peso = peso.setScale(4, RoundingMode.HALF_UP);


		//Calculamos en dto para la linea
		BigDecimal importeDescuentoProrrateado = BigDecimalUtil.redondear(acumuladosPromo.getTotalAcumulado().multiply((peso.divide(new BigDecimal(100)))),2);

		DtoPromocion dtoPromocion = new DtoPromocion();
		dtoPromocion.setIdPromocion(idPromo);
		dtoPromocion.setItTipoPromocion(acumuladosPromo.getIdTipoPromo());
		dtoPromocion.setImporteTotalDtoProrrateado(importeDescuentoProrrateado);
		dtoPromocion.setPrecioSinDto(linea.getPrecioSinDto());
		dtoPromocion.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
		dtoPromocion.setAccesoCupon(acumuladosPromo.isAccesoCupon());
		//Lo asiganmos a la linea del ticket
		linea.getDtoPromociones().add(dtoPromocion);

		return importeDescuentoProrrateado;
	}

	//GAP 169 DEVOLUCIÓN DE PROMOCIONES
	public void generarCuponesDevolucion() {
		if (ticketManager.getTicketOrigen() == null) {
			log.warn("generarCuponesDevolucion() - No se puede generar cupones de devolución porque no hay ticket origen.");
			return;
		}

		if (!ticketManager.isEsDevolucion()) {
			log.warn("generarCuponesDevolucion() - No se puede generar cupones de devolución porque el ticket no es una devolución.");
			return;
		}

		HashMap<Long, BigDecimal> cuponesDevolucion = obtenerCuponesDevolucion();
		generarCuponesDevolucion(cuponesDevolucion);
		log.debug("generarCuponesDevolucion() - Todos los cupones de devolución generados correctamente.");
	}

	private HashMap<Long, BigDecimal> obtenerCuponesDevolucion() {
		log.debug("obtenerCuponesDevolución() - Iniciando obtención de cupones de devolución.");
		HashMap<Long, BigDecimal> cuponesDevolucion = new HashMap<>();
		for (IskaypetLineaTicket linea : (List<IskaypetLineaTicket>) ticketManager.getTicket().getLineas()) {
			log.debug("obtenerCuponesDevolución() - Procesando línea: " + linea.getIdLinea());
			IskaypetLineaTicket lineaOrigen = (IskaypetLineaTicket) ticketManager.getTicketOrigen().getLinea(linea.getLineaDocumentoOrigen());
			if (lineaOrigen != null && lineaOrigen.getDtoPromociones() != null) {
				log.debug("obtenerCuponesDevolución() - Línea origen encontrada con id: " + lineaOrigen.getIdLinea());
				for (DtoPromocion dtoPromocionCupon : lineaOrigen.getDtoPromociones()) {
					log.debug("obtenerCuponesDevolución() - Procesando dtoPromocion: " + dtoPromocionCupon);
					if (aplicaCuponDevolucion(dtoPromocionCupon) && esPromoOrigenVigente(dtoPromocionCupon.getIdPromocion())) {
						log.debug("obtenerCuponesDevolución() - Aplicando cupon de devolución para dtoPromocion: " + dtoPromocionCupon);
						Long idPromocionOrigen = dtoPromocionCupon.getIdPromocion();
						BigDecimal importeCuponDevolucion = cuponesDevolucion.getOrDefault(idPromocionOrigen, BigDecimal.ZERO);
						importeCuponDevolucion = importeCuponDevolucion.add(dtoPromocionCupon.getImporteTotalDtoProrrateado());
						cuponesDevolucion.put(idPromocionOrigen, importeCuponDevolucion);
						log.debug("obtenerCuponesDevolución() - Acumulado importe cupon de devolución para idPromocion " + idPromocionOrigen + ": " + importeCuponDevolucion);
					}
					log.debug("obtenerCuponesDevolución() - dtoPromocion procesado: " + dtoPromocionCupon);
				}
				log.debug("obtenerCuponesDevolución() - Línea procesada: " + linea.getIdLinea());
			}
			log.debug("obtenerCuponesDevolución() - Línea " + linea.getIdLinea() + " procesada correctamente.");
		}
		log.debug("obtenerCuponesDevolución() - Finalizada la obtención de cupones de devolución. Total cupones: " + cuponesDevolucion.size());
		return cuponesDevolucion;
	}

	private void generarCuponesDevolucion(HashMap<Long, BigDecimal> cuponesDevolucion) {
		log.debug("generarCuponesDevolucion() - Generando cupones de devolución para " + cuponesDevolucion.size() + " promociones.");
		for (Entry<Long, BigDecimal> entry : cuponesDevolucion.entrySet()) {
			log.debug("generarCuponesDevolucion() - Generando cupon de devolución para idPromocion " + entry.getKey() + " con importe: " + entry.getValue());
			Long idPromocionOrigen = entry.getKey();
			BigDecimal importeCupon = entry.getValue();
			if (BigDecimalUtil.isMayorACero(importeCupon)) {
				log.debug("generarCuponesDevolucion() - Generando cupon de devolución para idPromocion " + idPromocionOrigen + " con importe: " + importeCupon);
				generarCuponDevolucion(idPromocionOrigen, importeCupon);
				log.debug("generarCuponesDevolucion() - Cupon de devolución generado para idPromocion " + idPromocionOrigen);
			}
		}
		log.debug("generarCuponesDevolucion() - Finalizado el proceso de generación de cupones de devolución.");
	}

	private void generarCuponDevolucion(Long idPromocionOrigen, BigDecimal importeCupon) {
		log.debug("generarCuponDevolucionIndividual() - Generando cupon de devolución individual para idPromocion " + idPromocionOrigen + " con importe: " + importeCupon);
		String divisa = getDivisa();
		try {
			CuponEmitidoTicket cuponEmitidoDevolucion = cuponesPuntosService.generarCuponPuntos(importeCupon, ticketManager.getTicket(), divisa);
			((IskaypetCuponEmitidoTicket) cuponEmitidoDevolucion).setCodCuponOrigen(getCodCuponOrigen(idPromocionOrigen, cuponEmitidoDevolucion.getImporteCupon()));
			((TicketVentaAbono) ticketManager.getTicket()).addCuponEmitido(cuponEmitidoDevolucion);
			log.debug("generarCuponDevolucionIndividual() - Cupon de devolución generado correctamente.");
		} catch (Exception e) {
			log.error("generarCuponDevolucionIndividual() - Error generando un cupon por diferencia en la devolución del ticket: " + e.getMessage());
		}
	}

	private String getDivisa() {
		log.debug("getDivisa() - Obteniendo divisa del ticket.");
		String divisa = "";
		if (StringUtils.isNotBlank(((IskaypetCabeceraTicket) ticketManager.getTicket().getCabecera()).getDivisa())
				&& ((IskaypetCabeceraTicket) ticketManager.getTicketOrigen().getCabecera()).getDivisa().equals("EUR")) {
			log.debug("getDivisa() - Divisa del ticket es EUR.");
			divisa = "€";
		}
		log.debug("getDivisa() - Divisa obtenida: " + divisa);
		return divisa;
	}


	private boolean aplicaCuponDevolucion(DtoPromocion dtoPromocionCupon) {
		log.debug("aplicaCuponDevolucion() - Comprobando si aplica cupon de devolución para dtoPromocion: " + dtoPromocionCupon);
		if (dtoPromocionCupon.getItTipoPromocion() == null) {
			log.debug("aplicaCuponDevolucion() - Tipo de promoción es nulo, no aplica cupon de devolución.");
			return false;
		}

		if (dtoPromocionCupon.getItTipoPromocion() == 12L || dtoPromocionCupon.isAccesoCupon()) {
			log.debug("aplicaCuponDevolucion() - Tipo de promoción es cupon o acceso a cupon, aplica cupon de devolución.");
			return true;
		}

		log.debug("aplicaCuponDevolucion() - Tipo de promoción no es cupon ni acceso a cupon, no aplica cupon de devolución.");
		return false;
	}

    private boolean esPromoOrigenVigente(Long idPromocion) {
        log.debug("esPromoOrigenVigente() - Comprobando si la promoción origen es vigente para idPromocion: " + idPromocion);
        try {
            PromocionBean promocion = promocionesService.getPromotion(idPromocion);
            if (promocion != null && promocion.getFechaFin() != null) {
                Instant fechaFin = promocion.getFechaFin().toInstant();
                Instant ahora = Instant.now();
                if (!fechaFin.isBefore(ahora)) {
                    log.debug("esPromoOrigenVigente() - Promoción origen es vigente.");
                    return true;
                }
            }
        } catch (PromocionesServiceException e) {
            log.error("esPromoOrigenVigente() - Error al comprobar la vigencia de la promoción origen: " + e.getMessage());
            return false;
        }
        log.debug("esPromoOrigenVigente() - Promoción origen no es vigente.");
        return false;
    }

    private String getCodCuponOrigen(long idPromocionOrigen, BigDecimal importeDevolucion) {
		for(CuponAplicadoTicket cuponAplicado : ((IskaypetTicketVentaAbono) ticketManager.getTicketOrigen()).getCuponesAplicados()){
			if((Objects.equals(idPromocionOrigen, cuponAplicado.getIdPromocion()))
					&& (cuponAplicado.getImporteTotalAhorrado().compareTo(importeDevolucion) == 0)){
				return cuponAplicado.getCodigo();
			}
		}
		return null;
	}

	public void accionPagosOrigen() {

		if (!isDevolucion()) {
			String message = I18N.getTexto("Solo se pueden consultar los pagos de origen en una devolución.");
			VentanaDialogoComponent.crearVentanaAviso(message, getStage());
		}

		if (((IskaypetTicketManager) ticketManager).getDatosOrigenDevolucionSinOrigen() != null) {
			String message = I18N.getTexto("No se puede consultar los pagos de origen en una devolución sin origen.");
			VentanaDialogoComponent.crearVentanaAviso(message, getStage());
			return;
		}

		HashMap<String, Object> datos = new HashMap<>();
		datos.put(TICKET, ticketManager.getTicketOrigen());
		getApplication().getMainView().showModalCentered(PagosOrigenView.class, datos, getStage());

	}
	/* ######################################################################################################################### */
	/* ###################### GAP 172 TRAZABILIDAD ANIMALES #################################################################### */
	/* ######################################################################################################################### */
	private boolean isFacturaCompletaVentaAnimal() {
		//Comprobamos si hay mascotas en el ticket, de haberlas se requiere factura completa
		boolean requiereFacturaCompleta = false;
		for (Object lineaTicket : ticketManager.getTicket().getLineas()) {
			IskaypetLineaTicket linea = (IskaypetLineaTicket) lineaTicket;
			if (linea.isMascota() && linea.isRequiereMascotaFacturaCompleta()) {
				requiereFacturaCompleta = true;
				break;
			}
		}
		//Comprobamos que sea factura completa y que la requiere de ser así no dejamos continuar hasta que este realizada la factura completa
		if (!ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA) && requiereFacturaCompleta) {
			String mensaje = I18N.getTexto("Para poder continuar con la venta de animales es necesario que sea factura completa");
			VentanaDialogoComponent.crearVentanaAviso(mensaje, getStage());
		}else {
			requiereFacturaCompleta = false;
		}

		return requiereFacturaCompleta;
	}

	private boolean isFacturaCompletaVentaPlanesSalud() {
		log.debug("isFacturaCompletaVentaPlanesSalud() - Comprobando si es necesario factura completa para planes de salud");

		// Comprobamos si el documento activo es una factura completa
		if (ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.FACTURA_COMPLETA)) {
			log.debug("isFacturaCompletaVentaPlanesSalud() - El documento activo es una factura completa, no es necesario continuar");
			return false;
		}

		if (contienePlanesSalud()) {
			log.debug("isFacturaCompletaVentaPlanesSalud() - El ticket contiene planes de salud, se requiere factura completa");
			String mensaje = I18N.getTexto("Para poder continuar con la venta de planes de salud es necesario que sea factura completa");
			VentanaDialogoComponent.crearVentanaAviso(mensaje, getStage());
			return true;
		}

		log.debug("isFacturaCompletaVentaPlanesSalud() - El ticket no contiene planes de salud, no es necesario continuar");
		return false;
	}

	private boolean contienePlanesSalud() {
		log.debug("contienePlanesSalud() - Comprobando si el ticket contiene planes de salud");
		// Comprobamos si estan configuradada la variable de los planes de salud
		String planesSalud = variablesServices.getVariableAsString("X_POS.FAMILIA_PLANES_DE_SALUD");
		if (StringUtils.isBlank(planesSalud)) {
			log.debug("contienePlanesSalud() - No se ha configurado la variable de planes de salud, no se puede continuar");
			return false;
		}

		// Comprobamos si hay planes de salud en el ticket
		for (Object lineaTicket : ticketManager.getTicket().getLineas()) {
			log.debug("contienePlanesSalud() - Comprobando linea de ticket: " + lineaTicket);
			IskaypetLineaTicket linea = (IskaypetLineaTicket) lineaTicket;
			if (Arrays.asList(planesSalud.split(";")).contains(linea.getArticulo().getCodFamilia())) {
				log.debug("contienePlanesSalud() - Se ha encontrado un plan de salud en la línea de ticket: " + linea.getArticulo().getCodFamilia());
				return true;
			}
		}
		log.debug("contienePlanesSalud() - No se han encontrado planes de salud en el ticket");
		return false;
	}

	/* ######################################################################################################################### */
	/* ############################## CZZ-120 AGRUPACIÓN MEDIOS DE PAGO ######################################################## */
	/* ######################################################################################################################### */
	public void toggleBotonMedioPagoOtros() {

		log.debug("toggleBotonMedioPagoOtros - Se ha pulsado el botón de pago otros");

		ObservableList<Node> children = panelMediosPago.getChildren();

		try {
			if (children != null && !children.isEmpty()) {

				children.forEach(botonera -> {
					if (botonera instanceof BotoneraComponent) {
						Collection<BotonBotoneraComponent> botoneraComponents = ((BotoneraComponent) botonera).getMapConfiguracionesBotones().values();

						botoneraComponents.forEach(boton -> {
							if (StringUtils.isNotBlank(boton.getClave()) && boton.getClave().equals(BOTON_CLAVE_SELECCIONAR_MEDIO_PAGO)) {
								String tipoPago = (String) boton.getParametro(BOTON_PARAMETRO_TIPO_PAGO);
								String codMedioPago = (String) boton.getParametro(BOTON_PARAMETRO_MEDIO_PAGO);
								cambiarVisibilidadBotonPagoOtros(codMedioPago, tipoPago, boton);
							} else {
								if (boton instanceof BotonBotoneraNormalComponent) {
									String ruta = "";
									String rutaActual = boton.getConfiguracionBoton().getRutaImagen();
									switch (rutaActual) {
										case RUTA_IMAGEN_OTROS:
											ruta = RUTA_IMAGEN_OTROS_CERRAR;
											break;
										case RUTA_IMAGEN_OTROS_CERRAR:
											ruta = RUTA_IMAGEN_OTROS;
											break;
									}
									boton.setDisable(false);
									boton.getConfiguracionBoton().setRutaImagen(ruta);
									((BotonBotoneraNormalComponent) boton).cambiarImagen(ruta);
								}
							}
						});
					}
				});

			}
		} catch (Exception e) {
			log.error("Error al seleccionar la apertura de pago otros", e);
		}


	}

	public void cambiarVisibilidadBotonPagoOtros(String codMedioPago, String tipoPago, BotonBotoneraComponent boton) {

		// Comprobamos si el medio de pago viene relleno
		if (StringUtils.isBlank(codMedioPago)) {
			boton.setVisible(false);
			return;
		}

		// Comprobamos que el medio de pago existe y este activo
		MedioPagoBean medioPago = MediosPagosService.mediosPago.get(codMedioPago);
		if (medioPago == null || !medioPago.getActivo()) {
			boton.setVisible(false);
			return;
		}

		// Comprobamos si el medio de pago es de tipo otros para cambiar su visibilidad
		if (StringUtils.isNotBlank(tipoPago) && "OTROS".equals(tipoPago)) {
			boton.setVisible(isDevolucion() || (!esVistaInicialOtroMedioPago && !boton.isVisible()));
		}
	}

	@Override
	public void initializeComponents() {
		log.debug("inicializarComponentes()");

		try {
			initTecladoNumerico(tecladoNumerico);
			log.debug("inicializarComponentes() - Cargando medios de pago");
			frImportePago = SpringContext.getBean(FormularioImportePagosBean.class);
			registrarEventosNavegacionPestanha(panelPagos, getStage());
			List<MedioPagoBean> mediosPago = MediosPagosService.mediosPagoContado;
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList();
			List<ConfiguracionBotonBean> listaAccionesMP = new LinkedList();
			List<ConfiguracionBotonBean> listaAccionesTarjeta = new LinkedList();
			log.debug("inicializarComponentes() - Registrando eventos de acceso rápido por teclado...");
			crearEventoEliminarTabla(tbPagos);
			log.debug("inicializarComponentes() - Configurando botonera");
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));
			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			panelMenuTabla.getChildren().add(botoneraAccionesTabla);
			PanelBotoneraBean botoneraMediosPagos = null;

			String uidActividad = sesion.getAplicacion().getUidActividad();
			String codEmpresa = sesion.getAplicacion().getEmpresa().getCodEmpresa();

			try {
				botoneraMediosPagos = getView().loadBotonera(String.join("_", "", uidActividad, codEmpresa, "mediospago_panel.xml"));
			} catch (InitializeGuiException ex) {
				log.info("inicializarComponentes() - No cargando botonera personalizada de mediospago \"" + uidActividad + "_" + codEmpresa + "_mediospago_panel.xml\": " + ex.getMessage());
			}

			if (botoneraMediosPagos == null) {
				try {
					botoneraMediosPagos = getView().loadBotonera(String.join("_","", uidActividad, "mediospago_panel.xml"));
				} catch (InitializeGuiException ex) {
					log.info("inicializarComponentes() - No cargando botonera personalizada de mediospago \"" + uidActividad + "_mediospago_panel.xml\": " + ex.getMessage());
				}
			}

			//Mantenemos flujo del estándar si no encuentra los paneles customizados
			if (botoneraMediosPagos == null) {
				try {
					botoneraMediosPagos = getView().loadBotonera("_mediospago_panel.xml");
				} catch (InitializeGuiException ex) {
					log.info("inicializarComponentes() - No cargando botonera personalizada de mediospago \"xxx_mediospago_panel.xml\": " + ex.getMessage());
				}
			}
			if (botoneraMediosPagos == null) {
				List<String> codMediosPagosUtilizados = new ArrayList();

				try {
					log.debug("inicializarComponentes() - Cargando panel de importes");
					PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
					for (LineaPanelBotoneraBean lineaPanel : panelBotoneraBean.getLineasBotones()) {
						for (ConfiguracionBotonBean boton : lineaPanel.getLineaBotones()) {
							if (boton.getParametros() != null && boton.getParametros().containsKey("codMedioPago")) {
								codMediosPagosUtilizados.add(boton.getParametro("codMedioPago"));
							}
						}
					}
					botoneraImportes = new BotoneraComponent(panelBotoneraBean, null, panelPagoEfectivo.getPrefHeight(), this, BotonBotoneraImagenValorComponent.class);
					panelPagoEfectivo.getChildren().add(botoneraImportes);
				} catch (InitializeGuiException e) {
					log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
				}

				log.debug("inicializarComponentes() - Creando acciones para botonera de pago contado");

				for (MedioPagoBean mpTarjeta : mediosPago) {
					crearBotonMedioPago(listaAccionesMP, mpTarjeta, codMediosPagosUtilizados);
				}

				botoneraMediosPagoContado = new BotoneraComponent(3, 4, this, listaAccionesMP, null, panelPagoContado.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
				panelPagoContado.getChildren().add(botoneraMediosPagoContado);

				for (MedioPagoBean mpTarjeta : MediosPagosService.mediosPagoTarjetas) {
					crearBotonMedioPago(listaAccionesTarjeta, mpTarjeta, codMediosPagosUtilizados);
				}

				botoneraMediosPagoTarjeta = new BotoneraComponent(3, 4, this, listaAccionesTarjeta, panelPagoTarjeta.getPrefWidth(), panelPagoTarjeta.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
				panelPagoTarjeta.getChildren().add(botoneraMediosPagoTarjeta);
			} else {
				panelMediosPago.getChildren().clear();
				BotoneraComponent botonera = new BotoneraComponent(botoneraMediosPagos, panelMediosPago.getPrefWidth(), panelMediosPago.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
				panelMediosPago.getChildren().add(botonera);
			}

			addCallbackPintadoLineas();
			inicializarFocos();
			addSeleccionarTodoCampos();
			registrarAccionCerrarVentanaEscape();
		} catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error creando botonera para medio de pago. error : " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla pagos."), getStage());
		}
	}

	private void validatePendigPaymentSipay() {
		log.debug("Comprobando operación pendiente en sipay...");
		TefSipayManager tefSipay = buscarSipayManager();
		if (tefSipay != null) {
			try {
				tefSipay.cancelPayPendingTicket(ticketManager.getTicket());
			}
			catch (Exception e) {
				log.error("validatePendigPaySipay() - No se ha podido cancelar la operación pendiente en Sipay " + e.getMessage(), e);
			}
		}

	}
	private TefSipayManager buscarSipayManager() {
		for (PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
			if (paymentMethodManager instanceof TefSipayManager) {
				return (TefSipayManager) paymentMethodManager;
			}
		}
		return null;
	}
}
