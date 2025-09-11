package com.comerzzia.iskaypet.pos.api.rest.path;

import com.comerzzia.api.rest.path.BackofficeWebservicesPath;

public class IskaypetBackofficeWebservicesPath extends BackofficeWebservicesPath {

	public static String servicio = null;

	public static final String ISKAYPET_PATH = "/iskaypet";

	public static final String servicioFidelizadoFidelizados = ISKAYPET_PATH + "/fidelizados/fidelizado";

	public static final String servicioFidelizados = ISKAYPET_PATH + "/fidelizados/{id_fidelizado}";
	public static final String servicioFidelizadoTarjetaRegalo = ISKAYPET_PATH + "/fidelizados/tarjetaRegalo";
	public static final String servicioFidelizadoFidelizadosDatos = ISKAYPET_PATH + "/fidelizados/fidelizadoDatos";
	public static final String servicioFidelizadoTarjetaMovimientos = servicioFidelizados + "/tarjetas/{id_tarjeta}/movimientos";

	public IskaypetBackofficeWebservicesPath() {
	}

	public static void initPath(String basePath) {
		servicio = basePath;
	}
}
