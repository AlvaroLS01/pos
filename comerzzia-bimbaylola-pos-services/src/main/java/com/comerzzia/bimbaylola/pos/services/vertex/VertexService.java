package com.comerzzia.bimbaylola.pos.services.vertex;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.RespuestaTokenDTO;
import com.comerzzia.bimbaylola.pos.persistence.articulos.stock.SolicitarTokenDTO;
import com.comerzzia.bimbaylola.pos.persistence.impuestos.porcentajes.ByLPorcentajeImpuesto;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.Customer;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.Destination;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.EnviarPeticionImpuesto;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.LineItem;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.PhysicalOrigin__1;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.Product;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.Quantity;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.peticion.Seller__1;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.RecibirPeticionImpuesto;
import com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.Tax;
import com.comerzzia.bimbaylola.pos.services.core.impuestos.porcentajes.PorcentajeImpuestosService;
import com.comerzzia.bimbaylola.pos.services.core.variables.ByLVariablesServices;
import com.comerzzia.bimbaylola.pos.services.ticket.ByLTicketVentaAbono;
import com.comerzzia.bimbaylola.pos.services.ticket.cabecera.ByLCabeceraTicket;
import com.comerzzia.bimbaylola.pos.services.ticket.profesional.ByLLineaTicketProfesional;
import com.comerzzia.bimbaylola.pos.services.vertex.exception.VertexException;
import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.core.documentos.propiedades.PropiedadDocumentoBean;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.paises.PaisBean;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.documentos.Documentos;
import com.comerzzia.pos.services.core.paises.PaisNotFoundException;
import com.comerzzia.pos.services.core.paises.PaisService;
import com.comerzzia.pos.services.core.paises.PaisServiceException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.rest.client.exceptions.RestException;
import com.comerzzia.rest.client.exceptions.RestTimeoutException;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class VertexService {

	protected static final Logger log = Logger.getLogger(VertexService.class);
	
	@Autowired
	protected VariablesServices variablesServices;
	@Autowired
	protected PorcentajeImpuestosService porcentajeImpuestosService;
	@Autowired
	protected PaisService paisService;
	@Autowired
	protected Sesion sesion;
	@Autowired
	protected Documentos documentos;
	
	public static final String GRANT_TYPE = "VERTEX.GRANT_TYPE";
	public static final String CLIENT_ID = "VERTEX.CLIENT_ID";
	public static final String CLIENT_SECRET = "VERTEX.CLIENT_SECRET";

	public static final String VERTEX_TIPO_QUOTATION = "QUOTATION";
	public static final String VERTEX_TIPO_INVOICE = "INVOICE";
	
	public static final String INTEGRACION_IMPUESTOS = "POS.TIENE_INTEGRACION_IMPUESTOS";
	
	protected Map<String, String> jurisdictionNameMap = new HashMap();
	
	public boolean mockCallSucceed() throws VertexException {
		return true;
	}

	public boolean mockCallFail() throws VertexException {
		throw new VertexException();
	}
	
	public RespuestaTokenDTO solicitarToken() throws RestException {
		log.debug("solicitarToken() - Solicitando token a vertex");
		RespuestaTokenDTO token = null;

		try {
			String grantType = variablesServices.getVariableAsString(GRANT_TYPE);
			String clientID = variablesServices.getVariableAsString(CLIENT_ID);
			String clientSecret = variablesServices.getVariableAsString(CLIENT_SECRET);
			token = solicitarTokenImpuestos(new SolicitarTokenDTO(grantType, clientID, clientSecret, null, null));
			log.debug("Token recibido: " + token.getAccess_token());
		}
		catch (RestException e) {
			log.error("solicitarToken() - Ha ocurrido un error " + e.getMessage());
			throw new RestException(e);
		}

		return token;

	}
	
	public void peticionVertex(String tipoPeticion, ITicket ticket, String tipoDocumento, ByLTicketVentaAbono ticketVentaAbono, Boolean esSoloDevolucion) throws Exception {
		log.debug("peticionVertex() - Comenzando la peticion a vertex de tipo - " + tipoPeticion);
		EnviarPeticionImpuesto peticion = null;
		String json = null;
		RecibirPeticionImpuesto jsonRespuesta = null;
		
		RespuestaTokenDTO token = solicitarToken();
		
		peticion = rellenarJSonAVertex(ticket, tipoPeticion, ticketVentaAbono);
		json = new Gson().toJson(peticion);
		
		String respuesta = llamadaVertex(json, token.getAccess_token());
		Gson gson = new Gson();
		jsonRespuesta = gson.fromJson(respuesta, RecibirPeticionImpuesto.class);

		if (jsonRespuesta == null || jsonRespuesta.getData() == null) {
			log.error("peticionVertex() - Se ha producido un error realizando la petición de Vertex: El json de respuesta viene vacío");
			throw new Exception("Se ha producido un error realizando la petición de Vertex");
		}

		if (tipoPeticion.equals(VERTEX_TIPO_QUOTATION)) {
			sumarImpuestosLinea(jsonRespuesta, ticket);
			guardarImpuestosTicket(jsonRespuesta, ticket, tipoDocumento);
		}
		
		if (tipoPeticion.equals(VERTEX_TIPO_INVOICE) && esSoloDevolucion) {
			guardarImpuestosTicket(jsonRespuesta, ticket, tipoDocumento);
		}
	}
	
	public EnviarPeticionImpuesto rellenarJSonAVertex(ITicket ticket, String tipoPeticion, ByLTicketVentaAbono ticketVentaAbono) {
		log.debug("rellenarJSonAVertex() - Peticion " + tipoPeticion);
		EnviarPeticionImpuesto peticion = inicializarDatosGenerales(ticket, tipoPeticion, false);
		List<LineItem> lineItems = new ArrayList<>();
		List<ByLLineaTicketProfesional> lineasTicket = ticket.getLineas();

		/*
		 * Al pasar a la pantalla de pagos si se hace una devolucion con venta habrá lineas negativas y positivas, y de
		 * las negativas no hay que calcular el impuesto
		 */
		if (tipoPeticion.equals(VERTEX_TIPO_QUOTATION)) {
			for (ByLLineaTicketProfesional lineaTicket : lineasTicket) {
				/* Comprobar si hay lineas negativas para devolucion */
				if (BigDecimalUtil.isMayorACero(lineaTicket.getImporteTotalSinDto())) {
					LineItem linea = inicializarDatosGeneralesJson(lineaTicket);
					/* precio articulo */
					linea.setUnitPrice(lineaTicket.getPrecioSinDto());
					/* Precio Total */
					linea.setExtendedPrice(lineaTicket.getImporteConDto());
					
					lineItems.add(linea);
				}
			}
		}
		else {
			for (ByLLineaTicketProfesional lineaTicket : lineasTicket) {
				LineItem linea = inicializarDatosGeneralesJson(lineaTicket);

				BigDecimal price = new BigDecimal(lineaTicket.getLineaVertex().getAmount());
				if (ticket.isEsDevolucion()) {
					price = price.negate();
				}
				linea.setExtendedPrice(price);
				linea.setUnitPrice(price);

				/*
				 * En el caso que sea una devolucion, se añadira el campo TAXDATE con la fecha de la venta original
				 */
				if(ticket.isEsDevolucion()) {
					String fechaString = ticket.getCabecera().getDatosDocOrigen().getFecha();
					if (StringUtils.isNotBlank(fechaString)) {
						SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
						String taxDate = sdf.format(Fecha.getFecha(fechaString, "Mm/dd/yy").getDate());

						linea.setTaxDate(taxDate);
					}
				}
				
				lineItems.add(linea);
			}
		}
		peticion.setLineItems(lineItems);
		return peticion;
	}
	
	public LineItem inicializarDatosGeneralesJson(ByLLineaTicketProfesional lineaTicket) {
		log.debug("inicializarDatosGeneralesJson()");

		LineItem linea = new LineItem();
		Product product = new Product();
		
		/* valor de codigoArticulo+colortalla */
		String codArt = lineaTicket.getCodArticulo();
		String talla = lineaTicket.getDesglose1();
		String color = lineaTicket.getDesglose2();
		product.setProductClass(codArt + talla + color);
		product.setValue(codArt + talla + color);
		linea.setProduct(product);

		/* cantidad de articulos */
		Quantity cantidad = new Quantity();
		cantidad.setValue(BigDecimalUtil.redondear(lineaTicket.getCantidad().abs(), 0).toString());
		linea.setQuantity(cantidad);
		linea.setTransactionType("SALE");
		linea.setLineItemId(lineaTicket.getIdLinea().toString());
		linea.setLineItemNumber(new BigDecimal(lineaTicket.getIdLinea()));

		return linea;
	}

	public RespuestaTokenDTO solicitarTokenImpuestos(SolicitarTokenDTO solicitarToken) throws RestException, RestTimeoutException {
		log.debug("solicitarTokenImpuestos()/ArticulosService - Inicio de la solicitud de token para impuesto de un articulo");

		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(null, null, null);
		}
		catch (Exception e) {
			throw new RestException(e);
		}

		Client client = javax.ws.rs.client.ClientBuilder.newBuilder().sslContext(ctx).build();

		// Aqui cambiar la url

		/* Creamos la URL desde donde queremos traer el token. */
		WebTarget target = client.target("").path(variablesServices.getVariableAsString(ByLVariablesServices.URL_IMPUESTOS_SOLOCITAR_TOKEN));
		log.debug("solicitarTokenImpuestos" + "()/ArticulosService - URL de servicio rest en la que se realiza la petición: " + target.getUri());

		Form formData = new Form();
		formData.param("client_id", solicitarToken.getClientId());
		formData.param("client_secret", solicitarToken.getClientSecret());
		formData.param("scope", solicitarToken.getScopeCards());
		formData.param("grant_type", solicitarToken.getGrantType());

		/* Creamos el Gson y el Entity para poder controlar el formato de salida de la respuesta. */
		Gson gson = new Gson();
		Entity entity = Entity.form(formData);

		try {
			Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(entity);

			log.debug("solicitarTokenImpuestos()/ArticulosService - Fin de la solicitud de token para impuesto de un articulo");
			String respuesta = response.readEntity(String.class);
			log.debug("solicitarTokenImpuestos() - Respuesta del servicio de tokens: " + respuesta);
			if (response.getStatus() != 200) {
				throw new ProcessingException("Se ha producido un error HTTP " + response.getStatus());
			}
			return gson.fromJson(respuesta, RespuestaTokenDTO.class);
		}
		catch (ProcessingException e) {
			throw new RestTimeoutException(e);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}

	}

	public String llamadaVertex(String peticionJson, String token) throws RestException, RestTimeoutException {
		log.debug("llamadaVertex() - Inicio de la llamada a vertex");
		String respuesta = null;
		OkHttpClient client = new OkHttpClient();
		SSLContext context;
		try {
			context = SSLContext.getInstance("TLSv1.2");
			context.init(null, null, null);
			client.setSslSocketFactory(context.getSocketFactory());

			com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/json");
			Request request = null;
			String url = variablesServices.getVariableAsString(ByLVariablesServices.URL_SOLICITAR_IMPUESTO);
			RequestBody body = RequestBody.create(mediaType, peticionJson);

			log.debug("llamadaVertex() - URL de servicio rest en la que se realiza la petición: " + url);
			log.debug("llamadaVertex() - JSON enviado: " + peticionJson);
			request = new Request.Builder().url(url).post(body).addHeader("content-type", "application/json").addHeader("authorization", "Bearer " + token).build();

			com.squareup.okhttp.Response response;
			response = client.newCall(request).execute();
			respuesta = response.body().string();

			log.debug("llamadaVertex() - JSON recibido: " + respuesta);
		}
		catch (Exception e) {
			throw new RestException("Se ha producido un error realizando la petición del impuesto. Causa: " + e.getCause().getClass().getName() + " - " + e.getLocalizedMessage(), e);
		}

		return respuesta;
	}

	public void sumarImpuestosLinea(RecibirPeticionImpuesto response, ITicket ticket) {
		log.debug("sumarImpuestosLineas()");
		List<com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem> lineItems = response.getData().getLineItems();
		Double impuestos = 0d;

		for (com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem lineItem : lineItems) {
			ByLLineaTicketProfesional bylLineaTicket = (ByLLineaTicketProfesional) ticket.getLinea(Integer.valueOf(lineItem.getLineItemId()));
			impuestos = lineItem.getTotalTax();
			BigDecimal precio = bylLineaTicket.getPrecioSinDto();
			BigDecimal total = new BigDecimal(impuestos).divide(bylLineaTicket.getCantidad());
			bylLineaTicket.setPrecioSinDto(precio);
			precio = precio.add(total);
			precio = BigDecimalUtil.redondear(precio, 2);
			bylLineaTicket.setPrecioTotalSinDto(precio);
//			bylLineaTicket.recalcularPreciosImportes();
//			ticket.getTotales().recalcular();
			log.debug("sumarImpuestosLineas() - idLinea: " + bylLineaTicket.getIdLinea() + " - Impuestos añadidos: " + impuestos);
		}
	}

	public void guardarImpuestosTicket(RecibirPeticionImpuesto jsonRespuesta, ITicket ticket, String tipoDocumento) {
		log.debug("guardarImpuestosTicket() - Comenzando a guardar los impuestos para nav en el ticket ");
		/* Datos Cabecera */
		ByLCabeceraTicket cabecera = (ByLCabeceraTicket) ticket.getCabecera();
		if (cabecera.getCabeceraVertex() == null) {
			cabecera.inicializarCabeceraVertex();
		}
		CabeceraVertex cabeceraVertex = cabecera.getCabeceraVertex();
		cabeceraVertex.setDocumentType(tipoDocumento);
		cabecera.setEsVertexOnline(true);
		/* Total sin impuestos */
		cabeceraVertex.setAmount(jsonRespuesta.getData().getSubTotal().toString());

		/* Total con impuestos */
		cabeceraVertex.setAmountWithTax(jsonRespuesta.getData().getTotal().toString());

		/* id del area impuesto */
		cabeceraVertex.setTaxAreaId(jsonRespuesta.getData().getCustomer().getDestination().getTaxAreaId());

		/* Enviado a vertex */
		cabeceraVertex.setIsSentToVertex("S");

		/* Lineas */
		List<com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem> lineItems = jsonRespuesta.getData().getLineItems();

		for (com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem lineItem : lineItems) {
			ByLLineaTicketProfesional bylLineaTicket = (ByLLineaTicketProfesional) ticket.getLinea(Integer.valueOf(lineItem.getLineItemId()));
			guardarLineasImpuesto(bylLineaTicket, lineItem, tipoDocumento);
		}

	}

	public void guardarLineasImpuesto(ByLLineaTicketProfesional byLLineaTicket, com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem lineaJson, String tipoDocumento) {
		log.debug("guardarLineasImpuesto()");

		if (byLLineaTicket.getLineaVertex() == null) {
			byLLineaTicket.inicializarLineaVertex();
		}
		
		LineaVertex lineaVertex = byLLineaTicket.getLineaVertex();
		com.comerzzia.bimbaylola.pos.persistence.tickets.vertex.respuesta.LineItem lineItem = lineaJson;

		lineaVertex.setDocumentType(tipoDocumento);

		lineaVertex.setLineNumber(byLLineaTicket.getIdLinea().toString());

		lineaVertex.setAmount(lineItem.getExtendedPrice().toString());

		lineaVertex.setAmountWithTax(String.valueOf(lineItem.getExtendedPrice() + lineItem.getTotalTax()));

		List<LineaDetailVertex> listaImpuestosVertex = byLLineaTicket.getListaImpuestosVertex();
		if (listaImpuestosVertex == null) {
			byLLineaTicket.inicializarListaImpuestos();
			listaImpuestosVertex = byLLineaTicket.getListaImpuestosVertex();
		}
		
		lineItem.getTaxes();
		List<Tax> taxes = lineItem.getTaxes();
		for (Tax impuesto : taxes) {
			LineaDetailVertex impuestoVertex = new LineaDetailVertex();

			impuestoVertex.setDocumentType(tipoDocumento);
			impuestoVertex.setDocumentNumber("");
			impuestoVertex.setLineNumber(byLLineaTicket.getIdLinea().toString());
			impuestoVertex.setTaxTypeId(impuesto.getJurisdiction().getJurisdictionId());
			impuestoVertex.setTaxAmount(impuesto.getCalculatedTax().toString());
			
			/* [BYL-197] Para el envio a Navision */
			Double taxPercentageFormateado = impuesto.getEffectiveRate() * 100;
			impuestoVertex.setTaxPercentage(taxPercentageFormateado.toString());
			
			impuestoVertex.setJurisdictionName(impuesto.getJurisdiction().getValue());
			impuestoVertex.setTaxTypeName(impuesto.getJurisdiction().getJurisdictionType());

			listaImpuestosVertex.add(impuestoVertex);
		}
	}

	public EnviarPeticionImpuesto inicializarDatosGenerales(ITicket ticket, String tipoPeticion, Boolean esDevolucion) {
		log.debug("inicializarDatosGenerales() - tipo de peticion:  " + tipoPeticion);
		EnviarPeticionImpuesto peticion = new EnviarPeticionImpuesto();
		Customer customer = new Customer();
		Destination destination = new Destination();
		ticket.getCliente().getDatosFactura();

		ClienteBean cliente = ticket.getCliente();
		String pais = "";
		if (cliente.getCodpais() != null) {
			pais = cliente.getCodpais();
		}
		else {
			try {
				PaisBean paisBean = paisService.consultarCodPais(cliente.getCodpais());
				if(paisBean != null) {
					pais = paisBean.getCodPais();
				}
			}
			catch (PaisNotFoundException | PaisServiceException e) {
				log.debug("inicializarDatosGenerales() - Error al buscar el pais del cliente: " + e.getMessage());
			}

		}

		destination.setCountry(pais);
		destination.setMainDivision(cliente.getProvincia());
		destination.setPostalCode(cliente.getCp());
		destination.setCity(cliente.getLocalidad());
		destination.setStreetAddress1(cliente.getDomicilio());
		customer.setDestination(destination);
		
		peticion.setCustomer(customer);
		if (!ticket.isEsDevolucion()) {
			Date fecha = new Date();
			String fechaAMD = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
			peticion.setDocumentDate(fechaAMD);
			peticion.setDocumentNumber(ticket.getCabecera().getCodTicket());
			peticion.setPostingDate(fechaAMD);
		}
		else {
			String fechaString = ticket.getCabecera().getDatosDocOrigen().getFecha();

			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			String fechaAMD = sdf.format(Fecha.getFecha(fechaString, "Mm/dd/yy").getDate());
			peticion.setDocumentDate(fechaAMD);

			Date fecha = new Date();
			String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
			peticion.setPostingDate(fechaActual);
		}

		peticion.setDocumentNumber(ticket.getCabecera().getCodTicket());
		peticion.setSaleMessageType(tipoPeticion);
		peticion.setTransactionType("SALE");
		Seller__1 seller = new Seller__1();

		/* DATOS DE LA COMPAÑIA */
		 String company = variablesServices.getVariableAsString(ByLVariablesServices.VERTEX_EMPRESA);
		 seller.setCompany(company);
		 PhysicalOrigin__1 physicalOrigin = new PhysicalOrigin__1();
//		 physicalOrigin.setCountry(pais);
		 physicalOrigin.setMainDivision(cliente.getProvincia());
		 physicalOrigin.setPostalCode(cliente.getCp());
		 physicalOrigin.setCity(cliente.getLocalidad());
		 physicalOrigin.setStreetAddress1(cliente.getDomicilio());
		 physicalOrigin.setSubDivision(cliente.getPoblacion());
		  
		 seller.setPhysicalOrigin(physicalOrigin);
		 peticion.setSeller(seller);

		return peticion;
	}

	public void modoOffline(ITicket ticket) {
		log.debug("modoOffline() - Iniciando modo OFFLINE de Vertex");
			// Primero inicializamos la cabecera de vertex
			ByLCabeceraTicket cabecera = (ByLCabeceraTicket) ticket.getCabecera();
			if (cabecera.getCabeceraVertex() == null) {
				cabecera.inicializarCabeceraVertex();
			}
			String taxAreaID = variablesServices.getVariableAsString(ByLVariablesServices.US_TAX_AREA_ID);
			cabecera.getCabeceraVertex().setTaxAreaId(taxAreaID);
			cabecera.getCabeceraVertex().setDocumentType(ticket.getCabecera().getCodTipoDocumento());
			cabecera.setEsVertexOnline(false);
			rellenarLineasVertexOffline(ticket);
			rellenarCabeceraVertexOffline(ticket);
	}

	private void rellenarCabeceraVertexOffline(ITicket ticket) {
		log.debug("rellenarCabeceraVertexOffline() - Rellenando la cabecera de vertex");
		
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountWithTax = BigDecimal.ZERO;
		
		List<LineaTicket> lineas = ticket.getLineas();
		for (LineaTicket lineaTicket : lineas) {
			amount = amount.add(new BigDecimal(((ByLLineaTicketProfesional)lineaTicket).getLineaVertex().getAmount()));
			amountWithTax = amountWithTax.add(new BigDecimal(((ByLLineaTicketProfesional)lineaTicket).getLineaVertex().getAmountWithTax()));
		}
		
		((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setAmount(FormatUtil.getInstance().formateaImporte(amount));
		((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setAmountWithTax(FormatUtil.getInstance().formateaImporte(amountWithTax));
	}
	
	private void rellenarLineasVertexOffline(ITicket ticket) {
		log.debug("rellenarLineasVertexOffline() - Rellenando líneas de Vertex para todas las líneas del ticket");
		/* Obtenemos los impuestos de base de datos */
		Integer idGrupoImpuestos = ticket.getCabecera().getCliente().getIdGrupoImpuestos();
		Long idTratamientoImpuestos = ticket.getCabecera().getTienda().getIdTratamientoImpuestos();
		Map<String, ByLPorcentajeImpuesto> porcentajesImpuestos = porcentajeImpuestosService.obtenerPorcentajes(idGrupoImpuestos, idTratamientoImpuestos);

		/* Recorremos las lineas y vamos calculando los impuestos */
		List<LineaTicket> lineas = ticket.getLineas();
		for (LineaTicket lineaTicket : lineas) {
			
			ByLPorcentajeImpuesto porcentajeImpuestoBean = porcentajesImpuestos.get(lineaTicket.getCodImpuesto());

			BigDecimal porcentajeImpuesto = porcentajeImpuestoBean.getPorcentaje();
			BigDecimal porcentajeRecargo = porcentajeImpuestoBean.getPorcentajeRecargo();
			BigDecimal porcentajeRecargo2 = porcentajeImpuestoBean.getPorcentajeRecargo2();
			BigDecimal porcentajeRecargo3 = porcentajeImpuestoBean.getPorcentajeRecargo3();
			BigDecimal porcentajeRecargo4 = porcentajeImpuestoBean.getPorcentajeRecargo4();
			BigDecimal porcentajeRecargo5 = porcentajeImpuestoBean.getPorcentajeRecargo5();

			BigDecimal precioSinDto = lineaTicket.getPrecioSinDto();
			BigDecimal cuotaPorcentaje = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeImpuesto));
			BigDecimal cuotaRecargo = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeRecargo));
			BigDecimal cuotaRecargo2 = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeRecargo2));
			BigDecimal cuotaRecargo3 = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeRecargo3));
			BigDecimal cuotaRecargo4 = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeRecargo4));
			BigDecimal cuotaRecargo5 = BigDecimalUtil.redondear(BigDecimalUtil.porcentaje(precioSinDto, porcentajeRecargo5));

			BigDecimal impuestoTotal = cuotaPorcentaje.add(cuotaRecargo).add(cuotaRecargo2).add(cuotaRecargo3).add(cuotaRecargo4).add(cuotaRecargo5);
			BigDecimal precioTotalConTodosLosImpuestos = precioSinDto.add(impuestoTotal);
			lineaTicket.setPrecioTotalSinDto(precioTotalConTodosLosImpuestos);
			
			/* Rellenamos la información de la lineaVertex menos el documentNumber que se genera al final */
			String codTipoDocumento = ticket.getCabecera().getCodTipoDocumento();
			String idLinea = lineaTicket.getIdLinea().toString();
			LineaVertex lineaVertex = new LineaVertex();
			lineaVertex.setAmount(BigDecimalUtil.redondear(precioSinDto, 2).toString());
			lineaVertex.setAmountWithTax(BigDecimalUtil.redondear(precioTotalConTodosLosImpuestos, 2).toString());
			lineaVertex.setDocumentType(codTipoDocumento);
			lineaVertex.setLineNumber(idLinea);
			((ByLLineaTicketProfesional) lineaTicket).setLineaVertex(lineaVertex);

			/* Rellenamos la información de la listaImpuestosVertex */
			List<LineaDetailVertex> listaImpuestosVertex = new ArrayList<LineaDetailVertex>();
			cargarMapJurisdiccion();
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaPorcentaje, porcentajeImpuesto, "1"));
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaRecargo, porcentajeRecargo, "2"));
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaRecargo2, porcentajeRecargo2, "3"));
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaRecargo3, porcentajeRecargo3, "4"));
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaRecargo4, porcentajeRecargo4, "5"));
			listaImpuestosVertex.add(rellenarLineaImpuestosVertex(codTipoDocumento, idLinea, cuotaRecargo5, porcentajeRecargo5, "6"));
			
			((ByLLineaTicketProfesional) lineaTicket).setListaImpuestosVertex(listaImpuestosVertex);
			lineaTicket.recalcularImporteFinal();
		}
	}

	private LineaDetailVertex rellenarLineaImpuestosVertex(String codTipoDocumento, String numLinea, BigDecimal precioPorcentaje, BigDecimal porcentajeImpuesto, String typeId) {
		log.debug("rellenarLineaImpuestosVertex() - Rellenando el impuesto número " + typeId + " para la línea del ticket número " + numLinea);
		/* Rellenamos la información de la lineaDetallesVertex menos el documentNumber que se genera al final */
		LineaDetailVertex lineaDetailVertexPorcentaje = new LineaDetailVertex();
		lineaDetailVertexPorcentaje.setDocumentType(codTipoDocumento);
		lineaDetailVertexPorcentaje.setLineNumber(numLinea);
		lineaDetailVertexPorcentaje.setTaxAmount(BigDecimalUtil.redondear(precioPorcentaje, 2).toString());
		lineaDetailVertexPorcentaje.setTaxPercentage(porcentajeImpuesto.toString());
		lineaDetailVertexPorcentaje.setTaxTypeId(typeId);
		lineaDetailVertexPorcentaje.setTaxLineNo(typeId);
		lineaDetailVertexPorcentaje.setJurisdictionName(jurisdictionNameMap.get(typeId));
		lineaDetailVertexPorcentaje.setTaxTypeName(jurisdictionNameMap.get(typeId));
		return lineaDetailVertexPorcentaje;
	}
	
	public void setDocumentNumber(ITicket ticket) {
		log.debug("setDocumentNumber() - Añadiendo el documentNumber a la cabeceraVertex y a las lineasVertex");
		String documentNumber = ticket.getCabecera().getCodTicket();
		String documentType = ticket.getCabecera().getCodTipoDocumento();
		
		((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setDocumentNumber(documentNumber);
		((ByLCabeceraTicket) ticket.getCabecera()).getCabeceraVertex().setDocumentType(documentType);
		List<LineaTicket> lineas = ticket.getLineas();
		for (LineaTicket linea : lineas) {
			((ByLLineaTicketProfesional) linea).getLineaVertex().setDocumentNumber(documentNumber);
			((ByLLineaTicketProfesional) linea).getLineaVertex().setDocumentType(documentType);
			List<LineaDetailVertex> lineasDetailVertex = ((ByLLineaTicketProfesional) linea).getListaImpuestosVertex();
			for (LineaDetailVertex lineaDetailVertex : lineasDetailVertex) {
				lineaDetailVertex.setDocumentNumber(documentNumber);
				lineaDetailVertex.setDocumentType(documentType);
			}
		}
	}
	
	public boolean integracionImpuestosVertexActiva(Long tipoDocumento) {
		log.debug("integracionImpuestosActiva() - Comprobando si la integración de impuestos de Vertex está activa");
		TipoDocumentoBean tipoDocumentoBean = null;
		try {
			tipoDocumentoBean = documentos.getDocumento(tipoDocumento);
		}
		catch (DocumentoException e) {
		}

		if (tipoDocumentoBean != null) {
			PropiedadDocumentoBean propiedad = tipoDocumentoBean.getPropiedades().get(INTEGRACION_IMPUESTOS);
			boolean integracionImpuestosActivo = propiedad != null && propiedad.getValor().equals("S");
			if (integracionImpuestosActivo) {
				log.debug("integracionImpuestosActiva() - Integración de impuestos activa");
			}
			return integracionImpuestosActivo;
		}
		else {
			return false;
		}
	}

	private void cargarMapJurisdiccion() {
		log.debug("cargarMapJurisdiccion() - Cargando información sobre los nombres de los impuestos en el map");
		jurisdictionNameMap.put("1", "STATE");
		jurisdictionNameMap.put("2", "COUNTY");
		jurisdictionNameMap.put("3", "CITY");
		jurisdictionNameMap.put("4", "DISTRICT");
		jurisdictionNameMap.put("5", "COUNTRY");
		jurisdictionNameMap.put("6", "PROVINCE");
	}
	
}
