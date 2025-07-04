package com.comerzzia.cardoso.pos.gui.ventas.devoluciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.comerzzia.cardoso.pos.gui.ventas.tickets.CARDOSOTicketManager;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesController;
import com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.autorizaracciones.AutorizarAccionesView;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.request.JSONOperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.request.TaxfreeCancellationRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.cancellation.response.TaxfreeCancellationResponse;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeService;
import com.comerzzia.cardoso.pos.services.taxfree.TaxfreeVariablesService;
import com.comerzzia.cardoso.pos.services.taxfree.webservice.TaxfreeWebService;
import com.comerzzia.cardoso.pos.services.ticket.CardosoTicketsService;
import com.comerzzia.cardoso.pos.services.ticket.cabecera.CARDOSOCabeceraTicket;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.xml.XMLDocumentException;
import com.comerzzia.core.util.xml.XMLDocumentUtils;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.componentes.dialogos.VentanaDialogoComponent;
import com.comerzzia.pos.gui.ventas.devoluciones.DevolucionesController;
import com.comerzzia.pos.gui.ventas.devoluciones.IntroduccionArticulosView;
import com.comerzzia.pos.gui.ventas.tickets.articulos.FacturacionArticulosController;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.util.i18n.I18N;
import com.google.gson.Gson;

@Controller
@Primary
public class CARDOSODevolucionesController extends DevolucionesController{

	private static final Logger log = Logger.getLogger(CARDOSODevolucionesController.class.getName());
	
	@Autowired
	private Sesion sesion;
	@Autowired
	private VariablesServices variablesServices;
	
	@Autowired
	protected TaxfreeService taxfreeService;
	
	@Autowired
	protected TaxfreeVariablesService taxfreeVariablesService;
	
	@Autowired
	protected TaxfreeWebService taxfreeWebService;
	
	@Autowired
	protected CardosoTicketsService ticketService;
	
	/**
	 * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL
	 * 
	 * En caso de estar la variable completa y ser la caja indicada en esa variable no permitimos realizar devoluciones.
	 */
	
	public static final String VARIABLE_CAJA_ESPECIAL = "TPV.CAJA_ESPECIAL";
	
	@Override
	public void initializeForm() throws InitializeGuiException{
	    super.initializeForm();
	    
	    String variable = variablesServices.getVariableAsString(VARIABLE_CAJA_ESPECIAL);
		if(StringUtils.isNotBlank(variable)){
			
			log.debug("initializeForm() : GAP - PERSONALIZACIONES V3 - CAJA ESPECIAL");
			
			if(variable.equals(sesion.getSesionCaja().getCajaAbierta().getCodCaja())){
				String msgError = I18N.getTexto("No se pueden realizar devoluciones en esta caja.");
				log.error("initializeForm() - " + msgError);
				throw new InitializeGuiException(msgError);
				
			}
		}
	}
	
	@Override
	protected void recuperarTicketDevolucionSucceeded(boolean encontrado){
		// GAP - DEVOLUCIÓN SIN DOCUMENTO ORIGEN
		// Comprobamos la longitud sea menor que 8 para evitar entrar en el proceso con localizadores.
		boolean esDevolucionSinOrigen = false;
		if(!encontrado && ((CARDOSOTicketManager) ticketManager).getRealizarDevolucionSinDocumento()){ 
			String msgError = "";
			String codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
			if("PT".equalsIgnoreCase(codPais)){
				msgError = I18N.getTexto("O documento fonte não foi encontrado na central.\r\nDeseja continuar com a devolução?");
			}
			else{
				msgError = I18N.getTexto("No se ha encontrado el documento origen en central.\r\n¿Desea continuar con la devolución?");
			}
			
			if(VentanaDialogoComponent.crearVentanaConfirmacion(msgError, this.getStage())){
				encontrado = true;
				esDevolucionSinOrigen = true;
			}
			else{
				return;
			}
		}
		
		// GAP - CAJERO AUXILIAR
		if(encontrado){
			if(((CARDOSOTicketManager) ticketManager).necesitaAutorizacion()){
				log.debug("recuperarTicketDevolucionSucceeded() : GAP - CAJERO AUXILIAR");
				getApplication().getMainView().showModalCentered(AutorizarAccionesView.class, getDatos(), getStage());
				if(((String) getDatos().get(AutorizarAccionesController.sDocumento) != null) && ((String) getDatos().get(AutorizarAccionesController.sTienda) != null)){
					((CARDOSOTicketManager) ticketManager).setDatosAutorizacion((String) getDatos().get(AutorizarAccionesController.sUsuario),
					        (String) getDatos().get(AutorizarAccionesController.sNombre), (String) getDatos().get(AutorizarAccionesController.sDocumento),
					        (String) getDatos().get(AutorizarAccionesController.sTienda));
				}
				else{
					return;
				}
			}
		}
		
		// PARTE ESTÁNDAR
		if (encontrado) {
			// TAXFREE
			boolean cancelaDevolucion = false;
			CARDOSOCabeceraTicket cabeceraOrigen = (CARDOSOCabeceraTicket) ticketManager.getTicketOrigen().getCabecera();
				if (StringUtils.isNotBlank(cabeceraOrigen.getIdTaxfree())) {
					cancelaDevolucion = true;
					String msgAviso = I18N.getTexto("Hay movimientos de taxfree asociado a este ticket, para realizar su devolución tiene que anularlos primero ¿Desea realizar esta operación?.");
					boolean confirmacion = VentanaDialogoComponent.crearVentanaConfirmacion(msgAviso, getStage());
					if (!confirmacion) {
						lbMensajeError.setText(I18N.getTexto("Tiene movimientos de Taxfree asociados y no se puede realizar ninguna operación"));
						return;
					}
					
					// anularTF 
					String msgAvisoConfirm = I18N.getTexto("Se procedera a ANULAR el Taxfree asociado a este documento ¿Esta seguro que desea realizar esta operación?.");
					boolean confirmacionDoble = VentanaDialogoComponent.crearVentanaConfirmacion(msgAvisoConfirm, getStage());
					if (!confirmacionDoble) {
						lbMensajeError.setText(I18N.getTexto("Tiene movimientos de Taxfree asociados y no se puede realizar ninguna operación"));
						return;
					}
					try {
						TaxfreeCancellationResponse cancelResponse = llamadaCancellationTaxfree(cabeceraOrigen);

						if (cancelResponse != null && cancelResponse.getHasError().equals("false") && cancelResponse.getErrorCode().equals("OK001")) {
							String etiqueta = "";

							TicketBean ticketBeanLocal = new TicketBean();
							
							DatosSesionBean datosSesion = new DatosSesionBean();
							datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
							datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
							datosSesion.setCodEmpresa(sesion.getAplicacion().getEmpresa().getCodEmpresa());

							String uidTicket = ticketManager.getTicketOrigen().getUidTicket();

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
										ticketService.inserccionTaxfreeXML(etiqueta, ticketManager.getTicketOrigen().getCabecera().getCodTipoDocumento(), ticketManager.getTicketOrigen().getCabecera().getCodTicket(),
										        codCajaTicket, ticketManager.getTicketOrigen().getUidTicket());
									}
								}
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
							
							VentanaDialogoComponent.crearVentanaInfo("TAXFREE anulado correctamente", getStage());
							cancelaDevolucion = false;

						}
						else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("WET380")) {
							lbMensajeError.setText("EL DOCUMENTO YA HA SIDO SELLADO, NO SE PUEDE CANCELAR");
							cancelaDevolucion = true;

						}
						else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("SYS117566")) {
							lbMensajeError.setText("REEMBOLSADO PERO NO FACTURADO A TIENDA");
							cancelaDevolucion = true;

						}
						else if (cancelResponse == null || cancelResponse.getHasError().equals("true") && cancelResponse.getErrorCode().equals("SYS117567")) {
							lbMensajeError.setText("REEMBOLSADO Y FACTURADO A LA TIENDA");
							cancelaDevolucion = true;

						}
						else {
							lbMensajeError.setText("ERROR EN LA PETICIÓN DE TAXFREE");
							cancelaDevolucion = true;
						}

					}
					catch (Exception e) {
						log.error("llamadaCancellationTaxfree() - No se ha obtenido respuesta satisfactoria de Taxfree");
						cancelaDevolucion = true;
						VentanaDialogoComponent.crearVentanaError(getStage(), String.format(I18N.getTexto("Ha habido un problema con la cancelacion de Taxfree"), e.getMessage()), e);
					}
			}
			if (!cancelaDevolucion) {
				boolean esMismoTratamientoFiscal = ticketManager.comprobarTratamientoFiscalDev();
				if (!esMismoTratamientoFiscal) {
					try {
						ticketManager.eliminarTicketCompleto();
					} catch (Exception e) {	log.error("recuperarTicketDevolucionSucceeded() - Ha habido un error al eliminar los tickets: "	+ e.getMessage(), e);
					}

					lbMensajeError.setText(I18N.getTexto("El ticket fue realizando en una tienda con un tratamiento fiscal diferente al de esta tienda. No se puede realizar esta devolución."));
					return;
				} else {
					try {
						getDatos().put(FacturacionArticulosController.TICKET_KEY, ticketManager);
						getView().changeSubView(IntroduccionArticulosView.class);
					} catch (InitializeGuiException e) {
						if (e.isMostrarError()) {
							log.error("accionCambiarArticulo() - Error abriendo ventana", e);
							VentanaDialogoComponent.crearVentanaError(getStage(),I18N.getTexto("Error cargando pantalla. Para mas información consulte el log."), e);
						}
					}
				}

				// En caso de no encontrar documento origen se tiene que evitar esta parte, ya que no existe ticket.
				if (!esDevolucionSinOrigen) {
					boolean recoveredOnline = ticketManager.getTicket().getCabecera().getDatosDocOrigen().isRecoveredOnline();
					if (!recoveredOnline) {
						String msgAviso = I18N.getTexto("No se han podido recuperar las líneas devueltas desde la central. Por favor, compruebe el ticket impreso para ver las líneas ya devueltas.");
						VentanaDialogoComponent.crearVentanaAviso(msgAviso, getStage());
					}
				}
			}else {
				if(lbMensajeError.getText().isEmpty()) {
					lbMensajeError.setText(I18N.getTexto("Tiene movimientos de Taxfree asociados y no se puede realizar ninguna operación"));
				}
			}
		} 
		else {
			lbMensajeError.setText(I18N.getTexto("No se ha encontrado ningún ticket con esos datos"));
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
	
	private TaxfreeCancellationResponse llamadaCancellationTaxfree(CARDOSOCabeceraTicket cabeceraOrigen) throws Exception {

		TaxfreeCancellationRequest cancellationRequest = createCancellationRequest(cabeceraOrigen);

		String contenidoRespuesta = taxfreeWebService.llamadaTaxfree(new Gson().toJson(cancellationRequest));
		
		TaxfreeCancellationResponse response = new Gson().fromJson(contenidoRespuesta, TaxfreeCancellationResponse.class);
		
		return response;
	}
	
    private TaxfreeCancellationRequest createCancellationRequest(CARDOSOCabeceraTicket cabeceraOrigen) throws Exception {
    	TaxfreeCancellationRequest cancelRequest = new TaxfreeCancellationRequest();
		JSONOperationData jsonOperationData = new JSONOperationData();
    	boolean tieneBarcode =	StringUtils.isNotBlank(cabeceraOrigen.getIdTaxfree());
    	
    	boolean esVentaAnonima = true;
		if(ticketManager.getTicketOrigen().getCabecera().getDatosFidelizado() != null || ticketManager.getTicketOrigen().getCabecera().getCliente().getDatosFactura() != null){
			esVentaAnonima = false;
		}
		String codPais = null;
		if(!esVentaAnonima) {
			codPais = ticketManager.getTicketOrigen().getCabecera().getDatosFidelizado() != null ? ticketManager.getTicketOrigen().getCabecera().getDatosFidelizado().getCodPais() : null;
			if(StringUtils.isBlank(codPais))
			codPais = ticketManager.getTicketOrigen().getCabecera().getCliente().getDatosFactura()!= null ? ticketManager.getTicketOrigen().getCabecera().getCliente().getDatosFactura().getPais() : null;
		}
		if (!StringUtils.isNotBlank(codPais)) {
			codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
		}
		
		//formamos el objeto de cancelación
    	jsonOperationData.setDataRequest("CancelForm");
		jsonOperationData.setBarCode(tieneBarcode ? cabeceraOrigen.getIdTaxfree() : "");
		jsonOperationData.setInvoice(tieneBarcode ? "" : cabeceraOrigen.getCodTicket());
		jsonOperationData.setFormCountry(codPais);
		jsonOperationData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOperationData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		
		cancelRequest.setOperationId("GetData");
		cancelRequest.setJSONOperationData(jsonOperationData);
    	
		return cancelRequest;
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
}
