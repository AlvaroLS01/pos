/**
 * ComerZZia 3.0 Copyright (c) 2008-2015 Comerzzia, S.L. All Rights Reserved. THIS WORK IS SUBJECT TO SPAIN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES. NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY. CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL RESTRICTIONS.
 */

package com.comerzzia.pos.services.articulos.tarifas;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleExample;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleMapper;
import com.comerzzia.pos.persistence.clientes.ClienteBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tarifas.TarifaBean;
import com.comerzzia.pos.persistence.tarifas.TarifaKey;
import com.comerzzia.pos.persistence.tarifas.TarifaMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.config.SpringContext;

@Service
public class ArticulosTarifaService {

	protected static final Logger log = Logger.getLogger(ArticulosTarifaService.class);

	@Autowired
	protected Sesion sesion;

	@Autowired
	protected TarifaDetalleMapper tarifaDetalleMapper;

	/** Código de la tarifa general */
	public static final String COD_TARIFA_GENERAL = "GENERAL";

	/**
	 * Obtiene la tarifa del artículo indicado
	 *
	 * @param codigoArticulo
	 * @param codTarifa
	 * @return
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException
	 */
	@Deprecated
	public TarifaDetalleBean consultarArticuloTarifa(String codigoArticulo, String codTarifa) throws ArticuloTarifaNotFoundException, ArticuloTarifaServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			return consultarArticuloTarifa(sqlSession, codigoArticulo, codTarifa, "*", "*", new Date());
		}
		finally {
			sqlSession.close();
		}
	}
	
	/**
	 * Obtiene la tarifa del artículo indicado
	 *
	 * @param codigoArticulo
	 * @param codTarifa
	 * @return
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException
	 */
	public TarifaDetalleBean consultarArticuloTarifa(String codigoArticulo, String codTarifa, String desglose1, String desglose2, Date vigencia) throws ArticuloTarifaNotFoundException, ArticuloTarifaServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			return consultarArticuloTarifa(sqlSession, codigoArticulo, codTarifa, desglose1, desglose2, vigencia);
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Obtiene la tarifa del artículo indicado
	 *
	 * @param sqlSession
	 * @param codigoArticulo
	 * @param codTarifa
	 * @return TarifaDetalleBean
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException
	 */
	public TarifaDetalleBean consultarArticuloTarifa(SqlSession sqlSession, String codigoArticulo, String codTarifa, String desglose1, String desglose2, Date vigencia) throws ArticuloTarifaNotFoundException, ArticuloTarifaServiceException {
		try {
			log.debug("consultarArticuloTarifa() - Consultando tarifa de articulo con código : " + codigoArticulo + " y codTarifa: " + codTarifa);

			TarifaDetalleBean articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, codTarifa, desglose1, desglose2, vigencia);

			if (articuloTarifa != null) {
				return articuloTarifa;
			}
			log.warn("consultarArticuloTarifa() - No se encontró la tarifa asociada al artículo : " + codigoArticulo + "/" + codTarifa);
			throw new ArticuloTarifaNotFoundException();
		}
		catch (ArticuloTarifaNotFoundException e) {
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando la tarifa del artículo con código " + codigoArticulo + " y codTarifa: " + codTarifa + " : " + e.getMessage();
			log.error("consultarArticuloTarifa() - " + msg, e);
			throw new ArticuloTarifaServiceException(e);
		}
	}

	/**
	 * Obtiene la tarifa del artículo indicado según tarifa del cliente y tarifa de la tienda
	 *
	 * @param sqlSession
	 * @param codigoArticulo
	 * @param cliente
	 * @return TarifaDetalleBean
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException
	 * @throws com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaServiceException
	 */
	public TarifaArticuloDto consultarArticuloTarifa(SqlSession sqlSession, String codigoArticulo, ClienteBean cliente, String desglose1, String desglose2, Date vigencia) throws ArticuloTarifaServiceException, ArticuloTarifaNotFoundException {
		try {
			if(desglose1 == null || desglose1.isEmpty()) {
				desglose1 = "*";
			}
			
			if(desglose2 == null || desglose2.isEmpty()) {
				desglose2 = "*";
			}
			log.debug("consultarArticuloTarifa() - Consultando tarifa de articulo con código : " + codigoArticulo + " para cliente: " + cliente);

			// Opción 1: Buscamos tarifa con código igual al código del cliente
			log.debug("consultarArticuloTarifa() - Buscando tarifa con código igual al código del cliente: " + cliente.getCodCliente());
			TarifaDetalleBean articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, cliente.getCodCliente(), desglose1, desglose2, vigencia);
			if (articuloTarifa != null) {
				TarifaArticuloDto tarifaArticuloDto = getTarifaArticuloDtoFromDetalle(sqlSession, articuloTarifa);
				return tarifaArticuloDto;
			}

			// Opción 2: Buscamos tarifa con código igual al código de tarifa del cliente
			if (cliente.isTarifaAsignada()) {
				log.debug("consultarArticuloTarifa() - Buscando tarifa con código igual al código de tarifa del cliente: " + cliente.getCodtar());
				articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, cliente.getCodtar(), desglose1, desglose2, vigencia);
				if (articuloTarifa != null) {
					TarifaArticuloDto tarifaArticuloDto = getTarifaArticuloDtoFromDetalle(sqlSession, articuloTarifa);
					return tarifaArticuloDto;
				}
			}

			// Opción 3: Buscamos tarifa con código igual al código de tarifa del cliente de la tienda
			if (sesion.getAplicacion().getTienda().getCliente().isTarifaAsignada()) {
				String codTarifaTienda = sesion.getAplicacion().getTienda().getCliente().getCodtar();
				log.debug("consultarArticuloTarifa() - Buscando tarifa con código igual al código de tarifa del cliente de la tienda: " + codTarifaTienda);
				articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, codTarifaTienda, desglose1, desglose2, vigencia);
				if (articuloTarifa != null) {
					TarifaArticuloDto tarifaArticuloDto = getTarifaArticuloDtoFromDetalle(sqlSession, articuloTarifa);
					return tarifaArticuloDto;
				}
			}
			
			 // Opción 4: Buscamos la tarifa padre de la tarifa con código igual al código de tarifa del cliente de la tienda
			if (sesion.getAplicacion().getTienda().getCliente().isTarifaAsignada()) {
				String codTarifaTienda = sesion.getAplicacion().getTienda().getCliente().getCodtar();
            	log.debug("consultarArticuloTarifa() - Buscando tarifa padre de la tarifa con código igual al código de tarifa del cliente de la tienda: " + codTarifaTienda);
            	      	
            	try{
            		TarifaKey key = new TarifaKey();
            	    key.setUidActividad(sesion.getAplicacion().getUidActividad());
            		key.setCodTarifa(codTarifaTienda);
            		
            		TarifaMapper tarifaMapper = sqlSession.getMapper(TarifaMapper.class);
	            	TarifaBean tarifaClienteTienda = tarifaMapper.selectByPrimaryKey(key);
	            	
	            	articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, tarifaClienteTienda.getCodTarifaPadre(), desglose1, desglose2, vigencia);
	                if (articuloTarifa != null) {
	    				TarifaArticuloDto tarifaArticuloDto = getTarifaArticuloDtoFromDetalle(sqlSession, articuloTarifa);
	    				return tarifaArticuloDto;
	                }
            	}catch(Exception ignore){}
            }
			
			// Opción 5: Buscamos tarifa general
			log.debug("consultarArticuloTarifa() - Buscando tarifa " + TarifaBean.TARIFA_GENERAL);
			articuloTarifa = obtenerTarifaDetalle(sqlSession, codigoArticulo, TarifaBean.TARIFA_GENERAL, desglose1, desglose2, vigencia);
			if (articuloTarifa != null) {
				TarifaArticuloDto tarifaArticuloDto = getTarifaArticuloDtoFromDetalle(sqlSession, articuloTarifa);
				return tarifaArticuloDto;
			}

			log.warn("consultarArticuloTarifa() - No se encontró la tarifa asociada al artículo : " + codigoArticulo);
			throw new ArticuloTarifaNotFoundException();
		}
		catch (ArticuloTarifaNotFoundException e) {
			log.warn("consultarArticuloTarifa() - No se encontró la tarifa asociada al artículo : " + codigoArticulo);
			throw e;
		}
		catch (Exception e) {
			String msg = "Se ha producido un error consultando la tarifa del artículo con código " + codigoArticulo + " para el cliente: " + cliente + " : " + e.getMessage();
			log.error("consultarArticuloTarifa() - " + msg, e);
			throw new ArticuloTarifaServiceException(e);
		}
	}

	protected TarifaArticuloDto getTarifaArticuloDtoFromDetalle(SqlSession sqlSession, TarifaDetalleBean articuloTarifa) {
	    TarifaArticuloDto tarifaArticuloDto = SpringContext.getBean(TarifaArticuloDto.class);
	    TarifaMapper tarifaMapper = sqlSession.getMapper(TarifaMapper.class);
	    
	    TarifaKey key = new TarifaKey();
	    key.setUidActividad(sesion.getAplicacion().getUidActividad());
	    key.setCodTarifa(articuloTarifa.getCodTarifa());
	    TarifaBean cabeceraTarifa = tarifaMapper.selectByPrimaryKey(key);
	    
	    tarifaArticuloDto.setCabecera(cabeceraTarifa);
	    tarifaArticuloDto.setDetalle(articuloTarifa);
	    
	    return tarifaArticuloDto;
    }

	private TarifaDetalleBean obtenerTarifaDetalle(SqlSession sqlSession, String codigoArticulo, String codTar, String desglose1, String desglose2, Date vigencia) {
		String uidActividad = sesion.getAplicacion().getUidActividad();
		TarifaDetalleExample example = new TarifaDetalleExample();
		example.setOrderByClause(TarifaDetalleExample.ORDER_BY_FECHA_INICIO_DESC + ", " + TarifaDetalleExample.ORDER_BY_DESGLOSE1_DESC + ", " + TarifaDetalleExample.ORDER_BY_DESGLOSE2_DESC);
		example.createCriteria().andCodArticuloEqualTo(codigoArticulo).andBorradoEqualTo("N").andCodTarifaEqualTo(codTar).andUidActividadEqualTo(uidActividad).andFechaInicioLessThanOrEqualTo(vigencia);

		return obtenerArticuloTarifaPorDesgloses(tarifaDetalleMapper.selectByExample(example), desglose1, desglose2);
	}

	public TarifaDetalleBean consultarArticuloTarifa(String codigoArticulo, ClienteBean cliente, String desglose1, String desglose2, Date vigencia) throws ArticuloTarifaNotFoundException, ArticuloTarifaServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());
			TarifaArticuloDto tarifaArticuloDto = consultarArticuloTarifa(sqlSession, codigoArticulo, cliente, desglose1, desglose2, vigencia);
			return tarifaArticuloDto != null ? tarifaArticuloDto.getDetalle() : null;
		}
		finally {
			sqlSession.close();
		}
	}

	/**
	 * Se busca dentro de la lista de objetos <i>TarifaDetalleBean</i> que se pasa como parámetro, la tarifa de artículo cuyos desgloses coinciden con los que se pasan como parámetros.
	 * 
	 * @param listArticulosTarifa
	 *            listado con todas las tarifas asociadas a artículos
	 * @param desglose1
	 *            cadena con el valor del desglose1 por el que se hace la comprobación
	 * @param desglose2
	 *            cadena con el valor del desglose1 por el que se hace la comprobación
	 * @return el objeto <i>TarifaDetalleBean</i> cuyos desgloses coinciden con los pasados como parámetros
	 * @throws ArticuloTarifaNotFoundException
	 *             se lanza cuando no se encuentra una tarifa para el artículo con los desgloses.
	 */
	public TarifaDetalleBean obtenerArticuloTarifaPorDesgloses(List<TarifaDetalleBean> listArticulosTarifa, String desglose1, String desglose2) {
		for (TarifaDetalleBean articuloTarifa : listArticulosTarifa) {
			if (desglose1.equals(articuloTarifa.getDesglose1()) && desglose2.equals(articuloTarifa.getDesglose2())) {
				return articuloTarifa;
			}
			else if (desglose1.equals(articuloTarifa.getDesglose1()) && "*".equals(articuloTarifa.getDesglose2())) {
				return articuloTarifa;
			}
			else if ("*".equals(articuloTarifa.getDesglose1()) && desglose2.equals(articuloTarifa.getDesglose2())) {
				return articuloTarifa;
			}
			else if ("*".equals(articuloTarifa.getDesglose1()) && "*".equals(articuloTarifa.getDesglose2())) {
				return articuloTarifa;
			}
		}
		return null;
	}

}
