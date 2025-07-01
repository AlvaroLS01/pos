package com.comerzzia.pos.gui.sales.specialbarcode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.comerzzia.omnichannel.facade.model.catalog.ItemSpecialBarcodeConfig;
import com.comerzzia.omnichannel.facade.service.catalog.ItemSpecialBarcodeServiceFacade;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.actionbutton.simple.ActionButtonSimpleComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonConfigurationBean;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.ActionSceneController;
import com.comerzzia.pos.core.gui.controllers.CzzActionScene;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

@Component
@CzzActionScene
public class SpecialBarcodeManagementController extends ActionSceneController implements ButtonsGroupController {

	protected Logger log = Logger.getLogger(getClass());
	
	@FXML
	protected TableView<SpecialBarcodeRow> tbSpecialBarcodes;
	@FXML
	protected TableColumn<SpecialBarcodeRow, String> tcDescription, tcPrefix, tcItemCode, tcDocumentCode, tcPrice, tcQuantity, tcLoyalty;
	@FXML
	protected HBox buttonGroupPanel;
	
	protected ObservableList<SpecialBarcodeRow> specialBarcodes;
	
	protected ButtonsGroupComponent tableActionsButtonGroup;
	
	@Autowired
	private ItemSpecialBarcodeServiceFacade itemSpecialBarcodeService;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		log.trace("initialize()- ");

		specialBarcodes = FXCollections.observableArrayList();
		tbSpecialBarcodes.setPlaceholder(new Label(""));

		tcDescription.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcDescription", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcPrefix.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcPrefix", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcItemCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcItemCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcDocumentCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcDocumentCode", 2, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcPrice.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcPrice", 2, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcQuantity.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcQuantity", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcLoyalty.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCodigos", "tcLoyalty", 2, CellFactoryBuilder.LEFT_ALIGN_STYLE));

		// Assign rows to table
		tbSpecialBarcodes.setItems(specialBarcodes);

		// Factory for every cell to increase performance
		tcDescription.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getDescriptionProperty();
			}
		});
		tcPrefix.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getPrefixProperty();
			}
		});
		tcItemCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getItemCodeProperty();
			}
		});
		tcDocumentCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getDocumentCodeProperty();
			}
		});
		tcPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getPriceProperty();
			}
		});
		tcQuantity.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getQuantityProperty();
			}
		});
		tcLoyalty.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpecialBarcodeRow, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<SpecialBarcodeRow, String> cdf) {
				return cdf.getValue().getLoyaltyProperty();
			}
		});
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
		log.trace("initializeComponents()- ");

		try{          
            log.debug("initializeComponents() - Carga de acciones de botonera de tabla de ventas");
            List<ButtonConfigurationBean> tableActionsList = loadTableActions();
            tableActionsButtonGroup = new ButtonsGroupComponent(1, 7, this, tableActionsList, buttonGroupPanel.getPrefWidth(), buttonGroupPanel.getPrefHeight(), ActionButtonSimpleComponent.class.getName());
            buttonGroupPanel.getChildren().add(tableActionsButtonGroup);
		}
		catch (LoadWindowException e) {
			log.error("initializeComponents() - Error al crear botonera: " + e.getMessage(), e);
		} 			
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		log.trace("onSceneOpen()- ");
		
		refreshWindow();
	}

	@Override
	public void initializeFocus() {
	}
	
	public void refreshWindow(){
		log.trace("refreshWindow()- ");
		
		try {
			List<ItemSpecialBarcodeConfig> barcodes = itemSpecialBarcodeService.findAll();
			specialBarcodes.clear();

			for (ItemSpecialBarcodeConfig barcode : barcodes) {
				specialBarcodes.add(new SpecialBarcodeRow(barcode));
			}
			tbSpecialBarcodes.setItems(specialBarcodes);
		}
		catch (Exception e) {
			log.error("Error consultando las configuraciones de los códigos de barras", e);
			DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("Se produjo un error consultando las configuraciones de códigos de barra."), e);
		}
	}
	
	@Override
	public void executeAction(ActionButtonComponent actionButton) {
		log.debug("executeAction() - Realizando la acción : " + actionButton.getClave() + " de tipo : " + actionButton.getTipo());
    	switch (actionButton.getClave()) {
    	case "ACTION_TABLE_DELETE_RECORD":
    		deleteBarcode();
    		break;
    	case "ACTION_TABLE_UPDATE_RECORD":
    		updateBarcode();
    		break;
    	case "ACTION_TABLE_CREATE_RECORD":
    		createBarcode();
    		break;
    	default:
    		log.error("No se ha especificado acción en pantalla para la operación :" + actionButton.getClave());
    		break;
    	}		
	}

	protected List<ButtonConfigurationBean> loadTableActions() {
		log.trace("loadTableActions()- ");
		
        List<ButtonConfigurationBean> actionList = new ArrayList<>();
        actionList.add(new ButtonConfigurationBean("icons/row_delete.png", null, null, "ACTION_TABLE_DELETE_RECORD", "REALIZAR_ACCION"));
        actionList.add(new ButtonConfigurationBean("icons/row_edit.png", null, null, "ACTION_TABLE_UPDATE_RECORD", "REALIZAR_ACCION"));
        actionList.add(new ButtonConfigurationBean("icons/row-plus.png", null, null, "ACTION_TABLE_CREATE_RECORD", "REALIZAR_ACCION"));
        return actionList;
    }
	
	public void createBarcode(){
		log.trace("createBarcode()- ");
		
		if (sceneData.containsKey(NewSpecialBarcodeController.PARAM_BARCODE)) {
			sceneData.remove(NewSpecialBarcodeController.PARAM_BARCODE);
		}

		openScene(NewSpecialBarcodeController.class, new SceneCallback<Void>() {
			
			@Override
			public void onSuccess(Void callbackData) {
				refreshWindow();
			}
		});
		
	}
	
	public void updateBarcode() {
		log.trace("editBarcode()- ");

		SpecialBarcodeRow selectedRow = tbSpecialBarcodes.getSelectionModel().getSelectedItem();

		if (selectedRow != null) {
			sceneData.put(NewSpecialBarcodeController.PARAM_BARCODE, selectedRow.getBarcode());
			openScene(NewSpecialBarcodeController.class, new SceneCallback<Void>(){

				@Override
				public void onSuccess(Void callbackData) {
					refreshWindow();
				}
			});
		}
	}
	
	public void deleteBarcode(){
		log.trace("deleteBarcode()- ");

		SpecialBarcodeRow selectedRow = tbSpecialBarcodes.getSelectionModel().getSelectedItem();

		if (selectedRow != null) {
			if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se borrará la configuración seleccionada. ¿Está seguro?"))){
				ItemSpecialBarcodeConfig barcode = selectedRow.getBarcode();
				try {
					itemSpecialBarcodeService.delete(barcode);
					refreshWindow();
				}
				catch (Exception e) {
					log.error("Error al eliminar el código de barras.", e);
					DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(I18N.getText("No se pudo eliminar la configuración seleccionada."), e);
				}
			}
		}
	}
}
