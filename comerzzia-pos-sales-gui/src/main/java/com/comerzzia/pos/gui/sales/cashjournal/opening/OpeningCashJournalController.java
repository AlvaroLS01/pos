
package com.comerzzia.pos.gui.sales.cashjournal.opening;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalCount;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalDocumentIssued;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowComponent;
import com.comerzzia.pos.core.gui.components.textField.NumericTextField;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.core.services.session.Session;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.cashjournal.opening.cashcount.OpeningCashCountController;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.extern.log4j.Log4j;


@Controller
@CzzScene
@Log4j
public class OpeningCashJournalController extends SceneController implements Initializable {
    protected OpeningCashJournalForm openingCashJournalForm;
    protected List<CashJournalCount> countLines; 

    @FXML
    protected NumericTextField tfBalance;
    
    @FXML
    protected DatePicker tfDate;
    
    @FXML
    protected Button btCountBalance;
    
    @Autowired
    protected Session session;
    
    @Autowired
    protected CashJournalSession cashJournalSession; 
    
	@Autowired
	protected PrintService printService;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @Override
    public void initializeComponents() {
    }

    @Override
    public void onSceneOpen() throws InitializeGuiException {
    	countLines = new ArrayList<>();

        Date d = new Date();
        tfDate.setSelectedDate(d); //Inicializamos la apertura a fecha de hoy
        tfDate.setDisable(true);
        tfBalance.setText("0");
    }

    @Override
    public void initializeFocus() {
    	tfBalance.selectAll();
    	tfBalance.requestFocus();
    }

    @FXML
    protected void actionSelectDate(ActionEvent event) {
        log.debug("actionSelectDate()");
    }

    @FXML
    public void actionAccept() {
        try {
            log.debug("actionAccept()");
            //Validar formulario                  
            openingCashJournalForm = new OpeningCashJournalForm(tfDate.getTexto(), tfBalance.getText());
            if (!actionValidateForm()) {
                return;
            }
            
            Date openingDate = FormatUtils.getInstance().parseDateTime(tfDate.getTexto(), true);
            
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.set(Calendar.HOUR_OF_DAY, 0);
            calendarToday.set(Calendar.MINUTE, 0);
            calendarToday.set(Calendar.SECOND, 0);
            calendarToday.set(Calendar.MILLISECOND, 0);
            
            //Si la fecha de apertura no es hoy, la ponemos sin hora
            Calendar calendarOpeningWithoutHour = Calendar.getInstance();
            calendarOpeningWithoutHour.setTime(openingDate);
            calendarOpeningWithoutHour.set(Calendar.HOUR_OF_DAY, 0);
            calendarOpeningWithoutHour.set(Calendar.MINUTE, 0);
            calendarOpeningWithoutHour.set(Calendar.SECOND, 0);
            calendarOpeningWithoutHour.set(Calendar.MILLISECOND, 0);
            if(!calendarOpeningWithoutHour.equals(calendarToday)){
            	openingDate = calendarOpeningWithoutHour.getTime();
            }
            
            if (countLines == null || (countLines != null && countLines.size() == 0)) {
            	countLines = new ArrayList<>();
            	CashJournalCount directAmount = new CashJournalCount();
            	directAmount.setLineId(1);
            	directAmount.setPaymentMethodCode(session.getApplicationSession().getStorePosBusinessData().getDefaultPymtMethod().getPaymentMethodCode());
            	directAmount.setQuantity(1);
            	directAmount.setCountValue(openingCashJournalForm.getBalanceAsBigDecimal());
				countLines.add(directAmount);
            } else {
            	BigDecimal balance = countLines.stream().map(
        				cl -> cl.getCountValue().multiply(new BigDecimal(cl.getQuantity()))
        				).collect(Collectors.reducing(BigDecimal.ZERO, BigDecimal::add));
            	
            	// controlar que no se haya alterado el importe
            	if (balance.compareTo(openingCashJournalForm.getBalanceAsBigDecimal()) != 0) {
            		DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("El importe no coincide con el total del recuento"));
            		return;
            	}
            }
                        
            
            // open cash journal
            CashJournalDocumentIssued<?> documentIssued = cashJournalSession.openManualCashJournal(openingDate, 
            		                                                         countLines, 
            		                                                         null,
            		                                                         printService.getDefaultPrintSettings(DocTypeServiceFacade.NATURE_CASH_JOURNAL_OPEN_DOCUMENT));
                        
            closeSuccess(documentIssued);
        }
        catch (Exception e) {
            log.error("actionAccept() - Error al tratar de realizar apertura de caja: " + e.getMessage(), e);
            DialogWindowBuilder.getBuilder(getStage()).simpleThrowableDialog(e);
        }
    }

    /**
     * Validamos el formulario
     *
     * @return
     */
    protected boolean actionValidateForm() {
        // Limpiamos los errores que pudiese tener el formulario
        openingCashJournalForm.clearErrorStyle();

        // Validamos el formulario de login
        Set<ConstraintViolation<OpeningCashJournalForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(openingCashJournalForm);
        if (constraintViolations.size() >= 1) {
            ConstraintViolation<OpeningCashJournalForm> next = constraintViolations.iterator().next();
            openingCashJournalForm.setErrorStyle(next.getPropertyPath(), true);
            openingCashJournalForm.setFocus(next.getPropertyPath());
            // Mostramos el error en una ventana de error
            DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(next.getMessage());
            return false;
        }
        return true;
    }

    @FXML
    public void actionIntro(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            actionAccept();
        }
    }
    
    @FXML
    public void actionCountBalance(){
    	openScene(OpeningCashCountController.class, new SceneCallback<List<CashJournalCount>>() {
			@Override
			public void onSuccess(List<CashJournalCount> countLines) {			
				BigDecimal balance = countLines.stream().map(
						cl -> cl.getCountValue().multiply(new BigDecimal(cl.getQuantity()))
						).collect(Collectors.reducing(BigDecimal.ZERO, BigDecimal::add));
				tfBalance.setText(FormatUtils.getInstance().formatAmount(balance));
				tfBalance.setDisable(true);
			}
			
			@Override
			public void onCancel() {
				countLines = null;
				tfBalance.setText(FormatUtils.getInstance().formatAmount(BigDecimal.ZERO));
				tfBalance.setDisable(false);
			}
		});
    }

}
