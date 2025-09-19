package com.comerzzia.bimbaylola.pos.services.movimientos;

import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjeta;
import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjetaKey;
import com.comerzzia.bimbaylola.pos.persistence.movimientostarjeta.CajaMovimientoTarjetaMapper;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaConstraintViolationException;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaException;
import com.comerzzia.bimbaylola.pos.services.movimientos.exception.CajaMovimientoTarjetaNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.movimientos.CajaMovimientoBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class CajaMovimientoTarjetaService{

	protected static Logger log = Logger.getLogger(CajaMovimientoTarjetaService.class);
		
	/**
	 * Realiza una consulta a los movimientos de caja de tarjetas y devuelve el Ticket.
	 * @param movimientoBean : 
	 * @return byte[]
	 * @throws CajaMovimientoTarjetaException
	 */
	public byte[] consultarTicket(CajaMovimientoBean movimientoBean) throws CajaMovimientoTarjetaException{
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			CajaMovimientoTarjetaMapper mapper = sqlSession.getMapper(CajaMovimientoTarjetaMapper.class);
			CajaMovimientoTarjetaKey key = new CajaMovimientoTarjetaKey();
			key.setUidActividad(movimientoBean.getUidActividad());
			key.setUidDiarioCaja(movimientoBean.getUidDiarioCaja());
			key.setLinea(movimientoBean.getLinea());			
			log.debug("consultarTicket() - Consultando Movimiento : ");
			log.debug("consultarTicket() - UidActividad : " + movimientoBean.getUidActividad());
			log.debug("consultarTicket() - UidDiarioCaja : " + movimientoBean.getUidDiarioCaja());
			log.debug("consultarTicket() - Linea : " + movimientoBean.getLinea());
			
			CajaMovimientoTarjeta movimientoTarjeta = mapper.selectByPrimaryKey(key);
			if(movimientoTarjeta == null || movimientoTarjeta.getUidActividad() == null){
				String mensajeInfo = "No se ha encontrado ningún resultado de Movimiento con los datos proporcionados";
				log.info("consultarTicket() - " + mensajeInfo);
				return null;
			}
			return movimientoTarjeta.getRespuestaTarjeta();
		}catch(Exception e){
			String mensajeError = "Error al consultar el Movimiento de Caja de una Tarjeta : " +  e.getMessage();
			log.error("consultarTicket() - " + mensajeError + " - " + e.getMessage());
		    throw new CajaMovimientoTarjetaException(mensajeError, e);
		}finally{
			sqlSession.close();
		}
	}
	
	public CajaMovimientoTarjeta consultar(CajaMovimientoBean cajaMovimientoBean) throws CajaMovimientoTarjetaException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());

			CajaMovimientoTarjetaMapper mapper = sqlSession.getMapper(CajaMovimientoTarjetaMapper.class);
			CajaMovimientoTarjetaKey key = new CajaMovimientoTarjetaKey();
			key.setUidActividad(cajaMovimientoBean.getUidActividad());
			key.setUidDiarioCaja(cajaMovimientoBean.getUidDiarioCaja());
			key.setLinea(cajaMovimientoBean.getLinea());
			log.debug("consultar() - Consultando Movimiento : ");
			log.debug("consultar() - UidActividad : " + cajaMovimientoBean.getUidActividad());
			log.debug("consultar() - UidDiarioCaja : " + cajaMovimientoBean.getUidDiarioCaja());
			log.debug("consultar() - Linea : " + cajaMovimientoBean.getLinea());

			CajaMovimientoTarjeta movimientoTarjeta = mapper.selectByPrimaryKey(key);
			if (movimientoTarjeta == null || movimientoTarjeta.getUidActividad() == null) {
				log.warn("No se ha encontrado ningún resultado de Movimiento con los datos proporcionados");
				throw new CajaMovimientoTarjetaNotFoundException("No se ha encontrado ningún resultado de movimiento con los datos proporcionados (uidDiarioCaja: " + cajaMovimientoBean.getUidDiarioCaja() + ", linea: " + cajaMovimientoBean.getLinea() + ")");
			}
			return movimientoTarjeta;
		}
		catch (Exception e) {
			String mensajeError = "Error al consultar el Movimiento de Caja '" + cajaMovimientoBean.getUidDiarioCaja() + "' (linea: " + cajaMovimientoBean.getLinea() + ") :" + e.getMessage();
			log.error("consultar() - " + mensajeError);
			throw new CajaMovimientoTarjetaException(mensajeError, e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	/**
	 * Crea un nuevo registro de un Movimiento de caja para una tarjeta.
	 * @param movimientoTarjeta : Objeto que contiene los datos de un movimiento de caja con tarjeta.
	 * @throws CajaMovimientoTarjetaException
	 * @throws CajaMovimientoTarjetaConstraintViolationException
	 */
	public void crear(CajaMovimientoTarjeta movimientoTarjeta, SqlSession sqlSession) throws CajaMovimientoTarjetaException,
		CajaMovimientoTarjetaConstraintViolationException{
		try{
			CajaMovimientoTarjetaMapper mapper = sqlSession.getMapper(CajaMovimientoTarjetaMapper.class);
			mapper.insert(movimientoTarjeta);
		}catch(Exception e){
			String mensajeError;
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "Ya existe un registro en la tabla para Movimiento de Caja de Tarjetas con estos datos";
				log.error("consultar() - " + mensajeError + " - " + e.getMessage());
				throw new CajaMovimientoTarjetaConstraintViolationException(mensajeError);
			}else{
				mensajeError = "Error al crear el Movimiento de Caja de una Tarjeta : " +  e.getMessage();
				log.error("consultar() - " + mensajeError + " - " + e.getMessage());
				throw new CajaMovimientoTarjetaException(mensajeError, e);
			}
		}
	}
	
}
