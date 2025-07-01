package com.comerzzia.pos.gui.sales.loyalcustomer.collectives;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.fxtable.cells.CellFactoryBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

@Component
@CzzScene
public class LoyalCustomerCollectivesController extends SceneController{
	
	public static final String PARAM_COLLECTIVES = "collectives";
    
	protected static final Logger log = Logger.getLogger(LoyalCustomerCollectivesController.class);

    @FXML
    protected TableView<LoyalCustomerCollectiveSelectionRow> tbCollectives;
    
    @FXML
    protected TextField tfCollectiveDes, tfCollectiveCode;
    
    @FXML
    protected TableColumn<LoyalCustomerCollectiveSelectionRow, String> tcCollectiveDes, tcCollectiveCode;
    
    protected LoyalCustomerCollectiveFormValidationBean frSearchCollective;
    
    private ObservableList<LoyalCustomerCollectiveSelectionRow> collectives;
    private List<LoyalCustomerCollectiveSelectionRow> originalCollectives;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		tbCollectives.setPlaceholder(new Label());
        
		collectives = FXCollections.observableArrayList();
		tbCollectives.setItems(collectives);
        
		frSearchCollective = SpringContext.getBean(LoyalCustomerCollectiveFormValidationBean.class);
		frSearchCollective.setFormField("collectiveCode", tfCollectiveCode);
		frSearchCollective.setFormField("collectiveDes", tfCollectiveDes);
        
		tcCollectiveDes.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCollectives", "tcCollectiveDes", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        tcCollectiveCode.setCellFactory(CellFactoryBuilder.createCellRendererCelda("tbCollectives", "tcCollectiveCode", null,CellFactoryBuilder.LEFT_ALIGN_STYLE));
        
        tcCollectiveDes.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveSelectionRow, String>("collectiveDes"));
        tcCollectiveCode.setCellValueFactory(new PropertyValueFactory<LoyalCustomerCollectiveSelectionRow, String>("collectiveCode"));
	}

	@Override
	public void initializeComponents() throws InitializeGuiException {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSceneOpen() throws InitializeGuiException {
		originalCollectives = (List<LoyalCustomerCollectiveSelectionRow>) sceneData.get(PARAM_COLLECTIVES);
    	
		tfCollectiveCode.setText("");
        tfCollectiveDes.setText("");
        actionSearch();
	}

	@Override
	public void initializeFocus() {
		tfCollectiveCode.requestFocus();
	}
	
	public void actionSearch(){
        
		frSearchCollective.clearErrorStyle();
        
		frSearchCollective.setCollectiveCode(tfCollectiveCode.getText());
        frSearchCollective.setCollectiveDes(tfCollectiveDes.getText());
        
        Set<ConstraintViolation<LoyalCustomerCollectiveFormValidationBean>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frSearchCollective);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<LoyalCustomerCollectiveFormValidationBean> next = constraintViolations.iterator().next();
            frSearchCollective.setErrorStyle(next.getPropertyPath(), true);
            frSearchCollective.setFocus(next.getPropertyPath());
        }
        else{
            List<LoyalCustomerCollectiveSelectionRow> result = originalCollectives;
            
            collectives.clear();
            if(result != null && !result.isEmpty()){
	            for(LoyalCustomerCollectiveSelectionRow collective: result){
	            	if(collective.getCollectiveCode().toUpperCase().contains(frSearchCollective.getCollectiveCode().toUpperCase()) && collective.getCollectiveDes().toUpperCase().contains(frSearchCollective.getCollectiveDes().toUpperCase())){
	            		collectives.add(collective);
	            	}
	            }
            }else{
            	log.debug("No se han encontrado resultados");
            }
           
        }
    }
    
    public void actionSearchCollectiveIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            actionSearch();
        }
    }
        
    public void actionAccept(){
        
        if(tbCollectives.getSelectionModel().getSelectedItem()!=null){
            closeSuccess(tbCollectives.getSelectionModel().getSelectedItem());
        }
        else{
        	DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Debe seleccionar antes el colectivo correspondiente."));
        }
    }
    
    public void acceptCollectiveDoubleClick(MouseEvent event) {
        log.debug("acceptCollectiveDoubleClick() - Acción aceptar");
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                actionAccept();
            }
        }
    }
    
    public void acceptCollectiveIntro(KeyEvent e){
        
        if(e.getCode() == KeyCode.ENTER){
            actionAccept();
        }
    }

}
