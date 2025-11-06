package com.comerzzia.dinosol.pos.services.articulos;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.dinosol.pos.persistence.articulos.ArticuloEtiquetasPrecioMapper;
import com.comerzzia.dinosol.pos.persistence.articulos.DinoArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.ArticuloMapper;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.util.i18n.I18N;

@SuppressWarnings("deprecation")
@Component
@Primary
public class DinoArticulosService extends ArticulosService {

	@Autowired
	protected ArticuloMapper articuloMapper;
	
	@Autowired
	protected ArticuloEtiquetasPrecioMapper articuloEtiquetasPrecioMapper;

	@Override
	public List<ArticuloBuscarBean> buscarArticulos(ArticulosParamBuscar paramBuscar) throws ArticulosServiceException {
		SqlSession sqlSession = new SqlSession();
		try {
			sqlSession.openSession(SessionFactory.openSession());

			String uidActividad = sesion.getAplicacion().getUidActividad();
			String codTarifa = paramBuscar.getCodTarifa();

			paramBuscar.setUidActividad(uidActividad);
			paramBuscar.setCodTarifa(codTarifa);

			String codartBusqueda = null;

			// Si nos han indicado un código de artículo, primero buscamos si es un código de barras
			if (!paramBuscar.getCodigo().isEmpty()) {
				try {
					ArticuloCodBarraBean codigoBarras = consultarCodigoBarras(sqlSession, paramBuscar.getCodigo());
					codartBusqueda = codigoBarras.getCodArticulo();
				}
				catch (ArticuloNotFoundException e) {
					// Realizaremos la búsqueda general a continuación.
				}
			}

			// Si no nos indican código o si no hemos encontrado resultados por código de barras
			Connection conn = new Connection();
			conn.abrirConexion(sqlSession.getConnection());
			if (codartBusqueda != null) {
				paramBuscar.setCodigo(codartBusqueda);
				return DinoArticulosBuscarDao.buscarArticulo(conn, paramBuscar);
			}
			else if (sesion.getAplicacion().isDesglosesActivos()) {
				return DinoArticulosBuscarDao.buscarArticulosDesglose(conn, paramBuscar);
			}
			else {
				return DinoArticulosBuscarDao.buscarArticulos(conn, paramBuscar);
			}
		}
		catch (Exception e) {
			String mgs = "Se ha producido un error realizando búsqueda de artículos: " + e.getMessage();
			log.error("buscarArticulos() - " + mgs, e);
			throw new ArticulosServiceException(e);
		}
		finally {
			sqlSession.close();
		}
	}
	
	public ArticuloBean buscarArticuloEtiquetaPrecio(String plu) throws ArticulosServiceException {
		plu = StringUtils.right(plu, 4);
		List<ArticuloBean> articulos = articuloEtiquetasPrecioMapper.selectArticuloEtiquetaPrecios(sesion.getAplicacion().getUidActividad(), "241" + plu + "00000_");
		if(articulos != null && !articulos.isEmpty()) {
			return articulos.get(0);
		}
		else {
			throw new ArticulosServiceException(I18N.getTexto("El artículo consultado no existe."));
		}
	}
	
}
