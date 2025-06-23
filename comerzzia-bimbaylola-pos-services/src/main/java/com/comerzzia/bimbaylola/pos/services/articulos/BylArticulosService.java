package com.comerzzia.bimbaylola.pos.services.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.comerzzia.bimbaylola.pos.persistence.articulos.buscar.ByLArticuloBuscarBean;
import com.comerzzia.bimbaylola.pos.persistence.articulos.buscar.ByLArticulosBuscarDao;
import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.ArticuloNotFoundException;
import com.comerzzia.pos.services.articulos.ArticulosService;
import com.comerzzia.pos.services.articulos.ArticulosServiceException;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;

@Service
@Primary
public class BylArticulosService extends ArticulosService{
	
	/** Busca los artículos que coincidan con los parámetros de búsqueda. La búsqueda se realiza 
     * indicando comodines al final de los filtros. El código sirve para filtrar tanto códigos de barra como
     * códigos de artículo. Se filtra por defecto artículos activos.
     * @param paramBuscar :: Parámetros de búsqueda.
     * @return :: Lista de artículos
     * @throws ArticulosServiceException 
     */
    public List<ArticuloBuscarBean> buscarArticulos(ArticulosParamBuscar paramBuscar) throws ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        List<ArticuloBuscarBean> resultados = null;
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
                    resultados.add(new ByLArticuloBuscarBean(articulo, codigoBarras, tarifa));
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
                return ByLArticulosBuscarDao.buscarArticulosDesgloseByL(conn, paramBuscar);
            }
            else{
                return ArticulosBuscarDao.buscarArticulos(conn, paramBuscar);
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

}
