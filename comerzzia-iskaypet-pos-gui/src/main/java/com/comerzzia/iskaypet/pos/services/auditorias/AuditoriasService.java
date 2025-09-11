package com.comerzzia.iskaypet.pos.services.auditorias;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.servicios.empresas.EmpresaException;
import com.comerzzia.core.servicios.tipodocumento.TipoDocumentoNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.services.ticket.lineas.IskaypetLineaTicket;
import com.comerzzia.iskaypet.pos.util.date.DateUtils;
import com.comerzzia.pos.gui.ventas.tickets.TicketManager;
import com.comerzzia.pos.persistence.core.documentos.tipos.TipoDocumentoBean;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.core.contadores.ContadorServiceException;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.documentos.DocumentoException;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.services.ticket.lineas.LineaTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.xml.MarshallUtil;

@SuppressWarnings("deprecation")
@Component	
public class AuditoriasService {

	private Logger log = Logger.getLogger(AuditoriasService.class);
	
	private static final String REP_NUM_AUDITORIA = "REP_NUM_AUDITORIA";
	
	public static final String TIPO_AUDITORIA_DEVOLUCION = "AUDE";
	public static final String TIPO_AUDITORIA_ANULACION_LINEA = "AUAL";
	public static final String TIPO_AUDITORIA_ANULACION_ORDEN_COMPLETA = "AUOC";
	public static final String TIPO_AUDITORIA_CAMBIOPRECIO = "AUCP";
	public static final String TIPO_AUDITORIA_DESCUENTO_GENERAL = "AUDG";
	public static final String TIPO_ENTRADA_CAJA = "ENTRADA_CAJA";
	public static final String TIPO_SALIDA_CAJA = "SALIDA_CAJA";

	public static final String FECHA_PATTERN = "dd-MM-yyyy HH:mm:ss";
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private ServicioContadores servicioContadores;

	@SuppressWarnings("unchecked")
	public AuditoriaDto generarAuditoria(AuditoriaDto auditoria, String tipoDoc, TicketManager ticketManager, Boolean guardar)
			throws ContadorServiceException, EmpresaException, TipoDocumentoNotFoundException, DocumentoException {

		log.debug("guardarAuditoria() - Creando auditoria");

		auditoria.setUidActividad(sesion.getAplicacion().getUidActividad());
		auditoria.setUidAuditoria(UUID.randomUUID().toString());
		auditoria.setCodalm(sesion.getAplicacion().getCodAlmacen());
		auditoria.setCodcaja(sesion.getAplicacion().getCodCaja());
		auditoria.setFecha(DateUtils.formatDate(new Date(), FECHA_PATTERN));

		UsuarioBean usuario = sesion.getSesionUsuario().getUsuario();
		if (usuario != null) {
			auditoria.setUsuario(usuario.getUsuario());
			auditoria.setDescripcion(usuario.getDesusuario());
		}

		TipoDocumentoBean docAuditoria = sesion.getAplicacion().getDocumentos().getDocumento(tipoDoc);
		auditoria.setIdTipoDocumento(docAuditoria.getIdTipoDocumento());
		auditoria.setTipoAuditoria(docAuditoria.getCodtipodocumento());

		// GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
		if (ticketManager != null && tipoDoc.equalsIgnoreCase("AUOC")) {
			AUOCTotales totales = new AUOCTotales();
			auditoria.setTotales(totales);

			// totales
			totales.setBase(ticketManager.getTicket().getTotales().getBase());
			totales.setTotal(ticketManager.getTicket().getTotales().getTotal());

			// Lineas
			List<LineaTicket> listLineasTicket = (List<LineaTicket>) ticketManager.getTicket().getLineas();
			for (LineaTicket linea : listLineasTicket) {
				AUOCLineas lineaAuoc = new AUOCLineas();
				lineaAuoc.setCantidad(linea.getCantidad());
				lineaAuoc.setCodArt(linea.getCodArticulo());
				lineaAuoc.setDesArt(linea.getDesArticulo());
				lineaAuoc.setDesglose1(linea.getDesglose1());
				lineaAuoc.setDesglose2(linea.getDesglose2());
				lineaAuoc.setImporte(BigDecimalUtil.redondear(linea.getImporteConDto()));
				lineaAuoc.setImporteTotal(BigDecimalUtil.redondear4(linea.getImporteTotalConDto()));
				lineaAuoc.setPrecio(linea.getPrecioConDto());
				lineaAuoc.setPrecioTotal(BigDecimalUtil.redondear4(linea.getPrecioTotalConDto()));
				lineaAuoc.setPrecioSinDto(BigDecimalUtil.redondear(linea.getPrecioSinDto()));
				lineaAuoc.setPrecioTotalSinDto(linea.getPrecioTotalSinDto());
				lineaAuoc.setPrecioTotalTarifaOrigen(BigDecimalUtil.redondear4(linea.getPrecioTotalTarifaOrigen()));
				lineaAuoc.setPretioTarifaOrigen(linea.getPrecioTarifaOrigen());
				auditoria.getLstLineas().add(lineaAuoc);
			}
		}

		if (guardar) {
			guardarAuditoria(auditoria);
		}

		return auditoria;
	}

	public AuditoriaDto guardarAuditoria(AuditoriaDto auditoria) {
		log.debug("guardarAuditoria() - Guardando auditoria en BBDD");
		SqlSession sqlSession = new SqlSession();
		try {

			String uidActividad = auditoria.getUidActividad();

			TicketBean ticket = new TicketBean();
			ticket.setIdTicket(servicioContadores.obtenerValorContador(REP_NUM_AUDITORIA, uidActividad));
			ticket.setUidTicket(auditoria.getUidAuditoria());
			ticket.setLocatorId(auditoria.getUidAuditoria());
			ticket.setCodAlmacen(auditoria.getCodalm());
			ticket.setCodcaja(auditoria.getCodcaja());
			ticket.setFecha(DateUtils.parseDate(auditoria.getFecha(), FECHA_PATTERN));
			ticket.setIdTipoDocumento(auditoria.getIdTipoDocumento());

			ticket.setCodTicket("*");
			ticket.setFirma("*");
			ticket.setSerieTicket("*");

			byte[] xml = MarshallUtil.crearXML(auditoria);
			log.debug("guardarAuditoria() - XML de auditoría: " + new String(xml));
			ticket.setTicket(xml);

			sqlSession.openSession(SessionFactory.openSession());
			ticketsService.insertarTicket(sqlSession, ticket, false);
			sqlSession.commit();
		}
		catch (Exception e) {
			log.error("guardarAuditoria() - Ha habido un error al guardar la auditoria: " + e.getMessage(), e);
			sqlSession.rollback();
		}
		finally {
			sqlSession.close();
		}
		return auditoria;
	}
	
    //GAP 113: AMPLIACIÓN DESARROLLO AUDITORÍAS EN POS
	public AuditoriaLineaTicket addAuditoriaLinea(IskaypetLineaTicket iskLinea, String tipoDocAuditoria, String uidAuditoria, String codigo) {
		AuditoriaLineaTicket auditoriaLinea = new AuditoriaLineaTicket();
		boolean exiteAuditoria = false;
		if (iskLinea.getAuditorias() == null) {
			List<AuditoriaLineaTicket> lstAuditoria = new ArrayList<>();
			auditoriaLinea.setTipo(tipoDocAuditoria);
			auditoriaLinea.setUidAuditoria(uidAuditoria);
			auditoriaLinea.setCodigo(codigo);
			lstAuditoria.add(auditoriaLinea);
			iskLinea.setAuditorias(lstAuditoria);

		} else {
			auditoriaLinea.setTipo(tipoDocAuditoria);
			auditoriaLinea.setUidAuditoria(uidAuditoria);
			auditoriaLinea.setCodigo(codigo);
			
			for (AuditoriaLineaTicket auditoriasLineas : iskLinea.getAuditorias()) {
				if (auditoriasLineas.getTipo().equals(tipoDocAuditoria)) {
					auditoriasLineas.setUidAuditoria(uidAuditoria);
					exiteAuditoria = true;
				}
			}
			if (!exiteAuditoria) {
				iskLinea.getAuditorias().add(auditoriaLinea);
			}
		}
		return auditoriaLinea;
	}

}
