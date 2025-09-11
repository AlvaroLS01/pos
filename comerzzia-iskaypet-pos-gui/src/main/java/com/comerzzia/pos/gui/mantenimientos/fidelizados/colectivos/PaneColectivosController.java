package com.comerzzia.pos.gui.mantenimientos.fidelizados.colectivos;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.api.model.loyalty.ColectivosFidelizadoBean;
import com.comerzzia.api.model.loyalty.FidelizadoBean;
import com.comerzzia.pos.core.gui.controllers.TabController;
import com.comerzzia.pos.core.gui.tablas.celdas.CellFactoryBuilder;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.ColectivoGui;
import com.comerzzia.pos.gui.mantenimientos.fidelizados.FidelizadoController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

@Component
public class PaneColectivosController extends TabController<FidelizadoController>{
	
	protected static final Logger log = Logger.getLogger(PaneColectivosController.class);
	
	@FXML
	protected TableView<ColectivoGui> tableColectivos;
	
	protected ObservableList<ColectivoGui> colectivos;
	
	protected FidelizadoBean fidelizado;
	
	@FXML
	protected TableColumn<ColectivoGui, String> tcCodColectivo, tcDesColectivo, tcTipoColectivo;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableColectivos.setPlaceholder(new Text(""));
		
		tcCodColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcCodColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcDesColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcDesColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));
        tcTipoColectivo.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tableColectivos", "tcTipoColectivo", null, CellFactoryBuilder.ESTILO_ALINEACION_IZQ));

        tcCodColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("codColectivo"));
        tcDesColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("desColectivo"));
        tcTipoColectivo.setCellValueFactory(new PropertyValueFactory<ColectivoGui, String>("tipoColectivo"));
	}
	
	public void selected(){
		FidelizadoBean newFidelizado = getTabParentController().getFidelizado();
		if(newFidelizado != null && (fidelizado == null || !fidelizado.getIdFidelizado().equals(newFidelizado.getIdFidelizado()))){
			cargarColectivos(newFidelizado);
		}
		fidelizado = newFidelizado;
		
	}
	
	protected void cargarColectivos(FidelizadoBean fidelizado){
		List<ColectivoGui> listColectivos = new ArrayList<ColectivoGui>();
		for(ColectivosFidelizadoBean col : fidelizado.getColectivos()){
			if(!"S".equals(col.getPrivado())){
				ColectivoGui colGui = new ColectivoGui(col);
				listColectivos.add(colGui);
			}
		}
		
		colectivos = FXCollections.observableArrayList(listColectivos);
        tableColectivos.setItems(colectivos);
	}
	
	public void setFidelizado(FidelizadoBean fidelizado){
		this.fidelizado = fidelizado;
	}

}
