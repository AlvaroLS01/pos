package com.comerzzia.dinosol.pos.services.promociones.tipos.especificos.rascas;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.config.ComerzziaApp;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.core.util.xml.XMLDocument;
import com.comerzzia.core.util.xml.XMLDocumentNode;
import com.comerzzia.dinosol.pos.services.cupones.DinoCuponesService;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.ITicket;
import com.comerzzia.pos.services.ticket.TicketVentaAbono;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.promociones.PromocionTicket;
import com.comerzzia.pos.util.xml.MarshallUtil;

@SuppressWarnings("deprecation")
@Component
public class RascasService {

	private Logger log = Logger.getLogger(RascasService.class);

	private static final Long ID_TIPO_DOCUMENTO_RASCAS = 1000050L;
	private static final String ID_CONTADOR_DOCUMENTO_RASCAS = "ID_DOC_RASCAS";

	private List<String> codigosRascaValidos;
	
	private Long idPromocionControlada;

	@Autowired
	private Sesion sesion;

	@Autowired
	private ServicioContadores servicioContadores;

	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private DinoCuponesService cuponesService;

	public boolean isRascaValido(String codigo) {
		log.debug("isRascaValido() - Consultando si es válido el código de rasca: " + codigo);

		if (codigosRascaValidos == null) {
			leerXmlConfiguracion();
		}

		return codigosRascaValidos.contains(codigo);
	}

	private void leerXmlConfiguracion() {
		ComerzziaApp comerzziaApp = ComerzziaApp.get();
		URL url = comerzziaApp.obtenerUrlFicheroConfiguracion("codigos_rascas.xml");
		File file = new File(url.getPath());
		log.debug("leerXmlConfiguracion() - Iniciamos la lectura del archivo codigos_rascas.xml en la ruta : " + url.toString());

		codigosRascaValidos = new ArrayList<String>();
		try {
			XMLDocument xml = new XMLDocument(file);
			
			Long idPromocionControladaSap = xml.getRoot().getNodo("id_promocion_sap").getValueAsLong();
			idPromocionControlada = cuponesService.getIdPromocionCzzDesdeIdSap(idPromocionControladaSap);

			for (XMLDocumentNode nodoXml : xml.getRoot().getNodo("codigos").getHijos()) {
				String codigoRascaValido = nodoXml.getValue();
				codigosRascaValidos.add(codigoRascaValido);
			}
		}
		catch (Exception e) {
			log.error("leerXmlConfiguracion() - Ha habido un error al cargar el XML de configuración: " + e.getMessage(), e);
		}
	}

	public void guardarDocumentoEntregaRascas(TicketVentaAbono ticketVenta, List<String> rascasIntroducidos, int numeroRascasConcedidos, int ultimaLinea) {
		if(rascasIntroducidos.isEmpty() && ultimaLinea > -1) {
			return;
		}
		
		EntregaRascasDto dto = crearDto(ticketVenta, rascasIntroducidos, numeroRascasConcedidos, ultimaLinea);

		guardarDocumento(dto);
	}

	protected void guardarDocumento(EntregaRascasDto dto) {
		SqlSession sqlSession = new SqlSession();
		try {
			TicketBean ticket = crearTicket();

			byte[] xml = MarshallUtil.crearXML(dto);
			log.debug("guardarDocumentoEntregaRascas() - XML de entrega de rascas: " + new String(xml));
			ticket.setTicket(xml);

			sqlSession.openSession(SessionFactory.openSession());
			ticketsService.insertarTicket(sqlSession, ticket, false);
			sqlSession.commit();
		}
		catch (Exception e) {
			log.error("guardarDocumentoEntregaRascas() - Ha habido un error al guardar la auditoria: " + e.getMessage(), e);
			sqlSession.rollback();
		}
		finally {
			sqlSession.close();
		}
	}

	protected TicketBean crearTicket() throws ContadorServiceException {
		TicketBean ticket = new TicketBean();

		String uidActividad = sesion.getAplicacion().getUidActividad();
		ticket.setUidActividad(uidActividad);

		String uid = UUID.randomUUID().toString();
		ticket.setUidTicket(uid);
		ticket.setLocatorId(uid);

		String codalm = sesion.getAplicacion().getCodAlmacen();
		ticket.setCodAlmacen(codalm);

		String codcaja = sesion.getAplicacion().getCodCaja();
		ticket.setCodcaja(codcaja);

		ticket.setIdTipoDocumento(ID_TIPO_DOCUMENTO_RASCAS);

		ticket.setCodTicket("*");
		ticket.setFirma("*");
		ticket.setSerieTicket("*");

		Date fecha = new Date();
		ticket.setFecha(fecha);

		ticket.setIdTicket(servicioContadores.obtenerValorContador(ID_CONTADOR_DOCUMENTO_RASCAS, uidActividad));
		return ticket;
	}

	protected EntregaRascasDto crearDto(TicketVentaAbono ticketVenta, List<String> rascasIntroducidos, int numeroRascasConcedidos, int ultimaLinea) {
		EntregaRascasDto dto = new EntregaRascasDto();

		if(ultimaLinea >= 0) {
			dto.setLinea(ultimaLinea + 1);
		}
		else {
			dto.setLinea(0);
		}
		
		dto.setUidTicket(ticketVenta.getUidTicket());
		dto.setCodTicket(ticketVenta.getCabecera().getCodTicket());
		dto.setCodAlm(sesion.getAplicacion().getCodAlmacen());
		dto.setCodCaja(sesion.getAplicacion().getCodCaja());
		dto.setIdCajero(sesion.getSesionUsuario().getUsuario().getIdUsuario());
		dto.setRascasConcedidos(numeroRascasConcedidos);
		dto.setImporteTotal(ticketVenta.getTotales().getTotal());
		dto.setSobreExtras(getRascasConcedidos(ticketVenta));
		dto.setRascasEntregados(rascasIntroducidos);
		return dto;
	}

	public Long getIdPromocionControlada() {
		if (idPromocionControlada == null) {
			leerXmlConfiguracion();
		}
		
		return idPromocionControlada;
	}
	
	//Este metodo es para los sobres Extras exclusivamente
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getRascasConcedidos(ITicket ticket) {
		int rascasConcedidos = 0;
		for(PromocionTicket promocion : (List<PromocionTicket>) ticket.getPromociones()) {
			if(promocion.getIdTipoPromocion().equals(1001L) && promocion.getIdPromocion().equals(getIdPromocionControlada())) {
				if(promocion.getAdicionales() != null) {
					String puntosLineas = (String) promocion.getAdicionales().get("puntos_lineas");
					if(StringUtils.isBlank(puntosLineas)) {
						puntosLineas = "0";
					}
					rascasConcedidos = rascasConcedidos + Integer.valueOf(puntosLineas);
				}
			}
		}
		return rascasConcedidos;
	}

}
