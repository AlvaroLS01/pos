package com.comerzzia.pos.gui.sales.cashjournal.close;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.core.commons.exception.NotFoundException;
import com.comerzzia.core.commons.sessions.ComerzziaTenantResolver;
import com.comerzzia.core.facade.service.doctype.DocTypeServiceFacade;
import com.comerzzia.core.facade.service.variable.VariableServiceFacade;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalDocumentIssued;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalLine;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalSummaryByPaymentCode;
import com.comerzzia.omnichannel.facade.model.cashjournal.CashJournalTotals;
import com.comerzzia.pos.core.gui.InitializeGuiException;
import com.comerzzia.pos.core.gui.components.actionbutton.normal.ActionButtonNormalComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupComponent;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupController;
import com.comerzzia.pos.core.gui.components.buttonsgroup.ButtonsGroupModel;
import com.comerzzia.pos.core.gui.components.datepicker.DatePicker;
import com.comerzzia.pos.core.gui.components.dialogs.DialogWindowBuilder;
import com.comerzzia.pos.core.gui.controllers.CzzScene;
import com.comerzzia.pos.core.gui.controllers.SceneController;
import com.comerzzia.pos.core.gui.exception.LoadWindowException;
import com.comerzzia.pos.core.gui.exception.ValidationException;
import com.comerzzia.pos.core.gui.permissions.exception.PermissionDeniedException;
import com.comerzzia.pos.core.gui.validation.ValidationUI;
import com.comerzzia.pos.core.services.session.ApplicationSession;
import com.comerzzia.pos.core.services.session.CashJournalSession;
import com.comerzzia.pos.devices.printer.PrintService;
import com.comerzzia.pos.gui.sales.cashjournal.counts.CashCountController;
import com.comerzzia.pos.util.format.FormatUtils;
import com.comerzzia.pos.util.i18n.I18N;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import lombok.extern.log4j.Log4j;

@Controller
@CzzScene
@Log4j
public class CloseCashJournalController extends SceneController implements Initializable, ButtonsGroupController {
	public static final String PERMISSION_ARQUEO_VISIBLE = "ARQUEO VISIBLE"; 
	public static final String PERMISSION_CERRAR_CON_DESCUADRE= "CERRAR CON DESCUADRE";

	@Autowired
	protected CashJournalSession cashJournalSession;
	
	protected Integer closingRetries;
	protected Boolean visibleCashJournal;
	
	@Autowired
	protected VariableServiceFacade variablesServices;
	
	// Lineas de cierre de pantalla de cierre de caja
	protected List<CloseCashJournalDto> closingLines;

	protected CashJournalTotals cashJournalTotals;
	
	// formulario de cierre de caja
	@Autowired
	protected CloseCashJournalForm cashJournalClosingForm;
	
	@Autowired
	protected PrintService printService;

	@Autowired
	protected ApplicationSession applicationSession;

	//Screen components
    @FXML
    protected Button btCashClose;

    @FXML
    protected Button btCancel;

    @FXML
    protected Label lbSalesValue, lbSales, lbRefundValue, lbMovesOutcome, lbMisalignment, lbIncomeTotal, lbOutcomeTotal,
    lbMovesIncome, lbIOTotal, lbTotalCount, lbCashierName, lbOpenDate, lbCashState, lbSalesCount, lbRefundCount, 
    lbMovesIncomeCount, lbMovesOutcomeCount;

    @FXML
    protected VBox vbBottomCountButton;
    
    @FXML
    protected HBox hbTotalIOCount, hbBreakDown, hbTotalMisalignment, hbDate;
    
    @FXML
    protected DatePicker dpCloseDate;
    
    @FXML
    protected WebView wvDataTable;
        
    protected List<CashJournalLine> sales;
	protected List<CashJournalLine> moves;
	
	@Autowired
	protected ComerzziaTenantResolver tenantResolver;
    
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		cashJournalClosingForm.setFormField("closingDate", dpCloseDate);
	}

	@Override
	public void onSceneOpen() throws InitializeGuiException {
		sales = new ArrayList<>();
		moves = new ArrayList<>();
		
		if (cashJournalSession.isOpenedCashJournal()) {
			sales = cashJournalSession.findSalesLines();
			moves.addAll(cashJournalSession.findManualLines());
			
			cashJournalTotals = cashJournalSession.virtualCashJournalCount();
			
		}
		dpCloseDate.setSelectedDate(new Date());
		if(!isMandatoryClosure()) {
			hbDate.setVisible(true);
			hbDate.setManaged(true);
		}else {
			hbDate.setVisible(false);
			hbDate.setManaged(false);
			dpCloseDate.setSelectedDate(cashJournalSession.getOpeningDate());
		}
		
		screenDataRefresh();
	}
	
	@Override
	public void initializeFocus() {
		screenDataRefresh();
		btCashClose.requestFocus();
	}
	
	@Override
	public void checkUIPermission() {
		super.checkUIPermission();
				
		// Comprobamos que el usuario pueda ver el arqueo
		visibleCashJournal = false;
				
		try {
			super.checkOperationPermissions(PERMISSION_ARQUEO_VISIBLE);
			
			visibleCashJournal = true;		
		}
		catch (PermissionDeniedException ex) {
			log.debug("checkUIPermission() - El usuario no puede ver el arqueo");
		}
		
		hbBreakDown.setVisible(visibleCashJournal);
		hbBreakDown.setManaged(visibleCashJournal);
		hbTotalIOCount.setVisible(visibleCashJournal);
		hbTotalIOCount.setManaged(visibleCashJournal);
		hbTotalMisalignment.setVisible(visibleCashJournal);
		hbTotalMisalignment.setManaged(visibleCashJournal);
		
		wvDataTable.setVisible(visibleCashJournal);
		wvDataTable.setManaged(visibleCashJournal);
	}

	/**
	 * Inicializa componetes personalizados de pantalla: botoneras, etc
	 * 
	 * @throws InitializeGuiException
	 */
	@Override
	public void initializeComponents() throws InitializeGuiException {
		// Comprobamos que existe el tipo de documento asociado
		try {
			applicationSession.getDocTypeForNatureDocumentType(DocTypeServiceFacade.NATURE_CASH_JOURNAL_CLOSE_DOCUMENT);
		}
		catch (NotFoundException e) {
			throw new InitializeGuiException(I18N.getText("No está configurado el tipo de documento cierre de caja en el entorno."));
		}
		
		try {
			loadBottomButton();
		} catch (LoadWindowException e) {
			e.printStackTrace();
		}
	}

	protected boolean isMandatoryClosure() {
		//Comprobamos si se puede añadir la fecha para el cierre
		String closeDate = variablesServices.getVariableAsString("CAJA.CIERRE_CAJA_DIARIO_OBLIGATORIO");
		return closeDate.equals("S");
	}

	protected void openCashJournalCount() {
		log.debug("openCashJournalCount() - Abrir pantalla de recuento de caja.");
		if(stageController.isAnotherActionOpened()) {
			if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"))) {
				return;
			}
			boolean closeAllScenesExcept = stageController.closeAllActionsExcept(stageController.getCurrentAction());
			if(!closeAllScenesExcept) {
				return;
			}
		}
		clearCounts();
		
		openScene(CashCountController.class, new SceneCallback<Void>() {
			@Override
			public void onSuccess(Void callbackData) {
				cashJournalTotals = cashJournalSession.virtualCashJournalCount();
				screenDataRefresh();
			}
			
			@Override
			public void onCancel() {
				cashJournalTotals = cashJournalSession.virtualCashJournalCount();
				screenDataRefresh();
			}
		});
	}

	protected void clearCounts() {
	    if (cashJournalSession.hasCashJournalCount()) {
	    	if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Ya existe un recuento de la caja. ¿Desea borrarlo y empezar de nuevo?", I18N.getText("Borrar recuento"), I18N.getText("Conservar recuento")))) {
	    		cashJournalSession.cleanCashJournalCount();
	    	}
	    }
    }

	protected void actionCashJournalClosing() {
		log.debug("actionCashJournalClosing()");
		
		if(stageController.isAnotherActionOpened()) {
			if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Para cerrar la caja se deben cerrar todas las pantallas abiertas. ¿Desea continuar?"))) {
				return;
			}
			boolean closeAllScenesExcept = stageController.closeAllActionsExcept(stageController.getCurrentAction());
			if(!closeAllScenesExcept) {
				return;
			}
		}
		
		Boolean hasMismatch = cashJournalSession.hasMismatch();
		Integer maxRetries = variablesServices.getVariableAsInteger(VariableServiceFacade.CAJA_REINTENTOS_CIERRE);
		
		if(closingRetries == null){
			closingRetries = 0;
		}
		if (hasMismatch) {
			boolean canCloseWithMismatch = false;
			try {
				checkOperationPermissions(PERMISSION_CERRAR_CON_DESCUADRE);
				canCloseWithMismatch = true;
			} catch (PermissionDeniedException e) {}
			
			Integer remainingRetries = maxRetries - closingRetries;
			if (remainingRetries > 0 || !canCloseWithMismatch) {
				DialogWindowBuilder.getBuilder(getStage()).simpleWarningDialog(I18N.getText("Caja descuadrada con un importe mayor que el permitido. Revise recuento."));
				closingRetries++;
				return;
			}
			else if (!DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("Se va a cerrar la caja con descuadres mayores al valor permitido, ¿Desea continuar?"))) {
				return;
			}
		}
		if (DialogWindowBuilder.getBuilder(getStage()).simpleConfirmDialog(I18N.getText("¿Seguro de realizar el Cierre?"))) {
			try {
				// Procedemos al cierre de la caja
				// Actualizamos el formulario
				cashJournalClosingForm.setClosingDateStr(dpCloseDate.getTexto());
				// validamos el formulario
				actionValidateForm();
				// cierre			
				HashMap<String, Object> customData = new HashMap<>();
				customData.put("cashImbalance", hasMismatch);
				customData.put("retries", closingRetries);
				customData.put("maxRetries", maxRetries);
				
				CashJournalDocumentIssued<?>documentIssued = cashJournalSession.closeCashJournal(cashJournalClosingForm.getClosingDate(), 
																							     customData,
																								 printService.getDefaultPrintSettings(DocTypeServiceFacade.NATURE_CASH_JOURNAL_CLOSE_DOCUMENT));
	
				customData.put("document", documentIssued);
				
				cashJournalSession.resetCashJournal();
				closingRetries = 0;
				
				closeSuccess(customData);
			}
			catch (ValidationException ex) {
				log.debug("actionCashJournalClosing() - La validación no fué exitosa");
				// La validación ya se encarga de mostrar el error
			}			
		}
	}
	
	/**
	 * Validación del formulario
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	protected void actionValidateForm() throws ValidationException {
		// Limpiamos los errores que pudiese tener el formulario
		boolean hasValidationErrors = false;
		cashJournalClosingForm.clearErrorStyle();
		cashJournalClosingForm.setClosingDate(null);

		// Validamos el formulario de login
		Set<ConstraintViolation<CloseCashJournalForm>> constraintViolations = ValidationUI.getInstance().getValidator().validate(cashJournalClosingForm);
		if (constraintViolations.size() >= 1) {
			ConstraintViolation<CloseCashJournalForm> next = constraintViolations.iterator().next();
			cashJournalClosingForm.setErrorStyle(next.getPropertyPath(), true);
			cashJournalClosingForm.setFocus(next.getPropertyPath());
			// Mostramos el error en una ventana de error
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(next.getMessage());
			throw new ValidationException();
		}

		Date openingDate = cashJournalSession.getOpeningDate();
		Calendar calendarOpening = Calendar.getInstance();
		calendarOpening.setTime(openingDate);

		Date closingDate = FormatUtils.getInstance().parseDateTime(dpCloseDate.getTexto(), true);


		// If closing date is not the current day, we set the time to the end of the day
		if (!DateUtils.isSameDay(closingDate, new Date())) {
			Calendar calendarClosingWithoutHour = Calendar.getInstance();
			calendarClosingWithoutHour.setTime(closingDate);
			calendarClosingWithoutHour.set(Calendar.HOUR_OF_DAY, 23);
			calendarClosingWithoutHour.set(Calendar.MINUTE, 59);
			calendarClosingWithoutHour.set(Calendar.SECOND, 59);
			calendarClosingWithoutHour.set(Calendar.MILLISECOND, 999);
			closingDate = calendarClosingWithoutHour.getTime();
		}

		if (closingDate.before(openingDate)) {
			DialogWindowBuilder.getBuilder(getStage()).simpleErrorDialog(I18N.getText("La fecha de cierre no puede ser anterior a la fecha de apertura."));
			throw new ValidationException();
		}
		
		cashJournalClosingForm.setClosingDate(closingDate);			
	}

	/*
	 * CMZ POS V5
	 */

	/**
     * Acción aceptar
     */
    @FXML
    public void actionAccept() {
    	actionCashJournalClosing();
    }
    
    
	private void loadWebViewPrincipal() {
		Map<String, Object> params = new HashMap<>();
				
		params.put("closeLines", closingLines);
		
		loadWebView("cash_management/close/close", params, wvDataTable);
	}
    
    /**
     * Función que refresca los totales en pantalla
     */
	public void screenDataRefresh() {
		log.debug("screenDataRefresh()");

    	closingLines = new ArrayList<>();
    	
		// Líneas de la tabla de descuadres
		for (CashJournalSummaryByPaymentCode cashJournaSummaryLine : cashJournalTotals.getSummaryByPaymentCode().values()) {
			closingLines.add(new CloseCashJournalDto(cashJournaSummaryLine));
		}

		// Cuadros de texto
		// -Totales
		lbIOTotal.setText(FormatUtils.getInstance().formatAmount(cashJournalTotals.getTotal()));
		lbTotalCount.setText(FormatUtils.getInstance().formatAmount(cashJournalTotals.getTotalCount()));
		lbMisalignment.setText(FormatUtils.getInstance().formatAmount(cashJournalTotals.getTotalCount().subtract(cashJournalTotals.getTotal())));

		// Cuadro inferior
		String cashierName = tenantResolver.getUser().getUserDes();
		lbCashierName.setText(getCashiersNameInitials(cashierName) + " · " + cashierName);
		lbOpenDate.setText(FormatUtils.getInstance().formatDate(cashJournalSession.getOpeningDate()));
		lbCashState.setText(cashJournalSession.isOpenedCashJournal() ? I18N.getText("Abierta") : I18N.getText("Cerrada"));

		// Entradas/salidas
		FormatUtils formatter = FormatUtils.getInstance();
		lbSalesValue.setText(formatter.formatAmount(cashJournalTotals.getSalesInput()));
		lbMovesIncome.setText(formatter.formatAmount(cashJournalTotals.getMovementsInput()));
		lbIncomeTotal.setText(formatter.formatAmount(cashJournalTotals.getSalesInput().add(cashJournalTotals.getMovementsInput())));
		lbRefundValue.setText(formatter.formatAmount(cashJournalTotals.getSalesOutput()));
		lbMovesOutcome.setText(formatter.formatAmount(cashJournalTotals.getMovementsOutput()));
		lbOutcomeTotal.setText(formatter.formatAmount(cashJournalTotals.getSalesOutput().add(cashJournalTotals.getMovementsOutput())));
		
		//Total counts
		
		lbSalesCount.setText(cashJournalTotals.getSaleDocumentsCount().toString());
		lbMovesOutcomeCount.setText(String.valueOf(getMovesAndSalesAmount("moves").get("oMCount")));
		lbMovesIncomeCount.setText(String.valueOf(getMovesAndSalesAmount("moves").get("iMCount")));
		lbRefundCount.setText(cashJournalTotals.getRefundDocumentsCount().toString());

		labelWidthAdjustment(lbSalesCount);
		labelWidthAdjustment(lbMovesOutcomeCount);
		labelWidthAdjustment(lbMovesIncomeCount);
		labelWidthAdjustment(lbRefundCount);

        log.debug("screenDataRefresh() - Loading web view for the screen");
        loadWebViewPrincipal();
    }
    
    public void actionEscapeKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
        	closeCancel();
        }
    }
    
    /*
	 * Sets the top buttons panel to change payment type
	 * */
	protected void loadBottomButton() throws LoadWindowException {
		try {
			log.debug("loadBottomButton() - loading top buttons pannel");
			ButtonsGroupModel buttonsGroupModel = getSceneView().loadButtonsGroup("_bottom_button.xml");
			ButtonsGroupComponent buttonsGroup = new ButtonsGroupComponent(buttonsGroupModel, vbBottomCountButton.getPrefWidth(), vbBottomCountButton.getPrefHeight(), this, ActionButtonNormalComponent.class);
			vbBottomCountButton.getChildren().clear();
			vbBottomCountButton.getChildren().add(buttonsGroup);
		}
		catch (InitializeGuiException e) {
			log.error("loadBottomButton() - Error al crear botonera: " + e.getMessage(), e);
		}
	}
	
	private String getCashiersNameInitials(String fullName) {
		String res = "";
		List<String> upperCases = new ArrayList<String>();
		
		for(Character c : fullName.toCharArray()) {
			if(Character.isUpperCase(c)) {
				upperCases.add(c.toString());
			}
		}
		
		for(String s : upperCases) {
			res = res + s;
		}
		
		return res;
	}
	
	public void labelWidthAdjustment(Label label) {
		Integer length = label.getText().length();
		if(length == 1 || length == 2) {
			label.setPrefWidth(lbSalesCount.getPrefWidth());
		}else if(length == 3) {
			label.setPrefWidth(lbSalesCount.getWidth() + 20);
			label.setTranslateX(5);
		}else if (length == 4) {
			label.setPrefWidth(lbSalesCount.getWidth() + 25);
			label.setTranslateX(5);
		}else if (length == 5) {
			label.setPrefWidth(lbSalesCount.getWidth() + 45);
			label.setTranslateX(5);
		}
	}
	
	public Map<String, Integer> getMovesAndSalesAmount(String filter) {
		Map<String, Integer> res = new HashMap<String, Integer>();		
		Integer iMCount = 0;
		Integer oMCount = 0;
		Integer sCount = 0;
		Integer rCount = 0;
		BigDecimal zero = BigDecimal.ZERO;
		if(filter.equals("moves")) {
			for (CashJournalLine m : moves) {
				if(zero.compareTo(m.getOutput())!=0) {
					oMCount++;
					
				}
				else {
					iMCount++;
				}
			}
			res.put("iMCount", iMCount);
			res.put("oMCount", oMCount);
		}else if (filter.equals("sales")) {
			for (CashJournalLine s : sales) {
				if(zero.compareTo(s.getInput())!=0) {
					sCount++;
				}
				else {
					rCount++;
				}
			}
			res.put("sCount", sCount);
			res.put("rCount", rCount);
		}
		return res;
	}

}


