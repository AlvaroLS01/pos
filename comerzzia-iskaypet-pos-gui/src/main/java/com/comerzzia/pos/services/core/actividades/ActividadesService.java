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


package com.comerzzia.pos.services.core.actividades;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.actividades.ActividadBean;
import com.comerzzia.pos.persistence.core.actividades.POSActividadMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class ActividadesService {
    
    protected static final Logger log = Logger.getLogger(ActividadesService.class);
    
    @Autowired
    protected POSActividadMapper actividadMapper;
    
    /**
     * Devuelve un Bean que representa la Actividad que se desea consultar.
     * @param uidActividad
     * @return :: actividadBean
     * @throws ActividadNotFoundException
     * @throws ActividadesServiceException 
     */
    public  ActividadBean consultarActividad(String uidActividad) throws ActividadNotFoundException, ActividadesServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            ActividadBean actividadBean;
            sqlSession.openSession(SessionFactory.openSession());
            
            log.debug("consultarActividad() - realizando consulta de actividad para uidActividad :"+uidActividad);
            actividadBean = actividadMapper.selectByPrimaryKey(uidActividad);
            if(actividadBean == null){
                String mgs= "No se ha encontrado la actividad configurada en el fichero pos_config.xml: "+uidActividad;
                throw new ActividadNotFoundException(mgs);
            }
            else{
                return actividadBean;
            }
        }
        catch (ActividadNotFoundException ex) {
             log.error("consultarActividad() - actividad no encontrada para el uidActividad:"+uidActividad);
             throw new ActividadNotFoundException(ex);
        }       
        catch( PersistenceException ex){
        	String msg = "Se ha producido un error consultando la entidad con codigo " + uidActividad + ": " + ex.getMessage();
            log.error("consultarActividad() - "+ msg);
            ActividadesServiceException exception = new ActividadesServiceException(ex.getMessage());
            throw exception;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando la entidad con codigo " + uidActividad + ": " + e.getMessage();
            log.error("consultarActividad() - "+ msg);
            throw new ActividadesServiceException(e.getMessage(), e);
        }
        finally{
            sqlSession.close();
        }
    }
}