package com.comerzzia.cardoso.pos.services.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.cardoso.pos.persistence.articulos.CardosoArticuloBean;
import com.comerzzia.cardoso.pos.persistence.articulos.buscar.CardosoArticulosBuscarDao;
import com.comerzzia.cardoso.pos.persistence.lotes.CardosoAtributosAdicionalesArticuloBean;
import com.comerzzia.cardoso.pos.persistence.lotes.anexa.CardosoLoteArticuloBean;
import com.comerzzia.cardoso.pos.services.lotes.CardosoLoteService;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("deprecation")
@Service
@Primary
public class CardosoArticulosService extends ArticulosService{
	
	private static final Logger log = Logger.getLogger(CardosoArticulosService.class.getName());
	
	/**
     * ########################################################################################
	 * GAP - PERSONALIZACIONES V3 - LOTES
	 * 
	 * Incluimos en las búsquedas de artículos la parte de buscar si son lotes o no.
	 * Tablas : "X_LOTE_ARTICULO_TBL", "X_ARTICULOS_LOTE_TBL".
	 */
	
	@Autowired
	private CardosoLoteService loteArticuloService;
	
	@Override
	public ArticuloBean consultarArticulo(SqlSession sqlSession, String codigo, boolean esNotaCredito) throws ArticuloNotFoundException, ArticulosServiceException {
		log.debug("consultarArticulo() : GAP - PERSONALIZACIONES V3 -  LOTES");
		
		ArticuloBean articulo = super.consultarArticulo(sqlSession, codigo, esNotaCredito);
		
		CardosoArticuloBean articuloCardoso = null;
		if(articulo != null){
			try{
				articuloCardoso = new CardosoArticuloBean();
				articuloCardoso = articuloCardoso.getBean(articulo);
				CardosoAtributosAdicionalesArticuloBean atributosAdicionalesArticulo = 
						loteArticuloService.getAtributosAdicionalesArticulo(sesion.getAplicacion().getUidActividad(), codigo);
				if(atributosAdicionalesArticulo != null){
					articuloCardoso.setAtributosAdicionalesArticulo(atributosAdicionalesArticulo);
					
					if(atributosAdicionalesArticulo.getLote()){
						CardosoLoteArticuloBean loteArticulo = loteArticuloService.getLoteArticulo(sesion.getAplicacion().getUidActividad(), codigo);
						if(loteArticulo != null){
							articuloCardoso.setLote(loteArticulo);
						}
					}
				}
			}
			catch(Exception e){
				log.error("consultarArticulo() - " + e.getMessage(), e);
				throw new ArticulosServiceException(e.getMessage(), e);
			}
		}
		return articuloCardoso == null ? articulo : articuloCardoso;
	}
	
	/**
	 * ########################################################################################
	 * GAP - DESCUENTO TARIFA
	 * 
	 * Cambiamos la búsqueda de artículos para poder coger su descuento de tarifa.
	 */
	
	@Override
	public List<ArticuloBuscarBean> buscarArticulos(ArticulosParamBuscar paramBuscar) throws ArticulosServiceException{
		log.debug("buscarArticulos() : GAP - DESCUENTO TARIFA");
		
		SqlSession sqlSession = new SqlSession();
		List<ArticuloBuscarBean> resultados = null;
		try{
			sqlSession.openSession(SessionFactory.openSession());

			String uidActividad = sesion.getAplicacion().getUidActividad();
			String codTarifa = paramBuscar.getCodTarifa();

			paramBuscar.setUidActividad(uidActividad);
			paramBuscar.setCodTarifa(codTarifa);

			/* Si nos han indicado un código de artículo, primero buscamos si es un código de barras */
			if(!paramBuscar.getCodigo().isEmpty()){
				try{
					ArticuloCodBarraBean codigoBarras = consultarCodigoBarras(sqlSession, paramBuscar.getCodigo());
					ArticuloBean articulo = consultarArticulo(sqlSession, codigoBarras.getCodArticulo(), false);
					TarifaDetalleBean tarifa = null;
					try{
						TarifaArticuloDto tarifaArticuloDto = articulosTarifaService.consultarArticuloTarifa(sqlSession, articulo.getCodArticulo(), paramBuscar.getCliente(),
						        codigoBarras.getDesglose1(), codigoBarras.getDesglose2(), new Date());
						if(tarifaArticuloDto != null){
							tarifa = tarifaArticuloDto.getDetalle();
						}
					}
					catch(ArticuloTarifaNotFoundException ex){
						/* No hacemos nada. Pondremos precio a vacío. */
					}
					resultados = new ArrayList<>();
					resultados.add(new ArticuloBuscarBean(articulo, codigoBarras, tarifa));
					return resultados;
				}
				catch(ArticuloNotFoundException e){
					/* Realizaremos la búsqueda general a continuación. */
				}
			}

			/* Si no nos indican código o si no hemos encontrado resultados por código de barras */
			Connection conn = new Connection();
			conn.abrirConexion(sqlSession.getConnection());
			if(sesion.getAplicacion().isDesglosesActivos()){
				return CardosoArticulosBuscarDao.buscarArticulosDesglose(conn, paramBuscar);
			}
			else{
				return CardosoArticulosBuscarDao.buscarArticulos(conn, paramBuscar);
			}
		}
		catch(Exception e){
			String mgsError = I18N.getTexto("Error realizando la búsqueda de artículos : ") + e.getMessage();
			log.error("buscarArticulos() - " + mgsError, e);
			throw new ArticulosServiceException(e);
		}
		finally{
			sqlSession.close();
		}
	}
	
}
