package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad;

public class DetailPets extends DetailPetsKey {
    private String jerarquia;

    private String idChip;

    private String idAnilla;

    private String idCites;

    private String contrato;

    public String getJerarquia() {
        return jerarquia;
    }

    public void setJerarquia(String jerarquia) {
        this.jerarquia = jerarquia == null ? null : jerarquia.trim();
    }

    public String getIdChip() {
        return idChip;
    }

    public boolean getIdChipAsBoolean() {
        return idChip != null && idChip.equals("S");
    }

    public void setIdChip(String idChip) {
        this.idChip = idChip == null ? null : idChip.trim();
    }

    public void setIdChip(boolean idChip) {
        this.idChip = idChip ? "S" : "N";
    }


    public String getIdAnilla() {
        return idAnilla;
    }

    public boolean getIdAnillaAsBoolean() {
        return idAnilla != null && idAnilla.equals("S");
    }

    public void setIdAnilla(String idAnilla) {
        this.idAnilla = idAnilla == null ? null : idAnilla.trim();
    }

    public void setIdAnilla(boolean idAnilla) {
        this.idAnilla = idAnilla ? "S" : "N";
    }

    public String getIdCites() {
        return idCites;
    }

    public boolean getIdCitesAsBoolean() {
        return idCites != null && idCites.equals("S");
    }

    public void setIdCites(String idCites) {
        this.idCites = idCites == null ? null : idCites.trim();
    }

    public void setIdCites(boolean idCites) {
        this.idCites = idCites ? "S" : "N";
    }

    public String getContrato() {
        return contrato;
    }

    public boolean getContratoAsBoolean() {
        return contrato != null && contrato.equals("S");
    }

    public void setContrato(String contrato) {
        this.contrato = contrato == null ? null : contrato.trim();
    }

    public void setContrato(boolean contrato) {
        this.contrato = contrato ? "S" : "N";
    }
}