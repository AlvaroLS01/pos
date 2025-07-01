package com.comerzzia.pos.devices.drivers.javapos;

import java.util.ArrayList;
import java.util.List;

import com.comerzzia.pos.util.i18n.I18N;

import jpos.BaseControl;
import jpos.JposConst;
import lombok.extern.log4j.Log4j;

@Log4j
public class JavaPosUtils {
	public static List<String> getAvailabilityErrors(BaseControl jposControl) {
		List<String> errors = new ArrayList<String>();

		if (jposControl == null) {
			log.error("getAvailabilityErrors() - jposControl == null");
			errors.add(I18N.getText("La impresora no ha sido inicializado correctamente."));
		}

		try {
			if (!jposControl.getClaimed()) {
				log.error("getAvailabilityErrors() - !jposControl.getClaimed()");
				errors.add(I18N.getText("No se pudo tomar el control del dispositivo."));
				
				return errors;
			}
		} catch (Exception ignore) {
			log.error("getAvailabilityErrors() - !jposControl.getClaimed(): " + ignore.getMessage());
		}
		
		try {
			if (jposControl.getState() == JposConst.JPOS_S_CLOSED) {
				log.error("getAvailabilityErrors() - Dispositivo cerrado");
				errors.add(I18N.getText("Dispositivo cerrado."));
			}
		} catch (Exception ignore) {
			log.error("getAvailabilityErrors() - jposControl.getState(): " + ignore.getMessage());
			errors.add(I18N.getText("Error obteniendo estado del dispositivo"));
		}

		try {
			if (!jposControl.getDeviceEnabled()) {
				log.error("getAvailabilityErrors() - !jposControl.getDeviceEnabled()");
				errors.add(I18N.getText("El dispositivo no está habilitado."));
			}
		} catch (Exception ignore) {
			log.error("getAvailabilityErrors() - !jposControl.getDeviceEnabled(): " + ignore.getMessage());
		}
		
		return errors;
	}
}
