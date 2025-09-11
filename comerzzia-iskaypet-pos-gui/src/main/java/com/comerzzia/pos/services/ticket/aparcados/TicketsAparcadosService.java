/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND
 * INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST,
 * TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE
 * AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.services.ticket.aparcados;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoBean;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoExample;
import com.comerzzia.pos.persistence.tickets.aparcados.TicketAparcadoMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.copiaSeguridad.CopiaSeguridadTicketService;
import com.comerzzia.pos.util.xml.MarshallUtil;
import com.comerzzia.pos.util.xml.MarshallUtilException;

@Service
@SuppressWarnings("deprecation")
public class TicketsAparcadosService {

	protected static final Logger log = Logger.getLogger(TicketsAparcadosService.class);

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected TicketAparcadoMapper ticketAparcadoMapper;

    public List<TicketAparcadoBean> consultarTickets(String uidTicket, Date fecha, String usuario, Long idTipoDoc) throws TicketsServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			log.debug("consultarTickets() - Consultando ticket aparcado en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen());
			if (uidTicket != null) {
				criteria.andUidTicketEqualTo(uidTicket);
			}
			if (fecha != null) {
				criteria.andFechaEqualTo(fecha);
			}
			if (usuario != null) {
				criteria.andUsuarioEqualTo(usuario);
			}
			if (idTipoDoc != null) {
				criteria.andIdTipoDocumentoEqualTo(idTipoDoc);
			}
			criteria.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);
			return ticketAparcadoMapper.selectFromViewByExample(example);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando tickets aparcados en base de datos con parámetros indicados: Fecha: " + fecha + " UidTicket: " + uidTicket + " Usuario: " + usuario
			        + " - " + e.getMessage();
			log.error("consultarTickets() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	public int countTicketsAparcados(Long idTipoDocumento) {
		SqlSession sqlSession = new SqlSession();
		try {
			log.debug("countTicketsAparcados() - Consultando ticket aparcado en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidActividadEqualTo(sesion.getAplicacion().getUidActividad()).andCodAlmacenEqualTo(sesion.getAplicacion().getCodAlmacen())
			        .andCodCajaEqualTo(sesion.getAplicacion().getCodCaja());
			if (idTipoDocumento != null) {
				criteria.andIdTipoDocumentoEqualTo(idTipoDocumento);
			}
			criteria.andUsuarioNotEqualTo(CopiaSeguridadTicketService.USUARIO_BACKUP_TICKET);
			return ticketAparcadoMapper.countByExample(example);
		}
		finally {
			sqlSession.close();
		}
	}

	public TicketAparcadoBean consultarTicket(String uidTicket) throws TicketsServiceException {
		SqlSession sqlSession = new SqlSession();
		TicketAparcadoBean res = null;
		try {
			log.debug("consultarTickets() - Consultando ticket en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidTicketEqualTo(uidTicket);
			List<TicketAparcadoBean> resultados = ticketAparcadoMapper.selectByExampleWithBLOBs(example);
			if (resultados != null && !resultados.isEmpty()) {
				res = resultados.get(0);
			}
			return res;

		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando ticket aparcado en base de datos con parámetros indicados: uidTicket: " + uidTicket;
			log.error("consultarTickets() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	@SuppressWarnings("rawtypes")
    public void aparcarTicket(TicketVenta ticket) throws TicketsServiceException {
		log.debug("aparcarTicket()");
		TicketAparcadoBean ta = new TicketAparcadoBean();
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());

			// Seteamos los valores para el nuevo bean
			// Obtenemos el xml del ticket

			byte[] xmlTicket = MarshallUtil.crearXML(ticket);
			log.debug("TICKET: " + ticket.getUidTicket() + "\n" + new String(xmlTicket, "UTF-8") + "\n");

			ta.setTicket(xmlTicket);
			// Atributos del ticket
			ta.setCodAlmacen(ticket.getTienda().getCodAlmacen());
			ta.setFecha(new Date());
			ta.setUidTicket(ticket.getUidTicket());
			ta.setUidActividad(sesion.getAplicacion().getUidActividad());

			// Atributos particulares de ticket aparcado
			ta.setUsuario(ticket.getCajero().getUsuario());
			ta.setCodCaja(ticket.getCodCaja());
			if (ticket.getCliente() != null) {
				ta.setCodCliente(ticket.getCliente().getCodCliente());
			}
			ta.setNumArticulos(ticket.getLineas().size());
			ta.setImporte(ticket.getTotales().getTotal());
			ta.setIdTipoDocumento(ticket.getCabecera().getTipoDocumento());

			// Guardamos el Bean
			log.debug("insertarTicket() - Salvando ticket en base de datos...");
			ticketAparcadoMapper.insert(ta);

			log.debug("aparcarTicket() - Ticket aparcado correctamente.");
		}
		catch (MarshallUtilException ex) {
			log.error("aparcarTicket() - Error realizando marshall del ticket en pantalla");
			throw new TicketsServiceException();
		}
		catch (Exception e) {
			sqlSession.rollback();
			String msg = "Se ha producido un error aparcando ticket con uid " + ticket.getUidTicket() + " : " + e.getMessage();
			log.error("aparcarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}

	public void eliminarTicket(String uidTicket) throws TicketsServiceException {
		log.debug("eliminarTicket()");
		SqlSession sqlSession = new SqlSession();

		try {
			sqlSession.openSession(SessionFactory.openSession());

			eliminarTicket(sqlSession, uidTicket);
		}
		finally {
			sqlSession.close();
		}
	}

	public void eliminarTicket(SqlSession sqlSession, String uidTicket) throws TicketsServiceException {

		try {
			log.debug("eliminarTicket() - Eliminando ticket en base de datos...");

			TicketAparcadoExample example = new TicketAparcadoExample();
			TicketAparcadoExample.Criteria criteria = example.createCriteria();
			criteria.andUidTicketEqualTo(uidTicket);
			example.or(criteria);
			ticketAparcadoMapper.deleteByExample(example);

			log.debug("eliminarTicket() - Ticket eliminado correctamente.");
		}
		catch (Exception e) {
			String msg = "Se ha producido un error eliminando ticket con uid " + uidTicket + " : " + e.getMessage();
			log.error("eliminarTicket() - " + msg, e);
			throw new TicketsServiceException(e);
		}

	}

}
