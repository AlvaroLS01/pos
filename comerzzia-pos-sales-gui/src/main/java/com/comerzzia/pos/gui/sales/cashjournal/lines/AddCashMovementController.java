package com.comerzzia.pos.gui.sales.cashjournal.lines;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalConcept;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalDocumentIssued;
import com.comerzzia.omnichannel.facade.model.cashjournal.NewCashJournalManualMove;
import com.comerzzia.omnichannel.facade.service.cashjournal.CashJournalServiceFacade;
import com.comerzzia.omnichannel.facade.service.cashjournal.concept.CashJournalConceptServiceFacade;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.cashjournal.lines.cashmovementconcept.CashMovementConceptController;
import com.comerzzia.pos.gui.sales.retail.items.RetailBasketItemizationController;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Controller
@CzzScene
public class AddCashMovementController extends SceneController implements Initializable {

    private static final Logger log = Logger.getLogger(RetailBasketItemizationController.class.getName());
    // Variables del controlador
    protected CashJournalSession cashJournalSession;

    // Componentes del controlador
    @FXML
    protected TextField tfDocument, tfConceptCode;
    
    @FXML
    protected NumericTextField tfAmount;
    
    @FXML
    protected TextArea taConceptDes;
    
    @FXML
    protected Label lbAmountType, lbError;
    
    protected CashMovementForm frAnnotation;
    
    protected CashJournalConcept concept;
    
    @Autowired
    protected Session session;
    
    @Autowired
    protected CashJournalConceptServiceFacade cashJournalConceptServices;
    
    @Autowired
    protected PrintService printService;
    
    @Autowired
    protected ComerzziaTenantResolver tenantResolver;
    
    @Autowired
    protected CashJournalServiceFacade cashJournalService;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Inicializamos el formulario de login
        frAnnotation = SpringContext.getBean(CashMovementForm.class);
        // Asignamos un componente a cada elemento del formulario. (Para establecer foco o estilos de error)
        frAnnotation.setFormField("amount", tfAmount);
        frAnnotation.setFormField("document", tfDocument);
        frAnnotation.setFormField("concept", tfConceptCode);
        frAnnotation.setFormField("description", taConceptDes);            
    }

    @Override
    public void onSceneOpen() {
    	cashJournalSession = session.getCashJournalSession();
        refreshScreenData();
        
    }

    @Override
    public void initializeFocus() {
    	frAnnotation.clearErrorStyle();
    	
    	lbError.setText("");
        tfConceptCode.requestFocus();
        
        tfConceptCode.requestFocus();
    }

    @Override
    public void initializeComponents() {
        // Aquí van elementos que habría que inicializar tras establecer el application
    	tfAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                evalInOutValue(t1);
            }
        });
    	
    	tfConceptCode.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) {
            	if(oldValue){
            		validateConcept(tfConceptCode.getText());
            	}
            }
        });
        
       //registramos el evento al pulsar enter
      		addKeyEventHandler(new EventHandler<KeyEvent>() {			
      			@Override
      			public void handle(KeyEvent arg0) {
      				// TODO Auto-generated method stub
      				if(arg0.getCode().equals(KeyCode.ENTER)){
      					try
      					{
      					   actionAccept(null);
      					   arg0.consume();
      					}
      					catch(Exception ex)
      					{}		
      				}
      			}
      		}, KeyEvent.KEY_RELEASED);
      		
    }

    // </editor-fold>
    
    // <editor-fold desc="AccionesMenu">    
    protected boolean validateConcept(String code){
    	frAnnotation.clearErrorStyle();
    	lbError.setText("");
    	
    	String conceptCode = code.trim();
    	
    	if(!conceptCode.isEmpty()){
    		CashJournalConcept concept = null;
	        
	        try {
	        	concept = cashJournalConceptServices.findById(conceptCode);
				this.concept = concept;
				taConceptDes.setText(concept.getCashJournalConceptDes());
				evalInOutValue(tfAmount.getText());
	        }
	        catch (NotFoundException ex) {
	            log.error("No se encontró el código del concepto de movimiento de caja.");	            
	            lbError.setText(I18N.getText("El código de concepto no existe en la base de datos"));        
	            //tfCodConcepto.requestFocus();
	            concept = null;
	            taConceptDes.setText("");
	            return false;
	        }
    	}
    	return true;
    }
    
    
    protected boolean evalInOutValue(String inOutValue){
    	int inOut = -1;
		if(concept != null && StringUtils.isNotBlank(concept.getInputOutputType())) {
			if(concept.getInputOutputType().equals(CashJournalConceptServiceFacade.INPUT)) {
				inOut = inOut*-1;
			}
		}
		
		if(StringUtils.isNotBlank(inOutValue)) {
			BigDecimal bd = FormatUtils.getInstance().parseAmount(inOutValue);
			if(bd != null) {				
				inOut = inOut * bd.signum();
			}
		}
		
		if(inOut<0) {
			lbAmountType.setText(I18N.getText("SALIDA DE CAJA"));
		}else {
			lbAmountType.setText(I18N.getText("ENTRADA EN CAJA"));
		}
    	return true;
    }
    
    @FXML
    public void actionAccept(ActionEvent event) {
        log.debug("actionAccept()");
        
        if((this.concept == null) || (!this.concept.getCashJournalConceptCode().equals(tfConceptCode.getText()))){
        	if (!validateConcept(tfConceptCode.getText())){
        		return;
        	}
        }
        
        frAnnotation.setDocument(tfDocument.getText());
        frAnnotation.setAmount(tfAmount.getText());
        frAnnotation.setConcept(concept);
        frAnnotation.setDescription(taConceptDes.getText());
        
        if (!actionValidateForm()) {
        	log.debug("datos del apunte invalidos");
        	return;
        }
        
        CashJournalDocumentIssued<?> document = null;
        
        try {
            NewCashJournalManualMove cashJournalMove = new NewCashJournalManualMove();
            
            cashJournalMove.setCashJournalDate(new Date());
            cashJournalMove.setAmount(frAnnotation.getAmountAsBigDecimal());
            cashJournalMove.setMovConceptCode(frAnnotation.getConcept().getCashJournalConceptCode());
            cashJournalMove.setMovConceptDes(taConceptDes.getText());
            cashJournalMove.setSalesDocCode(frAnnotation.getDocument());
                        
            document = cashJournalSession.createManualMovement(cashJournalMove, printService.getDefaultPrintSettings(DocTypeServiceFacade.NATURE_CASH_JOURNAL_MOVEMENT_DOCUMENT));
        }
        catch (Exception e) {
            log.error("accionAceptar() - Error inesperado - " + e.getCause(), e);
            DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
            return;
        }
                        
        closeSuccess(document);                        
    }

    @FXML
    public void actionConceptHelp(ActionEvent event) {
        log.debug("actionConceptHelp()");
    }

    public void refreshScreenData() {
        tfAmount.setText("");
        tfConceptCode.setText("");
        taConceptDes.setText("");
        tfDocument.setText("");
        concept = null;
    }
    
    protected void establishFocus() {	
		if (tfAmount.getText().trim().equals("")){
			tfAmount.requestFocus();
		}
		else{
			if (tfConceptCode.getText().trim().equals("")){
				tfConceptCode.requestFocus();
			}
			else{
				if (taConceptDes.getText().trim().equals("")){
					taConceptDes.requestFocus();
				}
			}
		}
	}

    protected boolean actionValidateForm() {       
        boolean result = true;
		
        //Inicializamos la etiqueta de error
        frAnnotation.clearErrorStyle();
		lbError.setText("");
		
        // Validamos el formulario 
        Set<ConstraintViolation<CashMovementForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(frAnnotation);
        Iterator<ConstraintViolation<CashMovementForm>> iterator = constraintViolations.iterator();
        while (iterator.hasNext()){
            ConstraintViolation<CashMovementForm> next = iterator.next();
            frAnnotation.setErrorStyle(next.getPropertyPath(), true);

            lbError.setText(next.getMessage());

            result = false;
        }
        if(!result) {
        	establishFocus();
        }
        
        return result;
    }
    
    
    public void actionSearchConcepts(){
    	
    	openScene(CashMovementConceptController.class, new SceneCallback<CashJournalConcept>() {
			@Override
			public void onSuccess(CashJournalConcept newConcept) {
	    		taConceptDes.setText(newConcept.getCashJournalConceptDes());
	    		tfConceptCode.setText(newConcept.getCashJournalConceptCode());
	    		concept = newConcept;
	    		evalInOutValue(tfAmount.getText());
			}
		});
    	
    }
    
    public void tabChange(KeyEvent event) {
    	if(event.getCode().equals(KeyCode.TAB) && !event.isShiftDown() && !event.isControlDown()) {
    		event.consume();
            Node node = (Node) event.getSource();            
            KeyEvent newEvent 
              = new KeyEvent(event.getSource(),
                         event.getTarget(), event.getEventType(),
                         event.getCharacter(), event.getText(),
                         event.getCode(), event.isShiftDown(),
                         true, event.isAltDown(),
                         event.isMetaDown());

            node.fireEvent(newEvent);            
    	}
    }

}
