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

package com.comerzzia.pos.services.core.acciones;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.acciones.AccionBean;
import com.comerzzia.pos.persistence.core.acciones.AccionExample;
import com.comerzzia.pos.persistence.core.acciones.POSAccionMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class AccionesService {

    protected static Logger log = Logger.getLogger(AccionesService.class);

    @Autowired
    protected POSAccionMapper accionMapper;
    
    /**
     * Devuelve la accion con el idAccion indicado que se encuentre en la BBDD en la tabla de CONFIG_ACCIONES . La acción debe estar activa
     *
     * @param idAccion
     * @return :: AccionBean
     * @throws com.comerzzia.pos.services.core.acciones.AccionesServiceException
     * @throws AccionNotFoundException :: Lanzada si no existe la acción con
     * el código indicado. La accion debe estar activa
     */
    public AccionBean consultarAccion(Long idAccion) throws AccionesServiceException,AccionNotFoundException {
       
        SqlSession sqlSession = new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            AccionExample exampleAccion = new AccionExample();
            exampleAccion.or().andIdAccionEqualTo(idAccion).andActivoEqualTo(Boolean.TRUE);
            log.debug("consultarAccion() - consultando accion para idAccion "+ idAccion);
            List<AccionBean> accionBean = accionMapper.selectByExample(exampleAccion);
            if(!accionBean.isEmpty()){
                return accionBean.get(0);
            }
            else{
                throw new AccionNotFoundException();
            }
        }
        catch(AccionNotFoundException e){
            log.error("consultarAccion() - Accion no encontrada para el idAccion " + idAccion);
            throw new AccionNotFoundException();
        }
        catch(Exception e){
            String mgs = "Se ha producido un error consultando la accion con idAccion :"+ idAccion +" , "+ e;
            log.error("consultarAccion() - " +mgs);
            throw new AccionesServiceException(e);
        }
        finally{
            sqlSession.close();
        }        
    }
    
}
