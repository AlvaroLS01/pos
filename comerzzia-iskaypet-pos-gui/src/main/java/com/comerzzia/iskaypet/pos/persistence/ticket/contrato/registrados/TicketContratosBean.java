package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.registrados;

import java.util.Date;

public class TicketContratosBean extends TicketContratosKey {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8095905576352446499L;

	private Date fecha;

    private String metodoFirma;

    private byte[] contrato;
    
    //INICIO ATRIBUTOS PERSONALIZADOS--------------------------------------------
    
    //FIN ATRIBUTOS PERSONALIZADOS-----------------------------------------------


    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMetodoFirma() {
        return metodoFirma;
    }

    public void setMetodoFirma(String metodoFirma) {
        this.metodoFirma = metodoFirma == null ? null : metodoFirma.trim();
    }

    public byte[] getContrato() {
        return contrato;
    }

    public void setContrato(byte[] contrato) {
        this.contrato = contrato;
    }

}