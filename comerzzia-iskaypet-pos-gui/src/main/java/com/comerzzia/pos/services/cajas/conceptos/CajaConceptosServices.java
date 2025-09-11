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

package com.comerzzia.pos.services.cajas.conceptos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoBean;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoExample;
import com.comerzzia.pos.persistence.cajas.conceptos.CajaConceptoMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class CajaConceptosServices {
    
    protected static final Logger log = Logger.getLogger(CajaConceptosServices.class);

    protected static Map<String, CajaConceptoBean> conceptosCaja; // Código - CajaConceptoBean
    protected static List<CajaConceptoBean> conceptosCajaManual; 
    
    @Autowired
    protected CajaConceptoMapper cajaConceptoMapper;

    /** Carga en memoria los conceptos de caja configurados para el sistema.
     * @param uidActividad UidActividad con la que trabaja la aplicación
     * @throws CajaConceptosServiceException 
     */
    public void cargarConceptosCaja(String uidActividad) throws CajaConceptosServiceException {
        SqlSession sqlSession = new SqlSession();
        try {
            // inicializamos objetos
            conceptosCaja = new HashMap<>();
            conceptosCajaManual = new ArrayList<>();
            
            // consultamos la base de datos
            sqlSession.openSession(SessionFactory.openSession());
            CajaConceptoExample exampleCajaConcepto = new CajaConceptoExample();
            exampleCajaConcepto.or().andActivoEqualTo(Boolean.TRUE).andUidActividadEqualTo(uidActividad);

            log.debug("cargarConceptosCaja() - Cargando conceptos de caja para el uidActividad " + uidActividad);
            List<CajaConceptoBean> listaConceptos = cajaConceptoMapper.selectByExample(exampleCajaConcepto);
            
            // construimos mapa y listas
            for (CajaConceptoBean concepto : listaConceptos) {
                conceptosCaja.put(concepto.getCodConceptoMovimiento(), concepto);
                if (concepto.getManual()){
                    conceptosCajaManual.add(concepto);
                }
            }
        }
        catch (Exception e) {
            String msg = "Se ha producido un error cargando los conceptos de caja : " + e.getMessage();
            log.error("cargarConceptosCaja() - " + msg, e);
            throw new CajaConceptosServiceException(e);
        }
        finally {
            sqlSession.close();
        }
    }

    /** Devuelve la lista de conceptos de caja manuales cargados en sesión.
     * @return  Lista de conceptos de caja manuales
     */
    public List<CajaConceptoBean> getConceptosCajaManual(){
        return conceptosCajaManual;
    }

    /** Devuelve el concepto de caja cargado en sesión con el código indicado. 
     * @param codConcepto
     * @return CajaConceptoBean
     */
    public CajaConceptoBean getConceptoCaja(String codConcepto){
        return conceptosCaja.get(codConcepto);
    }
    
    
    /**
     * Consulta un concepto activo por su código de concepto
     * @param codConcepto
     * @return
     * @throws CajaConceptosServiceException 
     */
    public CajaConceptoBean consultarConcepto(String codConcepto) throws CajaConceptosServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            CajaConceptoExample exampleCajaConcepto = new CajaConceptoExample();
            exampleCajaConcepto.or().andActivoEqualTo(Boolean.TRUE).andCodConceptoMovimientoEqualTo(codConcepto);
            log.debug("consultaConcepto() - consultando los conceptos de caja registrados en el sistema con código: "+codConcepto);
            List<CajaConceptoBean> res = cajaConceptoMapper.selectByExample(exampleCajaConcepto);            
            if (res!=null){
                return res.get(0);
            }
            return null;
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando concepto de cajas activo, código: "+codConcepto +" - " + e.getMessage();
            log.error("consultaConcepto() - "+ msg);
            throw new CajaConceptosServiceException();
        }
        finally{
            sqlSession.close();
        }
    }
}
