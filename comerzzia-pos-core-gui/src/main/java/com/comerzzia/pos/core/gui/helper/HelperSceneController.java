package com.comerzzia.pos.core.gui.helper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public abstract class HelperSceneController<T extends HelperRow<?>> extends SceneController {

	@Autowired
    protected Session session;
	
	@FXML
    protected TableView tbHelperRow;
	
	@FXML
    protected Label lbTitle;
	
	@FXML
    protected TableColumn tcHelperCode, tcHelperDesc;
	
	@FXML
    protected Button btAccept, btCancel;
	
	protected ObservableList<T> helperRowList;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tbHelperRow.setPlaceholder(new Label(""));
		
		helperRowList = FXCollections.observableArrayList(new ArrayList<T>());
		
		setCodeCellFactory();
		setDescCellFactory();
		
	}
	
	protected void setCodeCellFactory() {
		tcHelperCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbHelperRow", "tcHelperCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcHelperCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> cdf) {
                return cdf.getValue().getHelperCode();
            }
        });
	}
	
	protected void setDescCellFactory() {
		tcHelperDesc.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbHelperRow", "tcHelperDesc", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
		tcHelperDesc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> cdf) {
                return cdf.getValue().getHelperDesc();
            }
        });
	}
	
	@Override
	public void initializeComponents() throws InitializeGuiException {
		addEnterTableEvent(tbHelperRow);
        addNavegationTableEvent(tbHelperRow);
	}
	
	@Override
	public void onSceneOpen() throws InitializeGuiException {
    	setTitle();
    	

    	helperRowList.clear();
    	helperRowList.addAll(buildHelpersRows(sceneData));
        
    	tbHelperRow.setItems(helperRowList);
	}
	
	@Override
	protected void initializeFocus() {
		tbHelperRow.requestFocus();
		tbHelperRow.scrollTo(0);
		if(tbHelperRow.getSelectionModel().getSelectedIndex()<0){
			tbHelperRow.getSelectionModel().select(0);
		}
	}
	
	@FXML
    protected void actionAccept(){
		if(tbHelperRow.getSelectionModel().getSelectedItem()!=null){
            closeSuccess(((T)tbHelperRow.getSelectionModel().getSelectedItem()).getObject());
        }
        else{
            DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar un elemento."));
        }
	}
	
	@FXML
	protected void acceptTableDoubleClick(MouseEvent event) {
		log.trace("acceptTableDoubleClick() - Acción aceptar");
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				actionAccept();
			}
		}
	}
	
	@Override
	public void actionTableEventEnter(String idTabla) {
	   log.trace("actionEventEnterTable() - Aceptar");
	   actionAccept();
	}
	
	public abstract void setTitle();
	public abstract List<T> buildHelpersRows(Map<String,Object> params);

}
