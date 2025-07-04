
package com.comerzzia.cardoso.pos.services.taxfree;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.client.JerseyInvocation.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.api.rest.client.exceptions.RestConnectException;
import com.comerzzia.api.rest.client.exceptions.RestException;
import com.comerzzia.api.rest.client.exceptions.RestHttpException;
import com.comerzzia.api.rest.client.exceptions.RestTimeoutException;
import com.comerzzia.api.rest.path.BackofficeWebservicesPath;
import com.comerzzia.cardoso.pos.persistence.taxfree.TaxfreeXML;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.request.JSONOperationData;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.request.TaxfreeCountryRequest;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.Country;
import com.comerzzia.cardoso.pos.persistence.taxfree.country.response.TaxfreeCountryResponse;
import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.MovimientosTaxfree;
import com.comerzzia.cardoso.pos.persistence.taxfree.movimientos.TaxfreeConsultaResponse;
import com.comerzzia.cardoso.pos.services.core.sesion.CardosoSesion;
import com.comerzzia.cardoso.pos.services.taxfree.webservice.TaxfreeWebService;
import com.comerzzia.core.model.ventas.tickets.TicketBean;
import com.comerzzia.core.persistencia.ventas.tickets.TicketMapper;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.instoreengine.rest.ClientBuilder;
import com.comerzzia.pos.persistence.core.contadores.ContadorBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


@Service
@Primary
public class TaxfreeService {
	
	protected static final Logger log = Logger.getLogger(TaxfreeService.class);

	public static final String CODTIPODOCUMENTO_TAXFREE = "TAXFREE";	
	
	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@Autowired
	private CardosoSesion sesion;
	
	@Autowired
	protected VariablesServices variablesServices;
	
	@Autowired
	protected ServicioContadores contadoresService;
	
	@Autowired
	protected TicketMapper ticketMapper;
	
	protected DatosSesionBean datosSesion;
	
	@Autowired
	protected TaxfreeWebService taxfreeWebService;
	
	@Autowired
	protected TaxfreeVariablesService taxfreeVariablesService;
	
	protected HashMap<String, Object> datos = new HashMap<>();
	 
	public void comprobarTipoDocumento() throws Exception {
		TipoDocumentoBean documentoTaxfree = null;
		log.debug("comprobarTipoDocumento() - Comprobando documento en sesion");
		documentoTaxfree = sesion.getAplicacion().getDocumentos().getDocumento(CODTIPODOCUMENTO_TAXFREE);
		if(documentoTaxfree == null){
			String msgError = I18N.getTexto("No se ha encontrado el tipo de documento taxfree [" + CODTIPODOCUMENTO_TAXFREE + "]");
			throw new Exception(msgError);
		}
	}

	public void comprobacionesPreviasTicket(List<LineaTicket> lineasTicket, BigDecimal importe, BigDecimal impuestos) throws Exception {
		log.info("comprobacionesPreviasTicket() - Comprobando si el ticket cumple los requisitos para generar TAXFREE...");
		
		if (impuestos.compareTo(BigDecimal.ZERO) <= 0) {
			throw new Exception("La venta se encuentra exenta de impuestos.");
		}
		for (LineaTicket linea : lineasTicket) {
			if (linea.getCantidadDevuelta() != null && linea.getCantidadDevuelta().compareTo(BigDecimal.ZERO) == 1) {
				throw new Exception("Existen devoluciones asociadas al ticket, para poder iniciar un proceso de TAXFREE, haga una devolución global y vuelva a realizar la compra.");
			}
		}
		for (LineaTicket linea : lineasTicket) {
			if (linea.getCantidad().signum() == -1) {
				throw new Exception("La venta contiene lineas negativas.");
			}
		}
		if (importe.compareTo(BigDecimal.ZERO) == -1 || importe.compareTo(BigDecimal.ZERO) == 0) {
			throw new Exception("El importe total de la venta tiene que ser mayor de 0.");
		}
	}

	public boolean comparaImporteMinimo(BigDecimal importe) throws Exception {
		BigDecimal importeMinimo = new BigDecimal(getImporteMinimo());
		return importe.compareTo(importeMinimo) == 0 || importe.compareTo(importeMinimo) == 1;
	}
	
	public boolean compruebaPaisResidenciaFueraUE(String pais, TaxfreeCountryResponse paisesDisponiblesTaxfree ) throws Exception {
		//respuesta del ws para comprobar los paises con Taxfree disponible
		if(paisesDisponiblesTaxfree != null && paisesDisponiblesTaxfree.getHasError().equals("false") && !paisesDisponiblesTaxfree.getDetails().getCountries().isEmpty()) {
			for (Country countryResponse : paisesDisponiblesTaxfree.getDetails().getCountries()) {
				if(countryResponse.getCountryId().equalsIgnoreCase(pais)) {
					//si la lista tiene el pais del cliente, este, vive fuera de la UE es valido devolviendo true
					return true;
				}
			}
			//devuelve false si el pais de residencia esta en la UE 
			return false;
		}else {
			// si falla la peticion lanza error y devuelve false
			if(paisesDisponiblesTaxfree.getHasError().equals("true") && !paisesDisponiblesTaxfree.getErrorCode().isEmpty()) {
				throw new Exception("Error al realizar la petición. No se han podido recuperar los paises con taxfree disponible");
			}
			return false;
			}
		}

	public TaxfreeCountryRequest crearCountryRequest() throws Exception {
		TaxfreeCountryRequest countryRequest = new TaxfreeCountryRequest();
		JSONOperationData jsonOperationData = new JSONOperationData();
		
		countryRequest.setOperationId(TaxfreeVariablesService.OPERATION_ID_GET_DATA);
		jsonOperationData.setDataRequest("GetCountries");
		jsonOperationData.setCustAccount(taxfreeVariablesService.getCustAccount());
		jsonOperationData.setFormCountry(sesion.getAplicacion().getTienda().getCliente().getCodpais());
		countryRequest.setJSONOperationData(jsonOperationData);
		return countryRequest;
	}


	public String getImporteMinimo() throws Exception {
		String importe = "";
		try{
			//Dependiendo del pais añadir la variable en bbdd con la terminacion del pais y usar el mismo metodo para los distintos paises añadiendo casos al switch
			switch (sesion.getAplicacion().getTienda().getCliente().getCodpais()) {
			case "PT":
				importe = taxfreeVariablesService.getImporte();
				break;
			default:
				importe = "0";
				break;
			}
			
			if (StringUtils.isBlank(importe)) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = "La variable VARIABLE_TAXFREE_IMPORTE_" + sesion.getAplicacion().getTienda().getCliente().getCodpais() + " no se encontró o está mal definida.";
			log.error("getImporteMinimo() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError + e.getMessage(), e);
		}
		return importe;
	}
	

	public int insertDocument(String codTicket, String barcode, String status, String pasaporte, String codCaja, String uidTicket) throws Exception{
		log.info("insertDocument() - Insertando documento TAXFREE XML...");
		TipoDocumentoBean documentoTaxfree = null;
		int result = 0;
		String uidTicketTX = "";
		try{
			/* ################### TIPO DOCUMENTO ################### */
			comprobarTipoDocumento();
			
			ContadorBean contador = null;
			documentoTaxfree = sesion.getAplicacion().getDocumentos().getDocumento(CODTIPODOCUMENTO_TAXFREE);
			contador = contadoresService.obtenerContador(documentoTaxfree.getIdContador(), sesion.getAplicacion().getUidActividad());
			if(contador == null){
				String msgError = "No se ha encontrado el contador para el documento de tipo taxfree [" + documentoTaxfree.getCodtipodocumento() + "].";
				log.info("insertDocument() - " + msgError);
				throw new Exception(msgError);
			}
			
			/* ################### DOCUMENTO XML ################### */
			TaxfreeXML taxfreeXML = new TaxfreeXML();
			taxfreeXML.setUidActividad(sesion.getAplicacion().getUidActividad());
			taxfreeXML.setCodTicket(codTicket);
			taxfreeXML.setBarcode(barcode);
			taxfreeXML.setTipoMovimiento(status);
			taxfreeXML.setFechaMovimiento(format.format(new Date()));
			taxfreeXML.setCajaMovimiento(codCaja);
			taxfreeXML.setPasaporte(pasaporte);
			taxfreeXML.setUidTicket(uidTicket);
			
			/* ################### TICKET ################### */
			TicketBean ticket = new TicketBean();
			ticket.setUidActividad(taxfreeXML.getUidActividad());
			ticket.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
			ticket.setUidTicket(UUID.randomUUID().toString());
			ticket.setIdTipoDocumento(documentoTaxfree.getIdTipoDocumento());
			ticket.setCodCaja(codCaja);
			ticket.setFecha(new Date());
			ticket.setIdTicket(contador.getValor());
//			ticket.setCodTicket(contador.getDivisor3());
			ticket.setCodTicket(codTicket);
			ticket.setSerieTicket(contador.getDivisor3());
			ticket.setFirma("*");
			ticket.setLocatorId(ticket.getUidTicket());
			ticket.setProcesado("N");
			
			byte[] xmlDocumentoTaxfree = MarshallUtil.crearXML(taxfreeXML);
			ticket.setTicket(xmlDocumentoTaxfree);
			uidTicketTX = ticket.getUidTicket();
			result = ticketMapper.insert(ticket);
			
			if (result <= 0) {
				throw new Exception();
			}
		}
		catch(Exception e){
			String msgError = I18N.getTexto("No ha sido posible insertar en base de datos el documento XML de tipo " + documentoTaxfree.getCodtipodocumento());
			log.error("insertDocument() - " + msgError + " : " + e.getMessage(), e);
			throw new Exception(msgError, e);
		}
		log.debug("El uidTicket del documento Taxfree que se ha generado es: " + uidTicketTX);
		return result;
	}

	
	public MovimientosTaxfree getMovimientosTaxfree(String apiKey, String uidActividad, String codTicket, String barcode) throws RestHttpException, RestException {

		GenericType<TaxfreeConsultaResponse> genericType = new GenericType<TaxfreeConsultaResponse>(){};
		MovimientosTaxfree response = new MovimientosTaxfree();
		
		try {
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path("/consultaTaxfree/consulta");				

			target = target.queryParam("apiKey", apiKey).queryParam("uidActividad", uidActividad).queryParam("codTicket", codTicket).queryParam("barcode", barcode);
    		
			log.info("getMovimientosTaxfree() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
			
			Builder builder = (Builder) target.request(MediaType.APPLICATION_XML);

			TaxfreeConsultaResponse respuesta = builder.get(genericType);
			
			if(respuesta!=null) {
				 response = tfResponseToMovimientoTf(respuesta);
			}else {
				throw new Exception();
			}
			
			return response;

			
    	}catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
		}
		catch (WebApplicationException e) {
			throw new RestHttpException(e.getResponse().getStatus(),
			        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			else if (e.getCause() instanceof SocketTimeoutException) {
				throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		
	}
	
	public MovimientosTaxfree getMovimientosTaxfreeByExample(String apiKey, String uidActividad, String codTicket, String barcode) throws RestHttpException, RestException {

		GenericType<TaxfreeConsultaResponse> genericType = new GenericType<TaxfreeConsultaResponse>(){};
		MovimientosTaxfree response = new MovimientosTaxfree();
		
		if(barcode.isEmpty())
			barcode = "noBarcode";
		
		try {
			WebTarget target = ClientBuilder.getClient().target(BackofficeWebservicesPath.servicio).path("/consultaTaxfree/consultaByExample");				

			target = target.queryParam("apiKey", apiKey).queryParam("uidActividad", uidActividad).queryParam("codTicket", codTicket).queryParam("barcode", barcode);
    		
			log.info("getMovimientosTaxfreeByExample() - URL de servicio rest en la que se realiza la petición: " + target.getUri());
			
			Builder builder = (Builder) target.request(MediaType.APPLICATION_XML);

			TaxfreeConsultaResponse respuesta = builder.get(genericType);
			
			if(respuesta!=null) {
				 response = tfResponseToMovimientoTf(respuesta);
			}else {
				throw new Exception();
			}
			
			return response;

			
    	}catch(BadRequestException e){
        	throw RestHttpException.establecerException(e);
		}
		catch (WebApplicationException e) {
			throw new RestHttpException(e.getResponse().getStatus(),
			        "Se ha producido un error HTTP " + e.getResponse().getStatus() + ". Causa: " + e.getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (ProcessingException e) {
			if (e.getCause() instanceof ConnectException) {
				throw new RestConnectException("Se ha producido un error al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			else if (e.getCause() instanceof SocketTimeoutException) {
				throw new RestTimeoutException("Se ha producido timeout al conectar con el servidor - " + e.getLocalizedMessage(), e);
			}
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}
		
	}

	private MovimientosTaxfree tfResponseToMovimientoTf(TaxfreeConsultaResponse respuesta) {
		MovimientosTaxfree mov = new MovimientosTaxfree();
		
		mov.setUidActividad(respuesta.getUidActividad());
		mov.setCodTicket(respuesta.getCodTicket());
		mov.setBarcode(respuesta.getBarcode());
		mov.setTipoMovimiento(respuesta.getTipoMovimiento());
		mov.setFechaMovimiento(respuesta.getFechaMovimiento());
		mov.setCajaMovimiento(respuesta.getCajaMovimiento());
		
		return mov;
	}

	public HashMap<String, Object> getDatos() {
		return datos;
	}

	public void updateTicket(String apiKey, String uidActividad, String uidTicket, String etiqueta) throws Exception {
		log.debug("updateTicket()");

		try {
			Client client = Client.create();
			String url = BackofficeWebservicesPath.servicio +"/consultaTaxfree/updateTicket" + "?uidTicket=" + uidTicket +"&apiKey=" + apiKey + "&uidActividad=" + uidActividad
					+ "&etiqueta=" + etiqueta ;
			
			log.info("updateTicket() - REQUEST : " + url);
			WebResource webResource = client.resource(url);
			ClientResponse response = webResource.put(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				log.info("updateTicket() - Error updateando el ticket en la API. Response Status: " + response.getStatus());
				throw new Exception();				
			} else {
				log.info("updateTicket() - Update realizado. Response Status: " + response.getStatus());
			}
		} catch (Exception e) {
			String msgError = I18N.getTexto(e.getMessage());
			log.error("updateTicket() - " + msgError);
			throw new Exception();
		}
		
	}
}
