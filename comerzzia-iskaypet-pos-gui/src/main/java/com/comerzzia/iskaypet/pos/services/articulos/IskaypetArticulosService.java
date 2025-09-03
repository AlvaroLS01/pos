package com.comerzzia.iskaypet.pos.services.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.iskaypet.pos.persistence.articulos.buscar.IskaypetArticuloBuscarBean;
import com.comerzzia.iskaypet.pos.persistence.articulos.buscar.IskaypetArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraExample;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Primary
@Service
@SuppressWarnings("deprecation")
public class IskaypetArticulosService extends ArticulosService {
	
	@Autowired
	protected Sesion sesion;
	
	@Override
    public List<ArticuloBuscarBean> buscarArticulos(ArticulosParamBuscar paramBuscar) throws ArticulosServiceException {
        SqlSession sqlSession = new SqlSession();
        List<ArticuloBuscarBean> resultados;
        try{
            sqlSession.openSession(SessionFactory.openSession());

            String uidActividad = sesion.getAplicacion().getUidActividad();
            String codTarifa = paramBuscar.getCodTarifa();

            paramBuscar.setUidActividad(uidActividad);
            paramBuscar.setCodTarifa(codTarifa);

            // Si nos han indicado un código de artículo, primero buscamos si es un código de barras
            if (!paramBuscar.getCodigo().isEmpty()){
                try{
                    ArticuloCodBarraBean codigoBarras = consultarCodigoBarras(sqlSession, paramBuscar.getCodigo());
                    ArticuloBean articulo = consultarArticulo(sqlSession, codigoBarras.getCodArticulo(), false);
                    TarifaDetalleBean tarifa = null;
                    try{
                        TarifaArticuloDto tarifaArticuloDto = articulosTarifaService.consultarArticuloTarifa(sqlSession, articulo.getCodArticulo(), paramBuscar.getCliente(), codigoBarras.getDesglose1(), codigoBarras.getDesglose2(), new Date());
                        if(tarifaArticuloDto != null) {
                            tarifa = tarifaArticuloDto.getDetalle();
                        }
                    }
                    catch(ArticuloTarifaNotFoundException ex){
                        // No hacemos nada. Pondremos precio a vacío.
                    }
                    resultados = new ArrayList<>();
                    resultados.add(new IskaypetArticuloBuscarBean(articulo, codigoBarras, tarifa));
                    return resultados;
                }
                catch(ArticuloNotFoundException e){
                    // Realizaremos la búsqueda general a continuación.
                }
            }

            // Si no nos indican código o si no hemos encontrado resultados por código de barras
            Connection conn = new Connection();
            conn.abrirConexion(sqlSession.getConnection());
            if (sesion.getAplicacion().isDesglosesActivos()){
                return ArticulosBuscarDao.buscarArticulosDesglose(conn, paramBuscar);
            }
            else{
                return IskaypetArticulosBuscarDao.buscarArticulosIskaypet(conn, paramBuscar, sesion);
            }
        }
        catch(Exception e){
            String mgs = "Se ha producido un error realizando búsqueda de artículos: "  + e.getMessage();
            log.error("buscarArticulos() - "+ mgs, e);
            throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }

	/**
	 * CZZ-2447 - Modificación metodo estandar para que consulte un artículo por código de barras, reintentando con un 0 delante si tiene 12 dígitos.
	 */
	@Override
	public ArticuloCodBarraBean consultarCodigoBarras(SqlSession sqlSession, String codigoBarras) throws ArticuloNotFoundException, ArticulosServiceException {
	    try {
	    	log.debug("consultarCodigoBarras()");
	        String uidActividad = sesion.getAplicacion().getUidActividad();
	        ArticuloCodBarraBean articulo = consultarPorCodigo(codigoBarras, uidActividad);

	        if (articulo != null) {
	            return articulo;
	        }

	        // Si no se encuentra y es numérico de 12 dígitos, intentar con un 0 delante
	        if (codigoBarras != null && codigoBarras.length() == 12 && codigoBarras.matches("\\d{12}")) {
	            String codigoAlternativo = "0" + codigoBarras;
	            log.debug("consultarCodigoBarras() - Reintentando con código alternativo: " + codigoAlternativo);
	            articulo = consultarPorCodigo(codigoAlternativo, uidActividad);

	            if (articulo != null) {
	                return articulo;
	            }
	        }

	        log.debug("consultarCodigoBarras() - No se encontró el artículo por código de barras: " + codigoBarras);
	        throw new ArticuloNotFoundException();

	    } catch (ArticuloNotFoundException e) {
	        throw e;
	    } catch (Exception e) {
	        String msg = "Se ha producido un error consultando el código de barras " + codigoBarras + ": " + e.getMessage();
	        log.error("consultarCodigoBarras() - " + msg, e);
	        throw new ArticulosServiceException(e);
	    }
	}
	
	/**
	 * CZZ-2447 - Consulta un artículo por código de barras exacto y actividad.
	 */
	private ArticuloCodBarraBean consultarPorCodigo(String codigoBarras, String uidActividad) {
		log.debug("consultarPorCodigo()");
	    ArticuloCodBarraExample exampleCodBarra = new ArticuloCodBarraExample();
	    exampleCodBarra.or().andCodigoBarrasEqualTo(codigoBarras).andUidActividadEqualTo(uidActividad);

	    List<ArticuloCodBarraBean> articulos = articuloCodBarraMapper.selectByExample(exampleCodBarra);
	    if (!articulos.isEmpty()) {
	        return articulos.get(0);
	    }
	    return null;
	}
	
}
