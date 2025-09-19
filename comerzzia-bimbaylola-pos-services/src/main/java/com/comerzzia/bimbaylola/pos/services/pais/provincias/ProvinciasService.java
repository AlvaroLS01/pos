package com.comerzzia.bimbaylola.pos.services.pais.provincias;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.pais.provincias.ProvinciasBean;
import com.comerzzia.bimbaylola.pos.persistence.pais.provincias.ProvinciasExample;
import com.comerzzia.bimbaylola.pos.persistence.pais.provincias.ProvinciasMapper;
import com.comerzzia.bimbaylola.pos.services.pais.provincias.exception.ProvinciasException;
import com.comerzzia.bimbaylola.pos.services.pais.provincias.exception.ProvinciasNotFoundException;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class ProvinciasService{

	protected static Logger log = Logger.getLogger(ProvinciasService.class);

	public Boolean consultarProvincia(String codigoPais, String descripcion, String uidInstancia) throws ProvinciasNotFoundException, ProvinciasException{
		log.debug("consultarProvincia() - Iniciamos la consulta de la Provincia...");
		SqlSession sqlSession = new SqlSession();
		try{
			sqlSession.openSession(SessionFactory.openSession());
			ProvinciasMapper mapper = sqlSession.getMapper(ProvinciasMapper.class);
			ProvinciasExample example = new ProvinciasExample();
			example.or().andUidInstanciaEqualTo(uidInstancia)
						.andCodpaisEqualTo(codigoPais)
						.andDesprovinciaEqualTo(descripcion);
			
			log.debug("consultarProvincia() - Consultando la Provincia : ");
			log.debug("consultarProvincia() - UidInstancia : " + uidInstancia);
			log.debug("consultarProvincia() - Código País : " + codigoPais);
			log.debug("consultarProvincia() - Nombre : " + descripcion);
			
			List<ProvinciasBean> provinviaConsultada = mapper.selectByExample(example);
			if(provinviaConsultada != null && !provinviaConsultada.isEmpty()){
				log.debug("consultarProvincia() - Finalizada la consulta de la Provincia");
				return Boolean.TRUE;
			}
			else{
				String mensajeInfo = "La Provincia no es válida";
				throw new ProvinciasNotFoundException(mensajeInfo);
			}
		}catch(Exception e){
			String mensajeError = "";
			if(e instanceof ProvinciasNotFoundException){
				mensajeError = e.getMessage();
				log.info("consultarProvincia() - " + mensajeError, e);
				throw new ProvinciasNotFoundException(mensajeError, e);
			}
			else{
				mensajeError = "Error al consultar la Provincia : " +  e.getMessage();;
				log.error("consultarProvincia() - " + mensajeError + " - " + e.getMessage());
				throw new ProvinciasException(mensajeError, e);
			}
		}finally{
			sqlSession.close();
		}
	}
	
}
