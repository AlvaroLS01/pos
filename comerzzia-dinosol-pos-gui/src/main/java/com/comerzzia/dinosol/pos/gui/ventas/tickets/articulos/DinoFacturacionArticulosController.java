package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.comarch.clm.partner.exception.BpConfiguracionException;
import com.comarch.clm.partner.exception.BpRespuestaException;
import com.comarch.clm.partner.exception.BpSoapException;
import com.comerzzia.dinosol.librerias.cryptoutils.CryptoUtils;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.DomicilioApi;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.pos.core.gui.componentes.keyboard.KeyboardDataDto;
import com.comerzzia.dinosol.pos.devices.fidelizacion.DinoFidelizacion;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.componentes.aviso.AvisoComponentController;
import com.comerzzia.dinosol.pos.gui.componentes.aviso.AvisoComponentView;
import com.comerzzia.dinosol.pos.gui.tarjetas.saldo.DinoSaldoTarjetasView;
import com.comerzzia.dinosol.pos.gui.ventas.blackfriday.infodevoluciones.InfoDevolucionesBlackFridayView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.DomicilioOfflineDto;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.EnvioDomicilioController;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.EnvioDomicilioView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADController;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasServiciosException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.gui.ventas.gestiontickets.detalle.DinoDetalleGestionticketsController;
import com.comerzzia.dinosol.pos.gui.ventas.promociones.opciones.SeleccionOpcionesPromocionController;
import com.comerzzia.dinosol.pos.gui.ventas.promociones.opciones.SeleccionOpcionesPromocionView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.DinoTicketManager;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.anulacion.AnulacionArticuloDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.anulacion.SeleccionPrecioController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.anulacion.SeleccionPrecioView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.avisos.ScreenNotificationDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.avisos.ShowScreenNotificationsController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.avisos.ShowScreenNotificationsView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.busquedas.DinoBuscarArticulosController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.seleccionrecarga.SeleccionArticuloRecargaController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.seleccionrecarga.SeleccionArticuloRecargaView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.ticketparking.TicketParkingController;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.ticketparking.TicketParkingView;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.auxiliar.LineaPlasticoInexistenteException;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaCuponImporteDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaQRLiquidacionDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaQrBalanzaDto;
import com.comerzzia.dinosol.pos.gui.ventas.tickets.dto.LecturaTarjetaBpDto;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionDocumentoBean;
import com.comerzzia.dinosol.pos.persistence.restricciones.RestriccionLineaBean;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriaDto;
import com.comerzzia.dinosol.pos.services.auditorias.AuditoriasService;
import com.comerzzia.dinosol.pos.services.codbarrasesp.DinoCodBarrasEspecialesServices;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.RestriccionesException;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.RestriccionesService;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionPromociones;
import com.comerzzia.dinosol.pos.services.cupones.ComparatorCuponImporte;
import com.comerzzia.dinosol.pos.services.cupones.CustomerCouponDTO;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.dispositivos.recargas.articulos.ArticulosRecargaService;
import com.comerzzia.dinosol.pos.services.documents.LocatorManagerImpl;
import com.comerzzia.dinosol.pos.services.payments.methods.prefijos.PrefijosTarjetasService;
import com.comerzzia.dinosol.pos.services.payments.methods.types.bp.BPManager;
import com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.DinoPromocionTextoBean;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.dinosol.pos.services.tarjetasregalo.TarjetasRegaloService;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketsService;
import com.comerzzia.dinosol.pos.services.ticket.aparcados.DinoTicketsAparcadosService;
import com.comerzzia.dinosol.pos.services.ticket.aparcados.TicketConIdAparcadoException;
import com.comerzzia.dinosol.pos.services.ticket.aparcados.TicketsMaximoAparcadosException;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.TarjetaIdentificacionDto;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.opciones.OpcionPromocionesSeleccionadaDto;
import com.comerzzia.dinosol.pos.services.ventas.reparto.ServiciosRepartoService;
import com.comerzzia.dinosol.pos.services.ventas.reparto.dto.ServicioRepartoDto;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.DispositivoCallback;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.BalanzaNoConfig;
import com.comerzzia.pos.core.dispositivos.dispositivo.balanza.IBalanza;
import com.comerzzia.pos.core.dispositivos.dispositivo.impresora.IPrinter;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.VisorNoConfig;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.login.seleccionUsuarios.SeleccionUsuarioController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.dispositivo.impresora.parser.PrintParserException;
import com.comerzzia.pos.gui.ventas.gestiontickets.GestionTicketGui;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionTicketView;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.LineaTicketGui;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.balanza.SolicitarPesoArticuloView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.BuscarArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.articulos.edicion.EdicionArticuloController;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.codBarras.CodigoBarrasBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.services.cajas.CajaRetiradaEfectivoException;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.codBarrasEsp.CodBarrasEspecialesServices;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.SesionPromociones;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.payments.configuration.PaymentMethodConfiguration;
import com.comerzzia.pos.services.payments.configuration.PaymentsMethodsConfiguration;
import com.comerzzia.pos.services.payments.methods.PaymentMethodManager;
import com.comerzzia.pos.services.promociones.DocumentoPromocionable;
import com.comerzzia.pos.services.promociones.Promocion;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.cabecera.TotalesTicket;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.cupones.CuponAplicadoTicket;
import com.comerzzia.pos.services.ticket.lineas.ILineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketAbstract;
import com.comerzzia.pos.services.ticket.lineas.LineaTicketException;
import com.comerzzia.pos.services.ticket.lineas.LineasTicketServices;
import com.comerzzia.pos.services.ticket.profesional.TotalesTicketProfesional;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.services.ticket.tarjetaRegalo.TarjetaRegaloException;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.rest.client.exceptions.RestException;
import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;

import feign.RetryableException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

@Component
@Primary
@SuppressWarnings({ "unchecked", "rawtypes", "restriction"})
public class DinoFacturacionArticulosController extends FacturacionArticulosController {

	private static final String TITULO_COLUMNA_PVP_DTO = "PVP DTO.";
	private static final String PERMISO_RESTRICCIONES = "SIN RESTRICCIONES";
	public static final String PERMISO_IMPRIMIR_ULTIMO_TICKET = "IMPRIMIR ULTIMO TICKET";
	
	public static final String ID_VARIABLE_TIPO_PARKING = "X_PARKING_TIPO";
	public static final String ID_VARIABLE_TIMESTAMP = "X_FIDELIZACION.TIMESTAMP";
	public static final String ID_VARIABLE_TIMESTAMP_SEGMENTO = "X_FIDELIZACION.SEGMENTO_TIMESTAMP";

	/* Objetos para controlar las excepciones */
	protected List<RestriccionLineaBean> restriccionLineas;
	protected RestriccionDocumentoBean restriccionDocumento;
	
	@FXML
	private ImageView imgTarjetaDinoBP, imgTarjetaEmpleado;
	
	@FXML
	private Label lbCuponesLeidos, lbTituloCodCliente, lbAnulacion, lbRetiradaCaja;
	
	@FXML
	private AnchorPane apSolicitarQr, apCabecera;
	
	@FXML
	private Button btBorrarFidelizado;
	
	@FXML
	private ImageView ivServicioReparto;

	@Autowired
	private VariablesServices variablesServices;
	@Autowired
	private RestriccionesService restriccionesService;

	@Autowired
	protected DinoTicketsService dinoTicketsService;
	
	@Autowired
	private DinoCuponesService cuponesService;
	
	@Autowired
	private CodBarrasEspecialesServices codBarrasEspecialesServices;

	@Autowired
	private PaymentsMethodsConfiguration paymentsMethodsConfiguration;
	
	@Autowired
	private ArticulosRecargaService articulosRecargaService;
	
	@Autowired
	private PrefijosTarjetasService prefijosTarjetasService;
	
	@Autowired
	private DinoTicketsAparcadosService ticketsAparcadosService;
	
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	
	@Autowired
	private LineasTicketServices lineasTicketServices;
	
	@Autowired 
	private DinoCodBarrasEspecialesServices dinoCodBarrasEspecialesService;
	
	@Autowired
	private CajasService cajasService;
	
	@Autowired
	private TarjetasRegaloService tarjetasRegaloService;
	
	@Autowired
	private AuditoriasService auditoriasService;
	
	@Autowired
	private SesionPromociones sesionPromociones;
	
	@Autowired
	private ServiciosRepartoService serviciosRepartoService;
	
	private Clip clip;
	private Clip clipEspecial;
	private Clip clipError;

	private String idPedidoSAD;
	/* Para el cambio de imagen del botón de Portes */
	private BooleanProperty cambiarIcono;
	
	private DomicilioApi domicilioService;

	private BPManager bpManager;
	
	private boolean insertandoCupon;
	
	private boolean parkingActivo;
	
	private boolean modoAnulacion;
	
	private boolean modoLecturaQrLiquidacion;
	
	public void initialize(URL url, ResourceBundle rb) {
		super.initialize(url, rb);

		/* Anteriormente mostraba */
		tcLineasDescuento.setCellFactory(CellFactoryBuilder.createCellRendererCeldaImporte("tbLineas", "tcLineasDescuento", CellFactoryBuilder.ESTILO_ALINEACION_DCHA));
		tcLineasDescuento.setText(TITULO_COLUMNA_PVP_DTO);
		tcLineasDescuento.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LineaTicketGui, BigDecimal>, ObservableValue<BigDecimal>>(){

			@Override
			public ObservableValue<BigDecimal> call(CellDataFeatures<LineaTicketGui, BigDecimal> cdf) {
				ObservableValue<BigDecimal> res = null;
				if(cdf.getValue().getDescuentoProperty().getValue() != null){
					if(BigDecimalUtil.isIgualACero(cdf.getValue().getDescuentoProperty().getValue())){
						res = cdf.getValue().getPvpProperty();
					}else{
						res = cdf.getValue().getPvpConDtoProperty();
					}
				}
			return res;
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
							getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
							getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							getStyleClass().removeAll(Collections.singleton("linea-negativa"));
							
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
							/*
							 * En caso de tener alguna promoción aplicada, se pone en color amarillo
							 */
							if (BigDecimalUtil.isMayorACero(linea.getDescuentoProperty().getValue())) {
								getStyleClass().add("cell-renderer-lineaDocAjeno");
							}
							
							if (BigDecimalUtil.isMenorOrIgualACero(linea.getCantidadProperty().getValue())) {
								getStyleClass().add("linea-negativa");
							}
						}
						else{
							getStyleClass().removeAll(Collections.singleton("cell-renderer-lineaDocAjeno"));
							getStyleClass().removeAll(Collections.singleton("cell-renderer-cupon"));
							getStyleClass().removeAll(Collections.singleton("linea-negativa"));
						}
					};
				};
			};

		};
		tbLineas.setRowFactory(callback);
		
		String tipoParking = variablesServices.getVariableAsString(ID_VARIABLE_TIPO_PARKING);
		if(StringUtils.isNotBlank(tipoParking)) {
			parkingActivo = true;
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		super.initializeForm();

		cargarRestricciones();
		
		if(ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			lbTituloCodCliente.setVisible(false);
			lbCodCliente.setVisible(false);
			lbDesCliente.setVisible(false);
		}
		
		activarModoAnulacion(false);
	}

	/**
	 * Realizamos la carga de los dos tipos de restricciones.
	 */
	protected void cargarRestricciones(){
		cargarRestriccionesLineas();
		cargarRestriccionesDocumento();
	}
	
	protected void cargarRestriccionesDocumento() {
		/* Realizamos la carga de las restricciones de documento desde BD. */
		try {
			restriccionDocumento = restriccionesService.cargarRestriccionesDocumento(ticketManager.getNuevoDocumentoActivo());
		}
		catch (DocumentoException e) {
			String mensajeError = "Se ha producido un error cargando las restricciones de documento";
			log.error("initializeForm() - " + mensajeError + " : " + e.getMessage());
			VentanaDialogoComponent.crearVentanaAviso(mensajeError, getStage());
		}
    }
	
	protected void cargarRestriccionesLineas() {
		/* Realizamos la carga del archivo XML de las restricciones. */
		try {
			restriccionLineas = restriccionesService.cargarRestriccionesLineas();
		}
		catch (RestriccionesException e) {
			log.error("initializeForm() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaAviso(e.getMessage(), getStage());
		}
    }

	/**
	 * Cambia el icono del botón de Portes cuando se pulsa.
	 */
	private void addListenerBotonPortes() {
		/* Creamos la acción que saltará al pulsar sobre el botón */
		cambiarIcono = new SimpleBooleanProperty();
		botonera.getBotonClave("modificarPortes").setOnAction(event -> {
            cambiarIcono.set(!cambiarIcono.get());
        });
		
		cambiarIcono.addListener((observable, oldValue, newValue) -> {
			String imagen = null;
			if (newValue) {
				imagen = "iconos/truck-checked.png";
            } else {
            	imagen = "iconos/truck.png";
            }
			cambiarBotonPortes(imagen);
			modificarPortes();
        });
	}
	
	/**
	 * Reinicia el panel de botones con la nueva imagen puesta en el botón de Portes.
	 * @param imagen : Ruta donde está la imagen.
	 */
	private void cambiarBotonPortes(String imagen) {
		BotonBotoneraComponent boton = botonera.getBotonBotonera("modificarPortes");
		if(boton instanceof BotonBotoneraNormalComponent) {
			((BotonBotoneraNormalComponent) boton).cambiarImagen(imagen);
		}
	}

	protected boolean validarLinea(LineaTicket linea) {
		/* Si no tiene el permiso, se deberán comprobar las restricciones */
		Boolean venderSinRestricciones = comprobarPermisoRestricciones();
		if(restriccionLineas == null || restriccionLineas.isEmpty()) {
			cargarRestricciones();
		}
		if (!venderSinRestricciones && linea != null) {
			String mensajeError = null;
			try {
				mensajeError = restriccionesService.validadorLineas(linea);
			} catch (RestriccionesException e) {
				log.error("validarLinea() - " + e.getMessage());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
			}
			
			if (mensajeError == null) {
				return true;
			}
			else {
				log.error("validarLinea() - " + mensajeError);
				reproducirSonidoError();
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeError), getStage());
				return false;
			}
		}
		else {
			return true;
		}
	}

	/**
	 * Acción editar registro de la tabla
	 */
	@FXML
	protected void accionTablaEditarRegistro() {
		if (tbLineas.getItems() != null && getLineaSeleccionada() != null) {
			LineaTicketGui selectedItem = getLineaSeleccionada();
			BigDecimal cantidad = selectedItem.getCantidadProperty().getValue();
			if(BigDecimalUtil.isIgualACero(cantidad)) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se puede editar una línea que haya sido anulada"), this.getStage());
			}
			else {
				super.accionTablaEditarRegistro();
			}
			
		}
		
	}

	public void abrirPagos() {
		if(parkingActivo) {
			String codartParking = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_PARKING);
			String codartExtravio = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_EXTRAVIO);
			
			boolean tieneArticuloParking = false;
			for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
				if(linea.getCodArticulo().equals(codartParking) || linea.getCodArticulo().equals(codartExtravio)) {
					tieneArticuloParking = true;
				}	
			}
			
			if(!tieneArticuloParking && !((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).isGenerarQrParking()) {
				String texto = I18N.getTexto("Recuerde preguntar al cliente si tiene el ticket del parking.")
						+ System.lineSeparator() + System.lineSeparator()
						+ I18N.getTexto("¿Desea leer el ticket del parking del cliente?.");
				if(VentanaDialogoComponent.crearVentanaConfirmacion(texto, getStage(), I18N.getTexto("Si"), I18N.getTexto("No"))) {
					introducirArticuloParking();
					return;
				}
			}
		}
		
		boolean tieneServicioReparto = comprobarServicioRepartoSeleccionado();
		if(!tieneServicioReparto) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Debe seleccionar un servicio de reparto. Cancele la venta y empiece de nuevo."), getStage());
			return;
		}
		
		boolean continuarCompra = abrirPantallaSeleccionOpcionesPromocion();
		if(continuarCompra) {
			return;
		}
		
		boolean tienePromoTexto = showTextPromotions();
		if(tienePromoTexto) {
			return;
		}
		
		
		if (((DinoTicketManager) ticketManager).hayQrLiquidacionGuardado()) {
			log.debug("abrirPagos() - Se ha introducido previamente un QR de Liquidacion pero no se ha introducido su articulo asociado");

			((DinoTicketManager) ticketManager).setQrLiquidacionDTO(null);
			
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se ha pasado el artículo asociado al descuento. Vuelva a pasar el descuento y el artículo."), getStage());
			
			return;
		}
		
		boolean continuar = redirigirPantallaActivacionContenidosDigitales();
		if(!continuar) {
			return;
		}
		
		Boolean venderSinRestricciones = comprobarPermisoRestricciones();
		if (!venderSinRestricciones) {
			if (restriccionDocumento == null) {
				cargarRestricciones();
			}

			/* Cogemos la cantidad de articulos que contiene el ticket */
			BigDecimal cantidadArticulosTicket = BigDecimal.ZERO;
			for (LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
				cantidadArticulosTicket = cantidadArticulosTicket.add(linea.getCantidad());
			}
			/* Cogemos el importe del ticket para compararlo */
			BigDecimal importeTicket = ticketManager.getTicket().getTotales().getTotalAPagar();

			if (BigDecimalUtil.isMayor(cantidadArticulosTicket.abs(), restriccionDocumento.getCantidadMaxima().abs())) {
				String mensajeError = "Superada la cantidad máxima de artículos por documento (" + restriccionDocumento.getCantidadMaxima() + ")";
				log.info("abrirPagos() - " + mensajeError);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeError), getStage());
				return;
			}
			if (BigDecimalUtil.isMayor(importeTicket.abs(), restriccionDocumento.getImporteMaximo().abs())) {
				String mensajeError = "Superado el importe máximo por documento (" + restriccionDocumento.getImporteMaximo() + ")";
				log.info("abrirPagos() - " + mensajeError);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeError), getStage());
				return;
			}

			/* Vaciamos los datos de SAD */
			((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosSad(null);
			if (tienePorte()) {
				if (ticketManager.getTicket().getLineas().size() == 1) {
					String mensajeInfo = "No se puede realizar una venta solo con el Porte";
					log.info("abrirPagos() - " + mensajeInfo);
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
				}
				else {
					irPantallaEnvioDomicilio();
				}
			}
			else { 
				abrirPagosEstandar();
			}
		}
		else {
			/* Vaciamos los datos de SAD */
			((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setDatosSad(null);
			if (tienePorte()) {
				if (ticketManager.getTicket().getLineas().size() == 1) {
					String mensajeInfo = "No se puede realizar una venta solo con el Porte";
					log.info("abrirPagos() - " + mensajeInfo);
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
				}
				else {
					irPantallaEnvioDomicilio();
				}
			}
			else {
				abrirPagosEstandar();
			}
		}
	}

	private boolean redirigirPantallaActivacionContenidosDigitales() {
		if(!((DinoTicketManager) ticketManager).esVentaContenidoDigital() && !((DinoTicketManager) ticketManager).esVentaRecargaMovil()) {
			return true;
		}
		
		String mensaje = I18N.getTexto("La activación de tarjetas de contenido digital requiere la presencia física del cliente, bajo ninguna circunstancia se permite la activación a través de peticiones vía telefónica.");
		mensaje = mensaje + System.lineSeparator() + System.lineSeparator() + I18N.getTexto("¿Confirma que el cliente se encuentra de forma presencial en la tienda?");
		
		HashMap<String, Object> datos = new HashMap<String, Object>();
		datos.put(AvisoComponentController.PARAM_AVISO_MENSAJE, mensaje);
		getApplication().getMainView().showModalCentered(AvisoComponentView.class, datos, getStage());
		boolean confirmacion = (boolean) datos.get(AvisoComponentController.PARAM_AVISO_RESULTADO);
		
		return confirmacion;
	}
	
	private void abrirPagosEstandar() {
		if(isVentaVacia()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede hacer una venta con todas las líneas con cantidad cero."), getStage());
			return;
		}
		
		if(hayVariosArticuloRecarga()) {
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Solo se puede vender un artículo de recarga en cada venta."), getStage());
			return;
		}
		
		aplicarCupones();
	    super.abrirPagos();
	    if (!getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
	    	if ((boolean) getDatos().get("esVentaDigital")) {
	    		getApplication().getMainView().showActionView(7003L, datos);
	    	}
	    	
	    	if (getDatos().containsKey("esRecargaMovil") && (boolean) getDatos().get("esRecargaMovil")) {
	    		getApplication().getMainView().showActionView(7001L, datos);
	    	}
	    }
    }

	protected boolean showTextPromotions() {
		List<ScreenNotificationDto> notifications = new ArrayList<ScreenNotificationDto>();
		
		List<PromocionTicket> promotions = ticketManager.getTicket().getPromociones();
		if(promotions != null) {
			for(PromocionTicket promotion : promotions) {
				Promocion activePromotion = sesionPromociones.getPromocionActiva(promotion.getIdPromocion());
				if(activePromotion instanceof DinoPromocionTextoBean && ((DinoPromocionTextoBean) activePromotion).isVisibleCajero()) {
					String titlePromotion = activePromotion.getDescripcion();
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
			boolean confirmacion = (boolean) params.get(ShowScreenNotificationsController.PARAM_AVISO_RESULTADO);
			
			if(!confirmacion) {
				return true;
			}
		}
		return false;
	}

	
	private boolean hayVariosArticuloRecarga() {
		try {
			int cantidad = 0;
			for(String codart : articulosRecargaService.getConfiguracion().getArticulos()) {
				for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
					if(linea.getCodArticulo().equals(codart) && BigDecimalUtil.isMayorACero(linea.getCantidad())) {
						cantidad++;
					}
				}
			}
			
			if(cantidad > 1) {
				return true;
			}
			
		    return false;
		}
		catch(Exception e) {
			log.error("hayVariosArticuloRecarga() - Ha habido un problema al comprobar si hay varios artículos de recarga en la misma venta: " + e.getMessage(), e);
			return true;
		}
    }

	private boolean isVentaVacia() {
		for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			if(!BigDecimalUtil.isIgualACero(linea.getCantidad())) {
				return false;
			}
		}
	    return true;
    }

	protected void aplicarCupones() {
		List<CustomerCouponDTO> cuponesLeidos = new ArrayList<CustomerCouponDTO>(((DinoTicketManager) ticketManager).getCuponesLeidos());
		
		eliminarCuponesDuplicados(cuponesLeidos);
		
		List<String> cuponesAntiguos = new ArrayList<String>();
		for (CustomerCouponDTO cupon : cuponesLeidos) {
			if(cupon.isCuponAntiguo()) {
				cuponesAntiguos.add(cupon.getCouponCode());
			}
		}
		
		boolean hayCuponesAntiguos = cuponesAntiguos != null && !cuponesAntiguos.isEmpty();
		if (hayCuponesAntiguos) {
			List<CustomerCouponDTO> cupones95 = new ArrayList<CustomerCouponDTO>();
			List<String> flyers = new ArrayList<String>();
			separarCupones(cuponesAntiguos, cupones95, flyers);

			introducirCupones95(cupones95);

			introducirFlyers(flyers);
		}

		boolean hayCuponesLeidos = cuponesLeidos != null && !cuponesLeidos.isEmpty();
		if (hayCuponesLeidos) {
			
			try {
				Collections.sort(cuponesLeidos, new ComparatorCuponImporte());
			}
			catch(Exception e) {
				log.error("aplicarCupones() - Ha habido un error al ordenar la lista de cupones: " + e.getMessage(), e);
			}
			
			for (CustomerCouponDTO cupon : cuponesLeidos) {
				if(cupon.isCuponNuevo()) {
					try {
						boolean resultado = ((DinoSesionPromociones) sesion.getSesionPromociones()).aplicarCupon(cupon, (DocumentoPromocionable) ticketManager.getTicket());
						log.debug("aplicarCupones() - ¿Cupón " + cupon.getCouponCode() + " aplicado? " + resultado);
					}
					catch (Exception e) {
						log.error("aplicarCupones() - Error al aplicar el cupón " + cupon.getCouponCode() + ": " + e.getMessage());
					}
				}
			}
		}
		
		// Volvemos a recuperar los cupones leídos ya que se pueden haber borrado en el transcurso de la aplicación para pasarlos a la lista de cupones de fidelizado
		cuponesLeidos = new ArrayList<CustomerCouponDTO>(((DinoTicketManager) ticketManager).getCuponesLeidos());

		if (hayCuponesLeidos) {
			mostrarResultadoAplicacionCupones(cuponesLeidos);
		}
    }

	private void eliminarCuponesDuplicados(List<CustomerCouponDTO> cuponesLeidos) {
		Set<CustomerCouponDTO> set = new HashSet<>(cuponesLeidos);
		cuponesLeidos.clear();
		cuponesLeidos.addAll(set);
	}

	protected void introducirCupones95(List<CustomerCouponDTO> cupones95) {
		log.debug("introducirCupones95() - Introduciendo cupones de promoción.");
		
		for(CustomerCouponDTO cupon : cupones95) {
			log.debug("introducirCupones95() - Introduciendo cupón: " + cupon.getCouponCode());
			try {
				insertandoCupon = true;
				
				boolean resultado = ((DinoSesionPromociones) sesion.getSesionPromociones()).aplicarCupon(cupon, (DocumentoPromocionable) ticketManager.getTicket());
				log.debug("aplicarCupones() - ¿Cupón " + cupon.getCouponCode() + " aplicado? " + resultado);
			}
			catch (Exception e) {
				log.error("introducirCupones95() - Ha habido un error al introducir el cupón " + cupon.getCouponCode() + ": " + e.getMessage());
			}
			finally {
				insertandoCupon = false;
			}
		}
	}

	protected void introducirFlyers(List<String> flyers) {
		log.debug("introducirFlyers() - Introduciendo flyers.");
		
		BigDecimal total = ticketManager.getTicket().getTotales().getTotalAPagar();
		for(String cupon : flyers) {
			log.debug("introducirFlyers() - Introduciendo flyer " + cupon);
			
			try {
				insertandoCupon = true;
				
				BigDecimal importeCupon = null;
				
				//Comprobamos si es un FLYER
				CodigoBarrasBean codBarrasEspecial = codBarrasEspecialesServices.esCodigoBarrasEspecial(cupon);
				if(codBarrasEspecial != null && codBarrasEspecial.getDescripcion().equals("FLYER") && cupon.length() == 13) {
					importeCupon = new BigDecimal(codBarrasEspecial.getCodticket()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
					
					BigDecimal importeCuponesAplicados = BigDecimal.ZERO;
					for(CuponAplicadoTicket cuponAplicado : (List<CuponAplicadoTicket>) ticketManager.getTicket().getCuponesAplicados()) {
						importeCuponesAplicados = importeCuponesAplicados.add(cuponAplicado.getImporteTotalAhorrado());
					}
					
					BigDecimal importeCuponesAplicadosTrasAplicacion = importeCuponesAplicados.add(importeCupon);
					//Si es un FLYER y el importe de la compra no supera al importe del FLYER, no se aplica el cupón
					if(BigDecimalUtil.isMayor(importeCuponesAplicadosTrasAplicacion, total)) {
						log.debug("introducirFlyers() - El descuento del cupón " + cupon + " es superior al pendiente de la compra.");
						log.debug("introducirFlyers() - No se aplicará el cupón.");
						continue;
					}
				}
				else{
					log.error("introducirFlyers() - El cupón introducido no es un flyer válido.");
				}
				
				/*Si es un FLYER que cumple las condiciones anteriores se delega en el motor de promociones*/
				insertarLineaVenta(cupon, null, null, BigDecimal.ONE);
			}
			catch (Exception e) {
				log.error("introducirFlyers() - Ha habido un error al introducir el cupón " + cupon + ": " + e.getMessage());
			}
			finally {
				insertandoCupon = false;
			}
		}
	}

	protected void separarCupones(List<String> cuponesLeidos, List<CustomerCouponDTO> cupones95, List<String> flyers) {
		log.debug("separarCupones() - Se van a separar los cupones para aplicar los cupones de promoción en orden decreciente de importe.");
		
		// Ordenamos los cupones en orden decreciente para que los cupones 21 se apliquen los últimos. 
		Comparator<String> comparadorCupones = new Comparator<String>(){
			@Override
            public int compare(String o1, String o2) {
	            return o2.compareTo(o1);
            }
		};
		Collections.sort(cuponesLeidos, comparadorCupones);
		
		log.debug("separaCupones() - Cupones disponibles: " + cuponesLeidos);
		
		for(String cupon : cuponesLeidos) {
			cupon = cupon.trim();
			
			if(StringUtils.isNotBlank(cupon)) {
				BigDecimal importeCupon = cuponesService.getImporteDescuentoCupon(cupon);
				if(importeCupon != null) {
					log.debug("separaCupones() - " + cupon + " es un cupón de promoción con importe " + importeCupon);
					CustomerCouponDTO cuponCliente = new CustomerCouponDTO(cupon, false, CustomerCouponDTO.CUPON_ANTIGUO);
					cuponCliente.setBalance(importeCupon);
					cupones95.add(cuponCliente);
				}
				else {
					log.debug("separaCupones() - " + cupon + " es un flyer");
					flyers.add(cupon);
				}
			}
		}
		
		// Ordenamos los cupones que empiecen por 95 para que se apliquen los de mayor importe 
		Comparator<CustomerCouponDTO> comparadorCupones95 = new Comparator<CustomerCouponDTO>(){
			@Override
            public int compare(CustomerCouponDTO o1, CustomerCouponDTO o2) {
	            return o2.getBalance().compareTo(o1.getBalance());
            }
		};
		Collections.sort(cupones95, comparadorCupones95);
		
		log.debug("separarCupones() - Orden de aplicación de los cupones de promoción: " + cupones95);
		log.debug("separarCupones() - Orden de aplicación de los flyers: " + flyers);
	}

	protected void mostrarResultadoAplicacionCupones(List<CustomerCouponDTO> cuponesLeidos) {
		List<String> codigosCuponesLeidos = new ArrayList<String>();
		for(CustomerCouponDTO cuponLeido : cuponesLeidos) {
			codigosCuponesLeidos.add(cuponLeido.getCouponCode());
		}

		List<String> cuponesAplicados = new ArrayList<String>();
		List<String> cuponesNoAplicados = new ArrayList<String>();
		for(CustomerCouponDTO cupon : cuponesLeidos) {
			cuponesNoAplicados.add(cupon.getCouponCode());
		}
		
		ITicket ticket = ticketManager.getTicket();
		for (PromocionTicket promocion : (List<PromocionTicket>) ticket.getPromociones()) {
			String codigo = promocion.getCodAcceso();
			if(StringUtils.isNotBlank(codigo) && promocion.getAcceso().equals("CUPON")) {
				if (codigosCuponesLeidos.contains(codigo)) {
					cuponesAplicados.add(codigo);
					cuponesNoAplicados.remove(codigo);
				}
			}
		}
		
		for (CuponAplicadoTicket cuponAplicado : (List<CuponAplicadoTicket>) ticket.getCuponesAplicados()) {
			String codigo = cuponAplicado.getCodigo();
			String tipoCupon = cuponAplicado.getTipoCupon();
			if ("FLYER".equals(tipoCupon) && codigosCuponesLeidos.contains(codigo)) {
				cuponesAplicados.add(codigo);
				cuponesNoAplicados.remove(codigo);
			}
		}		

		log.debug("mostrarResultadoAplicacionCupones() - Resultado final de la aplicación de cupones.");
		log.debug("mostrarResultadoAplicacionCupones() - Cupones aplicados: " + cuponesAplicados);
		log.debug("mostrarResultadoAplicacionCupones() - Cupones no aplicados: " + cuponesNoAplicados);

		String mensaje = "";

		if (!cuponesNoAplicados.isEmpty()) {
			mensaje = mensaje + I18N.getTexto("No se han podido aplicar los siguientes cupones: ") + System.lineSeparator() + System.lineSeparator();
			for (String cupon : cuponesNoAplicados) {
				mensaje = mensaje + " · " + cupon + System.lineSeparator();
			}
		}

		if (StringUtils.isNotBlank(mensaje)) {
			if (!(Dispositivos.getInstance().getVisor() instanceof VisorNoConfig)) {
				((DinoVisorPantallaSecundaria) visor).modoInfoCupones(cuponesAplicados, cuponesNoAplicados);
			}
			VentanaDialogoComponent.crearVentanaInfo(mensaje, getStage());
		}
	}

	/**
	 * Nos envía a la pantalla de "Envíos a domicilio", pasandole el "ticketManager".
	 */
	public void irPantallaEnvioDomicilio() {
		DinoCabeceraTicket cabecera = (DinoCabeceraTicket) ticketManager.getTicket().getCabecera();
		if(cabecera.getDomicilioResponseBusquedaEnvioDomicilio() != null && cabecera.getDomicilioResponseBusquedaEnvioDomicilio() instanceof DomicilioOfflineDto) {
			cabecera.setDomicilioResponseBusquedaEnvioDomicilio(null);
		}
		
		HashMap<String, Object> params = new HashMap<>();
		
		params.put(EnvioDomicilioKeys.TICKET_VENTA, ticketManager);
	    if(idPedidoSAD != null){
	    	params.put(EnvioDomicilioKeys.CODPEDIDO, idPedidoSAD);
	    }
	    
	    /* Iniciamos los servicios de Sherpa para poder cargar el usuario*/
    	cargarServiciosSherpa();
	    
	    /* Cargamos los datos del usuario de Sherpa */
	    if(ticketManager.getTicket().getCabecera().getDatosFidelizado() != null){
	    	new ConsultarUsuarioSherpaTask(ticketManager.getTicket().getCabecera().getDatosFidelizado(), params).start();
	    }
	    else{	    	
	    	getApplication().getMainView().showModal(EnvioDomicilioView.class, params);
	    	
	    	if(params.containsKey(EnvioDomicilioKeys.IR_PAGOS) || (!params.containsKey(EnvioDomicilioController.PARAM_CANCELAR) && !params.containsKey(PagosController.ACCION_CANCELAR))) {
	    		abrirPagosEstandar();
	    	}
	    }
	}

	private class ConsultarUsuarioSherpaTask extends BackgroundTask<DomicilioResponse> {
		
		private FidelizacionBean fidelizado;
		
		private HashMap<String, Object> params;
		
		public ConsultarUsuarioSherpaTask(FidelizacionBean fidelizado, HashMap<String, Object> params) {
			super();
			this.fidelizado = fidelizado;
			this.params = params;
		}
		@Override
		protected DomicilioResponse call() {
			String numTarjetaFidelizado = fidelizado.getNumTarjetaFidelizado();
			log.debug("ConsultarUsuarioSherpaTask() - Iniciamos la búsqueda del usuario con código " + numTarjetaFidelizado);
			
			DomicilioResponse respuesta = null;
			try{
				String posType = SherpaApiBuilder.getSherpaPosType();
				String shop = sesion.getAplicacion().getCodAlmacen();
				String tpv = sesion.getAplicacion().getCodCaja();
				respuesta = domicilioService.getDomicilio(posType, shop, tpv, numTarjetaFidelizado);
			}
			catch(Exception e){
				
				if(e instanceof RetryableException) {
					throw e;
				}
				
				log.error("call() - Ha habido un error al consultar el domicilio: " + e.getMessage(), e);
				/* Significa que no lo encuentra */
				if(e.getMessage().contains("404")){
					respuesta = new DomicilioResponse();
				}
			}
			return respuesta;
		}
		@Override
		protected void succeeded() {
			super.succeeded();
			DomicilioResponse domicilio = getValue();
			log.debug("ConsultarUsuarioSherpaTask() - El resultado de la consulta es : " + domicilio);
			
			if(domicilio != null && domicilio.getPersonData() != null){
				params.put(EnvioDomicilioKeys.DOMICILIOS, domicilio);
				params.put(EnvioDomicilioKeys.FIDELIZADO_NOMBRE, fidelizado.getNombre() + " " + fidelizado.getApellido());
				params.put(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO, fidelizado.getDocumento());
			}
			getApplication().getMainView().showModal(EnvioDomicilioView.class, params);
	    	
	    	if(!params.containsKey(EnvioDomicilioController.PARAM_CANCELAR) && !params.containsKey(PagosController.ACCION_CANCELAR)) {
	    		abrirPagosEstandar();
	    	}
		}
		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			String mensajeError = "Se ha producido un error consultando los datos del usuario en Servicio a Domicilio";
			log.error("ConsultarUsuarioSherpaTask() - " + exception.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getApplication().getMainView().getStage());
			
			if(exception instanceof RetryableException) {
				params.put(AltaUsuarioSADController.PARAM_OFFLINE, true);
				getApplication().getMainView().showModal(AltaUsuarioSADView.class, params);
				
				getApplication().getMainView().showModal(EnvioDomicilioView.class, params);
				
				if(!params.containsKey(EnvioDomicilioController.PARAM_CANCELAR) && !params.containsKey(PagosController.ACCION_CANCELAR)) {
		    		abrirPagosEstandar();
		    	}
			}
			
			return;
		}
	}
	
	/**
	 * Realiza la carga de los servicios necesarios en la clase para consultar en Sherpa.
	 * @throws RutasServiciosException 
	 */
	public void cargarServiciosSherpa() {
		domicilioService = SherpaApiBuilder.getSherpaDomicilioApiService();		
	}
	
	/**
	 * Comprueba si está el artículo de tipo Porte incluido en el ticket.
	 * @return Boolean
	 */
	public Boolean tienePorte(){
		Boolean tiene = false;
		for (LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			/* Comprobaciones de null por si el dispositivo no está configurado correctamente */
			if(Dispositivos.getInstance().getFidelizacion() != null){
				if(Dispositivos.getInstance().getFidelizacion() instanceof DinoFidelizacion){
					/* Comprobaciones de null por si el dispositivo no está configurado correctamente */
					if(((DinoFidelizacion)Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte() != null){
						if(((DinoFidelizacion)Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte().equals(linea.getArticulo().getCodArticulo())){
							tiene = true;
						}
					}
				}
			}
		}
		return tiene;
	}
	
	/**
	 * Comprobamos el permiso para saber si usar las restricciones o no.
	 * 
	 * @return venderSinRestricciones
	 */
	private Boolean comprobarPermisoRestricciones() {
		Boolean venderSinRestricciones = true;
		try {
			compruebaPermisos(PERMISO_RESTRICCIONES);
		}
		catch (SinPermisosException e) {
			log.debug("El usuario " + sesion.getSesionUsuario().getUsuario().getUsuario() + " no tiene permiso para vender sin restricción.");
			venderSinRestricciones = false;
		}
		return venderSinRestricciones;
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		super.initializeComponents();

		imgInfo.setVisible(false);
		imgInfo.setManaged(false);
		tfCantidadIntro.setFocusTraversable(false);
		tfCantidadIntro.setEditable(false);
		tfCantidadIntro.setDisable(true);
		tbLineas.setFocusTraversable(false);

		addListenerBotonPortes();
		
		String tipoParking = variablesServices.getVariableAsString(ID_VARIABLE_TIPO_PARKING);
		if(StringUtils.isBlank(tipoParking)) {
			botonera.getBotonClave("introducirArticuloParking").setDisable(true);
		}
		else {
			botonera.getBotonClave("introducirArticuloParking").setDisable(false);
		}
		
		tbLineas.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		        setHeaderClickListeners();
            }
		});
		
		KeyboardDataDto keyboardDataDto = new KeyboardDataDto();
		keyboardDataDto.setMostrar(false);
		tfCodigoIntro.setUserData(keyboardDataDto);
	}

	@Override
	public void accionVerFidelizado() {
		log.error("accionVerFidelizado() - Pantalla inhabilitada para Dinosol");
	}

	public void consultarSaldoTarjeta() {
		getApplication().getMainView().showModalCentered(DinoSaldoTarjetasView.class, getStage());
	}

	/**
	 * Inserta un artículo "Porte" para indicar que se desea realizar un envío a domicilio.
	 */
	public void modificarPortes() {
		try{
			String codArticuloPortes = null;
			if(((DinoFidelizacion)Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte() != null){
				codArticuloPortes = ((DinoFidelizacion)Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte();
			}else{
				String mensajeAviso = "El dispositivo de Fidelización no está configurado correctamente";
				log.info("modificarPortes() - " + mensajeAviso);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeAviso), getStage());
				return;
			}
	
			List<Integer> lineasDePorte = new ArrayList<Integer>();
			for (Object lineaTicket : ticketManager.getTicket().getLineas()) {
				LineaTicket linea = (LineaTicket) lineaTicket;
				if (linea.getCodArticulo().equals(codArticuloPortes)) {
					lineasDePorte.add(linea.getIdLinea());
				}
			}
	
			if (lineasDePorte != null && lineasDePorte.isEmpty()) {
				addItem(codArticuloPortes);
			}
			else {
				for (Integer idLinea : lineasDePorte) {
					ticketManager.eliminarLineaArticulo(idLinea);
					refrescarDatosPantalla();
				}
			}
		}catch(Exception e){
			String mensajeError = "El dispositivo de Fidelización no está configurado correctamente";
			log.error("modificarPortes() - " + mensajeError);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}
	}

	@Override
	protected boolean accionValidarFormulario() {
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
	
	public synchronized LineaTicket insertarLineaVenta(String sCodart, String sDesglose1, String sDesglose2, BigDecimal nCantidad) throws LineaTicketException, PromocionesServiceException,
	        DocumentoException, CajasServiceException, CajaRetiradaEfectivoException {
		
		if(compruebaCajonAbierto()) {
			return null;
		}
		
		LineaTicket nuevaLinea = null;
		
		try {
			
			AnulacionArticuloDto precioAnulacion = null;
			if(modoAnulacion) {
				precioAnulacion = buscarPrecioAnulacion(sCodart, nCantidad);
				nCantidad = precioAnulacion.getCantidad().negate();
			}
			
			if (ticketManager.getTicket().getLineas().isEmpty()) {
				// Es la primera linea así que llamamos a nuevoTicket()
				log.debug("insertarLineaVenta() - Se inserta la primera línea al ticket por lo que inicializamos nuevo ticket");
	
				ClienteBean cliente = ticketManager.getTicket().getCliente();
				FidelizacionBean tarjeta = ticketManager.getTicket().getCabecera().getDatosFidelizado();
				List<TarjetaIdentificacionDto> tarjetasIdentificacion = null;
				if(ticketManager.getTicket() != null) {
					tarjetasIdentificacion = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getTarjetasIdentificacion();
				}
				crearNuevoTicket();
				ticketManager.getTicket().setCliente(cliente);
				ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
				if(tarjetasIdentificacion != null) {
					((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setTarjetasIdentificacion(tarjetasIdentificacion);
				}
			}
	
			try {
				Stage stage = getStage();
				if(modoAnulacion && serviciosRepartoService.getServiciosReparto() == null) {
					stage = null;
				}
				nuevaLinea = ((DinoTicketManager) ticketManager).nuevaLineaArticulo(sCodart, sDesglose1, sDesglose2, nCantidad, stage, null, false);
				
				if (modoAnulacion && !(nuevaLinea instanceof LecturaQrBalanzaDto)) {
					BigDecimal cantidad = nuevaLinea.getCantidad();
					if(BigDecimalUtil.isMayorACero(cantidad)) {
						nuevaLinea.setCantidad(cantidad.negate());
					}
					
				    if(precioAnulacion != null && precioAnulacion.getPrecio() != null) {
				    	if(tarjetasRegaloService.getTipoTarjeta(sCodart) == null) {
							if (precioAnulacion.getPrecioModificadoEnOrigen()) {
								nuevaLinea.resetPromociones();
								nuevaLinea.setAdmitePromociones(false);
							}
	
							nuevaLinea.setPrecioSinDto(precioAnulacion.getPrecio());
							nuevaLinea.setPrecioTotalSinDto(precioAnulacion.getPrecio());
	
							nuevaLinea.recalcularImporteFinal();
				    	}
				    } else {
					   nuevaLinea.recalcularPreciosImportes();
					}

				}
				ticketManager.recalcularConPromociones();
			}
			catch (LineaTicketException e) {
				if(e instanceof LineaPlasticoInexistenteException) {
					VentanaDialogoComponent.crearVentanaAviso(e.getMessage(), getStage());
				}
				else {
					/* Reproducimos el sonido de error */
					reproducirSonidoError();
					throw new LineaTicketException(e);
				}
			}
	
			boolean esTipoEspecial = nuevaLinea instanceof LecturaTarjetaBpDto || nuevaLinea instanceof LecturaQrBalanzaDto || nuevaLinea instanceof LecturaCuponImporteDto || nuevaLinea instanceof LecturaQRLiquidacionDto;
			if (!esTipoEspecial && !validarLinea(nuevaLinea)) {
				ticketManager.eliminarLineaArticulo(nuevaLinea.getIdLinea());
				return null;
			}
	
			guardarCopiaSeguridad();
			/* En caso no haber errores, reproducimos el sonido */
			reproducirSonido();
		}
		finally {
			if(modoAnulacion) {
				activarModoAnulacion(false);
			}
			if(modoLecturaQrLiquidacion) {
				activarLecturaQrLiquidacion(false);
			}
		}

		if(apSolicitarQr.isVisible()) {
			apSolicitarQr.setVisible(false);
		}
		return nuevaLinea;
	}

	private void reproducirSonido() {
		if(!insertandoCupon) {
			try {
				if (clip == null) {
					ClassPathResource resource = new ClassPathResource("beep.wav");
				
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(resource.getFile()));					
				}
				clip.setFramePosition(0);
 				clip.start();				
			}
			catch (Exception e) {
				log.error("reproducirSonido() - Ha habido un problema al reproducir el WAV: " + e.getMessage(), e);
			}
		}
	}
	

	private void reproducirSonidoError() {
		if(!insertandoCupon) {
			try {
				if (clipError == null) {
					ClassPathResource resource = new ClassPathResource("error.wav");
				
					clipError = AudioSystem.getClip();
					clipError.open(AudioSystem.getAudioInputStream(resource.getFile()));					
				}
				clipError.setFramePosition(0);
				clipError.start();				
			}
			catch (Exception e) {
				log.error("reproducirSonidoError() - Ha habido un problema al reproducir el WAV: " + e.getMessage(), e);
			}
		}
	}

	@Override
	protected void nuevoArticuloTaskSucceeded(LineaTicket value) {
		if (value instanceof LecturaTarjetaBpDto) {
			tratarTarjetaBp(value);
			return;
		}
		else if (value instanceof LecturaQrBalanzaDto) {
			ticketManager.recalcularConPromociones();
			resetearCantidad();
			refrescarDatosPantalla();
			return;
		}
		else if (value instanceof LecturaCuponImporteDto) {
			refrescarDatosPantalla();
			return;
		}
		else if(value instanceof LecturaQRLiquidacionDto) {
			activarLecturaQrLiquidacion(true);
			return;
		}
		else if (value == null && ticketManager.isTicketVacio()) {
			refrescarDatosPantalla();
			return;
		}

		super.nuevoArticuloTaskSucceeded(value);
	}

	protected void tratarTarjetaBp(LineaTicket value) {
		new BackgroundTask<Void>(){

			@Override
            protected Void call() throws Exception {
				if(bpManager == null) {
		    		inicializarServicioBp();
		    	}
		    	
		        bpManager.getSaldo(((LecturaTarjetaBpDto) value).getNumeroTarjeta());
		        
	            return null;
            }
			
			@Override
			protected void failed() {
			    super.failed();
			    
			    Throwable e = getException();
			    if(e instanceof BpSoapException) {
			    	log.error("guardarTarjetaIdentificacionBp() - Ha habido un error al conectar con el servicio de BP para validar la tarjeta.");
			    	log.error("guardarTarjetaIdentificacionBp() - Se trabajará de forma offline.");
			    	log.error("guardarTarjetaIdentificacionBp() - Error: " + e.getMessage(), e);
			    	
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
			    	
			    	ticketManager.recalcularConPromociones();
			    }
			    else {
			    	log.error("guardarTarjetaIdentificacionBp() - Ha habido un error indeterminado al realizar la validación de la tarjeta BP.");
			    	log.error("guardarTarjetaIdentificacionBp() - No se admite la tarjeta.");
			    	log.error("guardarTarjetaIdentificacionBp() - Error: " + e.getMessage(), e);
			    	
			    	tratarErrorLecturaTarjetaBp(e);
			    }
			    
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
			
			@Override
			protected void succeeded() {
			    super.succeeded();
			    
			    ticketManager.recalcularConPromociones();
			    refrescarDatosPantalla();
			}
		}.start();
    }

	public void TraspasarCantidadToCasillaCantidad() {
		String strCantidad = tfCodigoIntro.getText().replace("*", "").trim();
		if (strCantidad.equals("")) {
			tfCodigoIntro.setText("");
			return;

		}
		try {
			Double intCantidad = Double.parseDouble(strCantidad.replace(",", "."));

			DecimalFormat df = new DecimalFormat("0.000");
			strCantidad = df.format(intCantidad).replace(".", ",");
			if(!("0,000").equals(strCantidad)){
				tfCantidadIntro.setText(strCantidad);
				tfCodigoIntro.setText("");
			}
		}
		catch (NumberFormatException e) {
			log.debug("Se ha pulsado * sin cantidad previa valida.");
		}

	}

	private void resetearCantidad() {
		tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
	}

	@Override
	public void actionTfCodigoIntro(KeyEvent event) {
		if ((event.getCode() == KeyCode.MULTIPLY) || (event.getCode() == KeyCode.EQUALS)) {
			// cambiarCantidad();
			TraspasarCantidadToCasillaCantidad();
		}
		else if (event.getCode() == KeyCode.ENTER) {
			nuevoCodigoArticulo();
		}
		else if (AppConfig.modoDesarrollo && (event.isAltDown() && event.isControlDown() && event.getCode() == KeyCode.T)) {
			generarTicketsAleatorios();
		}
	}

	@Override
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
			resetearCantidad();
			cambiarCantidad();
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
	        
	        if(ticketManager.getTicket() != null && !ticketManager.getTicket().getLineas().isEmpty()) {
	        	ticketManager.salvarTicketVacio();
				imprimirTicketVacio();
				
		        AuditoriaDto auditoria = new AuditoriaDto();
				auditoria.setTipo("ANULAR TICKET");
				auditoria.setUidTicket(((DinoTicketManager) ticketManager).getTicketVacio().getUidTicket());
				auditoriasService.guardarAuditoria(auditoria);
	        }
	        else {
	        	ticketManager.eliminarTicketCompleto();
	        }
			
			resetearCantidad();
			refrescarDatosPantalla();
			initializeFocus();
			tbLineas.getSelectionModel().clearSelection();

			visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
			visor.modoEspera();
		}
		catch (Exception ex) {
			log.error("accionAnularTicket() - Error inicializando nuevo ticket: " + ex.getMessage(), ex);
			VentanaDialogoComponent.crearVentanaError(getStage(), ex.getMessage(), ex);
		}
	}

	@Override
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

						if(linea != null) {
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
					}

					// Restauramos la cantidad en la pantalla
					resetearCantidad();
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

	/**
	 * Imprime el último ticket
	 * 
	 * @throws TicketsServiceException
	 * @throws InitializeGuiException
	 */
	public void imprimirUltimo() throws TicketsServiceException, InitializeGuiException {
		
		if(compruebaCajonAbierto()) {
			return;
		}

		boolean permisoParaImprimir = true;
		try {
			compruebaPermisos(PERMISO_IMPRIMIR_ULTIMO_TICKET); // DIN-113
		} catch (SinPermisosException e) {
			permisoParaImprimir = false;
		}
		
		try {
			TicketBean ultimoTicket = dinoTicketsService.consultarUltimoTicket();
			
			GestionTicketGui dto = new GestionTicketGui(ultimoTicket);
			
			HashMap<String, Object> datosTicket = new HashMap<String, Object>();
			ObservableList<GestionTicketGui> list = FXCollections.observableArrayList();
			list.add(dto);
            datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKETS, list);
            if(dto.getTicketXML()!=null){
            	datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKET_XML, dto.getTicketXML());
            }
            datosTicket.put(DinoDetalleGestionticketsController.PERMISO_IMPRIMIR_TICKETS, permisoParaImprimir);

            getApplication().getMainView().showModalCentered(DetalleGestionTicketView.class, datosTicket, this.getStage());
            
            AuditoriaDto auditoria = new AuditoriaDto();
			auditoria.setTipo("REIMPRESIÓN TICKET");
			auditoria.setUidTicket(dto.getUidTicket());
			auditoriasService.guardarAuditoria(auditoria);
		}
		catch (Exception e) {
			log.error("imprimirUltimo() - Error en la impresión del ticket: " + e.getMessage(), e);
		}
	}

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
		classes.add(SpringContext.getBean(TotalesTicketProfesional.class).getClass());

		return classes;
	}
	
	protected void accionNegarRegistroTabla() {
		activarModoAnulacion(!modoAnulacion);
	}
	
	protected void negarLineaSeleccionada(LineaTicketGui lineaSeleccionada) throws LineaTicketException {
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
	
	@Override
	public void refrescarDatosPantalla() {
		super.refrescarDatosPantalla();

		if (visor instanceof DinoVisorPantallaSecundaria && (DinoVisorPantallaSecundaria.getModo() != DinoVisorPantallaSecundaria.MODO_PARTICIPACIONES_ANIVERSARIO || !ticketManager.isTicketVacio())) {
			((DinoVisorPantallaSecundaria) visor).modoVenta(visorConverter.convert((TicketVentaAbono) ticketManager.getTicket()));
		}
		
		/* Al iniciar la pantalla debemos poner el icono de siempre */
		if(tienePorte()){
			cambiarBotonPortes("iconos/truck-checked.png");
		}else{
			cambiarBotonPortes("iconos/truck.png");
		}
		
		if(tieneParking()) {
			cambiarBotonParking("iconos/car-checked.png");
		}
		else {
			cambiarBotonParking("iconos/car.png");
		}
		
		TarjetaIdentificacionDto tarjeta = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).buscarTarjeta("BP");
 		if(tarjeta != null) {
			imgTarjetaDinoBP.setVisible(true);
		}
 		else {
 			imgTarjetaDinoBP.setVisible(false);
 		}
		
		TarjetaIdentificacionDto tarjetaEmpleados = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).buscarTarjeta("EMP");
 		if(tarjetaEmpleados != null) {
 			imgTarjetaEmpleado.setVisible(true);
		}
 		else {
 			imgTarjetaEmpleado.setVisible(false);
 		}
		
		pintarLabelCupones();
		
		//Si hay lineas, no volvemos a pintar la pantalla del QR
		if (ticketManager.getTicket().getLineas().isEmpty()) {
			apSolicitarQr.setVisible(true);
		}
		else {
			apSolicitarQr.setVisible(false);
		}
		
		if (ticketManager.getTicket().getCabecera().getDatosFidelizado() != null) {
			btBorrarFidelizado.setVisible(true);
		}
		else {
			btBorrarFidelizado.setVisible(false);
		}
		
		if(serviciosRepartoService.getServiciosReparto() != null) {
			btBorrarFidelizado.setVisible(false);
		}
		
		mostrarServicioRepartoSeleccionado();
	}

	protected void pintarLabelCupones() {
		int sizeCupones = 0;

		List<CustomerCouponDTO> cuponesLeidos = ((DinoTicketManager) ticketManager).getCuponesLeidos();
		if (cuponesLeidos != null && !cuponesLeidos.isEmpty()) {
			sizeCupones = cuponesLeidos.size();
		}

		if (sizeCupones > 0) {
			if (sizeCupones == 1) {
				lbCuponesLeidos.setText("Un cupón leído");
			}
			else {
				lbCuponesLeidos.setText(sizeCupones + " cupones leídos");
			}

			lbCuponesLeidos.setVisible(true);
		}
		else {
			lbCuponesLeidos.setVisible(false);
		}
	}

	public void recargasArticulos() {
		if(compruebaCajonAbierto()) {
			return;
		}
		getApplication().getMainView().showModalCentered(SeleccionArticuloRecargaView.class,  getDatos(), getStage());
		
		String codArticuloRecarga = (String) getDatos().get(SeleccionArticuloRecargaController.COD_ART_RECARGA);
		String importe = (String) getDatos().get(SeleccionArticuloRecargaController.CANTIDAD_RECARGA);
		
		if(StringUtils.isNotBlank(codArticuloRecarga) && StringUtils.isNotBlank(importe) && StringUtils.isNumeric(importe) && !BigDecimalUtil.isIgualACero(FormatUtil.getInstance().desformateaBigDecimal(importe))) {
			tfCodigoIntro.setText(codArticuloRecarga);
			tfCantidadIntro.setText(importe);
			nuevoCodigoArticulo();
		}
	}
	@Override
	public void nuevoCodigoArticulo() {
		
		// no dejar introducir líneas en un ticket nuevo si ha superado el importe de bloqueo de retirada
		if (tbLineas.getItems().size() == 0 && checkBloqueoRetirada()) {			
			tfCodigoIntro.clear();
			return;
		}
			
		// Validamos los datos
		String codigo = tfCodigoIntro.getText();
		if (!codigo.trim().isEmpty()) {
			
			if(StringUtils.isNumeric(codigo) && !prefijosTarjetasService.isTarjetaBp(codigo)) {
				codigo = new BigInteger(codigo).toString();
			}
			
			String[] split = null;
			if(Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(codigo)) {
				try {
					log.debug("nuevoCodigoArticulo() - Procesando QR de fidelizado.");
					
					log.debug("nuevoCodigoArticulo() - QR leído: " + codigo);
					String numeroTarjetaDesencriptado = CryptoUtils.decrypt(codigo, LocatorManagerImpl.secretKey);
					log.debug("nuevoCodigoArticulo() - QR desencriptado: " + numeroTarjetaDesencriptado);
					
					split = numeroTarjetaDesencriptado.split("-");
					
					Long timestampInSecondsQR = new Long(split[1]);
					log.debug("nuevoCodigoArticulo() - Timestamp de vigencia del QR: " + timestampInSecondsQR);
					
					if(!timestampInSecondsQR.equals(0L)) {
						Long timestampInSecondsNow = System.currentTimeMillis() / 1000L;
						VariablesServices variablesServices = SpringContext.getBean(VariablesServices.class);
						Integer minutosPermitidos = variablesServices.getVariableAsInteger(ID_VARIABLE_TIMESTAMP);
						if(minutosPermitidos > 0) {
							Long diferenciaEnSegundos = timestampInSecondsNow - timestampInSecondsQR;
							int segundosPermitidos = (minutosPermitidos * 60);
							if(diferenciaEnSegundos > segundosPermitidos) {
								tfCodigoIntro.clear();
								VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Este código ya no es válido. Por favor, renueve su código."), getStage());
								return;
							}
						}
					}
				}
				catch (Exception e) {
					log.error("nuevoCodigoArticulo() - Ha habido un error al leer la tarjeta de fidelizado: " + e.getMessage(), e);
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Este código ya no es válido. Por favor, renueve su código."), getStage());
					return;
				}
			}
			
			if(cuponesService.isCupon(codigo.trim())) {
				log.debug("nuevoCodigoArticulo() - El código " + codigo + " es un código de cupón. Se guardará para aplicarlo posteriormente al pasar a la pantalla de pagos.");
				
				boolean cuponValido = true;
				if(codigo.startsWith("21")) {
					cuponValido = new EAN13CheckDigit().isValid(codigo);
				}
				
				if(cuponValido) {
					reproducirSonidoEspecial();
				}
				else {
					reproducirSonidoError();
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El CUPÓN introducido no es válido."), getStage());
					tfCodigoIntro.clear();
					return;
				}
			}
			
			
			log.debug("nuevoCodigoArticulo() - Creando línea de artículo");

			frValidacion.setCantidad(tfCantidadIntro.getText().trim());
			frValidacion.setCodArticulo(codigo.trim());
			BigDecimal cantidad = frValidacion.getCantidadAsBigDecimal();
			tfCodigoIntro.clear();

			if (accionValidarFormulario() && cantidad != null && !BigDecimalUtil.isIgualACero(cantidad)) {
				log.debug("nuevoCodigoArticulo()- Formulario validado");

				// Si es prefijo de tarjeta fidelizacion, marcamos la venta como fidelizado y llamamos al REST
				if (Dispositivos.getInstance().getFidelizacion().isPrefijoTarjeta(frValidacion.getCodArticulo())) {
					ticketManager.recalcularConPromociones();
					refrescarDatosPantalla();

					final String[] splitFinal = split;
					Dispositivos.getInstance().getFidelizacion().cargarTarjetaFidelizado(frValidacion.getCodArticulo(), getStage(), new DispositivoCallback<FidelizacionBean>(){

						@Override
						public void onSuccess(FidelizacionBean tarjeta) {
							if (tarjeta.isBaja()) {
								VentanaDialogoComponent.crearVentanaError(I18N.getTexto("La tarjeta de fidelización {0} no está activa", tarjeta.getNumTarjetaFidelizado()), getStage());
								tarjeta = null;
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							}
							else {
								borrarCuponesFidelizadoAnterior();								
								
								// Tarjeta válida - lo seteamos en el ticket
								ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
								
								leerSegmentoQr(splitFinal);
								
								guardarCupones(tarjeta);
							}

							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}

						protected void leerSegmentoQr(final String[] splitFinal) {
							try {
								List<String> colectivos = ticketManager.getTicket().getCabecera().getDatosFidelizado().getCodColectivos();
								
								if (splitFinal != null && splitFinal.length == 5 && colectivos != null) {
									String segmento = splitFinal[2];
									
									Long timestampInSecondsQR = new Long(splitFinal[3]);
									Integer minutosPermitidos = variablesServices.getVariableAsInteger(ID_VARIABLE_TIMESTAMP_SEGMENTO);
									
									String codAlmQr = splitFinal[4];
									
									log.debug("leerSegmentoQr() - Segmento leído del QR: " + segmento);
									log.debug("leerSegmentoQr() - Timestamp del segmento leído del QR: " + timestampInSecondsQR);
									log.debug("leerSegmentoQr() - Minutos permitidos para la lectura del segmento (variable de base de datos): " + minutosPermitidos);
									log.debug("leerSegmentoQr() - Almacen del segmento leído del QR: " + codAlmQr);
									
									if (StringUtils.isBlank(segmento)) {
										log.error("leerSegmentoQr() - El segmento del QR está en blanco.");
										return;
									}
									
									if (!timestampInSecondsQR.equals(0L) && minutosPermitidos > 0) {
										Long timestampInSecondsNow = System.currentTimeMillis() / 1000L;
										Long diferenciaEnSegundos = timestampInSecondsNow - timestampInSecondsQR;
										int segundosPermitidos = (minutosPermitidos * 60);
										if (diferenciaEnSegundos > segundosPermitidos) {
											log.warn("leerSegmentoQr() - El segmento asociado ha pasado el tiempo máximo de vigencia, no se añadirá");
											VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("QR NO VÁLIDO EN ESTA FRANJA HORARIA"), getStage());
											return;
										}
										if(diferenciaEnSegundos < 0) {
											log.warn("leerSegmentoQr() - El segmento asociado todavía no está vigente, no se añadirá");
											VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("QR NO VÁLIDO EN ESTA FRANJA HORARIA"), getStage());
											return;
										}
									}
									
									if (!sesion.getAplicacion().getCodAlmacen().equals(codAlmQr) && !codAlmQr.equals("9999")) {
										log.warn("leerSegmentoQr() - El segmento asociado no tiene asociado este almacén en el QR");
										VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("QR NO VÁLIDO EN ESTA TIENDA"), getStage());
										return;
									}
									
									log.debug("leerSegmentoQr() - Se añadirá el segmento " + segmento + " al fidelizado.");
									colectivos.add(segmento);
									ticketManager.recalcularConPromociones();
								}
							}
							catch (Exception e) {
								log.error("leerSegmentoQr() - Ha habido un error al leer el segmento del código QR: " + e.getMessage(), e);
							}
						}

						@Override
						public void onFailure(Throwable e) {
							// Los errores se muestran desde el código del dispositivo
							// Quitamos los datos de fidelizado
							FidelizacionBean tarjeta = null;
							ticketManager.getTicket().getCabecera().setDatosFidelizado(tarjeta);
							borrarCuponesFidelizadoAnterior();
							ticketManager.recalcularConPromociones();
							refrescarDatosPantalla();
						}
					});
					return;
				}
				
				NuevoCodigoArticuloTask taskArticulo = SpringContext.getBean(NuevoCodigoArticuloTask.class, this, frValidacion.getCodArticulo().toUpperCase(), cantidad);                                                                                                                                // anidada
				taskArticulo.start();
			}
		}
	}
	
	private void reproducirSonidoEspecial() {
		if(!insertandoCupon) {
			try {
				if (clipEspecial == null) {
					ClassPathResource resource = new ClassPathResource("especial.wav");
				
					clipEspecial = AudioSystem.getClip();
					clipEspecial.open(AudioSystem.getAudioInputStream(resource.getFile()));					
				}
				clipEspecial.setFramePosition(0);
				clipEspecial.start();				
			}
			catch (Exception e) {
				log.error("reproducirSonido() - Ha habido un problema al reproducir el WAV: " + e.getMessage(), e);
			}
		}
    }

	public void cambiarCantidad() {
		log.debug("cambiarCantidad() - preparamos la interfaz para una modificación de la cantidad");
		tfCodigoIntro.setText(tfCodigoIntro.getText().replace("*", ""));
		BigDecimal cantidad = FormatUtil.getInstance().desformateaBigDecimal(tfCodigoIntro.getText(),4);
		
		if(BigDecimalUtil.isIgualACero(cantidad)){
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Cantidad 0 no permitida"), getStage());
			tfCodigoIntro.setText("");
		}
		
		if(BigDecimalUtil.isMayor(cantidad, new BigDecimal(9999))){
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Cantidad mayor de 9999 no permitida"), getStage());
			tfCodigoIntro.setText("");
			tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
		}
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				tfCantidadIntro.requestFocus();
			}
		});
		tfCantidadIntro.selectAll();
	}
	
	private void inicializarServicioBp() {
		PaymentMethodManager manager = null;
		/* Recorremos los medios de pago con configuración */
		for (PaymentMethodConfiguration configuration : paymentsMethodsConfiguration.getPaymentsMethodsConfiguration()) {
			log.debug("inicializarServicioBp() - Comprobando la configuración de los medios de pago");
			/* Ignoramos los medios de pago no configurados */
			if (!configuration.getConfigurationProperties().isEmpty()) {
				/* Sacamos la configuración del medio de pago, comprobando el manager */
				if (configuration.getManager() != null) {
					try {
						if (configuration.getManager() instanceof BPManager) {
							manager = configuration.getManager();
							log.debug("inicializarServicioBp() - Configuración cargada : " + manager.getPaymentCode());
						}
					}
					catch (Exception e) {
						String mensajeError = "Error al cargar la configuración del medio de pago BP";
						log.error("inicializarServicioBp() - " + mensajeError + " " + configuration.getPaymentCode() + " : " + e.getMessage());
					}
				}
			}
		}

		/* En caso de no tener el manager configurado, deberemos devolver un error */
		if (manager != null) {
			bpManager = (BPManager) manager;
			try {
				bpManager.initialize();
				log.debug("inicializarServicioBp() - Creando ticket para poder realizar la carga del salgo");
				TicketManager ticketManager = SpringContext.getBean(TicketManager.class);
				ticketManager.nuevoTicket();
				bpManager.setTicketData(ticketManager.getTicket(), null);
			}
			catch (Exception e) {
				String mensajeError = "Error al inicializar la pantalla";
				log.error("inicializarServicioBp() - : " + mensajeError + " " + e.getMessage(), e);
			}
		}
		else {
			String mensajeError = "No se ha encontrado configuración para BP";
			log.error("inicializarServicioBp() - " + mensajeError);
		}
	}
	
	@Override
	protected List<ConfiguracionBotonBean> cargarAccionesTabla() {
		List<ConfiguracionBotonBean> listaAcciones = new ArrayList<>();
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", "REALIZAR_ACCION")); // "Home"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", "REALIZAR_ACCION")); // "Page Up"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", "REALIZAR_ACCION")); // "Page Down"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", "REALIZAR_ACCION")); // "End"
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_edit.png", null, null, "ACCION_TABLA_EDITAR_REGISTRO", "REALIZAR_ACCION"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/row_neg.png", null, null, "ACCION_TABLA_NEGAR_REGISTRO", "REALIZAR_ACCION")); // "Num Pad -"
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean(null, null, null, null, "HUECO"));
		listaAcciones.add(new ConfiguracionBotonBean("iconos/zoom.png", null, null, "ABRIR_BUSQUEDA_ARTICULOS", "REALIZAR_ACCION"));
		return listaAcciones;
	}
	
	protected void anularLineaSeleccionada(LineaTicketGui lineaSeleccionada){
		int idLinea = lineaSeleccionada.getIdLinea();
		LineaTicket linea = (LineaTicket) ticketManager.getTicket().getLinea(idLinea);
		if (linea.isEditable()) {
			if(BigDecimalUtil.isIgualACero(linea.getCantidad())) {
				linea.setCantidad(BigDecimal.ONE);
			}else {
				linea.setCantidad(BigDecimal.ZERO);
			}
	        	        
	        ticketManager.recalcularConPromociones();
	        escribirLineaEnVisor(linea);
			guardarCopiaSeguridad();
			resetearCantidad();
			refrescarDatosPantalla();
			visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
		}
		else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La línea no se puede modificar."), this.getStage());
		}
	}
	
	@Override
	protected void cerrarPantallaPagos() {
	    super.cerrarPantallaPagos();
	    
	    limpiarCupones();
	    
	    limpiarOpcionPromocionesSeleccionada();
	}

	protected void limpiarCupones() {
		if(ticketManager.getTicket() != null) {
			ticketManager.getTicket().getPagos().clear();
		    ticketManager.getTicket().getCuponesAplicados().clear();
		    ticketManager.recalcularConPromociones();
		    refrescarDatosPantalla();
		}
    }
	
	@Override
	public void comprobarPermisosUI() {
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
	}
	
	public void introducirArticuloParking() {	
		String codartParking = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_PARKING);
		if(StringUtils.isBlank(codartParking)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado correctamente el artículo del parking. Contacte con el administrador."), getStage());
			return;
		}
		String codartExtravio = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_EXTRAVIO);
		if(StringUtils.isBlank(codartExtravio)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado correctamente el artículo de extravío del ticket del parking. Contacte con el administrador."), getStage());
			return;
		}
		
		List<Integer> lineasParking = new ArrayList<Integer>();
		for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			String codArticulo = linea.getCodArticulo();
			if(codArticulo.equals(codartParking) || codArticulo.equals(codartExtravio)) {
				lineasParking.add(linea.getIdLinea());
			}
		}
		
		if(!lineasParking.isEmpty()) {
			for(Integer idLinea : lineasParking) {
				ticketManager.eliminarLineaArticulo(idLinea);
			}
			
			refrescarDatosPantalla();
			
			return;
		}
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(FacturacionArticulosController.TICKET_KEY, ticketManager.getTicket());
		
		getApplication().getMainView().showModalCentered(TicketParkingView.class, params, getStage());
		
		if(params.containsKey(TicketParkingController.PARAM_GENERAR_QR)) {
			((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setGenerarQrParking(true);
		}
		else {
			Integer minutos = (Integer) params.get(TicketParkingController.PARAM_MINUTOS_PARKING);
			String codart = (String) params.get(TicketParkingController.PARAM_ARTICULO_PARKING);
			String codigo = (String) params.get(TicketParkingController.PARAM_CODIGO_SALIDA);
			String horaSalida = (String) params.get(TicketParkingController.PARAM_HORA_SALIDA);
			
			if(minutos != null && codart != null && codigo != null) {
				((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setCodigoParking(codigo);
				((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setHoraSalidaParking(horaSalida);
				
				tfCantidadIntro.setText(minutos.toString());
				tfCodigoIntro.setText(codart);
				
				nuevoCodigoArticulo();
			}
		}
	}
	
	private void cambiarBotonParking(String imagen) {
		BotonBotoneraComponent boton = botonera.getBotonBotonera("introducirArticuloParking");
		if(boton instanceof BotonBotoneraNormalComponent) {
			((BotonBotoneraNormalComponent) boton).cambiarImagen(imagen);
		}
    }

	private boolean tieneParking() {
		String codartParking = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_PARKING);
		String codartExtravio = variablesServices.getVariableAsString(TicketParkingController.ID_VARIABLE_CODART_EXTRAVIO);
		
		List<Integer> lineasParking = new ArrayList<Integer>();
		for(LineaTicket linea : (List<LineaTicket>) ticketManager.getTicket().getLineas()) {
			String codArticulo = linea.getCodArticulo();
			if(codArticulo.equals(codartParking) || codArticulo.equals(codartExtravio)) {
				lineasParking.add(linea.getIdLinea());
			}
		}
		
		if(!lineasParking.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
    }
	
	@Override
	public boolean canClose() {
		if(!ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			if (ticketManager.countTicketsAparcados() > 0) {
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
		}
		
		int numLineas = tbLineas.getItems().size();
		if(numLineas > 0 ){
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
	
	@Override
	protected void actualizarTicketsAparcados() {
		if (!ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			log.debug("actualizarTicketsAparcados() - Consultando los tickets aparcados en la base de datos local.");
			super.actualizarTicketsAparcados();
		}
		else {
			log.debug("actualizarTicketsAparcados() - Consultando los tickets aparcados en la caja central");

			Task<List<TicketAparcadoBean>> taskComprobacionAparcados = new Task<List<TicketAparcadoBean>>(){
				
				@Override
				protected List<TicketAparcadoBean> call() throws Exception {
					return ticketsAparcadosService.consultarTickets(null, null, null, ticketManager.getDocumentoActivo().getIdTipoDocumento());
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					String usuario = sesion.getSesionCaja().getCajaAbierta().getUsuario();

					List<TicketAparcadoBean> tickets = getValue();
					int numTickets = 0;
					for(TicketAparcadoBean ticket : tickets) {
						if(ticket.getUsuario().equals(usuario)) {
							numTickets++;
						}
					}
					
					if (numTickets > 0) {
						if (numTickets == 1) {
							lbStatusTicketsAparcados.setText(I18N.getTexto("Tiene un Ticket Aparcado"));
						}
						else {
							lbStatusTicketsAparcados.setText(I18N.getTexto("Tiene") + " (" + numTickets + ") " + "Tickets Aparcados");
						}
						lbimagenTicketsAparcados.setVisible(true);
						lbStatusTicketsAparcados.setVisible(true);
					}
					else {
						lbStatusTicketsAparcados.setVisible(false);
						lbimagenTicketsAparcados.setVisible(false);
					}
				}

				@Override
				protected void failed() {
					super.failed();
					log.error("actualizarTicketsAparcados() - Error al consultar los tickets aparcados en la caja central");
					lbStatusTicketsAparcados.setVisible(false);
					lbimagenTicketsAparcados.setVisible(false);
				}
			};

			ExecutorService executor = Executors.newFixedThreadPool(1);
			executor.execute(taskComprobacionAparcados);
		}
	}
	
	@Override
	public void aparcarTicket() {
		if(!ticketsAparcadosService.isTicketAparcadoRemotoActivo()) {
			super.aparcarTicket();
		}
		else {
			log.debug("aparcarTicket()");
			if (!ticketManager.isTicketVacio()) { // Si el ticket no es vacío se puede aparcar
				log.debug("accionAparcarTicket()");

				// Se borra la copia de seguridad para que no de fallos de violación de claves al guardar el ticket
				// aparcado en la misma tabla
				try {
					copiaSeguridadTicketService.guardarBackupTicketActivo(new TicketVentaAbono());
				}
				catch (TicketsServiceException ex) {
					log.error("accionAparcarTicket() - Ha habido un error al guardar la copia de seguridad: " + ex.getMessage(), ex);
				}
				
				new BackgroundTask<Void>(){
					@Override
	                protected Void call() throws Exception {
						ticketManager.aparcarTicket();
						return null;
	                }
					
					@Override
					protected void succeeded() {
						super.succeeded();
						
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

					
					@Override
					protected void failed() {
					    super.failed();
					    
					    Throwable e = getException();
		                String message = e.getMessage();
						log.error("aparcarTicket() - Error aparcando ticket: " + message, e);
		                
						message = I18N.getTexto("Ha habido un problema al aparcar el ticket en la caja máster: ");
		                if(e.getCause() instanceof RestException) {
		                	message = message + e.getCause().getCause().getMessage();
		                }
		                
		                if(e.getCause() instanceof TicketsMaximoAparcadosException) {
		                	message = I18N.getTexto("No se pueden aparcar más de dos tickets por cajero el mismo día.");
		                }
		                
		                if(e.getCause() instanceof TicketConIdAparcadoException) {
		                	message = I18N.getTexto("Esta venta ya tiene identificador asignado. Tendrá que anularla o completar los pagos.");
		                }
		                
		                if(e.getCause().getCause() != null && e.getCause().getCause().getCause() instanceof ConnectException) {
		                	message = I18N.getTexto("No se ha podido establecer la comunicación con la caja máster.");
		                }		                

		                VentanaDialogoComponent.crearVentanaError(message, getStage());
						
		                // Se vuelve a guardar la copia de seguridad ya que se ha borrado antes de aparcar el ticket
		                ticketManager.guardarCopiaSeguridadTicket();
					}
					
				}.start();
			}
			else {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El ticket no contiene líneas de artículo."), this.getScene().getWindow());
			}
		}
		
		if(!ticketManager.getTicket().getLineas().isEmpty()) {
			ticketManager.guardarCopiaSeguridadTicket();
		}
	}
	
	@SuppressWarnings("restriction")
	private void setHeaderClickListeners() {
		// Step 1: Get the table header row.
		TableHeaderRow headerRow = null;
		for (Node n : ((TableViewSkin<?>) tbLineas.getSkin()).getChildren()) {
			if (n instanceof TableHeaderRow) {
				headerRow = (TableHeaderRow) n;
				break;
			}
		}
		if (headerRow == null) {
			return;
		}

		// Step 2: Get the list of the header columns.
		NestedTableColumnHeader ntch = (NestedTableColumnHeader) headerRow.getChildren().get(1);
		ObservableList<TableColumnHeader> headers = ntch.getColumnHeaders();

		// Step 3: Add click listener to the header columns.
		for (int i = 0; i < headers.size(); i++) {
			TableColumnHeader header = headers.get(i);
			header.setOnMouseClicked(mouseEvent -> {
				tfCodigoIntro.requestFocus();
			});
			header.setOnDragDetected(new EventHandler<Event>(){
				@Override
                public void handle(Event event) {
					event.consume();
					tfCodigoIntro.requestFocus();
                }
			});
			header.setOnDragDone(event -> { log.trace("setOnDragDone"); });
			header.setOnDragDropped(event -> { log.trace("setOnDragDropped"); });
			header.setOnDragEntered(event -> { log.trace("setOnDragEntered"); });
			header.setOnDragExited(event -> { log.trace("setOnDragExited"); });
			header.setOnDragOver(event -> { log.trace("setOnDragOver"); });
			header.setOnKeyPressed(event -> { log.trace("setOnKeyPressed"); });
			header.setOnKeyReleased(event -> { log.trace("setOnKeyReleased"); });
			header.setOnKeyTyped(event -> { log.trace("setOnKeyTyped"); });
			header.setOnMouseDragEntered(event -> { log.trace("setOnMouseDragEntered"); });
			header.setOnMouseDragExited(event -> { log.trace("setOnMouseDragExited"); });
			header.setOnMouseDragged(event -> { log.trace("setOnMouseDragged"); });
			header.setOnMouseDragOver(event -> { log.trace("setOnMouseDragOver"); });
			header.setOnMouseEntered(event -> { log.trace("setOnMouseEntered"); });
			header.setOnMouseExited(event -> { log.trace("setOnMouseExited"); });
			header.setOnMouseMoved(event -> { log.trace("setOnMouseMoved"); });
			header.setOnMousePressed(event -> { log.trace("setOnMousePressed"); });
			header.setOnMouseReleased(event -> { log.trace("setOnMouseReleased"); });
			header.setOnSwipeDown(event -> { log.trace("setOnSwipeDown"); });
			header.setOnSwipeLeft(event -> { log.trace("setOnSwipeLeft"); });
			header.setOnSwipeRight(event -> { log.trace("setOnSwipeRight"); });
			header.setOnSwipeUp(event -> { log.trace("setOnSwipeUp"); });
			header.setOnTouchMoved(event -> { log.trace("setOnTouchMoved"); });
			header.setOnTouchPressed(event -> { log.trace("setOnTouchPressed"); });
			header.setOnTouchReleased(event -> { log.trace("setOnTouchReleased"); });
			header.setOnTouchStationary(event -> { log.trace("setOnTouchStationary"); });
		}
	}

	private void activarModoAnulacion(boolean activo) {
		modoAnulacion = activo;
		
		for(Button boton : botonera.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(!clave.equals("abrirSeleccionManual")) {
				boton.setDisable(activo);
			}
		}
		
		for(Button boton : botoneraAccionesTabla.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(!clave.equals("ABRIR_BUSQUEDA_ARTICULOS") && !clave.equals("ACCION_TABLA_NEGAR_REGISTRO")) {
				boton.setDisable(activo);
			}
		}
		
		lbAnulacion.setVisible(activo);
		
		if(!activo) {
			comprobarPermisosUI();
		}
    }
	
	public BigDecimal  calculaCantidadLinea(BigDecimal precioVenta, BigDecimal precioCodBarra){
		BigDecimal cantidad = BigDecimal.ONE;
		
		if(!BigDecimalUtil.isIgualACero(precioVenta)){
			cantidad = precioCodBarra.divide(precioVenta, 3, RoundingMode.HALF_UP);
		}
				
		return cantidad;
	}

	private AnulacionArticuloDto buscarPrecioAnulacion(String codigo, BigDecimal cantidad) throws LineaTicketException {		
		try {
			AnulacionArticuloDto resultado = new AnulacionArticuloDto();
			
			boolean articuloPesado = false;
			
			CodigoBarrasBean codBarrasEspecial = dinoCodBarrasEspecialesService.esCodigoBarrasEspecial(codigo);
			
			if(codBarrasEspecial != null) {				
				articuloPesado = codBarrasEspecial.getDescripcion().equals("Etiquetas Precios");
				
				codigo = codBarrasEspecial.getCodart();
				String cantCodBar = codBarrasEspecial.getCantidad();
				
				if (cantCodBar != null) {
					cantidad = FormatUtil.getInstance().desformateaBigDecimal(cantCodBar, 3);
				} 			
			}
			
			resultado.setCantidad(cantidad);
			
			LineaTicketAbstract lineaOriginal = lineasTicketServices.createLineaArticulo((TicketVenta) ticketManager.getTicket(), codigo, null, null, BigDecimal.ONE, null, SpringContext.getBean(LineaTicket.class));
			String codArt = lineaOriginal.getCodArticulo();

			boolean esArticuloPeso = StringUtils.isNotBlank(lineaOriginal.getArticulo().getBalanzaTipoArticulo()) && lineaOriginal.getArticulo().getBalanzaTipoArticulo().trim().toUpperCase().equals("P");
			
			BigDecimal precio = null;
			BigDecimal cantidadTicket = BigDecimal.ZERO;
			Map<BigDecimal, BigDecimal> preciosAcum = new HashMap<BigDecimal, BigDecimal>();
			Map<BigDecimal, BigDecimal> cantidadesAcum = new HashMap<BigDecimal, BigDecimal>();
			resultado.setPrecioModificadoEnOrigen(false);
			
			// acumula las cantidades y los precios de todas las coincidencias del artículo
			for(LineaTicket linea : ((List<LineaTicket>) ticketManager.getTicket().getLineas())) {
				if(linea.getCodArticulo().equals(codArt)) {
					if(esArticuloPeso) {
						BigDecimal cantidadCantidad = cantidadesAcum.get(linea.getCantidad().abs());
						if(cantidadCantidad == null) {
							cantidadCantidad = linea.getCantidad();
						}
						else {
							cantidadCantidad = cantidadCantidad.add(linea.getCantidad());
						}
						cantidadesAcum.put(linea.getCantidad().abs(), cantidadCantidad);
					}
					
					if(!resultado.getPrecioModificadoEnOrigen() && linea.tieneCambioPrecioManual()) {
						resultado.setPrecioModificadoEnOrigen(true);
						precio = linea.getPrecioTotalSinDto();
					}
					
					cantidadTicket = cantidadTicket.add(linea.getCantidad());
					
					BigDecimal precioLinea = BigDecimalUtil.redondear(linea.getPrecioTotalSinDto());
					BigDecimal cantidadPrecio = preciosAcum.get(precioLinea);
					if(cantidadPrecio == null) {
						cantidadPrecio = linea.getCantidad();
					}
					else {
						cantidadPrecio = cantidadPrecio.add(linea.getCantidad());
					}
					
					preciosAcum.put(precioLinea, cantidadPrecio);
				}
			}
			
			// lógica para obtener el precio			
			if(resultado.getPrecioModificadoEnOrigen()) {
				try {
					super.compruebaPermisos(FacturacionArticulosController.PERMISO_MODIFICAR_LINEA);
				}
				catch(SinPermisosException e) {
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
					throw new LineaTicketException(I18N.getTexto("No tiene permisos para realizar anulaciones de líneas con precios modificados."));
				}
			}
			
			List<BigDecimal> precios = new ArrayList<BigDecimal>();
			for(BigDecimal precioAcum : preciosAcum.keySet()) {
				if(!BigDecimalUtil.isIgualACero(preciosAcum.get(precioAcum))) {
					precios.add(precioAcum);
				}
			}
			
			Collections.sort(precios);
						
			if(!precios.isEmpty() && precios.size() > 1 && tarjetasRegaloService.getTipoTarjeta(codigo) == null) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(SeleccionPrecioController.PARAM_PRECIOS, precios);
				params.put(SeleccionPrecioController.PARAM_CANTIDAD, cantidad);
				
				getApplication().getMainView().showModalCentered(SeleccionPrecioView.class, params, getStage());
				
				BigDecimal precioSeleccionado = (BigDecimal) params.get(SeleccionPrecioController.PARAM_PRECIO_SELECCIONADO);
				
				if (precioSeleccionado == null) {
					throw new LineaTicketException(I18N.getTexto("Anulación cancelada"));
				}
				
				resultado.setCantidad(BigDecimal.ONE);
				
				if(!BigDecimalUtil.isIgual(lineaOriginal.getPrecioTotalSinDto(), precioSeleccionado)) {
					precio = precioSeleccionado;
					
					// volver a acumular las cantidades solo para el precio seleccionado
					if(esArticuloPeso && !articuloPesado) {
						cantidadesAcum.clear();
						
						for(LineaTicket linea : ((List<LineaTicket>) ticketManager.getTicket().getLineas())) {
							if(linea.getCodArticulo().equals(codArt) && linea.getPrecioTotalSinDto().compareTo(precio) == 0) {
								BigDecimal cantidadCantidad = cantidadesAcum.get(linea.getCantidad().abs());
								if(cantidadCantidad == null) {
									cantidadCantidad = linea.getCantidad();
								}
								else {
									cantidadCantidad = cantidadCantidad.add(linea.getCantidad());
								}
								cantidadesAcum.put(linea.getCantidad().abs(), cantidadCantidad);
							}
						}
					}
				}
				else {
					resultado.setPrecioModificadoEnOrigen(false);					
					precio = null;
				}
			}
			else {
				if(BigDecimalUtil.isMayor(cantidad, cantidadTicket) || precios.isEmpty()) {
					precio = ((DinoTicketManager)ticketManager).damePrecioVenta(codigo).getPrecioTotalConDto();
				}
				else {
					precio = precios.get(0);
				}
			}
			
			resultado.setPrecio(precio);
						
			// tratamiento de artículos de peso			
			if(esArticuloPeso && !articuloPesado) {
				List<BigDecimal> cantidades = new ArrayList<BigDecimal>();
				for(BigDecimal cantidadAcum : cantidadesAcum.keySet()) {
					if(BigDecimalUtil.isMayorACero(cantidadesAcum.get(cantidadAcum))) {
						cantidades.add(cantidadAcum);
					}
				}
				
				
				if(cantidades.size() == 1) {
				   // ANULACIÓN DEL UNICO ARTÍCULO QUE EXISTE
				   cantidad = cantidades.get(0);
				} else {
					// VARIOS ARTÍCULOS CON DISTINTO PESO, HAY QUE PEDIR EL PESO					
					IBalanza balanza = Dispositivos.getInstance().getBalanza();
					if(!(balanza instanceof BalanzaNoConfig)) {
						HashMap<String, Object> params = new HashMap<String, Object>();
						POSApplication.getInstance().getMainView().showModalCentered(SolicitarPesoArticuloView.class, params, getStage());
						
						if(params.containsKey(SolicitarPesoArticuloController.PARAM_PESO)) {
							BigDecimal peso = (BigDecimal) params.get(SolicitarPesoArticuloController.PARAM_PESO);
							
							if(peso == null || BigDecimalUtil.isMenorOrIgualACero(peso)) {
								throw new LineaTicketException(I18N.getTexto("No se ha podido pesar el artículo, compruebe la configuración de la balanza."));
							}
							
							cantidad = peso;
							
							if(cantidadesAcum.get(cantidad) == null) {
								try {
									super.compruebaPermisos(FacturacionArticulosController.PERMISO_DEVOLUCIONES);
								}
								catch(SinPermisosException e) {
									tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
									throw new LineaTicketException(I18N.getTexto("No tiene permisos para realizar devoluciones."));
								}
							}
						}
						else {
							throw new LineaTicketException(I18N.getTexto("Este artículo no puede ser introducido sin ser pesado previamente."));
						}
					}
				}
				
				resultado.setCantidad(cantidad);
			}
			
			if(BigDecimalUtil.isMayor(cantidad, cantidadTicket)) {
				try {
					super.compruebaPermisos(FacturacionArticulosController.PERMISO_DEVOLUCIONES);
				}
				catch(SinPermisosException e) {
					tfCantidadIntro.setText(FormatUtil.getInstance().formateaNumero(BigDecimal.ONE, 3));
					log.error("Error de permisos de devoluciones por cantidad superior a la del ticket. A devolver: " + cantidad.stripTrailingZeros().toPlainString() + " Pendiente ticket: " + cantidadTicket.stripTrailingZeros().toPlainString());
					throw new LineaTicketException(I18N.getTexto("No tiene permisos para realizar devoluciones. La cantidad a devolver es superior a la introducida en la venta"));
				}
			}
			
			
		    return resultado;
		}
		catch(LineaTicketException e) {
			throw e;
		}
		catch(Exception e) {
			log.error("buscarPrecioAnulacion() - Ha habido un error al buscar el artículo: " + e.getMessage(), e);
			throw new LineaTicketException("Ha habido un error al buscar el artículo: " + e.getMessage());
		}
    }
	
	@Override
	public boolean checkAvisoRetirada() {
		try {
			if(cajasService.validarImporteAvisoRetirada()){
				lbRetiradaCaja.setVisible(true);
				return true;
			}
			else {
				lbRetiradaCaja.setVisible(false);
				return true;
			}
		} catch (Exception e) {
			log.error("checkAvisoRetirada() - Excepción en cajasService : " + e.getCause(), e);
		}
		return false;
	}
	
	private void guardarCupones(FidelizacionBean tarjeta) {
		if(tarjeta != null && tarjeta.getAdicionales() != null) {
			List<CustomerCouponDTO> cuponesFidelizado = (List<CustomerCouponDTO>) tarjeta.getAdicionales().get("coupons");
			if (cuponesFidelizado != null) {
				((DinoTicketManager) ticketManager).setCuponesLeidos(cuponesFidelizado);
			}
		}
	}

	private void borrarCuponesFidelizadoAnterior() {
		Iterator<CustomerCouponDTO> it = ((DinoTicketManager) ticketManager).getCuponesLeidos().iterator();
		while(it.hasNext()) {
			CustomerCouponDTO coupon = it.next();
			if(coupon.isFromLoyaltyRequest()) {
				it.remove();
			}
		}
	}
	
	@FXML
	public void accionBorrarFidelizado() {
		boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion("¿Desea quitar al cliente de la venta actual?", getStage());
		
		if (confirmacion) {
			log.debug("accionBorrarFidelizado() - Borrando los datos del fidelizado");
			borrarCuponesFidelizadoAnterior();
			FidelizacionBean fidelizado = null;
			ticketManager.getTicket().getCabecera().setDatosFidelizado(fidelizado);
			
			ticketManager.recalcularConPromociones();
			refrescarDatosPantalla();
		}
	}

	private void imprimirTicketVacio() {
		new BackgroudPrintTask(this.getStage(), false).start();
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
		protected Void call() throws Exception {
			if (resetPrinter) {
				log.info("Reseteando impresora....");
				IPrinter impresora = Dispositivos.getInstance().getImpresora1();

				if (impresora.reset()) {
					log.info("Impresora reseteada con éxito.");
				}
			}

			TicketVentaAbono ticket = ((DinoTicketManager) ticketManager).getTicketVacio();
			
			String formatoImpresion = "ticket_vacio";

			if (formatoImpresion.equals(TipoDocumentoBean.PROPIEDAD_FORMATO_IMPRESION_NO_CONFIGURADO)) {
				log.info("BackgroudPrintTask::call() - Formato de impresión no configurado, no se imprimirá.");
				return null;
			}

			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			mapaParametros.put("ticket", ticket);
			mapaParametros.put("urlQR", variablesServices.getVariableAsString("TPV.URL_VISOR_DOCUMENTOS"));

			FidelizacionBean datosFidelizado = ticket.getCabecera().getDatosFidelizado();
			if (datosFidelizado != null) {
				Map<String, Object> adicionales = datosFidelizado.getAdicionales();
				if(adicionales != null) {
					Object ticketDigital = datosFidelizado.getAdicionales().get(DinoFidelizacion.FIDELIZADO_TICKET_DIGITAL);
					if(ticketDigital.equals(true) || ticketDigital.equals("true")) {
						mapaParametros.put("ticketDigital", true);
					}
				}
			}

			if (ticket.getCabecera().getCodTipoDocumento().equals(sesion.getAplicacion().getDocumentos().getDocumento(Documentos.FACTURA_COMPLETA).getCodtipodocumento())) {
				mapaParametros.put("empresa", sesion.getAplicacion().getEmpresa());
			}

			ServicioImpresion.imprimir(formatoImpresion, mapaParametros);

			return null;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			
			try {
				ticketManager.eliminarTicketCompleto();
				refrescarDatosPantalla();
			}
			catch (Exception e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al anular la venta. Contacte con un administrador"), e);
			}
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
				try {
					ticketManager.eliminarTicketCompleto();
					refrescarDatosPantalla();
				}
				catch (Exception ex) {
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Ha habido un error al anular la venta. Contacte con un administrador"), ex);
				}
			}
		}
	}

	private boolean abrirPantallaSeleccionOpcionesPromocion() {
		OpcionPromocionesSeleccionadaDto opcionSeleccionada = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getOpcionPromocionesSeleccionada();
		
		if(opcionSeleccionada == null) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(TICKET_KEY, ticketManager);
			getApplication().getMainView().showModalCentered(SeleccionOpcionesPromocionView.class, params, getStage());
			
			if(visor instanceof DinoVisorPantallaSecundaria) {
				visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
			}
			
			refrescarDatosPantalla();
			
			if(params.containsKey(SeleccionOpcionesPromocionController.PARAM_CONTINUAR_COMPRA)) {
				return true;
			}
			
			return false;
		}
		else {
			log.debug("abrirPantallaSeleccionOpcionesPromocion() - La pantalla no se abrirá ya que el cliente seleccionó previamente una opción.");
			return false;
		}
	}

	private void limpiarOpcionPromocionesSeleccionada() {
		log.debug("limpiarOpcionPromocionesSeleccionada() - Limpiando y desaplicando promociones de la opción seleccionada.");
		
		((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).setOpcionPromocionesSeleccionada(null);
		ticketManager.recalcularConPromociones();
	}
	
	public void abrirInfoDevolucionesBlackFriday() {
		getApplication().getMainView().showModalCentered(InfoDevolucionesBlackFridayView.class, getDatos(), getStage());
	}
	
	@Override
	protected void abrirVentanaBusquedaArticulos() {
		getDatos().put(DinoBuscarArticulosController.PARAM_TICKET_MANAGER_CURSO, ticketManager);
		
		super.abrirVentanaBusquedaArticulos();
		
		if(visor instanceof DinoVisorPantallaSecundaria) {
			visor.modoVenta(visorConverter.convert(((TicketVentaAbono) ticketManager.getTicket())));
		}
	}

	public void activarLecturaQrLiquidacion(boolean activar) {
		modoLecturaQrLiquidacion = true;
		
		for(Button boton : botonera.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(!clave.equals("abrirSeleccionManual")) {
				boton.setDisable(activar);
			}
		}
		
		for(Button boton : botoneraAccionesTabla.getListaBotones()) {
			String clave = ((ConfiguracionBotonBean) boton.getUserData()).getClave();
			if(!clave.equals("ABRIR_BUSQUEDA_ARTICULOS")) {
				boton.setDisable(activar);
			}
		}
	}
	
	@Override
	protected void crearNuevoTicket() throws PromocionesServiceException, DocumentoException {
		super.crearNuevoTicket();
	}
	
	private void mostrarServicioRepartoSeleccionado() {
		ServicioRepartoDto servicioSeleccionado = ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto();
		
		if(servicioSeleccionado != null) {
			lbTitulo.setStyle("-fx-background-color: " + servicioSeleccionado.getColor());
			apCabecera.setStyle("-fx-background-color: " + servicioSeleccionado.getColor());
			
			URL urlImagen = serviciosRepartoService.getUrlImage(servicioSeleccionado.getIcono());
			Image image = new Image(urlImagen.toString());
			ivServicioReparto.setImage(image);
		}
		else {
			lbTitulo.setStyle("-fx-background-color: #85b037");
			apCabecera.setStyle("-fx-background-color: #85b037");
			
			ivServicioReparto.setImage(null);
		}
	}

	private boolean comprobarServicioRepartoSeleccionado() {
		boolean hayServiciosRepartoActivo = serviciosRepartoService.getServiciosReparto() != null;
		
		if(!hayServiciosRepartoActivo) {
			return true;
		}
		
		return ((DinoCabeceraTicket) ticketManager.getTicket().getCabecera()).getServicioRepartoDto() != null;
	}
	
	private boolean compruebaCajonAbierto() {
	    if (Dispositivos.getInstance().getCajon().getConfiguracion() != null && Dispositivos.getInstance().getCajon().getConfiguracion().isUsaDispositivo() && Dispositivos.getInstance().getCajon().isOpen()) {
	        reproducirSonidoError();
	        VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede iniciar la venta con el cajón abierto"), getStage());
	        return true;
	    }
	    return false;
	}
	
	@Override
	public void recuperarTicket() {
		if(compruebaCajonAbierto()) {
			return;
		}
		super.recuperarTicket();
	}

	@Override
	public void abrirSeleccionManual() {
		if(compruebaCajonAbierto()) {
			return;
		}
		super.abrirSeleccionManual();
	}
}
