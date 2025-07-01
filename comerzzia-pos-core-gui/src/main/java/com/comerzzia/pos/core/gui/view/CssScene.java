package com.comerzzia.pos.core.gui.view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.config.AppConfigData;

import javafx.beans.NamedArg;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class CssScene extends Scene {
	protected static List<String> baseCSSs;
	
	public CssScene(@NamedArg("root") Parent root) {
		super(root);
		addBaseCSS(this);
	}
	
	public CssScene(@NamedArg("root") Parent root, @NamedArg("width") double width, @NamedArg("height") double height) {
		super(root, width, height);
		addBaseCSS(this);
	}
        
	
	protected void addBaseCSS(Scene scene) {
		AppConfigData appConfig = AppConfig.getCurrentConfiguration();
		
		if (baseCSSs == null) {
			// Cacheamos la lista de Strings para no llamar a getResource cada vez.
			ArrayList<String> cssList = new ArrayList<>();
			String stylesCss;
			String keyboardCss;
			File f = null;
			String cmzHomePath = appConfig.getCmzHomePath();
			if (cmzHomePath != null) {
				if (!StringUtils.equals(appConfig.getDEFAULT_SKIN(), appConfig.getSkin())) {
					stylesCss = cmzHomePath + "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/styles.css";
					f = new File(stylesCss);
					if (f != null && f.exists()) {
						cssList.add("file:///" + stylesCss);
					}
					keyboardCss = cmzHomePath + "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
					f = new File(keyboardCss);
					if (f != null && f.exists()) {
						cssList.add("file:///" + keyboardCss);
					}
				}
				stylesCss = cmzHomePath + "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/styles.css";
				f = new File(stylesCss);
				if (f != null && f.exists()) {
					cssList.add("file:///" + stylesCss);
				}
				keyboardCss = cmzHomePath + "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
				f = new File(keyboardCss);
				if (f != null && f.exists()) {
					cssList.add("file:///" + keyboardCss);
				}
			}
			else {
				URL resource;
				if (!StringUtils.equals(appConfig.getDEFAULT_SKIN(), appConfig.getSkin())) {
					stylesCss = "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/styles.css";
					resource = CssScene.class.getResource(stylesCss);
					if (resource != null) {
						cssList.add(stylesCss);
					}
					keyboardCss = "/skins/" + appConfig.getDEFAULT_SKIN() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
					resource = CssScene.class.getResource(stylesCss);
					if (resource != null) {
						cssList.add(keyboardCss);
					}
				}
				String defaultSkin = "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/styles.css";
				resource = CssScene.class.getResource(defaultSkin);
				if (resource != null) {
					cssList.add(defaultSkin);
				}
				keyboardCss = "/skins/" + appConfig.getSkin() + "/com/comerzzia/pos/gui/styles/KeyboardButtonStyle.css";
				resource = CssScene.class.getResource(defaultSkin);
				if (resource != null) {
					cssList.add(keyboardCss);
				}
			}

			baseCSSs = cssList;
		}
		for (String css : baseCSSs) {
			scene.getStylesheets().add(css);
		}
	}
}
