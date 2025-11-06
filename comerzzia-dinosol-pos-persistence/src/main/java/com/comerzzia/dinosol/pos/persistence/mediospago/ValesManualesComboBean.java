package com.comerzzia.dinosol.pos.persistence.mediospago;

import com.comerzzia.pos.persistence.mediosPagos.MedioPagoBean;

public class ValesManualesComboBean {

	private MedioPagoBean medioPago;

	public MedioPagoBean getMedioPago() {
		return medioPago;
	}

	public void setMedioPago(MedioPagoBean medioPago) {
		this.medioPago = medioPago;
	}
	
	@Override
	public String toString() {
		return medioPago.getDesMedioPago();
	}
}
