package com.comerzzia.dinosol.pos.services.core.sesion;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.services.cajas.Caja;
import com.comerzzia.pos.services.core.sesion.SesionCaja;

@Component
@Primary
public class DinoSesionCaja extends SesionCaja {

	public void setCajaAbierta(Caja cajaAbierta) {
		this.cajaAbierta = cajaAbierta;
	}

}
