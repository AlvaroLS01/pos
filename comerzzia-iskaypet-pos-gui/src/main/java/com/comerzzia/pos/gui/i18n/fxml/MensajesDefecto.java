package com.comerzzia.pos.gui.i18n.fxml;

import java.util.LinkedList;
import java.util.List;

public class MensajesDefecto {

	public static List<String> obtenerCadenas() throws MensajesException {
		List<String> res = new LinkedList<>();

		res.add("TALLA");
		res.add("COLOR");
		res.add("Talla");
		res.add("Color");
        
        return res;
	}
}
