package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados;

import com.comerzzia.core.util.base.MantenimientoBean;

public class TicketContratosKey extends MantenimientoBean {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7605023781391114857L;

	private String uidActividad;

    private String uidTicket;

    private Integer linea;

    public String getUidActividad() {
        return uidActividad;
    }

    public void setUidActividad(String uidActividad) {
        this.uidActividad = uidActividad == null ? null : uidActividad.trim();
    }

    public String getUidTicket() {
        return uidTicket;
    }

    public void setUidTicket(String uidTicket) {
        this.uidTicket = uidTicket == null ? null : uidTicket.trim();
    }

    public Integer getLinea() {
        return linea;
    }

    public void setLinea(Integer linea) {
        this.linea = linea;
    }

	@Override
	protected void initNuevoBean() {
		// TODO Auto-generated method stub
		
	}
}