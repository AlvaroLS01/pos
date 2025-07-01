package com.comerzzia.pos.util.i18n;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    private static ResourceBundle resourceBundleFXML;
    
    private static com.comerzzia.core.commons.i18n.I18N i18nService;
    
    static{
    	init();
    }

	public static void init() {
        resourceBundleFXML = new ResourceBundle() {
			
			@Override
			public boolean containsKey(String key) {
				return true;
			}

			@Override
			protected Object handleGetObject(String key) {
				return I18N.getText(key);
			}
			
			@Override
			public Enumeration<String> getKeys() {
				return Collections.enumeration(Collections.<String>emptyList());
			}
		};
	}
    
    /**
     * Returns resource bundle from FXML translations
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return resourceBundleFXML;
    }

	protected static String getTextLocale(String text, Locale locale) {
		if (i18nService == null) {
			i18nService = com.comerzzia.core.commons.i18n.I18N.get();
		}

		if (text == null) {
			return null;
		}
		text = i18nService.getMessage(text, locale);

		return text;
	}
    
    public static String getText(String str) {
    	return getTextLocale(str, Locale.getDefault());
    }
    
    public static String getText(String str, Object...args) {
    	return MessageFormat.format(getText(str), args);
    }    
}
