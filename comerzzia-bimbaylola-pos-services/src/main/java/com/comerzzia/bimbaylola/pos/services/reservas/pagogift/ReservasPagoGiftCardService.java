package com.comerzzia.bimbaylola.pos.services.reservas.pagogift;

import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.reservas.ReservasPagoGiftCardBean;
import com.comerzzia.bimbaylola.pos.persistence.reservas.ReservasPagoGiftCardKey;
import com.comerzzia.bimbaylola.pos.persistence.reservas.ReservasPagoGiftCardMapper;
import com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception.ReservasPagoGiftCardConstraintViolationException;
import com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception.ReservasPagoGiftCardException;
import com.comerzzia.bimbaylola.pos.services.reservas.pagogift.exception.ReservasPagoGiftCardNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class ReservasPagoGiftCardService{

	protected static Logger log = Logger.getLogger(ReservasPagoGiftCardService.class);
		
	public ReservasPagoGiftCardBean consultar(String uidActividad, Long idCliente, int linea) throws ReservasPagoGiftCardException,
		ReservasPagoGiftCardNotFoundException{
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			ReservasPagoGiftCardMapper mapper = sqlSession.getMapper(ReservasPagoGiftCardMapper.class);
			ReservasPagoGiftCardKey key = new ReservasPagoGiftCardKey();
			key.setUidActividad(uidActividad);
			key.setIdClieAlbaran(idCliente);
			key.setLinea(linea);			
			
			ReservasPagoGiftCardBean pagoGiftConsultado = mapper.selectByPrimaryKey(key);
			if(pagoGiftConsultado == null || pagoGiftConsultado.getUidActividad() == null){
				String mensajeInfo = "No se ha encontrado ningún resultado del pago con GifCard en Reservas con los datos proporcionados";
				log.info("consultar() - " + mensajeInfo);
				throw new ReservasPagoGiftCardNotFoundException(I18N.getTexto(mensajeInfo));
			}
			return pagoGiftConsultado;
		}catch(Exception e){
			String mensajeError;
			if(e instanceof ReservasPagoGiftCardNotFoundException){
				throw new ReservasPagoGiftCardNotFoundException(e.getMessage(), e);
			}
			else{
				mensajeError = "Error al consultar el pago con GifCard en Reservas : " +  e.getMessage();
				log.error("consultar() - " + mensajeError + " - " + e.getMessage());
				throw new ReservasPagoGiftCardException(mensajeError, e);
			}
		}finally{
			sqlSession.close();
		}
	}
	
	public void crear(ReservasPagoGiftCardBean movimientoTarjeta) throws ReservasPagoGiftCardException,
		ReservasPagoGiftCardConstraintViolationException{
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			ReservasPagoGiftCardMapper mapper = sqlSession.getMapper(ReservasPagoGiftCardMapper.class);
			mapper.insert(movimientoTarjeta);
			
			sqlSession.commit();
		}catch(Exception e){
			sqlSession.rollback();
			String mensajeError;
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException){
				mensajeError = "Ya existe un registro en la tabla para el pago con GifCard en Reservas con estos datos";
				log.error("consultar() - " + mensajeError + " - " + e.getMessage());
				throw new ReservasPagoGiftCardConstraintViolationException(mensajeError);
			}else{
				mensajeError = "Error al crear el pago con GifCard en Reservas : " +  e.getMessage();
				log.error("consultar() - " + mensajeError + " - " + e.getMessage());
				throw new ReservasPagoGiftCardException(mensajeError, e);
			}
		}finally{
			sqlSession.close();
		}
	}
	
}
