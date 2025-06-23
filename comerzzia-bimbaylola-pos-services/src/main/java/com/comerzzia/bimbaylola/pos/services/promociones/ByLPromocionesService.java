package com.comerzzia.bimbaylola.pos.services.promociones;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleExample;
import com.comerzzia.pos.services.promociones.PromocionesService;
import com.comerzzia.pos.services.promociones.PromocionesServiceException;

@Component
@Primary
public class ByLPromocionesService extends PromocionesService{

	/**
	 * Consulta el detalle de una promoción para un articulo.
	 * @param codArticulo : Código del articulo.
	 * @param codPromo : Código de la promoción.
	 * @return Resultado de la consulta con un objeto tipo PromocionDetalleBean.
	 * @throws PromocionesServiceException
	 */
	public List<PromocionDetalleBean> consultarDetallesPromocionArticulo(
			String codArticulo, Long codPromo) throws PromocionesServiceException{
		
		log.debug("consultarDetallesPromocion() - Consultando detalles promoción "
				+ "para el articulo: " + codArticulo);
		
        SqlSession sqlSession = new SqlSession();
        /* Necesitamos la fecha actual para poder calcular si la promoción 
         * está activa para esta fecha.*/
        Date fechaActual = new Date();
        
        try{
        	
            sqlSession.openSession(SessionFactory.openSession());
            /* Añadimos los filtros a la consulta. */
            PromocionDetalleExample example = new PromocionDetalleExample();
            example.or()
                    .andCodArticuloEqualTo(codArticulo)
                    .andIdPromocionEqualTo(codPromo)
            		.andFechaInicioLessThan(fechaActual)
            		.andFechaFinGreaterThan(fechaActual);
            
            return promocionDetalleMapper.selectByExampleWithBLOBs(example);
            
        }catch(Exception e){
            String msg = "Se ha producido un error consultando detalles de la promoción" 
            		+ e.getMessage();
            log.error("consultarDetallesPromocion() - " + msg, e);
            throw new PromocionesServiceException(e);
        }finally{
            sqlSession.close();
        }
        
    }
	
}
