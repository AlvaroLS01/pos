package com.comerzzia.iskaypet.pos.gui.ventas.tickets.articulos.busquedas;

import com.comerzzia.iskaypet.pos.persistence.articulos.buscar.IskaypetArticuloBuscarBean;
import com.comerzzia.pos.gui.ventas.tickets.articulos.busquedas.LineaResultadoBusqGui;
import com.comerzzia.pos.persistence.articulos.buscar.ArticuloBuscarBean;

import javafx.beans.property.SimpleStringProperty;

public class IskaypetLineaResultadoBusqGui extends LineaResultadoBusqGui{

	protected SimpleStringProperty ean;

	public IskaypetLineaResultadoBusqGui(ArticuloBuscarBean articuloBuscar){
		super(articuloBuscar);
		if(articuloBuscar instanceof IskaypetArticuloBuscarBean){
			this.ean = new SimpleStringProperty(((IskaypetArticuloBuscarBean) articuloBuscar).getEan());
		}
	}

	public SimpleStringProperty getEan(){
		return ean;
	}

	public void setEan(SimpleStringProperty ean){
		this.ean = ean;
	}

}
