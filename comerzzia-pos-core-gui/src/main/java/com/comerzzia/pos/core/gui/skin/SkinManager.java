package com.comerzzia.pos.core.gui.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;
import com.comerzzia.pos.util.config.Variables;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class SkinManager {
	
	@Autowired
	protected ResourcePatternResolver resourcePatternResolver;

	private static SkinManager instance;
		
	public static SkinManager getInstance() {
		if (instance == null) {
			instance = (SkinManager)CoreContextHolder.get().getBean("skinManager");
		}
		return instance;
	}
	
	public Image getImage(String path) {
		InputStream is = null;
		Image i = null;
		AppConfigData appConfig = AppConfig.getCurrentConfiguration();
		
		try {
			if (StringUtils.isNotBlank(appConfig.getCmzHomePath())) {
				File imageFile = new File(appConfig.getCmzHomePath() + "/skins/" + appConfig.getSkin() + Variables.IMAGES_BASE_PATH + path);
				try {
					is = new FileInputStream(imageFile);
				}
				catch (FileNotFoundException e) {
					log.warn("No se encontró la imagen dentro del directorio config: " + imageFile.toURI());
				}
				if (is == null) {
					imageFile = new File(appConfig.getCmzHomePath() + "/skins/" + appConfig.getDEFAULT_SKIN() + Variables.IMAGES_BASE_PATH + path);
					try {
						is = new FileInputStream(imageFile);
					}
					catch (FileNotFoundException e) {
						log.warn("No se encontró la imagen dentro del directorio config: " + imageFile.toURI());
					}
				}
			}
			if (is == null) {
				is = SkinManager.class.getResourceAsStream("/skins/" + appConfig.getSkin() + Variables.IMAGES_BASE_PATH + path);
			}
			if (is == null) {
				is = SkinManager.class.getResourceAsStream("/skins/" + appConfig.getDEFAULT_SKIN() + Variables.IMAGES_BASE_PATH + path);
			}
			if (is != null) {
				i = new Image(is);
			}

		}
		catch (Exception e) {
			log.warn("No se encontró la imagen dentro del directorio config: " + path);
		}
		finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			}
			catch (IOException e) {
				log.warn("createImage() - Error al cerrar InputStream " + e.getMessage(), e);
			}
		}
		return i;
	}

	public URL getResource(String path) {

		URL url = null;
		AppConfigData appConfig = AppConfig.getCurrentConfiguration();

		File skinFile = null;
		if (StringUtils.isNotBlank(appConfig.getCmzHomePath())) {
			skinFile = new File(appConfig.getCmzHomePath() + "/skins/" + appConfig.getSkin() + "/" + path);
			if (skinFile != null && skinFile.exists()) {
				try {
					url = skinFile.toURI().toURL();
				}
				catch (MalformedURLException e) {
					log.warn("Error recuperando el recurso de la ruta: " + skinFile.toURI());
				}
			}
			else {
				skinFile = new File(appConfig.getCmzHomePath() + "/skins/" + appConfig.getDEFAULT_SKIN() + "/" + path);
				if (skinFile != null && skinFile.exists()) {
					try {
						url = skinFile.toURI().toURL();
					}
					catch (MalformedURLException e) {
						log.warn("Error recuperando el recurso de la ruta: " + skinFile.toURI());
					}
				}
			}
		}
		if (url == null) {
			url = SkinManager.class.getResource("/skins/" + appConfig.getSkin() + "/" + path);
			if (url == null) {
				url = SkinManager.class.getResource("/skins/" + appConfig.getDEFAULT_SKIN() + "/" + path);
			}
		}

		log.debug("getResource() - URL: " + (url == null ? "null" : url.toExternalForm()));
		return url;
	}
	
	@PostConstruct
	protected void loadFonts() {
		loadFonts("com/comerzzia/pos/gui/fonts/");
		loadFonts("com/comerzzia/pos/gui/ext-fonts/");
	}
	
	protected void loadFonts(String folder) {
		try {
			Resource[] fonts = resourcePatternResolver.getResources(getResource(folder)+"*");
			if(fonts != null && fonts.length > 0) {
				log.debug("Loading " + fonts.length + " fonts from " + folder);
				for (Resource fontResource : fonts) {
					try (InputStream is = fontResource.getInputStream()) {
						double size = 50;
						Font.loadFont(is, size);
					}
				}
			}
		} catch (IOException e) {
			log.error("An error was thrown loading font: " + e.getMessage(), e);
		}			
	}

}
