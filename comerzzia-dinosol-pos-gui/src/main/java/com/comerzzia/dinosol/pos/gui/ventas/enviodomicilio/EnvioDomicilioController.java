package com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.numeros.BigDecimalUtil;
import com.comerzzia.dinosol.librerias.sad.client.BultosApi;
import com.comerzzia.dinosol.librerias.sad.client.PedidosApi;
import com.comerzzia.dinosol.librerias.sad.client.VehiculosApi;
import com.comerzzia.dinosol.librerias.sad.client.model.Bulto;
import com.comerzzia.dinosol.librerias.sad.client.model.DisponibilidadPedido;
import com.comerzzia.dinosol.librerias.sad.client.model.Entrega;
import com.comerzzia.dinosol.librerias.sad.client.model.EntregaReserva;
import com.comerzzia.dinosol.librerias.sad.client.model.EstadoPedido;
import com.comerzzia.dinosol.librerias.sad.client.model.Paquete;
import com.comerzzia.dinosol.librerias.sad.client.model.SadReserva;
import com.comerzzia.dinosol.librerias.sad.client.model.SadSolicitud;
import com.comerzzia.dinosol.librerias.sad.client.model.Tienda;
import com.comerzzia.dinosol.librerias.sad.client.model.TipoBulto;
import com.comerzzia.dinosol.librerias.sad.client.model.TipoVehiculo;
import com.comerzzia.dinosol.librerias.sad.client.model.Vehiculos;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.Campos;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.DomicilioResponse;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.ListAddressData;
import com.comerzzia.dinosol.librerias.sherpa.client.domicilio.model.PersonData;
import com.comerzzia.dinosol.pos.devices.fidelizacion.DinoFidelizacion;
import com.comerzzia.dinosol.pos.dispositivo.visor.pantallasecundaria.gui.DinoVisorPantallaSecundaria;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADController;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.AltaUsuarioSADView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.BusquedaDomicilioView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.domicilio.SeleccionarDomicilioView;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasFidelizadoException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.exception.RutasServiciosException;
import com.comerzzia.dinosol.pos.gui.ventas.enviodomicilio.key.EnvioDomicilioKeys;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasBultosBoletaBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasErroresBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasFechasDisponiblesBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasTicketBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasTipoBultoBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.RutasVehiculoBean;
import com.comerzzia.dinosol.pos.persistence.enviosdomicilio.domicilio.DireccionTablaBean;
import com.comerzzia.dinosol.pos.persistence.tickets.sad.TicketAnexoSadBean;
import com.comerzzia.dinosol.pos.services.core.documentos.propiedades.DinoPropiedadesDocumentoService;
import com.comerzzia.dinosol.pos.services.core.documentos.tipos.DinosolTipoDocumentoService;
import com.comerzzia.dinosol.pos.services.sad.ServicioADomicilioService;
import com.comerzzia.dinosol.pos.services.sherpa.SherpaApiBuilder;
import com.comerzzia.dinosol.pos.services.ticket.cabecera.DinoCabeceraTicket;
import com.comerzzia.dinosol.pos.services.ticket.sad.TicketAnexoSadService;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.POSApplication;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.permisos.exception.SinPermisosException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.dispositivo.visor.pantallasecundaria.gui.TicketVentaDocumentoVisorConverter;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.pagos.PagosController;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.tipos.TipoDocumentoService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.devices.DeviceException;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.servicios.impresion.ServicioImpresion;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;
import com.google.gson.Gson;

import feign.FeignException;
import feign.RetryableException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@SuppressWarnings("deprecation")
@Component
public class EnvioDomicilioController extends WindowController {
	
	private Logger log = Logger.getLogger(EnvioDomicilioController.class);
	
	private static String PERMISO_FORZAR_DIRECCION = "FORZAR DIRECCIÓN SAD";
	
	public static String PARAM_CANCELAR = "EnvioDomicilioController.Cancelar";

	@FXML
	protected TextArea taObservaciones;
	@FXML
	private AnchorPane apTitulo;
	@FXML
	protected Label lbTitulo, lbNombre, lbPorte, lbTotalBultos;
	@FXML
	protected TextField tfTelefono, tfEmail, tfDireccion, tfPoblacion, tfCodigoPostal, tfProvincia;
	@FXML
	protected ComboBox<RutasVehiculoBean> cbMediosTransporte;
	protected ObservableList<RutasVehiculoBean> tiposVehiculos;
	@FXML
	protected Button btSolicitarDisponibilidad, bAltaDireccion, bCambiarDireccion, bModificarDireccion;
	@FXML
	protected HBox hbPorte;

	protected ObservableList<RutasTipoBultoBean> bultos;
	@FXML
	protected TableView<RutasTipoBultoBean> tbBultos;
	@FXML
	protected TableColumn<RutasTipoBultoBean, String> tcNombreBulto;
	@FXML
	protected TableColumn<RutasTipoBultoBean, ButtonsBox> tcNumeroBulto;

	protected ObservableList<RutasFechasDisponiblesBean> disponibilidades;
	@FXML
	protected TableView<RutasFechasDisponiblesBean> tbDisponibilidad;
	@FXML
	protected TableColumn<RutasFechasDisponiblesBean, String> tcFechaDisponible;
	@FXML
	protected TableColumn<RutasFechasDisponiblesBean, String> tcTramoDisponible;
	
	@FXML
	private CheckBox cbDireccionForzada;

	private TicketManager ticketManager;

	private BultosApi bultosService;
	private PedidosApi pedidosService;
	private VehiculosApi vehiculosService;

	@Autowired
	protected TicketsService ticketService;

	@SuppressWarnings("rawtypes")
	private ITicket ticket;
	private String idPedido;

	@Autowired
	protected VariablesServices variablesServices;

	private DireccionTablaBean direccion;

	protected IVisor visor;

	@Autowired
	protected TicketVentaDocumentoVisorConverter visorConverter;

	@Autowired
	protected ServicioContadores contadoresService;
	@Autowired
	protected TipoDocumentoService tipoDocumentoService;
	protected Boolean ticketRecuperado;

	@Autowired
	public static DinoPropiedadesDocumentoService propiedadesService;
	
	@Autowired
	private TicketAnexoSadService ticketAnexoSadService;

	private Boolean integracionRutas;

	private BigDecimal importeTotalPortes;

	@Autowired
	private Sesion sesion;
	
	@Autowired
	private ServicioADomicilioService sadService;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando la configuración de la pantalla...");

		/* Configuramos las columnas de la tabla de Bultos */
		tcNombreBulto.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbBultos", "tcNombreBulto", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

		/* Indicamos las variables que cargarán las columnas */
		tcNumeroBulto.getStyleClass().add("columnaBultos");
		tcNombreBulto.setCellValueFactory(new PropertyValueFactory<RutasTipoBultoBean, String>("nombre"));
		tcNumeroBulto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RutasTipoBultoBean, ButtonsBox>, ObservableValue<ButtonsBox>>(){

			@Override
			public ObservableValue<ButtonsBox> call(final CellDataFeatures<RutasTipoBultoBean, ButtonsBox> p) {
				/* Creamos los objetos que contendrá la tabla de Bultos */
				RutasTipoBultoBean bultoGui = p.getValue();

				Image imageMas = POSApplication.getInstance().createImage("iconos/add.png");
				ImageView imagenMas = new ImageView(imageMas);
				Button btnMas = new Button();
				btnMas.setGraphic(imagenMas);

				Label lbCantidadBulto = new Label("0");
				lbCantidadBulto.setPrefSize(17, 17);

				Image imageMenos = POSApplication.getInstance().createImage("iconos/less.png");
				ImageView imagenMenos = new ImageView(imageMenos);
				Button btnMenos = new Button();
				btnMenos.setGraphic(imagenMenos);

				final ButtonsBox bb = new ButtonsBox();
				bb.setBtnMenos(btnMenos);
				bb.setCantidad(lbCantidadBulto);
				bb.setBtnMas(btnMas);
				bb.getChildren().add(btnMenos);
				bb.getChildren().add(lbCantidadBulto);
				bb.getChildren().add(btnMas);
				btnMas.setUserData(bultoGui);
				btnMenos.setUserData(bultoGui);
				bb.setAlignment(Pos.BOTTOM_CENTER);
				bb.setSpacing(10);
				bb.setStyle("-fx-alignment: CENTER;");

				bb.getBtnMenos().setOnAction(new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent evt) {
						/* Rescatamos la cantidad actual y le restamos uno */
						BigDecimal cantidadActual = new BigDecimal(bb.getCantidad().getText());
						if (BigDecimalUtil.isMayorACero(cantidadActual)) {
							BigDecimal cantidadResta = cantidadActual.subtract(BigDecimal.ONE);
							bb.getCantidad().setText(cantidadResta.toString());

							/* Restamos al total uno */
							if (lbTotalBultos.getText() != null) {
								/* Comprobamos que sea mayor que cero, sino no restamos */
								if (BigDecimalUtil.isMayorOrIgualACero(new BigDecimal(lbTotalBultos.getText()))) {
									BigDecimal total = new BigDecimal(lbTotalBultos.getText());
									total = total.subtract(BigDecimal.ONE);
									lbTotalBultos.setText(total.toString());
									log.debug("BotonRestarBulto - El total después de restar es : " + lbTotalBultos.getText());
								}
							}

							/* Pasamos la cantidad al objeto de la tabla */
							p.getValue().setCantidad(cantidadResta.intValue());
							/* Limpiamos la tabla de disponibilidades */
							disponibilidades.clear();
						}
					}
				});

				bb.getBtnMas().setOnAction(new EventHandler<ActionEvent>(){

					@Override
					public void handle(ActionEvent evt) {
						/* Rescatamos la cantidad actual y le sumamos uno */
						BigDecimal cantidadActual = new BigDecimal(bb.getCantidad().getText());
						BigDecimal cantidadSuma = cantidadActual.add(BigDecimal.ONE);
						bb.getCantidad().setText(cantidadSuma.toString());

						/* Sumamos al total uno */
						if (lbTotalBultos.getText() != null) {
							BigDecimal total = new BigDecimal(lbTotalBultos.getText());
							total = total.add(BigDecimal.ONE);
							lbTotalBultos.setText(total.toString());
							log.debug("BotonSumarBulto - El total después de sumar es : " + lbTotalBultos.getText());
						}

						/* Pasamos la cantidad al objeto de la tabla */
						p.getValue().setCantidad(cantidadSuma.intValue());
						/* Limpiamos la tabla de disponibilidades */
						disponibilidades.clear();
					}
				});

				return new ObservableValueBase<ButtonsBox>(){

					@Override
					public ButtonsBox getValue() {
						return bb;
					}
				};
			}
		});

		/* Configuramos las columnas de la tabla de Disponibilidad Horaria */
		tcFechaDisponible.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDisponibilidad", "tcFechaDisponible", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));
		tcTramoDisponible.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbDisponibilidad", "tcTramoDisponible", null, CellFactoryBuilder.ESTILO_ALINEACION_CEN));

		/* Indicamos las variables que cargarán las columnas */
		tcFechaDisponible.setCellValueFactory(new PropertyValueFactory<RutasFechasDisponiblesBean, String>("fecha"));
		tcTramoDisponible.setCellValueFactory(new PropertyValueFactory<RutasFechasDisponiblesBean, String>("tramoHorario"));

		/* Pasamos la lista de Disponibilidad Horaria a la tabla para poder cargarla */
		disponibilidades = FXCollections.observableList(new ArrayList<RutasFechasDisponiblesBean>());
		tbDisponibilidad.setItems(disponibilidades);

		/* Insertamos las imagenes de los botones */
		ImageView imagenBotonDisponibilidad = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/pendientenvio.png");
		btSolicitarDisponibilidad.setGraphic(imagenBotonDisponibilidad);
		ImageView imagenBotonDireccion = new ImageView("/skins/dinosol" + Variables.IMAGES_BASE_PATH + "iconos/refresh.png");
		bCambiarDireccion.setGraphic(imagenBotonDireccion);
		ImageView imagenBotonAlta = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/add.png");
		bAltaDireccion.setGraphic(imagenBotonAlta);
		ImageView imagenBotonModificar = new ImageView("/skins/standard" + Variables.IMAGES_BASE_PATH + "iconos/edit.png");
		bModificarDireccion.setGraphic(imagenBotonModificar);

		comprobarRutasActivadas();

		tiposVehiculos = FXCollections.observableArrayList(new ArrayList<RutasVehiculoBean>());
		bultos = FXCollections.observableList(new ArrayList<RutasTipoBultoBean>());

		log.debug("initialize() - Finalizada la configuración de la pantalla...");
	}

	/**
	 * Personalización de la clase de HBox para poder incluir botones y el label en la tabla de Bultos.
	 */
	protected static class ButtonsBox extends HBox {

		private Button btnMenos;
		private Label cantidad;
		private Button btnMas;

		public ButtonsBox() {
			super();
		}

		public Button getBtnMenos() {
			return btnMenos;
		}

		public void setBtnMenos(Button btnMenos) {
			this.btnMenos = btnMenos;
		}

		public Button getBtnMas() {
			return btnMas;
		}

		public void setBtnMas(Button btnMas) {
			this.btnMas = btnMas;
		}

		public Label getCantidad() {
			return cantidad;
		}

		public void setCantidad(Label cantidad) {
			this.cantidad = cantidad;
		}
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		visor = Dispositivos.getInstance().getVisor();
		
		addTextLimiter(taObservaciones, 250);
	}
	
    public void addTextLimiter(final TextArea tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initializeForm() throws InitializeGuiException {
		activarIntegracionRutas();
		comprobarRutasActivadas();

		/* Controlamos si viene desde la recuperación del ticket */
		if (getDatos().containsKey(EnvioDomicilioKeys.TICKET_RECUPERADO)) {
			ticketRecuperado = true;
			/* Ocultamos el porte */
			hbPorte.setVisible(false);
		}
		else {
			ticketRecuperado = false;
			hbPorte.setVisible(true);
		}

		/* Limpiamos todos los datos de pantalla que se van a volver a cargar */
		limpiarDatosPantalla();

		/* Cargamos el ticket de la pantalla de ventas */
		ticketManager = (TicketManager) getDatos().get(EnvioDomicilioKeys.TICKET_VENTA);
		if (ticketRecuperado) {
			ticket = ticketManager.getTicketOrigen();
		}
		else {
			ticket = ticketManager.getTicket();
		}
		if (ticket.getIdTicket() == null) {
			/* Iniciamos el código del ticket */
			try {
				ticketService.setContadorIdTicket((Ticket) ticket);
			}
			catch (TicketsServiceException e) {
				String mensajeError = "Se ha producido un error al generar el código del ticket";
				log.error("initializeForm() - " + mensajeError, e);
				controlarErrores(mensajeError);
			}
		}

		
		DomicilioResponse domicilio = null;
		if (getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS)) {
			domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
		}
		
		if(domicilio != null && domicilio instanceof DomicilioOfflineDto) {
			direccion = new DireccionTablaBean();
			
			domicilio.setPersonData(new PersonData());
			domicilio.getPersonData().setSherpaCode("");
			direccion.setDirecciones(domicilio);

			tfTelefono.setText(((DomicilioOfflineDto)domicilio).getTelefono());
			tfEmail.setText(((DomicilioOfflineDto)domicilio).getEmail());
			tfDireccion.setText(((DomicilioOfflineDto)domicilio).getDireccion());
			tfPoblacion.setText(((DomicilioOfflineDto)domicilio).getPoblacion());
			tfCodigoPostal.setText(((DomicilioOfflineDto)domicilio).getCp());
			tfProvincia.setText(((DomicilioOfflineDto)domicilio).getProvincia());			
		}
		else {
			/*
			 * Realizamos las comprobaciones necesarias del Fidelizado, en caso de no estar cargado lanzamos las pantallas
			 * para cargar los datos.
			 */
			try {
				comprobarFidelizado();
			}
			catch (InitializeGuiException e2) {
				log.error("initializeForm() - " + e2.getMessage());
				controlarErrores(e2.getMessage());
			}
		}

		/*
		 * Realizamos la carga de los servicios, en caso de fallar esta parte no deberemos dejar continuar en la
		 * pantalla
		 */
		try {
			cargarServicios();
		}
		catch (RutasServiciosException e1) {
			log.error("initializeForm() - Error al cargar los servicios, se funcionará de forma offline: " + e1.getMessage(), e1);
			desactivarIntegracionRutas();
		}

		/*
		 * Después de haber realizado todas las operaciones con el Fidelizado, si sigue sin estar cargado, procedemos a
		 * cerrar la pantalla
		 */
		if (direccion == null) {
			/* Provocar exception pero sin mostrar el error en pantalla */
			InitializeGuiException initializeGuiException = new InitializeGuiException("");
			initializeGuiException.setMostrarError(false);
			throw initializeGuiException;
		}

		/* Generamos el ID del pedido que vamos a realizar */
		if (getDatos().get(EnvioDomicilioKeys.CODPEDIDO) != null) {
			generarIdPedido((String) getDatos().get(EnvioDomicilioKeys.CODPEDIDO));
		}
		else {
			generarIdPedido(null);
		}

		/* En caso de ser un ticket recuperado, no se deberá calcular el porte */
		if (ticketRecuperado) {
			importeTotalPortes = BigDecimal.ZERO;
			lbPorte.setText(I18N.getTexto("GRATUITO"));
		}
		else {
			for (LineaTicket linea : (List<LineaTicket>) ticket.getLineas()) {
				if (((DinoFidelizacion) Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte().equals(linea.getArticulo().getCodArticulo())) {
					importeTotalPortes = linea.getPrecioTotalConDto();
					break;
				}
			}
			lbPorte.setText(FormatUtil.getInstance().formateaImporte(importeTotalPortes) + " €");
		}

		/* Bloqueamos los campos realacionados con el Fidelizado */
		bloquearCampos();

		/* Limpiamos y pedimos los medios de transporte */
		List<Vehiculos> listadoTransportes = new ArrayList<Vehiculos>();
		try {
			listadoTransportes = cargarTransportes();
		}
		catch (RutasServiciosException e3) {
			log.debug("initializeForm() - " + e3.getMessage(), e3);
			log.warn("initializeForm() - Se usarán los vehículos anteriormente cargados.");
		}
		/* Comprobamos la carga de los medios de transporte */
		if (listadoTransportes != null && !listadoTransportes.isEmpty()) {
			cbMediosTransporte.getItems().clear();
			/* Ordenamos la lista por ID para seleccionar la primera por defecto */
			Collections.sort(listadoTransportes, new Comparator<Vehiculos>(){

				@Override
				public int compare(Vehiculos o1, Vehiculos o2) {
					return o1.getId().toString().compareToIgnoreCase(o2.getId().toString());
				}
			});
			for (Vehiculos medioTransporte : listadoTransportes) {
				RutasVehiculoBean nuevoVehiculo = new RutasVehiculoBean();
				nuevoVehiculo.setVehiculo(medioTransporte);
				tiposVehiculos.add(nuevoVehiculo);
				log.debug("initializeForm() - Vehículo cargado : " + medioTransporte);
			}
			cbMediosTransporte.setItems(tiposVehiculos);
			cbMediosTransporte.getSelectionModel().selectFirst();
		}
		if(tiposVehiculos.isEmpty()) {
			controlarErrores(I18N.getTexto("No se han podido cargar los tipos de transporte."));
		}

		cbMediosTransporte.valueProperty().addListener((ov, p1, p2) -> {
			/* Limpiamos la tabla de disponibilidades */
			disponibilidades.clear();
		});

		/* Pedimos los Bultos y los cargamos en la tabla */
		List<RutasTipoBultoBean> listadoBultos = new ArrayList<RutasTipoBultoBean>();
		try {
			listadoBultos = cargarBultos();
		}
		catch (RutasServiciosException e4) {
			log.debug("initializeForm() - " + e4.getMessage(), e4);
			
			if(listadoBultos != null) {
				for(RutasTipoBultoBean tipoBulto : listadoBultos) {
					tipoBulto.setCantidad(0);
				}
				lbTotalBultos.setText(BigDecimal.ZERO.toString());
			}
		}

		/* Comprobamos la carga de los Bultos */
		if (listadoBultos != null && !listadoBultos.isEmpty()) {
			bultos.clear();
			for (RutasTipoBultoBean bulto : listadoBultos) {
				bultos.add(bulto);
				log.debug("initializeForm() - Bulto cargado : " + (bulto).getNombre());
			}
			/* Eliminamos el Focus del primer elemento */
			tbBultos.requestFocus();
			tbBultos.getSelectionModel().select(null);
			tbBultos.getFocusModel().focus(null);
			tbBultos.setItems(bultos);
		}
		if(bultos.isEmpty()) {
			controlarErrores(I18N.getTexto("No se han podido cargar los bultos."));
		}

		/* Establecemos el total de los bultos en 0 en un inicio */
		lbTotalBultos.setText(BigDecimal.ZERO.toString());
		
		cbDireccionForzada.setSelected(false);
		try {
			compruebaPermisos(PERMISO_FORZAR_DIRECCION);
			cbDireccionForzada.setDisable(false);			
		}
		catch(SinPermisosException e) {
			cbDireccionForzada.setDisable(true);			
		}
	}

	public void controlarErrores(String mensajeError) throws InitializeGuiException {
		/* Enviamos este dato para que no se cancele el ticket aunque se genere algún error */
		getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
		String mensajePregunta = mensajeError + "\n¿Desea continuar para realizar los pagos?";

		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto(mensajePregunta), getStage())) {
			/* En caso de aceptar, pasamos a la pantalla de pagos */
			((DinoCabeceraTicket) ticket.getCabecera()).setDatosSad(null);
			getDatos().put(EnvioDomicilioKeys.IR_PAGOS, true);
		}

		/* Provocar exception pero sin mostrar el error en pantalla */
		InitializeGuiException initializeGuiException = new InitializeGuiException(I18N.getTexto(mensajeError));
		initializeGuiException.setMostrarError(false);
		throw initializeGuiException;
	}

	public void generarIdPedido(String idPedidoCancelado) {
		idPedido = ticket.getCabecera().getCodTicket();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");
		String randomIdTicket = simpleDateFormat.format(new Date());
		idPedido = idPedido.replace("/", "-") + "-" + randomIdTicket;

		if (idPedidoCancelado != null) {
			if (idPedidoCancelado.equals(idPedido)) {
				idPedido = ticket.getCabecera().getCodTicket();
				Integer randomIdTicketRepetido = new Random().nextInt(9999);
				idPedido = idPedido.replace("/", "-") + randomIdTicketRepetido;
			}
		}
	}

	@Override
	public void initializeFocus() {
		cbMediosTransporte.requestFocus();
	}

	/**
	 * Realiza la carga de los servicios necesarios en la clase para usar los servicios de Rutas.
	 * 
	 * @throws RutasServiciosException
	 */
	public void cargarServicios() throws RutasServiciosException {
		log.debug("cargarServicios() - Iniciando la carga de los servicios...");
		try {
			bultosService = SherpaApiBuilder.getSadBultosService();
			pedidosService = SherpaApiBuilder.getSadPedidosService();
			vehiculosService = SherpaApiBuilder.getSadVehiculosService();

		}
		catch (Exception e) {
			String mensajeError = "Se ha producido un error de conexión con los servicios de Rutas: " + e.getMessage();
			log.error("cargarServicios() - " + mensajeError);
			throw new RutasServiciosException(mensajeError, e);
		}
		log.debug("cargarServicios() - Finalizada la carga de los servicios");
	}

	/**
	 * Realiza comprobaciones con los servicios de Sherpa para cargar los datos de un Fidelizado.
	 * 
	 * @throws InitializeGuiException
	 */
	public void comprobarFidelizado() throws InitializeGuiException {

		direccion = null;
		if (!ticketRecuperado) {
			/* Comprobamos los datos del Fidelizado */
			if (ticket.getCabecera().getDatosFidelizado() != null) {
				DomicilioResponse domicilio = null;
				if (getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS)) {
					domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
				}

				if (domicilio == null || domicilio.getListAddressData().isEmpty()) {
					DinoCabeceraTicket cabecera = (DinoCabeceraTicket) ticketManager.getTicket().getCabecera();
					if(StringUtils.isBlank(cabecera.getNifBusquedaEnvioDomicilio()) && StringUtils.isBlank(cabecera.getNombreBusquedaEnvioDomicilio()) && cabecera.getDomicilioResponseBusquedaEnvioDomicilio() == null) {
						getApplication().getMainView().showModalCentered(BusquedaDomicilioView.class, getDatos(), getApplication().getMainView().getStage());
						
						String nif = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO);
						if(StringUtils.isNotBlank(nif)) {
							cabecera.setNifBusquedaEnvioDomicilio(nif);
						}
						
						String nombre = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_NOMBRE);
						if(StringUtils.isNotBlank(nombre)) {
							cabecera.setNombreBusquedaEnvioDomicilio(nombre);
						}
						
						DomicilioResponse domicilioSeleccionado = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO);
						if(domicilioSeleccionado != null) {
							cabecera.setDomicilioResponseBusquedaEnvioDomicilio(domicilioSeleccionado);
						}
					}
					else {
						getDatos().put(EnvioDomicilioKeys.FIDELIZADO_NOMBRE, cabecera.getNombreBusquedaEnvioDomicilio());
						getDatos().put(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO, cabecera.getNifBusquedaEnvioDomicilio());
						getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, cabecera.getDomicilioResponseBusquedaEnvioDomicilio());
					}

					if (getDatos().containsKey(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO)) {
						DomicilioResponse domicilioSeleccionado = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO);
						direccion = seleccionarDireccion(domicilioSeleccionado);

					}
				}
				else {
					/* En caso de tener solo una dirección la pasamos directamente a SAD */
					if (domicilio.getListAddressData().size() == 1) {
						direccion = new DireccionTablaBean();
						direccion.setDirecciones(domicilio);
						direccion.setSherpaCodeDireccion(domicilio.getListAddressData().get(0).getSherpaAddressCode());
					}
					else if (domicilio.getListAddressData().size() > 1) {
						direccion = seleccionarDireccion(domicilio);
					}
				}

			}
			else {
				/* En caso de no encontrar nada, muestra la pantalla de búsqueda por DNI/Tlf */
				log.debug("comprobarFidelizado() - Iniciamos la ventana de búsqueda del usuario de Sad...");
				DinoCabeceraTicket cabecera = (DinoCabeceraTicket) ticketManager.getTicket().getCabecera();
				
				if(StringUtils.isBlank(cabecera.getNifBusquedaEnvioDomicilio()) && StringUtils.isBlank(cabecera.getNombreBusquedaEnvioDomicilio()) && cabecera.getDomicilioResponseBusquedaEnvioDomicilio() == null) {
					getApplication().getMainView().showModalCentered(BusquedaDomicilioView.class, getDatos(), getApplication().getMainView().getStage());
					
					String nif = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO);
					if(StringUtils.isNotBlank(nif)) {
						cabecera.setNifBusquedaEnvioDomicilio(nif);
					}
					
					String nombre = (String) getDatos().get(EnvioDomicilioKeys.FIDELIZADO_NOMBRE);
					if(StringUtils.isNotBlank(nombre)) {
						cabecera.setNombreBusquedaEnvioDomicilio(nombre);
					}
					
					DomicilioResponse domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO);
					if(domicilio != null) {
						cabecera.setDomicilioResponseBusquedaEnvioDomicilio(domicilio);
					}
					
					if(getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS) && getDatos().get(EnvioDomicilioKeys.DOMICILIOS) instanceof DomicilioOfflineDto) {
						DomicilioOfflineDto domicilioOffline = (DomicilioOfflineDto) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
						crearDireccionOffline(domicilioOffline);
					}
				}
				else {
					getDatos().put(EnvioDomicilioKeys.FIDELIZADO_NOMBRE, cabecera.getNombreBusquedaEnvioDomicilio());
					getDatos().put(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO, cabecera.getNifBusquedaEnvioDomicilio());
					getDatos().put(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO, cabecera.getDomicilioResponseBusquedaEnvioDomicilio());
				}

				if (getDatos().containsKey(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO)) {
					DomicilioResponse domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.BUSQUEDA_FIDELIZADO);
					direccion = seleccionarDireccion(domicilio);

				}
				else if (getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)) {

					DomicilioResponse domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
					direccion = new DireccionTablaBean();
					direccion.setDirecciones(domicilio);
					direccion.setSherpaCodeDireccion((String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));

				}
				else if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
					return;
				}
				log.debug("comprobarFidelizado() - Finalizada la búsqueda del usuario de Sad");
			}
		}
		else {
			if (getDatos().containsKey(EnvioDomicilioKeys.DOMICILIOS)) {

				DomicilioResponse domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
				direccion = seleccionarDireccion(domicilio);

			}
			else if (getDatos().containsKey(EnvioDomicilioKeys.ALTA_DIRECCION_SAD)) {

				DomicilioResponse domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
				direccion = new DireccionTablaBean();
				direccion.setDirecciones(domicilio);
				direccion.setSherpaCodeDireccion((String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));

			}
			else if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
				return;
			}
		}
		/* Si se ha podido cargar el Fidelizado, procedemos a cargar sus datos en pantalla */
		if (direccion != null && !(direccion.getDirecciones() instanceof DomicilioOfflineDto)) {
			try {
				cargarFidelizado(direccion);
			}
			catch (RutasFidelizadoException e1) {
				log.error("comprobarFidelizado() - " + e1.getMessage());
				throw new InitializeGuiException(e1.getMessage());
			}
		}
		else {
			return;
		}
	}

	public DireccionTablaBean seleccionarDireccion(DomicilioResponse domicilio) {
		/* Si las cargamos correctamente, procedemos a mostrar el listado */
		if (domicilio != null && !domicilio.getListAddressData().isEmpty()) {
			if (domicilio != null) {
				getDatos().put(EnvioDomicilioKeys.DOMICILIOS, domicilio);
				addDatosFidelizado(domicilio);
				getApplication().getMainView().showModalCentered(SeleccionarDomicilioView.class, getDatos(), getApplication().getMainView().getStage());

				if (getDatos().containsKey(EnvioDomicilioKeys.SELECCION_DOMICILIO)) {
					direccion = (DireccionTablaBean) getDatos().get(EnvioDomicilioKeys.SELECCION_DOMICILIO);
				}
			}
		}
		else {
			if(domicilio != null && domicilio instanceof DomicilioOfflineDto) {
				getDatos().put(AltaUsuarioSADController.PARAM_OFFLINE, true);
			}
			
			getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos(), getApplication().getMainView().getStage());
			
			domicilio = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.DOMICILIOS);
			
			if(domicilio != null) {
				crearDireccionOffline(domicilio);
			}
			else {
				getStage().close();
			}
		}
		return direccion;
	}

	protected void addDatosFidelizado(DomicilioResponse domicilio) {
		String nombre = null;
		String apellido = null;
		String nif = null;
		for (Campos campo : domicilio.getPersonData().getCampos()) {
			if (campo.getField().equals("nif")) {
				nif = campo.getValue();
			}
			else if (campo.getField().equals("nombre")) {
				nombre = campo.getValue();
			}
			else if (campo.getField().equals("apellidos")) {
				apellido = campo.getValue();
			}
		}
		getDatos().put(EnvioDomicilioKeys.FIDELIZADO_NOMBRE, nombre + " " + apellido);
		getDatos().put(EnvioDomicilioKeys.FIDELIZADO_DOCUMENTO, nif);
	}

	public DireccionTablaBean seleccionarDireccionEnSad(DomicilioResponse domicilio) {
		/* Si las cargamos correctamente, procedemos a mostrar el listado */
		if (!domicilio.getListAddressData().isEmpty()) {
			if (domicilio != null) {
				getDatos().put(EnvioDomicilioKeys.DOMICILIOS, domicilio);
				addDatosFidelizado(domicilio);
				getApplication().getMainView().showModalCentered(SeleccionarDomicilioView.class, getDatos(), getStage());

				if (getDatos().containsKey(EnvioDomicilioKeys.SELECCION_DOMICILIO)) {
					direccion = (DireccionTablaBean) getDatos().get(EnvioDomicilioKeys.SELECCION_DOMICILIO);
				}
			}
		}
		else {
			getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos(), getApplication().getMainView().getStage());
		}
		return direccion;
	}

	@FXML
	public void cambiarDireccion() {
		/* Realizamos la búsqueda en Sherpa de las direcciones */
		try {
			getDatos().put(EnvioDomicilioKeys.CAMBIO_DIRECCION, true);
			if (direccion.getDirecciones().getListAddressData().size() > 1) {
				direccion = seleccionarDireccionEnSad(direccion.getDirecciones());
				cargarFidelizado(direccion);
				/*
				 * Limpiamos la tabla de disponibilidades después de cambiar de dirección, para tener que pedirlas de
				 * nuevo
				 */
				disponibilidades.clear();
			}
			else if (direccion.getDirecciones().getListAddressData().size() == 1) {
				String mensajeInfo = "El usuario solo dispone de una Dirección";
				log.info("cambiarDireccion() - " + mensajeInfo);
				VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto(mensajeInfo), getStage());
			}
		}
		catch (RutasFidelizadoException e) {
			log.error("cambiarDireccion() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
			return;
		}
	}

	/**
	 * Realiza la carga de los datos del fidelizado que nos vienen de getDatos() de la pantalla anterior, realizando
	 * todas las comprobaciones necesarias.
	 * 
	 * @param direccion
	 *            : Objeto con datos de la dirección seleccionada.
	 * @return
	 * @throws RutasFidelizadoException
	 */
	public void cargarFidelizado(DireccionTablaBean direccion) throws RutasFidelizadoException {
		/* Variable para controlar los errores de carga del Fidelizado */
		Boolean erroresFidelizado = false;
		String mensajeError = "Se ha producido un error al cargar los datos del Fidelizado";

		log.debug("cargarFidelizado() - Inicio de la carga del Fidelizado...");

		Map<String, String> mapaDatosUsuario = datosUsuarioSherpa(direccion);
		Map<String, String> mapaDatosDomicilio = datosDireccionSherpa(direccion);

		/* Realizamos las comprobaciones previas de los datos del fidelizado */
		if (mapaDatosUsuario.containsKey("nombre") && mapaDatosUsuario.containsKey("apellidos")) {
			lbNombre.setText(mapaDatosUsuario.get("nombre") + " " + mapaDatosUsuario.get("apellidos"));
		}
		else {
			erroresFidelizado = true;
		}
		if (mapaDatosDomicilio.containsKey("telefono")) {
			tfTelefono.setText(mapaDatosDomicilio.get("telefono"));
		}
		else {
			erroresFidelizado = true;
		}
		if (mapaDatosDomicilio.containsKey("email")) {
			tfEmail.setText(mapaDatosDomicilio.get("email"));
		}
		else {
			erroresFidelizado = true;
		}
		if (mapaDatosDomicilio.containsKey("direccion")) {
			tfDireccion.setText(mapaDatosDomicilio.get("direccion"));
		}
		else {
			erroresFidelizado = true;
		}
		if (mapaDatosDomicilio.containsKey("ciudad")) {
			tfPoblacion.setText(mapaDatosDomicilio.get("ciudad"));
		}
		if (mapaDatosDomicilio.containsKey("codigopostal")) {
			tfCodigoPostal.setText(mapaDatosDomicilio.get("codigopostal"));
		}
		else {
			erroresFidelizado = true;
		}
		if (mapaDatosDomicilio.containsKey("provincia")) {
			tfProvincia.setText(mapaDatosDomicilio.get("provincia"));
		}
		/*
		 * En caso de estar a "true", significa que se ha producido algún fallo en la carga de alguna de las variables
		 * del Fidelizado
		 */
		if (erroresFidelizado) {
			log.error("cargarFidelizado() - " + mensajeError);
			throw new RutasFidelizadoException(I18N.getTexto(mensajeError));
		}

		log.debug("cargarFidelizado() - Fidelizado : " + lbNombre.getText());
		log.debug("cargarFidelizado() - Finalizada la carga del Fidelizado");
	}

	public Map<String, String> datosUsuarioSherpa(DireccionTablaBean direccion) {
		/* Cargamos un mapa con los datos de usuario */
		Map<String, String> mapaDatosUsuario = new HashMap<String, String>();
		List<Campos> listadoCampos = new ArrayList<Campos>();

		/* Según de donde se venga se cargara de una forma o de otra */
		if (direccion.getDirecciones() != null) {
			listadoCampos = direccion.getDirecciones().getPersonData().getCampos();
		}
		else if (direccion.getDireccionSeleccionada() != null) {
			listadoCampos = direccion.getDireccionSeleccionada().getPersonData().getCampos();
		}

		for (Campos campoDireccion : listadoCampos) {
			if ("nombre".equals(campoDireccion.getField())) {
				mapaDatosUsuario.put("nombre", campoDireccion.getValue());
			}
			else if ("apellidos".equals(campoDireccion.getField())) {
				mapaDatosUsuario.put("apellidos", campoDireccion.getValue());
			}
			else if ("nif".equals(campoDireccion.getField())) {
				mapaDatosUsuario.put("nif", campoDireccion.getValue());
			}
		}

		return mapaDatosUsuario;
	}

	public Map<String, String> datosDireccionSherpa(DireccionTablaBean direccion) {
		/* Cargamos un mapa con los datos del domicilio */
		Map<String, String> mapaDatosDomicilio = new HashMap<String, String>();

		/* Según de donde se venga se cargara de una forma o de otra */
		if (direccion.getDirecciones() != null) {
			/* Cargamos un mapa con los datos de direccion */
			for (ListAddressData listadoDirecciones : direccion.getDirecciones().getListAddressData()) {
				/* Comprobamos que el código de sherpa es el mismo para cargar la dirección */
				if (direccion.getSherpaCodeDireccion().equals(listadoDirecciones.getSherpaAddressCode())) {
					for (Campos campoDireccion : listadoDirecciones.getCampos()) {
						if ("direccion".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("direccion", campoDireccion.getValue());
						}
						else if ("codigopostal".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("codigopostal", campoDireccion.getValue());
						}
						else if ("ciudad".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("ciudad", campoDireccion.getValue());
						}
						else if ("provincia".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("provincia", campoDireccion.getValue());
						}
						else if ("email".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("email", campoDireccion.getValue());
						}
						else if ("telefono".equals(campoDireccion.getField())) {
							mapaDatosDomicilio.put("telefono", campoDireccion.getValue());
						}
					}
				}
			}
		}
		else if (direccion.getDireccionSeleccionada() != null) {
			/* Cargamos un mapa con los datos de direccion */
			for (Campos campoDireccion : direccion.getDireccionSeleccionada().getAddressData().getCampos()) {
				if ("direccion".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("direccion", campoDireccion.getValue());
				}
				else if ("codigopostal".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("codigopostal", campoDireccion.getValue());
				}
				else if ("ciudad".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("ciudad", campoDireccion.getValue());
				}
				else if ("provincia".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("provincia", campoDireccion.getValue());
				}
				else if ("email".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("email", campoDireccion.getValue());
				}
				else if ("telefono".equals(campoDireccion.getField())) {
					mapaDatosDomicilio.put("telefono", campoDireccion.getValue());
				}
			}
		}

		return mapaDatosDomicilio;
	}

	/**
	 * Realiza una llamada a Rutas para cargar los Bultos
	 * 
	 * @return List<RutasTipoBultoBean>
	 * @throws RutasServiciosException
	 */
	public List<RutasTipoBultoBean> cargarBultos() throws RutasServiciosException {
		log.debug("cargarBultos() - Iniciamos la carga de Bultos desde Rutas....");

		List<RutasTipoBultoBean> listadoBultosCantidad = new ArrayList<RutasTipoBultoBean>();
		try {
			List<TipoBulto> listadoBultos = bultosService.bultos();
			for (TipoBulto tipoBulto : listadoBultos) {
				RutasTipoBultoBean conCantidad = new RutasTipoBultoBean();
				BeanUtils.copyProperties(conCantidad, tipoBulto);
				listadoBultosCantidad.add(conCantidad);
			}
			log.debug("cargarBultos() - " + listadoBultos);
		}
		catch (Exception e) {
			log.error("cargarBultos() - Se ha producido un error al cargar los Bultos en el sistema:" + e.getMessage());
			return sadService.getTiposBultos();
		}

		log.debug("cargarBultos() - Finalizada la carga de Bultos desde Rutas");
		return listadoBultosCantidad;
	}

	/**
	 * Genera un listado con los medios de transporte para cargarlos en pantalla
	 * 
	 * @return List<Vehiculos>
	 * @throws RutasServiciosException
	 */
	public List<Vehiculos> cargarTransportes() throws RutasServiciosException {
		log.debug("cargarTransportes() - Iniciamos la carga de Tipos de Vehículos desde Rutas....");

		List<Vehiculos> listadoVehiculos = new ArrayList<Vehiculos>();
		try {
			listadoVehiculos = vehiculosService.vehiculos();
			log.debug("cargarTransportes() - " + listadoVehiculos);
		}
		catch (Exception e) {
			log.error("cargarTransportes() - Se ha producido un error al cargar los Tipos de Vehículos en el sistema: " + e.getMessage());
			return sadService.getTiposVehiculos();
		}

		log.debug("cargarTransportes() - Finalizada la carga de Tipos de Vehículo desde Rutas");
		return listadoVehiculos;
	}

	/**
	 * Realiza la carga de los rangos de fechas disponibles para realizar la reserva
	 */
	public void cargarFechasDisponibles() {		
		log.debug("cargarFechasDisponibles() - Iniciamos la carga de fechas disponibles desde Rutas....");
		/* Comprobamos que se han seleccionado bultos previamente */
		if (Integer.parseInt(lbTotalBultos.getText()) > 0) {
			if (cbMediosTransporte.getValue() != null) {
				disponibilidades.clear();				
				/* Rellenamos el objeto que contiene los datos de petición */
				SadSolicitud pedido = rellenarPeticionDisponibles();
				/* Realizamos la llamada para pedir las disponibilidades a través de una clase Task */
				new PedirDisponibilidadTask(pedido).start();

			}
			else {
				String mensajeAviso = "Debe seleccionar algún Medio de Transporte antes de Solicitar Disponibilidad";
				log.info("cargarFechasDisponibles() - " + mensajeAviso);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeAviso), getStage());
			}
		}
		else {
			String mensajeAviso = "Debe seleccionar algún Bulto antes de Solicitar Disponibilidad";
			log.info("cargarFechasDisponibles() - " + mensajeAviso);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeAviso), getStage());
		}
	}

	public class PedirDisponibilidadTask extends BackgroundTask<DisponibilidadPedido> {

		private SadSolicitud pedido;

		public PedirDisponibilidadTask(SadSolicitud pedido) {
			super();
			this.pedido = pedido;
		}

		@Override
		protected DisponibilidadPedido call() {
			log.debug("cargarFechasDisponibles() - " + pedido);
			return pedidosService.disponibilidad(pedido);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			DisponibilidadPedido resultadoPedido = getValue();
			log.debug("RealizarPedidoTask() - " + resultadoPedido);

			/* Comprobamos que todo ha salido correctamente y procedemos a la carga de los tramos horarios */
			if (resultadoPedido != null && resultadoPedido.getFechas() != null && !resultadoPedido.getFechas().isEmpty()) {
				/* Pasamos las fechas al Bean que se ha creado para cargar la tabla */
				List<RutasFechasDisponiblesBean> listadoFechas = new ArrayList<RutasFechasDisponiblesBean>();
				for (String fecha : resultadoPedido.getFechas()) {
					RutasFechasDisponiblesBean fechaDisponible = new RutasFechasDisponiblesBean();
					fechaDisponible.setFechaOrigen(fecha);
					listadoFechas.add(fechaDisponible);
				}
				if (visor.getConfiguracion() != null) {
					((DinoVisorPantallaSecundaria) visor).modoEnvioDomicilio(listadoFechas);
				}
				/* Primero limpiamos los anteriores resultados y recargamos la tabla */
				disponibilidades.clear();
				for (RutasFechasDisponiblesBean fecha : listadoFechas) {
					disponibilidades.add(fecha);
					log.debug("initializeForm() - Fecha cargada : " + fecha.getFecha() + " " + fecha.getTramoHorario());
				}
				/* Eliminamos el Focus del primer elemento */
				tbDisponibilidad.requestFocus();
				tbDisponibilidad.getSelectionModel().select(null);
				tbDisponibilidad.getFocusModel().focus(null);
			}

			log.debug("cargarFechasDisponibles() - Finalizada la carga de fechas disponibles desde Rutas");
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("cargarFechasDisponibles() - " + exception.getMessage(), exception);
			
			if(exception instanceof RetryableException) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido conectar con el servicio de disponibilidad de Servicio a Domicilio. La fecha y hora de entrega será informada al cliente por el responsable"), getStage());
				desactivarIntegracionRutas();
			}
			else {
				String mensajeError = "Se ha producido un error consultando las fechas disponibles - " + getError(exception.getMessage());
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
			}
			
			return;
		}
	}

	/**
	 * Rellena la petición de disponibilidad de fechas a Rutas.
	 * 
	 * @return SadSolicitud
	 * @throws TicketsServiceException
	 */
	public SadSolicitud rellenarPeticionDisponibles() {
		/* Forzamos la generación del código del ticket */
		SadSolicitud pedido = new SadSolicitud();

		/* El Id de pedido es el código del ticket */
		pedido.setIdPedido(idPedido);

		Entrega entrega = new Entrega();
		entrega.setForzada(cbDireccionForzada.isSelected());
		entrega.setDireccion(tfDireccion.getText() + "," + tfCodigoPostal.getText() + "," + tfPoblacion.getText() + "," + tfProvincia.getText());
		entrega.setCodigoPostal(tfCodigoPostal.getText());

		List<Paquete> listaBultos = new ArrayList<Paquete>();
		for (RutasTipoBultoBean bulto : tbBultos.getItems()) {
			if (bulto.getCantidad() > 0) {
				Bulto bultoDTO = new Bulto();
				bultoDTO.setIdentificador(bulto.getIdentificador());

				Paquete bultosDTO = new Paquete();
				bultosDTO.setCantidad(bulto.getCantidad());
				bultosDTO.setBulto(bultoDTO);

				listaBultos.add(bultosDTO);
			}
		}
		entrega.setPaquetes(listaBultos);

		Tienda tiendas = new Tienda();
		tiendas.setIdentificador(ticket.getTienda().getCodAlmacen());

		pedido.setEntrega(entrega);
		pedido.setTienda(tiendas);

		TipoVehiculo vehiculo = new TipoVehiculo();
		vehiculo.setId(cbMediosTransporte.getValue().getVehiculo().getId());
		pedido.setTipoVehiculo(vehiculo);

		return pedido;
	}

	/**
	 * Se lanza al pulsar el botón Aceptar, al pulsar el botón realiza la reserva del pedido.
	 */
	public void accionAceptar() {
		log.debug("accionAceptar() - Iniciamos la reserva desde Rutas....");
		
		/* Comprobamos que se han seleccionado bultos y disponibilidad previamente */
		if (Integer.parseInt(lbTotalBultos.getText()) > 0) {
			if (!integracionRutas) {
				/* En caso de no estar activado la integración rutas, realizamos el xml sin algunos datos */
				if (ticketRecuperado) {
					irRecuperacionTicket(null);
				}
				else {
					irPantallaPagos(null);
				}
			}
			else {
				if (tbDisponibilidad.getSelectionModel().getSelectedItem() != null) {
					/* Rellenamos el objeto que nos permite hacer la reserva y la realizamos */
					SadReserva reserva = rellenarPeticionReserva();

					/* Realizamos el pedido en una clase Task */
					new RealizarPedidoTask(reserva).start();
				}
				else {
					String mensajeAviso = "Debe seleccionar algún Tramo Disponible antes de realizar la reserva";
					log.info("cargarFechasDisponibles() - " + mensajeAviso);
					VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeAviso), getStage());
					return;
				}
			}
		}
		else {
			String mensajeAviso = "Debe seleccionar algún Bulto antes de realizar la reserva";
			log.info("cargarFechasDisponibles() - " + mensajeAviso);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto(mensajeAviso), getStage());
			return;
		}
	}

	public class RealizarPedidoTask extends BackgroundTask<EstadoPedido> {

		private SadReserva reserva;

		public RealizarPedidoTask(SadReserva reserva) {
			super();
			this.reserva = reserva;
		}

		@Override
		protected EstadoPedido call() {
			log.debug("RealizarPedidoTask() - " + reserva);
			return pedidosService.reservapedido(reserva);
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			EstadoPedido resultadoPedido = getValue();
			log.debug("RealizarPedidoTask() - " + resultadoPedido);

			/* Si todo se ha realizado correctamente se procede a la pantalla de pagos */
			if (resultadoPedido != null) {
				if (ticketRecuperado) {
					irRecuperacionTicket(resultadoPedido);
				}
				else {
					irPantallaPagos(resultadoPedido);
				}
			}
		}

		@Override
		protected void failed() {
			super.failed();
			Throwable exception = getException();
			log.error("RealizarPedidoTask() - " + exception.getMessage(), exception);
			
			int status = ((FeignException) exception).status();
			
			if(status == 0) {
				VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido conectar con el servicio de disponibilidad de Servicio a Domicilio. La fecha y hora de entrega será informada al cliente por el responsable"), getStage());
				desactivarIntegracionRutas();
			}
			else {
				String responseContent = exception.getMessage();
				try {
					String[] feignExceptionMessageParts = exception.getMessage().split("\n");
					responseContent = feignExceptionMessageParts[1];
					Gson gson = new Gson();
					FeignContent fromJson = gson.fromJson(responseContent, FeignContent.class);
					responseContent = fromJson.getDetail();
				}
				catch(Exception ignore) {}
				
				String mensajeError = I18N.getTexto("Ha habido un error al realizar el pedido en la comunicación con Rutas.") + System.lineSeparator() + System.lineSeparator() + responseContent;
				VentanaDialogoComponent.crearVentanaError(getStage(), mensajeError, exception);
			}
			
			return;
		}
	}

	public void irPantallaPagos(EstadoPedido resultadoPedido) {
		/* Rellenamos el objeto que vamos a cargar en el ticket */
		RutasTicketBean objetoTicket = cargarBeanTicket(resultadoPedido);
		((DinoCabeceraTicket) ticket.getCabecera()).setDatosSad(objetoTicket);
		
		/* Para controlar si usar los servicios de envio a domicilio o no */
		getDatos().put(EnvioDomicilioKeys.CONTROLAR_SERVICIOS_SAD, integracionRutas);
		
		getStage().close();
	}

	public void accionCancelar() {
		getDatos().put(EnvioDomicilioKeys.CODPEDIDO, idPedido);
		getDatos().put(PARAM_CANCELAR, Boolean.TRUE);
		getStage().close();
	}

	/**
	 * Rellena un objeto de tipo "SadReserva" para poder realizar una reserva
	 * 
	 * @return SadReserva
	 */
	public SadReserva rellenarPeticionReserva() {
		SadReserva reserva = new SadReserva();
		/* El Id de pedido es el código del ticket ya generado anteriormente */
		reserva.setIdPedido(idPedido);

		EntregaReserva entrega = new EntregaReserva();
		entrega.setForzada(cbDireccionForzada.isSelected());
		entrega.setDireccion(tfDireccion.getText());
		entrega.setCodigoPostal(tfCodigoPostal.getText());
		/* Generamos la fecha a partir de la seleccionada */
		RutasFechasDisponiblesBean fechaSeleccionada = tbDisponibilidad.getSelectionModel().getSelectedItem();
		String arrayFechas[] = fechaSeleccionada.getFechaOrigen().split("\\+");

		LocalDateTime localTime = LocalDateTime.parse(arrayFechas[0]);
		String zona[] = arrayFechas[1].split(":");
		ZoneOffset zone = ZoneOffset.ofHours(Integer.parseInt(zona[0]));

		OffsetDateTime ejemplo = OffsetDateTime.of(localTime, zone);
		entrega.setFecha(ejemplo);

		entrega.setEmail(tfEmail.getText());
		entrega.setTelefono(tfTelefono.getText());
		entrega.setContacto(lbNombre.getText());
		entrega.setObservacion(taObservaciones.getText());

		List<Paquete> listaBultos = new ArrayList<Paquete>();
		for (RutasTipoBultoBean bulto : tbBultos.getItems()) {
			if (bulto.getCantidad() > 0) {
				Bulto bultoDTO = new Bulto();
				bultoDTO.setIdentificador(bulto.getIdentificador());

				Paquete bultosDTO = new Paquete();
				bultosDTO.setCantidad(bulto.getCantidad());
				bultosDTO.setBulto(bultoDTO);

				listaBultos.add(bultosDTO);
			}
		}
		entrega.setPaquetes(listaBultos);

		Tienda tiendas = new Tienda();
		tiendas.setIdentificador(ticket.getTienda().getCodAlmacen());

		reserva.setEntrega(entrega);
		reserva.setTienda(tiendas);

		TipoVehiculo vehiculo = new TipoVehiculo();
		vehiculo.setId(cbMediosTransporte.getValue().getVehiculo().getId());
		reserva.setTipoVehiculo(vehiculo);

		return reserva;
	}

	/**
	 * Cargamos el objeto que va a formar la cabecera del ticket.
	 * 
	 * @return
	 */
	public RutasTicketBean cargarBeanTicket(EstadoPedido resultadoPedido) {
		RutasTicketBean contenidoTicket = new RutasTicketBean();
		try {
			if (resultadoPedido != null) {
				contenidoTicket.setCodTicket(resultadoPedido.getIdPedido());

				/* Cargamos el medio de transporte seleccionado en el comboBox */
				contenidoTicket.setRutaVehiculo(resultadoPedido.getIdRuta());
				contenidoTicket.setIdVehiculo(resultadoPedido.getIdVehiculo());

				RutasFechasDisponiblesBean fechaEntrega = tbDisponibilidad.getSelectionModel().getSelectedItem();
				contenidoTicket.setFechaEntrega(fechaEntrega.getFecha());
				String hora[] = fechaEntrega.getTramoHorario().split("\\+");
				contenidoTicket.setHoraEntrega(hora[0]);
			}
			else {
				contenidoTicket.setCodTicket(idPedido);

				contenidoTicket.setRutaVehiculo("");
				contenidoTicket.setIdVehiculo("");

				contenidoTicket.setFechaEntrega("");
				contenidoTicket.setHoraEntrega("");
			}
			
			if(ticket != null) {
				if(StringUtils.isNotBlank(ticket.getUidTicket())) {
					contenidoTicket.setUidTicketCzz(ticket.getUidTicket());
				}
				
				if(StringUtils.isNotBlank(ticket.getCabecera().getCodTicket())) {
					contenidoTicket.setCodTicketCzz(ticket.getCabecera().getCodTicket());
				}
				
				if(StringUtils.isNotBlank(ticket.getCabecera().getCajero().getUsuario())) {
					contenidoTicket.setCajero(ticket.getCabecera().getCajero().getUsuario());
				}
			}

			contenidoTicket.setMedioTransporte(cbMediosTransporte.getSelectionModel().getSelectedItem().getNombre());

			contenidoTicket.setTelefono(tfTelefono.getText());
			contenidoTicket.setNombreCompleto(lbNombre.getText());
			contenidoTicket.setDireccion(tfDireccion.getText());
			contenidoTicket.setCp(tfCodigoPostal.getText());
			contenidoTicket.setPoblacion(tfPoblacion.getText());
			contenidoTicket.setProvincia(tfProvincia.getText());
			contenidoTicket.setEmail(tfEmail.getText());

			contenidoTicket.setImporteTotal(ticket.getTotales().getTotalAPagar());
			contenidoTicket.setObservaciones(taObservaciones.getText());

			contenidoTicket.setIdFidelizado(direccion.getDirecciones().getPersonData().getSherpaCode());

			/* Creamos la fecha de compra en la fecha actual */
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy+HH:mm:ss");
			String fechaCompra = simpleDateFormat.format(new Date());
			String arrayFechaCompra[] = fechaCompra.split("\\+");
			contenidoTicket.setFechaCompra(arrayFechaCompra[0]);
			contenidoTicket.setHoraCompra(arrayFechaCompra[1]);

			List<RutasTipoBultoBean> listadoTipoBulto = new ArrayList<RutasTipoBultoBean>();
			Integer cantidadTotalBultos = 0;
			for (RutasTipoBultoBean bulto : bultos) {
				cantidadTotalBultos = cantidadTotalBultos + bulto.getCantidad();
				if (bulto.getCantidad() > 0) {
					String nombre = bulto.getNombre();
					bulto.setNombre(nombre.toUpperCase());
					listadoTipoBulto.add(bulto);
				}
			}
			contenidoTicket.setListadoTiposBultos(listadoTipoBulto);

			List<RutasBultosBoletaBean> listadoBultos = new ArrayList<RutasBultosBoletaBean>();
			Integer matriculaControl = new Random().nextInt(9999999);
			Integer secuencialTipoBulto = 0;
			for (RutasTipoBultoBean bulto : bultos) {
				if (bulto.getCantidad() > 0) {
					/* Generamos un número aleatorio con 6 dígitos */
					for (int i = 0; i < bulto.getCantidad(); i++) {
						secuencialTipoBulto++;
						RutasBultosBoletaBean datosBulto = new RutasBultosBoletaBean();
						datosBulto.setNombre(bulto.getNombre().toUpperCase());
						datosBulto.setCantidad(1);
						datosBulto.setNumeroBulto(secuencialTipoBulto + "/" + cantidadTotalBultos);
						datosBulto.setMatriculaControl(matriculaControl);
						/*
						 * Generamos el código de barras a partir de un número secuencial por tipo, el código del bulto
						 * y el código del ticket
						 */
						String codigoBarras = StringUtils.leftPad(secuencialTipoBulto.toString(), 2, '0') + bulto.getCodigo() + ticket.getCabecera().getCodTicket();
						datosBulto.setCodBar(codigoBarras);

						listadoBultos.add(datosBulto);
					}
				}
			}
			contenidoTicket.setListadoBultos(listadoBultos);
			contenidoTicket.setTotalBultos(Integer.parseInt(lbTotalBultos.getText()));

			contenidoTicket.setTotalPorte(importeTotalPortes);
			
			contenidoTicket.setDireccionForzada(cbDireccionForzada.isSelected());

			if (!taObservaciones.getText().isEmpty()) {
				contenidoTicket.setObservaciones(taObservaciones.getText());
			}
			else {
				contenidoTicket.setObservaciones("");
			}
		}
		catch (Exception e) {
			String mensajeError = "Se ha producido un error al cargar los datos para el ticket";
			log.error("cargarBeanTicket()- " + mensajeError);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());
		}

		return contenidoTicket;
	}

	/**
	 * Bloquea los campos del formulario que contiene los datos del Fidelizado.
	 */
	public void bloquearCampos() {
		tfTelefono.setEditable(false);
		tfEmail.setEditable(false);
		tfDireccion.setEditable(false);
		tfPoblacion.setEditable(false);
		tfCodigoPostal.setEditable(false);
		tfProvincia.setEditable(false);
	}

	/**
	 * Limpia los datos de pantalla que se tienen que recargar.
	 */
	public void limpiarDatosPantalla() {
		/* TextArea */
		taObservaciones.clear();
		/* Labels */
		lbNombre.setText("");
		lbPorte.setText("");
		lbTotalBultos.setText("");
		/* TextFields */
		tfTelefono.clear();
		tfEmail.clear();
		tfDireccion.clear();
		tfPoblacion.clear();
		tfCodigoPostal.clear();
		tfProvincia.clear();
		/* Listados de las tablas */
		disponibilidades.clear();
		cbMediosTransporte.getSelectionModel().selectFirst();
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

	/**
	 * Permite lanzar la acción aceptar al pulsar el "Enter".
	 * 
	 * @param e
	 */
	public void accionAceptarIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			accionAceptar();
		}
	}

	@FXML
	public void altaDireccion() {
		/* Para poder modificar necesitamos tener seleccionado algo de la tabla */
		getDatos().put(EnvioDomicilioKeys.IR_ALTA_DIRECCION_SAD, direccion.getDirecciones());
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());

		if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
			getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
			return;
		}

		DomicilioResponse respuestaAlta = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
		direccion.setDirecciones(respuestaAlta);
		direccion.setSherpaCodeDireccion((String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));

		try {
			cargarFidelizado(direccion);
		}
		catch (RutasFidelizadoException e) {
			log.error("altaDireccion() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
		}
		/* Limpiamos la tabla de disponibilidades después de cambiar de dirección, para tener que pedirlas de nuevo */
		disponibilidades.clear();
	}

	@FXML
	public void modificarDireccion() {
		/* Para poder modificar necesitamos tener seleccionado algo de la tabla */
		getDatos().put(EnvioDomicilioKeys.UPDATE_DIRECCION_SAD, direccion);
		getApplication().getMainView().showModal(AltaUsuarioSADView.class, getDatos());

		if (getDatos().containsKey(PagosController.ACCION_CANCELAR)) {
			getDatos().put(PagosController.ACCION_CANCELAR, Boolean.TRUE);
			return;
		}

		DomicilioResponse respuestaModificar = (DomicilioResponse) getDatos().get(EnvioDomicilioKeys.ALTA_DIRECCION_SAD);
		direccion.setDirecciones(respuestaModificar);
		direccion.setSherpaCodeDireccion((String) getDatos().get(EnvioDomicilioKeys.SHERPA_ADDRESS));

		try {
			cargarFidelizado(direccion);
		}
		catch (RutasFidelizadoException e) {
			log.error("modificarDireccion() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());
		}
		/* Limpiamos la tabla de disponibilidades después de cambiar de dirección, para tener que pedirlas de nuevo */
		disponibilidades.clear();
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			accionAceptar();
		}
	}

	/**
	 * Guarda en BD el documento y el ticket del servicio a domicilio.
	 * 
	 * @param datosTicket
	 *            : Datos para completar el ticket.
	 * @throws RutasServiciosException
	 */
	public void salvarDocumentoSAD(RutasTicketBean datosTicket) throws RutasServiciosException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			TicketBean ticketXML = new TicketBean();
			ticketXML.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
			ticketXML.setCodcaja(sesion.getAplicacion().getCodCaja());
			ticketXML.setCodTicket("*");
			ticketXML.setFecha(new Date());
			ticketXML.setFirma("*");
			ticketXML.setIdTicket(contadoresService.obtenerValorContador(EnvioDomicilioKeys.CONTADOR_SAD, sesion.getAplicacion().getUidActividad()));
			ticketXML.setIdTipoDocumento(((DinosolTipoDocumentoService) tipoDocumentoService).consultar(sesion.getAplicacion().getUidActividad(),
			        sesion.getAplicacion().getTienda().getCliente().getCodpais(), EnvioDomicilioKeys.COD_TIPO_DOC_SAD).getIdTipoDocumento());
			ticketXML.setSerieTicket("*");
			ticketXML.setTicket(generarXMLTicketSad(datosTicket));
			ticketXML.setUidActividad(sesion.getAplicacion().getUidActividad());
			ticketXML.setUidTicket(UUID.randomUUID().toString());
			ticketXML.setLocatorId(ticketXML.getUidTicket());

			ticketService.insertarTicket(sqlSession, ticketXML, false);
			
			TicketAnexoSadBean ticketAnexoSad = new TicketAnexoSadBean();
			ticketAnexoSad.setUidActividad(sesion.getAplicacion().getUidActividad());
			ticketAnexoSad.setUidTicket(ticket.getUidTicket());
			ticketAnexoSad.setUidTicketSad(ticketXML.getUidTicket());
			
			ticketAnexoSadService.crear(sqlSession, ticketAnexoSad);
			
			sqlSession.commit();
		}
		catch (MarshallUtilException e) {
			sqlSession.rollback();
			log.error("salvarDocumentoSAD() - " + e.getMessage());
			throw new RutasServiciosException(e.getMessage());
		}
		catch (Exception e) {
			sqlSession.rollback();
			String mensajeError = "Se ha producido un error al guardar el ticket (" + ticketManager.getTicketOrigen().getUidTicket() + ")";
			log.error("salvarDocumentoSAD() - " + mensajeError + " : " + e.getMessage(), e);
			throw new RutasServiciosException(e.getMessage());
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Genera el XML para el documento de servicio a domicilio.
	 * 
	 * @param datosTicket
	 *            : Datos que contendrá el ticket.
	 * @return byte[]
	 * @throws MarshallUtilException
	 */
	public byte[] generarXMLTicketSad(RutasTicketBean datosTicket) throws MarshallUtilException {
		byte[] result = null;
		try {
			result = MarshallUtil.crearXML(datosTicket);
		}
		catch (MarshallUtilException e) {
			String mensajeError = "Se ha producido un error al generar el Ticket de Servicio a Domicilio";
			log.error("generarXMLTicketSad() - " + mensajeError + " : " + e.getMessage(), e);
			throw new MarshallUtilException(mensajeError);
		}
		return result;
	}

	public void irRecuperacionTicket(EstadoPedido resultadoPedido) {
		/* Rellenamos el objeto que vamos a cargar en el ticket */
		RutasTicketBean objetoTicket = cargarBeanTicket(resultadoPedido);
		try {
			log.debug("RealizarPedidoTask() - Iniciamos el guardado del Ticket de SAD en BD...");
			salvarDocumentoSAD(objetoTicket);
			log.debug("RealizarPedidoTask() - Finalizado el guardado del Ticket de SAD en BD");

			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			objetoTicket.setLocalizador(ticket.getCabecera().getLocalizador());
			mapaParametros.put("datosSAD", objetoTicket);

			log.debug("RealizarPedidoTask() - Iniciamos la impresión del Ticket de SAD...");
			ServicioImpresion.imprimir(EnvioDomicilioKeys.PLANTILLA_SERVICIO_DOMICILIO, mapaParametros);
			log.debug("RealizarPedidoTask() - Finalizada la impresión del Ticket de SAD");

			getStage().close();
		}
		catch (DeviceException e) {
			String mensajeError = "Se ha producido un error al realizar la impresión del Ticket";
			log.error("RealizarPedidoTask() - " + mensajeError + " - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());

			/*
			 * En caso de producirse error en alguno de estos puntos, se deberá generar otra vez el Id del pedido, y
			 * deberemos vaciar la tabla de disponibilidad
			 */
			generarIdPedido(idPedido);
			disponibilidades.clear();
			return;
		}
		catch (RutasServiciosException e) {
			log.error("RealizarPedidoTask() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(e.getMessage()), getStage());

			generarIdPedido(idPedido);
			disponibilidades.clear();
			return;
		}
		catch (Exception e) {
			String mensajeError = "Se ha producido un error al realizar el guardado del Ticket";
			log.error("RealizarPedidoTask() - " + mensajeError);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto(mensajeError), getStage());

			generarIdPedido(idPedido);
			disponibilidades.clear();
			return;
		}
	}

	private void activarIntegracionRutas() {
		integracionRutas = true;
		lbTitulo.setText(I18N.getTexto("Envío a Domicilio"));
		btSolicitarDisponibilidad.setDisable(false);
		apTitulo.getStyleClass().clear();
		apTitulo.getStyleClass().addAll("titulo-ventana", "cabecera-venta");
	}

	private void desactivarIntegracionRutas() {
		integracionRutas = false;
		lbTitulo.setText(I18N.getTexto("Envío a Domicilio (sin conexión)"));
		btSolicitarDisponibilidad.setDisable(true);
		apTitulo.getStyleClass().clear();
		apTitulo.getStyleClass().addAll("titulo-ventana", "cabecera-offline");
	}

	private void comprobarRutasActivadas() {
		integracionRutas = true;
		if (Dispositivos.getInstance().getFidelizacion() != null) {
			if (Dispositivos.getInstance().getFidelizacion() instanceof DinoFidelizacion) {
				/* Comprobaciones de null por si el dispositivo no está configurado correctamente */
				if (((DinoFidelizacion) Dispositivos.getInstance().getFidelizacion()).getCodigoArticuloPorte() != null) {
					if ("N".equals(((DinoFidelizacion) Dispositivos.getInstance().getFidelizacion()).getIntegracionRutas())) {
						desactivarIntegracionRutas();
					}
				}
			}
		}
	}


	private void crearDireccionOffline(DomicilioResponse domicilio) {
		direccion = new DireccionTablaBean();
		
		domicilio.setPersonData(new PersonData());
		domicilio.getPersonData().setSherpaCode("");
		direccion.setDirecciones(domicilio);

		tfTelefono.setText(((DomicilioOfflineDto)domicilio).getTelefono());
		tfEmail.setText(((DomicilioOfflineDto)domicilio).getEmail());
		tfDireccion.setText(((DomicilioOfflineDto)domicilio).getDireccion());
		tfPoblacion.setText(((DomicilioOfflineDto)domicilio).getPoblacion());
		tfCodigoPostal.setText(((DomicilioOfflineDto)domicilio).getCp());
		tfProvincia.setText(((DomicilioOfflineDto)domicilio).getProvincia());
	}
	
	private class FeignContent {
		private String detail;
		
		public String getDetail() {
			return detail;
		}
		public void setDetail(String detail) {
			this.detail = detail;
		}
		
	}

}
