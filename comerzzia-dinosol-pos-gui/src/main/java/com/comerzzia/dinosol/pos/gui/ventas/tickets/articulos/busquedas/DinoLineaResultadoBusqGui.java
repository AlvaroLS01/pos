package com.comerzzia.dinosol.pos.gui.ventas.tickets.articulos.busquedas;

import javafx.beans.property.SimpleStringProperty;

import com.comerzzia.dinosol.pos.persistence.articulos.DinoArticuloBuscarBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;

public class DinoLineaResultadoBusqGui extends LineaResultadoBusqGui {

	private SimpleStringProperty eanPrincipal;

	public DinoLineaResultadoBusqGui(ArticuloBuscarBean articuloBuscar) {
		super(articuloBuscar);
		
		this.eanPrincipal = new SimpleStringProperty(((DinoArticuloBuscarBean) articuloBuscar).getEanPrincipal());
	}

	public SimpleStringProperty getEanPrincipal() {
		return eanPrincipal;
	}

	public void setEanPrincipal(SimpleStringProperty eanPrincipal) {
		this.eanPrincipal = eanPrincipal;
	}

}
