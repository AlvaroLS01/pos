package com.comerzzia.iskaypet.pos.gui.autorizacion;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.main.MainView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class AutorizacionGerenteUtils {

	protected static Logger log = Logger.getLogger(AutorizacionGerenteController.class);

	public static final  String PARAMETRO_REQUIERE_GERENTE = "requiereGerente";

	public static void muestraPantallaAutorizacion(MainView mainView, Stage stage, HashMap<String, Object> datos ) throws InitializeGuiException {
		log.debug("muestraPantallaAutorizacion()");

		mainView.showModalCentered(AutorizacionGerenteView.class, datos, stage);

		if (datos.containsKey(AutorizacionGerenteController.ACCION_CANCELAR)) {
			throw new InitializeGuiException(false);
		}
		if (!(boolean) datos.get(AutorizacionGerenteController.PARAM_SALIDA_CLAVE_CORRECTA)) {
			throw new InitializeGuiException(false);
		}
	}

}
