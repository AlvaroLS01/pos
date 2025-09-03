package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad;

public class DetailPetsKey {
    private String activityId;

    private String tipoMaterial;

    private String grupoArticulos;

    private String valorCaracteristica;

    private String pais;

    private String region;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId == null ? null : activityId.trim();
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial == null ? null : tipoMaterial.trim();
    }

    public String getGrupoArticulos() {
        return grupoArticulos;
    }

    public void setGrupoArticulos(String grupoArticulos) {
        this.grupoArticulos = grupoArticulos == null ? null : grupoArticulos.trim();
    }

    public String getValorCaracteristica() {
        return valorCaracteristica;
    }

    public void setValorCaracteristica(String valorCaracteristica) {
        this.valorCaracteristica = valorCaracteristica == null ? null : valorCaracteristica.trim();
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais == null ? null : pais.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region == null ? null : region.trim();
    }
}