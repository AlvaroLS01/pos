package com.comerzzia.cardoso.pos.gui.ventas.tickets.articulos.lotes.lineasorigen;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * GAP - PERSONALIZACIONES V3 - LOTES
 */
public class CardosoLotesSerieOrigenGui{

	private SimpleStringProperty lote;
	private SimpleBooleanProperty lineaSelec;

	public CardosoLotesSerieOrigenGui(String lote){
		super();
		this.lote = new SimpleStringProperty(lote);
		this.lineaSelec = new SimpleBooleanProperty(false);
	}

	public SimpleStringProperty getLote(){
		return lote;
	}

	public void setLote(SimpleStringProperty lote){
		this.lote = lote;
	}

	public SimpleBooleanProperty getLineaSelec(){
		return lineaSelec;
	}

	public void setLineaSelec(SimpleBooleanProperty lineaSelec){
		this.lineaSelec = lineaSelec;
	}

	public void setLineaSelecValue(boolean lineaSelec){
		this.lineaSelec.setValue(lineaSelec);
	}

}
