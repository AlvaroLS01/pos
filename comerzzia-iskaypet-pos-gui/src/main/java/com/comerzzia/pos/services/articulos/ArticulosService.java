/**
 * ComerZZia 3.0
 *
 * Copyright (c) 2008-2015 Comerzzia, S.L.  All Rights Reserved.
 *
 * THIS WORK IS  SUBJECT  TO  SPAIN  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF COMERZZIA, S.L. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 *
 * CONSULT THE END USER LICENSE AGREEMENT FOR INFORMATION ON ADDITIONAL
 * RESTRICTIONS.
 */

package com.comerzzia.pos.services.articulos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.db.Connection;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.articulos.ArticuloBean;
import com.comerzzia.pos.persistence.articulos.ArticuloExample;
import com.comerzzia.pos.persistence.articulos.ArticuloKey;
import com.comerzzia.pos.persistence.articulos.ArticuloMapper;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosBuscarDao;
import com.comerzzia.pos.persistence.articulos.buscar.ArticulosParamBuscar;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraBean;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraExample;
import com.comerzzia.pos.persistence.articulos.codBarras.ArticuloCodBarraMapper;
import com.comerzzia.pos.persistence.articulos.etiquetas.EtiquetaArticuloExample;
import com.comerzzia.pos.persistence.articulos.etiquetas.EtiquetaArticuloKey;
import com.comerzzia.pos.persistence.articulos.etiquetas.EtiquetaArticuloMapper;
import com.comerzzia.pos.persistence.articulos.tarifas.TarifaDetalleBean;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.articulos.tarifas.ArticuloTarifaNotFoundException;
import com.comerzzia.pos.services.articulos.tarifas.ArticulosTarifaService;
import com.comerzzia.pos.services.articulos.tarifas.TarifaArticuloDto;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.util.i18n.I18N;

@Service
public class ArticulosService {
    
    protected static final Logger log = Logger.getLogger(ArticulosService.class);
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected ArticulosTarifaService articulosTarifaService;
    
    @Autowired
    protected ArticuloMapper articuloMapper;
    
    @Autowired
    protected ArticuloCodBarraMapper articuloCodBarraMapper;
    
    @Autowired
    protected EtiquetaArticuloMapper etiquetaArticuloMapper;

    /** Busca un artículo activo con el código indicado. Si no existe, se devuelve una excepción.
     * @param sqlSession
     * @param codigo :: Código de artículo
     * @param noFiltrarActivo :: Si el valor está a true, se comprobará si el artículo está activo y si no lo está lanzará una excepción
     * @return :: ArticuloBean
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el artículo o está inactivo
     * @throws ArticulosServiceException 
     */
    public ArticuloBean consultarArticulo (SqlSession sqlSession, String codigo, boolean noFiltrarActivo) throws ArticuloNotFoundException, ArticulosServiceException{
        try{ 
            String uidActividad = sesion.getAplicacion().getUidActividad();
            log.debug("consultarArticulo() - Consultando articulo con código : " + codigo);
            
            ArticuloKey keyArticulo = new ArticuloKey(); 
            keyArticulo.setUidActividad(uidActividad); 
            keyArticulo.setCodArticulo(codigo); 
             
            ArticuloBean articuloBean = articuloMapper.selectByPrimaryKey(keyArticulo); 
             
            if(articuloBean!= null){ 
            	// En devoluciones permitimos utilizar articulos inactivos
                if(noFiltrarActivo || articuloBean.getActivo()){
                	EtiquetaArticuloExample etiquetaArticuloExample = new EtiquetaArticuloExample();
                	etiquetaArticuloExample.or()
                		.andUidActividadEqualTo(articuloBean.getUidActividad())
                		.andIdClaseEqualTo("D_ARTICULOS_TBL.CODART")
                		.andIdObjetoEqualTo(articuloBean.getCodArticulo());
                	EtiquetaArticuloKey example = new EtiquetaArticuloKey();
                	example.setUidActividad(uidActividad);
                	example.setIdClase("D_ARTICULOS_TBL.CODART");
                	example.setIdObjeto(articuloBean.getCodArticulo());
					articuloBean.setEtiquetas(etiquetaArticuloMapper.selectWithDesc(example));
                	
                    return articuloBean;
                }
                else{
                    log.warn("consultarArticulo() - Articulo Inactivo : "+ codigo );
                    throw new ArticuloNotFoundException(I18N.getTexto("El artículo indicado se encuentra inactivo"));
                }
            }            
            throw new ArticuloNotFoundException();
        }
        catch(ArticuloNotFoundException e){
        	log.debug("consultarArticulo() - No se encontró el artículo : "+ codigo );
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el artículo con código " + codigo + ": " + e.getMessage();
            log.error("consultarArticulo() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
    }    
    
    /** Busca un artículo activo con el código indicado. Si no existe, se devuelve una excepción.
     * @param codigo :: Código de artículo
     * @return :: ArticuloBean
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el artículo
     * @throws ArticulosServiceException 
     */
    public ArticuloBean consultarArticulo (String codigo) throws ArticuloNotFoundException, ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{ 
            sqlSession.openSession(SessionFactory.openSession());
            return consultarArticulo(sqlSession, codigo, false);
        }
        catch(ArticuloNotFoundException | ArticulosServiceException e){
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el artículo con código " + codigo + ": " + e.getMessage();
            log.error("consultarArticulo() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
    
    public ArticuloBean consultarArticuloSinEtiqueta(String codigo) throws ArticuloNotFoundException, ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{ 
            sqlSession.openSession(SessionFactory.openSession());
            String uidActividad = sesion.getAplicacion().getUidActividad();
            log.debug("consultarArticuloSinEtiqueta() - Consultando articulo con código : " + codigo);
            
            ArticuloKey keyArticulo = new ArticuloKey(); 
            keyArticulo.setUidActividad(uidActividad); 
            keyArticulo.setCodArticulo(codigo); 
             
            ArticuloBean articuloBean = articuloMapper.selectByPrimaryKey(keyArticulo); 
            if(articuloBean == null){
            	throw new ArticuloNotFoundException();
            }
            return articuloBean;
        }
        catch(ArticuloNotFoundException e){
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el artículo con código " + codigo + ": " + e.getMessage();
            log.error("consultarArticuloSinEtiqueta() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }

    
    /** Busca un código de barras con el código indicado. Si el código de barras no existe,
     * se devuelve una excepción.
     * @param codigoBarras
     * @return :: ArticuloCodBarraBean
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el código de barras
     * @throws ArticulosServiceException 
     */
    public ArticuloCodBarraBean consultarCodigoBarras (String codigoBarras) throws ArticuloNotFoundException, ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{ 
            sqlSession.openSession(SessionFactory.openSession());
            return consultarCodigoBarras(sqlSession, codigoBarras);
        }
        catch(ArticuloNotFoundException | ArticulosServiceException e){
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el código de barras " + codigoBarras + ": " + e.getMessage();
            log.error("consultarCodigoBarras() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
    
    public List<ArticuloCodBarraBean> consultarCodigoBarras () throws ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{ 
            sqlSession.openSession(SessionFactory.openSession());
            return consultarCodigoBarras(sqlSession);
        }
        catch(ArticulosServiceException e){
            throw e;
        }
        catch(Exception e){
        	String msg = "Se ha producido un error consultando el listado completo de código de barras: " + e.getMessage();
        	log.error("consultarCodigoBarras() - "+ msg, e);
        	throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }


    /** Busca un código de barras con el código indicado. Si el código de barras no existe,
     * se devuelve una excepción.
     * @param sqlSession
     * @param codigoBarras
     * @return :: ArticuloCodBarraBean
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el código de barras
     * @throws ArticulosServiceException 
     */
    public ArticuloCodBarraBean consultarCodigoBarras (SqlSession sqlSession, String codigoBarras) throws ArticuloNotFoundException, ArticulosServiceException{
        try{ 
            String uidActividad = sesion.getAplicacion().getUidActividad();

            ArticuloCodBarraExample exampleCodBarra = new ArticuloCodBarraExample();
            exampleCodBarra.or().andCodigoBarrasEqualTo(codigoBarras).andUidActividadEqualTo(uidActividad); 
            log.debug("consultarCodigoBarras() - Consultando código de barras con codigo : " + codigoBarras);
            List<ArticuloCodBarraBean> articulosCodBarraBean = articuloCodBarraMapper.selectByExample(exampleCodBarra);
            
            if(!articulosCodBarraBean.isEmpty()){
                return articulosCodBarraBean.get(0);
            }
            throw new ArticuloNotFoundException();
        }
        catch(ArticuloNotFoundException e){
            log.debug("consultarCodigoBarras() - No se encontró el artículo por código de barras : "+ codigoBarras );
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el código de barras " + codigoBarras + ": " + e.getMessage();
            log.error("consultarCodigoBarras() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
    }
    
    public List<ArticuloCodBarraBean> consultarCodigoBarras (SqlSession sqlSession) throws ArticuloNotFoundException, ArticulosServiceException{
        try{ 
            String uidActividad = sesion.getAplicacion().getUidActividad();

            ArticuloCodBarraExample exampleCodBarra = new ArticuloCodBarraExample();
            exampleCodBarra.or().andUidActividadEqualTo(uidActividad);
            log.debug("consultarCodigoBarras() - Consultando listado completo de código de barras.");
            List<ArticuloCodBarraBean> articulosCodBarraBean = articuloCodBarraMapper.selectByExample(exampleCodBarra);
            
            if(!articulosCodBarraBean.isEmpty()){
                return articulosCodBarraBean;
            }
            throw new ArticuloNotFoundException();
        }
        catch(ArticuloNotFoundException e){
            throw e;
        }
        catch(Exception e){
        	String msg = "Se ha producido un error consultando el listado completo de código de barras: " + e.getMessage();
            log.error("consultarCodigoBarras() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
    }
    
    
    /** Devuelve lista de códigos de barras para el artículo indicado. 
     * @param codigoArticulo
     * @return :: List<ArticuloCodBarraBean>
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el código de barras
     * @throws ArticulosServiceException 
     */
    public List<ArticuloCodBarraBean> consultarCodigosBarrasArticulo (String codigoArticulo) throws ArticuloNotFoundException, ArticulosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{ 
            sqlSession.openSession(SessionFactory.openSession());
            return consultarCodigosBarrasArticulo(sqlSession, codigoArticulo);
        }
        catch(ArticuloNotFoundException | ArticulosServiceException e){
            throw e;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando códigos de barras para el artículo " + codigoArticulo + ": " + e.getMessage();
            log.error("consultarCodigosBarrasArticulo() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
    
    /** Devuelve lista de códigos de barras para el artículo indicado.
     * @param sqlSession
     * @param codigoArticulo
     * @return :: List<ArticuloCodBarraBean>
     * @throws ArticuloNotFoundException :: Lanzada si no se encuentra el código de barras
     * @throws ArticulosServiceException 
     */
    public List<ArticuloCodBarraBean> consultarCodigosBarrasArticulo (SqlSession sqlSession, String codigoArticulo) throws ArticuloNotFoundException, ArticulosServiceException{
        try{ 
            String uidActividad = sesion.getAplicacion().getUidActividad();
            ArticuloCodBarraExample exampleCodBarra = new ArticuloCodBarraExample();
            exampleCodBarra.or().andCodArticuloEqualTo(codigoArticulo).andUidActividadEqualTo(uidActividad); 
            log.debug("consultarCodigosBarrasArticulo() - Consultando código de barras para código artículo : " + codigoArticulo);
            return articuloCodBarraMapper.selectByExample(exampleCodBarra);
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando códigos de barras para el artículo " + codigoArticulo + ": " + e.getMessage();
            log.error("consultarCodigosBarrasArticulo() - "+ msg, e);
            throw new ArticulosServiceException(e);
        }
    }
    
    
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
                    resultados.add(new ArticuloBuscarBean(articulo, codigoBarras, tarifa));
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
    
    public DesglosesDTO consultarDesglosesArticulo(String codart) throws ArticulosServiceException {
    	try{ 
    		String uidActividad = sesion.getAplicacion().getUidActividad();
    		
    		DesglosesDTO desglosesDTO = new DesglosesDTO();
    		desglosesDTO.setCodart(codart);
    		desglosesDTO.setDesgloses1(articuloCodBarraMapper.selectDistinctDesglose1(uidActividad, codart));
    		desglosesDTO.setDesgloses2(articuloCodBarraMapper.selectDistinctDesglose2(uidActividad, codart));
    		
            return desglosesDTO;
        }
        catch(Exception e){
            log.error("consultarDesglosesArticulo() - Ha habido un error al consultar los desgloses del artículo: " + e.getMessage(), e);
            throw new ArticulosServiceException(I18N.getTexto("Ha habido un error al consultar los desgloses del artículo"), e);
        }
    }
    
    public List<ArticuloBean> consultar(ArticuloExample example) {
        example.createCriteria().andUidActividadEqualTo(sesion.getAplicacion().getUidActividad());
        return articuloMapper.selectByExample(example);    
    }
    
}
