package com.comerzzia.pos.core.devices;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceConfigKey;
import com.comerzzia.pos.core.devices.device.cashdrawer.DeviceCashDrawerDummy;
import com.comerzzia.pos.core.devices.device.fiscal.DeviceFiscalDummy;
import com.comerzzia.pos.core.devices.device.linedisplay.DeviceLineDisplayDummy;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCardDummy;
import com.comerzzia.pos.core.devices.device.mobilephonerecharge.DeviceMobilePhoneRechargeDummy;
import com.comerzzia.pos.core.devices.device.printer.DevicePrinterDummy;
import com.comerzzia.pos.core.devices.device.scale.DeviceScaleDummy;
import com.comerzzia.pos.core.devices.device.scanner.DeviceScannerDummy;
import com.comerzzia.pos.util.i18n.I18N;

public class DeviceType {
	public static final String LINE_DISPLAY = "visor";
	public static final String SCALE = "balanza";
	public static final String LOYALTY_CARD = "fidelizacion";
	public static final String MOBILE_PHONE_RECHARGE = "recarga_movil";
	public static final String SCANNER = "escaner";	
	public static final String PRINTER1 = "impresora1";
	public static final String PRINTER2 = "impresora2";
	public static final String CASH_DRAWER = "cajon";
	public static final String FISCAL = "fiscal";
	
	public static final String TAG_LINE_DISPLAYS = "lineDisplays";
	public static final String TAG_SCALES = "scales";
	public static final String TAG_LOYALTYCARDS = "loyaltyCards";
	public static final String TAG_MOBILE_PHONE_RECHARGES = "mobilePhoneRecharges";
	public static final String TAG_SCANNERS = "scanners";
	public static final String TAG_PRINTERS = "printers";
	public static final String TAG_CASH_DRAWERS = "cashDrawers";
	public static final String TAG_FISCAL_DEVICE = "fiscalDevices";
	
	protected final String key;
	protected String description;
	protected Set<DeviceConfigKey> configurationTags = new LinkedHashSet<>();
	
	protected Class<? extends DeviceAbstractImpl> dummyImplementation;
	
	public DeviceType(String key, String label) {
		this.key = key;
		this.description = label;
		
		if (StringUtils.isBlank(description)) {
			applyDefaultDescription();
		}
		
		applyDefaultConfigTags();
		
		applyDefaultDummyImplementation();
	}
	
	protected void applyDefaultDescription() {
		// compatibilidad
		switch (key) {
		case TAG_LINE_DISPLAYS:
			this.description = I18N.getText("Visor");
			break;
		case TAG_SCALES:
			this.description = I18N.getText("Balanza");
			break;
		case TAG_LOYALTYCARDS:
			this.description = I18N.getText("Fidelizado");
			break;
		case TAG_MOBILE_PHONE_RECHARGES:
			this.description = I18N.getText("Recarga móvil");
			break;
		case TAG_SCANNERS:
			this.description = I18N.getText("Escáner");
			break;
		case TAG_PRINTERS:
			this.description = I18N.getText("Impresora");
			break;
		case TAG_CASH_DRAWERS:
			this.description = I18N.getText("Cajón");
			break;
		case TAG_FISCAL_DEVICE:
			this.description = I18N.getText("Fiscal");
			break;
		default:
			this.description = key;
		}		
	}
	
	protected void applyDefaultConfigTags() {		
		switch (key) {
		case TAG_LINE_DISPLAYS:
			this.configurationTags.add(new DeviceConfigKey(LINE_DISPLAY, this.description));
			break;
		case TAG_SCALES:
			this.configurationTags.add(new DeviceConfigKey(SCALE, this.description));
			break;
		case TAG_LOYALTYCARDS:
			this.configurationTags.add(new DeviceConfigKey(LOYALTY_CARD, this.description));
			break;
		case TAG_MOBILE_PHONE_RECHARGES:
			this.configurationTags.add(new DeviceConfigKey(MOBILE_PHONE_RECHARGE, this.description));
			break;
		case TAG_SCANNERS:
			this.configurationTags.add(new DeviceConfigKey(SCANNER, this.description));
			break;
		case TAG_PRINTERS:
			this.configurationTags.add(new DeviceConfigKey(PRINTER1, this.description));
			this.configurationTags.add(new DeviceConfigKey(PRINTER2, I18N.getText("Impresora 2")));
			break;
		case TAG_CASH_DRAWERS:
			this.configurationTags.add(new DeviceConfigKey(CASH_DRAWER, this.description));
			break;
		case TAG_FISCAL_DEVICE:
			this.configurationTags.add(new DeviceConfigKey(FISCAL, this.description));
			break;
		default:
			this.configurationTags.add(new DeviceConfigKey(key, description));	
		}
		
	}
	
	protected void applyDefaultDummyImplementation() {		
		switch (key) {
		case TAG_LINE_DISPLAYS:
			this.dummyImplementation = DeviceLineDisplayDummy.class;
			break;
		case TAG_SCALES:
			this.dummyImplementation = DeviceScaleDummy.class;
			break;
		case TAG_LOYALTYCARDS:
			this.dummyImplementation = DeviceLoyaltyCardDummy.class;
			break;
		case TAG_MOBILE_PHONE_RECHARGES:
			this.dummyImplementation = DeviceMobilePhoneRechargeDummy.class;
			break;
		case TAG_SCANNERS:
			this.dummyImplementation = DeviceScannerDummy.class;
			break;
		case TAG_PRINTERS:
			this.dummyImplementation = DevicePrinterDummy.class;
			break;
		case TAG_CASH_DRAWERS:
			this.dummyImplementation = DeviceCashDrawerDummy.class;
			break;
		case TAG_FISCAL_DEVICE:
			this.dummyImplementation = DeviceFiscalDummy.class;
			break;
		}		
	}
	
	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Set<DeviceConfigKey> getConfigurationTags() {
		return configurationTags;
	}

	public void setConfigurationTags(Set<DeviceConfigKey> configurationTags) {
		this.configurationTags = configurationTags;
	}

	public Class<? extends DeviceAbstractImpl> getDummyImplementation() {
		return dummyImplementation;
	}

	public void setDummyImplementation(Class<? extends DeviceAbstractImpl> dummyImplementation) {
		this.dummyImplementation = dummyImplementation;
	}
	
	@Override
	public String toString() {
		return key;
	}
	
}
