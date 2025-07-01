

package com.comerzzia.pos.core.services.session;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.service.user.UserInvalidLoginException;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.listeners.ListenersExecutor;
import com.comerzzia.pos.util.listeners.POSListenerException;
import com.comerzzia.pos.util.listeners.types.session.SessionListener;

@Component
public class Session {
    protected static final Logger log = Logger.getLogger(Session.class.getName());
    
    @Autowired
    protected ApplicationSession applicationSession;
    @Autowired
    protected POSUserSession posUserSession;
    @Autowired
    protected CashJournalSession cashJournalSession;
    
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;
    
    public void initAplicacion() throws SesionInitException{
        log.info("initAplicacion() - Inicializando sesión de aplicacion...");
        applicationSession.init();
        try {
        	cashJournalSession.init();
        }catch (SesionInitException e) {
        	throw new SesionInitException(e);
		}
                
        log.info("initAplicacion() - Sesión de aplicación inicializada correctamente.");
        
        try {
	        SpringContext.getBean(ListenersExecutor.class).executeListeners(SessionListener.class);
        }
        catch (POSListenerException e) {
        	throw new SesionInitException(e);
        }
        
    }
    
    public void initPosUserSession(String user, String password) throws SesionInitException, UserInvalidLoginException {
        log.debug("initUsuarioSesion() - Inicializando sesión de usuario...");
        posUserSession.init(user, password);
    }
    
    public void closeUserSession() {
        log.debug("closeUsuarioSesion() - Cerrando sesión de usuario");
        posUserSession.clear();
    }

    public ApplicationSession getApplicationSession() {
        return applicationSession;
    }

    public POSUserSession getPOSUserSession() {
        return posUserSession;
    }

    public CashJournalSession getCashJournalSession() {
        return cashJournalSession;
    }

}    	
