package com.comerzzia.dinosol.pos.services.auditorias;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.usuarios.UsuarioBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tickets.TicketBean;
import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.core.contadores.ServicioContadores;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.TicketsService;
import com.comerzzia.pos.util.xml.MarshallUtil;

@SuppressWarnings("deprecation")
@Component
public class AuditoriasService {
	
	private Logger log = Logger.getLogger(AuditoriasService.class);
	
	public static Long ID_TIPO_DOC_AUDITORIA = 1000030L;
	
	public static String ID_CONTADOR_AUDITORIA = "ID_AUDITORIA";
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
	private TicketsService ticketsService;
	
	@Autowired
	private ServicioContadores servicioContadores;
	
	public void guardarAuditoria(AuditoriaDto auditoria) {
		TicketBean ticket = new TicketBean();
		
		String uidActividad = sesion.getAplicacion().getUidActividad();
		auditoria.setUidActividad(uidActividad);
		ticket.setUidActividad(uidActividad);
		
		String uid = UUID.randomUUID().toString();
		auditoria.setUidAuditoria(uid);
		ticket.setUidTicket(uid);
		ticket.setLocatorId(uid);
		
		String codalm = sesion.getAplicacion().getCodAlmacen();
		auditoria.setCodalm(codalm);
		ticket.setCodAlmacen(codalm);
		
		String codcaja = sesion.getAplicacion().getCodCaja();
		auditoria.setCodcaja(codcaja);
		ticket.setCodcaja(codcaja);
		
		ticket.setIdTipoDocumento(ID_TIPO_DOC_AUDITORIA);
		
		ticket.setCodTicket("*");
		ticket.setFirma("*");
		ticket.setSerieTicket("*");
		
		Date fecha = new Date();
		if(auditoria.getFecha() != null) {
			fecha = auditoria.getFecha();
		}
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		auditoria.setHora(format.format(fecha));
		auditoria.setFecha(fecha);
		ticket.setFecha(fecha);
		
		Caja cajaAbierta = sesion.getSesionCaja().getCajaAbierta();
		if(cajaAbierta != null) {
			auditoria.setCajeroCaja(cajaAbierta.getUsuario());
		}
		
		UsuarioBean usuario = sesion.getSesionUsuario().getUsuario();
		if(usuario != null && StringUtils.isBlank(auditoria.getCajeroOperacion())) {
			auditoria.setCajeroOperacion(usuario.getUsuario());
		}
		
		SqlSession sqlSession = new SqlSession();
		try {
			ticket.setIdTicket(servicioContadores.obtenerValorContador(ID_CONTADOR_AUDITORIA, uidActividad));
			
			byte[] xml = MarshallUtil.crearXML(auditoria);
			log.debug("guardarAuditoria() - XML de auditoría: " + new String(xml));
			ticket.setTicket(xml);
			
			sqlSession.openSession(SessionFactory.openSession());
			ticketsService.insertarTicket(sqlSession, ticket, false);
			sqlSession.commit();
		}
		catch(Exception e) {
			log.error("guardarAuditoria() - Ha habido un error al guardar la auditoria: " + e.getMessage(), e);
			sqlSession.rollback();
		}
		finally {
			sqlSession.close();
		}
	}

}
