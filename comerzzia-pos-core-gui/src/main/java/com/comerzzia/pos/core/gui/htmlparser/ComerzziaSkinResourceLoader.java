package com.comerzzia.pos.core.gui.htmlparser;

import java.io.Reader;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.ExtProperties;

import com.comerzzia.pos.util.config.AppConfig;

public class ComerzziaSkinResourceLoader extends ClasspathResourceLoader {
	
	protected String skin;
	
	@Override
	public void init(ExtProperties configuration) {
		super.init(configuration);
		skin = configuration.getString("skin", AppConfig.getCurrentConfiguration().getDEFAULT_SKIN());
	}
	
	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {
		name = (name.startsWith("/")?name.replaceFirst("/", ""):name);
		try {
			return super.getResourceReader("skins/"+skin+"/screen_templates/"+name, encoding);
		}catch(ResourceNotFoundException e) {
			return super.getResourceReader("skins/"+AppConfig.getCurrentConfiguration().getDEFAULT_SKIN()+"/screen_templates/"+name, encoding);
		}
	}

}
