/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */
package com.comerzzia.pos.services.apartados;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraBean;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraExample;
import com.comerzzia.pos.persistence.apartados.ApartadosCabeceraMapper;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleBean;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleExample;
import com.comerzzia.pos.persistence.apartados.detalle.ApartadosDetalleMapper;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoBean;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoExample;
import com.comerzzia.pos.persistence.apartados.pagos.ApartadosPagoMapper;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.mybatis.SpringTransactionSqlSession;
import com.comerzzia.pos.services.cajas.CajasService;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.ticket.Ticket;
import com.comerzzia.pos.services.ticket.TicketVenta;
import com.comerzzia.pos.services.ticket.TicketsServiceException;
import com.comerzzia.pos.services.ticket.pagos.IPagoTicket;
import com.comerzzia.pos.services.ticket.pagos.PagoTicket;
import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.SpringContext;

@Service
public class ApartadosService {
	protected static final Logger log = Logger.getLogger(ApartadosService.class);
	
	@Autowired
	protected Sesion sesion;
	
	@Autowired
	protected CajasService cajasService;
	
	@Autowired
	protected ApartadosCabeceraMapper apartadosCabeceraMapper;
	
	@Autowired
	protected ApartadosDetalleMapper apartadosDetalleMapper;
	
	@Autowired
	protected ApartadosPagoMapper apartadosPagoMapper;
	
	public List<ApartadosCabeceraBean> consultarApartados(String cliente, Long numApartado, boolean verTodos, String uidActividad){
		
		SqlSession sqlSession = new SqlSession();

        try {
            log.debug("consultarApartados() - Consultando ticket en base de datos...");
            sqlSession.openSession(SessionFactory.openSession());
            ApartadosCabeceraExample example = new ApartadosCabeceraExample();
            ApartadosCabeceraExample.Criteria criteria = example.createCriteria();
            example.setOrderByClause(example.ORDER_BY_ID_APARTADO_DESC);
            criteria.andUidActividadEqualTo(uidActividad);
            if(cliente!=null && !cliente.isEmpty()){
            	criteria.andDesClienteLike("%"+cliente+"%");
            }
            if(numApartado!=null){
            	criteria.andIdApartadoEqualTo(numApartado);
            }            
            if(!verTodos){
            	criteria.andEstadoApartadoEqualTo(ApartadosCabeceraBean.ESTADO_DISPONIBLE);
            }
            List<ApartadosCabeceraBean> selectByExample = apartadosCabeceraMapper.selectByExample(example);
            
            log.debug("consultarApartados() - Consulta terminada, encontrados "+ selectByExample.size() + " apartados");
            
			return selectByExample;
        }
        catch (Exception e) {
            String msg = "Se ha producido un error consultando apartados en base de datos con parámetros indicados: cliente: " + cliente + ", apartado: "+ numApartado;
            log.error("consultarApartados() - " + msg, e);
            throw e;
        }
        finally {
        	sqlSession.close();
        }
	}

	public List<ApartadosDetalleBean> consultarArticulosApartados(String uidApartado){

		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("consultarArticulosApartados() - Consultando ticket en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			ApartadosDetalleExample example = new ApartadosDetalleExample();
			ApartadosDetalleExample.Criteria criteria = example.createCriteria();

			criteria.andUidApartadoEqualTo(uidApartado);

			return apartadosDetalleMapper.selectByExample(example);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando articulos apartados en base de datos con parámetros indicados: uidApartado: " + uidApartado ;
			log.error("consultarArticulosApartados() - " + msg, e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
	public List<ApartadosPagoBean> consultarPagos(String uidApartado){

		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("consultarPagos() - Consultando pagos de apartados...");
			sqlSession.openSession(SessionFactory.openSession());
			ApartadosPagoExample example = new ApartadosPagoExample();
			ApartadosPagoExample.Criteria criteria = example.createCriteria();

			criteria.andUidApartadoEqualTo(uidApartado);

			return apartadosPagoMapper.selectByExample(example);
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando articulos apartados en base de datos con parámetros indicados: uidApartado: " + uidApartado ;
			log.error("consultarArticulosApartados() - " + msg, e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void nuevoArticuloApartado(ApartadosDetalleBean articulo){
		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("nuevoArticuloApartado() - Añadiendo nuevo artículo al apartado...");
			sqlSession.openSession(SessionFactory.openSession());
			apartadosDetalleMapper.insert(articulo);
		}
		catch (Exception e) {
			log.error("nuevoArticuloApartado() - " + "", e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void nuevoApartado(ApartadosCabeceraBean cabecera){
		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("nuevoApartado() - Creando nuevo apartado...");
			sqlSession.openSession(SessionFactory.openSession());
			apartadosCabeceraMapper.insert(cabecera);
		}
		catch (Exception e) {
			log.error("nuevoApartado() - " + "", e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void eliminarArticuloApartado(ApartadosDetalleBean articulo){
		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("eliminarArticuloApartado() - Eliminando apartado en base de datos...");
			sqlSession.openSession(SessionFactory.openSession());
			ApartadosDetalleExample example = new ApartadosDetalleExample();
			ApartadosDetalleExample.Criteria criteria = example.createCriteria();

			criteria.andUidApartadoEqualTo(articulo.getUidApartado());
			criteria.andLineaEqualTo(articulo.getLinea());

			apartadosDetalleMapper.deleteByExample(example);
		}
		catch (Exception e) {
			log.error("eliminarArticuloApartado() - " + "", e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}

	
	public void actualizarCabeceraApartado(ApartadosCabeceraBean cabecera){
		
		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("actualizarCabeceraApartado() - Actualizando cabecera del apartado...");
			sqlSession.openSession(SessionFactory.openSession());
			apartadosCabeceraMapper.updateByPrimaryKey(cabecera);
			sqlSession.commit();
		}
		catch (Exception e) {
			sqlSession.rollback();
			log.error("actualizarCabeceraApartado() - " + "", e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
	public void actualizarCabeceraApartado(ApartadosCabeceraBean cabecera, SqlSession sqlSession){
		try {
			log.debug("actualizarCabeceraApartado() - Actualizando cabecera del apartado...");
			apartadosCabeceraMapper.updateByPrimaryKey(cabecera);
		}
		catch (Exception e) {
			log.error("actualizarCabeceraApartado() - " + "", e);
			throw e;
		}
	}
	
	public void actualizarDetalleApartado(ApartadosDetalleBean detalle){

		SqlSession sqlSession = new SqlSession();

		try {
			log.debug("actualizarDetalleApartado() - Actualizando detalle del apartado...");
			sqlSession.openSession(SessionFactory.openSession());
			apartadosDetalleMapper.updateByPrimaryKey(detalle);
		}
		catch (Exception e) {
			log.error("actualizarDetalleApartado() - " + "", e);
			throw e;
		}
		finally {
			sqlSession.close();
		}
	}
	
    public void registrarMovimientosPagosApartado(Ticket ticket, List<PagoTicket> pagos, ApartadosCabeceraBean cabeceraApartado) throws TicketsServiceException{
    	log.debug("registrarMovimientosPagosApartado() - Procesando movimientos... ");
    	
    	SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
    	Date fecha = new Date();
    	ApartadosPagoBean pagoApartado;
    	try{
    		sqlSession.openSession(SessionFactory.openSession());
    		Integer idLineaCaja = cajasService.consultarProximaLineaDetalleCaja(sqlSession);
    		for (IPagoTicket pago : pagos) {
    			CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
    			detalleCaja.setLinea(idLineaCaja);
    			detalleCaja.setFecha(fecha);
    			//Si es positivo se crea el movimiento como cargo, si no, como abono
    			if(pago.getImporte().compareTo(BigDecimal.ZERO)>0){
    				detalleCaja.setCargo(pago.getImporte());
    				detalleCaja.setAbono(BigDecimal.ZERO);
    			}
    			else if(pago.getImporte().compareTo(BigDecimal.ZERO)<0){
    				detalleCaja.setCargo(BigDecimal.ZERO);
    				detalleCaja.setAbono(pago.getImporte().negate());
    			}
    			detalleCaja.setConcepto("Apartado nº " + cabeceraApartado.getIdApartado());
    			detalleCaja.setCodMedioPago(pago.getCodMedioPago());
    			cajasService.crearMovimiento(sqlSession, detalleCaja);
    			    			
    			pagoApartado = new ApartadosPagoBean();
    			pagoApartado.setLinea(idLineaCaja);
    			pagoApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
    			pagoApartado.setUidApartado(cabeceraApartado.getUidApartado());
    			pagoApartado.setUidDiarioCaja(sesion.getSesionCaja().getUidDiarioCaja());
    			pagoApartado.setFechaActualizacion(fecha);
    			pagoApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
    			nuevoPagoApartado(pagoApartado, sqlSession);
    			    			
    			idLineaCaja++;
    		}
    		
    		//Añadimos el movimiento de cambio
    		IPagoTicket cambio = ticket.getTotales().getCambio();
    		if(!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())){
	            CajaMovimientoBean detalleCaja = new CajaMovimientoBean();
	            detalleCaja.setLinea(idLineaCaja);
	            detalleCaja.setFecha(ticket.getFecha());
	            //Cargo o abono al revés de lo normal
	            if(BigDecimalUtil.isMayorOrIgualACero(ticket.getCabecera().getTotales().getTotal())){
	                detalleCaja.setCargo(cambio.getImporte().negate());
	                detalleCaja.setAbono(BigDecimal.ZERO);
	            }else{
	                detalleCaja.setCargo(BigDecimal.ZERO);
	                detalleCaja.setAbono(cambio.getImporte().negate());
	            }
	            detalleCaja.setConcepto("Apartado nº " + cabeceraApartado.getIdApartado() + " (cambio)");
	            detalleCaja.setCodMedioPago(cambio.getCodMedioPago());
	            cajasService.crearMovimiento(sqlSession, detalleCaja);
	            
	            pagoApartado = new ApartadosPagoBean();
    			pagoApartado.setLinea(idLineaCaja);
    			pagoApartado.setUidActividad(sesion.getAplicacion().getUidActividad());
    			pagoApartado.setUidApartado(cabeceraApartado.getUidApartado());
    			pagoApartado.setUidDiarioCaja(sesion.getSesionCaja().getUidDiarioCaja());
    			pagoApartado.setFechaActualizacion(fecha);
    			pagoApartado.setUsuario(sesion.getSesionUsuario().getUsuario().getUsuario());
    			nuevoPagoApartado(pagoApartado, sqlSession);
	            
	            idLineaCaja++;
            }
            
            //Añadimos el cambio como un pago
            if(!BigDecimalUtil.isIgualACero(ticket.getCabecera().getTotales().getCambio().getImporte())){
	            IPagoTicket pagoCodMedPagoCambio = ((TicketVenta)ticket).getPago(cambio.getMedioPago().getCodMedioPago());
	        	MedioPagoBean medioPagoCambio = ticket.getCabecera().getTotales().getCambio().getMedioPago();
	        	
	        	if(pagoCodMedPagoCambio == null){
	        		pagoCodMedPagoCambio = createPago();
	        		pagoCodMedPagoCambio.setEliminable(false);
	        		pagoCodMedPagoCambio.setMedioPago(medioPagoCambio);
	        		
	        		((TicketVenta)ticket).addPago(pagoCodMedPagoCambio);
	        	}
	        	
	        	if(BigDecimalUtil.isMayorOrIgualACero(ticket.getCabecera().getTotales().getTotal())){
	        		pagoCodMedPagoCambio.setImporte(pagoCodMedPagoCambio.getImporte().subtract(((TicketVenta)ticket).getTotales().getCambio().getImporte()));
	        	}else{
	        		pagoCodMedPagoCambio.setImporte(pagoCodMedPagoCambio.getImporte().add(((TicketVenta)ticket).getTotales().getCambio().getImporte()));
	        	}
            }
    		
            actualizarCabeceraApartado(cabeceraApartado, sqlSession);
    		
    		sqlSession.commit();
    	}
    	catch (Exception e) {
    		sqlSession.rollback();
    		String msg = "Se ha producido un error procesando los movimientos";
    		log.error("registrarMovimientosPagosApartado() - " + msg, e);
    		throw new TicketsServiceException(e);
    	}
    	finally {
    		sqlSession.close();
    	}
    }
	
    protected PagoTicket createPago() {
		return SpringContext.getBean(PagoTicket.class);
	}
    
	public void nuevoPagoApartado(ApartadosPagoBean pago, SqlSession sqlSession){
		
		try {
			log.debug("nuevoPagoApartado() - Insertando nuevo pago...");
			apartadosPagoMapper.insert(pago);
		}
		catch (Exception e) {
			log.error("nuevoPagoApartado() - " + "", e);
			throw e;
		}
	}
	
	public void crearMovimientoDevolucion(ApartadosCabeceraBean apartadoCabecera, MedioPagoBean mP, CajaMovimientoBean detalleCaja) throws TicketsServiceException{
		log.debug("crearMovimientoDevolucion() - Procesando movimientos... ");
    	
    	SqlSession sqlSession = SpringContext.getBean(SpringTransactionSqlSession.class);
    	try{
    		sqlSession.openSession(SessionFactory.openSession());
    		Integer idLineaCaja = cajasService.consultarProximaLineaDetalleCaja(sqlSession);
    		detalleCaja.setLinea(idLineaCaja);
    		    		   		
    		cajasService.crearMovimiento(sqlSession, detalleCaja);
    		
    		sqlSession.commit();   		
    	}
    	catch (Exception e) {
    		sqlSession.rollback();
    		String msg = "Se ha producido un error procesando los movimientos";
    		log.error("registrarMovimientosPagosApartado() - " + msg, e);
    		throw new TicketsServiceException(e);
    	}
    	finally {
    		sqlSession.close();
    	}
	}
	
	public long obtenerSiguienteIDApartado() throws Exception{
		log.trace("obtenerSiguienteIDApartado() - ");
		
		String uidActividad = sesion.getAplicacion().getUidActividad();
        SqlSession sqlSession = new SqlSession();
        try {
        	sqlSession.openSession(SessionFactory.openSession());
        	ApartadosCabeceraExample exampleCabecera = new ApartadosCabeceraExample();
        	exampleCabecera.or().andUidActividadEqualTo(uidActividad);
            Long id = apartadosCabeceraMapper.selectUltimoIDApartado(exampleCabecera);
            if (id == null) {
                id = 1l;
            }
            return id;
        }
        catch (Exception e) {
        	String msg = "Se ha producido un error obteniendo el siguiente id disponible: " + e.getMessage();
            log.error("obtenerSiguienteIDApartado() - " + msg, e);
            throw e;
        }
        finally {
            sqlSession.close();
        }
	}
}
