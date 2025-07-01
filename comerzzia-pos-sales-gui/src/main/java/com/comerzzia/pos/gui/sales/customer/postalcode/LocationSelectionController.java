package com.comerzzia.pos.gui.sales.customer.postalcode;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.comerzzia.core.facade.model.PostalCode;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class LocationSelectionController extends SceneController {
	public static final String PARAM_POSTAL_CODES_LIST = "PostalCodes"; 
	
    @FXML
	protected TableView tbPostalCodes;
    @FXML
	protected TableColumn clProvince, clLocation, clCity;
    
    protected ObservableList<PostalCode> postalCodes;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
		clProvince.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPostalCodes", "clProvince", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		clLocation.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPostalCodes", "clLocation", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
		clCity.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbPostalCodes", "clCity", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));

		clProvince.setCellValueFactory(new PropertyValueFactory<PostalCode, String>("province"));
		clLocation.setCellValueFactory(new PropertyValueFactory<PostalCode, String>("location"));
		clCity.setCellValueFactory(new PropertyValueFactory<PostalCode, String>("city"));
    }

	@Override
    public void onSceneOpen() throws InitializeGuiException {
		tbPostalCodes.getSelectionModel().clearSelection();
		postalCodes = FXCollections.observableList((List<PostalCode>) sceneData.get(PARAM_POSTAL_CODES_LIST));
		tbPostalCodes.setItems(postalCodes);
    }

	@Override
    public void initializeFocus() {
		tbPostalCodes.requestFocus();
    }
	
	@FXML
	public void actionAccept() {
		PostalCode postalCode = (PostalCode) tbPostalCodes.getSelectionModel().getSelectedItem();
		if(postalCode != null) {			
			closeSuccess(postalCode);
		} else {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("Seleccione una población antes de continuar."));
		}
	}

}
