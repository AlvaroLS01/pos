package com.comerzzia.pos.gui.i18n;

import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.ApplicationListener;
import com.comerzzia.pos.util.config.AppConfig;
import com.comerzzia.pos.util.i18n.I18N;

@Component
public class I18NApplicationListener implements ApplicationListener {

	@Override
	public void onBeforeApplicationInit() {
		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.DEFAULT_SKIN + ".com.comerzzia.pos.gui.i18n.cmz-pos");
		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.DEFAULT_SKIN + ".com.comerzzia.backstore.gui.i18n.cmz-backstore");
		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.DEFAULT_SKIN + ".com.comerzzia.pos.services.i18n.cmz-pos-services");
		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.DEFAULT_SKIN + ".com.comerzzia.pos.devices.i18n.cmz-pos-devices");
		if(!AppConfig.DEFAULT_SKIN.equals(AppConfig.skin)){
    		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.skin + ".com.comerzzia.pos.gui.i18n.cmz-pos");
    		I18N.addTranslationPropertiesBaseNameExt("skins." + AppConfig.skin + ".com.comerzzia.pos.gui.i18n.cmz-pos-ext");
    		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.skin + ".com.comerzzia.backstore.gui.i18n.cmz-backstore");
    		I18N.addTranslationPropertiesBaseNameExt("skins." + AppConfig.skin + ".com.comerzzia.backstore.gui.i18n.cmz-backstore-ext");
    		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.skin + ".com.comerzzia.pos.services.i18n.cmz-pos-services");
    		I18N.addTranslationPropertiesBaseNameExt("skins." + AppConfig.skin + ".com.comerzzia.pos.services.i18n.cmz-pos-services-ext");
    		I18N.addTranslationPropertiesBaseName("skins." + AppConfig.skin + ".com.comerzzia.pos.devices.i18n.cmz-pos-devices");
    		I18N.addTranslationPropertiesBaseNameExt("skins." + AppConfig.skin + ".com.comerzzia.pos.devices.i18n.cmz-pos-devices-ext");
    	}
	}

	@Override
	public void onAfterApplicationInit() {

	}

	@Override
	public void onBeforeApplicationStart() {

	}

	@Override
	public void onAfterApplicationStart() {
	}

	@Override
	public void onBeforeApplicationClose() {
	}

}
