package com.comerzzia.pos.core.services.audit;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.service.audit.ComerzziaAuditEvent;
import com.comerzzia.core.service.audit.ComerzziaAuditJsonLayout;
import com.comerzzia.pos.core.services.session.Session;

public class ComerzziaPOSAuditJsonLayout extends ComerzziaAuditJsonLayout {
	private static final long serialVersionUID = 1L;
	
	protected Session session;
	
	protected Session getSession() {
		if (session != null) return session;
		
		try {
			session = CoreContextHolder.get().getBean(Session.class);
		} catch (Throwable ignore) {
			session = null;
		}
		
		return session;
	}
	
	@Override
	public void formatComerzziaEvent(ComerzziaAuditEvent event) {
		Session sesion = getSession();
		
		if (sesion != null) {			
			event.setStoreCode(sesion.getApplicationSession().getCodAlmacen());
			event.setTillCode(sesion.getApplicationSession().getTillCode());
		}
	}
}
