package com.comerzzia.pampling.pos.services.payments.methods.types;

import org.apache.log4j.Logger;

import com.comerzzia.pampling.pos.core.gui.componentes.CashlogyVentanaCargando;
import com.comerzzia.pos.core.gui.BackgroundTask;

import javafx.stage.Stage;

public abstract class CashlogyTask<V> extends BackgroundTask<V> {
	
	protected Stage stage;
	
	private static Logger log = Logger.getLogger(CashlogyTask.class);
	
	public void start(Stage stageR) {
		
		this.stage = stageR;
		currentThread = new Thread(this);
		currentThread.setName(this.getClass().toString());
		currentThread.start();
		
		mostrarVentanaCargando();

    }
	
	@Override
	protected void mostrarVentanaCargando(){
    	if(mostrarVentanaCargando){
    		CashlogyVentanaCargando.crearVentanaCargando(stage);
    		CashlogyVentanaCargando.mostrar();
            
    	}
    }
	
	@Override
	protected void cerrarVentanaCargando(){
    	log.trace("cerrarVentanaCargando()");
    	if(mostrarVentanaCargando){
    		CashlogyVentanaCargando.cerrar();
    	}
    }
}
