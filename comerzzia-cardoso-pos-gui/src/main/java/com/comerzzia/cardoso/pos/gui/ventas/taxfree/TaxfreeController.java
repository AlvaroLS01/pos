package com.comerzzia.cardoso.pos.gui.ventas.taxfree;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.clientes.ClientesRest;
import com.comerzzia.api.rest.client.clientes.ConsultarClienteRequestRest;
import com.comerzzia.api.rest.client.clientes.ResponseGetClienteRest;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.request.TaxfreeCountryRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.TaxfreeCountryResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.Document;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.Invoice;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.Item;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.OperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.Store;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.TaxfreeCreateRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.Tourist;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.request.TransDatum;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.response.TaxfreeCreateResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.mode.request.JSONOperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.mode.request.TaxfreeModeRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.mode.response.TaxfreeModeResponse;
import com.comerzzia.cardoso.pos.services.core.sesion.CardosoSesion;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;
import com.comerzzia.cardoso.pos.services.taxfree.webservice.TaxfreeWebService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.botonaccion.BotonBotoneraComponent;
import com.comerzzia.pos.core.gui.componentes.botonera.IContenedorBotonera;
import com.comerzzia.pos.core.gui.componentes.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.WindowController;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.cabecera.ISubtotalIvaTicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
@Component
public class TaxfreeController extends WindowController implements Initializable, IContenedorBotonera {
	
	private static final Logger log = Logger.getLogger(TaxfreeController.class.getName());
	
	public static final String TITULO_VENTANA = "Solicitud de Taxfree";
	public static final String BARCODE = "barCode";
	public static final String TAXFREE_CREATE_RESPONSE = "createResponse";
    public static final String PARAMETRO_TAXFREE_CANCELADO = "TAXFREE_CANCELADO";
    
	public static final String PARAM_IMPRESORAS = "impresoras";
	public static final String TAXFREE_IMPRESORA = "POS.TAXFREE_IMPRESORA";
	public static final String PARAM_ES_CANCELADO = "cancelado";
	
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

	@FXML
	protected Label lbTitulo;
	
	@FXML
	protected Button btCancelar, btAceptar;
	
	@FXML
	protected TextField tfNombreCompleto, tfDocumento, tfEmail, tfCP, tfTarjeta;
	
	@FXML
	protected DatePicker dpFechaNacimiento;
	
	@FXML
	protected ComboBox<String> cbPais, cbMedioPago, cbNacionalidad;
	
	private ITicket ticket;
	
	@Autowired
	private PaisService paisService;
	
	private List<PaisBean> paisesBBDD;

	@Autowired
	private VariablesServices variablesServices;

	@Autowired
	private CardosoSesion sesion;

	private ResponseGetFidelizadoRest fidelizado;
	
	protected FidelizacionBean dfcab;
	
	@Autowired
	protected TaxfreeWebService taxfreeWebService;
	
	@Autowired
	protected TaxfreeVariablesService taxfreeVariablesService;
	
	@Autowired 
	protected TaxfreeService taxfreeService;
		
	private String codPaisComparado, codPaisNacionalidad;
	
	@Autowired
	protected TicketsService ticketsService;
	
	protected Boolean esDiferido;

	private TaxfreeCountryResponse paisesDisponiblesTaxfree;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.debug("initialize() - Inicializando ventana...");
		lbTitulo.setText(TITULO_VENTANA);		
		cbPais.setItems(consultaPaisesBBDD());
		cbNacionalidad.setItems(consultaPaisesBBDD());
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {		
		log.debug("inicializarComponentes()");
		registrarAccionCerrarVentanaEscape();
	}

	@Override
	public void initializeFocus() {	
		tfNombreCompleto.requestFocus();
	}

	@Override
	public void initializeForm() throws InitializeGuiException {
		log.debug("initializeForm() - Inicializando pantalla...");
		ticket = (ITicket) getDatos().get("ticket");
		esDiferido = (Boolean)getDatos().get("esDiferido");
		paisesDisponiblesTaxfree = (TaxfreeCountryResponse)getDatos().get("paisesDisponiblesTaxfree");
		
		//Se comenta lo relacionado con el fidelizado, el cual se usaba para recoger los datos y autorrellenar los campos.
		
//		fidelizado = (ResponseGetFidelizadoRest)getDatos().get("fidelizadoRest");
//		if(fidelizado == null) {
//			fidelizado = recuperarFidelizadoParaTaxfree();
//		}
//
//		FidelizacionBean datosFidOrigen = null;
//		
		DatosFactura datosCliente = ticket.getCabecera().getCliente().getDatosFactura();
        
//        if(fidelizado == null) {
//        	datosFidOrigen = (FidelizacionBean) getDatos().get("datosFidOrigen"); 
//        	seleccionTarjeta();
//        }
        
        if(esDiferido) {
        	fidelizado = null;
        }

//		if(fidelizado != null) {
//			rellenarFormularioVentaDirecta(fidelizado);
//			seleccionTarjeta();
//		}else 
        if(datosCliente!=null) {
			rellenarFormularioVentaDirectaCliente(datosCliente);
			seleccionTarjeta();
//		}else if (datosFidOrigen != null){
//			rellenarFormularioTicketOrigen(datosFidOrigen);
//			seleccionTarjeta();
		}else {
			limpiarFormulario();
			seleccionTarjeta();
		}
		if(paisesDisponiblesTaxfree == null)
			getCountriesTask();
	}

	private void rellenarFormularioTicketOrigen(FidelizacionBean datosFidOrigen) {
		log.debug("rellenarFormularioTicketOrigen() - Rellenando formulario desde ticketOrigen");

		tfNombreCompleto.setText(datosFidOrigen.getNombre() + " " + datosFidOrigen.getApellido()); 
		tfDocumento.setText("");
		tfEmail.setText("");
		tfTarjeta.setText("");
		tfCP.setText(datosFidOrigen.getCp());
		dpFechaNacimiento.setSelectedDate(null);
		cbPais.setValue(datosFidOrigen.getDesPais());
		cbMedioPago.getSelectionModel().selectFirst();
		
	}

	private ResponseGetFidelizadoRest recuperarFidelizadoParaTaxfree() {
		log.debug("recuperarFidelizadoParaTaxfree() - Recuperando los datos del fidelizado desde la API");
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();

		FidelizacionBean datosFidelizado = ticket.getCabecera().getDatosFidelizado();
		
		String numTarjetaFidelizado = datosFidelizado != null ? datosFidelizado.getNumTarjetaFidelizado() : "";
		fidelizado = recuperarFidelizado(apiKey, uidActividad, numTarjetaFidelizado);
		return fidelizado;
	}

	private void rellenarFormularioVentaDirecta(ResponseGetFidelizadoRest fidelizado) {
		log.debug("rellenarFormularioVentaDirecta() - Rellenando formulario desde venta Directa");

		tfNombreCompleto.setText(fidelizado.getNombre() + " " + fidelizado.getApellidos()); 
		tfDocumento.setText(""); 
		tfEmail.setText(fidelizado.getEmail());
		tfTarjeta.setText("");
		tfCP.setText(fidelizado.getCp());
		dpFechaNacimiento.setSelectedDate(fidelizado.getFechaNacimiento());
		
		if(StringUtils.isBlank(fidelizado.getDesPais())) {
			cbPais.getSelectionModel().selectFirst();
		}else {
			cbPais.setValue(fidelizado.getDesPais());
		}
		cbMedioPago.getSelectionModel().selectFirst();
	}
	
	private void rellenarFormularioVentaDirectaCliente(DatosFactura datosCliente) {
		log.debug("rellenarFormularioVentaDirectaCliente() - Rellenando formulario desde venta Directa para cliente");

		tfNombreCompleto.setText(datosCliente.getNombre()); 
		tfDocumento.setText("");
		tfEmail.setText("");
		tfTarjeta.setText("");
		tfCP.setText(datosCliente.getCp());
		dpFechaNacimiento.setSelectedDate(null);
		cbPais.setValue(datosCliente.getPais());
		for (PaisBean pais : paisesBBDD) {
			if(pais.getCodPais().equals(datosCliente.getPais())) {
				cbPais.setValue(pais.getDesPais());
			}
		}
		cbMedioPago.getSelectionModel().selectFirst();
	}

	@Override
	public void realizarAccion(BotonBotoneraComponent botonAccionado) throws CajasServiceException {
		switch (botonAccionado.getClave()) {
			case "ACCION_ACEPTAR":
			try {
				accionAceptar();
			} catch (Exception e) {
				log.debug("accionAceptar() - error en la solicitud de taxfree",e);
			}
				break;
			case "ACCION_CANCELAR":
				accionCancelar();
				break;
			default:
				break;
		}		
	}

	@FXML
	private void accionAceptar() throws Exception {	
		log.debug("accionAceptar() - generando taxfree");
		
		if(!esDiferido) {
			ticketsService.setContadorIdTicket((Ticket)ticket);			
		}
		dfcab = ticket.getCabecera().getDatosFidelizado();
		
		log.debug("accionAceptar() taxfreeController - comprobando datos");
		if(comprobarDatos()) {
			// Crear objeto Taxfree para la llamada de create
			log.debug("accionAceptar() taxfreeController  - creando objeto request de taxfree");
			TaxfreeCreateRequest taxfreeCreate = ticketToTaxfreeCreateObject(ticket);
			
			// llamar al ws
			log.debug("accionAceptar() taxfreeController - llamada al ws para creacion de taxfree");
			llamadaCreateTaxfree(taxfreeCreate);
		}
	}
	
	private TaxfreeCreateRequest ticketToTaxfreeCreateObject(ITicket ticket) throws Exception {
		log.debug("ticketToTaxfreeCreateObject() - generando objeto taxfree");

		TaxfreeCreateRequest createRequest = new TaxfreeCreateRequest();
		log.debug("ticketToTaxfreeCreateObject() taxfreeController - Creando Objeto llamada ");
		createRequest.setOperationId(TaxfreeVariablesService.OPERATION_ID_CREATION);
		createRequest.setOperationData(createOperationData(ticket));
		
		return createRequest;
	}

	private OperationData createOperationData(ITicket ticket) throws Exception {
		OperationData operationData = new OperationData();
		
		log.debug("createOperationData() taxfreeController - Setando objeto documento ");		
		operationData.setDocument(getTaxfreeMode());
		log.debug("createOperationData() taxfreeController - Setando objeto tienda");
		operationData.setStore(createStoreObject());
		log.debug("createOperationData() taxfreeController - Setando objeto turista");
		operationData.setTourist(createTouristData());
		log.debug("createOperationData() taxfreeController - Setando objeto factura");
		operationData.setInvoice(createInvoiceObject(ticket));
		
		return operationData;
	}

	private Document getTaxfreeMode() throws Exception {
		Document document = new Document();
		//Comprobar si queremos alguno en concreto ya que trae lista (El indice 3 es el id standar)
//		document.setTaxFreeMode(Integer.parseInt(getTaxFreeModeResponse().getDetails().getGetTaxFreeModeResponse().get(3).getValue().getId()));
		document.setTaxFreeMode(0);

		return document;
	}
	
	private TaxfreeModeRequest getTaxFreeModeRequest() throws Exception {
		
		TaxfreeModeRequest modeRequest = new TaxfreeModeRequest();
		JSONOperationData jsonOpData = new JSONOperationData();
		jsonOpData.setDataRequest("GetTaxFreeMode");
		jsonOpData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOpData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		modeRequest.setJSONOperationData(jsonOpData);
		
		return modeRequest;
	}

	private TaxfreeModeResponse getTaxFreeModeResponse() throws Exception {
		TaxfreeModeRequest taxfreeModeRequest = getTaxFreeModeRequest();

		String contenidoRespuesta = taxfreeWebService.llamadaTaxfree(new Gson().toJson(taxfreeModeRequest));

		TaxfreeModeResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeModeResponse.class);
		
		return response;
	}


	private void llamadaCreateTaxfree(TaxfreeCreateRequest taxfreeCreate) throws Exception {

		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("llamadaCreateTaxfree() call() - recibiendo respuesta de Taxfree");
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(taxfreeCreate));
			}
			@Override
			protected void succeeded() {
				super.succeeded();
				String contenidoRespuesta = getValue();
				
				log.debug("llamadaCreateTaxfree() taxfreeController - parseando respuesta a objeto");
				TaxfreeCreateResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeCreateResponse.class);
				
				if (StringUtils.isNotBlank(contenidoRespuesta)) {
					if (response != null && response.getHasError().equals("false")&& response.getErrorCode().equals("ESP735")) {
						// recoger idTaxfree y setearlo en el xml
						String barCode = response.getDetails().getCreateResponseV0().getBarCode();
						((CARDOSOCabeceraTicket) ticket.getCabecera()).setIdTaxfree(barCode);
						getDatos().put(BARCODE, barCode);
						getDatos().put("pasaporte", tfDocumento.getText());
						getDatos().put("codTicketDiferido", ticket.getCabecera().getCodTicket());
						getDatos().put(PARAMETRO_TAXFREE_CANCELADO, false);
						// insertar la respuesta en los datos para pasarla a la pantalla de gestion de taxfree
						getDatos().put(TAXFREE_CREATE_RESPONSE, response);
					} 
					else if(response != null && response.getHasError().equals("true")&& response.getErrorCode().equals("WET152")){
						String barCode =  "noBarcode";
						String[] mensaje = response.getMessage().split(" ");
						String invoice = mensaje[1];
						getDatos().put(BARCODE, barCode);
						getDatos().put("invoice", invoice);
						getDatos().put(PARAMETRO_TAXFREE_CANCELADO, false);
						
					}else if(response != null && response.getHasError().equals("true")){
						getDatos().put(PARAMETRO_TAXFREE_CANCELADO, true);
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha ocurrido un error al realizar el documento TAXFREE, revise los datos e inténtelo de nuevo. \n" + response.getMessage() ), getStage());
						return;
					}
					else {
						getDatos().put(PARAMETRO_TAXFREE_CANCELADO, true);
						VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha ocurrido un error al realizar el documento TAXFREE, revise los datos e inténtelo de nuevo."), getStage());
						return;
					}
				}
				getStage().close();
			}

			@Override
			protected void failed() {
				super.failed();
				Throwable e = getException();
				log.error("llamadaCreateTaxfree:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("Ha ocurrido un error solicitando el taxfree. Puede intentarlo de nuevo más tarde en la pantalla de Gestión de Taxfree"), getStage());
			}
		};
		task.start();
	}
	

	private Store createStoreObject() throws Exception {
		Store store = new Store();
		log.debug("createStoreObject() taxfreeController - creando objeto tienda");
		store.setCountryId(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		store.setCustAccount(taxfreeVariablesService.getCustAccount());
		
		return store;	
	}
	

	private Tourist createTouristData() {
		log.debug("createTouristData() taxfreeController - creando objeto turista");
		
		Tourist tourist = new Tourist();
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
		
		if(fidelizado != null || dfcab != null) {
			log.debug("createTouristData() taxfreeController - seteando datos del turista desde el ticket");

			tourist.setPassport(tfDocumento.getText());
			tourist.setName(tfNombreCompleto.getText());

			String[] fecha = dpFechaNacimiento.getValue().split("/");
			tourist.setBirthDate(fecha[2]+"-"+fecha[1]+"-"+fecha[0]);
			
			//Si se cambia el pais en el formulario con respecto al de la BBDD prioriza el del formulario
			if(StringUtils.isNotBlank(fidelizado.getDesPais()) || StringUtils.isNotBlank(dfcab.getDesPais())) {
				if(fidelizado.getDesPais().equals(cbPais.getValue()) || dfcab.getDesPais().equals(cbPais.getValue()))
					tourist.setCountry(fidelizado != null ? fidelizado.getCodPais() : dfcab.getCodPais());
			}else{				
				tourist.setCountry(codPaisComparado);
			}
				
			tourist.setEmail(tfEmail.getText());
			//paymentMode
			tourist.setAddress(fidelizado != null ? fidelizado.getDomicilio() : dfcab.getDomicilio());
			if(fidelizado != null)
				tourist.setGender(fidelizado.getSexo().equals("H") ? 1 : 2);
			else
				tourist.setGender(0);
			
			//card
			tourist.setCard(tfTarjeta.getText());
			
			//cardToken
			tourist.setZipCode(tfCP.getText());
			tourist.setPhone(fidelizado != null ? fidelizado.getTelefono1() : "");
			tourist.setCity(fidelizado != null ? fidelizado.getPoblacion() : dfcab.getPoblacion());
			//TouristID
			
			//Nationality
			tourist.setNationality(codPaisNacionalidad);
			
		}else {
			log.debug("createTouristData() taxfreeController - seteando datos del turista desde el modal (Venta anónima)");
			
			tourist.setName(tfNombreCompleto.getText());
			tourist.setPassport(tfDocumento.getText());
			tourist.setEmail(tfEmail.getText());
			tourist.setCard(tfTarjeta.getText());
			String[] fecha = dpFechaNacimiento.getValue().split("/");
			tourist.setBirthDate(fecha[2]+"-"+fecha[1]+"-"+fecha[0]);
			tourist.setCountry(codPaisComparado);
			tourist.setZipCode(tfCP.getText());
			tourist.setNationality(codPaisNacionalidad);
		}
		log.debug("createTouristData() taxfreeController - seteando datos medio de devolucion del turista");
		switch (cbMedioPago.getValue()) {
			case "Efectivo":
				tourist.setRefundMode(1);
				break;
				
			case "Tarjeta":
				tourist.setRefundMode(3);
				break;	
				
			case "Alipay":
				tourist.setRefundMode(5);
				break;
				
			default:
				break;
		}
		
		return tourist;
	}


	private Invoice createInvoiceObject(ITicket ticket) {
		Invoice invoice = new Invoice();
		log.debug("createInvoiceObject() taxfreeController - seteando datos objeto Factura");
		invoice.setTransData(lineasTicketCMZToTaxfreeListItems(ticket.getCabecera().getCliente(), ticket.getLineas()));
		invoice.setInvoiceId(ticket.getCabecera().getCodTipoDocumento() + "/" + ticket.getCabecera().getCodTicket());
		
		return invoice;
	}
	
	private List<TransDatum> lineasTicketCMZToTaxfreeListItems(ClienteBean clienteCab, List<CARDOSOLineaTicket> lineas) {
		
		List<TransDatum> listaTranData = new ArrayList<>();
		TransDatum transDatumTf;
		List<Item> itemsTF;
		
		log.debug("lineasTicketCMZToTaxfreeListItems() taxfreeController - seteando datos de los items recogidos del ticket");

		for (CARDOSOLineaTicket linea : lineas) {
			if (!linea.isBorrada()) {
				itemsTF = new ArrayList<>();
				transDatumTf = new TransDatum();
				itemsTF.add(itemCMZToItemTf(clienteCab, linea));
				transDatumTf.setItems(itemsTF);
				transDatumTf.setTaxValue(ValorPorcentaje(clienteCab, linea));
				
				listaTranData.add(transDatumTf);
			}
		}
		return listaTranData;		
	}

	private int ValorPorcentaje(ClienteBean clienteCab, LineaTicket linea) {
		return sesion.getImpuestos().getPorcentaje(clienteCab.getIdTratImpuestos(), linea.getCodImpuesto()).getPorcentaje().intValue();
	}
	
	private Item itemCMZToItemTf(ClienteBean clienteCab, LineaTicket linea) {
		Item itemTf = new Item();
		
		log.debug("itemCMZToItemTf() taxfreeController - parseando linea " + linea.getIdLinea() + " del ticket de comerzzia a item de taxfree");

		itemTf.setBaseTotal(0);
		itemTf.setFamily(linea.getArticulo().getCodFamilia());
		itemTf.setName(linea.getDesArticulo());
		itemTf.setQty(linea.getCantidad().intValue());
		itemTf.setSalesTotal(linea.getCantidad().intValue() * linea.getPrecioTotalConDto().doubleValue());
		itemTf.setReference(linea.getCodArticulo());
		itemTf.setSerial(null);
		
		return itemTf;
	}
	
	private boolean comprobarDatos() throws Exception {
		
		log.debug("comprobarDatos() - comprobando datos y creando el objeto para crear el taxfree");		

		String nombreCompleto = tfNombreCompleto.getText();
		if (StringUtils.isBlank(nombreCompleto)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo nombre completo no puede estar vacio."), getStage());
			return false;
		}
		String documento = tfDocumento.getText();
		if (StringUtils.isBlank(documento)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo documento no puede estar vacio."), getStage());
			return false;
		}
		String email = tfEmail.getText();
		if (StringUtils.isBlank(email)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo email no puede estar vacio."), getStage());
			return false;
		}else if (!comprobarEmail(email)){
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El email no es válido."), getStage());
			return false;
		}
		if (dpFechaNacimiento.getSelectedDate() != null) {
			if (dpFechaNacimiento.getSelectedDate().getTime() > System.currentTimeMillis()) {
				log.debug("comprobarDatos() - Fecha futura");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo fecha de nacimiento no puede tener una fecha futura."), getStage());
				return false;
			}else if ((System.currentTimeMillis() - dpFechaNacimiento.getSelectedDate().getTime()) < 567993600000L) { // 567993600000 18 años y 4 dias (años bisiestos)
				log.debug("comprobarDatos() - Persona menor de edad");
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La persona sobre la que se realiza la operación debe de ser mayor de edad."), getStage());				
				return false;
			}
		}else {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo fecha de nacimiento no puede estar vacio."), getStage());
			return false;
		}
		String pais = cbPais.getValue();
		if (StringUtils.isBlank(pais)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo país no puede estar vacio."), getStage());
			return false;
		}else {
			 boolean puedeTaxfree = false;
			for (PaisBean paisCompare : paisesBBDD) {
				if(paisCompare.getDesPais().equals(pais)) {
					puedeTaxfree = taxfreeService.compruebaPaisResidenciaFueraUE(paisCompare.getCodPais(), paisesDisponiblesTaxfree);
					codPaisComparado = paisCompare.getCodPais();
					break;
				}
			}

			if(!puedeTaxfree) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("País no permitido para realizar Taxfree."), getStage());
				return false;
			}
		}
		String CP = tfCP.getText();
		if (StringUtils.isBlank(CP)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo código postal no puede estar vacio."), getStage());
			return false;
		}
		else if (permiteCPAlfanumerico(pais)) {
			if (tfCP.getText().length() > 7) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El código postal no puede tener más de 7 dígitos para el país seleccionado."), getStage());
				return false;
			}
		}
		else {
			if (!CP.matches("\\d+")) {
				VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo código postal no puede tener letras."), getStage());
				return false;
			}
		}
		
		String pago = cbMedioPago.getValue();
		if (StringUtils.isBlank(pago)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo pago no puede estar vacio."), getStage());
			return false;
		}
		
		String tarjeta = tfTarjeta.getText();
		if (cbMedioPago.getValue().equalsIgnoreCase("tarjeta")&& StringUtils.isBlank(tarjeta)) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El campo tarjeta no puede estar vacío."), getStage());
			return false;
		}else if (cbMedioPago.getValue().equalsIgnoreCase("tarjeta") && !tarjeta.matches("\\d+")) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("La tarjeta solo debe tener números."), getStage());
			return false;
		}
		
		if (!StringUtils.isNotBlank(cbNacionalidad.getValue())) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("El país del pasaporte no puede estar vacío."), getStage());
			return false;
		}
		else {
			for (PaisBean paisCompare : paisesBBDD) {
				if(paisCompare.getDesPais().equals(cbNacionalidad.getValue())) {
					codPaisNacionalidad= paisCompare.getCodPais();
					break;
				}
			}
		}
		return true;
	}
	
	private boolean permiteCPAlfanumerico(String paisSeleccionado) {
		// Lista de codigos de paises con CP alfanumericos
		List<String> codPaisesConCPAlfanumericos = Arrays.asList("GI", "GB");
		for (PaisBean pais : paisesBBDD) {
			if (pais.getDesPais().equals(paisSeleccionado) && codPaisesConCPAlfanumericos.contains(pais.getCodPais())) {
				return true;
			}
		}
		// El pais no admite codigos postales alfanumericos
		return false;
	}
	
	protected void getCountriesTask() {
		
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("getCountriesTask() - Haciendo llamada");
				TaxfreeCountryRequest countryRequest = taxfreeService.crearCountryRequest();
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(countryRequest));
			}
			
			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("getCountriesTask() taxfreeController - obteniendo respuesta");
				String contenidoRespuesta = getValue();
				
				log.debug("getCountriesTask() taxfreeController - parseando respuesta a objeto");
				TaxfreeCountryResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeCountryResponse.class);
				
				paisesDisponiblesTaxfree = response;
			}
			
			@Override
			protected void failed() {
				super.failed();
				Throwable e = getException();
				log.error("getCountriesTask:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
			}
			
		};
		task.start();
	}

	@FXML
	public void accionCancelar() {
		log.debug("accionCancelar() - cancelando la generacion de taxfree");
		boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se procederá a cancelar la solicitud de Taxfree, ¿Está seguro? (Podrá generarlo más adelante en la pantalla Gestión de Taxfree)"), getStage());
		if (!confirmacion) {
			return;
		}
		getDatos().put(PARAMETRO_TAXFREE_CANCELADO, true);
		super.accionCancelar();
	}
	
	private void limpiarFormulario() {
		log.debug("limpiarFormulario() - No trae datos seteamos la pantalla en blanco");
		tfNombreCompleto.setText(""); 
		tfDocumento.setText(""); 
		tfEmail.setText("");
		tfCP.setText("");
		dpFechaNacimiento.setSelectedDate(null);
		cbPais.getSelectionModel().selectFirst();
		cbMedioPago.getSelectionModel().selectFirst();
		cbNacionalidad.getSelectionModel().selectFirst();
	}

	private ObservableList consultaPaisesBBDD() {			
		log.debug("consultaPaisesBBDD() - consultando paises de la BBDD de cmz");
		ObservableList paisToComboBox = FXCollections.observableArrayList();
		
		try {
			log.debug("consultaPaisesBBDD() - Obteniendo paises de la BBDD");
			paisesBBDD = paisService.consultarPaises();
			paisToComboBox.add(" ");
			for (PaisBean pais : paisesBBDD) {
				paisToComboBox.add(pais.getDesPais());
			}
		} catch (PaisNotFoundException e) {
			log.error("No se han podido recuperar los paises de la BBDD",e);
		}
		
		Collections.sort(paisToComboBox);
		return paisToComboBox;
	}
	
	private ResponseGetFidelizadoRest recuperarFidelizado(String apiKey, String uidActividad, String numTarjetaFidelizado) {
		log.debug("recuperarFidelizado() - Obteniendo fidelizado de central");
		ConsultarFidelizadoRequestRest consultaRest = new ConsultarFidelizadoRequestRest(apiKey, uidActividad, numTarjetaFidelizado);
		ResponseGetFidelizadoRest fidelizado = null;
		try {
			fidelizado = FidelizadosRest.getFidelizado(consultaRest);
			consultaRest.setIdFidelizado(fidelizado.getIdFidelizado().toString());
			List<TiposContactoFidelizadoBean> contactos = FidelizadosRest.getContactos(consultaRest);
			
			for (TiposContactoFidelizadoBean tiposContacto : contactos) {
				if(tiposContacto.getCodTipoCon().equals("EMAIL")) {
					fidelizado.setEmail(tiposContacto.getValor());
				}else if(tiposContacto.getCodTipoCon().equals("MOVIL") || tiposContacto.getCodTipoCon().equals("TELEFONO1")){
					fidelizado.setTelefono1(tiposContacto.getValor());
				}else if(tiposContacto.getCodTipoCon().equals("TELEFONO2")) {
					fidelizado.setTelefono2(tiposContacto.getValor());
				}
			}
			
		} catch (RestException | RestHttpException e1) {
			log.error("No se ha podido recuperar el fidelizado "+ e1.getMessage(), e1);
		}
		return fidelizado;
	}
	
	private void seleccionTarjeta() {
		 BooleanProperty editableProperty = new SimpleBooleanProperty();
	        tfTarjeta.editableProperty().bind(editableProperty);

	        cbMedioPago.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	            if (newValue.equals("Tarjeta")) {
	                editableProperty.set(true);
	                tfTarjeta.setDisable(false);
	            } else {
	                editableProperty.set(false);
	                tfTarjeta.setText("");
	                tfTarjeta.setDisable(true);
	            }
	        });
	}
	
	private boolean comprobarEmail(String email) {
	        Pattern pattern = Pattern.compile(EMAIL_REGEX);
	        return pattern.matcher(email).matches();
	}

	@FXML
	private void comprobarResidencia(){
		if (!StringUtils.isNotBlank(cbPais.getValue())) {
			if (VentanaDialogoComponent.crearVentanaConfirmacion("¿Desea establecer el mismo país como residencia?", getStage())) {
				cbPais.setValue(cbNacionalidad.getValue());
			}
		}
	}
	
	@FXML
	public void accionAceptarIntro(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			try {
				accionAceptar();
			}
			catch (Exception ex) {
				log.error("accionAceptarIntro() - Ha courrido un error:", ex);
			}
		}
	}
}
