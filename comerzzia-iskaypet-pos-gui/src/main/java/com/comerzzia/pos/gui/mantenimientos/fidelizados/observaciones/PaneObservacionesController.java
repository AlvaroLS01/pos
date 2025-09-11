package com.comerzzia.pos.gui.mantenimientos.fidelizados.observaciones;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

@Component
public class PaneObservacionesController extends TabController<FidelizadoController>{
	
protected static final Logger log = Logger.getLogger(PaneObservacionesController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@FXML
	protected TextArea taObservaciones;
	
	protected FidelizadoBean fidelizado;
	
	protected boolean modoEdicion;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			taObservaciones.setText(newFidelizado.getObservaciones());
		}	
		fidelizado = newFidelizado;
		
		if("CONSULTA".equals(getTabParentController().getModo())){
			editarCampos(false);
		}else{
			editarCampos(true);
		}		
//		if(fidelizado != null){
//				taObservaciones.setText(fidelizado.getObservaciones());
//			
//		}else{
//			taObservaciones.setText("");
//		}
		
		
	}
	
	public void editarCampos(boolean modoInsercion){
		taObservaciones.setEditable(modoInsercion);
	}
	
	public void limpiarFormulario(){
		taObservaciones.clear();
	}

	public TextArea getTaObservaciones() {
		return taObservaciones;
	}

	public FidelizadoBean getFidelizado() {
		return fidelizado;
	}

	public void setFidelizado(FidelizadoBean fidelizado) {
		this.fidelizado = fidelizado;
	}

}
