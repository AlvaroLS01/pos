package com.comerzzia.pos.gui.ventas.tickets.articulos.numerosSerie.lineaOrigen;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class NumerosSerieOrigenGui {

	private SimpleStringProperty numeroSerie;
	private SimpleBooleanProperty lineaSelec;

	public NumerosSerieOrigenGui(String numeroSerie) {
		super();
		this.numeroSerie = new SimpleStringProperty(numeroSerie);
		this.lineaSelec = new SimpleBooleanProperty(false);
	}

	public SimpleStringProperty getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(SimpleStringProperty numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public SimpleBooleanProperty getLineaSelec() {
		return lineaSelec;
	}

	public void setLineaSelec(SimpleBooleanProperty lineaSelec) {
		this.lineaSelec = lineaSelec;
	}
    
    public void setLineaSelecValue(boolean lineaSelec){
        this.lineaSelec.setValue(lineaSelec);
    }

}
