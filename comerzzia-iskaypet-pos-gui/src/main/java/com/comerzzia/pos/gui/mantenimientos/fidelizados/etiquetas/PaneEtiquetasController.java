package com.comerzzia.pos.gui.mantenimientos.fidelizados.etiquetas;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.core.EtiquetaBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;
import com.comerzzia.pos.services.core.sesion.Sesion;
import com.comerzzia.pos.services.core.variables.VariablesServices;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;


@Component
public class PaneEtiquetasController  extends TabController<FidelizadoController>{

protected static final Logger log = Logger.getLogger(PaneEtiquetasController.class);
	
	@Autowired
	private Sesion sesion;
	
	@Autowired
    protected VariablesServices variablesService;
	
	@FXML
	protected TextArea taEtiquetas;
	
	protected FidelizadoBean fidelizado;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			fidelizado = newFidelizado;
			List<EtiquetaBean> listaEtiquetas = fidelizado.getEtiquetasCategorias();			
			Collections.sort(listaEtiquetas, new Comparator<EtiquetaBean>(){
				@Override
	            public int compare(EtiquetaBean o1, EtiquetaBean o2) {
	               return o1.getEtiqueta().toUpperCase().compareTo(o2.getEtiqueta().toUpperCase());
	            }
			});
			String etiquetaAux = "";
			Set<String> treeSet = new TreeSet<String>();
			if(!listaEtiquetas.isEmpty()){							
				for(EtiquetaBean etiqueta: listaEtiquetas ){
					if(treeSet.add(etiqueta.getEtiqueta())){						
						etiquetaAux += etiqueta.getEtiqueta()+", ";
					}
				}
				String etiquetasFidelizado = etiquetaAux.substring(0, etiquetaAux.length()-2);
				taEtiquetas.setText(etiquetasFidelizado);
			}else{
				taEtiquetas.setText("");
			}

		}
	}

	public void limpiarFormulario(){
		taEtiquetas.clear();
	}

	public TextArea getTaEtiquetas() {
		return taEtiquetas;
	}

	public FidelizadoBean getFidelizado() {
		return fidelizado;
	}

	public void setFidelizado(FidelizadoBean fidelizado) {
		this.fidelizado = fidelizado;
	}

}
