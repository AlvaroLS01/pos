package com.comerzzia.pos.core.services.api.locale;

import com.comerzzia.pos.core.services.session.Session;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ComerzziaApiLocaleInterceptor implements RequestInterceptor {
	
	protected Session session;
	
	public ComerzziaApiLocaleInterceptor(Session session) {
		this.session = session;
	}

	@Override
	public void apply(RequestTemplate template) {
		if (session.getApplicationSession().getStoreLanguageCode() != null) {
		   template.header("accept-language", session.getApplicationSession().getStoreLanguageCode().replace("_", "-"));
		}
	}

}
