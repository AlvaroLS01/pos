package com.comerzzia.bimbaylola.pos.services.pais.x;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.pais.x.XPaisBean;
import com.comerzzia.bimbaylola.pos.persistence.pais.x.XPaisKey;
import com.comerzzia.bimbaylola.pos.persistence.pais.x.XPaisMapper;
import com.comerzzia.bimbaylola.pos.services.pais.x.exception.XPaisException;
import com.comerzzia.bimbaylola.pos.services.pais.x.exception.XPaisNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class XPaisesService{

	protected static Logger log = Logger.getLogger(XPaisesService.class);
	
	public Integer consultarPrefijoPais(String codPais, String uidInstancia) throws XPaisException{
		log.debug("consultarPrefijoPais() - Iniciamos la consulta del Prefijo...");
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			XPaisMapper mapper = sqlSession.getMapper(XPaisMapper.class);
			XPaisKey key = new XPaisKey();
			key.setUidInstancia(uidInstancia);
			key.setCodpais(codPais);
			
			log.debug("consultarPrefijoPais() - Consultando Prefijo del País : ");
			log.debug("consultarPrefijoPais() - UidInstancia : " + uidInstancia);
			log.debug("consultarPrefijoPais() - Código País : " + codPais);
			
			XPaisBean xPais = mapper.selectByPrimaryKey(key);
			if(xPais == null || xPais.getUidInstancia() == null){
				String mensajeInfo = "No se ha encontrado ningún resultado de XPais para el País consultado";
				log.info("consultarPrefijoPais() - " + mensajeInfo);
				throw new XPaisNotFoundException(mensajeInfo);
			}
			log.debug("consultarPrefijoPais() - Finalizada la consulta del Prefijo");
			return xPais.getPrefijoTelefono();
		}catch(Exception e){
			String mensajeError = "Error al consultar el Prefijo de un País : " +  e.getMessage();
			log.error("consultarPrefijoPais() - " + mensajeError + " - " + e.getMessage());
		    throw new XPaisException(mensajeError, e);
		}finally{
			sqlSession.close();
		}
	}
	
}
