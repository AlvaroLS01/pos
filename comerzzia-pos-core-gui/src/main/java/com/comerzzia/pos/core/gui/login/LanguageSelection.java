package com.comerzzia.pos.core.gui.login;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class LanguageSelection {
	
	protected Locale locale;
	
	public LanguageSelection(String locale) {
		super();
		this.locale = Locale.forLanguageTag(locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	@Override
	public String toString() {
		if(StringUtils.isNotBlank(locale.getDisplayCountry())) {
			return locale.getDisplayLanguage()+"("+locale.getDisplayCountry()+")";
		}else {
			return locale.getDisplayLanguage();
		}
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public String getScript() {
		return locale.getScript();
	}

	public String getCountry() {
		return locale.getCountry();
	}

	public String getVariant() {
		return locale.getVariant();
	}

	public String getExtension(char key) {
		return locale.getExtension(key);
	}

	public Set<Character> getExtensionKeys() {
		return locale.getExtensionKeys();
	}

	public Set<String> getUnicodeLocaleAttributes() {
		return locale.getUnicodeLocaleAttributes();
	}

	public String getUnicodeLocaleType(String key) {
		return locale.getUnicodeLocaleType(key);
	}

	public Set<String> getUnicodeLocaleKeys() {
		return locale.getUnicodeLocaleKeys();
	}

	public String toLanguageTag() {
		return locale.toLanguageTag();
	}

	public String getISO3Language() throws MissingResourceException {
		return locale.getISO3Language();
	}

	public String getISO3Country() throws MissingResourceException {
		return locale.getISO3Country();
	}

	public final String getDisplayLanguage() {
		return locale.getDisplayLanguage();
	}

	public String getDisplayLanguage(Locale inLocale) {
		return locale.getDisplayLanguage(inLocale);
	}

	public String getDisplayScript() {
		return locale.getDisplayScript();
	}

	public String getDisplayScript(Locale inLocale) {
		return locale.getDisplayScript(inLocale);
	}

	public final String getDisplayCountry() {
		return locale.getDisplayCountry();
	}

	public String getDisplayCountry(Locale inLocale) {
		return locale.getDisplayCountry(inLocale);
	}

	public final String getDisplayVariant() {
		return locale.getDisplayVariant();
	}

	public String getDisplayVariant(Locale inLocale) {
		return locale.getDisplayVariant(inLocale);
	}

	public final String getDisplayName() {
		return locale.getDisplayName();
	}

	public String getDisplayName(Locale inLocale) {
		return locale.getDisplayName(inLocale);
	}

	
}
