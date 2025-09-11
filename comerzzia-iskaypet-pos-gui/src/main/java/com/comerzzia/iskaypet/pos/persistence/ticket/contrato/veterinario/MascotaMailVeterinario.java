package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.veterinario;

public class MascotaMailVeterinario extends MascotaMailVeterinarioKey {
    private String desart;

    public String getDesart() {
        return desart;
    }

    public void setDesart(String desart) {
        this.desart = desart == null ? null : desart.trim();
    }
}