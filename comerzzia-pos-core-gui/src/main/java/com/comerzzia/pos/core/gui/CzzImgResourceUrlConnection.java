package com.comerzzia.pos.core.gui;

import java.io.IOException;
import java.net.URL;

import com.comerzzia.pos.util.config.AppConfig;

import lombok.extern.log4j.Log4j;

@Log4j
public class CzzImgResourceUrlConnection extends CzzSkinResourceUrlConnection {
	private final String baseImagesPath;
	
	protected CzzImgResourceUrlConnection(URL url) {
		super(url);
		
		baseImagesPath = AppConfig.getCurrentConfiguration().getImagesPath();
	}
	
	@Override
	public void connect() throws IOException {
		if (this.connected) {
			return;
		}		
		
		try {
			URL finalUrl = new URL(new URL("file:"), getResourcePath());
			is = finalUrl.openStream();
		} catch (IOException e) {
			log.warn("Error opening resource : " + getResourcePath());
			throw e;
		}
		
		this.connected = true;
	}
	
	@Override
	protected String getResourcePath() {
		String imgPath = url.toExternalForm();
		
		imgPath = imgPath.startsWith("czzpos://images/resource/") ? imgPath.substring("czzpos://images/resource/".length())
				: imgPath.substring("czzpos:images/resource/".length());
		
		return baseImagesPath + imgPath;
	}    
}
