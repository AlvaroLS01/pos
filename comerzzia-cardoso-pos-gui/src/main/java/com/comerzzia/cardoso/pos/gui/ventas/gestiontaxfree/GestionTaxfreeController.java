package com.comerzzia.cardoso.pos.gui.ventas.gestiontaxfree;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.validation.ConstraintViolation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.comerzzia.api.model.loyalty.TiposContactoFidelizadoBean;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.fidelizados.ConsultarFidelizadoRequestRest;
import com.comerzzia.api.rest.client.fidelizados.FidelizadosRest;
import com.comerzzia.api.rest.client.fidelizados.ResponseGetFidelizadoRest;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.TaxfreeController;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.TaxfreeView;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.impresora.SeleccionImpresoraController;
import com.comerzzia.cardoso.pos.gui.ventas.taxfree.impresora.SeleccionImpresoraView;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.pagos.CARDOSOPagosController;
import com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request.TaxfreeBarCodeCreationRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.response.TaxfreeBarCodeCreationResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.request.JSONOperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.request.TaxfreeCancellationRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.response.TaxfreeCancellationResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.request.TaxfreeCountryRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.Country;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.TaxfreeCountryResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.create.response.TaxfreeCreateResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.invoicecreation.request.TaxfreeInvoiceCreationRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.invoicecreation.response.TaxfreeInvoiceCreationResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.MovimientosTaxfree;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;
import com.comerzzia.cardoso.pos.services.taxfree.webservice.TaxfreeWebService;
import com.comerzzia.cardoso.pos.services.ticket.CardosoTicketVentaAbono;
import com.comerzzia.cardoso.pos.services.ticket.CardosoTicketsService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.cardoso.pos.services.ticket.lineas.CARDOSOLineaTicket;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.core.dispositivos.configuracion.ConfiguracionDispositivo;
import com.comerzzia.pos.core.dispositivos.dispositivo.visor.IVisor;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.core.gui.controllers.Controller;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.gui.ventas.devoluciones.FormularioConsultaTicketBean;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoController;
import com.comerzzia.pos.gui.ventas.devoluciones.tipoDocumento.TipoDocumentoView;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.persistence.tickets.datosfactura.DatosFactura;
import com.comerzzia.pos.services.cajas.CajaEstadoException;
import com.comerzzia.pos.services.cajas.CajasServiceException;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@SuppressWarnings({ "unused", "rawtypes" })
@Component
@Primary
public class GestionTaxfreeController extends Controller {

	private static final Logger log = Logger.getLogger(GestionTaxfreeController.class.getName());

	public static final String ESTADO_ACTIVO = "";
	public static final String ESTADO_CANDIDATO = "";
	public static final String ESTADO_ANULADO = "";

	@FXML
	protected Button btReimprimir, btGenerar, btAnular;

	@FXML
	protected Label lbEstado;

	@Autowired
	protected TaxfreeService taxfreeService;

	final IVisor visor = Dispositivos.getInstance().getVisor();

	@Autowired
	private Sesion sesion;
	@Autowired
	private VariablesServices variablesServices;

	@FXML
	protected TextField tfOperacion, tfTienda, tfCodCaja, tfCodDoc, tfDesDoc;

	@FXML
	protected Label lbMensajeError;

	@FXML
	protected Button btAceptar, btDoc;

	protected FormularioConsultaTicketBean frConsultaTicket;

	protected TicketManager ticketManager;

	@Autowired
	protected Documentos documentos;

	@Autowired
	protected CARDOSOPagosController cardosoPagosController;

	private CARDOSOCabeceraTicket cabecera;

	private String barCode;

	@Autowired
	protected TaxfreeWebService taxfreeWebService;

	@Autowired
	protected TaxfreeVariablesService taxfreeVariablesService;
	
	@Autowired
	protected CardosoTicketsService ticketService;

	private TaxfreeCountryResponse paisesDisponiblesTaxfree;

	protected boolean impreso;

	@Override
	public void initializeForm() throws InitializeGuiException {
		ticketManager = SpringContext.getBean(TicketManager.class);

		// Realizamos las comprobaciones de apertura automática de caja y de cierre de caja obligatorio
		try {
			comprobarAperturaPantalla();
		}
		catch (CajasServiceException | CajaEstadoException e) {
			log.error("initializeForm() - Error inicializando pantalla:" + e.getMessageI18N(), e);
			throw new InitializeGuiException(e.getMessageI18N(), e);
		}

		visor.escribirLineaArriba(I18N.getTexto("--NUEVA DEVOLUCION--"));

		tfTienda.setText(sesion.getAplicacion().getTienda().getCodAlmacen());
		tfCodCaja.setText(sesion.getAplicacion().getCodCaja());
		tfOperacion.setText("");

		List<String> tiposDocumentoAbonables = documentos.getTiposDocumentoAbonables();
		if (tiposDocumentoAbonables.isEmpty()) {
			VentanaDialogoComponent.crearVentanaAviso(I18N.getTexto("No está configurado el tipo de documento nota de crédito en el entorno."), getStage());
			btAceptar.setDisable(true);
		}
		else {
			btAceptar.setDisable(false);
		}

		for (String tipoDoc : tiposDocumentoAbonables) {
			try {
				if (documentos.getDocumento(tipoDoc) != null) {
					TipoDocumentoBean docPreseleccion = documentos.getDocumento(tipoDoc);
					tfCodDoc.setText(docPreseleccion.getCodtipodocumento());
					tfDesDoc.setText(docPreseleccion.getDestipodocumento());
					break;
				}
			}
			catch (DocumentoException ex) {
				log.error("No se ha encontrado el documento asociado", ex);
			}
		}

		paisesDisponiblesTaxfree = (TaxfreeCountryResponse) getDatos().get("paisesDisponiblesTaxfree");
		if (paisesDisponiblesTaxfree == null)
			getCountriesTask();

		limpiarCamposTaxFree();
	}

	@FXML
	public void accionBuscarTF() throws RestHttpException, RestException {
		log.info("accionBuscarTF() - Buscando ticket y documento TAXFREE asociado...");

		resetearPantalla();

		accionAceptar();
	}

	private void resetearPantalla() {
		btGenerar.setDisable(true);
		btReimprimir.setDisable(true);
		btAnular.setDisable(true);
		lbEstado.setText("");
		cabecera = null;
		barCode = "";
	}

	@FXML
	public void accionGenerarTF() {
		boolean valido = comprobarDisponibilidadTaxfree();
		if (valido) {
			log.debug("accionGenerarTF() - GestionTaxfreeController Solicitando taxfree en diferido");
			solicitarTaxfreeDiferido();
			// solo hace esto si no se ha cancelado el taxfree
			Boolean tfCancelado = (Boolean) getDatos().get(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO);
			if (getDatos().containsKey(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO) && !tfCancelado) {
				log.debug("accionGenerarTF() - GestionTaxfreeController obteniendo datos de la respuesta createResponse");
				TaxfreeCreateResponse createResponse = (TaxfreeCreateResponse) getDatos().get(TaxfreeController.TAXFREE_CREATE_RESPONSE);
				String barcode = createResponse != null ? createResponse.getDetails().getCreateResponseV0().getBarCode() : "";

				log.debug("accionGenerarTF() - GestionTaxfreeController tratamos datos de la respuesta createResponse");
				if (createResponse != null && createResponse.getHasError().equals("false") && StringUtils.isNotBlank(barcode)) {
					lbEstado.setStyle("-fx-text-fill: green;");
					lbEstado.setText("ACTIVO");
					btReimprimir.setDisable(false);
					btAnular.setDisable(false);
					btGenerar.setDisable(true);

				}
				else if (createResponse == null || createResponse.getHasError().equals("true") && createResponse.getErrorCode().equals("WET152")) {
					lbEstado.setStyle("-fx-text-fill: green;");
					lbEstado.setText("YA EXISTE EN EL SISTEMA, NO PUEDE HACER LA SOLICITUD POR DUPLICADO");
					btReimprimir.setDisable(false);
					btAnular.setDisable(false);
					btGenerar.setDisable(true);
				}
				else {
					lbEstado.setStyle("-fx-text-fill: red;");
					lbEstado.setText("ERROR EN LA PETICIÓN DE TAXFREE");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(true);
				}
			}
		}
		else {
			lbMensajeError.setText("Ticket no susceptible de aplicar taxfree");
			btReimprimir.setDisable(true);
			btAnular.setDisable(true);
			btGenerar.setDisable(true);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean comprobarDisponibilidadTaxfree() {
		boolean valido = false;
		Boolean paisOK = null;
		Boolean superaImporteMinimo = null;
		try {
			CARDOSOCabeceraTicket cabeceraCardosoTicketRecuperado = (CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera();
			
			//Se comenta la llamada a los fiedelizados que se hacía para autorellenar los campos del TaxFree
			
//			ResponseGetFidelizadoRest fidelizadoRecuperado = recuperarFidelizadoParaTaxfree(cabeceraCardosoTicketRecuperado);
			DatosFactura datosFactura = cabeceraCardosoTicketRecuperado.getCliente().getDatosFactura();
//			if (fidelizadoRecuperado != null) {
//				if (paisesDisponiblesTaxfree != null && paisesDisponiblesTaxfree.getHasError().equals("false") && !paisesDisponiblesTaxfree.getDetails().getCountries().isEmpty()) {
//					for (Country countryResponse : paisesDisponiblesTaxfree.getDetails().getCountries()) {
//						if (countryResponse.getCountryId().equalsIgnoreCase(fidelizadoRecuperado.getCodPais())) {
//							// si la lista tiene el pais del cliente, este vive fuera de la UE por tanto es válido
//							// devolviendo true
//							paisOK = true;
//							break;
//						}
//					}
//					// devuelve false si el pais de residencia esta en la UE (No válido)
//					if (paisOK == null)
//						paisOK = false;
//				}
//				else {
//					// Si no hay respuesta se prohibe el taxfree
//					paisOK = false;
//				}
//			}
//			else 
			if (datosFactura != null) {
				if (paisesDisponiblesTaxfree != null && paisesDisponiblesTaxfree.getHasError().equals("false") && !paisesDisponiblesTaxfree.getDetails().getCountries().isEmpty()) {
					for (Country countryResponse : paisesDisponiblesTaxfree.getDetails().getCountries()) {
						if (countryResponse.getCountryId().equalsIgnoreCase(datosFactura.getPais())) {
							// si la lista tiene el pais del cliente, este vive fuera de la UE por tanto es válido
							// devolviendo true
							paisOK = true;
							break;
						}
					}
					// devuelve false si el pais de residencia esta en la UE (No válido)
					if (paisOK == null)
						paisOK = false;
				}
				else {
					// Si no hay respuesta se prohibe el taxfree
					paisOK = false;
				}
			}
			else {
				// permitimos el pais ya que es venta anónima
				paisOK = true;
			}

			try {
				// Añadir distintos paises y moficar la variable del metodo comparaImporteMinimo segun el pais al que
				// pertenezca
				switch (sesion.getAplicacion().getTienda().getCliente().getCodpais()) {
					case "PT":
						superaImporteMinimo = taxfreeService.comparaImporteMinimo(cabeceraCardosoTicketRecuperado.getTotales().getTotal());
						break;
					default:
						superaImporteMinimo = true;
						break;
				}
			}
			catch (Exception e) {
				log.error("comprobarDisponibilidadTaxFreeTask() - Error obteniendo la variable de importe minimo de la bbdd " + e);
			}

			return (superaImporteMinimo && paisOK) ? true : false;
		}
		catch (Exception e) {
			VentanaDialogoComponent.crearVentanaInfo(I18N.getTexto("No se ha encontrado ningún documento taxfree."), getStage());
			return false;
		}
	}

	@FXML
	public void accionReimprimirTF() throws Exception {
		if (StringUtils.isBlank(barCode) || barCode.equals("noBarcode")) {
			CARDOSOCabeceraTicket cabecera2 = (CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera();
			String codTicket = cabecera2.getCodTipoDocumento() + "/" + cabecera2.getCodTicket();
			getDatos().put("invoice", codTicket);
			imprimirPDFTaxfreeFromInvoice();
		}
		else {
			getDatos().put("barCode", barCode);
			imprimirPDFTaxfree();
		}
	}

	@FXML
	public void accionAnularTF() throws Exception {
		log.debug("accionGenerarTF() - GestionTaxfreeController cancelando taxfree en diferido");
		boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(I18N.getTexto("Se procederá a ANULAR la solicitud de Taxfree, esta operación no puede revertirse ¿Está seguro?"),
		        getStage());
		if (!confirmacion) {
			return;
		}

		accionAnularTfTask();
	}

	private void accionAnularTfTask() {
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("llamadaCancellationTaxfree() - GestionTaxfreeController formamos el objeto de request para la cancelacion");
				TaxfreeCancellationRequest cancellationRequest = createCancellationRequest();

				log.debug("llamadaCancellationTaxfree() - GestionTaxfreeController llamamos al ws para la cancelacion");
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(cancellationRequest));
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("getCountriesTask() taxfreeController - obteniendo respuesta");
				String contenidoRespuesta = getValue();

				log.debug("getCountriesTask() taxfreeController - parseando respuesta a objeto");
				TaxfreeCancellationResponse cancelResponse = new Gson().fromJson(contenidoRespuesta, TaxfreeCancellationResponse.class);

				if (cancelResponse != null && cancelResponse.getHasError().equals("false") && cancelResponse.getErrorCode().equals("OK001")) {
					lbEstado.setStyle("-fx-text-fill: orange;");
					lbEstado.setText("ANULADA");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(false);

					String[] mensaje = cancelResponse.getMessage().split(" ");
					String codigoBarras = mensaje[2];
					
					TicketBean ticketBeanLocal = new TicketBean();
					
					String codCajaTicket = ticketManager.getTicketOrigen().getCodCaja();
					String etiqueta = "";
					
					DatosSesionBean datosSesion = new DatosSesionBean();
					try {
						datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
						datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
						datosSesion.setCodEmpresa(sesion.getAplicacion().getEmpresa().getCodEmpresa());
					}
					catch (EmpresaException e) {
						log.error("Ha habido un problema inicializando DatosSesion.", e);
					}
					
					String uidTicket = ticketManager.getTicketOrigen().getUidTicket();

					if (codCajaTicket.equals(sesion.getAplicacion().getCodCaja())) {
						// Si es la misma caja, updateamos en local y en central, si la api falla creamos documento taxfree.
						try {
							ticketBeanLocal = ticketService.consultarTicket(uidTicket, sesion.getAplicacion().getUidActividad()); // Se consulta el ticket en local para coger el xml
							if (ticketBeanLocal != null) {
								try {
									updatearLocal(etiqueta, ticketBeanLocal);
								}
								catch (ParserConfigurationException | SAXException | java.io.IOException e) {
									log.error("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error: ", e);
								}
							}
						}
						catch (TicketsServiceException e) {
							log.error("Ha ocurrido un error consultado el ticket", e);
						} 
						
						// Se updatea en central falle o no falle cuando se updatea en local.
						try {
							etiqueta = "anulacion";
							updatearTicket((CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera(), etiqueta, sesion.getAplicacion().getUidActividad());
						}
						catch (Exception e) {
							log.debug("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error al intentar hacer el update en central: ", e);
							log.info("recuperarTicketDevolucionSucceeded() - No se ha podido hacer el update en central, se procede a crear un documento taxFree");
							etiqueta = "";
							ticketService.inserccionTaxfreeXML(etiqueta, ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento(), ticketManager.getTicketOrigen().getCabecera().getCodTicket(),
							        codCajaTicket, ticketManager.getTicketOrigen().getUidTicket());
						}
					}
					else {
						// En el caso que no sea la misma caja, updateamos en central
						try {
							etiqueta = "anulacion";
							updatearTicket((CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera(), etiqueta, sesion.getAplicacion().getUidActividad());
						}
						catch (Exception e) {
							// Si falla la API, crearemos documento Taxfree
							log.debug("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error al intentar hacer el update en central: ", e);
							log.info("recuperarTicketDevolucionSucceeded() - No se ha podido hacer el update en central, se procede a crear un documento taxFree");
							etiqueta = "";
							ticketService.inserccionTaxfreeXML(etiqueta, ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento(), ticketManager.getTicketOrigen().getCabecera().getCodTicket(),
							       codCajaTicket, ticketManager.getTicketOrigen().getUidTicket());
						}
					}
					
//					log.debug("accionAnularTF() - GestionTaxfreeController insertando respuesta de anulacion taxfree en diferido");
//					try {
//						String codTicket = cabecera.getCodTipoDocumento() + "/" + cabecera.getCodTicket();
//						taxfreeService.insertDocument(codTicket, codigoBarras, "ANULACION", "");
//					}
//					catch (Exception e) {
//						log.error("accionAnularTfTask() - Error insertando datos de Taxfree en la bbdd" + e);
//					}

				}
				else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("WET380")) {
					lbEstado.setStyle("-fx-text-fill: orange;");
					lbEstado.setText("EL DOCUMENTO YA HA SIDO SELLADO, NO SE PUEDE CANCELAR");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(true);

				}
				else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("SYS117566")) {
					lbEstado.setStyle("-fx-text-fill: orange;");
					lbEstado.setText("REEMBOLSADO PERO NO FACTURADO A TIENDA");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(true);

				}
				else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("SYS117567")) {
					lbEstado.setStyle("-fx-text-fill: orange;");
					lbEstado.setText("REEMBOLSADO Y FACTURADO A LA TIENDA");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(true);

				}
				else {
					lbEstado.setStyle("-fx-text-fill: red;");
					lbEstado.setText("ERROR EN LA PETICIÓN DE TAXFREE");
					btReimprimir.setDisable(true);
					btAnular.setDisable(true);
					btGenerar.setDisable(true);
				}
			}

			@Override
			protected void failed() {
				super.failed();
				Throwable e = getException();
				log.error("accionAnularTfTask:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
				VentanaDialogoComponent.crearVentanaError(lbEstado.getText(), getStage());
			}
		};
		task.start();
	}

	private TaxfreeCancellationRequest createCancellationRequest() throws Exception {
		TaxfreeCancellationRequest cancelRequest = new TaxfreeCancellationRequest();
		JSONOperationData jsonOperationData = new JSONOperationData();
		boolean tieneBarcode = StringUtils.isNotBlank(barCode);
		String invoice = cabecera.getCodTipoDocumento() + "/" + cabecera.getCodTicket();

		// formamos el objeto de cancelación
		log.debug("createCancellationRequest() - GestionTaxfreeController seteamos los datos al objeto de request para la cancelación");
		jsonOperationData.setDataRequest("CancelForm");
		jsonOperationData.setBarCode("");
		jsonOperationData.setInvoice(invoice);
		jsonOperationData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		jsonOperationData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOperationData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());

		cancelRequest.setOperationId("GetData");
		cancelRequest.setJSONOperationData(jsonOperationData);

		return cancelRequest;
	}

	private TaxfreeCancellationResponse llamadaCancellationTaxfree() throws Exception {

		log.debug("llamadaCancellationTaxfree() - GestionTaxfreeController formamos el objeto de request para la cancelación");
		TaxfreeCancellationRequest cancellationRequest = createCancellationRequest();

		log.debug("llamadaCancellationTaxfree() - GestionTaxfreeController llamamos al ws para la cancelacion");
		String contenidoRespuesta = taxfreeWebService.llamadaTaxfree(new Gson().toJson(cancellationRequest));

		log.debug("llamadaCancellationTaxfree() - GestionTaxfreeController parseamos a objeto la respuesta");
		TaxfreeCancellationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeCancellationResponse.class);

		return response;
	}

	public void limpiarCamposTaxFree() {
		lbMensajeError.setText("");
		lbEstado.setText("");
		btReimprimir.setDisable(true);
		btGenerar.setDisable(true);
		btAnular.setDisable(true);
	}

	@FXML
	public void keyReleased(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && !btAceptar.isDisable()) {
			accionAceptar();
		}
	}

	@FXML
	public void accionBuscarTipoDoc() {
		datos = new HashMap<String, Object>();
		datos.put(TipoDocumentoController.PARAMETRO_ENTRADA_POSIBLES_DOCS, documentos.getTiposDocumentoAbonables());

		getApplication().getMainView().showModalCentered(TipoDocumentoView.class, datos, this.getStage());

		if (datos.containsKey(TipoDocumentoController.PARAMETRO_SALIDA_DOC)) {
			TipoDocumentoBean tipoDoc = (TipoDocumentoBean) datos.get(TipoDocumentoController.PARAMETRO_SALIDA_DOC);
			tfCodDoc.setText(tipoDoc.getCodtipodocumento());
			tfDesDoc.setText(tipoDoc.getDestipodocumento());
		}
	}

	protected void comprobarAperturaPantalla() throws CajasServiceException, CajaEstadoException, InitializeGuiException {
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
			String fechaCaja = FormatUtil.getInstance().formateaFecha(sesion.getSesionCaja().getCajaAbierta().getFechaApertura());
			String fechaActual = FormatUtil.getInstance().formateaFecha(new Date());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se puede realizar la venta. El día de apertura de la caja {0} no coincide con el del sistema {1}", fechaCaja, fechaActual),
			        getStage());
			throw new InitializeGuiException(false);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		frConsultaTicket = SpringContext.getBean(FormularioConsultaTicketBean.class);

		frConsultaTicket.setFormField("codTienda", tfTienda);
		frConsultaTicket.setFormField("codOperacion", tfOperacion);
		frConsultaTicket.setFormField("codCaja", tfCodCaja);
		frConsultaTicket.setFormField("tipoDoc", tfCodDoc);

	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@Override
	public void initializeFocus() {
		tfOperacion.requestFocus();
	}

	@FXML
	public void accionAceptar() {
		lbMensajeError.setText("");
		if (validarFormularioConsultaCliente()) {

			ticketManager = SpringContext.getBean(TicketManager.class);
			String codTienda = frConsultaTicket.getCodTienda();
			String codCaja = frConsultaTicket.getCodCaja();
			String codigo = frConsultaTicket.getCodOperacion();
			String codDoc = frConsultaTicket.getCodDoc();

			try {
				if (ticketManager.comprobarConfigContador(documentos.getDocumentoAbono(codDoc).getCodtipodocumento())) {
					Long idTipoDocumento = documentos.getDocumento(codDoc).getIdTipoDocumento();
					new RecuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDocumento).start();
				}
				else {
					ticketManager.crearVentanaErrorContador(getStage());
				}
			}
			catch (DocumentoException e) {
				VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("El documento %s no se ha encontrado"), codDoc), e);
			}
		}
	}

	protected boolean validarFormularioConsultaCliente() {
		boolean valido;

		// Limpiamos los errores que pudiese tener el formulario
		frConsultaTicket.clearErrorStyle();
		// Limpiamos el posible error anterior
		lbMensajeError.setText("");

		frConsultaTicket.setCodCaja(tfCodCaja.getText());
		frConsultaTicket.setCodOperacion(tfOperacion.getText());
		frConsultaTicket.setCodTienda(tfTienda.getText());
		frConsultaTicket.setCodDoc(tfCodDoc.getText());

		// Validamos el formulario de login
		Set<ConstraintViolation<FormularioConsultaTicketBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frConsultaTicket);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<FormularioConsultaTicketBean> next = constraintViolations.iterator().next();
			frConsultaTicket.setErrorStyle(next.getPropertyPath(), true);
			frConsultaTicket.setFocus(next.getPropertyPath());
			lbMensajeError.setText(next.getMessage());
			valido = false;
		}
		else {
			valido = true;
		}

		return valido;
	}

	public class RecuperarTicketDevolucion extends BackgroundTask<Boolean> {

		private String codigo;
		private String codTienda, codCaja;
		private Long idTipoDoc;

		public RecuperarTicketDevolucion(String codigo, String codTienda, String codCaja, Long idTipoDoc) {
			this.codigo = codigo;
			this.codTienda = codTienda;
			this.codCaja = codCaja;
			this.idTipoDoc = idTipoDoc;
		}

		@Override
		protected Boolean call() throws Exception {
			return ticketManager.recuperarTicketDevolucion(codigo, codTienda, codCaja, idTipoDoc);
		}

		@Override
		protected void failed() {
			super.failed();
			if (getException() instanceof com.comerzzia.pos.util.exception.Exception) {
				VentanaDialogoComponent.crearVentanaError(getStage(), getCMZException().getMessage(), getCMZException());
			}
			else {
				VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error."), getException());
			}
		}

		@Override
		protected void succeeded() {
			boolean res = getValue();
			try {
				recuperarTicketDevolucionSucceeded(res);
			}
			catch (RestHttpException | RestException e) {
				log.error("recuperarTicketDevolucionSucceeded(res) - " + e);
			}
			super.succeeded();
		}

	}

	protected void recuperarTicketDevolucionSucceeded(boolean encontrado) throws RestHttpException, RestException {

		boolean valido = comprobarDisponibilidadTaxfree();
		if (encontrado && valido) {
			boolean esMismoTratamientoFiscal = ticketManager.comprobarTratamientoFiscalDev();
			if (!esMismoTratamientoFiscal) {
				try {
					ticketManager.eliminarTicketCompleto();
				}
				catch (Exception e) {
					log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error al eliminar los tickets: " + e.getMessage(), e);
				}

				lbMensajeError.setText(I18N.getTexto("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución."));
				return;
			}
			else {
				// comprobamos si trae el tag de taxfree_barcode
				cabecera = (CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera();
				barCode = cabecera.getIdTaxfree();

				if (cabecera != null) {
					if (StringUtils.isNotBlank(barCode)) {
						if (StringUtils.isNotBlank(barCode)) {
							// recoge el ultimo movimiento, si es null no lo ha encontrado por que no se ha procesado
							// pero al tener barcode significa que tiene un taxfree asociado
							Boolean esAnulacion = !StringUtils.isNotBlank(barCode) ? true : false;
							if (esAnulacion) {
								lbEstado.setStyle("-fx-text-fill: orange;");
								lbEstado.setText("ANULADO");
								btReimprimir.setDisable(true);
								btAnular.setDisable(true);
								btGenerar.setDisable(false);
							}
							else {
								lbEstado.setStyle("-fx-text-fill: green;");
								lbEstado.setText("ACTIVO");
								btReimprimir.setDisable(false);
								btAnular.setDisable(false);
								btGenerar.setDisable(true);
							}
						}
					}
					else {// No tiene barcode
						// comprobamos que no tiene movimientos
						if (StringUtils.isNotBlank(barCode)) {
							boolean esAnulacion = StringUtils.isNotBlank(barCode);
							if (esAnulacion) {
								lbEstado.setStyle("-fx-text-fill: orange;");
								lbEstado.setText("ANULADO");
								btReimprimir.setDisable(true);
								btAnular.setDisable(true);
								btGenerar.setDisable(true);
							}
							else {
								lbEstado.setStyle("-fx-text-fill: green;");
								lbEstado.setText("ACTIVO");
								btReimprimir.setDisable(false);
								btAnular.setDisable(false);
								btGenerar.setDisable(true);
							}
						}
						else {
							lbEstado.setStyle("-fx-text-fill: #909090;");
							lbEstado.setText("SOLICITUD TAXFREE DISPONIBLE");
							btReimprimir.setDisable(true);
							btAnular.setDisable(true);
							btGenerar.setDisable(false);
						}
					}
				}
				else {
					// no se ha podido recuperar el ticket
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido recuperar el ticket. No pueden realizarse operaciones de Taxfree sobre este ticket."), getStage());
				}

				boolean recoveredOnline = ticketManager.getTicket().getCabecera().getDatosDocOrigen().isRecoveredOnline();
				boolean ticketVacio = false;
				
				comprobarCantidadYPrecio();
				
				if (ticketManager.getTicketOrigen().getLineas().size() == 0) {
					ticketVacio = true;
				}
				
				boolean hayLineasDevueltas = hayLineasDevueltas();
				if (!recoveredOnline) {
					lbMensajeError.setText("");
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("No se ha podido comprobar si la venta contiene alguna devolución, por lo que no puede realizarse ninguna operación"),
					        getStage());
					btAnular.setDisable(true);
					btGenerar.setDisable(true);
					lbEstado.setText("NO SE PUDO COMPROBAR SI TIENE DEVOLUCIÓN");
					if (StringUtils.isNotBlank(barCode)) {
						btReimprimir.setDisable(false);
					}
				} else if (hayLineasDevueltas) {
					lbMensajeError.setText("");
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El ticket contiene lineas devueltas, no es posible realizar taxfree."),
					        getStage());
					btAnular.setDisable(true);
					btGenerar.setDisable(true);
					lbEstado.setText("TICKET CON DEVOLUCIÓN");
					if (StringUtils.isNotBlank(barCode)) {
						btReimprimir.setDisable(false);
					}
				}
				
				if (ticketVacio) {
					lbMensajeError.setText("");
					lbEstado.setStyle("-fx-text-fill: red;");
					VentanaDialogoComponent.crearVentanaError(I18N.getTexto("El ticket no contiene lineas suceptibles de taxfree."),
					        getStage());
					btAnular.setDisable(true);
					btGenerar.setDisable(true);
					btReimprimir.setDisable(true);
					lbEstado.setText("LINEAS NO SUCEPTIBLES DE TAXFREE");
				}
			}
		}
		else if (!valido) {
			lbMensajeError.setText(I18N.getTexto(lbMensajeError.getText().isEmpty() ? "Ticket no susceptible de aplicar Taxfree" : lbMensajeError.getText()));
		}
		else {
			lbMensajeError.setText(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"));
		}
	}

	@SuppressWarnings("unchecked")
	private boolean hayLineasDevueltas() {
		List<CARDOSOLineaTicket> lineas = ticketManager.getTicketOrigen().getLineas();
		for (CARDOSOLineaTicket linea : lineas) {
			int resultadoComparacion = linea.getCantidadDevuelta().compareTo(new BigDecimal(0));
			if (resultadoComparacion > 0 ) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void comprobarCantidadYPrecio() {
		log.debug("comprobarCantidadYPrecio() - Se comprobará la cantidad y el precio total, para ver si cumple las condiciones para ser enviadas las lineas.");
		boolean puedeRealizarTaxfree = true;
		List<CARDOSOLineaTicket> listaLineasTicket = ticketManager.getTicketOrigen().getLineas();
		Map<String, List<CARDOSOLineaTicket>> mapaAgrupado = new HashMap<>();
		
		for (CARDOSOLineaTicket linea : listaLineasTicket) {
			String codArticulo = linea.getCodArticulo();
			
			if (mapaAgrupado.containsKey(codArticulo)) {
                mapaAgrupado.get(codArticulo).add(linea);
            } else {
                List<CARDOSOLineaTicket> nuevaLista = new ArrayList<>();
                nuevaLista.add(linea);
                mapaAgrupado.put(codArticulo, nuevaLista);
            }
		}
		
		 
		for (Map.Entry<String, List<CARDOSOLineaTicket>> entry : mapaAgrupado.entrySet()) {
            String codArticulo = entry.getKey();
            List<CARDOSOLineaTicket> lineasAgrupadas = entry.getValue();
            BigDecimal sumatorioImporte = new BigDecimal(0);
      
            for (CARDOSOLineaTicket linea : lineasAgrupadas) {
            	sumatorioImporte = sumatorioImporte.add(linea.getImporteTotalConDto());
            }
            
            log.debug("IMPORTE " + sumatorioImporte);
            if (sumatorioImporte.compareTo(new BigDecimal(0)) == 0) { // Quiere decir que si el sumatorio de la cantidad es menor/igual entrará.
            	puedeRealizarTaxfree = false; 	
            }
            
            log.debug("El tamaño de la lista de las lineas es:" + listaLineasTicket.size());
            if (!puedeRealizarTaxfree) {
            	int tamaño = listaLineasTicket.size();
            	int contador = 0;
            	for (int i = 0; i < tamaño; i++) {
            		
            		for (CARDOSOLineaTicket linea : listaLineasTicket) {
            			
            			if (linea.getCodArticulo().equals(codArticulo)) {
            				linea.setBorrada(true);
            			}
            			
            		}
            	}
            	puedeRealizarTaxfree = true;
            }
            
            log.debug("El tamaño de la lista de las lineas es:" + listaLineasTicket.size());
		}
	}
	
	public void solicitarTaxfreeDiferido() {
		try {
			Boolean taxfreeRealizado = null;
			String url = taxfreeVariablesService.getUrlServicio();

			taxfreeRealizado = generarTaxfreeDiferido(url, (ITicket) ticketManager.getTicketOrigen());

			Exception exceptionImpresion = null;

			if (taxfreeRealizado) {
				impreso = false;

				String etiqueta = getDatos().get(TaxfreeController.BARCODE).toString();

				TicketBean ticketBeanLocal = new TicketBean(); // Ticket para rellenar con el ticket rescatado desde local
				String uidTicket = ticketManager.getTicketOrigen().getUidTicket();

				DatosSesionBean datosSesion = new DatosSesionBean();
				datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
				datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
				datosSesion.setCodEmpresa(sesion.getAplicacion().getEmpresa().getCodEmpresa());

				String codCajaTicket = ticketManager.getTicketOrigen().getCodCaja();
				if (codCajaTicket.equals(sesion.getAplicacion().getCodCaja())) {
					// Si es la misma caja, updateamos en local y en central, si la api falla creamos documento taxfree.
					ticketBeanLocal = ticketService.consultarTicket(uidTicket, sesion.getAplicacion().getUidActividad()); // Se consulta el ticket en local para coger el xml
					                                                                                                     
					if (ticketBeanLocal != null) {
						try {
							updatearLocal(etiqueta, ticketBeanLocal);
						}
						catch (ParserConfigurationException | SAXException | java.io.IOException e) {
							log.error("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error: ", e);
						}
						// Se updatea en central falle o no falle cuando se updatea en local.
					}
					try {
						updatearTicket((CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera(), etiqueta, sesion.getAplicacion().getUidActividad());
					}
					catch (Exception e) {
						log.debug("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error al intentar hacer el update en central: ", e);
						log.info("recuperarTicketDevolucionSucceeded() - No se ha podido hacer el update en central, se procede a crear un documento taxFree");
						ticketService.inserccionTaxfreeXML(etiqueta, ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento(), ticketManager.getTicketOrigen().getCabecera().getCodTicket(),
						      codCajaTicket, ticketManager.getTicketOrigen().getUidTicket());
					}
				}
				else {
					// En el caso que no sea la misma caja, updateamos en central
					try {
						updatearTicket((CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera(), etiqueta, sesion.getAplicacion().getUidActividad());
					}
					catch (Exception e) {
						// Si falla la API, crearemos documento Taxfree
						log.debug("recuperarTicketDevolucionSucceeded() - Ha ocurrido un error al intentar hacer el update en central: ", e);
						log.info("recuperarTicketDevolucionSucceeded() - No se ha podido hacer el update en central, se procede a crear un documento taxFree");
						ticketService.inserccionTaxfreeXML(etiqueta, ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento(), ticketManager.getTicketOrigen().getCabecera().getCodTicket(),
						       codCajaTicket, ticketManager.getTicketOrigen().getUidTicket());
					}
				}

				imprimirPDFTaxfree();
				VentanaDialogoComponent.crearVentanaInfo("TAXFREE generado correctamente", getStage());
			}

		}
		catch (Exception e) {
			log.error("ventanaTaxfreeDiferido() - " + e.getMessage());
			VentanaDialogoComponent.crearVentanaError(I18N.getTexto("Error generando TAXFREE. Si lo desea puede volver a intentarlo desde la pantalla Gestion TAXFREE en el apartado de Ventas"),
			        getStage());
		}
	}

	private void updatearLocal(String etiqueta, TicketBean ticketBeanLocal) throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		byte[] nuevosByteData;
		
		// Convertir el array de bytes a una cadena
		String xmlString = new String(ticketBeanLocal.getTicket(), "UTF-8");

		// Crear un documento XML a partir de la cadena
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8"))));

		modificarValorTag(document, "taxfree_barcode", etiqueta);
		
		//convertir el documento de nuevo a bytes
		nuevosByteData = convertirXmlABytes(document);
		ticketBeanLocal.setTicket(nuevosByteData);
		ticketService.modificarTicket(ticketBeanLocal);
	}

	private Boolean generarTaxfreeDiferido(String url, ITicket ticket) throws Exception {
		if (getDatos() == null) {
			this.datos = new HashMap<String, Object>();
		}

		getDatos().put("url", url);
		getDatos().put("ticket", ticket);
		getDatos().put("esDiferido", true);
		getApplication().getMainView().showModalCentered(TaxfreeView.class, getDatos(), getStage());

		if (getDatos().containsKey(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO)) {
			Boolean taxfreeCancelado = (Boolean) getDatos().get(TaxfreeController.PARAMETRO_TAXFREE_CANCELADO);
			if (taxfreeCancelado) {
				return Boolean.FALSE;
			}
			else {
				return Boolean.TRUE;
			}
		}
		else {
			return false;
		}
	}
	
	
	private void imprimirPDFTaxfree() throws Exception {
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("imprimirPDFTaxfree()");
				TaxfreeBarCodeCreationRequest getPDFRequest = getPdfVoucherRequest();

				log.debug("imprimirPDFTaxfree() - llamando al ws de impresion y obteniendo respuesta");
				return taxfreeWebService.llamadaTaxfree(new Gson().toJson(getPDFRequest));
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("imprimirPDFTaxfree() GestionTaxfreeController - obteniendo respuesta");
				String contenidoRespuesta = getValue();

				log.debug("imprimirPDFTaxfree() - parseando respuesta");
				TaxfreeBarCodeCreationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeBarCodeCreationResponse.class);
				if (response.getHasError().equals("false")) {
					log.debug("imprimirPDFTaxfree() - parseando respuesta");
					byte[] decodedPDF = Base64.getDecoder().decode(response.getMessage());
					// PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
					PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);

					if (ps.length == 0) {
						throw new IllegalStateException("No Printer found");
					}
					log.debug("Available printers: " + Arrays.asList(ps));

					PrintService printService = null;

					log.debug("imprimirPDFTaxfree() - Comprobando variable de impresion en bbdd");
					String taxFreeImpresora = (String) variablesServices.getVariableAsString(CARDOSOPagosController.TAXFREE_IMPRESORA);
					ConfiguracionDispositivo config = null;

					String impresora = "IMPRESORA1";
					if (taxFreeImpresora != null) {
						log.debug("imprimirPDFTaxfree() - seteando impresora");
						if (taxFreeImpresora.equals("S")) {
							impresora = "IMPRESORA1";
//							impresora = "Microsoft Print to PDF";
						}
						else {
							impresora = "IMPRESORA2";
						}
						for (PrintService impresoraSeleccionada : ps) {
							if (impresoraSeleccionada.getName().equals(impresora)) {
								printService = impresoraSeleccionada;
							}
						}

					}

					else {
						for (PrintService impresoraSeleccionada : ps) {
							if (impresoraSeleccionada.getName().equals(impresora)) {
								printService = impresoraSeleccionada;
							}
						}
					}

					log.debug("imprimirPDFTaxfree() - parseo para el PDF");
					try {
						PDDocument document = PDDocument.load(decodedPDF);

						PrinterJob printJob = PrinterJob.getPrinterJob();

						printJob.setPrintService(printService);
						printJob.setPageable(new PDFPageable(document));
						printJob.print();
						document.close();
					}
					catch (PrinterException | IOException e) {
						log.error("Error a la hora del parseo a PDF " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError("Se ha cancelado la impresión " + e.getMessage(), getStage());
					}
					impreso = true;
				}
			}

			@Override
			protected void failed() {
				super.failed();
				if (!impreso) {
					Throwable exceptionImpresion = null;
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), exceptionImpresion);
				}
				Throwable e = getException();
				log.error("getCountriesTask:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
			}

		};
		task.start();
	}

	private PrintService seleccionaImpresora(PrintService[] ps) {
		getDatos().put(CARDOSOPagosController.PARAM_IMPRESORAS, ps);
		getApplication().getMainView().showModalCentered(SeleccionImpresoraView.class, getDatos(), getStage());
		PrintService printService = (PrintService) getDatos().get(SeleccionImpresoraController.PARAM_PRINTSERVICE);

		return printService;
	}

	private TaxfreeBarCodeCreationRequest getPdfVoucherRequest() throws Exception {

		TaxfreeBarCodeCreationRequest pdfRequest = new TaxfreeBarCodeCreationRequest();
		com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request.JSONOperationData jsonOpData = new com.comerzzia.cardoso.pos.persistence.taxfree.barcodecreation.request.JSONOperationData();

		pdfRequest.setOperationId("GetData");
		jsonOpData.setDataRequest("GetPDFVoucher");
		jsonOpData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOpData.setBarcode((String) getDatos().get("barcodeFromMovimiento"));

		if (StringUtils.isBlank(jsonOpData.getBarcode()))
			jsonOpData.setBarcode((String) getDatos().get("barCode"));

		jsonOpData.setParameters(CARDOSOPagosController.PDF_80);
		pdfRequest.setJSONOperationData(jsonOpData);

		return pdfRequest;
	}

	private MovimientosTaxfree consultarMovimientoTaxfreeByExample(CARDOSOCabeceraTicket cab) throws RestHttpException, RestException {

		MovimientosTaxfree mtfRecuperado = new MovimientosTaxfree();
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String codTicket = cab.getCodTipoDocumento() + "/" + cab.getCodTicket();

		// llamar a la ServicioBO
		mtfRecuperado = taxfreeService.getMovimientosTaxfreeByExample(apiKey, cab.getUidActividad(), codTicket, "");

		return mtfRecuperado;
	}

	protected void getCountriesTask() {

		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
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

				getDatos().put("paisesDisponiblesTaxfree", paisesDisponiblesTaxfree);
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

	private ResponseGetFidelizadoRest recuperarFidelizadoParaTaxfree(CARDOSOCabeceraTicket cabeceraCardosoTicketRecuperado) {
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidActividad = sesion.getAplicacion().getUidActividad();

		FidelizacionBean datosFidelizado = cabeceraCardosoTicketRecuperado.getDatosFidelizado();

		String numTarjetaFidelizado = datosFidelizado != null ? datosFidelizado.getNumTarjetaFidelizado() : "";
		ResponseGetFidelizadoRest fidelizado = recuperarFidelizado(apiKey, uidActividad, numTarjetaFidelizado);

		getDatos().put("fidelizadoRest", fidelizado);

		return fidelizado;
	}

	private ResponseGetFidelizadoRest recuperarFidelizado(String apiKey, String uidActividad, String numTarjetaFidelizado) {
		ConsultarFidelizadoRequestRest consultaRest = new ConsultarFidelizadoRequestRest(apiKey, uidActividad, numTarjetaFidelizado);
		ResponseGetFidelizadoRest fidelizado = null;
		try {
			fidelizado = FidelizadosRest.getFidelizado(consultaRest);
			consultaRest.setIdFidelizado(fidelizado.getIdFidelizado().toString());
			List<TiposContactoFidelizadoBean> contactos = FidelizadosRest.getContactos(consultaRest);

			for (TiposContactoFidelizadoBean tiposContacto : contactos) {
				if (tiposContacto.getCodTipoCon().equals("EMAIL")) {
					fidelizado.setEmail(tiposContacto.getValor());
				}
				else if (tiposContacto.getCodTipoCon().equals("MOVIL") || tiposContacto.getCodTipoCon().equals("TELEFONO1")) {
					fidelizado.setTelefono1(tiposContacto.getValor());
				}
				else if (tiposContacto.getCodTipoCon().equals("TELEFONO2")) {
					fidelizado.setTelefono2(tiposContacto.getValor());
				}
			}

		}
		catch (RestException | RestHttpException e1) {
			log.error("No se ha podido recuperar el fidelizado " + e1.getMessage(), e1);
		}
		return fidelizado;
	}

	private void imprimirPDFTaxfreeFromInvoice() {
		BackgroundTask<String> task = new BackgroundTask<String>(){

			@Override
			protected String call() throws Exception {
				log.debug("getPdfVoucherResponse()");
				TaxfreeInvoiceCreationRequest getPDFRequestFromInvoice = getPdfVoucherRequestFromInvoice();

				log.debug("getPdfVoucherResponse() - llamando al ws de impresion y obteniendo respuesta");
				String res =  taxfreeWebService.llamadaTaxfree(new Gson().toJson(getPDFRequestFromInvoice));
				return res;
			}

			@Override
			protected void succeeded() {
				super.succeeded();
				log.debug("getCountriesTask() taxfreeController - obteniendo respuesta");
				String contenidoRespuesta = getValue();

				log.debug("getPdfVoucherResponse() - parseando respuesta");
				TaxfreeInvoiceCreationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeInvoiceCreationResponse.class);
				if (response.getHasError().equals("false")) {
					log.debug("imprimirPDFTaxfree() - parseando respuesta");
					byte[] decodedPDF = Base64.getDecoder().decode(response.getMessage());
					// PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
					PrintService[] ps = PrintServiceLookup.lookupPrintServices(null, null);

					if (ps.length == 0) {
						throw new IllegalStateException("No Printer found");
					}
					log.debug("Available printers: " + Arrays.asList(ps));

					PrintService printService = null;

					log.debug("imprimirPDFTaxfree() - Comprobando variable de impresion en bbdd");
					String taxFreeImpresora = (String) variablesServices.getVariableAsString(CARDOSOPagosController.TAXFREE_IMPRESORA);
					ConfiguracionDispositivo config = null;

					String impresora = "IMPRESORA1";
					if (taxFreeImpresora != null) {
						log.debug("imprimirPDFTaxfree() - seteando impresora");
						if (taxFreeImpresora.equals("S")) {
							impresora = "IMPRESORA1";
//							impresora = "Microsoft Print to PDF (vdi)";
						}
						else {
							impresora = "IMPRESORA2";
						}
						for (PrintService impresoraSeleccionada : ps) {
							if (impresoraSeleccionada.getName().toUpperCase().equals(impresora.toUpperCase())) {
								printService = impresoraSeleccionada;
							}
						}

					}

					else {
						for (PrintService impresoraSeleccionada : ps) {
							if (impresoraSeleccionada.getName().equals(impresora)) {
								printService = impresoraSeleccionada;
							}
						}
					}

					log.debug("imprimirPDFTaxfree() - parseo para el PDF");
					try {
						PDDocument document = PDDocument.load(decodedPDF);

						PrinterJob printJob = PrinterJob.getPrinterJob();

						printJob.setPrintService(printService);
						printJob.setPageable(new PDFPageable(document));
						printJob.print();
						document.close();
					}
					catch (PrinterException | IOException e) {
						log.error("Error a la hora del parseo a PDF " + e.getMessage(), e);
						VentanaDialogoComponent.crearVentanaError("Se ha cancelado la impresión" + e.getMessage(), getStage());
					}
					impreso = true;
				}
			}

			@Override
			protected void failed() {
				super.failed();
				if (!impreso) {
					Throwable exceptionImpresion = null;
					VentanaDialogoComponent.crearVentanaError(getStage(), I18N.getTexto("Lo sentimos, ha ocurrido un error al imprimir."), exceptionImpresion);
				}
				Throwable e = getException();
				log.error("getCountriesTask:failed() - Error recuperando la respuesta del ws: " + e.getMessage(), e);
			}

		};
		task.start();

	}

	private TaxfreeInvoiceCreationRequest getPdfVoucherRequestFromInvoice() throws Exception {
		TaxfreeInvoiceCreationRequest pdfRequest = new TaxfreeInvoiceCreationRequest();
		com.comerzzia.cardoso.pos.persistence.taxfree.invoicecreation.request.JSONOperationData jsonOpData = new com.comerzzia.cardoso.pos.persistence.taxfree.invoicecreation.request.JSONOperationData();

		pdfRequest.setOperationId("GetData");
		jsonOpData.setDataRequest("GetPDFVoucher");
		jsonOpData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOpData.setInvoice((String) getDatos().get("invoiceFromMovimiento"));
		jsonOpData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		if (StringUtils.isBlank(jsonOpData.getInvoice()))
			jsonOpData.setInvoice((String) getDatos().get("invoice"));

		jsonOpData.setParameters(CARDOSOPagosController.PDF_80);
		pdfRequest.setJSONOperationData(jsonOpData);

		return pdfRequest;
	}

	
	private static void modificarValorTag(Document document, String tagName, String etiqueta) {
		log.debug("modificarValorTag()");
		Element root = document.getDocumentElement();
		Element cabecera;
		try {
			cabecera = XMLDocumentUtils.getElement(root, "cabecera", false);
			try {
				Element barcode = XMLDocumentUtils.getElement(cabecera, tagName, false);
				barcode.setTextContent(etiqueta);
			}
			catch (Exception e) {
				Element taxfreeBarcode = document.createElement(tagName);
				taxfreeBarcode.setTextContent(etiqueta);
				cabecera.appendChild(taxfreeBarcode);

			}
		}
		catch (XMLDocumentException e1) {
			log.error("modificarValorTag() - No se ha encontrado la cabecera");
		}

	}

	private static byte[] convertirXmlABytes(Document document) {
		log.debug("convertirXmlABytes()");
	    try {
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        transformer.transform(new DOMSource(document), new StreamResult(baos));
	        return baos.toByteArray();
	    } catch (TransformerException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private void updatearTicket(CARDOSOCabeceraTicket cab, String etiqueta, String uidActividad) throws Exception {
		
		log.debug("updatearTicket()");
		String apiKey = variablesServices.getVariableAsString(VariablesServices.WEBSERVICES_APIKEY);
		String uidTicket = cab.getUidTicket();
	
		//llamar a la ServicioBO
		try {
			taxfreeService.updateTicket(apiKey, uidActividad, uidTicket, etiqueta);
		}
		catch (Exception e) {
			log.error("updatearTicket() - Ha ocurrido un error: ", e);
			throw new Exception();
		}

	}
	
}
