package com.comerzzia.pos.gui.ventas.identificada.cliente;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.base.Estado;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonaccion.normal.BotonBotoneraNormalComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.BotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.botonera.PanelBotoneraBean;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.exception.CargarPantallaException;
import com.comerzzia.pos.core.gui.view.View;
import com.comerzzia.pos.gui.mantenimientos.clientes.MantenimientoClienteController;
import com.comerzzia.pos.gui.mantenimientos.clientes.MantenimientoClienteView;
import com.comerzzia.pos.gui.ventas.identificada.venta.VentaIdentificadaController;
import com.comerzzia.pos.gui.ventas.identificada.venta.VentaIdentificadaView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ClienteGui;
import com.comerzzia.pos.gui.ventas.tickets.clientes.ConsultaClienteController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketController;
import com.comerzzia.pos.gui.ventas.tickets.recuperacionTickets.RecuperarTicketView;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.impuestos.tratamientos.TratamientoImpuestoBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.services.clientes.ClientesService;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.impuestos.tratamientos.TratamientoImpuestoService;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.sesion.SesionInitException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentNotFoundException;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentService;
import com.comerzzia.pos.services.core.tiposIdent.TiposIdentServiceException;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.giftcard.GiftCardService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
@Component
public class IdentificacionClienteController extends Controller implements Initializable, IContenedorBotonera {

	protected Logger log = Logger.getLogger(getClass());

	@FXML
	protected Label lbTitulo;
	
	@FXML
	protected AnchorPane panelBotonera;
	@FXML
	protected ComboBox<TiposIdentBean> cbTipoDocIdent;
	@FXML
	protected TextField tfNumDocIdent, tfDescripcion, tfNombre, tfDireccion, tfPoblacion, tfProvincia, tfLocalidad, tfCodigo, tfTelefono1, tfTelefono2, tfFax, tfEmail, tfCodPais, tfDesPais,
	        tfTratImpuesto, tfTarifa;
	@FXML
	protected TextArea taObservaciones;

	protected BotoneraComponent botonera;
	protected ObservableList<TiposIdentBean> tiposIdent;

	protected ClienteBean cliente;
	protected boolean vueltaEdicion;
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	private TiposIdentService tiposIdentService;
	
	@Autowired
	private ClientesService clientesService;
	
	@Autowired
	private PaisService paisService;
	
	@Autowired
	private TratamientoImpuestoService tratamientoImpuestoService;
	
	@Autowired
	private CopiaSeguridadTicketService copiaSeguridadTicketService;
	
	@Autowired
	private GiftCardService giftCardService;
	
	protected boolean vProfesional;
	
    final IVisor visor = Dispositivos.getInstance().getVisor();

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		try {
			PanelBotoneraBean panelBotoneraBean = getView().loadBotonera();
			botonera = new BotoneraComponent(panelBotoneraBean, panelBotonera.getPrefWidth(), panelBotonera.getPrefHeight(), this, BotonBotoneraNormalComponent.class);
			panelBotonera.getChildren().add(botonera);
		
			taObservaciones.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.TAB) {
						cbTipoDocIdent.requestFocus();
					}
				}
			});
			
			consultarCopiaSeguridad();
		}
		catch (InitializeGuiException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		}
		catch (CargarPantallaException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		}
        catch (DocumentoException e) {
        	log.error("initializeComponents() - Error al consultar la copia de seguridad del ticket: " + e.getMessage());
        }
        catch (TicketsServiceException e) {
        	log.error("initializeComponents() - Error al consultar la copia de seguridad del ticket: " + e.getMessage());
        }
	}
	
	protected void consultarCopiaSeguridad() throws DocumentoException, TicketsServiceException {
	    // Comprobamos si existens copias de seguridad de tickets en base de datos para esta pantalla y si es así ofrecemos
	    // la posibilidad de recuperarlo
		String tipoDocumentoCopia = "";
		if(vProfesional){
			tipoDocumentoCopia = Documentos.FACTURA_COMPLETA;
		}
		else{
			tipoDocumentoCopia = Documentos.FACTURA_SIMPLIFICADA;
		}
		
	    TipoDocumentoBean tipoDocumentoActivo = sesion.getAplicacion().getDocumentos().getDocumento(tipoDocumentoCopia);//Documentos.FACTURA_SIMPLIFICADA);
	    final TicketAparcadoBean copiaSeguridad = copiaSeguridadTicketService.consultarCopiaSeguridadTicket(tipoDocumentoActivo);
	    TicketManager ticketManager = getNuevoTicketManager();
	    if(copiaSeguridad != null && !tieneArticuloGiftCard(copiaSeguridad, tipoDocumentoActivo, ticketManager)) {
	    	if(VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Existe una venta sin finalizar. ¿Desea recuperarla?"), getStage())){ 
				try {
					ticketManager.recuperarCopiaSeguridadTicket(getStage(), copiaSeguridad);
					getDatos().put(VentaIdentificadaController.PARAMETRO_TICKET_MANAGER, ticketManager);
                    abrirPantallaVentas();
                }
                catch (Exception e) {
                	log.error("consultarCopiaSeguridad() - Error al inicializar la pantalla de tickets: " + e.getMessage());
                	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el ticket"), getStage());
                }
				catch(Throwable t) {
                	log.error("consultarCopiaSeguridad() - Error al inicializar la pantalla de tickets: " + t.getMessage());
                	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el ticket"), getStage());
				}
	    	}
	    }
    }

	protected boolean tieneArticuloGiftCard(TicketAparcadoBean ticketAparcado, TipoDocumentoBean tipoDocumentoActivo, TicketManager ticketManager){
		TicketVentaAbono ticketRecuperado = (TicketVentaAbono) MarshallUtil.leerXML(ticketAparcado.getTicket(), ticketManager.getTicketClasses(tipoDocumentoActivo).toArray(new Class[]{}));
		for(LineaTicket linea : ticketRecuperado.getLineas()){
			if (giftCardService.isGiftCardItem(linea.getCodArticulo())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void initializeForm() throws InitializeGuiException {
		try {
			if (!sesion.getSesionCaja().isCajaAbierta()) {
				Boolean aperturaAutomatica = variablesServices.getVariableAsBoolean(VariablesServices.CAJA_APERTURA_AUTOMATICA, true);
				if(aperturaAutomatica){
					VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No hay caja abierta. Se abrirá automáticamente."), getStage());
					sesion.getSesionCaja().abrirCajaAutomatica();
				}else{
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No hay caja abierta. Deberá ir a la gestión de caja para abrirla."), getStage());
					throw new InitializeGuiException(false);
				}
			}
			
			List<TiposIdentBean> tiposIdentificacion = tiposIdentService.consultarTiposIdent(null, true, sesion.getAplicacion().getTienda().getCliente().getCodpais());
			tiposIdent = FXCollections.observableArrayList();
			tiposIdent.add(new TiposIdentBean());
			for (TiposIdentBean tipoIdent : tiposIdentificacion) {
				tiposIdent.add(tipoIdent);
			}
			cbTipoDocIdent.setItems(tiposIdent);
			if(cbTipoDocIdent.getItems().size() > 0) {
				cbTipoDocIdent.getSelectionModel().select(1);
			}
			else {
				cbTipoDocIdent.getSelectionModel().select(0);
			}
			
			if(getDatos() != null && getDatos().get(MantenimientoClienteController.CLIENTE_EDITADO) != null) {
				ClienteBean cliente = (ClienteBean) getDatos().get(MantenimientoClienteController.CLIENTE_EDITADO);
				getDatos().remove(MantenimientoClienteController.CLIENTE_EDITADO);
				
				TiposIdentBean tipoIdentSeleccionado = null;
				for(TiposIdentBean tipoIdent : tiposIdent) {
					if(tipoIdent.getCodTipoIden() != null && tipoIdent.getCodTipoIden().equals(cliente.getTipoIdentificacion())) {
						tipoIdentSeleccionado = tipoIdent;
					}
				}
				
				cbTipoDocIdent.getSelectionModel().select(tipoIdentSeleccionado);
				tfNumDocIdent.setText(cliente.getCif());
				accionBuscar();
			} else {
				visor.escribirLineaArriba(I18N.getTexto("---NUEVO CLIENTE---"));
				limpiarFormulario();
			}
		}
		catch (TiposIdentNotFoundException ex) {
			log.error("initializeForm() - No se encontró ningún tipo de identificación.", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No se encontraron documentos de identificación configurados para la tienda."), this.getStage());
		}
		catch (TiposIdentServiceException ex) {
			log.error("initializeForm() - Error consultando los tipos de identificación.", ex);
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
		}
		catch(InitializeGuiException e){
			throw e;
		}
		catch (Exception ex) {
			log.error("initializeForm() - Se produjo un error en el tratamiento de los tipos de identificacion", ex);
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error consultando los documentos de identificación de la tienda."), this.getStage());
		}

		if (tfNumDocIdent.getText() != null && !tfNumDocIdent.getText().equalsIgnoreCase("")) {
			accionBuscar();
		}
	}

	@Override
	public void initializeFocus() {
		tfNumDocIdent.requestFocus();
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) {
	}

	@FXML
	public void accionBuscar() {
		log.trace("accionBuscar()");
		String ident = tfNumDocIdent.getText().trim();
		//si no hemos indicado el número de documento no hacemos nada
		if(!StringUtils.isBlank(ident)){
			String codTipoIdent = null;
			if(cbTipoDocIdent.getValue() != null) {
				codTipoIdent = cbTipoDocIdent.getValue().getCodTipoIden();
			}
			else {
				codTipoIdent = "";
			}
			new BuscarClientesTask(codTipoIdent, ident).start();
		}
	}

	@FXML
	public void accionBuscarTeclado(KeyEvent event) {
		log.trace("accionBuscarTeclado()");

		if (event.getCode() == KeyCode.ENTER) {
			accionBuscar();
		}
	}

	protected class BuscarClientesTask extends BackgroundTask<List<ClienteBean>> {

		private String codTipoIdent;
		private String ident;

		public BuscarClientesTask(String codTipoIdent, String ident) {
			this.codTipoIdent = codTipoIdent;
			this.ident = ident;
		}

		@Override
		protected List<ClienteBean> call() throws Exception {
			return clientesService.consultarClientes(codTipoIdent, ident);
		}

		@Override
		protected void failed() {
			super.failed();
			log.error(getCMZException().getLocalizedMessage(), getCMZException());
			VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessageI18N(), getCMZException());
			tfNumDocIdent.requestFocus();
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			List<ClienteBean> resultado = getValue();

			if(resultado == null || resultado.isEmpty()) {
				limpiarFormulario();
				VentanaDialogoComponent.crearVentanaAviso("", I18N.getTexto("El cliente no existe o está inactivo."), getStage());
			}
			else if(resultado.size() > 1) {
				VentanaDialogoComponent.crearVentanaAviso("", I18N.getTexto("Se ha encontrado más de un cliente."), getStage());
			}
			else {
				cliente = resultado.get(0);
				rellenarFormulario(cliente);
			}
			tfNumDocIdent.requestFocus();
		}
	}

	public void editar() {
		try {
			if (cliente != null) {
				ObservableList<ClienteGui> clienteList = FXCollections.observableList(new ArrayList<ClienteGui>());
				clienteList.add(new ClienteGui(cliente));
				HashMap<String, Object> datos = new HashMap<>();
				datos.put(MantenimientoClienteController.INDICE_CLIENTE_SELECCIONADO, 0);
				datos.put(MantenimientoClienteController.LISTA_CLIENTES, clienteList);
				datos.put(MantenimientoClienteController.MODO_EDICION, true);
				datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.MODIFICADO);
				getView().changeSubView(MantenimientoClienteView.class, datos);
			}
		}
		catch (InitializeGuiException e) {
			log.error("editar() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
	}

	public void nuevo() {
		try {
			HashMap<String, Object> datos = new HashMap<>();
			datos.put(MantenimientoClienteController.MODO_EDICION, true);
			datos.put(MantenimientoClienteController.ESTADO_CLIENTE, Estado.NUEVO);
			getView().changeSubView(MantenimientoClienteView.class, datos);
		}
		catch (InitializeGuiException e) {
			log.error("nuevo() - " + e.getMessage(), e);
			VentanaDialogoComponent.crearVentanaError(getStage(), e);
		}
	}

	public void vender() {
		if (cliente != null) {
			if(vProfesional || checkClientTaxes()) {
				getDatos().put(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE, cliente.clone());
				try {
					abrirPantallaVentas();
				}
				catch (InitializeGuiException e) {
					log.error("vender() - " + e.getMessage(), e);
					if(e.isMostrarError()) {
						VentanaDialogoComponent.crearVentanaError(getStage(), e);
					}
				}	
			}
		}
		else {
			accionBuscar();
			if (cliente != null) {
				VentanaDialogoComponent.crearVentanaAviso("", I18N.getTexto("Debe seleccionar el cliente que desee establecer para la venta."), getStage());
			}
		}
	}

	protected boolean checkClientTaxes() {
		TratamientoImpuestoBean trat = null;
		TratamientoImpuestoBean shopTrat = null;
		try {
			trat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos());
			shopTrat = tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos());
		}catch(Exception ignore) {}
		if(trat == null || !trat.getCodpais().equals(sesion.getAplicacion().getTienda().getCliente().getCodpais())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No es posible seleccionar este cliente al tener un tratamiento de impuestos no disponible para el país asociado a la tienda actual."), this.getStage());
			return false;
		}else if(sesion.getAplicacion().getTienda().getCliente().getIdTratImpuestos().equals(trat.getIdTratImpuestos()) || VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("El cliente seleccionado tiene un tratamiento de impuestos {0} diferente al de la tienda: {1}. Confirme si desea continuar.",trat.getDestratimp(),shopTrat.getDestratimp()), this.getStage())) {		
			return true;
		}
		return false;
	}

	public void clienteGenerico() {
		try {
			getDatos().put(ConsultaClienteController.PARAMETRO_SALIDA_CLIENTE, sesion.getAplicacion().getTienda().getCliente().clone());
			abrirPantallaVentas();
		}
		catch (InitializeGuiException e) {
			log.error("vender() - " + e.getMessage(), e);
			if(e.isMostrarError()) {
				VentanaDialogoComponent.crearVentanaError(getStage(), e);
			}
		}
	}
	
	protected void limpiarFormulario() {
		cliente = null;
		tfDescripcion.clear();
		tfNumDocIdent.clear();
		tfNombre.clear();
		tfDireccion.clear();
		tfPoblacion.clear();
		tfProvincia.clear();
		tfLocalidad.clear();
		tfCodigo.clear();
		tfTelefono1.clear();
		tfTelefono2.clear();
		tfFax.clear();
		tfEmail.clear();
		tfCodPais.clear();
		tfDesPais.clear();
		tfTratImpuesto.clear();
		tfTarifa.clear();
		taObservaciones.clear();
	}
	
	protected void rellenarFormulario(ClienteBean cliente) {
		tfDescripcion.setText(cliente.getDesCliente());
		tfNombre.setText(cliente.getNombreComercial());
		tfDireccion.setText(cliente.getDomicilio());
		tfPoblacion.setText(cliente.getPoblacion());
		tfProvincia.setText(cliente.getProvincia());
		tfLocalidad.setText(cliente.getLocalidad());
		tfCodigo.setText(cliente.getCp());
		tfTelefono1.setText(cliente.getTelefono1());
		tfTelefono2.setText(cliente.getTelefono2());
		tfFax.setText(cliente.getFax());
		tfEmail.setText(cliente.getEmail());
		tfCodPais.setText(cliente.getCodpais());
		if(cliente.getCodpais()!=null && !cliente.getCodpais().isEmpty()){
			PaisBean pais = null;
            try {
                pais = paisService.consultarCodPais(cliente.getCodpais());
            }
            catch (PaisNotFoundException | PaisServiceException e) {
            	log.error("Ha habido un error al recuperar el país con código: " + cliente.getCodpais());
            }
			if(pais!=null){
				tfDesPais.setText(pais.getDesPais());
			}
		}
		tfTratImpuesto.setText(tratamientoImpuestoService.consultarTratamientoImpuesto(sesion.getAplicacion().getUidActividad(), cliente.getIdTratImpuestos()).getDestratimp());
		tfTarifa.setText(cliente.getCodtar());
		taObservaciones.setText(cliente.getObservaciones());
	}
	
	protected void setVueltaEdicion(boolean vueltaEdicion) {
		this.vueltaEdicion = vueltaEdicion;
	}
	
	public void recuperarTicket() {
        try {
    		TicketManager ticketManager = getNuevoTicketManager();
    		ticketManager.init();
    		ticketManager.nuevoTicket();
    		if(ticketManager.countTicketsAparcados() > 0) {
	    		getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
	    		getDatos().put(RecuperarTicketController.PARAMETRO_TIPO_DOCUMENTO, ticketManager.getDocumentoActivo().getIdTipoDocumento());
	    		getApplication().getMainView().showModalCentered(RecuperarTicketView.class, getDatos(), this.getStage());
	    		
	    		if(getDatos().get(RecuperarTicketController.PARAMETRO_TICKET_RECUPERADO) != null) {
		        	getDatos().put(VentaIdentificadaController.PARAMETRO_TICKET_MANAGER, ticketManager);
			        abrirPantallaVentas();
	    		}
    		}
    		else {
    			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No hay tickets aparcados"), getStage());
    		}
        }
        catch (InitializeGuiException e) {
        	log.error("recuperarTicket() - Ha habido un error al abrir la pantalla de tickets: " + e.getMessage());
        	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el ticket"), getStage());
        }
        catch (PromocionesServiceException | DocumentoException e) {
        	log.error("recuperarTicket() - Ha habido un error al recuperar el ticket: " + e.getMessage());
        	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el ticket"), getStage());
        }
        catch (SesionInitException e) {
        	log.error("recuperarTicket() - Error al iniciar ticketManager: " + e.getMessage());
        	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Ha habido un error al recuperar el ticket"), getStage());
        }
	}
	
	protected TicketManager getNuevoTicketManager() {
		return SpringContext.getBean(TicketManager.class);
	}
	
	@Override
	public boolean canClose() {
		TicketManager ticketManager = getNuevoTicketManager();
		try {
	        ticketManager.init();
    		ticketManager.nuevoTicket();
			if(ticketManager.countTicketsAparcados() > 0) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Existen tickets aparcados pendientes de confirmar. Antes debería finalizar la operación."), getStage());
				return false;
			}
			else {
				return true;
			}
        }
        catch (Exception e) {
        	log.error("canClose() - Ha habido un error al inicializar el ticketManager: " + e.getMessage());
        	VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido cerrar la pantalla. Contacte con el administrador."), getStage());
        	return false;
        }
	}
	
	public void comprobarPermisosUI() {
		super.comprobarPermisosUI();
		botonera.comprobarPermisosOperaciones();
		// Actualizamos la pantalla de venta identificada en caso de que estuviese abierta
		for (View view : getApplication().getMainView().getCurrentSubView().getSubViews()) {
			view.getController().comprobarPermisosUI();
		}
	}
	
	protected void abrirPantallaVentas() throws InitializeGuiException {
		getView().changeSubView(VentaIdentificadaView.class, datos);
	}

}
