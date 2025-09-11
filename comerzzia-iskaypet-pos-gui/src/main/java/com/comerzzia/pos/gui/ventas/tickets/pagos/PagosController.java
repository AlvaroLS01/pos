/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.gui.ventas.tickets.pagos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;
import com.comerzzia.core.servicios.ContextHolder;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.ITarjeta;
import com.comerzzia.pos.core.dispositivos.dispositivo.tarjeta.TarjetaException;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.importe.BotonBotoneraImagenValorComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.medioPago.BotonBotoneraTextoComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.LineaPanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.botonera.medioPago.ConfiguracionBotonMedioPagoBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.componentes.tecladonumerico.TecladoNumerico;
import com.comerzzia.pos.core.gui.componentes.textField.TextFieldImporte;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaController;
import com.comerzzia.pos.dispositivo.comun.tarjeta.CodigoTarjetaView;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager.SalvarTicketCallback;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.factura.FacturaView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos.VentanaCambioController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.cambioPagos.VentanaCambioView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.datosCliente.CambiarDatosClienteView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.datosEnvio.DatosEnvioView;
import com.comerzzia.pos.gui.ventas.tickets.pagos.vuelta.VueltaController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.vuelta.VueltaView;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.giftcard.GiftCardBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.promociones.tipos.PromocionTipoBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.mediospagos.MediosPagosService;
import com.comerzzia.pos.services.payments.PaymentException;
import com.comerzzia.pos.services.payments.PaymentsManager;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.events.PaymentErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentSelectEvent;
import com.comerzzia.pos.services.payments.events.PaymentsCompletedEvent;
import com.comerzzia.pos.services.payments.events.PaymentsErrorEvent;
import com.comerzzia.pos.services.payments.events.PaymentsInitEvent;
import com.comerzzia.pos.services.payments.events.PaymentsOkEvent;
import com.comerzzia.pos.services.payments.events.PaymentsSelectEvent;
import com.comerzzia.pos.services.payments.events.PaymentsStatusEvent;
import com.comerzzia.pos.services.payments.events.listeners.PaymentsCompletedListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentsErrorListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentsOkListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentsProcessListener;
import com.comerzzia.pos.services.payments.events.listeners.PaymentsSelectListener;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.BasicPaymentMethodManager;
import com.comerzzia.pos.services.payments.methods.types.GiftCardManager;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cupones.CuponEmitidoTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.pagos.ComparatorPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosRespuestaPagoTarjeta;
import com.comerzzia.pos.services.ticket.promociones.PromocionLineaTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
public class PagosController extends WindowController implements Initializable, IContenedorBotonera {
 
	protected Logger log = Logger.getLogger(getClass());

	public static final String ACCION_CANCELAR = "ACCION_CANCELAR";
	public static final String IMP_MAX_SUPERADO = "IMPORTE_MAXIMO_SUPERADO";
	public static final String PLANTILLA_VALE = "vale";
	public static final String PLANTILLA_CUPON = "cupon_promocion";
	public static final String TIPO_DOC_INICIAL = "TIPO_DOC_INICIAL";

	protected TicketManager ticketManager;

	@Autowired
	protected Sesion sesion;
	
	@Autowired
	protected Documentos documentos;
	
	final IVisor visor = Dispositivos.getInstance().getVisor();
	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;

	protected MedioPagoBean medioPagoSeleccionado;

	@FXML
	protected TextFieldImporte tfImporte;
	@FXML
	protected Label lbTotal, lbEntregado, lbPendiente, lbCambio, lbMedioPago, lbMedioPagoVuelta, lbDocActivo, lbSaldo, lbACuenta, lbTextAPagar, lbTextEntregado;
	@FXML
	protected Button btAnotarPago, btAceptar, btCancelar;
	@FXML
	protected TecladoNumerico tecladoNumerico;
	@FXML
	protected TableView<PagoTicketGui> tbPagos;
	@FXML
	protected TableColumn<PagoTicketGui, String> tcPagosFormaPago;
	@FXML
	protected TableColumn<PagoTicketGui, BigDecimal> tcPagosImporte;
	@FXML
	protected Tab panelPestanaPagoEfectivo, panelPestanaPagoContado;
	@FXML
	protected AnchorPane panelPagoEfectivo, panelPagoContado, panelPagoTarjeta, panelMenuTabla, panelNumberPad, panelBotoneraDatosAdicionales, panelMediosPago;
	@FXML
	protected TabPane panelPagos;
	@FXML
	protected Label lbTitulo;
	
	@FXML
	protected FlowPane panelEntregaCuenta;

	protected ITarjeta creditoMgr;
	
	// botonera inferior de pantalla
	protected BotoneraComponent botoneraDatosAdicionales;

	// lista de pagos
	protected ObservableList<PagoTicketGui> pagos;
	// botonera de medios de pago al contado
	protected BotoneraComponent botoneraMediosPagoContado;
	// botonera de medios de pago con tarjeta
	protected BotoneraComponent botoneraMediosPagoTarjeta;
	// botonera de importes
	protected BotoneraComponent botoneraImportes;
	// botonera de acciones de tabla
	protected BotoneraComponent botoneraAccionesTabla;
	//Formulario para validar el importe del pago
	protected FormularioImportePagosBean frImportePago;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private MediosPagosService mediosPagosService;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	
	protected PaymentsManager paymentsManager;
	
	protected BigDecimal importeMaxEfectivo;
	
	protected boolean enProceso;
	
	protected List<LineaTicket> lineasVentaAbono = new ArrayList<LineaTicket>();
	
	protected List<PagoTicket> copiaPagos = new ArrayList<PagoTicket>();
	
	protected BigDecimal totalAPagarConjunto;
	
	protected TipoDocumentoBean tipoDocumentoInicial;
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando pantalla...");

		// Mensaje sin contenido para tabla. los establecemos a vacio
		tbPagos.setPlaceholder(new Label(""));

		log.debug("initialize() - Comprobando configuración para panel numérico");
		if (!Variables.MODO_PAD_NUMERICO) {
			log.debug("ocultarPanelNumerico() - PAD Numérico off");
			panelNumberPad.setVisible(false);
		}
		//configuración de la tabla de pagos        
		log.debug("initialize() - Configurando pantalla de pagos");
		tcPagosFormaPago.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPagos", "tcPagosFormaPago", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcPagosImporte.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbPagos", "tcPagosImporte", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));

		tcPagosFormaPago.setCellValueFactory(new PropertyValueFactory<PagoTicketGui, String>("formaPago"));
		tcPagosImporte.setCellValueFactory(new PropertyValueFactory<PagoTicketGui, BigDecimal>("importe"));

		pagos = FXCollections.observableList(new ArrayList<PagoTicketGui>());
		tbPagos.setItems(pagos);
	}

	@Override
	public void initializeComponents() {
		log.debug("inicializarComponentes()");
		try {
			initTecladoNumerico(tecladoNumerico);
			log.debug("inicializarComponentes() - Cargando medios de pago");
			frImportePago = SpringContext.getBean(FormularioImportePagosBean.class);

			//Registramos el evento de navegacion entre pestañas en el panel TabPane
			registrarEventosNavegacionPestanha(panelPagos, this.getStage());

			List<MedioPagoBean> mediosPago = MediosPagosService.mediosPagoContado;
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<ConfiguracionBotonBean>();
			List<ConfiguracionBotonBean> listaAccionesMP = new LinkedList<ConfiguracionBotonBean>();
			List<ConfiguracionBotonBean> listaAccionesTarjeta = new LinkedList<>();

			
			log.debug("inicializarComponentes() - Registrando eventos de acceso rápido por teclado...");
			crearEventoEliminarTabla(tbPagos);
			
			log.debug("inicializarComponentes() - Configurando botonera");
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/row_delete.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));

			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelMenuTabla.getPrefWidth(), panelMenuTabla.getPrefHeight(), BotonBotoneraSimpleComponent.class.getName());
			panelMenuTabla.getChildren().add(botoneraAccionesTabla);

			PanelBotoneraBean botoneraMediosPagos = null;
			try {
				botoneraMediosPagos = getView().loadBotonera("_mediospago_panel.xml");
			} 
			catch (InitializeGuiException ex) {
				log.info("inicializarComponentes() - No cargando botonera personalizada de mediospago \"xxx_mediospago_panel.xml\": " + ex.getMessage());
	        }
			
			if(botoneraMediosPagos == null) {
				List<String> codMediosPagosUtilizados = new ArrayList<String>();
				
				try{
					log.debug("inicializarComponentes() - Cargando panel de importes");
					PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
					
					for(LineaPanelBotoneraBean lineaPanel : panelBotoneraBean.getLineasBotones()) {
						for(ConfiguracionBotonBean boton : lineaPanel.getLineaBotones()) {
							if(boton.getParametros() != null && boton.getParametros().containsKey("codMedioPago")) {
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
				for (MedioPagoBean pag : mediosPago) {
					crearBotonMedioPago(listaAccionesMP, pag, codMediosPagosUtilizados);
				}
				botoneraMediosPagoContado = new BotoneraComponent(3, 4, this, listaAccionesMP, null, panelPagoContado.getPrefHeight(), BotonBotoneraTextoComponent.class.getName());
				panelPagoContado.getChildren().add(botoneraMediosPagoContado);
	
				for(MedioPagoBean mpTarjeta: MediosPagosService.mediosPagoTarjetas){
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
		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error creando botonera para medio de pago. error : " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla pagos."), getStage());
		}
	}

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
					}
				};
			}
		};
		tbPagos.setRowFactory(callback);
	}

	protected void registerPaymentsListeners() {
		log.debug("registrarListenersPago() - Registrando listeners de la pantalla de pago.");
		
		paymentsManager.addListenerOk(new PaymentsOkListener(){
			@Override
			public void paymentsOk(PaymentsOkEvent event) {
				processPaymentOk(event);
			}
		});
		
		paymentsManager.addListenerError(new PaymentsErrorListener(){
			@Override
			public void paymentsError(PaymentsErrorEvent event) {
				processPaymentError(event);
			}
		});
		
		paymentsManager.addListenerProcess(new PaymentsProcessListener(){
			@Override
			public void paymentStatus(PaymentsStatusEvent event) {
				processPaymentStatusChange(event);
			}
			
			@Override
			public void paymentInitProcess(PaymentsInitEvent event) {
				processPaymentInitProcess(event);
			}
		});
		
		paymentsManager.addListenerPaymentCompleted(new PaymentsCompletedListener(){
			@Override
			public void paymentsCompleted(PaymentsCompletedEvent event) {
				finishSale(event);
			}
		});
		
		paymentsManager.addListenerPaymentSelect(new PaymentsSelectListener(){
			@Override
			public void paymentsSelect(PaymentsSelectEvent event) {
				selectPayment(event);				
			}
		});
	}

	protected void selectPayment(PaymentsSelectEvent event) {
		PaymentMethodManager source = (PaymentMethodManager) event.getEventSelect().getSource();
		
		MedioPagoBean medioPago = mediosPagosService.getMedioPago(source.getPaymentCode());
		medioPagoSeleccionado = medioPago;
		lbSaldo.setText("");
		lbMedioPago.setText(medioPago.getDesMedioPago());
		
		if(source instanceof GiftCardManager) {
			askGiftCardNumber(source);
		}
		else {
			selectCustomPaymentMethod(event.getEventSelect());
		}
	}

	protected void selectCustomPaymentMethod(PaymentSelectEvent paymentSelectEvent) {	
	}

	protected void processPaymentOk(PaymentsOkEvent event) {
		PaymentOkEvent eventOk = event.getOkEvent();
		
		if(!eventOk.isCanceled()) {
			addPayment(eventOk);
		}
		else {
			deletePayment(eventOk);
		}
		
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
				refrescarDatosPantalla();
				selectDefaultPaymentMethod();
            }
		});
	}

	protected void selectDefaultPaymentMethod() {
		tbPagos.getSelectionModel().selectLast();
		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
		tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
		lbSaldo.setText("");
		tfImporte.requestFocus();
	}

	protected void addPayment(PaymentOkEvent eventOk) {
		BigDecimal amount = eventOk.getAmount();
		String paymentCode = ((PaymentMethodManager) eventOk.getSource()).getPaymentCode();
		Integer paymentId = eventOk.getPaymentId();
		boolean removable = eventOk.isRemovable();
		
		MedioPagoBean paymentMethod = mediosPagosService.getMedioPago(paymentCode);
		
		boolean cashFlowRecorded = ((PaymentMethodManager) eventOk.getSource()).recordCashFlowImmediately();
		
		PagoTicket payment = ticketManager.nuevaLineaPago(paymentCode, amount, true, removable, paymentId, true);
		payment.setMovimientoCajaInsertado(cashFlowRecorded);
		
		if(ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)<0){
		       amount = amount.negate();
		}
		
		if(eventOk.getSource() instanceof GiftCardManager) {
			GiftCardBean giftCard = (GiftCardBean) eventOk.getExtendedData().get(GiftCardManager.PARAM_TARJETA);
			payment.addGiftcardBean(amount, giftCard);
			
			GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(giftCard);
			asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
		}
		else {
			addCustomPaymentData(eventOk, payment);
		}
		
		if(paymentMethod.getTarjetaCredito() != null && paymentMethod.getTarjetaCredito()) {
			if(eventOk.getExtendedData().containsKey(BasicPaymentMethodManager.PARAM_RESPONSE_TEF)) {
				DatosRespuestaPagoTarjeta datosRespuestaPagoTarjeta = (DatosRespuestaPagoTarjeta) eventOk.getExtendedData().get(BasicPaymentMethodManager.PARAM_RESPONSE_TEF);
				payment.setDatosRespuestaPagoTarjeta(datosRespuestaPagoTarjeta);
				for(String key : eventOk.getExtendedData().keySet()) {
					payment.addExtendedData(key, eventOk.getExtendedData().get(key));
				}
			}
		}
		
		ticketManager.guardarCopiaSeguridadTicket();
	}

	protected void addCustomPaymentData(PaymentOkEvent eventOk, PagoTicket payment) {
    }

	protected void deletePayment(PaymentOkEvent eventOk) {
		Integer paymentId = eventOk.getPaymentId();
		
		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
		while(it.hasNext()) {
			PagoTicketGui gui = it.next();
			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
				if(ticketManager.deletePayment(paymentId)) {
					it.remove();
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
					return;
				}		
			}
		}
		
		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
		ticketManager.setEsOperacionTarjetaRegalo(false);
		
		deleteCustomPaymentData(eventOk, paymentId);
		
		ticketManager.guardarCopiaSeguridadTicket();
		
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
				refrescarDatosPantalla();
            }
		});
	}

	protected void deletePayment(PaymentErrorEvent eventError) {
		Integer paymentId = eventError.getPaymentId();
		
		Iterator<PagoTicketGui> it = tbPagos.getItems().iterator();
		while(it.hasNext()) {
			PagoTicketGui gui = it.next();
			if(gui.getPaymentId() != null && gui.getPaymentId().equals(paymentId)) {
				if(ticketManager.deletePayment(paymentId)) {
					it.remove();
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido borrar el pago, contacte con el administrador."), getStage());
					return;
				}		
			}
		}
		
		ticketManager.getTicket().getCabecera().setTarjetaRegalo(null);
		ticketManager.setEsOperacionTarjetaRegalo(false);
		
		deleteCustomPaymentData(eventError, paymentId);
		
		ticketManager.guardarCopiaSeguridadTicket();
		
		Platform.runLater(new Runnable() {
			@Override
            public void run() {
				refrescarDatosPantalla();
            }
		});
	}

	protected void deleteCustomPaymentData(PaymentOkEvent eventOk, Integer paymentId) {
    }

	protected void deleteCustomPaymentData(PaymentErrorEvent eventError, Integer paymentId) {
    }

	protected void processPaymentError(PaymentsErrorEvent event) {
		PaymentErrorEvent errorEvent = event.getErrorEvent();
		
		if(errorEvent.getException() != null) {
			log.error("PaymentErrorEvent :: Ha habido un error al registrar el pago: " + errorEvent.getException().getMessage(), errorEvent.getException());
			VentanaDialogoComponent.crearVentanaError(getStage(), errorEvent.getException().getMessage(), errorEvent.getException());
		}
		else {
			log.error("PaymentErrorEvent :: Ha habido un error al registrar el pago: " + errorEvent.getErrorMessage());
			VentanaDialogoComponent.crearVentanaError(errorEvent.getErrorMessage(), getStage());
		}
		
		if(event.getSource() instanceof PaymentMethodManager) {			
			PaymentMethodManager paymentMethodManager = (PaymentMethodManager) event.getSource();
			if(!isDevolucion()) {
				log.info("processPaymentError() - realizando cancelación de pago");
				cancelPay(paymentMethodManager, errorEvent.getPaymentId());
			}
			else {
				log.info("processPaymentError() - realizando cancelación de devolución");
				cancelReturn(paymentMethodManager, errorEvent.getPaymentId());
			}
			visor.modoPago(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
			ticketManager.guardarCopiaSeguridadTicket();
		}
		
		if(errorEvent.isRemovable()) {
			deletePayment(errorEvent);
		}
	}

	protected void processPaymentStatusChange(PaymentsStatusEvent event) {
		log.debug("PaymentStatusEvent :: Ha habido un cambio de estado durante el pago: " + event.getStatusEvent().getCode());
	}

	protected void processPaymentInitProcess(PaymentsInitEvent event) {
		log.debug("PaymentInitEvent :: Se ha iniciado el pago.");
	}

	protected void askGiftCardNumber(PaymentMethodManager source) {
		try {
			HashMap<String, Object> parametros= new HashMap<>();     
	    	parametros.put(CodigoTarjetaController.PARAMETRO_IN_TEXTOCABECERA, I18N.getTexto("Lea o escriba el código de barras de la tarjeta de regalo"));
	    	parametros.put(CodigoTarjetaController.PARAMETRO_TIPO_TARJETA, "GIFTCARD");
	    	
	    	POSApplication posApplication = POSApplication.getInstance();
	    	posApplication.getMainView().showModalCentered(CodigoTarjetaView.class, parametros, getStage());       
	    	
	        String numTarjeta = (String)parametros.get(CodigoTarjetaController.PARAMETRO_NUM_TARJETA);
	        
	        if(StringUtils.isNotBlank(numTarjeta)) {
		        String apiKey = variablesServices.getVariableAsString(com.comerzzia.core.servicios.variables.Variables.WEBSERVICES_APIKEY);
		        String uidActividad = sesion.getAplicacion().getUidActividad();
				ConsultarFidelizadoRequestRest paramConsulta = new ConsultarFidelizadoRequestRest(apiKey, uidActividad);
		        paramConsulta.setNumeroTarjeta(numTarjeta);
		        
		        ResponseGetFidelizadoRest result = FidelizadosRest.getTarjetaRegalo(paramConsulta);
				
				GiftCardBean tarjetaRegalo = SpringContext.getBean(GiftCardBean.class);
				tarjetaRegalo.setNumTarjetaRegalo(result.getNumeroTarjeta());
		        tarjetaRegalo.setBaja(result.getBaja().equals("S"));
		        tarjetaRegalo.setActiva(result.getActiva().equals("S"));
		        tarjetaRegalo.setSaldoProvisional(BigDecimal.ZERO);
		        tarjetaRegalo.setSaldo(BigDecimal.valueOf(result.getSaldo()));
		        tarjetaRegalo.setSaldoProvisional(BigDecimal.valueOf(result.getSaldoProvisional()));
		        tarjetaRegalo.setCodTipoTarjeta(result.getTipoTarjeta() != null ? result.getTipoTarjeta().getCodtipotarj() : null);
		        
		        if(tarjetaRegalo!=null){
					if(tarjetaRegalo.isBaja()){
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La tarjeta introducida está dada de baja."), getStage());
					}
					else{
						MedioPagoBean medioPago = mediosPagosService.getMedioPago(source.getPaymentCode());
						//TODO
//						boolean esMedioPagoCorrectoTipoTarj = Dispositivos.getInstance().getGiftCard().esMedioPago(tarjetaRegalo.getCodTipoTarjeta(), medioPago);
//							if(!esMedioPagoCorrectoTipoTarj) {
//							VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La tarjeta introducida no es del tipo permitido para este medio de pago."), getStage());
//							return;
//						}
						
						lbSaldo.setText(I18N.getTexto("Saldo")+": ("+FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal())+")");
						lbMedioPago.setText(medioPago.getDesMedioPago());
						medioPagoSeleccionado = medioPago;
	
						GiftCardBean tarjetaRegaloPago = obtenerPagoTarjetaRegalo(tarjetaRegalo);
						
						if(tarjetaRegaloPago != null) {
							asociarPagoTarjetaRegalo(ticketManager.getTicket().getCabecera().esVenta(), tarjetaRegaloPago);
						}
						else {
							lbSaldo.setText(I18N.getTexto("Saldo")+": ("+FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal())+")");
							if(ticketManager.getTicket().getCabecera().esVenta()){
								if(BigDecimalUtil.isMayor(ticketManager.getTicket().getTotales().getPendiente(),tarjetaRegalo.getSaldoTotal())){
									tfImporte.setText(FormatUtil.getInstance().formateaImporte(tarjetaRegalo.getSaldoTotal()));	
								}
							}
						}
					}
				}
		        
		        source.addParameter(GiftCardManager.PARAM_TARJETA, tarjetaRegalo);
	        }
	        else {
	        	selectDefaultPaymentMethod();
	        }
		}
		
		catch(Exception e) {
			log.error("askGiftCardNumber() - Ha habido un error al pedir el número de tarjeta: " + e.getMessage(), e);
			
			if (e instanceof RestHttpException) {
				String message = I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición.") + System.lineSeparator() + System.lineSeparator() + e.getMessage();
				VentanaDialogoComponent.crearVentanaError(getStage(), message, e);
			}
			else if (e instanceof RestConnectException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido conectar con el servidor"), e);
			}
			else if (e instanceof RestTimeoutException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("El servidor ha tardado demasiado tiempo en responder"), e);
			}
			else if (e instanceof RestException) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error en la petición"), e);
			}
			
			selectDefaultPaymentMethod();
		}
	}

	protected void enablePaymentsMethods() {
		comprobarConfiguracionBotoneraPago(panelPagoEfectivo);
		comprobarConfiguracionBotoneraPago(panelPagoTarjeta);
		comprobarConfiguracionBotoneraPago(panelPagoContado);
	}
	
	protected void comprobarConfiguracionBotoneraPago(AnchorPane panel) {
		ObservableList<Node> children = panel.getChildren();
		if(children != null && !children.isEmpty()) {
			for (BotonBotoneraComponent boton : ((BotoneraComponent) children.get(0)).getMapConfiguracionesBotones().values()) {
				String codMedioPago = MediosPagosService.medioPagoDefecto.getCodMedioPago();
				if(!panel.equals(panelPagoEfectivo)) {
					codMedioPago = ((BotonBotoneraTextoComponent) boton).getMedioPago().getCodMedioPago();
				}
				
				if(isDevolucion() && !paymentsManager.isPaymentMethodAvailableForReturn(codMedioPago)) {
					boton.setDisable(true);
				}
				else if (!isDevolucion() && !paymentsManager.isPaymentMethodAvailable(codMedioPago)) {
					boton.setDisable(true);
				}
				else {
					boton.setDisable(false);
				}
			}
		}
	}

	protected void crearBotonMedioPago(List<ConfiguracionBotonBean> listaAccionesMP, MedioPagoBean pag, List<String> codMediosPagosUtilizados) {
		if(!codMediosPagosUtilizados.contains(pag.getCodMedioPago())) {
			ConfiguracionBotonMedioPagoBean cfg = new ConfiguracionBotonMedioPagoBean(null, pag.getDesMedioPago(), null, "ACCION_SELECIONAR_MEDIO_PAGO", "", pag);
			listaAccionesMP.add(cfg);
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		enProceso = false;
		ticketManager = (TicketManager) getDatos().get(FacturacionArticulosController.TICKET_KEY);

		cargarBotoneraDatosAdicionales();
		
		lbDocActivo.setText(ticketManager.getDocumentoActivo().getDestipodocumento());
		if(!ticketManager.getDocumentoActivo().getRequiereCompletarPagos()){
			lbTitulo.setText(I18N.getTexto("Entrega a cuenta"));
		}else{
			lbTitulo.setText(I18N.getTexto("Pagos"));
		}
		
		panelPagos.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (t.booleanValue() == false && t1.booleanValue() == true){
					medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
					lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
					lbSaldo.setText("");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							tfImporte.requestFocus();
						}
					});  
				}
			}
		});
		
		if(ticketManager.isEsFacturacionVentaCredito()){
			panelEntregaCuenta.setVisible(true);
			panelEntregaCuenta.setManaged(true);
		}
		else{
			panelEntregaCuenta.setVisible(false);
			panelEntregaCuenta.setManaged(false);
		}

		// Establecemos el medio de pago por defecto
		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());

		panelPagos.getSelectionModel().select(panelPestanaPagoEfectivo);
		lbSaldo.setText("");

		introducirDescuentosPromocionales();
		
		visor.modoPago(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
		escribirVisor();
		
		initializePaymentsManager();
		
		lineasVentaAbono.clear();
		for (LineaTicket linea : (List<LineaTicket>) ((TicketVentaAbono) ticketManager.getTicket()).getLineas()) {
			lineasVentaAbono.add(linea.clone());
		}	
		
		tipoDocumentoInicial = (TipoDocumentoBean) getDatos().get(TIPO_DOC_INICIAL);
		
		ticketManager.cambiarMedioPagoVuelta(MediosPagosService.medioPagoDefecto);
		
		refrescarDatosPantalla();
		
		tbPagos.getSelectionModel().selectFirst();		
		
		if(!ticketManager.comprobarCierreCajaDiarioObligatorio()){
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
			throw new InitializeGuiException(false);
		}
		
		if(!ticketManager.comprobarConfigContador()){
			ticketManager.crearVentanaErrorContador(getStage());
			throw new InitializeGuiException(false);
		}
		else if(ticketManager.getContador()!=null && ticketManager.getContador().getConfigContadorRango()!=null){
			ticketManager.getTicket().getCabecera().setConfigContadorRango(ticketManager.getContador().getConfigContadorRango());
		}
		
		
	}

	protected void introducirDescuentosPromocionales() {
		ticketManager.getTicket().getPagos().clear();
		ticketManager.getTicket().getCabecera().getTotales().recalcular();
		Map<String, BigDecimal> descuentosPromocionales = new HashMap<String, BigDecimal>();
		for(PromocionTicket promocion : (List<PromocionTicket>) ticketManager.getTicket().getPromociones()) {
			if(promocion.isDescuentoMenosIngreso()) {
				PromocionTipoBean tipoPromocion = sesion.getSesionPromociones().getPromocionActiva(promocion.getIdPromocion()).getPromocionBean().getTipoPromocion();
				String codMedioPago = tipoPromocion.getCodMedioPagoMenosIngreso();
				if(codMedioPago != null) {
					BigDecimal importeDescPromocional = BigDecimalUtil.redondear(promocion.getImporteTotalAhorro(), 2);
					BigDecimal importeDescAcum = descuentosPromocionales.get(codMedioPago) != null ? descuentosPromocionales.get(codMedioPago) : BigDecimal.ZERO;
					importeDescAcum = importeDescAcum.add(importeDescPromocional);
					descuentosPromocionales.put(codMedioPago, importeDescAcum);
				}
				else {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El medio de pago asociado al tipo de promoción {0} no está configurado.", tipoPromocion.getDesTipoPromocion()), getStage());
				}
			}
		}
		
		for(String codMedioPago : descuentosPromocionales.keySet()) {
			BigDecimal importe = descuentosPromocionales.get(codMedioPago);
			if(BigDecimalUtil.isMayorACero(importe)){
				ticketManager.nuevaLineaPago(codMedioPago, importe, true, false, null, false);
			}
		}
	}

	protected void initializePaymentsManager() {
		paymentsManager = ContextHolder.get().getBean(PaymentsManager.class);
		paymentsManager.setPaymentsMethodsConfiguration(paymentsMethodsConfiguration);
		paymentsManager.setTicketData(ticketManager.getTicket(), ticketManager.getTicketOrigen());
		
		registerPaymentsListeners();
		
		enablePaymentsMethods();
	}

	protected boolean existePromocionPuntos() {
		boolean res = false;
		if(ticketManager.getTicketOrigen()!=null){
			for(PromocionTicket promo : (List<PromocionTicket>) ticketManager.getTicketOrigen().getPromociones()) {
				if(promo.getPuntos() > 0) {
					res = true;
					break;
				}
			}			
		}
	    return res;
	}

	protected Integer getSalidaDevolucion() {
		Integer result = 0;
		for(LineaTicketAbstract linea : (List<LineaTicketAbstract>) ticketManager.getTicket().getLineas()){
			if(ticketManager.getTicketOrigen() != null){
				Integer puntosConcedidos = getPuntosConcedidosLinea(linea.getCodArticulo(), linea.getLineaDocumentoOrigen(), linea.getCantidad());
				result += puntosConcedidos;
			}
		}
		return result;
	}

	protected Integer getPuntosConcedidosLinea(String codArticulo,
			Integer lineaOrigen, BigDecimal cantidadLineaDevolucion) {
		Integer result = 0;
	    List<LineaTicketAbstract> lineasOriginales = ticketManager.getTicketOrigen().getLinea(codArticulo);
	    for(LineaTicketAbstract lineaOriginal : lineasOriginales){
	    	if(lineaOriginal.getIdLinea().equals(lineaOrigen) &&
	    			lineaOriginal.getCantidadDevuelta().compareTo(lineaOriginal.getCantidad()) <= 0
	    			){
	    		Integer puntosLinea = 0;
				for(PromocionLineaTicket promo : lineaOriginal.getPromociones()){
			    	if(promo.getPuntos()>0){
			    		puntosLinea +=promo.getPuntos();
			    	}
			    }
				BigDecimal factor = cantidadLineaDevolucion.divide(lineaOriginal.getCantidad(),4,RoundingMode.HALF_UP).abs();
	    		result += BigDecimal.valueOf(puntosLinea).multiply(factor).intValue();
				break;
	    	}
	    }
	    return result;
	}

	@Override
	public void initializeFocus() {
		restaurarFocoTFImporte();
	}

	/**
	 * Establece un determiando orden en la secuencia de navegación mediante. si
	 * no se establece, se toma la navegación por defecto de la pantalla.
	 * tabulador
	 */
	protected void inicializarFocos() { 
		establecerControlFocosDefecto();
	}

	@FXML
	public void establecerControlFocosDefecto() {
		tfImporte.requestFocus();
	}

	// </editor-fold>
	// <editor-fold desc="Funciones relacionadas con interfaz GUI y manejo de pantalla"> 

	/**
	 * Función que refresca los totales en pantalla
	 */
	public void refrescarDatosPantalla() {
		log.debug("refrescarDatosPantalla()");
		
		pagos.clear();
		
		int numPagosEliminables = 0;
		
		Collections.sort(ticketManager.getTicket().getPagos(), new ComparatorPagoTicket());
		for (PagoTicket pagoTicket : (List<PagoTicket>) ((TicketVenta)ticketManager.getTicket()).getPagos()) {
			PagoTicketGui pagoTicketGui = new PagoTicketGui(pagoTicket);
			pagos.add(pagoTicketGui);
			
			if(pagoTicket.isEliminable()) {
				numPagosEliminables++;
			}
		}
		tfImporte.clear();
		lbTotal.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getTotalAPagar()));
		lbACuenta.setText(FormatUtil.getInstance().formateaNumero(ticketManager.getTicket().getCabecera().getTotales().getEntregadoACuenta(), 2));
		lbEntregado.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getEntregado()));
		BigDecimal pendiente = ticketManager.getTicket().getTotales().getPendiente();
		if(ticketManager.getDocumentoActivo().getCodtipodocumento().equals(Documentos.VENTA_CREDITO)){
			lbTextAPagar.setText(I18N.getTexto("TOTAL"));
			lbTextEntregado.setText(I18N.getTexto("ENTREGA A CUENTA"));
		}
		else{
			lbTextAPagar.setText(I18N.getTexto("TOTAL"));
			if(isDevolucion()){
				lbTextEntregado.setText(I18N.getTexto("DEVUELTO"));
			}
			else{
				lbTextEntregado.setText(I18N.getTexto("ENTREGADO"));
			}
		}

		lbCambio.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getCambio().getImporte()));
		if(BigDecimalUtil.isMenorACero(ticketManager.getTicket().getTotales().getTotalAPagar())) {
			lbPendiente.setText(FormatUtil.getInstance().formateaImporte(pendiente.negate()));			
		}else {			
			lbPendiente.setText(FormatUtil.getInstance().formateaImporte(pendiente));
		}
		applyTotalLabelStyle(lbPendiente);
		
		lbMedioPagoVuelta.setText(ticketManager.getTicket().getCabecera().getTotales().getCambio().getDesMedioPago());
		
		restaurarFocoTFImporte();

		log.debug("refrescarDatosPantalla() - Activando y desactivando botones de aceptar y cancelar.");
		if(BigDecimalUtil.isMayorACero(pendiente.abs()) && ticketManager.getDocumentoActivo().getRequiereCompletarPagos()) {
			btAceptar.setDisable(true);
		}
		else {
			btAceptar.setDisable(false);
		}
		
		if(numPagosEliminables > 0) {
			btCancelar.setDisable(true);
		}
		else {
			btCancelar.setDisable(false);
		}
		
		desactivarBotonFactura();
	}
	
	protected void applyTotalLabelStyle(Label label) {
		try {
			String text = label.getText();
			
			label.getStyleClass().clear();
			label.getStyleClass().add("label");
			label.getStyleClass().add("total");
			
			if(text.length()>=15) {
				label.getStyleClass().add("total-reduced-30");
			} else if(text.length()>=13) {
				label.getStyleClass().add("total-reduced-35");
			} else if(text.length()>=11) {
				label.getStyleClass().add("total-reduced-40");
			} else {
				label.getStyleClass().add("total-reduced");
			}
		} catch (Exception e) {
			log.debug("applyTotalLabelStyle() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
	}

	protected void desactivarBotonFactura() {
		if(botoneraDatosAdicionales!= null) {			
			for(Button boton : botoneraDatosAdicionales.getListaBotones()) {
				ConfiguracionBotonBean configuracion = (ConfiguracionBotonBean) boton.getUserData();
				if(configuracion.getClave().equals("accionFactura")) {
					TicketVentaAbono ticket = (TicketVentaAbono) ticketManager.getTicket();
					if(ticket.getCabecera().getIdTicket() != null && ticket.getDatosFacturacion() == null) {
						boton.setDisable(true);
					}
					else {
						boton.setDisable(false);
					}
				}
			}
		}
	}
	
	protected void cargarBotoneraDatosAdicionales(){
		
		panelBotoneraDatosAdicionales.getChildren().clear();
		PanelBotoneraBean panelBotoneraBean = null;
		String tipoDocumento = ticketManager.getDocumentoActivo().getCodtipodocumento();
		String tipoDocumentoReferencia = ticketManager.getDocumentoActivo().getTipoDocumentoEstandar();
		
		// Si tenemos datos de documento origen estamos en una devolución controlada y por tanto cargamos
		// la botonera de nota de crédito independientemente del tipo de documento para que 
		// no salgan los botones de datos adicionales salvo personalización
		if(ticketManager.getTicketOrigen() != null) {
			tipoDocumento = Documentos.NOTA_CREDITO;
		}
		else{
			if(tipoDocumento.equals(Documentos.FACTURA_COMPLETA) || (tipoDocumentoReferencia != null && tipoDocumentoReferencia.equals(Documentos.FACTURA_COMPLETA)) ) {
				tipoDocumento = Documentos.FACTURA_SIMPLIFICADA;
				String fsEstandar = sesion.getAplicacion().getDocumentos().getCodTipoDocumentoEstandar(tipoDocumento);
				if(fsEstandar!=null){					
					tipoDocumentoReferencia = sesion.getAplicacion().getDocumentos().getCodTipoDocumentoEstandar(tipoDocumento);
				}
			}
		}
		if (botoneraDatosAdicionales != null){
			botoneraDatosAdicionales.eliminaComponentes();
		}
		
		boolean botoneraCreada = crearBotoneraDatosAdicionales(panelBotoneraBean, tipoDocumento);
		if(!botoneraCreada && StringUtils.isNotBlank(tipoDocumentoReferencia)) {
			crearBotoneraDatosAdicionales(panelBotoneraBean, tipoDocumentoReferencia);
		}
	}

	protected boolean crearBotoneraDatosAdicionales(PanelBotoneraBean panelBotoneraBean, String tipoDocumento) {
		boolean resultado = true;
		
		try{
			log.debug("crearBotoneraDatosAdicionales() - Cargando panel de datos adicionales");
			panelBotoneraBean = getView().loadBotonera("_adic_"+ tipoDocumento + "_"+ticketManager.getDocumentoActivo().getCodpais()+".xml");
			botoneraDatosAdicionales = new BotoneraComponent(panelBotoneraBean, null, panelBotoneraDatosAdicionales.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
			panelBotoneraDatosAdicionales.getChildren().add(botoneraDatosAdicionales);
		} catch (InitializeGuiException e) {
			log.debug("crearBotoneraDatosAdicionales() - No existe botonera por tipo documento y país: " + e.getMessage());
			panelBotoneraDatosAdicionales.getChildren().clear();
			
			resultado = false;
		} catch (CargarPantallaException e) {
			log.debug("crearBotoneraDatosAdicionales() - Error cargando botonera asociada al tipo de documento", e);
			panelBotoneraDatosAdicionales.getChildren().clear();
			
			resultado = false;
		}
		
		try{
			if(panelBotoneraBean == null){
				panelBotoneraBean = getView().loadBotonera("_adic_"+ tipoDocumento +".xml");
				botoneraDatosAdicionales = new BotoneraComponent(panelBotoneraBean, null, panelBotoneraDatosAdicionales.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
				panelBotoneraDatosAdicionales.getChildren().add(botoneraDatosAdicionales);
			}
		} catch (InitializeGuiException e) {
			log.debug("crearBotoneraDatosAdicionales() - Error al crear botonera: " + e.getMessage());
			panelBotoneraDatosAdicionales.getChildren().clear();
			
			resultado = false;
		} catch (CargarPantallaException e) {
			log.debug("crearBotoneraDatosAdicionales() - Error cargando botonera asociada al tipo de documento", e);
			panelBotoneraDatosAdicionales.getChildren().clear();
			
			resultado = false;
		}
		
		return resultado;
	}

	/**
	 * Accion anotarPagos. Crea linea de pago con el importe indicado y el
	 * medio de pago seleccionado en pantalla
	 *
	 * @param importe importe a anotar
	 */
	public void anotarPago(BigDecimal importe) {
		log.debug("anotarPago() - Anotando pago: medio de pago:" + medioPagoSeleccionado + " // Importe: " + importe);
		
		if (medioPagoSeleccionado == null) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay ninguna forma de pago seleccionada."), getStage());
			return;
		}
		
		if(importe.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		
		if(!ticketManager.comprobarImporteMaximoOperacion(getStage())) {
			return;
		}
		
		if((MediosPagosService.mediosPagoTarjetas.contains(medioPagoSeleccionado)) && BigDecimalUtil.isMayor(importe.abs(), ticketManager.getTicket().getTotales().getPendiente().abs())){
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El medio de pago seleccionado no permite introducir un importe superior al total del documento"), getStage());
			tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
			return;
		}
		
		incluirPagoTicket(importe);
		
		visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
		escribirVisor();
		
		tfImporte.requestFocus();
	}
	
	/**
	 * Se calcula el saldo pendiente de la tarjeta en funcion de la tarjeta seleccionada
	 * 
	 * @param venta
	 * @param tarjtaRegaloPago
	 * @return
	 */
	public BigDecimal calcularSaldoPendienteTarjetaRegalo(boolean venta, GiftCardBean tarjetaRegalo) {
		BigDecimal saldo = null;
		GiftCardBean tarjetaRegaloExistente = obtenerPagoTarjetaRegalo(tarjetaRegalo);
		if (tarjetaRegaloExistente != null) {
			if (venta) {
				saldo = tarjetaRegalo.getSaldoTotal().subtract(tarjetaRegaloExistente.getImportePago());
			}
			else {
				saldo = tarjetaRegalo.getSaldoTotal().add(tarjetaRegaloExistente.getImportePago());
			}
			return saldo;
		}
		else {
			return tarjetaRegalo.getSaldoTotal();
		}
	}
	
    protected GiftCardBean obtenerPagoTarjetaRegalo(GiftCardBean tarjetaRegalo) {
		if (tarjetaRegalo != null) {
			for (PagoTicket pago : (List<PagoTicket>) ticketManager.getTicket().getPagos()) {
				if(pago.getGiftcards() != null) {
					for (GiftCardBean tarjPago : pago.getGiftcards()) {
						if (tarjetaRegalo.getNumTarjetaRegalo().equals(tarjPago.getNumTarjetaRegalo())) {
							return tarjPago;
						}
					}
					break;
				}
			}
		}
		return null;
	}

	protected void escribirVisor() {
		String cad1 = I18N.getTexto("TOTAL:     ");
		if (BigDecimalUtil.isMenorACero(ticketManager.getTicket().getTotales().getTotal())) {
			//Si el total es negativo, quitamos un espacio a la cadena anterior
			cad1 = cad1.substring(0, cad1.length()-1);
		}
		visor.escribir(cad1 + ticketManager.getTicket().getTotales().getTotalAsString(),
					   I18N.getTexto("PENDIENTE: ") + ticketManager.getTicket().getTotales().getPendienteAsString());
	}

    protected void incluirPagoTicket(BigDecimal importe) {
		BigDecimal importePagosTicket = importe;
		for(PagoTicket pago : (List<PagoTicket>) ticketManager.getTicket().getPagos()){
			if(pago.getCodMedioPago().equals(medioPagoSeleccionado.getCodMedioPago())){
				importePagosTicket = importePagosTicket.add(pago.getImporte());
			}
		}
		if(importePagosTicket.precision()-importePagosTicket.scale() > 10){
			String max = FormatUtil.getInstance().formateaNumero(new BigDecimal(10000000000L));
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El valor del importe total debe ser inferior a ")+max, getStage());
			return;
		}
		
		PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(medioPagoSeleccionado.getCodMedioPago());
		if(ticketManager.getTicket().getCabecera().esVenta()){
			pay(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
		}
		else {
			returnAmount(paymentMethodManager, medioPagoSeleccionado.getCodMedioPago(), importe);
		}
	}

	protected boolean isDevolucion() {
		return ticketManager.getTicket().getTotales().getTotalAPagar().compareTo(BigDecimal.ZERO)<0 || ticketManager.isEsDevolucion();
	}

	protected void finishSale(final PaymentsCompletedEvent event) {
		btAceptar.setDisable(false);
		btCancelar.setDisable(true);
	}
	
	protected void accionSalvarTicketSucceeded(boolean repiteOperacion) {
		
		imprimir();
		
		//Mostramos la ventana con la información de importe pagado, cambio...
		if(!BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())){
			mostrarVentanaCambio();
		}
		
		if(repiteOperacion){
			enProceso = false;
			initTicketManager(false);
			aceptarPagos(false);
		}else{
			ticketManager.notificarContadores();
			ticketManager.finalizarTicket();
		
			getStage().close();
			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();
		}

    }

	protected void imprimir() {
		try {
            while(true) {
				String formatoImpresion = ticketManager.getTicket().getCabecera().getFormatoImpresion();

				if (formatoImpresion.equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
					log.info("imprimir() - Formato de impresión no configurado, no se imprimirá.");
					return;
				}

				Map<String,Object> mapaParametros= new HashMap<String,Object>();
				mapaParametros.put("ticket",ticketManager.getTicket());
				mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
				FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				mapaParametros.put("paperLess", datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
				if(ticketManager.getTicket().getCabecera().getCodTipoDocumento().equals(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getCodtipodocumento())) {
					mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
				}

				ServicioImpresion.imprimir(formatoImpresion, mapaParametros);

                boolean hayPagosTarjeta = false;
                for(Object pago : ticketManager.getTicket().getPagos()) {
                    if(pago instanceof PagoTicket && ((PagoTicket) pago).getDatosRespuestaPagoTarjeta() != null) {
                        hayPagosTarjeta = true;
                        break;
                    }
                }

                if(hayPagosTarjeta) {
                    if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Es correcta la impresión del recibo del pago con tarjeta?"), getStage())) {
                        break;
                    }
                }
                else {
                    break;
                }

            }

            //Cupones
            if(BigDecimalUtil.isMayorACero(ticketManager.getTicket().getTotales().getTotal())){
                List<CuponEmitidoTicket> cupones = ((TicketVentaAbono)ticketManager.getTicket()).getCuponesEmitidos();
                if(cupones.size()>0){
                    Map<String,Object> mapaParametrosCupon = new HashMap<String,Object>();
                    mapaParametrosCupon.put("ticket", ticketManager.getTicket());
                    FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
                    mapaParametrosCupon.put("paperLess", datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
                    for(CuponEmitidoTicket cupon:cupones){
                        mapaParametrosCupon.put("cupon", cupon);
                        SimpleDateFormat df = new SimpleDateFormat();
                    	mapaParametrosCupon.put("fechaEmision", df.format(ticketManager.getTicket().getCabecera().getFecha()));
                    	
                    	Promocion promocionAplicacion = sesion.getSesionPromociones().getPromocionActiva(cupon.getIdPromocionAplicacion());
                    	if(promocionAplicacion!=null) {
	                    	Date fechaInicio = promocionAplicacion.getFechaInicio();
	                    	if(fechaInicio==null || fechaInicio.before(ticketManager.getTicket().getCabecera().getFecha())) {
	                    		mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(ticketManager.getTicket().getCabecera().getFecha()));
	                    	} else {
	                    		mapaParametrosCupon.put("fechaInicio", FormatUtil.getInstance().formateaFecha(fechaInicio));
	                    	}
	                    	Date fechaFin = promocionAplicacion.getFechaFin();
	                    	mapaParametrosCupon.put("fechaFin", FormatUtil.getInstance().formateaFecha(fechaFin));
	                    	
                    	} else {
                    		mapaParametrosCupon.put("fechaInicio", "");
                    		mapaParametrosCupon.put("fechaFin", "");
                    	}
                    	if(cupon.getMaximoUsos()!=null) {
                    		mapaParametrosCupon.put("maximoUsos" , cupon.getMaximoUsos().toString());
                    	} else {
                    		mapaParametrosCupon.put("maximoUsos" , "");
                    	}
                    	
                    	
                        ServicioImpresion.imprimir(PLANTILLA_CUPON, mapaParametrosCupon);
                    }
                }
                if (!ticketManager.isEsDevolucion()) {
                    //Imprimimos vale para cambio
                    if(mediosPagosService.isCodMedioPagoVale(ticketManager.getTicket().getTotales().getCambio().getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento())
                            && !BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getCambio().getImporte())){
                        printVale(ticketManager.getTicket().getTotales().getCambio());
                    }
                } else {
                	if(documentos.isDocumentoAbono(ticketManager.getTicket().getCabecera().getCodTipoDocumento())) {
	                    //Es una devolución donde el signo del tipo de documento es positivo, imprimimos vales de pagos
	                    List<PagoTicket> pagos = ((TicketVenta) ticketManager.getTicket()).getPagos();
	                    for (PagoTicket pago : pagos) {
	                        if(mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento()) && BigDecimalUtil.isMenorACero(pago.getImporte())){
	                            printVale(pago);
	                        }
	                    }
                	}
                }
            }else{
                //Imprimimos vales para pagos si estamos en devolución pero no si es de cambio (pago positivo en una devolucion donde los pagos son negativos)
                List<PagoTicket> pagos = ((TicketVenta) ticketManager.getTicket()).getPagos();
                for (PagoTicket pago : pagos) {
                    if(mediosPagosService.isCodMedioPagoVale(pago.getCodMedioPago(), ticketManager.getTicket().getCabecera().getTipoDocumento()) && !BigDecimalUtil.isMayorACero(pago.getImporte())){
                        printVale(pago);
                    }
                }
            }
        } catch (Exception e) {
            log.error("imprimir() - " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
            VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir.") + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("El error es: ") + e.getMessage(), e);
        }
	}

	protected void printVale(IPagoTicket iPagoTicket) throws DeviceException {
		Map<String,Object> mapaParametrosTicket = new HashMap<String,Object>();
		mapaParametrosTicket.put("ticket",ticketManager.getTicket());
		mapaParametrosTicket.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));
		mapaParametrosTicket.put("importeVale", FormatUtil.getInstance().formateaImporte(iPagoTicket.getImporte().abs()));
		FidelizacionBean datosFidelizado = ticketManager.getTicket().getCabecera().getDatosFidelizado();
		mapaParametrosTicket.put("paperLess", datosFidelizado != null && datosFidelizado.getPaperLess() != null && datosFidelizado.getPaperLess());
		mapaParametrosTicket.put("esCopia", Boolean.FALSE);
		ServicioImpresion.imprimir(PLANTILLA_VALE, mapaParametrosTicket);
		mapaParametrosTicket.put("esCopia", Boolean.TRUE);
		ServicioImpresion.imprimir(PLANTILLA_VALE, mapaParametrosTicket);
	}

	protected void accionSalvarTicketOnFailure(Exception e) {
        String msg = I18N.getTexto("Error al salvar el ticket.");
		
		if(e instanceof TarjetaException){
			msg = e.getMessage();
		}
		
		if(!(e instanceof TarjetaRegaloException)){ //Ya se muestra desde el dispositivo
			VentanaDialogoComponent.crearVentanaError(getStage(), msg, e);
		}
    }

	@Override
	public void accionCancelar() {		
		boolean hayPagos = false;
		for(IPagoTicket pago : (List<IPagoTicket>) ticketManager.getTicket().getPagos()) {
			if(pago.isEliminable()) {
				hayPagos = true;
				break;
			}
		}
		
		
		if(hayPagos){
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Se han efectuados pagos. Debe cancelarlos para volver atrás."), getStage());
		}
		else {
			try {
				realizarComprobacionesTicketCierrePantalla();
			}
			catch(NoCerrarPantallaException e) {
				return;
			}
			
			visor.modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
			escribirVisor();
			getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
			getStage().close();
		}
	}

	protected void realizarComprobacionesTicketCierrePantalla() throws NoCerrarPantallaException {
	    ticketManager.getTicket().getTotales().recalcular();

	    if(ticketManager.isEsRecargaTarjetaRegalo()){
	    	try {
	    		ticketManager.eliminarTicketCompleto();
	    	} catch (TicketsServiceException | PromocionesServiceException | DocumentoException e) {
	    		log.error("accionCancelar() - Excepción capturada" + e.getMessage(), e);
	    	}
	    }
    }
	
	public void borrarDatosPago(){
		for(PagoTicketGui gui : tbPagos.getItems()) {
			ticketManager.deletePayment(gui.getPaymentId());
		}
		
		ticketManager.getTicket().getTotales().recalcular();

		if(ticketManager.isEsRecargaTarjetaRegalo()){
			try {
				ticketManager.eliminarTicketCompleto();
			} catch (TicketsServiceException | PromocionesServiceException | DocumentoException e) {
				log.error("accionCancelar() - Excepción capturada" + e.getMessage(), e);
			}
		}
		visor.modoPago(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
		escribirVisor();
		getDatos().put(ACCION_CANCELAR, Boolean.TRUE);
		getStage().close();
	}

	/**
	 * Acción realizada al pulsar el botón de facturación
	 */
	public void accionFactura() {
		log.debug("accionFactura()");
		// Comprobamos que se hayan cubierto los pagos
		log.debug("accionFactura() - Pagos cubiertos");
		try {
			if(ticketManager.comprobarConfigContador(Documentos.FACTURA_COMPLETA)){
				getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
				getApplication().getMainView().showModalCentered(FacturaView.class, getDatos(), this.getStage());
				lbDocActivo.setText(ticketManager.getDocumentoActivo().getDestipodocumento());
				cargarBotoneraDatosAdicionales();
			}else{
				ticketManager.crearVentanaErrorContador(getStage());
			}
		}
		catch (Exception e) {
			log.error("accionFactura() - Excepción no controlada : " + e.getCause(), e);
		}
	}

	/**
	 * Acción evento de teclado sobre campo importe
	 *
	 * @param event
	 */
	public void actionTfImporte(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			log.debug("actionTfImporte(() - Tecla INTRO pulsada en campo importe");
			actionBtAnotarPago();
		}
	}

	public void actionBtAnotarPago() {

		frImportePago.clearErrorStyle();
		frImportePago.setImporte(tfImporte.getText());

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioImportePagosBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frImportePago);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioImportePagosBean> next = constraintViolations.iterator().next();
			frImportePago.setErrorStyle(next.getPropertyPath(), true);
			frImportePago.setFocus(next.getPropertyPath());
			VentanaDialogoComponent.crearVentanaAviso(next.getMessage(), getStage());
			tfImporte.requestFocus();
		}
		else {
			BigDecimal importe = FormatUtil.getInstance().desformateaImporte(frImportePago.getImporte());
			anotarPago(importe);
		}
	}

	@FXML
	public void accionCambiarMPDevolucion(){
		HashMap<String, Object> params = new HashMap<>();
		params.put(VueltaController.CLAVE_PARAMETRO_ENTRADA_VUELTA_CODMEDIOPAGO, ticketManager.getTicket().getTotales().getCambio().getMedioPago().getCodMedioPago());
		params.put(VueltaController.CLAVE_PARAMETRO_ENTRADA_VUELTA_DESMEDIOPAGO, ticketManager.getTicket().getTotales().getCambio().getMedioPago().getDesMedioPago());
		params.put(VueltaController.CLAVE_PAYMENT_MANAGER, paymentsManager);

		getApplication().getMainView().showModalCentered(VueltaView.class, params, this.getStage());  

		// Si se seleccionó un medio de pago en la ventana
		if (params.containsKey(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA)){
			String codMedioPagoSeleccionado = (String)params.remove(VueltaController.CLAVE_PARAMETRO_SALIDA_VUELTA);
			MedioPagoBean medioPago = SpringContext.getBean(MediosPagosService.class).getMedioPago(codMedioPagoSeleccionado);
			ticketManager.cambiarMedioPagoVuelta(medioPago);
		}

		// Actualizamos etiquetas
		lbMedioPagoVuelta.setText(ticketManager.getTicket().getTotales().getCambio().getDesMedioPago());

	}

	/**
	 * Acción Evento Eliminar registro
	 *
	 * @param idTabla
	 */
	@Override
	public void accionEventoEliminarTabla(String idTabla) {
		log.debug("accionEventoEliminarTabla()- id:" + idTabla);
		accionBorrarRegistroTabla();
	}

	/**
	 * Acción mover a anterior registro de la tabla
	 */
	protected void accionIrAnteriorRegistroTabla() {
		log.debug("accionIrAnteriorRegistroTabla() - Acción ejecutada");
		if (tbPagos.getItems() != null && tbPagos.getItems() != null) {
			int indice = tbPagos.getSelectionModel().getSelectedIndex();
			if (indice > 0) {
				tbPagos.getSelectionModel().select(indice - 1);
				tbPagos.scrollTo(indice - 1);  // Mueve el scroll para que se vea el registro
			}
		}
		
		restaurarFocoTFImporte();
	}

	/**
	 * Acción mover a siguiente registro de la tabla
	 */
	protected void accionIrSiguienteRegistroTabla() {
		log.debug("accionIrSiguienteRegistroTabla() - Acción ejecutada");
		if (tbPagos.getItems() != null && tbPagos.getItems() != null) {
			int indice = tbPagos.getSelectionModel().getSelectedIndex();
			if (indice < tbPagos.getItems().size()) {
				tbPagos.getSelectionModel().select(indice + 1);
				tbPagos.scrollTo(indice + 1);  // Mueve el scroll para que se vea el registro
			}
		}
		
		restaurarFocoTFImporte();
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
		
		if(gui == null) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay ningún pago seleccionado."), getStage());
			return;
		}
		
		if(!gui.isRemovable()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El pago seleccionado no se puede borrar."), getStage());
			return;
		}
			
		if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Confirme operación."), I18N.getTexto("¿Desea eliminar el pago?"), getStage())){
			log.debug("accionBorrarRegistroTabla() - El usuario quiere borrar el pago con ID: " + gui.getPaymentId());
			
			String codMedioPago = paymentsManager.getCurrentPayments().get(gui.getPaymentId()).getPaymentCode();
			PaymentMethodManager paymentMethodManager = paymentsManager.getPaymentsMehtodManagerAvailables().get(codMedioPago);
			
			if(isDevolucion()) {
				cancelReturn(paymentMethodManager, gui.getPaymentId());
			}
			else {
				cancelPay(paymentMethodManager, gui.getPaymentId());
			}
			visor.modoPago(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
			ticketManager.guardarCopiaSeguridadTicket();
		}
		
		restaurarFocoTFImporte();
	}
	
	/**
	 * Método de control de acciones de página de pagos
	 *
	 * @param botonAccionado botón pulsado
	 */
	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
		case "ACCION_SELECIONAR_MEDIO_PAGO":
			log.debug("realizarAccion() - Acción cambiar medio de pago en pantalla");
			BotonBotoneraTextoComponent boton = (BotonBotoneraTextoComponent) botonAccionado;
			MedioPagoBean mp = boton.getMedioPago();
			
			paymentsManager.select(mp.getCodMedioPago());
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
			accionBorrarRegistroTabla();
			break;
		default:
			log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
			restaurarFocoTFImporte();
			break;
		}

	}
	// </editor-fold>

	/**
	 * Es llamado desde BotoneraComponent si hay botones de tipo PAGO
	 * */
	public void seleccionarImporte(BigDecimal importe) {
		log.debug("seleccionarImporte() - " + importe.toPlainString());
		medioPagoSeleccionado = MediosPagosService.medioPagoDefecto;
		lbMedioPago.setText(medioPagoSeleccionado.getDesMedioPago());
		anotarPago(importe);
	}

	/**
	 * Se hace focus al text field del importe con el importe pendiente por pagar cargado.
	 */
	protected void restaurarFocoTFImporte(){
		tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
		tfImporte.requestFocus();
		tfImporte.selectAll();
	}

	protected void mostrarVentanaCambio(){

		HashMap<String,Object> datos = new HashMap<String, Object>();
		datos.put(VentanaCambioController.PARAMETRO_CANTIDAD_TOTAL, ticketManager.getTicket().getCabecera().getCantidadArticulos());

		datos.put(VentanaCambioController.PARAMETRO_ENTRADA_CAMBIO, lbCambio.getText());
		datos.put(VentanaCambioController.PARAMETRO_ENTRADA_FORMA_PAGO_CAMBIO, lbMedioPagoVuelta.getText());
		datos.put(VentanaCambioController.PARAMETRO_ENTRADA_TOTAL, lbTotal.getText());
		datos.put(VentanaCambioController.PARAMETRO_ENTRADA_ENTREGADO, lbEntregado.getText());
		
		visor.escribir(I18N.getTexto("CAMBIO"), lbCambio.getText());

		getApplication().getMainView().showModalCentered(VentanaCambioView.class,datos,this.getStage());
	}

	public void accionCambiarDatosCliente() {
		getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
		getApplication().getMainView().showModalCentered(CambiarDatosClienteView.class,getDatos(),this.getStage());
	}
	
	public void accionEstablecerDatosEnvio() {
		getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
		getApplication().getMainView().showModalCentered(DatosEnvioView.class,getDatos(),this.getStage());
	}
	
	public void asociarPagoTarjetaRegalo(Boolean venta, GiftCardBean giftCard) {
		if (giftCard != null) {
			BigDecimal saldo = null;

			if (venta) {
				// Si es venta se resta el importe de la tarjeta al valor del saldo
				saldo = giftCard.getSaldoTotal().subtract(giftCard.getImportePago());
				lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(giftCard.getSaldoTotal()) + ")");

				// Si pendiente es 0 , importe es 0
				if (BigDecimalUtil.isIgualACero(ticketManager.getTicket().getTotales().getPendiente())) {
					tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
				}
				else {
					// Si saldo pendiente menor que saldo tarjeta , importe sera saldo pendiente en caso contrario sera el saldo de la tarjeta
					BigDecimal importe = ticketManager.getTicket().getTotales().getPendiente().compareTo(saldo) < 0 ? ticketManager.getTicket().getTotales().getPendiente() : saldo;
					tfImporte.setText(FormatUtil.getInstance().formateaImporte(importe));
				}

			}
			else {
				lbSaldo.setText(I18N.getTexto("Saldo") + ": (" + FormatUtil.getInstance().formateaImporte(giftCard.getSaldoTotal()) + ")");
				tfImporte.setText(FormatUtil.getInstance().formateaImporte(ticketManager.getTicket().getTotales().getPendiente()));
			}
		}
	}
	
    public void seleccionarMedioPago(HashMap<String, String> parametros) {
		if(parametros.containsKey("codMedioPago")){
            String codMedioPago = parametros.get("codMedioPago");
            MedioPagoBean medioPago = mediosPagosService.getMedioPago(codMedioPago);
            if(medioPago != null) {
	            medioPagoSeleccionado = medioPago;
	            paymentsManager.select(medioPago.getCodMedioPago());
	            
	            lbMedioPago.setText(medioPago.getDesMedioPago());
	            
	            if(parametros.containsKey("valor")){
	            	String valor = parametros.get("valor");
	            	try {
		            	BigDecimal importe = new BigDecimal(valor);
		            	anotarPago(importe);
	            	}
	            	catch(Exception e) {
	            		log.error("El valor configurado no se puede formatear: " + valor);
	            	}
	            }
            } else {
            	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el medio de pago"), getStage());
            	log.error("No se ha encontrado el medio de pago con código: " + codMedioPago);
            }
        } else {
        	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha especificado una acción correcta para este botón"), getStage());
        	log.error("No existe el código del medio de pago para este botón.");
        }
	}

	/**
	 * Añade a los campos de texto de la pantalla la capacidad de seleccionar todo su texto cuando adquieren el foco
	 */
	protected void addSeleccionarTodoCampos() {
		addSeleccionarTodoEnFoco(tfImporte);
    }

	/**
	 * Método auxuliar para añadir a un campo de texto la capacidad de seleccionar todo su texto cuando adquiere el foco
	 * @param campo
	 */
	protected void addSeleccionarTodoEnFoco(final TextField campo) {
		campo.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
			public void changed(ObservableValue ov, Boolean t, Boolean t1) {
				Platform.runLater(new Runnable() {
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
	
	/**
	 * Acción aceptar
	 * @throws DocumentoException 
	 */
	@FXML
	public void aceptar() throws DocumentoException {
		if(comprobarGeneracionDocumentos()){
			initTicketManager(true);
			aceptarPagos(true);
		}else{
			aceptarPagos(false);
		}
		
		
	}
	
	protected void aceptarPagos(final boolean repiteOperacion) {
		if(!enProceso){
			enProceso = true;
			log.debug("aceptar()");
			
			if ((((TicketVenta)ticketManager.getTicket()).isPagosCubiertos() && ticketManager.getDocumentoActivo().getRequiereCompletarPagos()) || !ticketManager.getDocumentoActivo().getRequiereCompletarPagos()) {
				log.debug("aceptar() - Pagos cubiertos");
				
				if(superaImporteMaximoEfectivo()) {
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. La cantidad que se quiere pagar en efectivo supera el máximo permitido ({0})", importeMaxEfectivo),getStage());
					enProceso = false;
					recuperarDatosIniciales(repiteOperacion);
					return;
				}
				
				if(!ticketManager.comprobarImporteMaximoOperacion(getStage())){
					enProceso = false;
					recuperarDatosIniciales(repiteOperacion);
					return;
				}
				
				if(!ticketManager.comprobarCierreCajaDiarioObligatorio()){
					String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
					String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual), getStage());
					enProceso = false;
					recuperarDatosIniciales(repiteOperacion);
					return;
				}
				ticketManager.completaLineaDevolucionPunto();
				ticketManager.salvarTicketSeguridad(getStage(), new SalvarTicketCallback() {
					
					@Override
					public void onSucceeded() {
						accionSalvarTicketSucceeded(repiteOperacion);
						enProceso = false;
					}
					
					@Override
					public void onFailure(Exception e) {
						accionSalvarTicketOnFailure(e);
						recuperarDatosIniciales(repiteOperacion);
						enProceso = false;
					}
				});
				
			}
			else {
				log.debug("aceptar() - Pagos no cubiertos");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Los pagos han de cubrir el importe a pagar."), this.getStage());
				recuperarDatosIniciales(repiteOperacion);
				enProceso = false;
			}
		}else{
			log.warn("aceptar() - Pago en proceso");
		}
	}
    
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

				visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
				escribirVisor();
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
    
    protected void returnAmount(PaymentMethodManager paymentMethodManager, String codMedioPago, BigDecimal importe) {
    	final BigDecimal importeFinal = importe;
		final PaymentMethodManager paymentMethodManagerFinal = paymentMethodManager;
		new BackgroundTask<Void>(){
			@Override
            protected Void call() throws Exception {
				paymentsManager.returnAmount(paymentMethodManagerFinal.getPaymentCode(), importeFinal);
                return null;
            }
			@Override
			protected void succeeded() {
				super.succeeded();
				
				visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
	    		escribirVisor();
			}
			@Override
		    protected void failed() {
				super.failed();
				
				Throwable e = getException();
				
				if (e instanceof PaymentException) {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, ((PaymentException)e).getPaymentId(), e, ((PaymentException)e).getErrorCode(), ((PaymentException)e).getMessage());
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
					
				} else {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, -1, e, null, null);
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
				}
			}
		}.start();
    }
	
	protected void cancelReturn(PaymentMethodManager paymentMethodManager, Integer paymentId) {
		final Integer paymentIdFinal = paymentId;
		new BackgroundTask<Void>(){
			@Override
            protected Void call() throws Exception {
				paymentsManager.cancelReturn(paymentIdFinal);
                return null;
            }
			@Override
			protected void succeeded() {
				super.succeeded();
				
				visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
	    		escribirVisor();
			}
			@Override
		    protected void failed() {
				super.failed();
				
				Throwable e = getException();
				
				if (e instanceof PaymentException) {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, ((PaymentException)e).getPaymentId(), e, ((PaymentException)e).getErrorCode(), ((PaymentException)e).getMessage());
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
					
				} else {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, -1, e, null, null);
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
				}
			}
		}.start();
	}
	
	protected void cancelPay(PaymentMethodManager paymentMethodManager, Integer paymentId) {
		final Integer paymentIdFinal = paymentId;
		new BackgroundTask<Void>(){
			@Override
            protected Void call() throws Exception {
				paymentsManager.cancelPay(paymentIdFinal);
                return null;
            }
			@Override
			protected void succeeded() {
				super.succeeded();
				
				visor.modoPago(visorConverter.convert((TicketVenta) ticketManager.getTicket()));
	    		escribirVisor();
			}
			@Override
		    protected void failed() {
				super.failed();
				
				Throwable e = getException();
				
				if (e instanceof PaymentException) {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, ((PaymentException)e).getPaymentId(), e, ((PaymentException)e).getErrorCode(), ((PaymentException)e).getMessage());
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
					
				} else {
					PaymentErrorEvent errorEvent = new PaymentErrorEvent(this, -1, e, null, null);
					PaymentsErrorEvent event = new PaymentsErrorEvent(this, errorEvent);
					paymentsManager.getEventsHandler().paymentsError(event);						
				}
			}				
		}.start();
	}
	
	protected boolean comprobarGeneracionDocumentos() throws DocumentoException{
		boolean lineaDevuelta = false;
		boolean lineaNueva = false;
		if(ticketManager.getTicketOrigen() != null && tipoDocumentoInicial != null){
			boolean esSignoForzadoInicial = !tipoDocumentoInicial.isSignoLibre();
			boolean esSignoForzadoOrigen = !documentos.getDocumento(ticketManager.getTicketOrigen().getCabecera().getTipoDocumento()).isSignoLibre();

			boolean signoForzado = esSignoForzadoInicial || esSignoForzadoOrigen;
			
			if(signoForzado){
				for(LineaTicket linea : lineasVentaAbono){
					if(linea.getLineaDocumentoOrigen() == null){
						lineaNueva = true;
						if(lineaDevuelta){
							break;
						}
					}else{
						lineaDevuelta = true;
						if(lineaNueva){
							break;
						}
					}
				}
			}
		}
		
		return lineaNueva && lineaDevuelta;
	}
	
	// Inicializamos el ticket con las lineas de venta 
  	protected void initTicketManager(boolean primeraOperacion){	
  		boolean esDevolucion;
  		if(primeraOperacion){
  			copiaPagos = new ArrayList<>(ticketManager.getTicket().getPagos());
			totalAPagarConjunto = ticketManager.getTicket().getCabecera().getTotales().getTotalAPagar();
			esDevolucion = BigDecimalUtil.isMenorOrIgualACero(totalAPagarConjunto);
  		}else{
  			ticketManager.setEsDevolucionTarjetaRegalo(false);
  			ticketManager.setEsOperacionTarjetaRegalo(false);
  			esDevolucion = !BigDecimalUtil.isMenorOrIgualACero(totalAPagarConjunto);
  		}
		BigDecimal factorSigno = BigDecimal.ONE;
		if(esDevolucion) {
			ticketManager.setDocumentoActivo(tipoDocumentoInicial);
		}
		else {
			// Se inicializa documento activo a tipo de documento de origen
			try{
				ticketManager.setDocumentoActivo(documentos.getDocumento(ticketManager.getTicketOrigen().getCabecera().getTipoDocumento()));
			}catch(Exception ignore){}
		}
		
		// Solo tiene sentido en la venta, ya que la devocuíçon se genera con el signo con el que viene
	    // obtenemos el signo del concepto de almacén
		if(ticketManager.getDocumentoActivo().isSignoNegativo()){
			factorSigno = factorSigno.negate();
		}

		// Limpiamos las líneas
		ticketManager.getTicket().getLineas().clear();
		// Se eliminan los pagos anotados
		ticketManager.getTicket().getPagos().clear();
		
		for (LineaTicket linea : lineasVentaAbono) {
			if(esDevolucion){
				// Solo las líneas con referencia en el documento origen
				if(linea.getLineaDocumentoOrigen()!=null){
	    			ticketManager.getTicket().getLineas().add(linea.clone());
	    		}
			}
			else {
  				if (linea.getLineaDocumentoOrigen() == null) {
  					// Establecemos el signo a lo indicado en el tipo de documento
  					linea.setCantidad(linea.getCantidad().abs().multiply(factorSigno));
  					linea.recalcularImporteFinal();
  					ticketManager.getTicket().getLineas().add(linea.clone());
  				}
			}
		}
		

		((TicketVentaAbono)ticketManager.getTicket()).getTotales().recalcular();
		arreglaPagos();
		
  	}
  	
  	protected void arreglaPagos() {
  		BigDecimal totalActual = ticketManager.getTicket().getCabecera().getTotales().getTotalAPagar();
  		BigDecimal diferencia = totalActual.subtract(totalAPagarConjunto);
  		boolean diferenciaMenor = BigDecimalUtil.isMenor(diferencia.abs(), totalActual.abs());
  		PagoTicket pagoComplementario = new PagoTicket(mediosPagosService.medioPagoDefecto);
  		if(diferenciaMenor){
  			ticketManager.getTicket().getPagos().addAll(copiaPagos);
  			ticketManager.getTicket().getTienda().getTiendaBean().getCodMedioPagoPorDefecto();
  			pagoComplementario.setImporte(diferencia);
  		}else{
  			pagoComplementario.setImporte(ticketManager.getTicket().getTotales().getTotalAPagar());
  		}
  		if(!BigDecimalUtil.isIgualACero(pagoComplementario.getImporte())){  			
  			ticketManager.getTicket().getPagos().add(pagoComplementario);
  		}
  		((TicketVentaAbono)ticketManager.getTicket()).getTotales().recalcular();

	}
  	
	protected void recuperarDatosIniciales(boolean repiteOperacion) {
		if(repiteOperacion){			
			ticketManager.setDocumentoActivo(tipoDocumentoInicial);
			ticketManager.getTicket().setPagos(new ArrayList<>(copiaPagos));
			ticketManager.getTicket().getLineas().clear();
			ticketManager.getTicket().getLineas().addAll(new ArrayList<>(lineasVentaAbono));
			((TicketVentaAbono)ticketManager.getTicket()).getTotales().recalcular();
		}
	}
  	
	@Override
  	public void onClose(){
  		super.onClose();
  		tipoDocumentoInicial = null;
  	}
	
}
