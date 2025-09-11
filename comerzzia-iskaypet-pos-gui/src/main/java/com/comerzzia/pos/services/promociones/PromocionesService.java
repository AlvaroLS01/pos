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


package com.comerzzia.pos.services.promociones;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.fechas.Fecha;
import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.promociones.PromocionBean;
import com.comerzzia.pos.persistence.promociones.PromocionMapper;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleBean;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleExample;
import com.comerzzia.pos.persistence.promociones.detalle.PromocionDetalleMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class PromocionesService {
    
    protected static final Logger log = Logger.getLogger(PromocionesService.class);
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected PromocionMapper promocionMapper;
    
    @Autowired
    protected PromocionDetalleMapper promocionDetalleMapper;

    public List<PromocionBean> consultarPromocionesActivas() throws PromocionesServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            log.debug("consultarPromocionesActivas() - Consultando promociones activas...");
            sqlSession.openSession(SessionFactory.openSession());
            
            String uidActividad = sesion.getAplicacion().getUidActividad();
            String codAlmacen = sesion.getAplicacion().getTienda().getCodAlmacen();
            Fecha ahora = new Fecha();
            Fecha hoy = new Fecha(ahora.getString() + " - 00:00");
            
            PromocionBean example = new PromocionBean();
            example.setCodAlmacen(codAlmacen);
            example.setUidActividad(uidActividad);
            example.setFechaInicio(hoy.getDate());
            return promocionMapper.selectByCodAlmacen(example);
        }
        catch (Exception e) {
            String msg = "Se ha producido un error consultando promociones activas: " + e.getMessage();
            log.error("consultarPromocionesActivas() - " + msg, e);
            throw new PromocionesServiceException(e);
        }
        finally {
            sqlSession.close();
        }
    }

    public List<PromocionDetalleBean> consultarDetallesPromocion(Long idPromocion) throws PromocionesServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            log.debug("consultarDetallesPromocion() - Consultando detalles promoción: " + idPromocion);
            sqlSession.openSession(SessionFactory.openSession());
            
            // Consultamos detalles si los hubiera
            String uidActividad = sesion.getAplicacion().getUidActividad();
            PromocionDetalleExample example = new PromocionDetalleExample();
            example.or()
                    .andUidActividadEqualTo(uidActividad)
                    .andIdPromocionEqualTo(idPromocion);
            
            return promocionDetalleMapper.selectByExampleWithBLOBs(example);
        }
        catch (Exception e) {
            String msg = "Se ha producido un error consultando detalles de la promoción con id: " + idPromocion + " - " + e.getMessage();
            log.error("consultarDetallesPromocion() - " + msg, e);
            throw new PromocionesServiceException(e);
        }
        finally {
            sqlSession.close();
        }
    }
    
    
    
}
