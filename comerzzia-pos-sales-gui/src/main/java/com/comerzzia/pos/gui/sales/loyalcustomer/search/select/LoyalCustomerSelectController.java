package com.comerzzia.pos.gui.sales.loyalcustomer.search.select;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.loyalty.client.model.LyCustomer;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.ActionButtonComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.gui.sales.loyalcustomer.search.LoyalCustomerSearchController;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

@Controller
@SuppressWarnings({"rawtypes", "unchecked"})
@CzzScene
public class LoyalCustomerSelectController extends SceneController implements Initializable, ButtonsGroupController{
	
	@FXML
    protected TableView tbLoyalCustomers;
    
    @FXML
    protected Label lbTitle;

    protected ObservableList<LoyalCustomerSelectRow> loyalCustomers;
    
    @FXML
    protected TableColumn tcLyCustomerCode, tcName, tcLastName, tcDocument, tcCard, tcAddress;
    
    @FXML
    protected Button btAccept, btCancel;
    
    // botonera de acciones de tabla
    protected ButtonsGroupComponent buttonsGroupComponent;
    
    protected Logger log = Logger.getLogger(getClass());
    
	@Override
    public void initialize(URL url, ResourceBundle rb) {

    	tbLoyalCustomers.setPlaceholder(new Label(""));
        
    	loyalCustomers = FXCollections.observableList(new ArrayList<LoyalCustomerSelectRow>());
        
        tcLyCustomerCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcLyCustomerCode", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcName.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcName", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcLastName.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcLastName", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcDocument.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcDocument", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCard.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcCard", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcAddress.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbLoyalCustomers", "tcAddress", null, CellFactoryBuilder.LEFT_ALIGN_STYLE));
    
        tcLyCustomerCode.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getLyCustomerCode();
            }
        });
        tcName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getName();
            }
        });
        tcLastName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getLastName();
            }
        });
        tcDocument.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getDocument();
            }
        });
        tcCard.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getCard();
            }
        });
        tcAddress.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LoyalCustomerSelectRow, String> cdf) {
                return cdf.getValue().getAddress();
            }
        });
    }

    @Override
    public void initializeComponents() throws InitializeGuiException {
        
        addEnterTableEvent(tbLoyalCustomers);
        addNavegationTableEvent(tbLoyalCustomers);
        
    }
    
    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	try {
	    	Map<String,Object> params = sceneData;
	    	setTitle();
	    	
	        loyalCustomers.clear();
	        
	        List<LyCustomer> loyalCustomersParam = (List<LyCustomer>)params.get(LoyalCustomerSearchController.PARAMETRO_LOYAL_CUSTOMERS_SELECTION);
	        
	        for(LyCustomer loyalCustomer: loyalCustomersParam){
        		loyalCustomers.add(new LoyalCustomerSelectRow(loyalCustomer));
	        }
	        
	        tbLoyalCustomers.setItems(loyalCustomers);
    	} catch(Exception e) {
    		log.error("onSceneOpen() - Error inicializando la pantalla: " + e.getMessage());
    	}
    }

    @Override
    public void initializeFocus() {
    	tbLoyalCustomers.requestFocus();
    	tbLoyalCustomers.scrollTo(0);
		if(tbLoyalCustomers.getSelectionModel().getSelectedIndex()<0){
			tbLoyalCustomers.getSelectionModel().select(0);
		}
    }

    @Override
    public void executeAction(ActionButtonComponent pressedButton) {
        switch (pressedButton.getClave()) {
            // BOTONERA TABLA MOVIMIENTOS
            case "ACCION_TABLA_PRIMER_REGISTRO":
                actionTableFirstRegister();
                break;
            case "ACCION_TABLA_ANTERIOR_REGISTRO":
                actionTablePreviousRegister();
                break;
            case "ACCION_TABLA_SIGUIENTE_REGISTRO":
                actionTableNextRegister();
                break;
            case "ACCION_TABLA_ULTIMO_REGISTRO":
                actionTableLastRegister();
                break;
        }
    }
    
    @FXML
    public void actionAccept(){
        if(tbLoyalCustomers.getSelectionModel().getSelectedItem() == null) {
        	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar un fidelizado de la lista."));
        	return;
        }

       	closeSuccess(((LoyalCustomerSelectRow)tbLoyalCustomers.getSelectionModel().getSelectedItem()).getLyCustomer());
    }
    
    /**
     * Acción mover a primer registro de la tabla
     *
     * @param event
     */
    @FXML
    protected void actionTableFirstRegister() {
        log.debug("actionTableFirstRegister() - Acción ejecutada");
        if (tbLoyalCustomers.getItems() != null && !tbLoyalCustomers.getItems().isEmpty()) {
            tbLoyalCustomers.getSelectionModel().select(0);
            tbLoyalCustomers.scrollTo(0);  // Mueve el scroll para que se vea el registro
        }
    }

    /**
     * Acción mover a anterior registro de la tabla
     *
     * @param event
     */
    @FXML
    protected void actionTablePreviousRegister() {
        log.debug("actionTablePreviousRegister() - Acción ejecutada");
        if (tbLoyalCustomers.getItems() != null && !tbLoyalCustomers.getItems().isEmpty()) {
            int index = tbLoyalCustomers.getSelectionModel().getSelectedIndex();
            if (index > 0) {
                tbLoyalCustomers.getSelectionModel().select(index - 1);
                tbLoyalCustomers.scrollTo(index - 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a siguiente registro de la tabla
     *
     * @param event
     */
    @FXML
    protected void actionTableNextRegister() {
        log.debug("actionTableNextRegister() - Acción ejecutada");
        if (tbLoyalCustomers.getItems() != null && !tbLoyalCustomers.getItems().isEmpty()) {
            int index = tbLoyalCustomers.getSelectionModel().getSelectedIndex();
            if (index < tbLoyalCustomers.getItems().size()) {
                tbLoyalCustomers.getSelectionModel().select(index + 1);
                tbLoyalCustomers.scrollTo(index + 1);  // Mueve el scroll para que se vea el registro
            }
        }
    }

    /**
     * Acción mover a último registro de la tabla
     *
     * @param event
     */
    @FXML
    protected void actionTableLastRegister() {
        log.debug("accionTablaUltimoRegistro() - Acción ejecutada");
        if (tbLoyalCustomers.getItems() != null && !tbLoyalCustomers.getItems().isEmpty()) {
            tbLoyalCustomers.getSelectionModel().select(tbLoyalCustomers.getItems().size() - 1);
            tbLoyalCustomers.scrollTo(tbLoyalCustomers.getItems().size() - 1);  // Mueve el scroll para que se vea el registro
        }
    }
    
    /**
     * Accion que trata el evento de teclado para enviar el artículo a facturar.
     *
     * @param event
     */
   public void acceptDocDoubleClick(MouseEvent event) {
        log.trace("acceptDocDoubleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                actionAccept();
            }
        }
    }
   
   @Override
	public void actionTableEventEnter(String idTabla) {
	   log.trace("accionEventoEnterTabla() - Aceptar");
	   actionAccept();
	}
   
   public void setTitle(){
	   lbTitle.setText(I18N.getText("Selección de Fidelizado"));
   }

}
