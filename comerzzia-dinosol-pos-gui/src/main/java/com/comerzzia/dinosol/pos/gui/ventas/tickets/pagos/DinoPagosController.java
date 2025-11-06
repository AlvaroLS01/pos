package com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comarch.clm.partner.dto.ExtendedBalanceInquiryResponse;
import com.comarch.clm.partner.dto.IssuanceResponse;
import com.comarch.clm.partner.dto.TenderRedemptionGroupDataResponse;
import com.comarch.clm.partner.exception.BpConfiguracionException;
import com.comarch.clm.partner.exception.BpRespuestaException;
import com.comarch.clm.partner.exception.BpSoapException;
import com.comarch.clm.partner.util.CLMServiceErrorString;
import com.comarch.clm.partner.util.CLMServiceFomatString;
import com.comerzzia.api.virtualmoney.client.model.AccountDTO;
import com.comerzzia.core.servicios.api.errorhandlers.ApiClientException;
import com.comerzzia.dinosol.librerias.gtt.client.dto.SaldoEstadoResponseDTO;
import com.comerzzia.dinosol.librerias.sad.client.PedidosApi;
import com.comerzzia.dinosol.librerias.sad.client.model.EstadoPedido;
import com.comerzzia.dinosol.pos.devices.fidelizacion.DinoFidelizacion;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasServiciosException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasTokenException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.cupononline.CuponOnlineController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.cupononline.CuponOnlineView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.encuestas.EncuestasController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.encuestas.EncuestasView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.rascas.IntroduccionRascasController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.rascas.IntroduccionRascasView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.selecciontarjetapago.SeleccionTarjetaPagoController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.selecciontarjetapago.SeleccionTarjetaPagoView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.tarjetas.TarjetasPagoController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.tarjetas.TarjetasPagoView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.vales.ValesPagosController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.pagos.vales.ValesPagosView;
import com.comerzzia.dinosol.pos.persistence.encuestas.Encuesta;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasErroresBean;
import com.comerzzia.dinosol.pos.services.encuestas.EncuestaTicket;
import com.comerzzia.dinosol.pos.services.encuestas.EncuestasService;
import com.comerzzia.dinosol.pos.services.parking.TicketParkingService;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosCuponesService;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.DinoPaymentsManagerImpl;
import com.comerzzia.dinosol.pos.services.payments.methods.types.bp.BPManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.descuentosespeciales.DescuentosEspecialesManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttRedencionCompletaManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.gtt.GttRedencionParcialManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.siam.TefSiamManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.sipay.TefSipayManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.vales.ValesManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.DescuentosEmpleadoManager;
import com.comerzzia.dinosol.pos.services.payments.methods.types.virtualmoney.VirtualMoneyManager;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.PromocionPuntosBPBean;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.TarjetaIdentificacionDto;
import com.comerzzia.dinosol.pos.services.ticket.cupones.DinoCuponEmitidoTicket;
import com.comerzzia.dinosol.pos.services.ticket.lineas.DinoLineaTicket;
import com.comerzzia.dinosol.pos.services.ticket.lineas.TarjetaRegaloDto;
import com.comerzzia.dinosol.pos.services.ventas.reparto.ServiciosRepartoService;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.cajon.CajonNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.importe.BotonBotoneraImagenValorComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.dispositivo.impresora.parser.PrintParserException;
import com.comerzzia.pos.dispositivo.tarjeta.conexflow.TefConexflowManager;
import com.comerzzia.pos.gui.ventas.tickets.pagos.NoCerrarPantallaException;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagoTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentSelectEvent;
import com.comerzzia.pos.services.payments.events.PaymentsErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentsOkEvent;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

@Component
@Primary
@SuppressWarnings("unchecked")
public class DinoPagosController extends PagosController {

	public static final String CODMEDPAG_EMPLEADO = "0300";
	public static final String CODMEDPAG_RESIDENTE = "0301";
	public static final String CODMEDPAG_VIP = "0302";	
	private static final String EXTENDED_DATA_CUPON = "NUM_CUPON";
	public static final String PARAM_NUMERO_TARJETA_BP = "PARAM_NUMERO_TARJETA_BP";
	public static final String PARAM_NUMERO_TARJETA_GTT = "PARAM_NUMERO_TARJETA_GTT";
	public static final String PERMISO_PAGO_EXCEPCIONES = "PAGO EXCEPCIONES";
	public static final String PARAM_PAGOS = "TICKET_PAGOS";
	public static final String PAGO_SELECCIONADO = "PAGO_SELECCIONADO";

	final IVisor visor = Dispositivos.getInstance().getVisor();
	
	@Autowired
	private MediosPagosService mediosPagosService;

	@Autowired
	protected TicketsService ticketService;
	
	private BPManager bpManager;
	
	private VirtualMoneyManager vmManager;
	
	@FXML
	private AnchorPane panelPagoOtros;

	@FXML
	private HBox hbBotonesTarjeta;

	@FXML
	private VBox vbCambioPagos, vbPagoTarjetas, vbConfirmacionPagoTarjeta;

	@FXML
	private HBox HBoxPagos;

	@FXML
	private ImageView ivEnvioDomicilio, ivEcologico;

	@FXML
	protected Label lbFormaPagoCambio, lbEntregadoCambio, lbAPagarCambio, lbDevolverCambio, lbTicketDigital, lbPendienteTarjetasFidelizado;

	@FXML
	protected Button btAceptarCambio, btTicketDigital;
	
	@FXML
	protected TableView<TarjetaFidelizadoGui> tbTarjetasFidelizado;
	
	@FXML
	protected TableColumn<TarjetaFidelizadoGui, String> tcNumeroTarjeta, tcTipoTarjeta, tcMensajeTarjeta;
	
	@FXML
	protected TableColumn<TarjetaFidelizadoGui, BigDecimal> tcSaldoDisponible, tcImportePago;
	
	@FXML
	protected TableColumn<TarjetaFidelizadoGui, VBox> tcBotonPagar;
	
	@FXML
	private ImageView ivTarjetaBp;
	
	@FXML
	private ImageView ivServicioReparto;

	private String numTarjetaLeido;

	private PedidosApi pedidosService;

	@Autowired
	protected VariablesServices variablesServices;

	@Autowired
	protected PrefijosTarjetasService prefijosTarjetasService;

	@Autowired
	protected PrefijosCuponesService prefijosCuponesService;

	@Autowired
	private TicketParkingService ticketParkingService;
	
	@Autowired
	private EncuestasService encuestasService;
	
	@Autowired
	private ServiciosRepartoService serviciosRepartoService;
	
	protected BigDecimal importeMaximoVirtualMoney;

	private Boolean integracionRutas;

	private Button btPagoExcepciones;

	protected Timeline timeline;
	
	private List<String> tarjetasFidelizadoDisponibles;
	private List<String> tarjetasPagoAutomatico;

	private List<TarjetaFidelizadoGui> tarjetasGui;
	
	private TarjetaFidelizadoGui ultimoPagoTarjetaFidelizado;
	
	public boolean activarPagoAutomaticoEnPantallaIntermediaTarjetas;
	
	private ArrayList<Button> listaBotones;
		
	/*
	 * Metemos este pequeño parche para no tocar la lógica de los pagos automáticos de BP
	 */
	private boolean encuestasHechas;

	@Override
	public void initializeComponents() {
		super.initializeComponents();
		addSeleccionarTodoEnFoco(tfImporte);

		try {
			PanelBotoneraBean panelBotoneraBean = getView().loadBotonera("_otros.xml");
			BotoneraComponent botoneraMediosPago = new BotoneraComponent(panelBotoneraBean, null, panelPagoOtros.getPrefHeight(), this, BotonBotoneraImagenValorComponent.class);
			panelPagoOtros.getChildren().add(botoneraMediosPago);

			listaBotones = botoneraMediosPago.getListaBotones();
			for(Button boton : listaBotones) {
				ConfiguracionBotonBean configuracion = (ConfiguracionBotonBean) boton.getUserData();
				if(configuracion.getTexto().equals("VALE")) {
					btPagoExcepciones = boton;
				}
			}
		}
		catch (Exception e) {
			log.error("initializeComponents() - Ha habido un error al cargar la botonera de otros medios de pago: " + e.getMessage(), e);
		}

		prepararTablaTarjetasFidelizado();
	}

	@Override
	protected void initializePaymentsManager() {
		super.initializePaymentsManager();

		buscarBpManager();
		buscarVmManager();
	}

	private void buscarBpManager() {
		for (PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
			if (paymentMethodManager instanceof BPManager) {
				bpManager = (BPManager) paymentMethodManager;
				break;
			}
		}
	}

	private void buscarVmManager() {
		for (PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
			if (paymentMethodManager instanceof VirtualMoneyManager) {
				vmManager = (VirtualMoneyManager) paymentMethodManager;
				break;
			}
		}
	}

	private void buscarVmManager(String codMedPago) {
		for (PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
			if (paymentMethodManager instanceof VirtualMoneyManager && paymentMethodManager.getPaymentCode().equals(codMedPago)) {
				vmManager = (VirtualMoneyManager) paymentMethodManager;
				break;
			}
		}
	}

	@Override
	protected void selectCustomPaymentMethod(PaymentSelectEvent paymentSelectEvent) {
		try {
			if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof BPManager) {
				askBPCardNumber((PaymentMethodManager) paymentSelectEvent.getSource());
			}
			else if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof DescuentosEspecialesManager) {
				askDescuentoEspecialCardNumber((PaymentMethodManager) paymentSelectEvent.getSource());
			}
			else if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof GttManager) {
				askGTTCardNumber((PaymentMethodManager) paymentSelectEvent.getSource());
			}
			else if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof ValesManager) {
				askValesNumber((PaymentMethodManager) paymentSelectEvent.getSource());
			}
			else if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof VirtualMoneyManager) {
				askVirtualMoneyCard((VirtualMoneyManager) paymentSelectEvent.getSource());
			}
			else if (((PaymentMethodManager) paymentSelectEvent.getSource()) instanceof TefSiamManager && ticketManager.getTicket().isEsDevolucion()) {
				abrirSeleccionTarjetaPago((PaymentMethodManager) paymentSelectEvent.getSource());
			}
			else {
				BigDecimal anotado = FormatUtil.getInstance().desformateaImporte(tfImporte.getText());
				anotarPago(anotado);
			}
		}
		finally {
			numTarjetaLeido = null;
		}
	}

	protected void askBPCardNumber(PaymentMethodManager source) {
		try {
			String numTarjeta = numTarjetaLeido;

			if (StringUtils.isNotBlank(numTarjeta)) {
				new ConsultarSaldoBp((BPManager) source, numTarjeta).start();
			}
			else {
				selectDefaultPaymentMethod();
			}
		}
		catch (RuntimeException e) {
			log.error("askBPCardNumber() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
			selectDefaultPaymentMethod();
		}
		catch (Exception e) {
			log.error("askBPCardNumber() - Ha habido un error al pedir el número de tarjeta: " + e.getMessage(), e);
			selectDefaultPaymentMethod();
		}
	}

	private void pintaSaldoBp(PaymentMethodManager source, String numTarjeta, ExtendedBalanceInquiryResponse saldoResponse) {
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
		BigDecimal saldo = BigDecimal.ZERO;
		BigDecimal saldoDisponible = null;
		if (saldoResponse != null) {
			if (saldoResponse.getTenderRedemptionGroupDataList() != null) {
				for (TenderRedemptionGroupDataResponse virtual : saldoResponse.getTenderRedemptionGroupDataList()) {
					/* Filtramos para solo coger uno de los dos saldos que nos trae. */
					if (BPManager.VIRTUAL_MONEY_CODE.equals(virtual.getTenderRedemptionGroupCode())) {
						saldo = new BigDecimal(virtual.getVirtualMoneyBalance());
						String saldoTexto = I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(saldo);
						saldoTexto = saldoTexto + " (Epx : " + sdfFecha.format(virtual.getVirtualMoneyExpirationDate()) + ")";
						lbSaldo.setText(saldoTexto);
					}

					if (!BigDecimalUtil.isIgualACero(saldo)) {
						/* En caso de ser una venta, se inserta también el importe. */
						if (ticketManager.getTicket().getCabecera().esVenta()) {
							BigDecimal total = ticketManager.getTicket().getTotales().getTotalAPagar();

							BigDecimal tramo = ((BPManager) source).getTramo();
							BigDecimal eurosTramo = ((BPManager) source).getEurosTramo();
							boolean condicionQuemadoPositiva = ((BPManager) source).isCondicionQuemadoPositiva();

							saldoDisponible = BigDecimal.ZERO;
							if (tramo == null || eurosTramo == null || BigDecimalUtil.isMenorACero(tramo) || BigDecimalUtil.isMenorACero(eurosTramo)) {
								throw new RuntimeException(I18N.getTexto("No está configurado el pago con la tarjeta BP."));
							}
							else {
								if (condicionQuemadoPositiva && BigDecimalUtil.isMayorACero(total)) {
									saldoDisponible = total.divide(tramo, 0, RoundingMode.FLOOR);
								}
								else {
									saldoDisponible = total.divide(tramo, 0, RoundingMode.CEILING);
								}
								saldoDisponible = BigDecimalUtil.redondear(saldoDisponible.multiply(eurosTramo));
							}

							if (BigDecimalUtil.isMayor(saldoDisponible, saldo)) {
								saldoDisponible = saldo;
							}

							tfImporte.setText(FormatUtil.getInstance().formateaImporte(saldoDisponible));
						}
					}
					else {
						BigDecimal saldoCero = BigDecimal.ZERO;
						String saldoTexto = I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(saldoCero);
						saldoTexto = saldoTexto + " (Epx : " + sdfFecha.format(new Date()) + ")";
						lbSaldo.setText(saldoTexto);
						tfImporte.setText(FormatUtil.getInstance().formateaImporte(saldoCero));
					}
				}
			}
			else {
				BigDecimal saldoCero = BigDecimal.ZERO;
				String saldoTexto = I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(saldoCero);
				saldoTexto = saldoTexto + " (Epx : " + sdfFecha.format(new Date()) + ")";
				lbSaldo.setText(saldoTexto);
				tfImporte.setText(FormatUtil.getInstance().formateaImporte(saldoCero));
			}
		}
	}

	private class ConsultarSaldoBp extends BackgroundTask<ExtendedBalanceInquiryResponse> {

		private BPManager bpManager;

		private String numTarjeta;

		public ConsultarSaldoBp(BPManager bpManager, String numTarjeta) {
			super();
			this.bpManager = bpManager;
			this.numTarjeta = numTarjeta;
		}

		@Override
		protected ExtendedBalanceInquiryResponse call() throws BpSoapException, BpRespuestaException, BpConfiguracionException, PaymentException {
			// Asignar la tarjeta leida al ticket para que acumule aunque de el error
			if (!((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).containsTarjeta(numTarjeta)) {
				TarjetaIdentificacionDto tarjetaIdentificacionDto = new TarjetaIdentificacionDto();
				tarjetaIdentificacionDto.setNumeroTarjeta(numTarjeta);
				tarjetaIdentificacionDto.setTipoTarjeta("BP");
				((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).addTarjetaIdentificacion(tarjetaIdentificacionDto);
			}
			
			ExtendedBalanceInquiryResponse saldo = bpManager.getSaldo(numTarjeta);

			// asignamos los valores de respuesta y número de tarjeta al manager
			bpManager.addParameter(BPManager.PARAM_TARJETA_BP, saldo);
			bpManager.addParameter(BPManager.PARAM_NUMERO_TARJETA_BP, numTarjeta);

			// Recalculamos promociones para la promoción de DinoBP
			((DinoTicketManager) ticketManager).aplicarPromocionesBp();

//			// lanza excepción si el saldo es 0
//			checkSaldoBPActivo(saldo);

			return saldo;
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			pintaSaldoBp(bpManager, numTarjeta, this.getValue());
		}

		@Override
		protected void failed() {
			super.failed();
		    
		    Throwable e = getException();
		    if(e instanceof BpSoapException) {
		    	log.error("guardarTarjetaIdentificacionBp() - Ha habido un error al conectar con el servicio de BP para validar la tarjeta.");
		    	log.error("guardarTarjetaIdentificacionBp() - Se trabajará de forma offline.");
		    	log.error("guardarTarjetaIdentificacionBp() - Error: " + e.getMessage(), e);
		    	
				VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
		    	
		    	ticketManager.recalcularConPromociones();
		    }
		    else if(e instanceof BpRespuestaException) {
		    	log.error("guardarTarjetaIdentificacionBp() - Ha habido un error al realizar la validación de la tarjeta BP.");
		    	log.error("guardarTarjetaIdentificacionBp() - No se admite la tarjeta.");
		    	log.error("guardarTarjetaIdentificacionBp() - Error: " + e.getMessage(), e);
		    	
		    	tratarErrorLecturaTarjetaBp(e);
		    }
		    else if(e instanceof BpConfiguracionException) {
		    	log.error("guardarTarjetaIdentificacionBp() - Error de configuración: " + e.getMessage(), e);
		    	log.error("guardarTarjetaIdentificacionBp() - Se trabajará de forma offline.");
		    	
				VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
		    	
		    	ticketManager.recalcularConPromociones();
		    }
		    else {
		    	log.error("guardarTarjetaIdentificacionBp() - Ha habido un error indeterminado al realizar la validación de la tarjeta BP.");
		    	log.error("guardarTarjetaIdentificacionBp() - No se admite la tarjeta.");
		    	log.error("guardarTarjetaIdentificacionBp() - Error: " + e.getMessage(), e);
		    	
		    	tratarErrorLecturaTarjetaBp(e);
		    }

			selectDefaultPaymentMethod();
		    refrescarDatosPantalla();
		}

		protected void tratarErrorLecturaTarjetaBp(Throwable e) {
            DinoCabeceraTicket cabecera = (DinoCabeceraTicket) (ticketManager.getTicket()).getCabecera();
            TarjetaIdentificacionDto tarjetaTipo = new TarjetaIdentificacionDto();
            tarjetaTipo.setTipoTarjeta("BP");
            cabecera.removeTarjetaIdentificacion(tarjetaTipo);
            
            String mensaje = I18N.getTexto("La tarjeta introducida no es correcta.") + System.lineSeparator() + System.lineSeparator() + e.getMessage();
            VentanaDialogoComponent.crearVentanaError(mensaje, getStage());
        }

	}

	private TarjetaIdentificacionDto buscarTarjetaIdentificacion(String tipo) {
		return ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).buscarTarjeta(tipo);
	}

	@SuppressWarnings("rawtypes")
	protected void askDescuentoEspecialCardNumber(PaymentMethodManager source) {
		try {
			if (StringUtils.isNotBlank(numTarjetaLeido)) {
				/* Rellenamos la cabecera del ticket, porque necesitamos el Id y el código */
				if (ticketManager.getTicket().getCabecera().getIdTicket() == null) {
					log.debug("askDescuentoEspecialCardNumber() - Se va a asignar el ID_TICKET ya que el ticket no tiene pagos.");
					ticketService.setContadorIdTicket((Ticket) ticketManager.getTicket());
					log.debug("askDescuentoEspecialCardNumber() - Contador obtenido: " + ticketManager.getTicket().getIdTicket());
				}
				/* Realizamos la petición del descuento, y devolvemos los datos de este en un mapa */
				new ConsultarDescuentoEspecial((DescuentosEspecialesManager) source, numTarjetaLeido).start();
			}
			else {
				/* En caso de fallar se pone el medio de pago por defecto */
				selectDefaultPaymentMethod();
			}
		}
		catch (Exception e) {
			log.error("askDescuentoEspecialCardNumber() - Ha habido un error al pedir el número de tarjeta: " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al pedir el número de tarjeta: " + e.getMessage()), e);
			selectDefaultPaymentMethod();
		}
	}

	private void procesarDescuentoDirecto(PaymentMethodManager source, String numTarjeta, Map<String, Object> mapaResponse) {
		BigDecimal descuento = (BigDecimal) mapaResponse.get(DescuentosEspecialesManager.PARAM_DESCUENTO_DESCUENTOS);

		if (BigDecimalUtil.isIgualACero(descuento)) {
			selectDefaultPaymentMethod();
			return;
		}

		if (BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(), descuento)) {
			tfImporte.setText(FormatUtil.getInstance().formateaImporte(descuento));
		}
		source.addParameter(DescuentosEspecialesManager.PARAM_NUMERO_TARJETA_DESCUENTOS, numTarjeta);
		source.addParameter(DescuentosEspecialesManager.PARAM_CODIGO_VENTA_DESCUENTOS, mapaResponse.get(DescuentosEspecialesManager.PARAM_CODIGO_VENTA_DESCUENTOS));
		source.addParameter(DescuentosEspecialesManager.PARAM_PUNTO_VENTA_DESCUENTOS, mapaResponse.get(DescuentosEspecialesManager.PARAM_PUNTO_VENTA_DESCUENTOS));
		source.addParameter(DescuentosEspecialesManager.PARAM_NUMERO_OPERACION_DESCUENTOS, mapaResponse.get(DescuentosEspecialesManager.PARAM_NUMERO_OPERACION_DESCUENTOS));
		source.addParameter(DescuentosEspecialesManager.PARAM_FECHA_DESCUENTOS, mapaResponse.get(DescuentosEspecialesManager.PARAM_FECHA_DESCUENTOS));

		/* Anotamos directamente el pago */
		anotarPago(descuento);
	}

	private class ConsultarDescuentoEspecial extends BackgroundTask<Map<String, Object>> {

		private DescuentosEspecialesManager descuentosEspecialesManager;
		private String numTarjeta;

		public ConsultarDescuentoEspecial(DescuentosEspecialesManager descuentosEspecialesManager, String numTarjeta) {
			super();
			this.descuentosEspecialesManager = descuentosEspecialesManager;
			this.numTarjeta = numTarjeta;
		}

		@Override
		protected Map<String, Object> call() throws Exception {
			return descuentosEspecialesManager.getDescuento(numTarjeta);
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			Map<String, Object> response = getValue();
			if (response != null) {
				procesarDescuentoDirecto(descuentosEspecialesManager, numTarjeta, response);
			}
			else {
				selectDefaultPaymentMethod();
			}
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			VentanaDialogoComponent.crearVentanaError(getStage(), exception.getMessage(), exception);
			selectDefaultPaymentMethod();
		}

	}

	protected void askGTTCardNumber(PaymentMethodManager source) {
		try {
			if (StringUtils.isNotBlank(numTarjetaLeido)) {
				new ConsultarSaldoTarjetaGtt((GttManager) source, numTarjetaLeido).start();
			}
			else {
				selectDefaultPaymentMethod();
			}
		}
		catch (Exception e) {
			log.error("askGTTCardNumber() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(getStage(), e.getMessage(), e);
			selectDefaultPaymentMethod();
		}
	}

	private class ConsultarSaldoTarjetaGtt extends BackgroundTask<SaldoEstadoResponseDTO> {

		private GttManager gttManager;
		private String codigo;

		public ConsultarSaldoTarjetaGtt(GttManager gttManager, String codigo) {
			super();
			this.gttManager = gttManager;
			this.codigo = codigo;
		}

		@Override
		protected SaldoEstadoResponseDTO call() throws Exception {
			SaldoEstadoResponseDTO saldoEstado = gttManager.getSaldoAndEstado(codigo);

			// validaciones previas
			if (BigDecimalUtil.isIgualACero(saldoEstado.getSaldo())) {
				throw new PaymentException("El saldo es igual a 0");
			}

			if (gttManager instanceof GttRedencionCompletaManager && BigDecimalUtil.isMenor(ticketManager.getTicket().getTotales().getPendiente(), saldoEstado.getSaldo())) {
				throw new PaymentException("El importe del talón es superior al importe a pagar");
			}
			
			// Como es una forma de pago provisional, quitamos la comprobación de las restricciones para 
			// este medio de pago ya que se compruebas las restriccciones de otra forma 
			if(!medioPagoSeleccionado.getCodMedioPago().equals("0405")) {
				gttManager.checkRestricciones(saldoEstado.getRestriccion());
			}

			gttManager.addParameter(GttManager.PARAM_NUMERO_TARJETA_GTT, codigo);

			return saldoEstado;
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			procesarSaldoGtt(gttManager, codigo, getValue());
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			VentanaDialogoComponent.crearVentanaError(getStage(), exception.getMessage(), exception);
			selectDefaultPaymentMethod();
		}

	}

	private void procesarSaldoGtt(GttManager source, String codigo, SaldoEstadoResponseDTO saldo) {
		importeMaximoVirtualMoney = saldo.getSaldo();

		/*
		 * Para los medios de pago "Tarjeta Regalo" y "Tarjeta Compra" se puede mostrar el saldo y pagar partes del
		 * importe pendiente
		 */
		if (source instanceof GttRedencionParcialManager) {
			lbSaldo.setText(I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(saldo.getSaldo()));
			if (BigDecimalUtil.isMayorACero(saldo.getSaldo())) {
				/* En caso de ser una venta, se inserta también el importe. */
				if (ticketManager.getTicket().getCabecera().esVenta()) {
					if (BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(), saldo.getSaldo())) {
						importeMaximoVirtualMoney = saldo.getSaldo();
						tfImporte.setText(FormatUtil.getInstance().formateaImporte(importeMaximoVirtualMoney));
					}
				}
			}
		}
		/*
		 * Para los medios de pago "Talón Hispamer" y "Talón Verde" no se deberá mostrar el saldo y se deberá insertar
		 * el pago directamente sin opción para pagar partes
		 */
		else if (source instanceof GttRedencionCompletaManager) {
			anotarPago(saldo.getSaldo());
		}
	}

	@Override
	protected void enablePaymentsMethods() {
		super.enablePaymentsMethods();

		for (BotonBotoneraComponent boton : ((BotoneraComponent) panelPagoEfectivo.getChildren().get(0)).getMapConfiguracionesBotones().values()) {
			if (BigDecimalUtil.isMenorACero(ticketManager.getTicket().getTotales().getTotalAPagar())) {
				boton.setDisable(true);
			}
			else {
				boton.setDisable(false);
			}
		}

		BotoneraComponent botonera = (BotoneraComponent) panelPagoOtros.getChildrenUnmodifiable().get(0);
		for (BotonBotoneraComponent boton : botonera.getMapConfiguracionesBotones().values()) {
			boolean disable = false;
			if (isDevolucion()) {
				if (boton.getConfiguracionBoton() != null && boton.getConfiguracionBoton().getParametros() != null && boton.getParametro("codMedioPago") != null) {
					String codMedioPago = (String) boton.getParametro("codMedioPago");
					if (StringUtils.isNotBlank(codMedioPago) && !paymentsManager.isPaymentMethodAvailableForReturn(codMedioPago)) {
						disable = true;
					}
				}
				else {
					if (boton.getConfiguracionBoton().getTexto().equals("TARJETAS")) {
						disable = true;
						for (String codMedioPagoTarjeta : prefijosTarjetasService.getCodMediosPagoTarjetas()) {
							if (paymentsManager.isPaymentMethodAvailableForReturn(codMedioPagoTarjeta)) {
								disable = false;
								break;
							}
						}
					}
					else if (boton.getClave().equals("VALE")) {
						disable = true;
					}
				}
			}
			// DIN-289 - POS de reparto
			else if (((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto() != null) {
				if (boton.getConfiguracionBoton() != null && boton.getConfiguracionBoton().getParametros() != null && boton.getParametro("codMedioPago") != null) {
					String codMedioPago = (String) boton.getParametro("codMedioPago");
					if (((DinoPaymentsManagerImpl) paymentsManager).isPaymentMethodAvailablePosReparto(codMedioPago)) {
						disable = false;
					}
					else {
						disable = true;
					}
				}
				panelPagoEfectivo.setDisable(true);
			}
			else {
				disable = false;
			}
			

			if (disable) {
				boton.setDisable(true);
			}
			else {
				boton.setDisable(false);
			}
			
			
		}

		refrescarDatosPantalla();
	}

	@Override
	protected void addCustomPaymentData(PaymentOkEvent eventOk, PagoTicket payment) {
		if (eventOk.getSource() instanceof BPManager) {
			TarjetaIdentificacionDto tarjetaIdentificacion = buscarTarjetaIdentificacion("BP");
			if (tarjetaIdentificacion == null) {
				tarjetaIdentificacion = new TarjetaIdentificacionDto();
				tarjetaIdentificacion.setTipoTarjeta("BP");
				tarjetaIdentificacion.setNumeroTarjeta((String) eventOk.getExtendedData().get("numeroTarjeta"));
				((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).addTarjetaIdentificacion(tarjetaIdentificacion);
			}
			tarjetaIdentificacion.setLineasImpresion((List<String>) eventOk.getExtendedData().get("formatedText"));
			tarjetaIdentificacion.setRespuesta(eventOk.getExtendedData());

			// Recalculamos promociones para la promoción de DinoBP
			((DinoTicketManager) ticketManager).aplicarPromocionesBp();
		}

		if (!(eventOk.getSource() instanceof TefConexflowManager)) {
			payment.setExtendedData(eventOk.getExtendedData());
		}
	}

	protected void askValesNumber(PaymentMethodManager source) {
		try {
			/* Pasamos el parámetro del total a pagar */
			HashMap<String, Object> parametros = new HashMap<>();
			parametros.put(ValesPagosController.PARAMETRO_IMPORTE_PORPAGAR, ticketManager.getTicket().getTotales().getTotalAPagar());
			/* Pasamos el parámetro del importe seleccionado en caso de haberlo */
			if (!tfImporte.getText().isEmpty()) {
				parametros.put(ValesPagosController.PARAMETRO_IMPORTE_SELECCIONADO, tfImporte.getText());
			}
			else {
				parametros.put(ValesPagosController.PARAMETRO_IMPORTE_SELECCIONADO, BigDecimal.ZERO);
			}
			getApplication().getMainView().showModalCentered(ValesPagosView.class, parametros, getStage());

			/* Rescatamos de la pantalla el importe seleccionado */
			BigDecimal importe = (BigDecimal) parametros.get(ValesPagosController.PARAMETRO_IMPORTE);
			String codMedioPago = (String) parametros.get(ValesPagosController.PARAMETRO_COD_MEDIOPAGO);
			String codImpreso = (String) parametros.get(ValesPagosController.PARAMETRO_CODIGO);
			if (importe != null) {
				/* Si todo sale correcto, anotamos el pago directamente */
				importeMaximoVirtualMoney = null;

				medioPagoSeleccionado = mediosPagosService.consultarMedioPago(codMedioPago);

				// pasar al medio de pago de destino el parámetro de número de vale
				PaymentMethodManager destinationManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(medioPagoSeleccionado.getCodMedioPago());
				destinationManager.addParameter(ValesManager.PARAM_NUMERO_VALE, codImpreso);

				anotarPago(importe);
			}
			else {
				selectDefaultPaymentMethod();
			}

		}
		catch (Exception e) {
			log.error("askValesNumber() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(e.getMessage(), getStage());
			selectDefaultPaymentMethod();
		}
	}
	
	@Override
	public void aceptar() {
		if(!esPagoEfectivoImportePermitido()) {
			return;
		}
		
		mostrarEncuestas();
		
		generarQrParking();

		TarjetaIdentificacionDto tarjetaIdentificacion = buscarTarjetaIdentificacion("BP");

		if (tarjetaIdentificacion != null && (tarjetaIdentificacion.getAdicionales() == null || !tarjetaIdentificacion.getAdicionales().containsKey("puntosAcumulados"))) {
			new AcumularPuntosBp(tarjetaIdentificacion.getNumeroTarjeta()).start();
		}
		else {
			super.aceptar();

		}
	}

	private boolean esPagoEfectivoImportePermitido() {
		if(superaImporteMaximoEfectivo()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. La cantidad que se quiere pagar en efectivo supera el máximo permitido ({0})", importeMaxEfectivo), getStage());
			return false;
		}
		else {
			return true;
		}
	}
	
	protected Boolean superaImporteMaximoEfectivo(){
		importeMaxEfectivo = null;
		/* Consultamos las propiedades del tipo de documento para coger la propiedad de límite 
		 * de pago de efectivo, dependiendo si es país extranjero ó no. */
		String codPaisCliente = null;
		if(ticketManager.getTicket().getCliente().getDatosFactura() == null){
			codPaisCliente = ticketManager.getTicket().getCliente().getCodpais();
		}else{
			codPaisCliente = ticketManager.getTicket().getCliente().getDatosFactura().getPais();
		}
		if(sesion.getAplicacion().getTienda().getCliente().getCodpais().equals(codPaisCliente)) {
			importeMaxEfectivo = ticketManager.getDocumentoActivo().getImporteMaximoEfectivo();
		}else{
			importeMaxEfectivo = ticketManager.getDocumentoActivo().getImporteMaximoEfectivoEx();
		}
		Boolean superaImporte = Boolean.FALSE;
		if(importeMaxEfectivo != null){
			BigDecimal importeEfectivo = BigDecimal.ZERO;
			for(PagoTicket pago : (List<PagoTicket>)ticketManager.getTicket().getPagos()){
				if(pago.getMedioPago().getEfectivo() && pago.getMedioPago().getManual()){
					importeEfectivo = importeEfectivo.add(pago.getImporte().abs());
					if(BigDecimalUtil.isMayor(importeEfectivo, importeMaxEfectivo)){
						superaImporte = Boolean.TRUE;
						break;
					}
				}
			}
		}
		
		return superaImporte;
	}

	protected void generarQrParking() {
		if (((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).isGenerarQrParking()) {
			ticketParkingService.generarQrParking((TicketVentaAbono) ticketManager.getTicket());
		}
	}

	private class AcumularPuntosBp extends BackgroundTask<IssuanceResponse> {

		private String numTarjeta;

		public AcumularPuntosBp(String numTarjeta) {
			super();
			this.numTarjeta = numTarjeta;
		}

		@Override
		protected IssuanceResponse call() throws Exception {
			TarjetaIdentificacionDto tarjetaBp = buscarTarjetaIdentificacion("BP");

			BigDecimal puntosConcedidos = BigDecimal.ZERO;
			if (tarjetaBp.getAdicionales() != null && tarjetaBp.getAdicionales().containsKey(PromocionPuntosBPBean.PARAM_PUNTOS_CONCEDIDOS)) {
				puntosConcedidos = (BigDecimal) tarjetaBp.getAdicionales().get(PromocionPuntosBPBean.PARAM_PUNTOS_CONCEDIDOS);
			}

			IssuanceResponse respuesta = bpManager.realizarMovimiento(null, numTarjeta, puntosConcedidos);

			if (!"0".equals(respuesta.getErrorCode())) {
				throw new BpRespuestaException("Respuesta de BP al sumar/quemar saldo: " + respuesta.getErrorCode() + "/" + CLMServiceErrorString.getErrorString(respuesta.getErrorCode()));
			}

			ExtendedBalanceInquiryResponse saldo = bpManager.getSaldo(tarjetaBp.getNumeroTarjeta());

			for (TenderRedemptionGroupDataResponse virtual : saldo.getTenderRedemptionGroupDataList()) {
				if (BPManager.VIRTUAL_MONEY_CODE.equals(virtual.getTenderRedemptionGroupCode())) {
					tarjetaBp.putAdicional("puntosHiperDino", virtual.getVirtualMoneyBalance());
				}
				else {
					tarjetaBp.putAdicional("puntosBp", virtual.getVirtualMoneyBalance());
				}
			}

			return respuesta;
		}

		@Override
		protected void succeeded() {
			super.succeeded();

			IssuanceResponse respuesta = getValue();
			TarjetaIdentificacionDto tarjetaIdentificacion = buscarTarjetaIdentificacion("BP");

			List<String> formatedString = CLMServiceFomatString.getFormatedString(respuesta.getText());
			tarjetaIdentificacion.setLineasImpresion(formatedString);
			String textoPromocion = "";
			for (String texto : formatedString) {
				if (texto.length() < 40) {
					texto = StringUtils.rightPad(texto, 40);
				}
				textoPromocion = textoPromocion + texto;
			}

			tarjetaIdentificacion.setRespuesta(respuesta);
			tarjetaIdentificacion.putAdicional("puntosAcumulados", true);

			for (PromocionTicket promo : (List<PromocionTicket>) ticketManager.getTicket().getPromociones()) {
				if (promo.getIdTipoPromocion().equals(1003L)) {
					promo.setTextoPromocion(textoPromocion);
				}
			}

			aceptar();
		}

		@Override
		protected void failed() {
			super.failed();

			log.error("AcumularPuntosBp.failed() - " + getException().getMessage(), getException());

			TarjetaIdentificacionDto tarjetaIdentificacion = buscarTarjetaIdentificacion("BP");
			tarjetaIdentificacion.setProcesamientoOffline(true);
			tarjetaIdentificacion.putAdicional("puntosAcumulados", false);
			tarjetaIdentificacion.putAdicional("urlBp", bpManager.getUrl());

			aceptar();
		}

	}

	/**
	 * Acción borrar registro seleccionado de la tabla
	 */
	@FXML
	protected void accionBorrarRegistroTabla() {
		log.debug("accionBorrarRegistroTabla() - Acción ejecutada");
		if (tbPagos.getItems() != null && tbPagos.getItems().size() == 0) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("Borrar pago."), I18N.getTexto("Sin pagos para eliminar."), getStage());
			return;
		}

		PagoTicketGui gui = tbPagos.getSelectionModel().getSelectedItem();

		if (gui == null) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ningún pago seleccionado."), getStage());
			return;
		}
		if (!gui.isRemovable()) {
			Iterator<PagoTicket> itPagosTicket = ((List<PagoTicket>) ticketManager.getTicket().getPagos()).iterator();
			while (itPagosTicket.hasNext()) {
				PagoTicket pago = itPagosTicket.next();
				if (BigDecimalUtil.isIgual(pago.getImporte(), gui.getImporte())) {
					String numCupon = (String) pago.getExtendedData(EXTENDED_DATA_CUPON);
					if (StringUtils.isNotBlank(numCupon)) {
						Iterator<CuponAplicadoTicket> it = (Iterator<CuponAplicadoTicket>) ticketManager.getTicket().getCuponesAplicados().iterator();
						while (it.hasNext()) {
							CuponAplicadoTicket cupon = it.next();
							if (cupon.getCodigo().equals(numCupon)) {
								it.remove();
								itPagosTicket.remove();
								ticketManager.getTicket().getCabecera().getTotales().recalcular();
								refrescarDatosPantalla();
								tbPagos.getSelectionModel().selectFirst();
								return;
							}
						}
					}
				}
			}

			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El pago seleccionado no se puede borrar."), getStage());
			return;
		}

		Boolean comprobarPago = true;
		String mensajeAlertaGtt = I18N.getTexto("No se podrá realizar la devolución en la tarjeta del cliente. ¿Desea eliminar el pago de todas formas?") + System.lineSeparator()
		        + System.lineSeparator() + I18N.getTexto("En caso afirmativo, consulte con los servicios centrales para que el dinero sea devuelto.");
		/* Comprobamos que el tipo de pago es de redención total */
		for (Map.Entry<String, PaymentMethodManager> entry : paymentsManager.getPaymentsMehtodManagerAvailables().entrySet()) {
			if (entry.getValue() instanceof GttManager) {
				if (gui.getCodMedioPago().equals(entry.getValue().getPaymentCode())) {
					if (VentanaDialogoComponent.crearVentanaConfirmacion("", mensajeAlertaGtt, getStage())) {
						log.debug("accionBorrarRegistroTabla() - El usuario quiere borrar el pago con ID: " + gui.getPaymentId());
						String codMedioPago = paymentsManager.getCurrentPayments().get(gui.getPaymentId()).getPaymentCode();
						PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(codMedioPago);
						if (isDevolucion()) {
							cancelReturn(paymentMethodManager, gui.getPaymentId());
						}
						else {
							cancelPay(paymentMethodManager, gui.getPaymentId());
						}
						/* Controlamos el borrado de pagos normales con esta variable */
						comprobarPago = false;
					}
					else {
						return;
					}
				}
			}
		}

		if (comprobarPago) {
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Confirme operación."), I18N.getTexto("¿Desea eliminar el pago?"), getStage())) {
				log.debug("accionBorrarRegistroTabla() - El usuario quiere borrar el pago con ID: " + gui.getPaymentId());
				String codMedioPago = paymentsManager.getCurrentPayments().get(gui.getPaymentId()).getPaymentCode();
				PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(codMedioPago);
				if (isDevolucion()) {
					cancelReturn(paymentMethodManager, gui.getPaymentId());
				}
				else {
					cancelPay(paymentMethodManager, gui.getPaymentId());
					tecladoNumerico.requestFocus();
					tfImporte.requestFocus();
				}
				ticketManager.guardarCopiaSeguridadTicket();
			}
		}
		restaurarFocoTFImporte();
	}

	public void introducirTarjeta() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		getApplication().getMainView().showModalCentered(TarjetasPagoView.class, params, getStage());

		String codMedioPago = (String) params.get(TarjetasPagoController.PARAM_COD_MEDIO_PAGO);
		if (StringUtils.isNotBlank(codMedioPago)) {
			numTarjetaLeido = (String) params.get(TarjetasPagoController.PARAM_NUM_TARJETA);
			
			boolean tarjetaValida = comprobarTarjetaServiciosReparto(numTarjetaLeido);
			if(!tarjetaValida) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Tarjeta de compra no válida para este delivery"), getStage());
				return;
			}
			
			HashMap<String, String> parametros = new HashMap<String, String>();
			parametros.put("codMedioPago", codMedioPago);
			seleccionarMedioPago(parametros);

			activarModoPagoTarjeta(true);
		}
	}

	private boolean comprobarTarjetaServiciosReparto(String numTarjeta) {
		ServicioRepartoDto servicio = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto();
		
		if(servicio == null) {
			return true;
		}
		
		return StringUtils.isNotBlank(numTarjeta) && numTarjeta.equals(servicio.getNumeroTarjeta());
	}

	private void activarModoPagoTarjeta(boolean activar) {
		hbBotonesTarjeta.setVisible(activar);

		for (Button button : ((BotoneraComponent) panelPagoEfectivo.getChildren().get(0)).getListaBotones()) {
			button.setDisable(activar);
		}

		for (Button button : ((BotoneraComponent) panelPagoOtros.getChildren().get(0)).getListaBotones()) {
			button.setDisable(activar);
		}

		for (Button button : ((BotoneraComponent) panelMenuTabla.getChildren().get(0)).getListaBotones()) {
			button.setDisable(activar);
		}

		btCancelar.setDisable(activar);

		if (!activar) {
			try {
				super.compruebaPermisos(PERMISO_PAGO_EXCEPCIONES);
				btPagoExcepciones.setDisable(false);
			}
			catch (SinPermisosException e) {
				btPagoExcepciones.setDisable(true);
			}
		}
	}

	public void aceptarPagoTarjeta() {
		actionBtAnotarPago();
	}

	public void cancelarPagoTarjeta() {
		selectDefaultPaymentMethod();
	}

	@Override
	protected void addPayment(PaymentOkEvent eventOk) {
		super.addPayment(eventOk);
		activarModoPagoTarjeta(false);
	}

	@Override
	protected void addCallbackPintadoLineas() {
		Callback<TableView<PagoTicketGui>, TableRow<PagoTicketGui>> callback = new Callback<TableView<PagoTicketGui>, TableRow<PagoTicketGui>>(){

			@Override
			public TableRow<PagoTicketGui> call(TableView<PagoTicketGui> p) {
				return new TableRow<PagoTicketGui>(){

					@Override
					protected void updateItem(PagoTicketGui pago, boolean empty) {
						super.updateItem(pago, empty);

						if (pago != null) {
							if (!pago.isRemovable()) {
								getStyleClass().add("cell-renderer-cupon");
							}
							else {
								getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							}
						}
						else {
							getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
						}
					}
				};
			}
		};
		tbPagos.setRowFactory(callback);
	}

	protected void realizarComprobacionesTicketCierrePantalla() throws NoCerrarPantallaException {
		super.realizarComprobacionesTicketCierrePantalla();

		if (((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosSad() != null) {
			/* Controlamos si debemos usar los servicios de envío a domicilio o no */
			if (!integracionRutas) {

				String idPedido = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosSad().getCodTicket();
				/* En caso de cancelar correctamente, lo devolvemos para que no vuelva a ocurrir. */
				getDatos().put(EnvioDomicilioKeys.CODPEDIDO, idPedido);

			}
			else {

				/* Realizamos la cancelación del pedido a domicilio */
				String mensajeConfirmar = "Si cancela los pagos, se cancelará el envío a domicilio. ¿Desea cancelar igualmente?";
				if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto(mensajeConfirmar), getStage())) {
					try {
						cancelarEnvioDomicilio();
					}
					catch (NoCerrarPantallaException e) {
						log.error("realizarComprobacionesTicketCierrePantalla() - " + e.getMessage());
						throw new NoCerrarPantallaException();
					}
				}
				else {
					throw new NoCerrarPantallaException();
				}

			}
		}
	}

	/**
	 * Realiza la cancelación de un pedido de envío a domicilio.
	 * 
	 * @throws RutasTokenException
	 * @throws RutasServiciosException
	 */
	private void cancelarEnvioDomicilio() throws NoCerrarPantallaException {
		try {
			pedidosService = SherpaApiBuilder.getSadPedidosService();

			String idPedido = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosSad().getCodTicket();
			/* Llamamos a la clase Task para que aparezca la carga */
			new CancelarPedidoTask(idPedido).start();

		}
		catch (Exception e) {
			log.error("accionCancelar() - " + e.getMessage());
			String mensajeConfirmar = "La cancelación de la reserva ha fallado, ¿Desea igualmente cancelar los pagos?";
			if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto(mensajeConfirmar), getStage())) {
				return;
			}
			else {
				throw new NoCerrarPantallaException();
			}
		}
	}

	private class CancelarPedidoTask extends BackgroundTask<EstadoPedido> {

		private String idPedido;

		public CancelarPedidoTask(String idPedido) {
			super();
			this.idPedido = idPedido;
		}

		@Override
		protected EstadoPedido call() {
			/* Realizamos la cancelación del pedido, para ello necesitamos el "idPedido" */
			log.debug("CancelarPedidoTask() - Pedido para cancelar : " + idPedido);
			return pedidosService.cancelapedido(idPedido);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			EstadoPedido respuesta = getValue();
			log.debug("CancelarPedidoTask() - " + respuesta);

			/* En caso de cancelar correctamente, lo devolvemos para que no vuelva a ocurrir. */
			getDatos().put(EnvioDomicilioKeys.CODPEDIDO, idPedido);
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("CancelarPedidoTask() - " + exception.getMessage());
			String mensajeError = "Se ha producido un error al cancelar el pedido de Servicio a Domicilio";
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
	}

	/**
	 * Recibe el error del servidor, lo recorta para extraer el mensaje de error y lo devuelve. Solo se puede aplicar a
	 * "disponibilidad()", "reservapedido()" y "cancelapedido()".
	 * 
	 * @param error
	 *            : Mensaje de error producido por un servicio.
	 * @return String
	 */
	public String getError(String error) {
		/* Recortamos el mensaje para traer la parte de JSON */
		String mensajeRecortado[] = error.split("content:");
		Gson gson = new Gson();

		RutasErroresBean errorControlado = null;
		try {
			errorControlado = gson.fromJson(mensajeRecortado[1], RutasErroresBean.class);
		}
		catch (Exception e) {
			return "Error al consultar el servicio";
		}

		return errorControlado.getDetail();
	}

	protected class BackgroudPrintTask extends BackgroundTask<Void> {

		protected Stage stage;
		protected boolean resetPrinter;

		public BackgroudPrintTask(Stage stage, boolean resetPrinter) {
			super(true);
			this.stage = stage;
			this.resetPrinter = resetPrinter;
		}

		@Override
		@SuppressWarnings("rawtypes")
		protected Void call() throws Exception {
			if (resetPrinter) {
				log.info("Reseteando impresora....");
				IPrinter impresora = Dispositivos.getInstance().getImpresora1();

				if (impresora.reset()) {
					log.info("Impresora reseteada con éxito.");
				}
			}

			String formatoImpresion = ticketManager.getTicket().getCabecera().getFormatoImpresion();

			if (formatoImpresion.equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
				log.info("BackgroudPrintTask::call() - Formato de impresión no configurado, no se imprimirá.");
				return null;
			}

			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			mapaParametros.put("ticket", ticketManager.getTicket());
			mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));

			FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
			if (datosFidelizado != null) {
				Map<String, Object> adicionales = datosFidelizado.getAdicionales();
				if(adicionales != null) {
					Object ticketDigital = datosFidelizado.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
					if(ticketDigital.equals(true) || ticketDigital.equals("true")) {
						mapaParametros.put("ticketDigital", true);
					}
				}
			}

			if (ticketManager.getTicket().getCabecera().getCodTipoDocumento().equals(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getCodtipodocumento())) {
				mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
			}

			ServicioImpresion.imprimir(formatoImpresion, mapaParametros);

			// Cupones
			if (BigDecimalUtil.isMayorACero(ticketManager.getTicket().getTotales().getTotal())) {
				List<CuponEmitidoTicket> cupones = ((DinoTicketVentaAbono) ticketManager.getTicket()).getCuponesEmitidos();
				if (cupones.size() > 0) {
					log.debug("BackgroudPrintTask::call() - Se van a imprimir " + cupones.size() + " cupones.");
					Map<String, Object> mapaParametrosCupon = new HashMap<String, Object>();
					mapaParametrosCupon.put("ticket", ticketManager.getTicket());
					for (CuponEmitidoTicket cupon : cupones) {
						if(ticketManager.getTicket().getCabecera().getDatosFidelizado() != null && cupon.getIdPromocionAplicacion().equals(cupon.getIdPromocionOrigen())) {
							//DIN-197 - No imprime cupones de clientes identificados dentro de sorteos
							continue;
						}
						log.debug("BackgroudPrintTask::call() - Imprimiendo cupón con código " + cupon.getCodigoCupon());
						mapaParametrosCupon.put("cupon", cupon);
						SimpleDateFormat df = new SimpleDateFormat();
						mapaParametrosCupon.put("fechaEmision", df.format(ticketManager.getTicket().getCabecera().getFecha()));
						SimpleDateFormat dfFin = new SimpleDateFormat("dd/MM/yyyy");
						Date fechaFin = ((DinoCuponEmitidoTicket) cupon).getFechaFin();
						if(fechaFin != null) {
							mapaParametrosCupon.put("fechaFin", dfFin.format(fechaFin));
						}
						ServicioImpresion.imprimir(PLANTILLA_CUPON, mapaParametrosCupon);
					}
				}
				if (!ticketManager.isEsDevolucion()) {
					// Imprimimos vale para cambio
					if (mediosPagosService.isCodMedioPagoVale(ticketManager.getTicket().getTotales().getCambio().getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
					        && !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())) {
						printVale(ticketManager.getTicket().getTotales().getCambio());
					}
				}
				else {
					if (documentos.isDocumentoAbono(ticketManager.getTicket().getCabecera().getCodTipoDocumento())) {
						// Es una devolución donde el signo del tipo de documento es positivo, imprimimos vales de pagos
						List<PagoTicket> pagos = ((TicketVenta) ticketManager.getTicket()).getPagos();
						for (PagoTicket pago : pagos) {
							if (mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
							        && BigDecimalUtil.isMenorACero(pago.getImporte())) {
								printVale(pago);
							}
						}
					}
				}
			}
			else {
				// Imprimimos vales para pagos si estamos en devolución pero no si es de cambio (pago positivo en una
				// devolucion donde los pagos son negativos)
				List<PagoTicket> pagos = ((TicketVenta) ticketManager.getTicket()).getPagos();
				for (PagoTicket pago : pagos) {
					if (mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento()) && !BigDecimalUtil.isMayorACero(pago.getImporte())) {
						printVale(pago);
					}
				}
			}

			return null;
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			finalizarTicket();
		}

		@Override
		protected void failed() {
			super.failed();

			Throwable e = getException();

			log.error("ImpresionTicketTask::failed() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);

			String error = e.getMessage();
			if (e instanceof DeviceException && e.getCause() != null) {
				if (e.getCause() instanceof PrintParserException) {
					error = e.getCause().getCause().getMessage();
				}
				else {
					error = e.getCause().getMessage();
				}
			}

			String mensajeError = I18N.getTexto("El ticket se ha generado correctamente aunque no se haya podido imprimir en papel.") + System.lineSeparator() + System.lineSeparator() + error
			        + System.lineSeparator() + I18N.getTexto("¿Desea reimprimir nuevamente el ticket?");

			VentanaDialogoComponent ventana = VentanaDialogoComponent.crearVentana(null, mensajeError, VentanaDialogoComponent.TIPO_ERROR, true, getStage(), e, true);
			if (ventana.isPulsadoAceptar()) {
				new BackgroudPrintTask(getStage(), true).start();
			}
			else {
				finalizarTicket();
			}
		}
	}

	@Override
	protected void imprimir() {
		new BackgroudPrintTask(this.getStage(), false).start();
	}

	@Override
	protected void accionSalvarTicketSucceeded() {
		comprobarActivacionTarjetasRegalo();
		
		if (!BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())) {
			HBoxPagos.setVisible(false);
			vbCambioPagos.setVisible(true);
			mostrarVentanaCambio();
		}
		
		mostrarMensajesSorteo();
		
		mostrarVentanaRascas();
		
		imprimir();
	}

	private void mostrarVentanaRascas() {
		int rascasConcedidos = ((DinoTicketManager) ticketManager).getRascasConcedidos();

		if(rascasConcedidos > 0) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(IntroduccionRascasController.PARAM_TICKET, ticketManager.getTicket());
			params.put(IntroduccionRascasController.PARAM_RASCAS, rascasConcedidos);
			
			getApplication().getMainView().showModalCentered(IntroduccionRascasView.class, params, getStage());
			
			int numRascasEntregados = (int) params.get(IntroduccionRascasController.PARAM_RASCAS_ENTREGADOS_CAJERO);
			((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setNumRascasEntregados(numRascasEntregados);
		}
	}

	private void comprobarActivacionTarjetasRegalo() {
		List<String> tarjetasRegaloNoActivas = new ArrayList<String>();
		
		for(DinoLineaTicket linea : (List<DinoLineaTicket>) ticketManager.getTicket().getLineas()) {
			TarjetaRegaloDto tarjetaRegalo = linea.getTarjetaRegalo();
			if(tarjetaRegalo != null && TarjetaRegaloDto.ESTADO_PENDIENTE_ACTIVAR.equals(tarjetaRegalo.getEstado())) {
				tarjetasRegaloNoActivas.add(tarjetaRegalo.getNumeroTarjeta());
			}
		}
		
		if(!tarjetasRegaloNoActivas.isEmpty()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La tarjeta no ha podido activarse con éxito. Contacte por favor con su responsable."), getStage());
		}
	}

	@Override
	protected void mostrarVentanaCambio() {
		lbEntregadoCambio.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getEntregado()));
		lbAPagarCambio.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getTotalAPagar()));
		lbDevolverCambio.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getCambio().getImporte()));
		lbFormaPagoCambio.setText(ticketManager.getTicket().getCabecera().getTotales().getCambio().getDesMedioPago());

		visor.escribir(I18N.getTexto("CAMBIO"), lbCambio.getText());

		activaTemporizadoCajon();
	}

	protected void finalizarTicket() {	
		DinoTicketManager dinoTicketManager = (DinoTicketManager) ticketManager;
		// Mostramos la ventana con la información de importe pagado, cambio...
		boolean pagoSinCambio = false;
		if (BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())) {
			pagoSinCambio = true;
		}
		ticketManager.notificarContadores();
		
		getDatos().put("esVentaDigital", dinoTicketManager.esVentaContenidoDigital());
		getDatos().put("esRecargaMovil", dinoTicketManager.esVentaRecargaMovil());
		
		ticketManager.finalizarTicket();
		if (pagoSinCambio) {
			getStage().close();
		}
		
		if (visor instanceof DinoVisorPantallaSecundaria && DinoVisorPantallaSecundaria.getModo() != DinoVisorPantallaSecundaria.MODO_PARTICIPACIONES_ANIVERSARIO) {
			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void introducirDescuentosPromocionales() {
		super.introducirDescuentosPromocionales();

		for (CuponAplicadoTicket cupon : (List<CuponAplicadoTicket>) ticketManager.getTicket().getCuponesAplicados()) {
			if (cupon.getIdTipoPromocion().equals(DinoTicketManager.ID_TIPO_PROMO_FICITIA_CUPON)) {
				String codMedioPago = prefijosCuponesService.getMedioPagoPrefijo(cupon.getCodigo());
				((TicketVenta) ticketManager.getTicket()).removePago(codMedioPago);
			}
		}

		for (CuponAplicadoTicket cupon : (List<CuponAplicadoTicket>) ticketManager.getTicket().getCuponesAplicados()) {
			if (cupon.getIdTipoPromocion().equals(DinoTicketManager.ID_TIPO_PROMO_FICITIA_CUPON)) {
				String codMedioPago = prefijosCuponesService.getMedioPagoPrefijo(cupon.getCodigo());
				if (codMedioPago != null) {
					MedioPagoBean medioPago = mediosPagosService.getMedioPago(codMedioPago);
					if (medioPago != null) {
						log.debug("introducirDescuentosPromocionales() - Añadiendo pago de cupones de: " + cupon.getCodigo() + " (" + cupon.getImporteTotalAhorrado() + ")");
						PagoTicket pago = ticketManager.nuevaLineaPago(codMedioPago, cupon.getImporteTotalAhorrado(), true, false, null, false);
						pago.addExtendedData(EXTENDED_DATA_CUPON, cupon.getCodigo());
					}
					else {
						log.error("introducirDescuentosPromocionales() - El medio de pago asociado al cupón no existe.");
					}
				}
				else {
					log.error("introducirDescuentosPromocionales() - El cupón " + cupon.getCodigo() + " no tiene medio de pago asociado.");
				}
			}
		}

		ticketManager.guardarCopiaSeguridadTicket();
	}

	@Override
	public void anotarPago(BigDecimal importe) {
		log.debug("anotarPago() - Anotando pago: medio de pago:" + medioPagoSeleccionado + " // Importe: " + importe);

		if (importe == null) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe introducido no es válido."), getStage());
			return;
		}

		if (medioPagoSeleccionado == null) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay ninguna forma de pago seleccionada."), getStage());
			return;
		}

		if (importe.compareTo(BigDecimal.ZERO) == 0) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El importe introducido no puede ser cero."), getStage());
			return;
		}

		String codMedioPago = medioPagoSeleccionado.getCodMedioPago();
		
		if (BigDecimalUtil.isMayor(importe, new BigDecimal("9999.99")) && MediosPagosService.medioPagoDefecto.getCodMedioPago().equals(codMedioPago)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede pagar más de 9999,99 € en efectivo."), getStage());
			return;
		}

		if (!MediosPagosService.medioPagoDefecto.getCodMedioPago().equals(codMedioPago) && !CODMEDPAG_EMPLEADO.equals(codMedioPago) && !CODMEDPAG_RESIDENTE.equals(codMedioPago) && ! CODMEDPAG_VIP.equals(codMedioPago)
		        && BigDecimalUtil.isMayor(importe.abs(), ticketManager.getTicket().getTotales().getPendiente().abs())) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El medio de pago seleccionado no permite introducir un importe superior al importe pendiente."), getStage());
			tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
			tfImporte.requestFocus();
			return;
		}

		PaymentMethodManager manager = paymentsManager.getPaymentsMehtodManagerAvailables().get(codMedioPago);
		if (importeMaximoVirtualMoney != null && manager != null && (manager instanceof GttManager || manager instanceof VirtualMoneyManager) && BigDecimalUtil.isMayor(importe, importeMaximoVirtualMoney)) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El importe indicado supera el saldo de la tarjeta."), getStage());
			tfImporte.requestFocus();
			return;
		}

		incluirPagoTicket(importe);

		tecladoNumerico.requestFocus();
		tfImporte.requestFocus();

		importeMaximoVirtualMoney = null;
	}

	@Override
	protected void selectDefaultPaymentMethod() {
		super.selectDefaultPaymentMethod();

		importeMaximoVirtualMoney = null;
		activarModoPagoTarjeta(false);
		refrescarDatosPantalla();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();
		
		encuestasHechas = false;
		
		tarjetasFidelizadoDisponibles = null;
		tarjetasPagoAutomatico = null;

		importeMaximoVirtualMoney = null;
		if (getDatos().containsKey(EnvioDomicilioKeys.CONTROLAR_SERVICIOS_SAD)) {
			integracionRutas = (Boolean) getDatos().get(EnvioDomicilioKeys.CONTROLAR_SERVICIOS_SAD);
		}
		else {
			integracionRutas = true;
		}

		lbTitulo.getStyleClass().removeAll("cabecera-pago");
		lbTitulo.getStyleClass().removeAll("cabecera-devolucion");
		if (isDevolucion()) {
			lbTitulo.getStyleClass().add("cabecera-devolucion");
		}
		else {
			lbTitulo.getStyleClass().add("cabecera-pago");
		}

		if (((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getDatosSad() != null) {
			ivEnvioDomicilio.setVisible(true);
		}
		else {
			ivEnvioDomicilio.setVisible(false);
		}

		activarModoPagoTarjeta(false);

		vbConfirmacionPagoTarjeta.setVisible(false);
		vbCambioPagos.setVisible(false);
		vbPagoTarjetas.setVisible(false);
		HBoxPagos.setVisible(true);
		
		ServicioRepartoDto servicioRepartoDto = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto();
		if(servicioRepartoDto == null) {
			introducirTarjetasFidelizado();
		}
		
		mostrarServicioRepartoSeleccionado();
		
		validatePendigPaymentSipay();
		
	}



	@SuppressWarnings("rawtypes")
	@Override
	public void refrescarDatosPantalla() {
		super.refrescarDatosPantalla();

		lbTextAPagar.setText(I18N.getTexto("PENDIENTE"));
		lbPendiente.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getTotalAPagar()));
		lbTotal.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));

		if (!BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())) {
			lbCambio.setStyle("-fx-background-color: #edda07");
		}
		else {
			lbCambio.setStyle("");
		}

		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if (datosFidelizado == null) {
			lbTicketDigital.setVisible(false);
			btTicketDigital.setVisible(false);
			ivEcologico.setVisible(false);
		}
		else {
			boolean ticketDigitalActivado = false;
			
			Map<String, Object> adicionales = datosFidelizado.getAdicionales();
			if(adicionales != null) {
				Object paramTicketDigital = adicionales.get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
				ticketDigitalActivado = (paramTicketDigital != null && (paramTicketDigital instanceof Boolean)) ? (boolean) paramTicketDigital : false;
			}
			
			if (!ticketDigitalActivado) {
				lbTicketDigital.setVisible(false);
				btTicketDigital.setVisible(false);
				ivEcologico.setVisible(false);
			}
			else {
				lbTicketDigital.setVisible(true);
				btTicketDigital.setVisible(true);
				ivEcologico.setVisible(true);
				cambiarBotonImpresionDigital(datosFidelizado);
			}
		}
		
		lbPendienteTarjetasFidelizado.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
		
		visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
		
		TarjetaIdentificacionDto tarjeta = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).buscarTarjeta("BP");
 		if(tarjeta != null) {
			ivTarjetaBp.setVisible(true);
		}
 		else {
 			ivTarjetaBp.setVisible(false);
 		}
	}

	protected void cambiarBotonImpresionDigital(FidelizacionBean datosFidelizado) {
		boolean ticketDigitalActivado = (boolean) datosFidelizado.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
		btTicketDigital.getStyleClass().remove("btTicketDigital");
		btTicketDigital.getStyleClass().remove("btTicketDigitalNo");
		if (ticketDigitalActivado) {
			lbTicketDigital.setText(I18N.getTexto("Ticket digital activo"));
			btTicketDigital.setText(I18N.getTexto("Imprimir ticket"));
			btTicketDigital.getStyleClass().add("btTicketDigital");
		}
		else {
			lbTicketDigital.setText(I18N.getTexto("Ticket digital desactivado"));
			btTicketDigital.setText(I18N.getTexto("No imprimir ticket"));
			btTicketDigital.getStyleClass().add("btTicketDigitalNo");
		}
	}

	@Override
	public void accionCancelar() {
		log.debug("accionCancelar() - Borrando cupones si los hubiese.");
		List<IPagoTicket> payments = ticketManager.getTicket().getPagos();
		Iterator<IPagoTicket> it = payments.iterator();
		while (it.hasNext()) {
			IPagoTicket payment = it.next();
			if (payment.getPaymentId() == null) {
				it.remove();
				ticketManager.getTicket().getTotales().recalcular();
			}
		}
		ticketManager.getTicket().getCuponesAplicados().clear();

		super.accionCancelar();
	}

	public void accionAceptarCambio() {
		IVisor visor = Dispositivos.getInstance().getVisor();
		visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));

		desactivaTemporizadoCajon();

		this.getStage().close();
	}

	public void seleccionarMedioPago(HashMap<String, String> parametros) {
		if (parametros.containsKey("codMedioPago")) {
			String codMedioPago = parametros.get("codMedioPago");
			MedioPagoBean medioPago = mediosPagosService.getMedioPago(codMedioPago);
			if (medioPago != null) {
				medioPagoSeleccionado = medioPago;
				paymentsManager.select(medioPago.getCodMedioPago());

				if (!medioPago.getCodMedioPago().equals("1XXX")) {
					lbMedioPago.setText(medioPago.getDesMedioPago());
				}

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

	@Override
	protected void processPaymentError(PaymentsErrorEvent event) {
		super.processPaymentError(event);

		if (tarjetasPagoAutomatico != null && !tarjetasPagoAutomatico.isEmpty()) {
			introducirPrimeraTarjetaPagoAutomatico();
		}
		
		if (vbPagoTarjetas.isVisible()) {
			activarPagoAutomaticoEnPantallaIntermediaTarjetas();
			refrescarPantallaIntermediaTarjeta();
		}
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				selectDefaultPaymentMethod();
			}
		});
	}

	@Override
	protected void restaurarFocoTFImporte() {
		super.restaurarFocoTFImporte();

		tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente().abs()));
	}

	protected void activaTemporizadoCajon() {
		if ((Dispositivos.getInstance().getCajon() instanceof CajonNoConfig))
			return;

		if (timeline == null) {
			timeline = new Timeline(new KeyFrame(Duration.millis(100), new TemporizadorCajon()));
			timeline.setCycleCount(Timeline.INDEFINITE);
		}

		log.debug("Se activa temporizador para detectar cierre de cajon en ventana de cambio");
		timeline.play();
	}

	protected void desactivaTemporizadoCajon() {
		if (timeline != null) {
			timeline.stop();
		}
	}

	protected class TemporizadorCajon implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			if (!Dispositivos.getInstance().getCajon().isOpen() && getStage().isFocused() && btAceptarCambio.isFocused()) {
				log.debug("Detectado cierre de cajón. Se pulsa botón de aceptar cambio");
				timeline.stop();
				btAceptarCambio.fire();
			}
			event.consume();
		}
	}

	public void cambiarImpresionTicketDigital() {
		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		boolean ticketDigitalActivo = (boolean) datosFidelizado.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
		datosFidelizado.getAdicionales().put(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL, !ticketDigitalActivo);

		cambiarBotonImpresionDigital(datosFidelizado);
	}
	
	public void introducirTarjetasFidelizado() {
		desactivarPagoAutomaticoEnPantallaIntermediaTarjetas();	
		
		// Si el ticket tiene pagos no metemos las tarjetas de nuevo, ya que llegamos desde la recuperación 
		// de la copia de seguridad y los pagos de estas tarjetas ya se han añadido anteriormente
		List<PagoTicket> pagosTicket = ticketManager.getTicket().getPagos();
		if(pagosTicket != null && !pagosTicket.isEmpty()) {
			for(PagoTicket pago : pagosTicket) {
				if(pago.isIntroducidoPorCajero()) {
					return;
				}
			}
		}
		
		List<TarjetaIdentificacionDto> tarjetasIdentificacion = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getTarjetasIdentificacion();
		List<String> tarjetas = new ArrayList<String>();
		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		if(datosFidelizado != null && datosFidelizado.getAdicionales() != null) {
			tarjetas = (List<String>) datosFidelizado.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TARJETAS);
		}
		
		if ((tarjetas != null && !tarjetas.isEmpty()) || (tarjetasIdentificacion != null && !tarjetasIdentificacion.isEmpty())) {
			tarjetasFidelizadoDisponibles = obtenerTarjetasDisponibles(tarjetasIdentificacion, tarjetas);
			tarjetasPagoAutomatico = rellenaListaTarjetasAuto();
			
			introducirPrimeraTarjetaPagoAutomatico();
			
			if (!tarjetasFidelizadoDisponibles.isEmpty()) {
				mostrarIntroduccionTarjetas();
			}

		}
	}

	protected void introducirTarjetaEmpleado(String tarjetaEmpleado) {
		numTarjetaLeido = tarjetaEmpleado;
		HashMap<String, String> parametros = new HashMap<String, String>();
		parametros.put("codMedioPago", CODMEDPAG_EMPLEADO);
		seleccionarMedioPago(parametros);
	}

	protected String obtenerTarjetaEmpleado(List<String> tarjetasDisponibles) {
		String tarjetaEmpleado = null;
		for(String tarjeta : tarjetasDisponibles) {
			String codMedioPago = prefijosTarjetasService.getMedioPagoPrefijo(tarjeta);
			if(StringUtils.isNotBlank(codMedioPago) && CODMEDPAG_EMPLEADO.equals(codMedioPago)) {
				tarjetaEmpleado = tarjeta;
			}
		}
		return tarjetaEmpleado;
	}

	protected List<String> obtenerTarjetasDisponibles(List<TarjetaIdentificacionDto> tarjetasIdentificacion, List<String> tarjetas) {
		List<String> tarjetasDisponibles = new ArrayList<String>();
		if(tarjetas != null && !tarjetas.isEmpty()) {
			tarjetasDisponibles.addAll(tarjetas);
		}
		if(tarjetasIdentificacion != null && !tarjetasIdentificacion.isEmpty()) {
			for(TarjetaIdentificacionDto tarjeta : tarjetasIdentificacion) {
				String numeroTarjeta = tarjeta.getNumeroTarjeta();
				if(!tarjetasDisponibles.contains(numeroTarjeta)) {
					tarjetasDisponibles.add(numeroTarjeta);
				}
			}
		}
		return tarjetasDisponibles;
	}
	
	@Override
	protected void processPaymentOk(PaymentsOkEvent event) {
		super.processPaymentOk(event);
		
		if (!event.getOkEvent().isCanceled()) {
			mostrarMensajeCobroTefCorrecto(event);			
		}

		if (tarjetasPagoAutomatico != null && !tarjetasPagoAutomatico.isEmpty()) {
			introducirPrimeraTarjetaPagoAutomatico();
		}

		if (tarjetasPagoAutomatico == null || tarjetasPagoAutomatico.isEmpty()) {
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					Object source = event.getOkEvent().getSource();
					if ((source instanceof DescuentosEmpleadoManager)
					        && (((DescuentosEmpleadoManager) source).getPaymentCode().equals(CODMEDPAG_EMPLEADO) || ((DescuentosEmpleadoManager) source).getPaymentCode().equals(CODMEDPAG_VIP))
					        && !event.getOkEvent().isCanceled()) {
						mostrarIntroduccionTarjetas();
					}
				}
			});
		}

		if (ultimoPagoTarjetaFidelizado != null) {
			ultimoPagoTarjetaFidelizado.setPagoHabilitado(false);
			tbTarjetasFidelizado.refresh();
		}

		if (vbPagoTarjetas.isVisible()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					activarPagoAutomaticoEnPantallaIntermediaTarjetas();
					refrescarPantallaIntermediaTarjeta();
				}
			});
		}
		
		if(event.getOkEvent().getSource() instanceof DescuentosEmpleadoManager) {
			BigDecimal amount = event.getOkEvent().getAmount();
			if(!event.getOkEvent().isCanceled()) {
				amount = amount.negate();
			}
			((DinoPaymentsManagerImpl) paymentsManager).addPending(amount);
		}
	}

	private void mostrarMensajeCobroTefCorrecto(PaymentsOkEvent event) {
		try {
			PaymentMethodManager manager = (PaymentMethodManager) event.getOkEvent().getSource();
			if(manager.getPaymentCode().equals("0002")) {
				vbConfirmacionPagoTarjeta.setVisible(true);
			}
		}
		catch(Exception e) {
			log.error("mostrarMensajeCobroTefCorrecto() - Error al comprobar cobro de EFT correcto: " + e.getMessage(), e);
		}
	}
	
	public void mostrarIntroduccionTarjetas() {
		if(tarjetasGui != null) {
			tarjetasGui = null;
		}
		
		if(tarjetasFidelizadoDisponibles != null) {
			refrescarPantallaIntermediaTarjeta();
			
			boolean hayTarjetas = !tarjetasGui.isEmpty();
			if(hayTarjetas) {
				log.debug("mostrarIntroduccionTarjetas() - Pantalla intermedia visible con tarjetas: " + tarjetasGui);
				HBoxPagos.setVisible(false);
				vbCambioPagos.setVisible(false);
				vbPagoTarjetas.setVisible(true);
				tbTarjetasFidelizado.setItems(FXCollections.observableArrayList(tarjetasGui));
			}
		}
	}

	protected void refrescarPantallaIntermediaTarjeta() {
		log.debug("mostrarIntroduccionTarjetas() - Mostrando pantalla intermedia de introducción de tarjetas");
		
		lbPendienteTarjetasFidelizado.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
		
		if(tarjetasGui == null) {
			tarjetasGui = new ArrayList<TarjetaFidelizadoGui>();
			for(String tarjeta : tarjetasFidelizadoDisponibles) {
				TarjetaFidelizadoGui gui = new TarjetaFidelizadoGui();
				gui.setNumeroTarjeta(tarjeta);
				
				String medioPago = prefijosTarjetasService.getMedioPagoPrefijo(tarjeta);
				if(StringUtils.isBlank(medioPago)) {
					continue;
				}
				MedioPagoBean medioPagoBean = mediosPagosService.getMedioPago(medioPago);
				if(medioPagoBean == null) {
					continue;
				}
				gui.setMedioPago(medioPagoBean);
				
				tarjetasGui.add(gui);
			}
		}
		
		for(TarjetaFidelizadoGui gui : tarjetasGui) {
			String medioPago = gui.getMedioPago().getCodMedioPago();
			String tarjeta = gui.getNumeroTarjeta();
			
			PaymentMethodManager manager = null;
			for(PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
				if(paymentMethodManager.getPaymentCode().equals(medioPago)) {
					manager = paymentMethodManager;
				}
			}
			
			if(manager != null) {
				if(manager instanceof BPManager) {
					Task<ExtendedBalanceInquiryResponse> taskComprobacion = new Task<ExtendedBalanceInquiryResponse>(){
						@Override
						protected ExtendedBalanceInquiryResponse call() throws Exception {
							return bpManager.getSaldo(tarjeta);
						}
						
						@Override
						protected void succeeded() {
							super.succeeded();
							
							ExtendedBalanceInquiryResponse saldoResponse = getValue();
							
							SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yy");
							BigDecimal saldo = BigDecimal.ZERO;
							BigDecimal saldoDisponible = null;
							BigDecimal tramo = null;
							
							if (saldoResponse != null) {
								if (saldoResponse.getTenderRedemptionGroupDataList() != null) {
									for (TenderRedemptionGroupDataResponse virtual : saldoResponse.getTenderRedemptionGroupDataList()) {
										/* Filtramos para solo coger uno de los dos saldos que nos trae. */
										if (BPManager.VIRTUAL_MONEY_CODE.equals(virtual.getTenderRedemptionGroupCode())) {
											saldo = new BigDecimal(virtual.getVirtualMoneyBalance());
											String saldoTexto = I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(saldo);
											saldoTexto = saldoTexto + " (Epx : " + sdfFecha.format(virtual.getVirtualMoneyExpirationDate()) + ")";
											gui.setSaldoDisponible(saldo);
										}

										if (!BigDecimalUtil.isIgualACero(saldo)) {
											/* En caso de ser una venta, se inserta también el importe. */
											if (ticketManager.getTicket().getCabecera().esVenta()) {
												BigDecimal total = ticketManager.getTicket().getTotales().getPendiente();

												tramo = ((BPManager) bpManager).getTramo();
												BigDecimal eurosTramo = ((BPManager) bpManager).getEurosTramo();
												boolean condicionQuemadoPositiva = ((BPManager) bpManager).isCondicionQuemadoPositiva();

												saldoDisponible = BigDecimal.ZERO;
												if (tramo == null || eurosTramo == null || BigDecimalUtil.isMenorACero(tramo) || BigDecimalUtil.isMenorACero(eurosTramo)) {
													throw new RuntimeException(I18N.getTexto("No está configurado el pago con la tarjeta BP."));
												}
												else {
													if (condicionQuemadoPositiva && BigDecimalUtil.isMayorACero(total)) {
														saldoDisponible = total.divide(tramo, 0, RoundingMode.FLOOR);
													}
													else {
														saldoDisponible = total.divide(tramo, 0, RoundingMode.CEILING);
													}
													saldoDisponible = BigDecimalUtil.redondear(saldoDisponible.multiply(eurosTramo));
												}

												if (BigDecimalUtil.isMayor(saldoDisponible, saldo)) {
													saldoDisponible = saldo;
												}

												gui.setImportePago(saldoDisponible);
											}
										}
									}
								}
							}
							
							boolean existePagoBp = false;
							for(PagoTicket pago : (List<PagoTicket>) ticketManager.getTicket().getPagos()) {
								if(pago.getCodMedioPago().equals(bpManager.getPaymentCode())) {
									existePagoBp = true;
									break;
								}
							}
							
							if(saldoDisponible != null && BigDecimalUtil.isMayorACero(saldoDisponible)) {
								if(!existePagoBp) {
									gui.setPagoHabilitado(true);
								}
								else {
									gui.setPagoHabilitado(false);
								}
							}
							else {
								gui.setImportePago(BigDecimal.ZERO);
								
								if(BigDecimalUtil.isIgualACero(saldo)) {
									gui.setMensaje(I18N.getTexto("Saldo no activo / caducado."));
								}
								else {
									gui.setMensaje(I18N.getTexto("No aplica para compra menor de " + FormatUtil.getInstance().formateaNumero(tramo, 0) + "€"));
								}
							}
							
							if(gui.isPagoHabilitado()) {
								if(activarPagoAutomaticoEnPantallaIntermediaTarjetas) {
									log.debug("refrescarPantallaIntermediaTarjeta() - Pagando de forma automática con la tarjeta BP del fidelizado: " + gui.getNumeroTarjeta());
									desactivarPagoAutomaticoEnPantallaIntermediaTarjetas();
									accionAnotarPagoTarjetaFidelizado(gui, gui.getImportePago());
								}
							}
							
							tbTarjetasFidelizado.refresh();
							tbTarjetasFidelizado.autosize();
						}
						
						@Override
						protected void failed() {
							super.failed();
							
							Throwable e = getException();
							log.error("mostrarIntroduccionTarjetas() - Error al consultar el saldo de la tarjeta " + gui.getNumeroTarjeta() + ": " + e.getMessage(), e);
							
							gui.setSaldoDisponible(new BigDecimal(-1));
							gui.setImportePago(new BigDecimal(-1));
							gui.setMensaje(e.getMessage());
							gui.setPagoHabilitado(false);
							
							tbTarjetasFidelizado.refresh();
							tbTarjetasFidelizado.autosize();
						}
					};
					ExecutorService executor = Executors.newFixedThreadPool(2);
					executor.execute(taskComprobacion);
				}
				else if (manager instanceof VirtualMoneyManager) {
					Task<AccountDTO> taskComprobacion = new Task<AccountDTO>(){

						@Override
						protected AccountDTO call() throws Exception {
							return vmManager.consultarSaldo(tarjeta);								
						}

						@Override
						protected void succeeded() {
							super.succeeded();

							AccountDTO saldoResponse = getValue();

							BigDecimal saldo = BigDecimal.ZERO;
							BigDecimal saldoDisponible = null;
							Date fechaExpiracion = null;

							if (saldoResponse != null) {

								BigDecimal importeMaximoVirtualMoney = saldoResponse.getPrincipaltBalance().getBalance();

								String tipoRedencion = saldoResponse.getRedemptionType();

								if (StringUtils.isNotBlank(tipoRedencion) && tipoRedencion.equals("P")) {
									if (BigDecimalUtil.isMayorACero(importeMaximoVirtualMoney)) {
										if (ticketManager.getTicket().getCabecera().esVenta()) {
											gui.setSaldoDisponible(importeMaximoVirtualMoney);
											saldoDisponible = importeMaximoVirtualMoney;

											BigDecimal importePago = ticketManager.getTicket().getTotales().getPendiente();
											if (saldoDisponible.compareTo(ticketManager.getTicket().getTotales().getPendiente()) < 0) {
												importePago = saldoDisponible;
											}
											gui.setImportePago(importePago);
										}
									}
								}
								else {
									gui.setSaldoDisponible(saldoResponse.getPrincipaltBalance().getBalance());
								}
								
								fechaExpiracion = saldoResponse.getEndDate();
								
							}
							

							if (fechaExpiracion != null && fechaExpiracion.before(new Date())) {
								gui.setMensaje(I18N.getTexto("Tarjeta expirada el ") + FormatUtil.getInstance().formateaFecha(fechaExpiracion));
								gui.setPagoHabilitado(false);
							}
							else if (saldoDisponible != null && BigDecimalUtil.isMayorACero(saldoDisponible)) {
								gui.setPagoHabilitado(true);
							}
							else {
								gui.setImportePago(BigDecimal.ZERO);

								if (BigDecimalUtil.isIgualACero(saldo)) {
									gui.setMensaje(I18N.getTexto("Sin saldo."));
								}
							}
							
							tbTarjetasFidelizado.refresh();
							tbTarjetasFidelizado.autosize();
						}

						@Override
						protected void failed() {
							super.failed();

							Throwable e = getException();
							String message = e.getMessage();
							log.error("mostrarIntroduccionTarjetas() - Error al consultar el saldo de la tarjeta " + gui.getNumeroTarjeta() + ": " + message, e);

							gui.setSaldoDisponible(new BigDecimal(-1));
							gui.setImportePago(new BigDecimal(-1));
														
							gui.setMensaje(I18N.getTexto("Error de conexión, no disponible"));
							gui.setPagoHabilitado(false);

							tbTarjetasFidelizado.refresh();
							tbTarjetasFidelizado.autosize();
						}
					};
					ExecutorService executor = Executors.newFixedThreadPool(2);
					executor.execute(taskComprobacion);
				}
				
			}
			
		}
	}
	
	public void terminarIntroduccionTarjetas() {
		HBoxPagos.setVisible(true);
		vbCambioPagos.setVisible(false);
		vbPagoTarjetas.setVisible(false);
		
		log.debug("terminarIntroduccionTarjetas() - Pantalla intermedia de introducción de tarjetas no visible.");
		
		tfImporte.requestFocus();
	}

	protected void accionAnotarPagoTarjetaFidelizado(TarjetaFidelizadoGui value, BigDecimal importePago) {
		medioPagoSeleccionado = value.getMedioPago();
		
		if(bpManager.getPaymentCode().equals(medioPagoSeleccionado.getCodMedioPago())) {
			bpManager.addParameter(BPManager.PARAM_NUMERO_TARJETA_BP, value.getNumeroTarjeta());
		}
		
		buscarVmManager(medioPagoSeleccionado.getCodMedioPago());
		if(vmManager != null) {
			vmManager.addParameter(VirtualMoneyManager.PARAM_NUMERO_TARJETA, value.getNumeroTarjeta());
		}
		else {
			return;
		}
		
		ultimoPagoTarjetaFidelizado = value;
		anotarPago(importePago);
	}
	
	@Override
	protected void pay(PaymentMethodManager paymentMethodManager, String codMedioPago, BigDecimal importe) {
		final BigDecimal importeFinal = importe;
		final String codMedioPagoFinal = codMedioPago;
		new BackgroundTask<Void>(){

			@Override
			protected Void call() throws Exception {
					paymentsManager.pay(codMedioPagoFinal, importeFinal);
					return null;
			}

			@Override
			protected void succeeded() {
				super.succeeded();

				/* Actualizamos los importes de pago de las tarjetas que lo necesiten con el nuevo total pendiente */
				actualizarImportesPago();
			}

			@Override
			protected void failed() {
				super.failed();

				Throwable e = getException();

				if (e instanceof PaymentException) {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, ((PaymentException) e).getPaymentId(), e, ((PaymentException) e).getErrorCode(), ((PaymentException) e).getMessage());
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);

				}
				else {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, -1, e, null, null);
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);
				}
			}
		}.start();
	}

	private void actualizarImportesPago() {
		if (tarjetasGui != null && !tarjetasGui.isEmpty()) {
			for (TarjetaFidelizadoGui tarjeta : tarjetasGui) {
				BigDecimal pendiente = ticketManager.getTicket().getTotales().getPendiente();
				if (tarjeta.getImportePago() != null && BigDecimalUtil.isMayor(tarjeta.getImportePago(), pendiente) && tarjeta.isPagoHabilitado()) {
					tarjeta.setImportePago(pendiente);
				}
			}

			tbTarjetasFidelizado.refresh();
		}
	}

	protected void prepararTablaTarjetasFidelizado() {
		tcNumeroTarjeta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTarjetasFidelizado", "tcNumeroTarjeta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcTipoTarjeta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTarjetasFidelizado", "tcTipoTarjeta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcMensajeTarjeta.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbTarjetasFidelizado", "tcMensajeTarjeta", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcSaldoDisponible.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbTarjetasFidelizado", "tcMensajeTarjeta", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcImportePago.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbTarjetasFidelizado", "tcMensajeTarjeta", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		
		tcNumeroTarjeta.setCellValueFactory(new PropertyValueFactory<TarjetaFidelizadoGui, String>("numeroTarjeta"));
		tcMensajeTarjeta.setCellValueFactory(new PropertyValueFactory<TarjetaFidelizadoGui, String>("mensaje"));
		
		tcTipoTarjeta.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TarjetaFidelizadoGui, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<TarjetaFidelizadoGui, String> cdf) {
				return new SimpleStringProperty(cdf.getValue().getMedioPago().getDesMedioPago());
			}
		});
		
		tcSaldoDisponible.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TarjetaFidelizadoGui, BigDecimal>, ObservableValue<BigDecimal>>(){
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<TarjetaFidelizadoGui, BigDecimal> cdf) {
				BigDecimal saldoDisponible = cdf.getValue().getSaldoDisponible();
				if(saldoDisponible != null && BigDecimalUtil.isMayorOrIgualACero(saldoDisponible)) {
					return new SimpleObjectProperty<BigDecimal>(saldoDisponible);
				}
				return null;
			}
		});
		
		tcImportePago.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TarjetaFidelizadoGui, BigDecimal>, ObservableValue<BigDecimal>>(){
			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<TarjetaFidelizadoGui, BigDecimal> cdf) {
				BigDecimal importePago = cdf.getValue().getImportePago();
				if(importePago != null && BigDecimalUtil.isMayorOrIgualACero(importePago)) {
					return new SimpleObjectProperty<BigDecimal>(importePago);
				}
				return null;
			}
		});
		
		tcBotonPagar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TarjetaFidelizadoGui, VBox>, ObservableValue<VBox>>(){
			@Override
			public ObservableValue<VBox> call(CellDataFeatures<TarjetaFidelizadoGui, VBox> cdf) {
				VBox contenedor = new VBox();

				TarjetaFidelizadoGui value = cdf.getValue();

				BigDecimal importePago = value.getImportePago();

				if (importePago == null) {
					contenedor.getChildren().add(new ProgressIndicator());
				}
				else {
					Button button = new Button();
					button.setText(I18N.getTexto("Incluir Pago"));
					
					if (value.isPagoHabilitado()) {
						button.setDisable(false);
					}
					else {
						button.setDisable(true);
					}

					button.setOnAction(new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent event) {
							accionAnotarPagoTarjetaFidelizado(value, importePago);
						}
					});

					contenedor.getChildren().add(button);
				}

				return new ObservableValueBase<VBox>(){

					@Override
					public VBox getValue() {
						return contenedor;
					}
				};
			}
		});
	}
	
	public void introducirCuponOnline() {
		GttManager gttManager = null;
		for (PaymentMethodManager paymentMethodManager : paymentsManager.getPaymentsMehtodManagerAvailables().values()) {
			if (paymentMethodManager instanceof GttRedencionCompletaManager && paymentMethodManager.getPaymentCode().equals("0405")) {
				gttManager = (GttRedencionCompletaManager) paymentMethodManager;
				break;
			}
		}
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CuponOnlineController.PARAM_GTT_MANAGER, gttManager);
		getApplication().getMainView().showModalCentered(CuponOnlineView.class, params, getStage());

		String codMedioPago = (String) params.get(CuponOnlineController.PARAM_COD_MEDIO_PAGO);
		if (StringUtils.isNotBlank(codMedioPago)) {
			numTarjetaLeido = (String) params.get(CuponOnlineController.PARAM_NUM_TARJETA);
			HashMap<String, String> parametros = new HashMap<String, String>();
			parametros.put("codMedioPago", codMedioPago);
			seleccionarMedioPago(parametros);
		}
	}

	private void askVirtualMoneyCard(VirtualMoneyManager source) {
		try {
			String numTarjeta = numTarjetaLeido;

			if (StringUtils.isNotBlank(numTarjeta)) {
				new ConsultarSaldoVirtualMoney(source, numTarjeta).start();
			}
			else {
				selectDefaultPaymentMethod();
			}
		}
		catch (Exception e) {
			log.error("askVirtualMoneyCard() - Ha habido un error al pedir el número de tarjeta: " + e.getMessage(), e);
			selectDefaultPaymentMethod();
		}
	}
	
	private class ConsultarSaldoVirtualMoney extends BackgroundTask<AccountDTO> {
		
		private VirtualMoneyManager manager;
		
		private String numeroTarjeta;

		public ConsultarSaldoVirtualMoney(VirtualMoneyManager manager, String numeroTarjeta) {
			super();
			this.manager = manager;
			this.numeroTarjeta = numeroTarjeta;
		}

		@Override
		protected AccountDTO call() throws Exception {
			return manager.consultarSaldo(numeroTarjeta);
		}
		
		@Override
		protected void succeeded() {
			super.succeeded();
			
			manager.addParameter(VirtualMoneyManager.PARAM_NUMERO_TARJETA, numeroTarjeta);
			
			procesarSaldoVirtualMoney(manager, getValue());
		}
		
		@Override
		protected void failed() {
			super.failed();
			
			selectDefaultPaymentMethod();
			
			Throwable exception = getException();
			ApiClientException e = (ApiClientException) exception;
			
			String tipoCuenta = manager.getTipoCuenta();
			tipoCuenta = tipoCuenta.replace("-", " ");
			
			VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido consultar el saldo de la {0}: ", tipoCuenta) + e.getMessage(), e);
		}
	}

	private void procesarSaldoVirtualMoney(VirtualMoneyManager manager, AccountDTO account) {
		importeMaximoVirtualMoney = account.getPrincipaltBalance().getBalance();
		
		String tipoRedencion = account.getRedemptionType();
		
		if(manager instanceof DescuentosEmpleadoManager) {
			// Si es un descuento de empleado, no se pide el importe a introducir ya que se pasa el total del ticket
			// para calcular el descuento correspondiente a la venta.
			tipoRedencion = "C";
			importeMaximoVirtualMoney = BigDecimal.ONE;
		}

		if (StringUtils.isNotBlank(tipoRedencion) && tipoRedencion.equals("P")) {
			lbSaldo.setText(I18N.getTexto("Saldo") + " : " + FormatUtil.getInstance().formateaImporte(importeMaximoVirtualMoney));
			if (BigDecimalUtil.isMayorACero(importeMaximoVirtualMoney)) {
				if (ticketManager.getTicket().getCabecera().esVenta()) {
					if (BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(), importeMaximoVirtualMoney)) {
						tfImporte.setText(FormatUtil.getInstance().formateaImporte(importeMaximoVirtualMoney));
					}
				}
			}
		}
		else {
			anotarPago(importeMaximoVirtualMoney);
		}
	}

	private List<String> rellenaListaTarjetasAuto() {
		List<String> listaTarjetasAuto = new ArrayList<String>();
		if (tarjetasFidelizadoDisponibles != null && !tarjetasFidelizadoDisponibles.isEmpty()) {
			for (String tarjeta : tarjetasFidelizadoDisponibles) {
				if (esTarjetaPagoAutomatico(tarjeta)) {
					listaTarjetasAuto.add(tarjeta);
				}
			}
		}

		return listaTarjetasAuto;
	}
	
	protected void introducirPrimeraTarjetaPagoAutomatico() {
		if (tarjetasPagoAutomatico != null && !tarjetasPagoAutomatico.isEmpty()) {
			String tarjeta = obtenerTarjetaAuto(tarjetasPagoAutomatico);
			
			log.debug("introducirPrimeraTarjetaPagoAutomatico() - Se va a introducir de forma automática la tarjeta: " + tarjeta);
			
			if (StringUtils.isNotBlank(tarjeta)) {
				tarjetasFidelizadoDisponibles.remove(tarjeta);
				tarjetasPagoAutomatico.remove(tarjeta);

				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						if (StringUtils.isNotBlank(tarjeta)) {
							introducirTarjetaAuto(tarjeta);
						}
					}
				});
			}
		}
		else {
			activarPagoAutomaticoEnPantallaIntermediaTarjetas();
		}
	}

	private void activarPagoAutomaticoEnPantallaIntermediaTarjetas() {
		log.debug("activarPagoAutomaticoEnPantallaIntermediaTarjetas() - Activando");
		activarPagoAutomaticoEnPantallaIntermediaTarjetas = true;
	}

	private void desactivarPagoAutomaticoEnPantallaIntermediaTarjetas() {
		log.debug("desactivarPagoAutomaticoEnPantallaIntermediaTarjetas() - Desactivando");
		activarPagoAutomaticoEnPantallaIntermediaTarjetas = false;
	}

	protected String obtenerTarjetaAuto(List<String> tarjetasDisponibles) {
		for (String tarjeta : tarjetasDisponibles) {
			if (esTarjetaPagoAutomatico(tarjeta)) {
				return tarjeta;
			}
		}
		return null;
	}

	protected Boolean esTarjetaPagoAutomatico(String tarjeta) {
		String codMedPag = prefijosTarjetasService.getMedioPagoPrefijo(tarjeta);
		
		if(codMedPag == null) {
			return false;
		}
		
		if (codMedPag.equals(CODMEDPAG_EMPLEADO) || codMedPag.equals(CODMEDPAG_VIP)) {
			return true;
		}
		else {
			return false;
		}
	}

	protected void introducirTarjetaAuto(String tarjeta) {
		numTarjetaLeido = tarjeta;
		log.debug("introducirTarjetaAuto() - Introduciendo tarjeta: " + numTarjetaLeido);
		HashMap<String, String> parametros = new HashMap<String, String>();
		String codMedPag = prefijosTarjetasService.getMedioPagoPrefijo(tarjeta);
		if (esTarjetaPagoAutomatico(tarjeta)) {
			parametros.put("codMedioPago", codMedPag);
			seleccionarMedioPago(parametros);
		}
	}
	
	@Override
	public void actionTfImporte(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			String valor = tfImporte.getText();

			if (StringUtils.isNotBlank(valor) && Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(valor)) {
				log.debug("actionTfImporte() - La tarjeta es de fidelización, debe introducirse en la pantalla de ventas.");
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha escaneado su QR de la App cliente Hiperdino. Para su registro, deberá volver a la pantalla de ventas. Recuerde que para volver a la pantalla de ventas, debe eliminar las formas de pago"), getStage());
				restaurarFocoTFImporte();
				return;
			}

			log.debug("actionTfImporte(() - Tecla INTRO pulsada en campo importe");
			actionBtAnotarPago();
		}
	}

	private void mostrarEncuestas() {
		if(!encuestasHechas) {
			List<Encuesta> encuestas = encuestasService.getEncuestasActivas();
			
			if(encuestas != null && !encuestas.isEmpty()) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(EncuestasController.PARAM_ENCUESTAS, encuestas);
				params.put(EncuestasController.PARAM_TICKET, ticketManager.getTicket());
				
				getApplication().getMainView().showModal(EncuestasView.class, params, getStage());
				
				List<EncuestaTicket> encuestasHechas = (List<EncuestaTicket>) params.get(EncuestasController.PARAM_RESPUESTAS);
				((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setEncuestasHechas(encuestasHechas);
			}
			
			encuestasHechas = true;
		}
	}
	
	public void aceptarConfirmacionPagoTarjeta() {
		vbConfirmacionPagoTarjeta.setVisible(false);
	}
	
	private void mostrarMensajesSorteo() {
		boolean hayParticipaciones = ((DinoTicketVentaAbono) ticketManager.getTicket()).getParticipaciones() != null;
		if (hayParticipaciones && Dispositivos.getInstance().getVisor() instanceof DinoVisorPantallaSecundaria) {
			((DinoVisorPantallaSecundaria) visor).modoParticipacionesAniversario((DinoTicketVentaAbono) ticketManager.getTicket());
		}
	}
	
	private void abrirSeleccionTarjetaPago(PaymentMethodManager paymentMethodManager) {
		PagoTicket pagoAnotar = null;
		List<PagoTicket> listaPagosTarjeta = new ArrayList<>();

		for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicketOrigen().getPagos()) {
			if (pago.getDatosRespuestaPagoTarjeta() != null) {
				listaPagosTarjeta.add(pago);
			}
		}

		getDatos().put(PARAM_PAGOS, listaPagosTarjeta);

		if (listaPagosTarjeta.size() > 1) {
			getApplication().getMainView().showModalCentered(SeleccionTarjetaPagoView.class, getDatos(), getStage());

			pagoAnotar = (PagoTicket) getDatos().get(SeleccionTarjetaPagoController.PARAMETRO_TARJETAS_SELECCIONADA);

			paymentMethodManager.addParameter(PAGO_SELECCIONADO, pagoAnotar.getDatosRespuestaPagoTarjeta());

		}
		else {
			pagoAnotar = listaPagosTarjeta.get(0);
			paymentMethodManager.addParameter(PAGO_SELECCIONADO, pagoAnotar.getDatosRespuestaPagoTarjeta());
		}

		BigDecimal importeAPagar = null;
		BigDecimal importeVentana = new BigDecimal(tfImporte.getText().replace(",", "."));

		int longitud = pagoAnotar.getDatosRespuestaPagoTarjeta().getImporte().length();
		String parte1 = pagoAnotar.getDatosRespuestaPagoTarjeta().getImporte().substring(0, longitud - 2);
		String parte2 = pagoAnotar.getDatosRespuestaPagoTarjeta().getImporte().substring(longitud - 2);
		String importeStringFormateado = parte1 + "." + parte2;

		BigDecimal importePago = new BigDecimal(importeStringFormateado);

		if (importeVentana.compareTo(importePago) == 1) {
			importeAPagar = importePago;
		}
		else {
			importeAPagar = importeVentana;
		}
		
		anotarPago(importeAPagar);
	}

	private void mostrarServicioRepartoSeleccionado() {
		ServicioRepartoDto servicioSeleccionado = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto();
		
		ivEnvioDomicilio.setManaged(true);

		if (servicioSeleccionado != null) {
			lbTitulo.setStyle("-fx-background-color: " + servicioSeleccionado.getColor());

			URL urlImagen = serviciosRepartoService.getUrlImage(servicioSeleccionado.getIcono());
			Image image = new Image(urlImagen.toString());
			
			if(!ivEnvioDomicilio.isVisible()) {
				ivEnvioDomicilio.setManaged(false);
			}
			
			ivServicioReparto.setImage(image);
		}
		else {
			lbTitulo.setStyle("-fx-background-color: #85b037");

			ivServicioReparto.setImage(null);
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
