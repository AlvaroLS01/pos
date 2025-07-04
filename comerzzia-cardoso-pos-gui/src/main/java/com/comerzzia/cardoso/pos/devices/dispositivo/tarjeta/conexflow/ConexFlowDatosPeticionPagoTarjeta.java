package com.comerzzia.cardoso.pos.devices.dispositivo.tarjeta.conexflow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.comerzzia.pos.services.ticket.pagos.tarjeta.DatosPeticionPagoTarjeta;

public class ConexFlowDatosPeticionPagoTarjeta extends DatosPeticionPagoTarjeta {

	private Map<String, String> adicionales;

	public ConexFlowDatosPeticionPagoTarjeta(String codTicket, Long idTicket, BigDecimal importe) {
		super(codTicket, idTicket, importe);
    }

	public Map<String, String> getAdicionales() {
		return adicionales;
	}

	public void setAdicionales(Map<String, String> adicionales) {
		this.adicionales = adicionales;
	}
	
	public void addAdiccional(String clave, String valor) {
		if(this.adicionales == null) {
			adicionales = new HashMap<String, String>();
		}
		adicionales.put(clave, valor);
	}

}
