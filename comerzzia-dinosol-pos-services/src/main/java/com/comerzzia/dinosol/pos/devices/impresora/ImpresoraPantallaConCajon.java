package com.comerzzia.dinosol.pos.devices.impresora;

import com.comerzzia.pos.core.dispositivos.Dispositivos;
import com.comerzzia.pos.dispositivo.impresora.ImpresoraPantalla;

public class ImpresoraPantallaConCajon extends ImpresoraPantalla {
	
	@Override
	public void abrirCajon() {
		Dispositivos.abrirCajon();
	}

}
