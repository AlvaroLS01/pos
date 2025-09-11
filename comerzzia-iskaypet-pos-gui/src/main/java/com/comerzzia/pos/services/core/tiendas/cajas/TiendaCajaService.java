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

package com.comerzzia.pos.services.core.tiendas.cajas;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.comerzzia.core.util.mybatis.session.SqlSession;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaBean;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaExample;
import com.comerzzia.pos.persistence.core.tiendas.cajas.TiendaCajaMapper;
import com.comerzzia.pos.persistence.mybatis.SessionFactory;

@Service
public class TiendaCajaService {

    protected static Logger log = Logger.getLogger(TiendaCajaService.class);

	@Autowired
	protected TiendaCajaMapper tiendaCajaMapper;
    
    /** Consulta en BBDD la caja TPV de la tienda asociada al uidCaja y uidActividad indicados.
     * Si no existe, lanza una excepción.
     * @param uidActividad
     * @param uidCaja
     * @return TiendaCajaBean
     * @throws TiendaCajaNotFoundException (Si no existe)
     * @throws TiendaCajaServiceException 
     */
    public TiendaCajaBean consultarTPV(String uidActividad, String uidCaja) throws TiendaCajaNotFoundException, TiendaCajaServiceException{
        SqlSession sqlSession = new SqlSession();
        try{
            sqlSession.openSession(SessionFactory.openSession());
            
            log.debug("consultarTPV() - consultado caja de tienda uidCaja "+ uidCaja);
            TiendaCajaBean tiendaCaja = tiendaCajaMapper.selectByPrimaryKey(uidCaja);

            if(tiendaCaja != null){
                if(tiendaCaja.getUidActividad().equals(uidActividad) && tiendaCaja.getActivo()){
                    return tiendaCaja;    
                }
            }
            throw new TiendaCajaNotFoundException();
        }
        catch(TiendaCajaNotFoundException e){
            String msg = "No existe caja de tienda : " + e.getMessage();
            log.error("consultarTPV() - "+ msg);
            throw new TiendaCajaNotFoundException();
        }
        catch(Exception e){
            String msg = "Se ha producido un error consultando la caja de tienda con uidCaja : " + e.getMessage();
            log.error("consultarTPV() - "+ msg);
            throw new TiendaCajaServiceException(e);
        }
        finally{
            sqlSession.close();
        }
    }
    
    public void grabarConfiguracionDispositivos(TiendaCajaBean tienda) throws TiendaCajaServiceException{
        
        SqlSession sqlSession = new SqlSession();
        try {
        	log.debug("grabarConfiguracionDispositivos() - Guardando la configuración del dispositivo");
            log.trace(new String(tienda.getConfiguracion(),"UTF-8"));
            sqlSession.openSession(SessionFactory.openSession());
            
            int result = tiendaCajaMapper.updateByPrimaryKeyWithBLOBs(tienda);
        }
        catch (UnsupportedEncodingException ex) {
            log.error("grabarConfiguracionDispositivos() - Error en la codificacion del xml.");
            throw new TiendaCajaServiceException(ex);
        }catch(Exception e){
            String msg = "Se ha producido un error consultando la caja de tienda con uidCaja : " + e.getMessage();
            log.error("grabarConfiguracionDispositivos() - "+ msg, e);
            throw new TiendaCajaServiceException(e);
        }finally{
            sqlSession.commit();
            sqlSession.close();
        }
    }
    
    public void actualizarUidPOS(TiendaCajaBean tiendaCajaActual, String newUidPos) throws TiendaCajaServiceException{
    	 SqlSession sqlSession = new SqlSession();
         try {
         	log.debug("actualizarUidPOS() - Actualizando UID_TPV");
             sqlSession.openSession(SessionFactory.openSession());

             TiendaCajaBean tiendaCaja = new TiendaCajaBean();
             tiendaCaja.setUidTpv(newUidPos);
             TiendaCajaExample example = new TiendaCajaExample();
             example.or()
             	.andUidActividadEqualTo(tiendaCajaActual.getUidActividad())
             	.andCodcajaEqualTo(tiendaCajaActual.getCodcaja())
             	.andCodAlmacenEqualTo(tiendaCajaActual.getCodAlmacen());
             
             tiendaCajaMapper.updateByExampleSelective(tiendaCaja, example);
         }catch(Exception e){
             String msg = "Se ha producido un error actualizando la caja de tienda con uidCaja : " + e.getMessage();
             log.error("grabarConfiguracionDispositivos() - "+ msg, e);
             throw new TiendaCajaServiceException(e);
         }finally{
             sqlSession.commit();
             sqlSession.close();
         }
    }
    
}
