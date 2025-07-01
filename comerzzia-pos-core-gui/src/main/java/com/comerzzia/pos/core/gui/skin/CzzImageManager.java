package com.comerzzia.pos.core.gui.skin;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.services.session.ApplicationSession;

import javafx.scene.image.Image;
import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class CzzImageManager {
	
	@Autowired
	protected SkinManager skinManager;
	@Autowired
	protected ApplicationSession sesionAplicacion;
	
	
	public static final String IMAGES_BASE_PATH = "/images/";
	public static final String IMAGES_LOGOS_PATH = "logos/";
	
	public static final String IMAGES_CUSTOMER_LOGO_NAME = "header-logo.png";
	public static final String IMAGES_MAIN_LOGO_NAME = "main-logo.png";
	public static final String IMAGES_CZZ_LOGO_NAME = "logo_comerzzia.png";
	public static final String IMAGES_CZZ_WHITE_LOGO_NAME = "logo-czz-white.png";

	public InputStream getImageStream(String path, String name) {
		return CzzImageManager.class.getResourceAsStream(path+name);
	}
	
	public Image getLogo(String name) {
		Image res = null;
		try {
			if(sesionAplicacion.getStorePosBusinessData()!=null) {
				String companyCode = sesionAplicacion.getCompany().getCompanyCode();
				String companyLogoName = companyCode+"-"+name;
				
				log.debug("getLogo() - Searching logo path: "+companyLogoName);
				try (InputStream is = getImageStream(IMAGES_BASE_PATH+IMAGES_LOGOS_PATH, companyLogoName)) {				
					if(is != null) {
						log.debug("getLogo() - Found logo in image resource path");
						res = new Image(is);
					}
				}
				
				if(res == null) {
					log.debug("getLogo() - Loading logo from skin: "+companyLogoName);
					res = skinManager.getImage(IMAGES_LOGOS_PATH+companyLogoName);
				}
			}
			
			if(res == null) {
				log.debug("getLogo() - Searching logo path: "+name);
				try (InputStream is = getImageStream(IMAGES_BASE_PATH+IMAGES_LOGOS_PATH, name)) {				
					if(is != null) {
						log.debug("getLogo() - Found logo in image resource path");
						res = new Image(is);
					}
					
					if(res == null) {
						log.debug("getLogo() - Loading logo from skin: "+name);
						res = skinManager.getImage(IMAGES_LOGOS_PATH+name);
					}
				}
			}
		}catch(Throwable e) {
			log.warn("getLogo() - An error was thrown trying to load the logo.", e);
		}
		
		return res;
	}

}
