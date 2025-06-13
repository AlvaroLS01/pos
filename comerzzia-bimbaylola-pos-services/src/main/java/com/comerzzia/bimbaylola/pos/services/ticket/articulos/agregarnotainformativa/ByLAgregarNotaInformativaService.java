package com.comerzzia.bimbaylola.pos.services.ticket.articulos.agregarnotainformativa;

import java.util.List;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.AvisoInformativoBean;
import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.AvisoInformativoExample;
import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.AvisoInformativoKey;
import com.comerzzia.bimbaylola.pos.persistence.ticket.articulos.agregarnotainformativa.AvisoInformativoMapper;
import com.comerzzia.bimbaylola.pos.services.articulos.articulosNoAptos.ArticuloNoAptoException;
import com.comerzzia.bimbaylola.pos.services.articulos.articulosNoAptos.ArticuloNoAptoNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class ByLAgregarNotaInformativaService {
	protected static Logger log = Logger.getLogger(ByLAgregarNotaInformativaService.class);
	
	protected static ByLAgregarNotaInformativaService instance;
	
	public static ByLAgregarNotaInformativaService get(){	
		if(instance == null){
			instance = new ByLAgregarNotaInformativaService();
		}
		return instance;
	}
	
	public static void setCustomInstance(ByLAgregarNotaInformativaService instance){
		ByLAgregarNotaInformativaService.instance = instance;
	}
	
	public List<AvisoInformativoBean> consultarAvisosInformativos() 
			throws ArticuloNoAptoException{
		
		log.debug("consultarAvisoInformativo() - Consultando un tipo de aviso informativo. ");
		
		SqlSession sqlSession = new SqlSession();
		
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			AvisoInformativoMapper mapper = sqlSession.getMapper(AvisoInformativoMapper.class);
			AvisoInformativoExample example = new AvisoInformativoExample();
			List<AvisoInformativoBean> avisos = mapper.selectByExample(example);
			
			if(avisos == null){
				String msg = "No se han encontrado avisos informativos";
				log.info("consultar() - " + msg);
				throw new ArticuloNoAptoNotFoundException(msg);
			}

			return avisos;
			
		}catch(Exception e){
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar el aviso informativo: " +  e.getMessage();
		    throw new ArticuloNoAptoException(mensaje, e);
		}finally{
			sqlSession.close();
		}
		
	}
	
	public AvisoInformativoBean consultarAvisoInformativo(String uidActividad, String codPais, String codigo) 
			throws ArticuloNoAptoException{
		
		log.debug("consultarAvisoInformativo() - Consultando un tipo de aviso informativo. ");
		
		SqlSession sqlSession = new SqlSession();
		
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			AvisoInformativoMapper mapper = sqlSession.getMapper(AvisoInformativoMapper.class);
			AvisoInformativoKey key = new AvisoInformativoKey();
			key.setUidActividad(uidActividad);
			key.setCodpais(codPais);
			key.setCodigo(codigo);
			AvisoInformativoBean aviso = mapper.selectByPrimaryKey(key);
			
			if(aviso == null){
				String msg = "No se ha encontrado el aviso con el código indicado: " + codigo;
				log.info("consultar() - " + msg);
				throw new ArticuloNoAptoNotFoundException(msg);
			}

			return aviso;
			
		}catch(Exception e){
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar el aviso informativo: " +  e.getMessage();
		    throw new ArticuloNoAptoException(mensaje, e);
		}finally{
			sqlSession.close();
		}
		
	}
}
