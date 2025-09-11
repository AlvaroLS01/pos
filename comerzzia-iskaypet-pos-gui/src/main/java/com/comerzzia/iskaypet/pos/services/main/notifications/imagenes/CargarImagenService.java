package com.comerzzia.iskaypet.pos.services.main.notifications.imagenes;

import org.springframework.stereotype.Service;

import com.comerzzia.pos.core.gui.POSApplication;

import javafx.scene.image.Image;

@Service
public class CargarImagenService {
	
	private Image imageWarn;
	private Image imageError;
	private Image imageInfo;
	
	public Image getImageWarn() {
		if(imageWarn == null) {
			imageWarn = POSApplication.getInstance().createImage("notificaciones/warn.png");
		}
		return imageWarn;
	}
	public void setImageWarn(Image imageWarn) {
		this.imageWarn = imageWarn;
	}
	public Image getImageError() {
		if(imageError == null) {
			imageError = POSApplication.getInstance().createImage("notificaciones/error.png");
		}
		return imageError;
	}
	public void setImageError(Image imageError) {
		this.imageError = imageError;
	}
	public Image getImageInfo() {
		if(imageInfo == null) {
			imageInfo = POSApplication.getInstance().createImage("notificaciones/info.png");
		}
		return imageInfo;
	}
	public void setImageInfo(Image imageInfo) {
		this.imageInfo = imageInfo;
	}
	
	
	

}
