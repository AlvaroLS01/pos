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

package com.comerzzia.pos.services.core.sesion;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.core.listeners.ListenersExecutor;
import com.comerzzia.pos.services.core.listeners.POSListenerException;
import com.comerzzia.pos.services.core.listeners.tipos.sesion.SesionListener;
import com.comerzzia.pos.services.core.usuarios.UsuarioInvalidLoginException;
import com.comerzzia.pos.util.config.SpringContext;

@Component
public class Sesion {
    
    // Logger
    protected static final Logger log = Logger.getLogger(Sesion.class.getName());
    
    @Autowired
    protected SesionAplicacion aplicacion;
    @Autowired
    protected SesionImpuestos impuestos;
    @Autowired
    protected SesionUsuario sesionUsuario;
    @Autowired
    protected SesionCaja sesionCaja;
    @Autowired
    protected SesionPromociones sesionPromociones;
    
    protected Sesion(){
    }

    public void initAplicacion() throws SesionInitException{
        log.info("initAplicacion() - Inicializando sesión de aplicacion...");
        aplicacion.init();
        impuestos.init();
        sesionCaja.init();
        log.info("initAplicacion() - Sesión de aplicación inicializada correctamente.");
        
        try {
	        SpringContext.getBean(ListenersExecutor.class).executeListeners(SesionListener.class);
        }
        catch (POSListenerException e) {
        	throw new SesionInitException(e);
        }
    }
    
    public void initUsuarioSesion(String usuario, String password) throws SesionInitException, UsuarioInvalidLoginException {
        log.debug("initUsuarioSesion() - Inicializando sesión de usuario...");
        sesionUsuario.init(usuario, password);
    }

    public void closeUsuarioSesion() {
        log.debug("closeUsuarioSesion() - Cerrando sesión de usuario");
        sesionUsuario.clear();
    }

    public SesionAplicacion getAplicacion() {
        return aplicacion;
    }

    public SesionImpuestos getImpuestos() {
        return impuestos;
    }

    public SesionUsuario getSesionUsuario() {
        return sesionUsuario;
    }

    public SesionCaja getSesionCaja() {
        return sesionCaja;
    }

    public SesionPromociones getSesionPromociones(){
        return sesionPromociones;
    }
    
}
