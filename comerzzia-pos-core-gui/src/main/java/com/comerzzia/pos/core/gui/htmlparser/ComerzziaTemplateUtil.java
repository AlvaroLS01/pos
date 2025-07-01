package com.comerzzia.pos.core.gui.htmlparser;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.Logger;

import com.comerzzia.pos.util.config.AppConfig;

public class ComerzziaTemplateUtil {

	protected static Logger log = Logger.getLogger(ComerzziaTemplateUtil.class);

	public static final String PATH_SCREEN_TEMPLATES = "/screen_templates/";

	public static String toExternalForm(String path) {
		path = ComerzziaTemplateUtil.class.getResource(path).toExternalForm();
		log.debug("toExternalForm() - " + path);
		return path;
	}

	public static String getResourceFromSkin(String path) {
		String basepath = "";
		path = (path.startsWith("/")?path.replaceFirst("/", ""):path);
		try {
			Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
			URL url = classLoader.getResource("skins/"+AppConfig.getCurrentConfiguration().getSkin()+PATH_SCREEN_TEMPLATES+path);
			if(url==null) {
				url = classLoader.getResource("skins/"+AppConfig.getCurrentConfiguration().getDEFAULT_SKIN()+PATH_SCREEN_TEMPLATES+path);
			}
			basepath = url.getPath();
		}catch(Exception e) {
			log.debug("getResourceFromSkin() - Error loading resource from skin: "+path, e);
		}
		
		if(basepath.startsWith("/")) {
			basepath = basepath.replaceFirst("/", "");
		}
		return basepath;
	}

}
