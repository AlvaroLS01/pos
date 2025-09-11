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

package com.comerzzia.pos.services.core.impuestos.grupos;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.impuestos.grupos.GrupoImpuestoBean;
import com.comerzzia.pos.persistence.core.impuestos.grupos.GrupoImpuestoExample;
import com.comerzzia.pos.persistence.core.impuestos.grupos.POSGrupoImpuestoMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.services.core.empresas.EmpresasService;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class GrupoImpuestosService {
 
    protected static final Logger log = Logger.getLogger(EmpresasService.class);
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected POSGrupoImpuestoMapper grupoImpuestoMapper;
    
    /** Obtiene el grupo de impuestos cuya fecha de inicio de vigencia es la más actual a partir
	de una fecha dada que se encuentra dentro de las fechas de vigencia de inicio y fin.
     * @return GrupoImpuestoBean
     * @throws com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosServiceException
     * @throws com.comerzzia.pos.services.core.impuestos.grupos.GrupoImpuestosNotFoundException
     */
    public GrupoImpuestoBean consultarGrupoImpuestosActual() throws GrupoImpuestosServiceException, GrupoImpuestosNotFoundException{
        SqlSession sqlSession =  new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            String uidActividad = sesion.getAplicacion().getUidActividad();
            
            GrupoImpuestoExample filtro = new GrupoImpuestoExample();
            filtro.or().andUidActividadEqualTo(uidActividad).andVigenciaDesdeLessThanOrEqualTo(new Date());
            filtro.setOrderByClause(GrupoImpuestoExample.ORDER_BY_VIGENCIA_DESDE_DESC);
            
            List<GrupoImpuestoBean> gruposImp = grupoImpuestoMapper.selectByExample(filtro);
            
            if(gruposImp != null && !gruposImp.isEmpty()){
                return gruposImp.get(0);
            }
            else{
                throw new GrupoImpuestosNotFoundException();
            }            
        }
        catch(GrupoImpuestosNotFoundException e){
            log.error("consultarGrupoImpuestosActual() - Grupo de impuestos con vigencia actual no encontrado. ");
            throw new GrupoImpuestosNotFoundException();
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando el grupo de impuestos con vigencia actual. " + e.getMessage();
            log.error("consultarGrupoImpuestosActual() - " + msg, e);
            throw new GrupoImpuestosServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
}