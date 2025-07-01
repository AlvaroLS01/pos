package com.comerzzia.pos.core.gui.htmlparser;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeConstants.SpaceGobbling;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.core.services.config.EnvironmentSelector;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.base64.Base64Coder;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.Variables;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.text.TextUtils;

@Component
public class ScreenTemplateParser {

    public static final String DEFAULT_ENDING = "view";
	
	private Logger log = Logger.getLogger(ScreenTemplateParser.class);

	@Autowired
	protected Session session;
	
	@Autowired
	protected ApplicationSession applicationSession;
	
	@Autowired
	protected SkinManager skinManager;

    protected Map<String, Template> templateCache = new HashMap<>();
    protected VelocityEngine velocityEngine;
    
    protected FormatUtils formatUtils;
    protected Currency currency;

	public String parser(String templatePath, Map<String, Object> params) {
		try {
			log.debug("getHtml() - Generando documento a partir de plantilla velocity: " + templatePath);

			// Buscamos la plantilla. Primero con el locale y si no existe, por defecto.
			Template template = getTemplate(templatePath);

			Context context = new VelocityContext();
			// Pasamos a la plantilla los parámetros que la alimentan
			addParameters(params, context);

			// Aplicamos a la plantilla las variables
			try (StringWriter writer = new StringWriter()) {
				template.merge(context, writer);
				String html = writer.toString();
				
				saveDebugFile(html, templatePath);
				
				return html;
			}
		} catch (Exception e) {
			log.error("parser() - Ha habido un error al cargar el HTML: " + e.getMessage(), e);
			return "";
		}
	}

	protected void saveDebugFile(String html, String templatePath) {
		if(AppConfig.getCurrentConfiguration().getDeveloperMode()) {
			try {
				FileUtils.writeByteArrayToFile(new File("./screen_templates_debug/" + templatePath + ".html"), replaceCzzProtocol(html).getBytes());
			} catch (Exception e) {
				log.error("saveDebugFile() - Error: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * This is a debug utility that replace all the czzpos protocol calls to paths that can be resolved with regular browsers.
	 * This utility may fail to resolve paths when a skin is configured
	 * @param html
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected String replaceCzzProtocol(String html) {
		try {
			html = html.replaceAll("czzpos:[/]{0,2}skin/resource/", getExtSkinImagePath());
			html = html.replaceAll("czzpos:[/]{0,2}images/resource/", AppConfig.getCurrentConfiguration().getImagesPath());
			html = html.replaceAll("czzpos:[/]{0,2}skin/template/", getExtTemplatePath());
		} catch(Exception e) {
			log.trace("replaceCzzProtocol() - An exception was thrown trying to parse czzpos protocol to absolute paths");
		}
		return html;
	}

	protected String getExtSkinImagePath() {
		String imagesPath = skinManager.getResource(Variables.IMAGES_BASE_PATH.replaceFirst("/", "") + "anchor.czz").getPath();
		
		if(imagesPath.startsWith("/")) {
			imagesPath = imagesPath.replaceFirst("/", "");
		}
		imagesPath = imagesPath.replaceAll("anchor.czz", "");
		return imagesPath;
	}

	protected String getExtTemplatePath() throws Exception {
		Method method = (java.lang.Thread.class).getMethod("getContextClassLoader", (Class<?>[]) null);
		ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), (Object[]) null);
		URL url = classLoader.getResource("skins/"+AppConfig.getCurrentConfiguration().getSkin()+Variables.PATH_SCREEN_TEMPLATES);
		String path = url.getPath();
		
		if(path.startsWith("/")) {
			path = path.replaceFirst("/", "");
		}
		return path;
	}

	protected void addParameters(Map<String, Object> parametros, Context context) {
		if (formatUtils == null) {
			formatUtils = FormatUtils.getInstance();
			formatUtils.init(Locale.getDefault());
		}
		if(currency == null && applicationSession.getStorePosBusinessData() != null && applicationSession.getStorePosBusinessData().getCountry().getCurrencyCode() != null) {
			currency = Currency.getInstance(applicationSession.getStorePosBusinessData().getCountry().getCurrencyCode());
		}
		
		context.put("fmt", formatUtils);
		context.put("imgresource", AppConfig.getCurrentConfiguration().getImagesPath());
		context.put("siteItemUrl", AppConfig.getCurrentConfiguration().getSiteItemUrl());
		context.put("sesion", session);
		context.put("textUtils", TextUtils.getInstance());
		context.put("StringUtils", StringUtils.class);
		context.put("Collections", Collections.class);
		context.put("MathUtils", MathUtils.class);
		context.put("ComerzziaTemplateUtil", ComerzziaTemplateUtil.class);
		context.put("I18N", I18N.class);
		context.put("base64Coder", new Base64Coder(Base64Coder.UTF8));
		context.put("CzzEnvironment", EnvironmentSelector.getCurrentEnvironment());
		context.put("currency",currency);
		
		if (AppConfig.getCurrentConfiguration().getDeveloperMode()) {
			context.put("devModeQueryParam", "?q=" + System.currentTimeMillis());
		} else {
			context.put("devModeQueryParam", "");
		}
        
		for (Entry<String, Object> entry : parametros.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
	}

    protected Template getTemplate(String plantilla) throws Exception {
        Template template = templateCache.get(plantilla);
        if (template != null && !AppConfig.getCurrentConfiguration().getDeveloperMode()) {
            log.debug("getTemplate() - Devolviendo plantilla cacheada para " + plantilla);
            return template;
        }

        long time = System.currentTimeMillis();

        VelocityEngine ve = getVelocityEngine();
        String plantillaDefault = plantilla + ".vm";

        template = ve.getTemplate(plantillaDefault, "UTF-8");

        Long timeEllapsed = System.currentTimeMillis() - time;
        log.debug(String.format("getTemplate() - Plantilla '%s' cargada en %sms", plantilla, timeEllapsed.toString()));

        templateCache.put(plantilla, template);
        return template;
    }

    protected VelocityEngine getVelocityEngine() throws Exception {
        if (velocityEngine != null && !AppConfig.getCurrentConfiguration().getDeveloperMode()) {
            return velocityEngine;
        }

        velocityEngine = new VelocityEngine();

        // Establecemos propiedades
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ComerzziaSkinResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.classpath.skin", AppConfig.getCurrentConfiguration().getSkin());
        velocityEngine.setProperty("runtime.log.logsystem.log4j.logger", log.getName());
        velocityEngine.setProperty("velocimacro.library.path", "commons/velocimacros.vm");
        velocityEngine.setProperty("event_handler.include.class", CzzIncludeEventHandler.class.getName());

        // macros definidas en un template no puede sustituir a una global
        velocityEngine.setProperty("velocimacro.inline.replace_global", false);
        // macro definida en un template solo es visible en ese template
        velocityEngine.setProperty("velocimacro.inline.local_scope", true);
        
        //Cambiamos el gobbler para evitar imprimir identaciones
        velocityEngine.setProperty(RuntimeConstants.SPACE_GOBBLING, SpaceGobbling.STRUCTURED.name());
        
        if(AppConfig.getCurrentConfiguration().getDeveloperMode()) {
        	velocityEngine.setProperty("velocimacro.library.autoreload", true);
        	velocityEngine.setProperty("resource.loader.classpath.cache", false);
        	
        }
        
        // Inicializamos
        velocityEngine.init();
        return velocityEngine;
    }

}
