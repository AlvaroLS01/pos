package com.comerzzia.iskaypet.pos.api.evicertia.paht;

public class IskaypetEvicertiaPath {

	public static String servicio = null;

	public static final String EVICERTIA_PATH = "https://app.ecertia.com/api";

	public static final String envioContratoUnico = EVICERTIA_PATH + "/EviSign/Submit";

	public IskaypetEvicertiaPath() {
	}

	public static void initPath(String basePath) {
		servicio = basePath;
	}
}
