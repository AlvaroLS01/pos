package com.comerzzia.pos.core.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.comerzzia.pos.core.gui.skin.SkinManager;
import com.comerzzia.pos.util.config.Variables;

import lombok.extern.log4j.Log4j;

@Log4j
public class CzzSkinResourceUrlConnection extends URLConnection {
	InputStream is;

	protected CzzSkinResourceUrlConnection(URL url) {
		super(url);
	}
	
	@Override
	public void connect() throws IOException {
		if (this.connected) {
			return;
		}
				
		try {
			URL finalUrl = SkinManager.getInstance().getResource(getResourcePath());
			is = finalUrl.openStream();
		} catch (IOException e) {
			log.warn("Error opening resource : " + getResourcePath());
			throw e;
		}
		
		this.connected = true;
	}


	@Override
	public synchronized InputStream getInputStream() throws IOException {
		connect();
		
		return is;
	}

	protected String getResourcePath() {
		String resourcePath = "";
		String imgPath = this.url.toExternalForm();
		imgPath = imgPath.startsWith("czzpos://skin/") ? imgPath.substring("czzpos://skin/".length())
				: imgPath.substring("czzpos:skin/".length());
		
		if(imgPath.contains("resource")) {
			resourcePath = Variables.IMAGES_BASE_PATH.replaceFirst("/", "");
		}else if(imgPath.contains("template")){
			resourcePath = Variables.PATH_SCREEN_TEMPLATES.replaceFirst("/", "");
		}else {
			resourcePath = "";
		}
		imgPath = imgPath.substring(imgPath.indexOf("/")+1);
		if(imgPath.contains("?")) {
			imgPath = imgPath.substring(0, imgPath.indexOf("?"));
		}
		resourcePath = resourcePath + imgPath;
		
		return resourcePath;
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
        return new ByteArrayOutputStream();
    }

	@Override
    public java.security.Permission getPermission() throws IOException {
        return null; 
    }
	
	@Override
	public String getContentType() {
        return guessContentTypeFromName(getResourcePath());
    }

	@Override
    public boolean getDoInput() {
        return true;
    }
	    
}
