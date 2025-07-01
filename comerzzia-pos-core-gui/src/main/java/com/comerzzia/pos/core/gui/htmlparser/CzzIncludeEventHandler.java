package com.comerzzia.pos.core.gui.htmlparser;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.event.IncludeEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;

import lombok.extern.log4j.Log4j;

@Log4j
public class CzzIncludeEventHandler implements IncludeEventHandler, RuntimeServicesAware {

	private RuntimeServices rs;
	
	@Override
	public void setRuntimeServices(RuntimeServices rs) {
		this.rs = rs;
	}

	@Override
	public String includeEvent(Context context, String includeResourcePath, String currentResourcePath, String directiveName) {
		String includePath = rs.getLoaderNameForResource(includeResourcePath);
		if(StringUtils.isBlank(includePath)) {
			log.warn("includeEvent() - Unable to find resource "+includeResourcePath);
		}
		return includePath != null ? includeResourcePath : null;
	}

}
