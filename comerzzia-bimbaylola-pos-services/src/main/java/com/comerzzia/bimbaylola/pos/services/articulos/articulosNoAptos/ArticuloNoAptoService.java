package com.comerzzia.bimbaylola.pos.services.articulos.articulosNoAptos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.articulosNoAptos.ArticuloNoAptoBean;
import com.comerzzia.bimbaylola.pos.persistence.articulosNoAptos.ArticuloNoAptoExample;
import com.comerzzia.bimbaylola.pos.persistence.articulosNoAptos.ArticuloNoAptoKey;
import com.comerzzia.bimbaylola.pos.persistence.articulosNoAptos.ArticuloNoAptoMapper;
import com.comerzzia.core.servicios.sesion.DatosSesionBean;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class ArticuloNoAptoService{
	
	protected static Logger log = Logger.getLogger(ArticuloNoAptoService.class);
	
	protected static ArticuloNoAptoService instance;
	
	public static ArticuloNoAptoService get(){
		if(instance == null){
			instance = new ArticuloNoAptoService();
		}
		return instance;
	}
	
	public static void setCustomInstance(ArticuloNoAptoService instance){
		ArticuloNoAptoService.instance = instance;
	}
	
	/**
	 * Consulta un articulo en BBDD para saber si es no apto o apto.
	 * @param codArt : Código del articulo a consultar. 
	 * @param uidActividad : ID de la actividad.
	 * @return apto : Boolean que indica si es apto o no.
	 * @throws ArticuloNoAptoException
	 */
	public Boolean consultarSiApto(String codArt, String uidActividad) 
			throws ArticuloNoAptoException{
		
		log.debug("consultar() - Consultando si un artículo es apto. ");
		Boolean apto = true;
		
		SqlSession sqlSession = new SqlSession();
		
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			ArticuloNoAptoMapper mapper = sqlSession.getMapper(ArticuloNoAptoMapper.class);
			ArticuloNoAptoKey key = new ArticuloNoAptoKey();
			key.setCodart(codArt);
			key.setUidActividad(uidActividad);

			ArticuloNoAptoBean articulo = mapper.selectByPrimaryKey(key);
			
			if(articulo == null){
				String msg = "No se ha encontrado articulo que no sean aptos";
				log.info("consultar() - " + msg);
				throw new ArticuloNoAptoNotFoundException(msg);
			}
			
			/* Comprobamos si el articulo es apto o no. */
			if(articulo.getApto().equals("N")){
				apto = false;
			}
			
			return apto;
			
		}catch(Exception e){
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar datos de un artículo no apto: " +  e.getMessage();
		    throw new ArticuloNoAptoException(mensaje, e);
		}finally{
			sqlSession.close();
		}
		
	}
	
	/**
	 * Devuelve un listado con los articulos no aptos e la lista pasada.
	 * @param codArt : Código del articulo a consultar. 
	 * @param uidActividad : ID de la actividad.
	 * @return 
	 * @throws ArticuloNoAptoException
	 */
	public List<String> consultarNoAptosLista(List<String> listCodArt, String uidActividad) 
			throws ArticuloNoAptoException{
		
		log.debug("consultar() - Consultando lista de articulos para comprobar que sean aptos. ");
		
		List<String> listadoNoAptos = new ArrayList<>();
		SqlSession sqlSession = new SqlSession();
		
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			ArticuloNoAptoMapper mapper = sqlSession.getMapper(ArticuloNoAptoMapper.class);
			ArticuloNoAptoBean artNoApto = null;
			
			for(String codArt : listCodArt){
				/* Añadimos la restricción de que tiene que ser no apto. */
				ArticuloNoAptoKey key = new ArticuloNoAptoKey();
				key.setCodart(codArt);
				key.setUidActividad(uidActividad);

				artNoApto = mapper.selectByPrimaryKey(key);
				
				/* Guardamos los articulos que contengan el apto a N en la lista.*/
				if(artNoApto != null){
					if(artNoApto.getApto().equals("N")){
						listadoNoAptos.add(artNoApto.getCodart());
					}
				}
			}
			
			/* Si la lista está vacía devolvemos una lista en blanco, esto
			 * indica que los articulos si son aptos.*/
			if(listadoNoAptos.isEmpty()){
				String msg = "No se ha encontrado articulo que no sean aptos";
				log.info("consultar() - " + msg);
				return new ArrayList<String>();
			}

			return listadoNoAptos;
			
		}catch(Exception e){
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar los articulos no aptos : " +  e.getMessage();
		    throw new ArticuloNoAptoException(mensaje, e);
		} finally {
			sqlSession.close();
		}
		
	}
	
	/**
	 * Consulta la base de datos para traer un listado de los
	 * articulos que no son aptos. 
	 * @param datosSesion
	 * @param sqlSession
	 * @return
	 * @throws ArticuloNoAptoException
	 */
	public List<ArticuloNoAptoBean> consultarSoloNoAptos(DatosSesionBean datosSesion) 
			throws ArticuloNoAptoException{
		
		log.debug("consultar() - Consultando todos los articulos no aptos. ");
		
		SqlSession sqlSession = new SqlSession();
		
		try{
			sqlSession.openSession(SessionFactory.openSession());
			
			ArticuloNoAptoMapper mapper = sqlSession.getMapper(ArticuloNoAptoMapper.class);
			/* Añadimos la restricción de que tiene que ser no apto. */
			ArticuloNoAptoExample example = new ArticuloNoAptoExample();
			example.or().andAptoEqualTo("N")
						.andUidActividadEqualTo(datosSesion.getUidActividad());

			List<ArticuloNoAptoBean> artNoApto = mapper.selectByExample(example);
			
			if(artNoApto.isEmpty()){
				String msg = "No se ha encontrado articulo que no sean aptos";
				log.info("consultar() - " + msg);
				throw new ArticuloNoAptoNotFoundException(msg);
			}
			
			return artNoApto;
			
		}catch(Exception e){
			log.error("consultar() - " + e.getMessage());
			String mensaje = "Error al consultar los articulos no aptos : " +  e.getMessage();
		    throw new ArticuloNoAptoException(mensaje, e);
		} finally {
			sqlSession.close();
		}
		
	}
		
}
