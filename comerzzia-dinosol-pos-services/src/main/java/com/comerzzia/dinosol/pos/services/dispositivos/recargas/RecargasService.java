package com.comerzzia.dinosol.pos.services.dispositivos.recargas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.devices.recarga.dto.recarga.DatosRespuestaRecargaDto;
import com.comerzzia.dinosol.pos.persistence.dispositivos.recargas.OperadorDTO;
import com.comerzzia.dinosol.pos.persistence.tickets.TicketAnexoBean;
import com.comerzzia.dinosol.pos.services.core.documentos.tipos.DinosolTipoDocumentoService;
import com.comerzzia.dinosol.pos.services.ticket.anexo.AnexaTicketsServiceException;
import com.comerzzia.dinosol.pos.services.ticket.anexo.TicketsAnexosService;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Component
@SuppressWarnings("deprecation")
public class RecargasService {

	private static Logger log = Logger.getLogger(RecargasService.class);

	public static final String CONTADOR_RECARGA = "ID_RECARGA_MOVIL";
	public static final String COD_TIPO_DOC_RECARGA = "RECMOV";

	private static final String CONTADOR_CONTENIDO_DIGITAL = "ID_CONTENIDO_DIGITAL";
	public static final String COD_TIPO_DOC_ACTIVA_POSACARD = "ACTPOSA";

	private static final String CONTADOR_PIN_PRINTNG = "ID_PIN_PRINTING";
	public static final String COD_TIPO_DOC_SOLICITUD_PIN = "PINPRIN";

	private List<OperadorDTO> operadores;

	@Autowired
	private Sesion sesion;

	@Autowired
	private TicketsService ticketsService;

	@Autowired
	private TicketsAnexosService ticketsAnexosService;

	@Autowired
	private ServicioContadores contadoresService;

	@Autowired
	private DinosolTipoDocumentoService tiposDocumentosService;

	public List<OperadorDTO> consultaOperadoresDisponiblesSegunImporte(BigDecimal importe) {
		List<OperadorDTO> result = new ArrayList<OperadorDTO>();

		if (operadores == null) {
			operadores = CargaOperadoresRecargas.getOperadoresRecargasXml();
		}

		for (OperadorDTO operadorDTO : operadores) {
			if (BigDecimalUtil.isIgual(operadorDTO.getImporte(), importe)) {
				OperadorDTO operador = new OperadorDTO();
				operador.setEan(operadorDTO.getEan());
				operador.setDescripcion(operadorDTO.getDescripcionr());
				operador.setImporte(operadorDTO.getImporte());
				result.add(operador);
			}
		}

		return result;
	}

	public OperadorDTO getOperador(String codigoArticulo, BigDecimal importe, boolean comprobarImporte) {
		log.debug("getOperador() - Buscando operador con código de artículo " + codigoArticulo + " e importe " + importe);

		if (operadores == null) {
			operadores = CargaOperadoresRecargas.getOperadoresRecargasXml();
		}

		for (OperadorDTO operadorDTO : operadores) {
			if (operadorDTO.getCodigoArticulo().equals(codigoArticulo)) {
				if(comprobarImporte) {
					if(BigDecimalUtil.redondear(operadorDTO.getImporte()).equals(BigDecimalUtil.redondear(importe))) {
						return operadorDTO;
					}
				}
				else {
					return operadorDTO;
				}
			}
		}

		log.debug("getOperador() - No se ha encontrado ningún operador con esos datos.");

		return null;
	}
	
	public boolean salvarDocumentoRecargaMovil(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		return salvarDocumentoRecarga(ticketRecarga, uidTicketOrigen, COD_TIPO_DOC_RECARGA, CONTADOR_RECARGA);
	}
	
	public boolean salvarDocumentoRecargaPOSA(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		return salvarDocumentoRecarga(ticketRecarga, uidTicketOrigen, COD_TIPO_DOC_ACTIVA_POSACARD, CONTADOR_CONTENIDO_DIGITAL);
	}
	
	public boolean salvarDocumentoRecargaPinPrinting(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		return salvarDocumentoRecarga(ticketRecarga, uidTicketOrigen, COD_TIPO_DOC_SOLICITUD_PIN, CONTADOR_PIN_PRINTNG);
	}

	public boolean salvarDocumentoCancelacionRecargaMovil(TicketRecarga ticketRecarga, String uidTicketOrigen) {
		return salvarDocumentoRecarga(ticketRecarga, uidTicketOrigen, COD_TIPO_DOC_RECARGA, CONTADOR_RECARGA, false);
	}
	
	public boolean salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicket, String codTipoDocumento, String idContador) {
		return salvarDocumentoRecarga(ticketRecarga, uidTicket, codTipoDocumento, idContador, true);
	}

	public boolean salvarDocumentoRecarga(TicketRecarga ticketRecarga, String uidTicket, String codTipoDocumento, String idContador, boolean guardarTicketAnexo) {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			TicketBean ticket = new TicketBean();
			ticket.setCodAlmacen(sesion.getAplicacion().getCodAlmacen());
			ticket.setCodcaja(sesion.getAplicacion().getCodCaja());
			ticket.setCodTicket("*");
			ticket.setFecha(new Date());
			ticket.setFirma("*");
			String uidActividad = sesion.getAplicacion().getUidActividad();
			ticket.setIdTicket(contadoresService.obtenerValorContador(idContador, uidActividad));
			String codPais = sesion.getAplicacion().getTienda().getCliente().getCodpais();
			ticket.setIdTipoDocumento(tiposDocumentosService.consultar(uidActividad, codPais, codTipoDocumento).getIdTipoDocumento());
			ticket.setSerieTicket("*");
			ticket.setTicket(generarXMLTicketRecarga(ticketRecarga));
			ticket.setUidActividad(uidActividad);
			ticket.setUidTicket(UUID.randomUUID().toString());
			ticket.setLocatorId(ticket.getUidTicket());

			ticketRecarga.setIdTicket(ticket.getIdTicket());
			ticketRecarga.setUidTicket(ticket.getUidTicket());

			if(guardarTicketAnexo) {
				guardarTicketAnexo(sqlSession, uidTicket, ticketRecarga);
			}

			ticketsService.insertarTicket(sqlSession, ticket, false);
			sqlSession.commit();

			return true;
		}
		catch (Exception e) {
			log.error("salvarDocumentoRecarga() - Se ha producido un error al generar y guardar el ticket de recarga: " + e.getMessage(), e);
			sqlSession.rollback();

			return false;
		}
		finally {
			sqlSession.close();
		}
	}

	private void guardarTicketAnexo(SqlSession sqlSession, String uidTicket, TicketRecarga ticketRecarga) throws AnexaTicketsServiceException {
		TicketAnexoBean ticketAnexo = new TicketAnexoBean();
		ticketAnexo.setUidActividad(sesion.getAplicacion().getUidActividad());
		ticketAnexo.setUidTicket(uidTicket);
		ticketAnexo.setTieneRecarga(true);
		ticketAnexo.setOperador(ticketRecarga.getEan());
		ticketAnexo.setTelefono(ticketRecarga.getTelefono());
		ticketAnexo.setCodValidacion(ticketRecarga.getCodReferenciaProveedor());

		ticketsAnexosService.crear(sqlSession, ticketAnexo);
	}

	@SuppressWarnings("rawtypes")
	public TicketRecarga generarTicketRecarga(TicketVenta ticketOrigen, OperadorDTO operador, DatosRespuestaRecargaDto response, String tipoRecarga) {
		TicketRecarga ticketRecarga = new TicketRecarga();
		ticketRecarga.setUidTicketOriginal(ticketOrigen.getUidTicket());
		ticketRecarga.setCodTicketOriginal(ticketOrigen.getCabecera().getCodTicket());
		ticketRecarga.setCodTienda(sesion.getAplicacion().getCodAlmacen());
		ticketRecarga.setCodCaja(sesion.getAplicacion().getCodCaja());
		ticketRecarga.setFecha(new Date());
		ticketRecarga.setCajero(sesion.getSesionCaja().getCajaAbierta().getUsuario());
		ticketRecarga.setEan(operador.getEan());
		ticketRecarga.setTelefono(response.getTelefono());
		ticketRecarga.setMensaje(response.getMensaje());
		ticketRecarga.setImporte(operador.getImporte());
		ticketRecarga.setTipoRecarga(tipoRecarga);
		ticketRecarga.setCodReferenciaProveedor(response.getReferenciaProveedor());
		ticketRecarga.setPin(response.getPin());

		return ticketRecarga;
	}

	private byte[] generarXMLTicketRecarga(TicketRecarga ticketRecarga) throws MarshallUtilException {
		byte[] result = null;
		try {
			result = MarshallUtil.crearXML(ticketRecarga);
		}
		catch (MarshallUtilException e) {
			log.error("generarTicketRecarga() - Se ha producido un error al crear el xml del ticket de recarga: " + e.getMessage(), e);
			throw e;
		}

		return result;
	}

}
