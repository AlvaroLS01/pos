package com.comerzzia.iskaypet.pos.devices.fidelizacion.gestion;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.tickets.TicketLocalizadorRequestRest;
import com.comerzzia.api.rest.client.tickets.TicketsRest;
import com.comerzzia.core.servicios.ventas.tickets.TicketException;
import com.comerzzia.core.servicios.ventas.tickets.TicketNotFoundException;
import com.comerzzia.iskaypet.pos.gui.ventas.devoluciones.IskaypetDevolucionesController;
import com.comerzzia.iskaypet.pos.gui.ventas.gestiontickets.IskaypetGestionTicketGui;
import com.comerzzia.iskaypet.pos.persistence.ticket.comprasfidelizado.ComprasFidelizado;
import com.comerzzia.iskaypet.pos.services.ticket.comprasfidelizados.ComprasFidelizadosService;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.simple.BotonBotoneraSimpleComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.ConfiguracionBotonBean;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.dispositivo.fidelizacion.busqueda.BusquedaFidelizadoController;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionTicketView;
import com.comerzzia.pos.gui.ventas.gestiontickets.detalle.DetalleGestionticketsController;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.clientes.ClienteNotFoundException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

@Primary
@Component
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GestionTicketsFidelizadoController extends WindowController implements Initializable, IContenedorBotonera {

	public static final String PARAMETRO_LOCALIZADOR_DEVOLUCION = "PARAMETRO_LOCALIZADOR_DEVOLUCION";

	private static final Logger log = Logger.getLogger(GestionTicketsFidelizadoController.class.getName());

	@FXML
	protected TableView<IskaypetGestionTicketGui> tableGestionGui;

	@FXML
	protected Label lbClienteFid, lbTituloVentasFid;

	@FXML
	protected TableColumn tcFecha, tcCaja, tcCodAlmacen, tcTienda, tcTipoDoc, tcTicket;

	@FXML
	protected Button btIniciarDev, btCancDev, btMostratTicket;

	protected BotoneraComponent botoneraAccionesTabla;

	@FXML
	protected AnchorPane panelBotonesVentas;

	boolean devolucionAfidelizado = false;

	// - lista de comprasFidelizado
	protected ObservableList<IskaypetGestionTicketGui> comprasFiltradas;

	@Autowired
	protected ComprasFidelizadosService comprasFidelizadosService;

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private Sesion sesion;

	@FXML
	protected Label lbTitulo;

	protected FidelizadoBean fidelizado;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize()");

		String dias = variablesServices.getVariableAsString("X_API.DIAS_CONSULTA_VENTAS_FIDELIZADO");

		lbTitulo.setText(I18N.getTexto("Devoluciones cliente"));
		lbTituloVentasFid.setText(I18N.getTexto("Ventas realizadas en los últimos ") + dias + I18N.getTexto(" días (últimos 50 registros)"));
		lbClienteFid.setText("");
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {

		log.debug("inicializarComponentes()");
		// Se registra el evento para salir de la pantalla pulsando la tecla escape.
		registrarAccionCerrarVentanaEscape();
		crearEventoEnterTabla(tableGestionGui);
		crearEventoNavegacionTabla(tableGestionGui);

		try {
			List<ConfiguracionBotonBean> listaAccionesAccionesTabla = new LinkedList<>();

			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up2.png", null, null, "ACCION_TABLA_PRIMER_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_up.png", null, null, "ACCION_TABLA_ANTERIOR_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down.png", null, null, "ACCION_TABLA_SIGUIENTE_REGISTRO", ""));
			listaAccionesAccionesTabla.add(new ConfiguracionBotonBean("iconos/navigate_down2.png", null, null, "ACCION_TABLA_ULTIMO_REGISTRO", ""));

			botoneraAccionesTabla = new BotoneraComponent(4, 1, this, listaAccionesAccionesTabla, panelBotonesVentas.getPrefWidth(), panelBotonesVentas.getPrefHeight(),
			        BotonBotoneraSimpleComponent.class.getName());
			panelBotonesVentas.getChildren().clear();
			panelBotonesVentas.getChildren().add(botoneraAccionesTabla);

		}
		catch (CargarPantallaException ex) {
			log.error("inicializarComponentes() - Error inicializando pantalla de gestiond e tickets");
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), getStage());
		}
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm()");

		List<IskaypetGestionTicketGui> listaCompras = new ArrayList<IskaypetGestionTicketGui>();

		if (datos.containsKey(BusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO)) {
			fidelizado = (FidelizadoBean) getDatos().get(BusquedaFidelizadoController.PARAMETRO_FIDELIZADO_SELECCIONADO);
		}
		else {
			List<FidelizadoBean> fidelizados = new ArrayList<FidelizadoBean>();
			if (datos.containsKey(BusquedaFidelizadoController.PARAMETRO_FIDELIZADOS_SELECCION)) {
				fidelizados = (List<FidelizadoBean>) getDatos().get(BusquedaFidelizadoController.PARAMETRO_FIDELIZADOS_SELECCION);
			}
			if (fidelizados.size() == 1) {
				fidelizado = fidelizados.get(0);
			}
			else {
				fidelizado = null;
			}
		}

		lbClienteFid.setText("Cliente: " + fidelizado.getNombre() + " " + fidelizado.getApellidos());

		try {
			listaCompras = extraerDatosCompraFidelizado();
		}
		catch (ClienteNotFoundException e) {
			log.error("initialize() - clienteNotFound " + e.getMessage(), e);
		}
		catch (TicketException e) {
			log.error("initialize() - errot en ticket " + e.getMessage(), e);

		}
		catch (TicketNotFoundException e) {
			log.error("initialize() - ticketNotFound " + e.getMessage(), e);

		}
		catch (RestException e) {
			log.error("initialize() - " + e.getMessage(), e);

		}
		catch (RestHttpException e) {
			log.error("initialize() - " + e.getMessage(), e);

		}

		tableGestionGui.setPlaceholder(new Text(""));

		comprasFiltradas = FXCollections.observableArrayList(listaCompras);
		tableGestionGui.setItems(comprasFiltradas);

		tcFecha.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcFecha", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcFecha.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("fecha"));

		tcCaja.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcCaja", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCaja.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("caja"));

		tcCodAlmacen.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcCodAlmacen", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcCodAlmacen.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("codalm"));

		tcTienda.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcTienda", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcTienda.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("tienda"));

		tcTipoDoc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcTipoDoc", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcTipoDoc.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("tipoDoc"));

		tcTicket.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableGestionGui", "tcTicket", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
		tcTicket.setCellValueFactory(new PropertyValueFactory<IskaypetGestionTicketGui, String>("idTicket"));
		
		tableGestionGui.getSelectionModel().selectFirst();
	}

	@Override
	public void initializeFocus() {
	}

	@FXML
	public void iniciarDevolucion() throws InitializeGuiException {
		if (VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("¿Está seguro de inciar la devolución?"), getStage())) {
			IskaypetGestionTicketGui ticketSeleccionado = (IskaypetGestionTicketGui) tableGestionGui.getSelectionModel().getSelectedItem();
			getDatos().put(PARAMETRO_LOCALIZADOR_DEVOLUCION, ticketSeleccionado.getLocatorId());
			getStage().close();
		}
	}

	@FXML
	public void cancelarDevolucion() {
		log.info("Devolución cancelada");
		getStage().close();
	}

	@FXML
	protected void mostrarTicket() {
		if (tableGestionGui.getItems() != null && tableGestionGui.getItems() != null) {
			IskaypetGestionTicketGui lineaTicket = tableGestionGui.getSelectionModel().getSelectedItem();
			if (lineaTicket != null) {
				HashMap<String, Object> datosTicket = new HashMap<String, Object>();
				datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKETS, tableGestionGui.getItems());
				datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_POSICION_TICKET, tableGestionGui.getSelectionModel().getSelectedIndex());

				if (devolucionAfidelizado) {
					datosTicket.put(IskaypetDevolucionesController.PARAMETRO_DEVOLUCION_FIDELIZADO, devolucionAfidelizado);
				}

				String apikey = variablesServices.getVariableAsString("WEBSERVICES.APIKEY");

				BackgroundTask<String> task = new BackgroundTask<String>(){

					@Override
					protected String call() throws Exception {
						TicketLocalizadorRequestRest request = new TicketLocalizadorRequestRest(sesion.getAplicacion().getUidActividad(), apikey, lineaTicket.getLocatorId(),
						        lineaTicket.getIdTipoDoc());
						return TicketsRest.recuperarTicketLocalizador(request);
					}

					@Override
					protected void succeeded() {
						super.succeeded();

						String ticket = getValue();

						if (StringUtils.isNotBlank(ticket)) {
							datosTicket.put(DetalleGestionticketsController.CLAVE_PARAMETRO_TICKET_XML, ticket);
						}

						getApplication().getMainView().showModalCentered(DetalleGestionTicketView.class, datosTicket, getStage());
					}

					@Override
					protected void failed() {
						super.failed();

						Throwable e = getException();
						log.error("mostrarTicket:failed() - Error recuperando el ticket: " + e.getMessage(), e);

						VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("No se ha podido recuperar el ticket para imprimir. Contacte con un administrador"), e);
					}

				};
				task.start();
			}
		}

	}

	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
		log.debug("realizarAccion() - Realizando la acción : " + botonAccionado.getClave() + " de tipo : " + botonAccionado.getTipo());
		switch (botonAccionado.getClave()) {
			case "ACCION_TABLA_PRIMER_REGISTRO":
				accionTablaPrimerRegistro(tableGestionGui);
				break;
			case "ACCION_TABLA_ANTERIOR_REGISTRO":
				accionTablaIrAnteriorRegistro(tableGestionGui);
				break;
			case "ACCION_TABLA_SIGUIENTE_REGISTRO":
				accionTablaIrSiguienteRegistro(tableGestionGui);
				break;
			case "ACCION_TABLA_ULTIMO_REGISTRO":
				accionTablaUltimoRegistro(tableGestionGui);
				break;
			default:
				log.error("No se ha especificado acción en pantalla para la operación :" + botonAccionado.getClave());
		}
	}

	private List<IskaypetGestionTicketGui> extraerDatosCompraFidelizado() throws ClienteNotFoundException, RestException, RestHttpException, TicketException, TicketNotFoundException {

		List<IskaypetGestionTicketGui> lista = new ArrayList<IskaypetGestionTicketGui>();

		String apiKey = variablesServices.getVariableAsString("WEBSERVICES.APIKEY");
	
		String tarjetasFid = tarjetasFidelizado();
		
		List<ComprasFidelizado> compras = comprasFidelizadosService.getComprasFidelizado(apiKey, sesion.getAplicacion().getUidActividad(),tarjetasFid);

		for (ComprasFidelizado cf : compras) {
			TicketBean ticketBean = ticketVentasToTicketPos(cf);
			getUltimasCompras(lista, cf, ticketBean);
		}	

		return lista;
	}

	private String tarjetasFidelizado() {
		String tarjetasFid = "";
		
		if(fidelizado.getTarjetas()!=null) {
			tarjetasFid = fidelizado.getTarjetas().get(0).getNumeroTarjeta();
			if(fidelizado.getTarjetas().size()>1) {	
				for (int i = 1; i < fidelizado.getTarjetas().size(); i++) {
					tarjetasFid += "," + fidelizado.getTarjetas().get(i).getNumeroTarjeta() ;
				}
			}
		}else {
			tarjetasFid = fidelizado.getNumeroTarjeta();
		}
		return tarjetasFid;
	}

	private TicketBean ticketVentasToTicketPos(ComprasFidelizado cf) {
		TicketBean ticketBean = new TicketBean();

		ticketBean.setUidActividad(cf.getUidActividad());
		ticketBean.setUidTicket(cf.getUidTicket());
		ticketBean.setCodAlmacen(cf.getCodalm());
		ticketBean.setIdTicket(cf.getIdTicket());
		ticketBean.setFecha(cf.getFecha());
		ticketBean.setCodcaja(cf.getCodcaja());
		ticketBean.setIdTipoDocumento(cf.getIdTipoDocumento());
		ticketBean.setCodTicket(cf.getCodAlbaran());
		ticketBean.setFirma(cf.getFirma());
		// ticketBean.setTicket(cf.getTicket());
		ticketBean.setSerieTicket(cf.getSerieTicket());
		ticketBean.setLocatorId(cf.getLocatorId());
		return ticketBean;
	}

	private void getUltimasCompras(List<IskaypetGestionTicketGui> lista, ComprasFidelizado cf, TicketBean ticketBean) {
		IskaypetGestionTicketGui gestion = new IskaypetGestionTicketGui(ticketBean);
		gestion.setCodalm(ticketBean.getCodAlmacen());
		gestion.setTienda(cf.getTienda());
		gestion.setLocatorId(ticketBean.getLocatorId());
		gestion.setTipoDoc(cf.getTipoDoc());
		gestion.setIdTipoDoc(cf.getIdTipoDocumento());

		lista.add(gestion);
	}
}