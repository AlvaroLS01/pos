package com.comerzzia.pos.util.config;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppConfigData {	
    protected Boolean developerMode = false;
    protected InterfaceInfo interfaceInfo = new InterfaceInfo();
    protected DeveloperModeInfo developerModeInfo = new DeveloperModeInfo();
    protected final String DEFAULT_SKIN = "standard";
	protected String skin = DEFAULT_SKIN;
    protected Long mainAction = 4000l;
    protected Boolean buttonsLogin = false;
    protected String country;
    protected String language;
    protected Boolean languageSelection = false;
    protected String application = "JPOS";
    protected String menu = "POS";
    protected Integer inactivityTimer = 0;
    protected String imagesPath;
    protected String defaultImage;
    protected String siteItemUrl;
    protected String dateFormat;
    protected String timeFormat;
    protected String decimalSeparator;
    protected String groupSeparator;
    protected Integer decimalNumbers;
    protected String iseRestUrl = "http://127.0.0.1:8080/comerzzia-inStoreEngine/ws/";
    protected long notificationsRequestSeconds = 30;
    protected long weightRequestMilliseconds = 300;
    protected String springContextPackagesBasePath = null;
    protected Boolean backgroundModalsEffect = true;
    protected Boolean showPreloader = true;
    protected Boolean showAlphanumericKeyboard = false;
    protected SystemProperties systemProperties = new SystemProperties();
    protected String salesDocument;
    protected KeyCodesInfo keyCodesInfo;
    protected List<String> hiddeButtons = new ArrayList<String>();
    protected Long loyaltyAction = 4021l;
    protected String applicationClassName; 
    protected Boolean showCashOpeningUser = true;
    protected String cmzHomePath;
    protected VirtualKeyboard virtualKeyboard;
}
