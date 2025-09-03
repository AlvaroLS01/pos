package com.comerzzia.iskaypet.pos.persistence.ticket.contrato.trazabilidad.items.ident;

import org.apache.commons.lang3.StringUtils;

public class ItemsPetsIdent extends ItemsPetsIdentKey {
    private String chip;

    private String anilla;

    private String cites;

    private Long version;

    private String activo;

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip == null ? null : chip.trim();
    }

    public String getAnilla() {
        return anilla;
    }

    public void setAnilla(String anilla) {
        this.anilla = anilla == null ? null : anilla.trim();
    }

    public String getCites() {
        return cites;
    }

    public void setCites(String cites) {
        this.cites = cites == null ? null : cites.trim();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo == null ? null : activo.trim();
    }
    
	@Override
	public String toString(){
		
		String chip = StringUtils.isNotBlank(getChip()) ? getChip() : "- ";
		String anilla = StringUtils.isNotBlank(getAnilla()) ? getAnilla(): " - ";
		String cites = StringUtils.isNotBlank(getCites()) ? getCites() : " -";
		String identificador = chip +" | "+ anilla + " | "+ cites;
		
		return identificador;
	}
}