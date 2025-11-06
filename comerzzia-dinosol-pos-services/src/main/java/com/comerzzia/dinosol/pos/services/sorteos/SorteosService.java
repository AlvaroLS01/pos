package com.comerzzia.dinosol.pos.services.sorteos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.comerzzia.api.v2.sorteos.client.LotteryApiApi;
import com.comerzzia.api.v2.sorteos.client.model.ParticipacionOfflineDto;
import com.comerzzia.api.v2.sorteos.client.model.RaffleTicketResponse;
import com.comerzzia.api.v2.sorteos.client.model.RaffleTicketsRequest;
import com.comerzzia.api.v2.sorteos.client.model.SorteoDto;
import com.comerzzia.core.servicios.api.ComerzziaApiManager;
import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.dinosol.pos.services.core.sesion.DinoSesionAplicacion;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.dinosol.pos.services.ticket.DinoTicketVentaAbono;
import com.comerzzia.dinosol.pos.services.ticket.cupones.DinoCuponEmitidoTicket;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionDTO;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.ParticipacionesDTO;
import com.comerzzia.dinosol.pos.services.ticket.sorteos.conversion.RaffleTicketResponseAParticipacionDTOConversor;
import com.comerzzia.pos.persistence.fidelizacion.FidelizacionBean;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.cupones.CuponesServiceException;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.cabecera.CabeceraTicket;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.format.FormatUtil;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class SorteosService {

	private Logger log = Logger.getLogger(SorteosService.class);

	@Autowired
	private ComerzziaApiManager apiManager;

	@Autowired
	private Sesion sesion;
	
	@Autowired
	private DinoCuponesService cuponesService;

	private LotteryApiApi lotteryApi;

	@Autowired
	private RaffleTicketResponseAParticipacionDTOConversor conversor;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void solicitarParticipacionesSorteo(Ticket ticket, boolean comunicarError) throws Exception {
		List<PromocionTicket> listaPromociones = ticket.getPromociones();

		for (PromocionTicket promo : listaPromociones) {
			Long idTipoPromocion = promo.getIdTipoPromocion();
			
			if (idTipoPromocion.equals(1001L)) {
				Long idSorteo = getIdSorteo(promo);
				
				if(idSorteo != null) {
					List<RaffleTicketResponse> listaRascasConcedidos = realizarPeticionApi(ticket, promo, idSorteo, comunicarError);
					incluirParticipacionesEnTicket(ticket, listaRascasConcedidos, promo);
				}
			}
		}
	}

	protected Long getIdSorteo(PromocionTicket promo) {
		// La extensión puede venir como un Long si estamos en la venta o como un String si el ticket ha sido
		// recuperado a través del XML
		Object extensionSorteo = promo.getAdicionales().get("id_sorteo");
		Long idSorteo = null;
		if (extensionSorteo instanceof Long) {
			idSorteo = (Long) extensionSorteo;
		}
		else if (extensionSorteo instanceof String) {
			idSorteo = new Long(extensionSorteo.toString());
		}
		return idSorteo;
	}

	@SuppressWarnings("rawtypes")
	private void incluirParticipacionesEnTicket(Ticket ticket, List<RaffleTicketResponse> listaRascasConcedidos, PromocionTicket promo) throws CuponesServiceException {
		int puntosCabecera = Integer.parseInt(promo.getAdicionales().get("puntos_cabecera").toString());
		int puntosAdicionales = Integer.parseInt(promo.getAdicionales().get("puntos_lineas").toString());
//		int participacionesConcedidos = puntosCabecera + puntosAdicionales;
		int participacionesConcedidos = puntosCabecera;
		
		ParticipacionesDTO participacionesDTO = new ParticipacionesDTO();
		
		participacionesDTO.setNombreSorteo(promo.getTextoPromocion());
		participacionesDTO.setParticipacionesObtenidas(participacionesConcedidos);
		participacionesDTO.setParticipacionesExtras(puntosAdicionales);
		
		if (listaRascasConcedidos == null) {
			((DinoTicketVentaAbono) ticket).setParticipaciones(participacionesDTO);
			
			Long idSorteo = getIdSorteo(promo);
			List<SorteoDto> listaSorteos = ((DinoSesionAplicacion) sesion.getAplicacion()).getListaSorteos();
			RaffleTicketResponse response = new RaffleTicketResponse();
			
			String xml = null;
			if (listaSorteos != null && !listaSorteos.isEmpty()) {
				for (SorteoDto sorteo : listaSorteos) {
					if (sorteo.getIdSorteo().equals(idSorteo)) {
						xml = sorteo.getConfiguracion();
					}
				}
			} else {
				/* Intentamos obtener los textos de incidencia tecnica desde los ficheros */
				xml = leerTextoIncidenciaTecnica(idSorteo);
			}
			
			try {
				if (StringUtils.isBlank(xml)) {
					rellenarTextoFijo(response);
				}
				else {
					obtenerIncidenciaTecnica(xml, response);
				}

				aplicarConfigSorteo(participacionesDTO, response);
			}
			catch (Exception e) {
			}
			
			return;
		}
		else if (listaRascasConcedidos.isEmpty()) {
			return;
		}

		int participacionesPremiadas = 0;

		log.debug("registrarTicket() - Guardando participaciones y cupones premiados para usarlas despues en la impresión del ticket...");

		// Repasamos todas las participaciones para sacar los premios, las participaciones obtenidas y los cupones
		for (RaffleTicketResponse part : listaRascasConcedidos) {
			if (part.getAwarded()) {
				if(StringUtils.isNotBlank(part.getCouponCode())) {
					DinoCuponEmitidoTicket cuponEmitido = crearCuponEmitido(part);
					((DinoTicketVentaAbono) ticket).getCuponesEmitidos().add(cuponEmitido);
				}
				
				participacionesPremiadas++;
			}

			ParticipacionDTO participacion = new ParticipacionDTO();
			participacion = conversor.convertir(part);
			participacionesDTO.getListaParticipaciones().add(participacion);
			
		}
		participacionesDTO.setParticipacionesPremiadas(participacionesPremiadas);
		((DinoTicketVentaAbono) ticket).setParticipaciones(participacionesDTO);

		for (RaffleTicketResponse part : listaRascasConcedidos) {
			aplicarConfigSorteo(participacionesDTO, part);
		}
		
	}

	private void aplicarConfigSorteo(ParticipacionesDTO participacion, RaffleTicketResponse response) {
		log.debug("aplicarConfigSorteo() - Aplicando configuracion sorteo a la participacion");
		if (response != null) {
			int participacionesObtenidas = participacion.getParticipacionesObtenidas();
			int participacionesExtras = participacion.getParticipacionesExtras();
			int participacionesPremiadas = participacion.getParticipacionesPremiadas();

			List<String> codigos = new ArrayList<>();
			for (ParticipacionDTO part : participacion.getListaParticipaciones()) {
				codigos.add(part.getCodParticipacion());
			}
			
			addTextosVisor(participacion, response, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);

			addTextosTicket(participacion, response, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);

			participacion.setEnlaceQr(StringUtils.isNotBlank(response.getEnlaceQr()) ? response.getEnlaceQr() : "");
		}
	}

	private void addTextosVisor(ParticipacionesDTO participacion, RaffleTicketResponse response, int participacionesObtenidas, int participacionesExtras, int participacionesPremiadas,
	        List<String> codigos) {
		String textoVisorConPremioSinApp = response.getTextoVisorConPremioSinApp();
		textoVisorConPremioSinApp = reemplazarParametros(textoVisorConPremioSinApp, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoVisorConPremioSinApp(textoVisorConPremioSinApp);
		
		String textoVisorSinPremioSinApp = response.getTextoVisorSinPremioSinApp();
		textoVisorSinPremioSinApp = reemplazarParametros(textoVisorSinPremioSinApp, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoVisorSinPremioSinApp(textoVisorSinPremioSinApp);
		
		String textoVisorIncidenciaTecnica = response.getTextoVisorIncidenciaTecnica();
		textoVisorIncidenciaTecnica = reemplazarParametros(textoVisorIncidenciaTecnica, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoVisorIncidenciaTecnica(textoVisorIncidenciaTecnica);
		
		String textoVisorIdentificadoTicketDigital = response.getTextoVisorIdentificadoTicketDigital();
		textoVisorIdentificadoTicketDigital = reemplazarParametros(textoVisorIdentificadoTicketDigital, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoVisorIdentificadoTicketDigital(textoVisorIdentificadoTicketDigital);
		
		String textoVisorIdentificadoTicketFisico = response.getTextoVisorIdentificadoTicketFisico();
		textoVisorIdentificadoTicketFisico = reemplazarParametros(textoVisorIdentificadoTicketFisico, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoVisorIdentificadoTicketFisico(textoVisorIdentificadoTicketFisico);
	}

	private void addTextosTicket(ParticipacionesDTO participacion, RaffleTicketResponse response, int participacionesObtenidas, int participacionesExtras, int participacionesPremiadas,
	        List<String> codigos) {
		String textoTicketSinPremioSinApp = response.getTextoTicketSinPremioSinApp();
		textoTicketSinPremioSinApp = reemplazarParametros(textoTicketSinPremioSinApp, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoTicketSinPremioSinApp(textoTicketSinPremioSinApp);

		String textoTicketConPremioSinApp = response.getTextoTicketConPremioSinApp();
		textoTicketConPremioSinApp = reemplazarParametros(textoTicketConPremioSinApp, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoTicketConPremioSinApp(textoTicketConPremioSinApp);

		String textoTicketIdentificadoTicketFisico = response.getTextoTicketIdentificadoTicketFisico();
		textoTicketIdentificadoTicketFisico = reemplazarParametros(textoTicketIdentificadoTicketFisico, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoTicketIdentificadoTicketFisico(textoTicketIdentificadoTicketFisico);

		String textoTicketIncidenciaTecnica = response.getTextoTicketIncidenciaTecnica();
		textoTicketIncidenciaTecnica = reemplazarParametros(textoTicketIncidenciaTecnica, participacionesObtenidas, participacionesExtras, participacionesPremiadas, codigos);
		participacion.setTextoTicketIncidenciaTecnica(textoTicketIncidenciaTecnica);
	}

	private String reemplazarParametros(String texto, int participacionesObtenidas, int participacionesExtras, int participacionesPremiadas, List<String> codigos) {
	    if (StringUtils.isNotBlank(texto)) {
	        String codParticipaciones = String.join("|", codigos);

	        texto = texto.replace("#NP#", String.valueOf(participacionesObtenidas))
	                     .replace("#NPP#", String.valueOf(participacionesPremiadas))
	                     .replace("#NPE#", String.valueOf(participacionesExtras))
	                     .replace("#CP#", codParticipaciones);

	        // Si pones algo entre #TPPE# y el #NPE es igual a 0, lo que va dentro no aparece en el ticket
	        Pattern pattern = Pattern.compile(
	        		"#TPPE#(.*?)#TPPE#");
	        Matcher matcher = pattern.matcher(texto);
	        StringBuffer sb = new StringBuffer();
	        
	        while (matcher.find()) {
	            String bloqueTexto = matcher.group(1); // Texto entre los marcadores #TPPE#
	            if (participacionesExtras > 0) {
	                // Si hay participaciones extras sale el texto
	                matcher.appendReplacement(sb, bloqueTexto);
	            } else {
	                // Si no hay, sale en blanco
	                matcher.appendReplacement(sb, "");
	            }
	        }
	        matcher.appendTail(sb);
	        texto = sb.toString();
	    }
	    return texto;
	}

	
	protected DinoCuponEmitidoTicket crearCuponEmitido(RaffleTicketResponse part) throws CuponesServiceException {
		DinoCuponEmitidoTicket cuponEmitido = new DinoCuponEmitidoTicket();
		cuponEmitido.setTituloCupon(part.getCouponName());
		
		String descripcionCupon = part.getCouponDescription();
		if(StringUtils.isNotBlank(descripcionCupon)) {
			descripcionCupon = descripcionCupon.replaceAll("#IMPORTE#", FormatUtil.getInstance().formateaImporte(part.getCouponBalance()));
			cuponEmitido.setDescripcionCupon(descripcionCupon);
		}
		
		cuponEmitido.setCodigoCupon(part.getCouponCode());
		
		Long idPromocionCzz = cuponesService.getIdPromocionCzzDesdeIdSap(part.getCouponPromotionId());
		cuponEmitido.setIdPromocionAplicacion(idPromocionCzz);
		
		cuponEmitido.setIdPromocionOrigen(cuponEmitido.getIdPromocionAplicacion());
		cuponEmitido.setImporteCupon(part.getCouponBalance());
		
		return cuponEmitido;
	}

	// Devuelve null si hay algún problema.
	@SuppressWarnings("rawtypes")
	private List<RaffleTicketResponse> realizarPeticionApi(Ticket ticket, PromocionTicket rascaPromocion, Long idSorteo, boolean comunicarError) throws Exception {

		List<RaffleTicketResponse> listaParticipaciones = null;

		try {
			LotteryApiApi api = getLotteryApi();

			RaffleTicketsRequest request = crearPeticionParticipaciones(ticket, rascaPromocion, idSorteo);
			log.debug("realizarPeticionApi() - Haciendo llamada a la APIV2 de SORTEOS.");

			listaParticipaciones = api.requestRaffleTickets(request);

			log.debug("realizarPeticionApi() - Resultado: " + listaParticipaciones);
		}
		catch (Exception e) {
			log.error("realizarPeticionApi() - Ha habido un error al solicitar las participaciones: " + e.getMessage(), e);
			if(comunicarError) {
				throw e;
			}
		}

		return listaParticipaciones;
	}

	@SuppressWarnings("rawtypes")
	private RaffleTicketsRequest crearPeticionParticipaciones(Ticket ticket, PromocionTicket rascaPromocion, Long idSorteo) {
		RaffleTicketsRequest request = new RaffleTicketsRequest();
		request.setLotteryId(idSorteo);
		request.setSalesStoreCode(ticket.getTienda().getCodAlmacen());
		request.setTillCode(ticket.getCodCaja());
		request.setSalesTicketCode(ticket.getCabecera().getCodTicket());
		request.setSalesDate(new Date());
		request.setAmountSale(ticket.getTotales().getTotalAPagar());

		// Cogemos las participacionas concedidas
		int puntosCabecera = Integer.parseInt(rascaPromocion.getAdicionales().get("puntos_cabecera").toString());
		int puntosAdicionales = Integer.parseInt(rascaPromocion.getAdicionales().get("puntos_lineas").toString());
		int rascasConcedidos = puntosCabecera + puntosAdicionales;
		request.setNumberOfRaffleTickets(rascasConcedidos);

		// Opcional, solo si hay fidelizado
		if (ticket.getCabecera().getDatosFidelizado() != null) {
			request.setSalesClient(ticket.getCabecera().getDatosFidelizado().getNumTarjetaFidelizado());
		}

		log.debug("crearPeticionParticipaciones() - Request: " + request);

		return request;
	}

	private LotteryApiApi getLotteryApi() throws EmpresaException, Exception {
		if (lotteryApi != null) {
			return lotteryApi;
		}

		DatosSesionBean datosSesion = new DatosSesionBean();
		datosSesion.setUidActividad(sesion.getAplicacion().getUidActividad());
		datosSesion.setUidInstancia(sesion.getAplicacion().getUidInstancia());
		datosSesion.setLocale(new Locale(AppConfig.idioma, AppConfig.pais));
		lotteryApi = apiManager.getClient(datosSesion, "LotteryApiApi");
		return lotteryApi;
	}
	
	@SuppressWarnings("rawtypes")
	public void solicitarParticipacionesSorteoOffline(Ticket ticket, boolean comunicarError) throws Exception {
		FidelizacionBean fidelizacion = null;
		((CabeceraTicket) ticket.getCabecera()).setDatosFidelizado(fidelizacion);
		solicitarParticipacionesSorteo(ticket, comunicarError);
		lotteryApi.markOfflineTicket(ticket.getCabecera().getLocalizador());
	}
	
	public ParticipacionOfflineDto getOfflineTicketRaffles(String locatorId) {
		return lotteryApi.getOfflineTicketRaffles(locatorId);
	}

	
	public List<SorteoDto> getRaffles() {
		List<SorteoDto> listaSorteos = null;

		try {
			LotteryApiApi api = getLotteryApi();
			
			log.debug("getRaffles() - Haciendo llamada a la APIV2 de SORTEOS.");
			listaSorteos = api.getListRaffles();

			log.debug("getRaffles() - Resultado: " + listaSorteos);
		}
		catch (Exception e) {
			log.error("getRaffles() - Ha habido un error al solicitar las participaciones: " + e.getMessage(), e);
		}

		return listaSorteos;
	}
	
	private void obtenerIncidenciaTecnica(String configuracion, RaffleTicketResponse response) throws ParserConfigurationException, SAXException, IOException {
		String textoVisorIncidenciaTecnica = null;
		String textoIncidenciaTecnica = null;
		String enlaceQr = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Convertir la cadena XML en un InputStream
        InputStream is = new ByteArrayInputStream(configuracion.getBytes());

        // Parsear el XML
        Document document = builder.parse(is);

        // Buscar el elemento con el id "incidenciaTecnica"
        NodeList nodeList = document.getElementsByTagName("textoVisorImagenSorteo");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            if (element.getAttribute("id").equals("textoVisorIncidenciaTecnica")) {
                // Obtener y mostrar el texto del elemento
            	textoVisorIncidenciaTecnica = element.getTextContent();
            }
        }
        
        nodeList = document.getElementsByTagName("textoTicketParticipacion");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            
            if (element.getAttribute("id").equals("textoTicketIncidenciaTecnica")) {
                // Obtener y mostrar el texto del elemento
            	textoIncidenciaTecnica = element.getTextContent();
            }
        }
        
        nodeList = document.getElementsByTagName("enlaceQR");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            
            if (element.getAttribute("id").equals("enlaceQr")) {
                // Obtener y mostrar el texto del elemento
            	enlaceQr = element.getTextContent();
            }
        }
        
        response.setTextoVisorIncidenciaTecnica(textoVisorIncidenciaTecnica);
        response.setTextoTicketIncidenciaTecnica(textoIncidenciaTecnica);
        response.setTextoVisorConPremioSinApp("");
        response.setTextoVisorSinPremioSinApp("");
        response.setTextoVisorIdentificadoTicketFisico("");
        response.setTextoVisorIdentificadoTicketDigital("");
        response.setEnlaceQr(enlaceQr);
	}
	
	private String leerTextoIncidenciaTecnica(Long idSorteo) {
		String filePath = "entities/raffle_" + idSorteo + "_configuration.xml";
		try {
			log.debug("readRestrictionFile() - Leyendo el fichero " + filePath);

			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource(filePath);

			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			String xml = "";
			String line = "";
			while ((line = in.readLine()) != null) {
				xml = xml + System.lineSeparator() + line;
			}
			in.close();

			return xml;
		}
		catch (Exception e) {
			log.error("leerTextoIncidenciaTecnica() - No se ha podido leer el fichero " + filePath + ": " + e.getMessage(), e);
			return null;
		}
	}

	private void rellenarTextoFijo(RaffleTicketResponse response) {
        response.setTextoVisorIncidenciaTecnica(I18N.getTexto("Por problemas técnicos, no hemos podido imprimir tus códigos.|Guarda este ticket y pasa en otro momento a solicitarlos.|Disculpa las molestias."));
        response.setTextoTicketIncidenciaTecnica(I18N.getTexto("Puedes Escanear el QR para descubrir cómo y consultar las bases:"));
        response.setTextoVisorConPremioSinApp("");
        response.setTextoVisorSinPremioSinApp("");
        response.setTextoVisorIdentificadoTicketFisico("");
        response.setTextoVisorIdentificadoTicketDigital("");
        response.setEnlaceQr("https://www.hiperdino.es/");
	}
}
