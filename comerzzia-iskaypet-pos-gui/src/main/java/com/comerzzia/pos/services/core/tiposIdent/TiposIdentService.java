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


package com.comerzzia.pos.services.core.tiposIdent;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentBean;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentExample;
import com.comerzzia.pos.persistence.tiposIdent.TiposIdentMapper;
import com.comerzzia.pos.services.core.sesion.Sesion;

@Service
public class TiposIdentService {
    
    protected static Logger log = Logger.getLogger(TiposIdentService.class);
    
    @Autowired
    protected Sesion sesion;
    
    @Autowired
    protected TiposIdentMapper tiposIdentMapper;
    
    public List<TiposIdentBean> consultarTiposIdent(Boolean empresa, boolean activo, String codPais) throws TiposIdentNotFoundException, TiposIdentServiceException{
        
        SqlSession sqlSession = new SqlSession();
        
        try{
            sqlSession.openSession(SessionFactory.openSession());
            TiposIdentExample exampleTiposIdent = new TiposIdentExample();
            TiposIdentExample.Criteria criteriaTipoIdent = exampleTiposIdent.createCriteria();
            criteriaTipoIdent.andUidInstanciaEqualTo(sesion.getAplicacion().getUidInstancia()).andActivoEqualTo(Boolean.TRUE);      
            
            if(empresa!=null){
                criteriaTipoIdent.andEmpresaEqualTo(empresa);
            }
            if(codPais!=null && !codPais.isEmpty()){
                criteriaTipoIdent.andCodPaisLike(codPais);
            }

            exampleTiposIdent.setOrderByClause(TiposIdentExample.ORDER_BY_CODTIPOIDEN);

            List<TiposIdentBean> tiposIdentBean = tiposIdentMapper.selectByExample(exampleTiposIdent);
            
            if(!tiposIdentBean.isEmpty()){
                return tiposIdentBean;
            }
            else{
                throw new TiposIdentNotFoundException();
            }
        }catch(TiposIdentNotFoundException ex){
            log.debug("consultarTiposIdent() - No se encontraron tipos de identificación " );
            throw ex;
        }catch(Exception ex){
            log.debug("consultarTiposIdent() - Error consultando tipos de identificación :" + ex.getMessage(), ex);
            throw new TiposIdentServiceException(ex);
        }
        finally{
            sqlSession.close();
        }
        
    }
}
